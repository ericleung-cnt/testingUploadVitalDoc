package org.mardep.ssrs.dms.ocr;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dms.ocr.service.IOcrBaseService;
import org.mardep.ssrs.dms.ocr.service.IOcrServiceEng2A;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlEng2A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class OcrServiceEng2ATest {
	@Autowired
	IOcrBaseService ocrService;
	
	@Autowired
	IOcrServiceEng2A service;
	
	@Test
	public void testGetEntityFromOcr() {
		List<String> filenames = ocrService.getXmlFileList("XmlTemplatePath_Eng2A");
		for (String name : filenames) {
			OcrXmlEng2A entity = service.getEntityFromOcr(name);
			service.saveEntityToSSRS(entity);
		}		

	}
}
