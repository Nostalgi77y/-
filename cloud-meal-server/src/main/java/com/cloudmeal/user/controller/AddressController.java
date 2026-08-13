package com.cloudmeal.user.controller;

import com.cloudmeal.common.api.ApiResponse;
import com.cloudmeal.user.dto.AddressSaveRequest;
import com.cloudmeal.user.entity.AddressBook;
import com.cloudmeal.user.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/user/addresses")
public class AddressController {
    private final AddressService service;
    public AddressController(AddressService service) { this.service = service; }
    @GetMapping public ApiResponse<List<AddressBook>> list() {
        return ApiResponse.success(service.list());
    }
    @PostMapping public ApiResponse<AddressBook> create(@Valid @RequestBody AddressSaveRequest request) { return ApiResponse.success(service.create(request)); }
    @PutMapping("/{id}") public ApiResponse<AddressBook> update(@PathVariable Long id, @Valid @RequestBody AddressSaveRequest request) { return ApiResponse.success(service.update(id, request)); }
    @DeleteMapping("/{id}") public ApiResponse<Void> remove(@PathVariable Long id) { service.remove(id); return ApiResponse.success(); }
    @PutMapping("/{id}/default") public ApiResponse<Void> setDefault(@PathVariable Long id) { service.setDefault(id); return ApiResponse.success(); }
}
