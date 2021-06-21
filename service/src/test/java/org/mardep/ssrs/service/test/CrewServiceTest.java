package org.mardep.ssrs.service.test;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.domain.codetable.Nationality;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.service.ICodeTableService;
import org.mardep.ssrs.service.ICrewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class CrewServiceTest {

//	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

//	@Autowired
//	ICrewService crewService;

	private byte[] pdf; 
	
	
//	@Before
//	public void init() throws Exception {
//		String path = "D:\\Aidan_workspace\\ssrs cr006\\HKENG2_test.xlsx";
//
//		File file = new File(path);
//		URL resource = getClass().getResource("/webdefault.xml");
//		pdf = IOUtils.toByteArray(resource);
//		Path path = Paths.get("/HKENG2_test.xlsx");
//		Path path = Paths.get("/copy.jpg");
//		
//		URL resource = getClass().getResource("/test.pdf");
//		pdf = IOUtils.toByteArray(resource);
//
//	}
//	
//	@BeforeClass
//	public void beforeAll() throws IOException {
//		
//	}
	
	
	@Test
	public void testreadEng2Excel() throws Exception {
		
//		logger.info("### test readEng2Excel");
//		String file = "test.pdf";
////		byte[] excelData= Files.readAllBytes(Paths.get("/HKENG2_test.xlsx")); 
//		URL resource = getClass().getResource(file);
//		byte[] excelData = IOUtils.toByteArray(resource);
//		crewService.readEng2Excel(excelData);
	}


	
}
