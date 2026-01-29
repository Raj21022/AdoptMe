package com.adoptme.backend.controller;

import com.adoptme.backend.dto.ApiResponse;
import com.adoptme.backend.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class UploadController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping("/image")
    public ResponseEntity<ApiResponse> uploadImage(@RequestParam("file") MultipartFile file) {
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Please select a file to upload"));
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Only image files are allowed"));
        }

        // Validate file size (max 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "File size must be less than 5MB"));
        }

        String imageUrl = cloudinaryService.uploadImage(file);

        Map<String, String> data = new HashMap<>();
        data.put("url", imageUrl);

        return ResponseEntity.ok(new ApiResponse(true, "Image uploaded successfully", data));
    }

    @PostMapping("/multiple")
    public ResponseEntity<ApiResponse> uploadMultipleImages(
            @RequestParam("files") List<MultipartFile> files) {

        if (files.isEmpty() || files.size() > 5) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Please upload between 1 and 5 images"));
        }

        // Validate each file
        for (MultipartFile file : files) {
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "All files must be images"));
            }

            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Each file must be less than 5MB"));
            }
        }

        List<String> imageUrls = cloudinaryService.uploadMultipleImages(files);

        Map<String, Object> data = new HashMap<>();
        data.put("urls", imageUrls);
        data.put("count", imageUrls.size());

        return ResponseEntity.ok(new ApiResponse(true, "Images uploaded successfully", data));
    }
}