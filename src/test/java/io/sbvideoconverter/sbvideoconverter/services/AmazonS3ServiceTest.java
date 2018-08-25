package io.sbvideoconverter.sbvideoconverter.services;

import io.sbvideoconverter.sbvideoconverter.util.Utility;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.lang3.StringUtils;
import java.io.File;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class AmazonS3ServiceTest {

    private static final String TEST_FILE = "/src/test/resources/sample_video.mp4";

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

            MultipartFile multipartFile = Utility.convertFileToMultipartFile(file);

            if (multipartFile != null) {
                String uploadedFileUrl = amazonS3Service.uploadFile(multipartFile);
                if(!StringUtils.isEmpty(uploadedFileUrl)) {
                    uploadStatus = true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        assertTrue(uploadStatus);
    }

}