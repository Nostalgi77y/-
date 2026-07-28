package com.cloudmeal.cart.dto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
public record CartAddRequest(@NotNull Long dishId, @NotNull @Min(1) Integer quantity) {}
