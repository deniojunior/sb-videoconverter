package io.sbvideoconverter.sbvideoconverter.controllers;

import io.sbvideoconverter.sbvideoconverter.services.AmazonS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/storage/")
public class BucketController {

    private AmazonS3Service amazonS3Service;

    @Autowired
    BucketController(AmazonS3Service amazonS3Service) {
        this.amazonS3Service = amazonS3Service;
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestPart(value = "file") MultipartFile file) {
        return this.amazonS3Service.uploadFile(file) ? "success" : "error";
    }
}