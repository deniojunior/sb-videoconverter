package io.sbvideoconverter.sbvideoconverter.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public class Utility {

    private static final String TEMP_DIR  = "temp/";
    public static final String FILE_NAME_SEPARATOR  = "___";

    public static File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(TEMP_DIR + file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convertedFile);
        fos.write(file.getBytes());
        fos.close();

        return convertedFile;
    }

    public static String generateFileName(MultipartFile multiPart, String prefix) {
        return prefix + FILE_NAME_SEPARATOR + multiPart.getOriginalFilename()
                .replace(" ", "_");
    }

    public static String readInputStreamToString(InputStream input) {
        String result = null;
        StringBuffer stringBuffer = new StringBuffer();
        InputStream inputStream = null;

        try {
            inputStream = new BufferedInputStream(input);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String inputLine = "";
            while ((inputLine = br.readLine()) != null) {
                stringBuffer.append(inputLine);
            }
            result = stringBuffer.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
            result = null;
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }
}
