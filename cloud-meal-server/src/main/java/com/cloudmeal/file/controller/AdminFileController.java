package com.cloudmeal.file.controller;

import com.cloudmeal.common.api.ApiResponse;
import com.cloudmeal.file.service.ImageStorageService;
import com.cloudmeal.file.vo.FileUploadVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/files")
public class AdminFileController {
    private final ImageStorageService service;

    public AdminFileController(ImageStorageService service) { this.service = service; }

    @PostMapping("/images")
    public ApiResponse<FileUploadVO> uploadImage(@RequestParam("file") MultipartFile file) {
        return ApiResponse.success(new FileUploadVO(service.store(file)));
    }
}
