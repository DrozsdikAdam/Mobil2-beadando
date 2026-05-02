package com.example.realtimechatbackend.service;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.webp.WebpWriter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ImageProcessingService {
    private static final int TARGET_SIZE = 512;

    public byte[] processProfileImage(MultipartFile file) throws IOException {
        ImmutableImage image = ImmutableImage.loader().fromBytes(file.getBytes());

        ImmutableImage resizedImage = image.cover(TARGET_SIZE, TARGET_SIZE);

        return resizedImage.bytes(WebpWriter.DEFAULT);
    }
}
