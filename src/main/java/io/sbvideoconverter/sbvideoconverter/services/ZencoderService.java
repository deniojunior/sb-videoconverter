package io.sbvideoconverter.sbvideoconverter.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.sbvideoconverter.sbvideoconverter.util.Utility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Service
public class ZencoderService {

    @Value("${zencoder.apiKey}")
    private String apiKey;

    @Value("${zencoder.endpointUrl}")
    private String endpointUrl;

    private RestTemplate rt;
    private ObjectMapper mapper;

    @PostConstruct
    public void initializeZencoder() { }

    public boolean encode(String fileUrl, String outputFileName){
        URL zencodeApi = null;
        int status = 0;
        try {
            zencodeApi = new URL(endpointUrl);
            HttpsURLConnection connection = (HttpsURLConnection) zencodeApi.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Zencoder-Api-Key", apiKey);

            Map<String, String> parameters = new HashMap<>();
            parameters.put("input", fileUrl);
            parameters.put("url", "http://sb-video-bucket.s3-sa-east-1.amazonaws.com");
            parameters.put("filename", "_converted" + outputFileName);

            connection.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(Utility.getParamsString(parameters));
            out.flush();
            out.close();

            status = connection.getResponseCode();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return status == 200 || status == 201;
    }
}