package com.lab.techstore.mapper;

import com.lab.techstore.DTO.CategoryDTO;
import com.lab.techstore.DTO.ProductResponseDTO;
import com.lab.techstore.entity.Product;

public class ProductMapper {

    public static ProductResponseDTO toDTO(Product product) {
        if (product == null) {
            return null;
        }

        // Mapeamos la categoría anidada controlando nulos
        CategoryDTO categoryDTO = null;
        if (product.getCategory() != null) {
            categoryDTO = new CategoryDTO(
                    product.getCategory().getId(),
                    product.getCategory().getName()
            );
        }

        // Retornamos el Record DTO con los 5 atributos + la categoría
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getQuantity(),
                product.getDescription(),
                categoryDTO
        );
    }
}