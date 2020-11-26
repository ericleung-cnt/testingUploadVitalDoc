package org.mardep.ssrs.dms.ocr;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dms.ocr.service.IOcrBaseService;
import org.mardep.ssrs.dms.ocr.service.IOcrServiceShipRegistration;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlShipRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class OcrServiceShipRegistrationTest {

	@Autowired
	IOcrBaseService ocrService;
	
	@Autowired
	IOcrServiceShipRegistration service;
	
	@Test
	public void testGetEntityFromOcr() {
		List<String> filenames = ocrService.getXmlFileList("XmlTemplatePath_ShipRegistration");
		for (String name : filenames) {
			OcrXmlShipRegistration entity = service.getEntityFromOcr(name);
			service.saveEntityToSSRS(entity);
		}		
	}
}
