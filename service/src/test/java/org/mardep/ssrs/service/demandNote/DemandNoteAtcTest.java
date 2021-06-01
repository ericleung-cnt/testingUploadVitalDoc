package org.mardep.ssrs.service.demandNote;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.service.DemandNoteAtcService;
//import org.mardep.ssrs.service.DemandNoteAtcService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import junit.framework.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:atcTest-context.xml")
public class DemandNoteAtcTest {
	
	@Test
	public void testCalcAtcAmt() throws ParseException {
		DemandNoteAtcService svc = new DemandNoteAtcService();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date regDate = sdf.parse("2008-06-30");
		Date detainDate = sdf.parse("2019-07-03");
		Date dueDate = sdf.parse("2021-06-30");
//		Date regDate = sdf.parse("2019-06-28");
//		Date detainDate = sdf.parse("2018-01-21");
//		Date dueDate = sdf.parse("2020-06-28");
		BigDecimal fullAtc = new BigDecimal("77500");
		BigDecimal halfAtc = new BigDecimal("38750");
		//BigDecimal atcAmt = svc.calcDetainAtcAmt(regDate, detainDate, dueDate, fullAtc, halfAtc);
		BigDecimal atcAmt = svc.calcAtcAmt(regDate, detainDate, dueDate, fullAtc, null);
		Assert.assertEquals(fullAtc, atcAmt);				
	}

	@Test
	public void testGetFirstAnniversaryDate() throws ParseException {
		DemandNoteAtcService svc = new DemandNoteAtcService();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date regDate = sdf.parse("2020-12-31");
		Date firstAnniversaryDate = svc.calcFirstAnniversaryDate(regDate);
		String expectedDateStr = "2006-02-01";
		Assert.assertEquals("2021-12-31", sdf.format(firstAnniversaryDate));
	}
	
	@Test
	public void testCalcNonDetainAtcAmt() throws ParseException {
		DemandNoteAtcService svc = new DemandNoteAtcService();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date regDate = sdf.parse("2019-06-28");
		Date dueDate = sdf.parse("2020-06-28");
		BigDecimal fullAtc = new BigDecimal("39269");
		BigDecimal halfAtc = new BigDecimal("19634.5");
		BigDecimal atcAmt = svc.calcNonDetainAtcAmt(regDate, dueDate, fullAtc, halfAtc);
		Assert.assertEquals(halfAtc, atcAmt);
	}
	
	@Test
	public void testCalcDetainAnniversaryDate() throws ParseException {
		DemandNoteAtcService svc = new DemandNoteAtcService();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date regDate = sdf.parse("2016-10-28");
		Date detainDate = sdf.parse("2016-10-28");
		Date detainAnniversaryDate = svc.calcDetainAnniversaryDate(regDate, detainDate);
		Assert.assertEquals("2018-10-28", sdf.format(detainAnniversaryDate));
	}
	
	@Test
	public void testCalcDetainAtcAmt() throws ParseException {
		DemandNoteAtcService svc = new DemandNoteAtcService();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		Date regDate = sdf.parse("2008-06-30");
//		Date detainDate = sdf.parse("2020-02-03");
		Date regDate = sdf.parse("2019-06-28");
		Date detainDate = sdf.parse("2018-01-21");
		Date dueDate = sdf.parse("2020-06-28");
		BigDecimal fullAtc = new BigDecimal("77500");
		BigDecimal halfAtc = new BigDecimal("38750");
		BigDecimal atcAmt = svc.calcDetainAtcAmt(regDate, detainDate, dueDate, fullAtc, halfAtc);
		Assert.assertEquals(fullAtc, atcAmt);		
	}
}
