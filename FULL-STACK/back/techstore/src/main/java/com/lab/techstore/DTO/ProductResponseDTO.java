package com.lab.techstore.DTO;


import java.math.BigDecimal;

// Record DTO para el Producto con la Categoría anidada
public record ProductResponseDTO(
        Long id,
        String name,
        BigDecimal price,
        int quantity,
        String description,
        CategoryDTO category
) {}