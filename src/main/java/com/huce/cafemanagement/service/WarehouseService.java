package com.huce.cafemanagement.service;

import com.huce.cafemanagement.exception.ResourceNotFoundException;
import com.huce.cafemanagement.model.dto.WarehouseDtos.WarehouseRequest;
import com.huce.cafemanagement.model.dto.WarehouseDtos.WarehouseResponse;
import com.huce.cafemanagement.model.entities.Warehouse;
import com.huce.cafemanagement.repository.WarehouseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WarehouseService {
    private final WarehouseRepository warehouseRepository;

    public WarehouseService(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    @Transactional(readOnly = true)
    public List<WarehouseResponse> getWarehouses() {
        return warehouseRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public WarehouseResponse getWarehouseById(Long id) {
        return toResponse(findWarehouse(id));
    }

    @Transactional
    public WarehouseResponse createWarehouse(WarehouseRequest request) {
        Warehouse warehouse = new Warehouse();
        apply(warehouse, request);
        return toResponse(warehouseRepository.save(warehouse));
    }

    @Transactional
    public WarehouseResponse updateWarehouse(Long id, WarehouseRequest request) {
        Warehouse warehouse = findWarehouse(id);
        apply(warehouse, request);
        return toResponse(warehouseRepository.save(warehouse));
    }

    @Transactional
    public void deleteWarehouse(Long id) {
        warehouseRepository.delete(findWarehouse(id));
    }

    private void apply(Warehouse warehouse, WarehouseRequest request) {
        warehouse.setName(request.name().trim());
        warehouse.setAddress(request.address() == null ? null : request.address().trim());
        warehouse.setPhone(request.phone() == null ? null : request.phone().trim());
        warehouse.setStatus(request.status().trim());
    }

    private Warehouse findWarehouse(Long id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kho với id " + id + "."));
    }

    private WarehouseResponse toResponse(Warehouse warehouse) {
        return new WarehouseResponse(warehouse.getId(), warehouse.getName(), warehouse.getAddress(),
                warehouse.getPhone(), warehouse.getStatus(), warehouse.getCreatedAt(), warehouse.getUpdatedAt());
    }
}