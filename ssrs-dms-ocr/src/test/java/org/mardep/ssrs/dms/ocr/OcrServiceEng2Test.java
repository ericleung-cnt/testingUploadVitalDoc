package org.mardep.ssrs.dms.ocr;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dms.ocr.service.IOcrBaseService;
import org.mardep.ssrs.dms.ocr.service.IOcrServiceEng2;
import org.mardep.ssrs.dms.ocr.service.OcrServiceEng2;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlEng2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class OcrServiceEng2Test {

	@Autowired
	IOcrBaseService ocrService;
	
	@Autowired
	IOcrServiceEng2 service;
	
	@Test
	public void testGetEntityFromOcr() {
		List<String> filenames = ocrService.getXmlFileList("XmlTemplatePath_Eng2");
		for (String name : filenames) {
			OcrXmlEng2 entity = service.getEntityFromOcr(name);
			service.saveEntityToSSRS(entity);
		}		
	}
	
	@Test
	public void testParseSeafarerName() {
		OcrServiceEng2 svc = new OcrServiceEng2();
		String rawStr = "：〔•）石向军SHI XIANGJUN";
		String SPECIAL_STR = "[\\t\\n\\r*：»〔•）]+"; //"[!@#$%^&?<>:-_»]*";
		String seafarerName = svc.parseSeafarerName(rawStr, SPECIAL_STR);
		System.out.println(seafarerName);
	}
	
	@Test
	public void testParsePlaceOfDischarge() {
		OcrServiceEng2 svc = new OcrServiceEng2();
		String rawStr = "r, 各 •\n;- _ ^\n";
		String specialStr = "[\\t\\n\\r(>;&_《«•]";
		String s = svc.parsePlaceOfDischarge(rawStr, specialStr);
		System.out.println(s);
	}
}
