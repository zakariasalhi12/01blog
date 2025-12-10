package com._blog._blog.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) throws IOException {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(this.fileStorageLocation);
    }

    public ResponseEntity<?> storeFile(MultipartFile file) {

        // Build JSON response helper
        Map<String, String> body = new HashMap<>();

        // ❌ empty file
        if (file.isEmpty()) {
            body.put("error", "File is empty");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }

        // ❌ size limit
        if (file.getSize() > MAX_FILE_SIZE) {
            body.put("error", "File exceeds 5MB limit");
            return new ResponseEntity<>(body, HttpStatus.PAYLOAD_TOO_LARGE); // 413
        }

        // Determine MIME type
        String contentType = file.getContentType();
        if (contentType == null) {
            body.put("error", "Could not determine file type");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }

        // Allow only images & videos
        if (!contentType.startsWith("image/") && !contentType.startsWith("video/")) {
            body.put("error", "Only image and video uploads are allowed");
            return new ResponseEntity<>(body, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }

        // Add correct extension based on MIME
        String extension = switch (contentType) {
            case "image/png" -> ".png";
            case "image/jpeg" -> ".jpg";
            case "image/webp" -> ".webp";
            case "image/gif" -> ".gif";

            case "video/mp4" -> ".mp4";
            case "video/webm" -> ".webm";
            case "video/ogg" -> ".ogg";

            default -> "";
        };

        String randomFileName = UUID.randomUUID().toString() + extension;

        try {
            Path targetLocation = this.fileStorageLocation.resolve(randomFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // ✔ Success response
            body.put("fileName", randomFileName);
            body.put("message", "File uploaded successfully");

            return new ResponseEntity<>(body, HttpStatus.OK);

        } catch (IOException e) {
            body.put("error", "Could not store file");
            return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
