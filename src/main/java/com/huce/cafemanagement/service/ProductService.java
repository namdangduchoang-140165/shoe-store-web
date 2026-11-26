package com.huce.cafemanagement.service;

import com.huce.cafemanagement.constant.StaticVariables;
import com.huce.cafemanagement.exception.DuplicateResourceException;
import com.huce.cafemanagement.exception.ResourceNotFoundException;
import com.huce.cafemanagement.model.dto.ProductDtos.ProductRequest;
import com.huce.cafemanagement.model.dto.ProductDtos.ProductResponse;
import com.huce.cafemanagement.model.entities.Brand;
import com.huce.cafemanagement.model.entities.Category;
import com.huce.cafemanagement.model.entities.Product;
import com.huce.cafemanagement.repository.BrandRepository;
import com.huce.cafemanagement.repository.CategoryRepository;
import com.huce.cafemanagement.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository,
                          BrandRepository brandRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProducts() {
        return productRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        return toResponse(findProduct(id));
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        ensureSlugAvailable(request.slug().trim(), null);
        Product product = new Product();
        apply(product, request);
        return toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = findProduct(id);
        ensureSlugAvailable(request.slug().trim(), id);
        apply(product, request);
        return toResponse(productRepository.save(product));
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = findProduct(id);
        product.setStatus(StaticVariables.PRODUCT_STATUS_INACTIVE);
        productRepository.save(product);
    }

    private void apply(Product product, ProductRequest request) {
        product.setCategory(findCategory(request.categoryId()));
        product.setBrand(request.brandId() == null ? null : findBrand(request.brandId()));
        product.setName(request.name().trim());
        product.setSlug(request.slug().trim());
        product.setDescription(trimToNull(request.description()));
        product.setMaterial(trimToNull(request.material()));
        product.setGender(trimToNull(request.gender()));
        product.setStatus(request.status().trim().toLowerCase(Locale.ROOT));
    }

    private void ensureSlugAvailable(String slug, Long currentProductId) {
        boolean exists = currentProductId == null
                ? productRepository.existsBySlug(slug)
                : productRepository.existsBySlugAndIdNot(slug, currentProductId);
        if (exists) {
            throw new DuplicateResourceException("Slug sản phẩm đã tồn tại: " + slug + ".");
        }
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với id " + id + "."));
    }

    private Category findCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với id " + id + "."));
    }

    private Brand findBrand(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thương hiệu với id " + id + "."));
    }

    private ProductResponse toResponse(Product product) {
        Category category = product.getCategory();
        Brand brand = product.getBrand();
        return new ProductResponse(product.getId(), category.getId(), category.getName(),
                brand == null ? null : brand.getId(), brand == null ? null : brand.getName(),
                product.getName(), product.getSlug(), product.getDescription(), product.getMaterial(),
                product.getGender(), product.getStatus(), product.getCreatedAt(), product.getUpdatedAt());
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}