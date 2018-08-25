package io.sbvideoconverter.sbvideoconverter.controllers;

import io.sbvideoconverter.sbvideoconverter.services.AmazonS3Service;
import io.sbvideoconverter.sbvideoconverter.services.ZencoderService;
import io.sbvideoconverter.sbvideoconverter.util.Utility;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
        String fileUrl = this.amazonS3Service.uploadFile(file);
        history += "Result: " + (StringUtils.isEmpty(fileUrl) ? "error" : "success --> " + fileUrl);

        if(!StringUtils.isEmpty(fileUrl)) {
            history += "Encoding file...";
            String outputFileName = Utility.generateFileName(file, "");
            boolean fileEncoded = zencoderService.encode(fileUrl, outputFileName);
            history += "Result: " + (fileEncoded ? "success" : "error");
        }

        return history;
    }
}