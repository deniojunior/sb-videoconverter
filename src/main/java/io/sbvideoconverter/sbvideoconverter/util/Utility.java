package io.sbvideoconverter.sbvideoconverter.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public class Utility {

    private static final String TEMP_DIR  = "temp/";
    public static final String FILE_NAME_SEPARATOR  = "___";

    public static void pipeStreams(java.io.InputStream source, java.io.OutputStream destination) throws IOException {
        // 1kb buffer
        byte [] buffer = new byte[1024];
        int read = 0;
        while((read=source.read(buffer)) != -1) {
            destination.write(buffer, 0, read);
        }
        destination.flush();
    }

    public static File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(TEMP_DIR + file.getOriginalFilename());

        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            pipeStreams(file.getInputStream(), fos);
        }

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
