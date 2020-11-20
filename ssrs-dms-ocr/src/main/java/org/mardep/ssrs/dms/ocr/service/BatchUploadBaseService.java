package org.mardep.ssrs.dms.ocr.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.itextpdf.text.pdf.PdfOCProperties;

import org.springframework.stereotype.Service;

@Service
public class BatchUploadBaseService implements IBatchUploadBaseService {

    private Properties props;
	private final String PROP_UNIT_TESTING_ENABLED = "UnitTesting_Enabled";

    public BatchUploadBaseService() throws IOException {
		props = new Properties();
		try (InputStream input = getClass().getResourceAsStream("/ocr.properties")) {
			props.load(input);
		}
    }

    @Override
    public List<String> getFileList(String propertyName) {
        // TODO Auto-generated method stub
		String prefix;
		
		if (getUnitTestingEnabled()){
			//prefix = "d:/mdtrusted-workspace/dms_template";			
			prefix = "//10.37.115.143/d/OCR_Data/";
		} else {
			prefix = System.getProperty("dms.ocr.service.OcrBaseService.dir");			
		}

        String uploadDirectory = prefix + "/" + props.getProperty(propertyName);
        List<String> files = new ArrayList<String>();
        File folder = new File(uploadDirectory);
        File[] filenames = folder.listFiles();

        if (filenames!=null){
            //files = Arrays.asList(filenames);
			for (File f : filenames) {
				String filename = f.getPath();
				//if (filename.contains(".xml")) {
					files.add(filename);
				//}
			}

        }
        return files;
    }
	
	@Override
	public String getFilenameFromFullPath(String fullPath){
		Path path = Paths.get(fullPath);
		String filename = path.getFileName().toString();
		return filename;
	}

    //@Override
	private boolean getUnitTestingEnabled() {
		if ("True".equals(props.getProperty(PROP_UNIT_TESTING_ENABLED))){
			return true;			
		} else {
			return false;			
		}	
	}

    @Override
    public Map<String, String> createVitalDocPropertiesForSrIssuedDoc(String imo, String shipName, String officialNum) {
		Map<String, String> vitalDocProperties = new HashMap<String, String>();
		vitalDocProperties.put("IMO number", imo==null ? "" : imo);
		vitalDocProperties.put("Ship name", shipName);
		vitalDocProperties.put("Official Number", officialNum);
		return vitalDocProperties;
	}

}