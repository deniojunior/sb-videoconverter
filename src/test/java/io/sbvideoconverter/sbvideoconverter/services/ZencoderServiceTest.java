package io.sbvideoconverter.sbvideoconverter.services;

import io.sbvideoconverter.sbvideoconverter.util.Utility;
import org.junit.Test;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class ZencoderServiceTest {

    private static final String TEST_FILE_PATH = "/src/test/resources/sample_video.mp4";
    private static final String TEST_FILE_NAME = "sample_video.mp4";

    @Test
    public void encode() {
        boolean uploadStatus = false;

        try {
            ZencoderService zencoderService = new ZencoderService();
            String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
            File file = new File(currentPath + TEST_FILE_PATH);

            MultipartFile multipartFile = Utility.convertFileToMultipartFile(file);

            if (multipartFile != null) {
                String filePath = "http://sb-video-bucket.s3-sa-east-1.amazonaws.com/" + TEST_FILE_NAME;
                uploadStatus = zencoderService.encode(filePath, TEST_FILE_NAME);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        assertTrue(uploadStatus);
    }
}