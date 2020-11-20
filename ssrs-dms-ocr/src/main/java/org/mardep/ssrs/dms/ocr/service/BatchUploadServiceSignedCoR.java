package org.mardep.ssrs.dms.ocr.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;

@Service
public class BatchUploadServiceSignedCoR implements IBatchUploadServiceSignedCoR {

    @Override
    public byte[] getFileContent(String filename) throws Exception{
        // TODO Auto-generated method stub
        try {
            byte[] fileContent = Files.readAllBytes(Paths.get(filename));
            // File file = new File(filename);
            // OutputStream os = new FileOutputStream(file);
            // os.write(fileContent);
            // os.close();				
            return fileContent;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
}