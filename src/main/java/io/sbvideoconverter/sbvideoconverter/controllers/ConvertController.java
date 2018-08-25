package io.sbvideoconverter.sbvideoconverter.controllers;

import io.sbvideoconverter.sbvideoconverter.exceptions.SBVideoConverterException;
import io.sbvideoconverter.sbvideoconverter.services.AmazonS3Service;
import io.sbvideoconverter.sbvideoconverter.services.ZencoderService;
import io.sbvideoconverter.sbvideoconverter.util.Utility;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
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

        String history = "Uploading file...";
        try {
            Map<String, String> responseAmazonS3 = this.amazonS3Service.uploadFile(file);

            if (responseAmazonS3.get("status").equals("error")){
                throw new SBVideoConverterException("Falha ao realizar o upload para Amazon S3");
            }

            String fileUrl = responseAmazonS3.get("file-url");
            history += "Result: " + (StringUtils.isEmpty(fileUrl) ? "error" : "success --> " + fileUrl);

            if (!StringUtils.isEmpty(fileUrl)) {
                history += "Encoding file...";
                String uploadedFileName = Utility.generateFileName(file, "uploaded_" + new Date().getTime());
                String outputFileName = uploadedFileName.split(Utility.FILE_NAME_SEPARATOR)[1];
                outputFileName = FilenameUtils.removeExtension(outputFileName);

                String outputFilePath = amazonS3Service.getS3FileTransferURL() + "converted_" + new Date().getTime() +
                        Utility.FILE_NAME_SEPARATOR + outputFileName + "." + ZencoderService.DEFAULT_OUTPUT_FORMAT;

                Map<String, String> responseZencoder = zencoderService.convert(fileUrl, outputFilePath);
                history += "Result: " + responseZencoder.get("status");
            }
        }catch (SBVideoConverterException e){
            history += "Result: " + e.getMessage();
        }

        return history;
    }
}