package org.mardep.ssrs.dms.ocr.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BatchUploadBaseService implements IBatchUploadBaseService {

    private Properties props;
	private final String PROP_UNIT_TESTING_ENABLED = "UnitTesting_Enabled";

	Logger logger = LoggerFactory.getLogger(BatchUploadBaseService.class);

    public BatchUploadBaseService() throws IOException {
		props = new Properties();
		try (InputStream input = getClass().getResourceAsStream("/ocr.properties")) {
			props.load(input);
		}
    }

	private String getBaseDir(){
		String prefix;
		
		if (getUnitTestingEnabled()){
			//prefix = "d:/mdtrusted-workspace/dms_template";			
			prefix = "//10.37.115.143/d/OCR_Data/";
		} else {
			prefix = System.getProperty("dms.ocr.service.OcrBaseService.dir");			
		}
		return prefix;
	}

	private String getPostActionProcessedDir(String propertyName){
		String prefix = getBaseDir();
		String processedDir = prefix + "/" + props.getProperty(propertyName);
		return processedDir;
	}

	private String getPostActionErrorDir(String propertyName){
		String prefix = getBaseDir();
		String errorDir = prefix + "/" + props.getProperty(propertyName);
		return errorDir;
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

	@Override
	public void postActionProcessedEntity(String pathname) {
		// TODO Auto-generated method stub
		String postActionDirStr = getPostActionProcessedDir("processed");
		postActionHandleEntity(pathname, postActionDirStr);
	}

	// @Override
	// public void postActionInvalidEntity(String pathname) {
	// 	// TODO Auto-generated method stub
	// 	postActionHandleEntity(pathname, "invalid");
	// }

	@Override
	public void postActionExceptionEntity(String pathname) {
		// TODO Auto-generated method stub
		String postActionDirStr = getPostActionErrorDir("exception");
		postActionHandleEntity(pathname, postActionDirStr);
	}

	private void postActionHandleEntity(String pathname, String postActionDirStr) {
		//String xmlFilename = Paths.get(pathname).getFileName().toString(); // getOcrDirectory(propertyName);
		//String pdfFilename = xmlFilename.replace(".xml", ".pdf");
		//String ocrDirectory = pathname.replace(xmlFilename,"");
		
		//File directory = new File(ocrDirectory + postActionDirStr);
		// File directory = new File (postActionDirStr);
	    // if (! directory.exists()){
	    //     directory.mkdir();
	    // }

		// Path xmlSource = Paths.get(ocrDirectory + xmlFilename);
		// Path xmlTarget = Paths.get(ocrDirectory + postActionDirStr + "/" + xmlFilename.replace(".xml", "." + postActionDirStr + ".xml"));
		// Path pdfSource = Paths.get(ocrDirectory + pdfFilename);
		// Path pdfTarget = Paths.get(ocrDirectory + postActionDirStr + "/" + pdfFilename.replace(".pdf", "." + postActionDirStr + ".pdf"));

		try {
			// File sourceXml = new File(xmlSource.toString());
			// if (sourceXml.exists()) {
			// 	Files.move(xmlSource, xmlTarget, StandardCopyOption.REPLACE_EXISTING);
			// 	logger.info("{} move to {}",  xmlSource.toString(), xmlTarget.toString() );
			// }
			// File sourcePdf = new File(pdfSource.toString());
			// if (sourcePdf.exists()) {
			// 	Files.move(pdfSource, pdfTarget, StandardCopyOption.REPLACE_EXISTING);
			// 	logger.info("{} move to {}",  pdfSource.toString(), pdfTarget.toString() );
			// }
				//Files.copy(pdfSource, pdfTarget, StandardCopyOption.REPLACE_EXISTING);
			Files.deleteIfExists(Paths.get(pathname));
		} catch (IOException e) {
			logger.error(e.getMessage());
			System.err.println(e);
			e.printStackTrace();
		}
	}

}