package io.sbvideoconverter.sbvideoconverter.services;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value ;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import util.Utility;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Date;

@Service
public class AmazonS3Service {

    private AmazonS3 s3client;

    @Value("${s3-properties.bucketName}")
    private String bucketName;

    @Value("${s3-properties.accessKey}")
    private String accessKey;

    @Value("${s3-properties.secretKey}")
    private String secretKey;

    @Value("${s3-properties.region}")
    private String region;

    @PostConstruct
    public void initializeAmazon() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        this.s3client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.fromName(region))
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

    private String generateFileName(MultipartFile multiPart) {
        return new Date().getTime() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
    }

    private void uploadFileTos3bucket(String fileName, File file) {
        s3client.putObject(new PutObjectRequest(bucketName, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }

    public boolean uploadFile(MultipartFile multipartFile) {

        try {
            File file = Utility.convertMultiPartToFile(multipartFile);
            String fileName = generateFileName(multipartFile);
            uploadFileTos3bucket(fileName, file);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}