package com.cloudmeal.product.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record DishSaveRequest(
        @NotNull Long categoryId,
        @NotBlank @Size(max = 100) String name,
        @NotNull @DecimalMin("0.01") BigDecimal price,
        @Size(max = 500) String image,
        @Size(max = 500) String description,
        @NotNull @Min(0) Integer stock,
        @NotNull @Min(0) @Max(1) Integer status) {}
