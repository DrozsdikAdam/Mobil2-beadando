package com.example.realtimechatbackend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SupabaseStorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket.images}")
    private String bucketName;

    private final RestTemplate restTemplate;

    public SupabaseStorageService() {
        this.restTemplate = new RestTemplate();
    }

    public String uploadImage(byte[] imageBytes, String fileName) {
        // 1. Összeállítjuk a Supabase Storage REST API végpontját.
        String uploadUrl = String.format("%s/storage/v1/object/%s/%s", supabaseUrl, bucketName, fileName);

        // 2. Beállítjuk a HTTP headereket
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + supabaseKey);
        headers.set("apikey", supabaseKey);

        headers.setContentType(MediaType.parseMediaType("image/webp"));

        // 3. Összerakjuk a Request-et
        HttpEntity<byte[]> requestEntity = new HttpEntity<>(imageBytes, headers);

        // 4. Elküldjük a POST kérést
        ResponseEntity<String> response = restTemplate.exchange(
                uploadUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Hiba történt a Supabase feltöltés során: " + response.getBody());
        }

        // Visszaadjuk a mentett fájl relatív útvonalát (bucketNév/fájlNév)
        return bucketName + "/" + fileName;
    }

    public String getPublicUrl(String bucketPath) {
        // Formátum: [PROJECT_URL]/storage/v1/object/[BUCKET_NAME]/[FILE_NAME]
        return String.format("%s/storage/v1/object/public/%s", supabaseUrl, bucketPath);
    }
}