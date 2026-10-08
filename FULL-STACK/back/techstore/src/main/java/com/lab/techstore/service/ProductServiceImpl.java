package com.lab.techstore.service;

import jakarta.persistence.EntityNotFoundException;
import com.lab.techstore.DTO.ProductRequestDTO;
import com.lab.techstore.DTO.ProductResponseDTO;
import com.lab.techstore.entity.Category;
import com.lab.techstore.entity.Product;
import com.lab.techstore.mapper.ProductMapper;
import com.lab.techstore.repository.CategoryRepository;
import com.lab.techstore.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO requestDTO) {
        Category category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada con ID: " + requestDTO.getCategoryId()));

        Product product = new Product();
        product.setName(requestDTO.getName());
        product.setPrice(requestDTO.getPrice());
        product.setQuantity(requestDTO.getQuantity());
        product.setDescription(requestDTO.getDescription());
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);
        return ProductMapper.toDTO(savedProduct); // Usamos el Mapper
    }

    @Override
    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductMapper::toDTO) // Usamos referencia a método del Mapper
                .toList();
    }

    @Override
    public ProductResponseDTO getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + productId));
        return ProductMapper.toDTO(product); // Usamos el Mapper
    }

    @Override
    public ProductResponseDTO updateProduct(Long productId, ProductRequestDTO requestDTO) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + productId));

        Category category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada con ID: " + requestDTO.getCategoryId()));

        existingProduct.setName(requestDTO.getName());
        existingProduct.setPrice(requestDTO.getPrice());
        existingProduct.setQuantity(requestDTO.getQuantity());
        existingProduct.setDescription(requestDTO.getDescription());
        existingProduct.setCategory(category);

        Product updatedProduct = productRepository.save(existingProduct);
        return ProductMapper.toDTO(updatedProduct); // Usamos el Mapper
    }

    @Override
    public void deleteProductById(Long productId) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + productId));
        productRepository.delete(existingProduct);
    }
}