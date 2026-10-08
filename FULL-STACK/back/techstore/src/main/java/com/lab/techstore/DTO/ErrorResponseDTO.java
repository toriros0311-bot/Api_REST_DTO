package com.lab.techstore.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ErrorResponseDTO {
    private int status;
    private String message;
    private LocalDateTime timestamp;

}
