package com.cloudmeal.file.service;

import com.cloudmeal.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

@Service
public class ImageStorageService {
    private static final Map<String, String> EXTENSIONS = Map.of(
            "image/jpeg", ".jpg", "image/png", ".png", "image/webp", ".webp", "image/gif", ".gif");

    @Value("${cloud-meal.upload-dir:./uploads}")
    private String uploadDir;

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new BusinessException("IMAGE_EMPTY", "请选择要上传的图片");
        String extension = EXTENSIONS.get(file.getContentType());
        if (extension == null) throw new BusinessException("IMAGE_TYPE_INVALID", "仅支持 JPG、PNG、WebP 或 GIF 图片");
        if (file.getSize() > 5 * 1024 * 1024) throw new BusinessException("IMAGE_TOO_LARGE", "图片大小不能超过 5MB");
        try {
            Path directory = Path.of(uploadDir, "images").toAbsolutePath().normalize();
            Files.createDirectories(directory);
            String filename = UUID.randomUUID().toString().replace("-", "") + extension;
            Path target = directory.resolve(filename).normalize();
            if (!target.startsWith(directory)) throw new BusinessException("IMAGE_PATH_INVALID", "图片保存路径非法");
            file.transferTo(target);
            return "/api/files/images/" + filename;
        } catch (IOException exception) {
            throw new BusinessException("IMAGE_SAVE_FAILED", "图片保存失败，请稍后重试");
        }
    }
}
