package io.sbvideoconverter.sbvideoconverter.services;

import io.sbvideoconverter.sbvideoconverter.util.Utility;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.lang3.StringUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.Assert.*;

public class AmazonS3ServiceTest {

    private static final String TEST_FILE = "/src/test/resources/sample_video.3gp";

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
                Map<String, String> response = amazonS3Service.uploadFile(multipartFile);
                if(response.get("status").equals("success")) {
                    String uploadedFileUrl = response.get("file-url");
                    if (!StringUtils.isEmpty(uploadedFileUrl)) {
                        uploadStatus = true;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        assertTrue(uploadStatus);
    }

    private static MultipartFile convertFileToMultipartFile(File file){
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