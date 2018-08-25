package io.sbvideoconverter.sbvideoconverter.services;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value ;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import io.sbvideoconverter.sbvideoconverter.util.Utility;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

    @Value("${s3-properties.endpointUrl}")
    private String endpointUrl;

    @PostConstruct
    public void initializeAmazon() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        this.s3client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.fromName(region))
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

    private void uploadFileTos3bucket(String fileName, File file) {
        s3client.putObject(new PutObjectRequest(bucketName, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }

    public Map<String, String> uploadFile(MultipartFile multipartFile) {

        Map<String, String> result = new HashMap<>();
        String fileUrl = "";

        try {
            File file = Utility.convertMultiPartToFile(multipartFile);
            String fileName = Utility.generateFileName(multipartFile, "uploaded_" + new Date().getTime());
            uploadFileTos3bucket(fileName, file);
            fileUrl = endpointUrl + fileName;
            file.delete();

            result.put("status", "success");
            result.put("message", "Success");
            result.put("file-url", fileUrl);

        } catch (AmazonS3Exception e){
            result.put("status", "error");
            result.put("message", "Erro ao fazer o upload para o Amazon S3: " + e.getMessage());
            result.put("file-url", "");
            e.printStackTrace();
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
            result.put("file-url", "");
            e.printStackTrace();
        }

        return result;
    }

    public String getS3FileTransferURL(){
        return "s3://"+this.bucketName+"/";
    }

    public String getBucketName() {
        return bucketName;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getRegion() {
        return region;
    }

    public String getEndpointUrl() {
        return endpointUrl;
    }
}