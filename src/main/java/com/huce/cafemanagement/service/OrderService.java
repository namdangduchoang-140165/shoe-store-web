package com.huce.cafemanagement.service;

import com.huce.cafemanagement.constant.StaticVariables;
import com.huce.cafemanagement.exception.ResourceNotFoundException;
import com.huce.cafemanagement.model.dto.OrderDtos.CreateOrderRequest;
import com.huce.cafemanagement.model.dto.OrderDtos.OrderItemRequest;
import com.huce.cafemanagement.model.dto.OrderDtos.OrderItemResponse;
import com.huce.cafemanagement.model.dto.OrderDtos.OrderResponse;
import com.huce.cafemanagement.model.dto.OrderDtos.UpdateOrderStatusRequest;
import com.huce.cafemanagement.model.entities.OrderItem;
import com.huce.cafemanagement.model.entities.OrderStatusHistory;
import com.huce.cafemanagement.model.entities.Orders;
import com.huce.cafemanagement.model.entities.Users;
import com.huce.cafemanagement.repository.OrderItemRepository;
import com.huce.cafemanagement.repository.OrderStatusHistoryRepository;
import com.huce.cafemanagement.repository.OrdersRepository;
import com.huce.cafemanagement.repository.UsersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class OrderService {
    private final OrdersRepository ordersRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final UsersRepository usersRepository;

    public OrderService(OrdersRepository ordersRepository, OrderItemRepository orderItemRepository,
                        OrderStatusHistoryRepository orderStatusHistoryRepository,
                        UsersRepository usersRepository) {
        this.ordersRepository = ordersRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
        this.usersRepository = usersRepository;
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrders() {
        return ordersRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        return toResponse(findOrder(id));
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Users user = findUser(request.userId());
        if (!"active".equals(user.getStatus())) {
            throw new IllegalArgumentException("Người dùng không hoạt động nên không thể tạo đơn hàng.");
        }
        List<OrderItem> items = request.items().stream().map(this::toEntity).toList();
        BigDecimal subtotal = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal discount = amountOrZero(request.discountAmount());
        BigDecimal shippingFee = amountOrZero(request.shippingFee());
        if (discount.compareTo(subtotal) > 0) {
            throw new IllegalArgumentException("Giảm giá không được lớn hơn giá trị đơn hàng.");
        }

        Orders order = new Orders();
        order.setOrderCode(generateOrderCode());
        order.setUser(user);
        order.setStatus("pending");
        order.setPaymentStatus("unpaid");
        order.setPaymentMethod(request.paymentMethod().trim().toLowerCase(Locale.ROOT));
        order.setRecipientName(request.recipientName().trim());
        order.setRecipientPhone(request.recipientPhone().trim());
        order.setShippingAddress(request.shippingAddress().trim());
        order.setShippingNote(request.shippingNote());
        order.setSubtotal(subtotal);
        order.setDiscountAmount(discount);
        order.setShippingFee(shippingFee);
        order.setTotalAmount(subtotal.subtract(discount).add(shippingFee));
        order.setOrderedAt(LocalDateTime.now());
        order = ordersRepository.save(order);

        for (OrderItem item : items) {
            item.setOrder(order);
        }
        orderItemRepository.saveAll(items);
        saveStatusHistory(order, null, "pending", null, "Đơn hàng được tạo.");
        return toResponse(order);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long id, UpdateOrderStatusRequest request) {
        Orders order = findOrder(id);
        String oldStatus = order.getStatus();
        String newStatus = request.status().trim().toLowerCase(Locale.ROOT);
        if (!StaticVariables.ALLOWED_STATUS_TRANSITIONS.getOrDefault(oldStatus, Set.of()).contains(newStatus)) {
            throw new IllegalArgumentException("Không thể chuyển trạng thái đơn hàng từ " + oldStatus + " sang " + newStatus + ".");
        }
        if ("cancelled".equals(newStatus) && "paid".equals(order.getPaymentStatus())) {
            throw new IllegalArgumentException("Cần hoàn tiền trước khi hủy đơn hàng đã thanh toán.");
        }

        Users staff = request.changedById() == null ? null : findUser(request.changedById());
        order.setStaff(staff);
        order.setStatus(newStatus);
        if ("completed".equals(newStatus)) {
            order.setCompletedAt(LocalDateTime.now());
        } else if ("cancelled".equals(newStatus)) {
            order.setCancelledAt(LocalDateTime.now());
        }
        order = ordersRepository.save(order);
        saveStatusHistory(order, oldStatus, newStatus, staff, request.note());
        return toResponse(order);
    }

    private OrderItem toEntity(OrderItemRequest request) {
        OrderItem item = new OrderItem();
        item.setProductName(request.productName().trim());
        item.setSku(request.sku().trim());
        item.setSizeValue(request.sizeValue().trim());
        item.setColorName(request.colorName().trim());
        item.setUnitPrice(request.unitPrice());
        item.setQuantity(request.quantity());
        item.setSubtotal(request.unitPrice().multiply(BigDecimal.valueOf(request.quantity())));
        return item;
    }

    private void saveStatusHistory(Orders order, String oldStatus, String newStatus, Users staff, String note) {
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setChangedBy(staff);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setNote(note);
        orderStatusHistoryRepository.save(history);
    }

    private OrderResponse toResponse(Orders order) {
        List<OrderItemResponse> items = orderItemRepository.findAllByOrder_Id(order.getId()).stream()
                .map(item -> new OrderItemResponse(item.getId(), item.getProductName(), item.getSku(),
                        item.getSizeValue(), item.getColorName(), item.getUnitPrice(), item.getQuantity(), item.getSubtotal()))
                .toList();
        return new OrderResponse(order.getId(), order.getOrderCode(), order.getUser().getId(),
                order.getStaff() == null ? null : order.getStaff().getId(), order.getStatus(),
                order.getPaymentStatus(), order.getPaymentMethod(), order.getRecipientName(), order.getRecipientPhone(),
                order.getShippingAddress(), order.getShippingNote(), order.getSubtotal(), order.getDiscountAmount(),
                order.getShippingFee(), order.getTotalAmount(), order.getOrderedAt(), order.getCompletedAt(),
                order.getCancelledAt(), items);
    }

    private Orders findOrder(Long id) {
        return ordersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với id " + id + "."));
    }

    private Users findUser(Long id) {
        return usersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với id " + id + "."));
    }

    private BigDecimal amountOrZero(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    private String generateOrderCode() {
        return "ORD-" + UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase(Locale.ROOT);
    }
}