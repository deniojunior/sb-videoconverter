package util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Utility {

    public static File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File("tmp/"+file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
}
