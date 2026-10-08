package com.lab.techstore.service;

import com.lab.techstore.DTO.ProductRequestDTO;
import com.lab.techstore.DTO.ProductResponseDTO;
import java.util.List;

public interface ProductService {
    ProductResponseDTO createProduct(ProductRequestDTO requestDTO);
    List<ProductResponseDTO> getAllProducts();
    ProductResponseDTO getProductById(Long productId);
    ProductResponseDTO updateProduct(Long productId, ProductRequestDTO requestDTO   );
    void deleteProductById(Long productId);
}
