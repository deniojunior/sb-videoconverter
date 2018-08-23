package io.sbvideoconverter.sbvideoconverter.controllers;

import io.sbvideoconverter.sbvideoconverter.services.AmazonS3Service;
import org.junit.Test;

import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class BucketControllerTest extends AbstractJUnit4SpringContextTests {

    private static final String TEST_FILE = "/src/test/resources/test.html";

    @Test
    public void uploadFile() {

        boolean uploadStatus = false;

        try {
            AmazonS3Service amazonS3Service = new AmazonS3Service();
            ReflectionTestUtils.setField(amazonS3Service, "bucketName", System.getenv("S3_BUCKET_NAME"));
            ReflectionTestUtils.setField(amazonS3Service, "accessKey", System.getenv("S3_ACCESS_KEY"));
            ReflectionTestUtils.setField(amazonS3Service, "secretKey", System.getenv("S3_SECRET_KEY"));
            ReflectionTestUtils.setField(amazonS3Service, "region", System.getenv("S3_REGION"));
            amazonS3Service.initializeAmazon();

            String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
            File file = new File(currentPath + TEST_FILE);

            MultipartFile multipartFile = this.convertFileToMultipartFile(file);

            if (multipartFile != null) {
                uploadStatus = amazonS3Service.uploadFile(multipartFile);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        assertTrue(uploadStatus);
    }

    private MultipartFile convertFileToMultipartFile(File file){
        try {
            Path path = Paths.get(file.getAbsolutePath());
            String name = file.getName();
            String originalFileName = file.getName();
            String contentType = Files.probeContentType(path);

            byte[] content = Files.readAllBytes(path);

            return new MockMultipartFile(name,
                    originalFileName, contentType, content);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}