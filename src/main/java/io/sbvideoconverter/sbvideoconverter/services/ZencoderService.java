package io.sbvideoconverter.sbvideoconverter.services;

import io.sbvideoconverter.sbvideoconverter.util.Utility;
import org.json.JSONObject;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Service
public class ZencoderService {

    public static final String DEFAULT_OUTPUT_FORMAT = "mp4";

    @Value("${zencoder.apiKey}")
    private String apiKey;

    @Value("${zencoder.endpointUrl}")
    private String endpointUrl;

    @Autowired
    private AmazonS3Service amazonS3Service;

    @PostConstruct
    public void initializeZencoder() { }

    public Map<String, String> convert(String fileUrl, String outputFilePath){

        Map<String, String> result = new HashMap<>();
        URL zencoderApi;

        int responseCode = 0;
        BufferedReader bufferedReader;
        try {
            zencoderApi = new URL(endpointUrl + "/jobs");
            HttpsURLConnection connection = (HttpsURLConnection) zencoderApi.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Zencoder-Api-Key", apiKey);

            JSONObject parameters = new JSONObject();

            JSONObject output = new JSONObject();
            JSONArray outputArray = new JSONArray();

            output.put("url", outputFilePath);
            output.put("format", DEFAULT_OUTPUT_FORMAT);
            output.put("public", true);
            outputArray.put(output);

            parameters.put("input", fileUrl);

            parameters.put("outputs", outputArray);

            connection.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(parameters.toString());
            out.flush();
            out.close();

            responseCode = connection.getResponseCode();

            if (responseCode >= 200 && responseCode <= 299) {
                result.put("status", "success");
                result.put("message", Utility.readInputStreamToString(connection.getInputStream()));
                result.put("response-code", responseCode + "");
            } else {
                result.put("status", "error");
                result.put("message", "Zencode API Error: " + Utility.readInputStreamToString(connection.getErrorStream()));
                result.put("response-code", responseCode + "");
            }


        }catch (FileNotFoundException e){
            result.put("status", "error");
            result.put("message", "Arquivo não econtrado: " + fileUrl);
            result.put("response-code", responseCode+"");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", "error");
            result.put("message", e.getMessage());
            result.put("response-code", responseCode+"");
        }

        return result;
    }
}