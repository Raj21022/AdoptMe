package com.adoptme.backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) {
        try {
            // Generate unique filename
            String publicId = "adoptme/animals/" + UUID.randomUUID().toString();

            // Upload to Cloudinary
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", publicId,
                            "folder", "adoptme/animals",
                            "resource_type", "image",
                            "transformation", ObjectUtils.asMap(
                                    "width", 800,
                                    "height", 800,
                                    "crop", "limit",
                                    "quality", "auto"
                            )
                    ));

            return (String) uploadResult.get("secure_url");

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image: " + e.getMessage());
        }
    }

    public List<String> uploadMultipleImages(List<MultipartFile> files) {
        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                String url = uploadImage(file);
                imageUrls.add(url);
            }
        }

        return imageUrls;
    }

    public void deleteImage(String imageUrl) {
        try {
            // Extract public_id from URL
            String publicId = extractPublicIdFromUrl(imageUrl);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete image: " + e.getMessage());
        }
    }

    private String extractPublicIdFromUrl(String imageUrl) {
        // Extract public_id from Cloudinary URL
        // Example: https://res.cloudinary.com/xxx/image/upload/v123/adoptme/animals/uuid.jpg
        // Extract: adoptme/animals/uuid
        String[] parts = imageUrl.split("/upload/");
        if (parts.length > 1) {
            String pathWithVersion = parts[1];
            // Remove version number (v123456/)
            String path = pathWithVersion.replaceFirst("v\\d+/", "");
            // Remove file extension
            return path.substring(0, path.lastIndexOf('.'));
        }
        return "";
    }
}