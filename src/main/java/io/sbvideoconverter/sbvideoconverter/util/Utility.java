package io.sbvideoconverter.sbvideoconverter.util;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Map;

public class Utility {

    private static final String TEMP_DIR  = "tmp/";

    public static File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(TEMP_DIR + file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convertedFile);
        fos.write(file.getBytes());
        fos.close();

        return convertedFile;
    }

    public static MultipartFile convertFileToMultipartFile(File file){
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

    public static String generateFileName(MultipartFile multiPart, String prefix) {
        return prefix + new Date().getTime() + "__" + multiPart.getOriginalFilename()
                .replace(" ", "_");
    }

    public static String getParamsString(Map<String, String> params)
            throws UnsupportedEncodingException {

        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            result.append("&");
        }

        String resultString = result.toString();

        return resultString.length() > 0
                ? resultString.substring(0, resultString.length() - 1)
                : resultString;
    }
}
