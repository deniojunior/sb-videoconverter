package io.sbvideoconverter.sbvideoconverter.services;

import io.sbvideoconverter.sbvideoconverter.util.Utility;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.Map;

import static org.junit.Assert.*;

public class ZencoderServiceTest {

    private static final String TEST_FILE_NAME = "sample_video.3gp";

    @Test
    public void convert() {
        boolean succeeded = false;

        try {
            AmazonS3Service amazonS3Service = new AmazonS3Service();
            ReflectionTestUtils.setField(amazonS3Service, "bucketName", System.getenv("S3_BUCKET_NAME"));
            ReflectionTestUtils.setField(amazonS3Service, "accessKey", System.getenv("S3_ACCESS_KEY"));
            ReflectionTestUtils.setField(amazonS3Service, "secretKey", System.getenv("S3_SECRET_KEY"));
            ReflectionTestUtils.setField(amazonS3Service, "region", System.getenv("S3_REGION"));
            amazonS3Service.initializeAmazon();

            ZencoderService zencoderService = new ZencoderService();
            ReflectionTestUtils.setField(zencoderService, "endpointUrl", System.getenv("ZENCODER_ENDPOINT_URL"));
            ReflectionTestUtils.setField(zencoderService, "apiKey", System.getenv("ZENCODER_API_KEY"));

            String filePath = amazonS3Service.getS3FileTransferURL() + TEST_FILE_NAME;
            String outputFilePath = amazonS3Service.getS3FileTransferURL() + "converted_" + new Date().getTime() +
                    Utility.FILE_NAME_SEPARATOR + TEST_FILE_NAME + "." + ZencoderService.DEFAULT_OUTPUT_FORMAT;

            Map<String, String> result = zencoderService.convert(filePath, outputFilePath);
            succeeded = result.get("status").equals("success");

        }catch (Exception e){
            e.printStackTrace();
        }

        assertTrue(succeeded);
    }
}