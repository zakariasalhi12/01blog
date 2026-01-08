package com._blog._blog.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private final Tika tika = new Tika();

    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) throws IOException {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(this.fileStorageLocation);
    }

    public ResponseEntity<?> storeFile(MultipartFile file) {

        Map<String, String> body = new HashMap<>();

        // ❌ empty file
        if (file.isEmpty()) {
            body.put("error", "File is empty");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }

        // ❌ size limit
        if (file.getSize() > MAX_FILE_SIZE) {
            body.put("error", "File exceeds 5MB limit");
            return new ResponseEntity<>(body, HttpStatus.PAYLOAD_TOO_LARGE);
        }

        // 🔐 Detect real MIME type using Tika
        String detectedType;
        try (InputStream is = file.getInputStream()) {
            detectedType = tika.detect(is);
        } catch (IOException e) {
            body.put("error", "Could not read file");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }

        // ❌ allow only image & video
        boolean isImage = detectedType.startsWith("image/");
        boolean isVideo = detectedType.startsWith("video/")
                || detectedType.equals("video/x-matroska")
                || detectedType.equals("application/x-matroska");

        if (!isImage && !isVideo) {
            body.put("error", "Only image and video uploads are allowed");
            return new ResponseEntity<>(body, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }

        // ✅ Map MIME → extension (STRICT)
        String baseType = detectedType.split(";")[0].trim();

        String extension = switch (baseType) {

            case "image/png" ->
                ".png";
            case "image/jpeg" ->
                ".jpg";
            case "image/webp" ->
                ".webp";
            case "image/gif" ->
                ".gif";

            case "video/mp4" ->
                ".mp4";

            case "video/webm", "video/x-matroska", "application/x-matroska" ->
                ".webm";

            case "video/ogg" ->
                ".ogg";

            default -> {
                body.put("error", "Unsupported image/video format: " + baseType);
                yield null;
            }
        };

        if (extension == null) {
            return new ResponseEntity<>(body, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }

        String randomFileName = UUID.randomUUID() + extension;

        try {
            Path targetLocation = this.fileStorageLocation.resolve(randomFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            body.put("fileName", randomFileName);
            body.put("message", "File uploaded successfully");

            return new ResponseEntity<>(body, HttpStatus.OK);

        } catch (IOException e) {
            body.put("error", "Could not store file");
            return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
