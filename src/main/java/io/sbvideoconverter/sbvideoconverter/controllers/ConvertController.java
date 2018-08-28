package io.sbvideoconverter.sbvideoconverter.controllers;

import io.sbvideoconverter.sbvideoconverter.exceptions.SBVideoConverterException;
import io.sbvideoconverter.sbvideoconverter.services.AmazonS3Service;
import io.sbvideoconverter.sbvideoconverter.services.ZencoderService;
import io.sbvideoconverter.sbvideoconverter.util.Utility;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/")
public class ConvertController {

    @Autowired
    private AmazonS3Service amazonS3Service;

    @Autowired
    private ZencoderService zencoderService;

    @PostMapping("/convert")
    public String convertFile(@RequestPart(value = "file") MultipartFile file) {

        JSONObject response = new JSONObject();
        String filePublicUrl = "";

        try {
            Map<String, String> responseAmazonS3 = this.amazonS3Service.uploadFile(file);

            if (responseAmazonS3.get("status").equals("error")) {
                throw new SBVideoConverterException(responseAmazonS3.get("message"));
            }

            String fileUrl = responseAmazonS3.get("file-url");

            if(!StringUtils.isEmpty(fileUrl)) {
                String uploadedFileName = Utility.generateFileName(file, "uploaded_" + new Date().getTime());
                String outputFileName = uploadedFileName.split(Utility.FILE_NAME_SEPARATOR)[1];
                outputFileName = FilenameUtils.removeExtension(outputFileName);

                long time = new Date().getTime();
                String outputFilePath = amazonS3Service.getS3FileTransferURL() + "converted_" + time +
                        Utility.FILE_NAME_SEPARATOR + outputFileName + "." + ZencoderService.DEFAULT_OUTPUT_FORMAT;

                filePublicUrl = amazonS3Service.getS3PublicURL() + "converted_" + time +
                        Utility.FILE_NAME_SEPARATOR + outputFileName + "." + ZencoderService.DEFAULT_OUTPUT_FORMAT;

                Map<String, String> responseZencoder = zencoderService.convert(fileUrl, outputFilePath);
                response.put("status", responseZencoder.get("status"));
                response.put("fileUrl", filePublicUrl);
                response.put("message", responseZencoder.get("message"));
            }
        } catch (SBVideoConverterException e) {
            response.put("status", "error");
            response.put("fileUrl", "");
            response.put("message", e.getMessage());
        }

        return response.toString();
    }

    @PostMapping("/progress")
    public String checkConvertingProgress(@RequestPart(value = "jobId") String jobId) {
        JSONObject response = new JSONObject();

        try {
            Map<String, String> responseZencoder = zencoderService.checkFileConvertingProgress(jobId);
            response.put("status", responseZencoder.get("status"));
            response.put("message", responseZencoder.get("message"));
        }catch (Exception e){
            response.put("status", "error");
            response.put("message", e.getMessage());
        }

        return response.toString();
    }
}