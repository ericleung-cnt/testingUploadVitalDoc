package org.mardep.ssrs.service.demandNote;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dao.Criteria;
import org.mardep.ssrs.dao.Operator;
import org.mardep.ssrs.dao.dn.DemandNoteItemJpaDao;
import org.mardep.ssrs.dao.sr.RegMasterJpaDao;
import org.mardep.ssrs.domain.dn.DemandNoteItem;
import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.service.DemandNoteAtcService;
import org.mardep.ssrs.service.DemandNoteService;
import org.mardep.ssrs.service.IDemandNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
//@ContextConfiguration("/test-context.xml")
@Transactional
public class DemandNoteServiceATCTest {

//	@Autowired
//	IDemandNoteService dnSvc;
	
//	@Autowired
//	IDemandNoteService  dns;
	
	

	@Test
	public void testATC_RegDate_NoDetain_1stYear_ReturnFullATC() throws ParseException {
		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date dueDate = sdf.parse("13/06/2017");
		Date regDate = sdf.parse("13/06/2016");
		Date detainDate = null;
		//RegMaster rm = prepare_RegMaster_RegDate_DetainDate_Passed2Yrs();
		BigDecimal fullATC = new BigDecimal(77500.00);
		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
		BigDecimal lastATC = null;
		
		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
		assertEquals(expectATC, fullATC);					
	}
	
	@Test
	public void testATC_RegDate_Detain_1stYear_ReturnFullATC() throws ParseException {
		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date dueDate = sdf.parse("13/06/2017");
		Date regDate = sdf.parse("13/06/2016");
		Date detainDate = sdf.parse("13/06/2017");
		//RegMaster rm = prepare_RegMaster_RegDate_DetainDate_Passed2Yrs();
		BigDecimal fullATC = new BigDecimal(77500.00);
		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
		BigDecimal lastATC = null;
		
		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
		assertEquals(expectATC, fullATC);					
	}
	

	@Test
	public void testATC_RegDate_NoDetain_2ndYear_ReturnFullATC() throws ParseException {
		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date dueDate = sdf.parse("13/06/2018");
		Date regDate = sdf.parse("13/06/2016");
		Date detainDate = null;
		//RegMaster rm = prepare_RegMaster_RegDate_DetainDate_Passed2Yrs();
		BigDecimal fullATC = new BigDecimal(77500.00);
		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
		BigDecimal lastATC = fullATC;
		
		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
		assertEquals(expectATC, fullATC);					
	}
	
	@Test
	public void testATC_RegDate_Detain_2ndYear_ReturnFullATC() throws ParseException {
		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date dueDate = sdf.parse("13/06/2018");
		Date regDate = sdf.parse("13/06/2016");
		Date detainDate =  sdf.parse("13/06/2018");
		//RegMaster rm = prepare_RegMaster_RegDate_DetainDate_Passed2Yrs();
		BigDecimal fullATC = new BigDecimal(77500.00);
		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
		BigDecimal lastATC = fullATC;
		
		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
		assertEquals(expectATC, fullATC);					
	}
	
	
	
	
	

	//2017 full, full,half
	@Test
	public void testATC_RegDate_NoDetain_3rdYrs_ReturnFullATC() throws ParseException {
		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date dueDate = sdf.parse("13/06/2019");
		Date regDate = sdf.parse("13/06/2016");
		Date detainDate = null;
		//RegMaster rm = prepare_RegMaster_RegDate_NoDetain_Passed2Yrs();
		BigDecimal fullATC = new BigDecimal(77500.00);
		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
		BigDecimal lastATC = fullATC;
		
		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
		assertEquals(expectATC, halfATC);
	}
	//2017 full, full,half ,full
	@Test
	public void testATC_RegDate_NoDetain_4thYrs_ReturnFullATC() throws ParseException {
		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date dueDate = sdf.parse("13/06/2020");
		Date regDate = sdf.parse("13/06/2016");
		Date detainDate = null;
		//RegMaster rm = prepare_RegMaster_RegDate_NoDetain_Passed2Yrs();
		BigDecimal fullATC = new BigDecimal(77500.00);
		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
		BigDecimal lastATC = halfATC;
		
		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
		assertEquals(expectATC, fullATC);
	}
	

	
	
	//2017 full, full,full
	@Test
	public void testATC_RegDate_detained_beforeAnnivDay() throws ParseException {
		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date dueDate = sdf.parse("13/06/2019");
		Date regDate = sdf.parse("13/06/2016");
		Date detainDate = sdf.parse("13/05/2019");
		//RegMaster rm = prepare_RegMaster_RegDate_NoDetain_Passed2Yrs();
		BigDecimal fullATC = new BigDecimal(77500.00);
		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
		BigDecimal lastATC = fullATC;
		
		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
		assertEquals(expectATC, fullATC);
	}
	//2017 full, full,full,full
	@Test
	public void testATC_RegDate_detained_beforeAnnivDay2() throws ParseException {
		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date dueDate = sdf.parse("13/06/2020");
		Date regDate = sdf.parse("13/06/2016");
		Date detainDate = sdf.parse("13/05/2019");
		//RegMaster rm = prepare_RegMaster_RegDate_NoDetain_Passed2Yrs();
		BigDecimal fullATC = new BigDecimal(77500.00);
		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
		BigDecimal lastATC = halfATC;
		
		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
		assertEquals(expectATC, fullATC);
	}
	//2017 full, full,full,full,half
	@Test
	public void testATC_RegDate_detained_beforeAnnivDay3() throws ParseException {
		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date dueDate = sdf.parse("13/06/2021");
		Date regDate = sdf.parse("13/06/2016");
		Date detainDate = sdf.parse("13/05/2019");
		//RegMaster rm = prepare_RegMaster_RegDate_NoDetain_Passed2Yrs();
		BigDecimal fullATC = new BigDecimal(77500.00);
		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
		BigDecimal lastATC = fullATC;
		
		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
		assertEquals(expectATC, halfATC);
	}
	
	//2017 full, full,full,full,half,full
	@Test
	public void testATC_RegDate_detaine_beforeAnnivDay4() throws ParseException {
		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date dueDate = sdf.parse("13/06/2022");
		Date regDate = sdf.parse("13/06/2016");
		Date detainDate = sdf.parse("13/05/2019");
		//RegMaster rm = prepare_RegMaster_RegDate_NoDetain_Passed2Yrs();
		BigDecimal fullATC = new BigDecimal(77500.00);
		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
		BigDecimal lastATC = fullATC;
		
		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
		assertEquals(expectATC, fullATC);
	}
	
	

	
	//2017 full, full,half,
	@Test
	public void testATC_RegDate_detained_after_AnnivDay1() throws ParseException {
		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date dueDate = sdf.parse("13/06/2019");
		Date regDate = sdf.parse("13/06/2016");
		Date detainDate = sdf.parse("13/07/2019");
		//RegMaster rm = prepare_RegMaster_RegDate_NoDetain_Passed2Yrs();
		BigDecimal fullATC = new BigDecimal(77500.00);
		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
		BigDecimal lastATC = halfATC;
		
		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
		assertEquals(expectATC, halfATC);
	}
	
	//2017 full, full,half,full,
	@Test
	public void testATC_RegDate_detained_after_AnnivDay2() throws ParseException {
		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date dueDate = sdf.parse("13/06/2020");
		Date regDate = sdf.parse("13/06/2016");
		Date detainDate = sdf.parse("13/07/2019");
		//RegMaster rm = prepare_RegMaster_RegDate_NoDetain_Passed2Yrs();
		BigDecimal fullATC = new BigDecimal(77500.00);
		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
		BigDecimal lastATC = halfATC;
		
		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
		assertEquals(expectATC, fullATC);
	}
	//2017 full, full,half,full,full
	@Test
	public void testATC_RegDate_detained_after_AnnivDay3() throws ParseException {
		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date dueDate = sdf.parse("13/06/2021");
		Date regDate = sdf.parse("13/06/2016");
		Date detainDate = sdf.parse("13/07/2019");
		//RegMaster rm = prepare_RegMaster_RegDate_NoDetain_Passed2Yrs();
		BigDecimal fullATC = new BigDecimal(77500.00);
		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
		BigDecimal lastATC = fullATC;
		
		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
		assertEquals(expectATC, fullATC);
	}
	
	//2017 full , 2018 full, 2019 half, 2020,full, 2021 full, 2022 half
	@Test
	public void testATC_RegDate_detained_after_AnnivDay4() throws ParseException {
		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date dueDate = sdf.parse("13/06/2022");
		Date regDate = sdf.parse("13/06/2016");
		Date detainDate = sdf.parse("13/07/2019");
		//RegMaster rm = prepare_RegMaster_RegDate_NoDetain_Passed2Yrs();
		BigDecimal fullATC = new BigDecimal(77500.00);
		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
		BigDecimal lastATC = halfATC;
		
		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
		assertEquals(expectATC, halfATC);
	}
	
	
	//2017 full, full,half,
		@Test
		public void testATC_RegDate_detained_in_AnnivDay1() throws ParseException {
			DemandNoteAtcService atcSvc = new DemandNoteAtcService();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date dueDate = sdf.parse("13/06/2019");
			Date regDate = sdf.parse("13/06/2016");
			Date detainDate = null;
			//RegMaster rm = prepare_RegMaster_RegDate_NoDetain_Passed2Yrs();
			BigDecimal fullATC = new BigDecimal(77500.00);
			BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
			BigDecimal lastATC = halfATC;
			
			BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
			assertEquals(expectATC, halfATC);
		}
		
		//2017 full, full,half,full,
		@Test
		public void testATC_RegDate_detained_in_AnnivDay2() throws ParseException {
			DemandNoteAtcService atcSvc = new DemandNoteAtcService();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date dueDate = sdf.parse("13/06/2020");
			Date regDate = sdf.parse("13/06/2016");
			Date detainDate = sdf.parse("13/06/2019");
			//RegMaster rm = prepare_RegMaster_RegDate_NoDetain_Passed2Yrs();
			BigDecimal fullATC = new BigDecimal(77500.00);
			BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
			BigDecimal lastATC = halfATC;
			
			BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
			assertEquals(expectATC, fullATC);
		}
		//2017 full, full,half,full,full
		@Test
		public void testATC_RegDate_detained_in_AnnivDay3() throws ParseException {
			DemandNoteAtcService atcSvc = new DemandNoteAtcService();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date dueDate = sdf.parse("13/06/2021");
			Date regDate = sdf.parse("13/06/2016");
			Date detainDate = sdf.parse("13/06/2019");
			//RegMaster rm = prepare_RegMaster_RegDate_NoDetain_Passed2Yrs();
			BigDecimal fullATC = new BigDecimal(77500.00);
			BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
			BigDecimal lastATC = fullATC;
			
			BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
			assertEquals(expectATC, fullATC);
		}
		
		//2017 full , 2018 full, 2019 half, 2020,full, 2021 full, 2022 half
		@Test
		public void testATC_RegDate_detained_in_AnnivDay4() throws ParseException {
			DemandNoteAtcService atcSvc = new DemandNoteAtcService();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date dueDate = sdf.parse("13/06/2022");
			Date regDate = sdf.parse("13/06/2016");
			Date detainDate = sdf.parse("13/06/2019");
			//RegMaster rm = prepare_RegMaster_RegDate_NoDetain_Passed2Yrs();
			BigDecimal fullATC = new BigDecimal(77500.00);
			BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
			BigDecimal lastATC = halfATC;
			
			BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
			assertEquals(expectATC, halfATC);
		}
	
	
//	@Test
//	public void createaFollowAtcDni() {
//		List<DemandNoteItem> resultList = new ArrayList<>();
//		Map<String, Criteria> map = new HashMap<String, Criteria>();
//		Date dueDate = new Date();
//		DemandNoteItemJpaDao demandNoteItemDao = new DemandNoteItemJpaDao();
//		RegMasterJpaDao  rmDao =new RegMasterJpaDao();
//
//		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
//		Date today0000;
//		try {
//			today0000 = simpleDateFormat.parse(simpleDateFormat.format(new Date()));
//			//today0000 = simpleDateFormat.parse("20191212");
//		} catch (ParseException e) {
//			throw new RuntimeException(e);
//		}
//
//
//		Calendar cal = Calendar.getInstance();
//		cal.setTime(today0000);
//		cal.add(Calendar.DAY_OF_MONTH, -30);
//		Date from = cal.getTime();
//	
//		map.put("createdDateFrom", new Criteria("createdDate", from, Operator.GREATER_OR_EQUAL));
//		map.put("createdDateTo", new Criteria("createdDate", today0000, Operator.LESS_OR_EQUAL));  // 2 days ?
//		map.put("fcFeeCode", new Criteria("fcFeeCode", "01", Operator.EQUALS));  // 2 days ?
//		
//		// find all ATC(A) in last 30 days 
//		System.out.println("map"+map);
//		demandNoteItemDao.findByPaging(map, null, null, resultList);
//		
//		
////		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
////		Date generationTime = new Date();
////		Calendar cal = Calendar.getInstance();
////		cal.setTime(dueDate);
//		int year = cal.get(Calendar.YEAR);
//		for (DemandNoteItem dni : resultList) {
//			
//			boolean discount = dni.getAdhocDemandNoteText().startsWith("50%");
//			RegMaster rm = rmDao.findById(dni.getApplNo());
//			
//			Date detainDate= rmDao.getLaestDetention(rm.getImoNo());
//		
//			if(discount&&detainDate.after(dni.getCreatedDate())) {
//				
//				DemandNoteItem item = new DemandNoteItem();		
//				
//				item.setApplNo(rm.getApplNo());
//				item.setFcFeeCode("111"); 
//				item.setAdhocDemandNoteText( "50% (Year " + (year - 1) + "-" + year + ")"); 
//				
//				List<DemandNoteItem> findByCriteria = demandNoteItemDao.findByCriteria(item);
//				if(findByCriteria.isEmpty()) {				
//				item.setAmount(dni.getAmount());				
//				item.setActive(Boolean.TRUE);
//				item.setChargedUnits(1);
//				item.setChgIndicator("Y");
//				item.setGenerationTime(new Date());
//				item.setUserId("SYSTEM");
//				demandNoteItemDao.save(item);
//				}
//				
////				logger.info("ATC Follow demand note item appl no {},  amount {}",  rm.getApplNo(),  dni.getAmount());
//			}
//			
//		}
//		
//	}
//	
//	
//	@Test
//	public void createaFollowAtcDni2() {
//
//		dns.createFollowAtcItem();
//		
//	}
	
	
	
//	@Test
//	public void testATC_RegDate_DetainInAnnivDay_3Yrs_ReturnFullATC() throws ParseException {
//		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
//		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//		Date dueDate = sdf.parse("13/06/2019");
//		Date regDate = sdf.parse("13/06/2016");
//		Date detainDate =sdf.parse("13/06/2019");
//		//RegMaster rm = prepare_RegMaster_RegDate_NoDetain_Passed2Yrs();
//		BigDecimal fullATC = new BigDecimal(77500.00);
//		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
//		BigDecimal lastATC = halfATC;
//		
//		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
//		assertEquals(expectATC, halfATC);
//	}
//	
//	
//	@Test
//	public void testATC_RegDate_DetainBeforeAnnivDay_3Yrs_ReturnFullATC() throws ParseException {
//		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
//		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//		Date dueDate = sdf.parse("13/06/2019");
//		Date regDate = sdf.parse("13/06/2016");
//		Date detainDate = sdf.parse("13/05/2019");
//		//RegMaster rm = prepare_RegMaster_RegDate_NoDetain_Passed2Yrs();
//		BigDecimal fullATC = new BigDecimal(77500.00);
//		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
//		BigDecimal lastATC = halfATC;
//		
//		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
//		assertEquals(expectATC, fullATC);
//	}
//	
//	@Test
//	public void testATC_RegDate_DetainAfterAnnivDay_3Yrs_ReturnFullATC() throws ParseException {
//		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
//		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//		Date dueDate = sdf.parse("13/06/2019");
//		Date regDate = sdf.parse("13/06/2016");
//		Date detainDate = sdf.parse("13/07/2019");
//		//RegMaster rm = prepare_RegMaster_RegDate_NoDetain_Passed2Yrs();
//		BigDecimal fullATC = new BigDecimal(77500.00);
//		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
//		BigDecimal lastATC = halfATC;
//		
//		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
//		assertEquals(expectATC, halfATC);
//	}
	
	
//	@Test
//	public void testATC_RegDate_DetainAfterAnnivDay_nextYrs_ReturnFullATC() throws ParseException {
//		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
//		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//		Date dueDate = sdf.parse("13/06/2020");
//		Date regDate = sdf.parse("13/06/2016");
//		Date detainDate = sdf.parse("13/07/2019");
//		//RegMaster rm = prepare_RegMaster_RegDate_NoDetain_Passed2Yrs();
//		BigDecimal fullATC = new BigDecimal(77500.00);
//		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
//		BigDecimal lastATC = halfATC;
//		
//		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
//		assertEquals(expectATC, fullATC);
//	}
//	
//	
//	
//
//	@Test
//	public void testATC_RegDate_NoDetain_4Yrs_ReturnFullATC() throws ParseException {
//		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
//		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//		Date dueDate = sdf.parse("13/06/2020");
//		Date regDate = sdf.parse("13/06/2016");
//		Date detainDate = null;
//		//RegMaster rm = prepare_RegMaster_RegDate_NoDetain_Passed2Yrs();
//		BigDecimal fullATC = new BigDecimal(77500.00);
//		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
//		BigDecimal lastATC = halfATC;
//		
//		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
//		assertEquals(expectATC, fullATC);
//	}
//	
//	@Test
//	public void testATC_RegDate_DetainInAnnivDay_4Yrs_ReturnFullATC() throws ParseException {
//		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
//		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//		Date dueDate = sdf.parse("13/06/2020");
//		Date regDate = sdf.parse("13/06/2016");
//		Date detainDate = sdf.parse("13/06/2020");
//		//RegMaster rm = prepare_RegMaster_RegDate_NoDetain_Passed2Yrs();
//		BigDecimal fullATC = new BigDecimal(77500.00);
//		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
//		BigDecimal lastATC = halfATC;
//		
//		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
//		assertEquals(expectATC, fullATC);
//	}
//	
//	
//	@Test
//	public void testATC_RegDate_DetainBeforeAnnivDay_4Yrs_ReturnFullATC() throws ParseException {
//		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
//		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//		Date dueDate = sdf.parse("13/06/2020");
//		Date regDate = sdf.parse("13/06/2016");
//		Date detainDate = sdf.parse("13/05/2020");
//		//RegMaster rm = prepare_RegMaster_RegDate_NoDetain_Passed2Yrs();
//		BigDecimal fullATC = new BigDecimal(77500.00);
//		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
//		BigDecimal lastATC = halfATC;
//		
//		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
//		assertEquals(expectATC, fullATC);
//	}
//	
//	@Test
//	public void testATC_RegDate_DetainAfterAnnivDay_4Yrs_ReturnFullATC() throws ParseException {
//		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
//		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//		Date dueDate = sdf.parse("13/06/2020");
//		Date regDate = sdf.parse("13/06/2016");
//		Date detainDate = sdf.parse("13/07/2020");
//		//RegMaster rm = prepare_RegMaster_RegDate_NoDetain_Passed2Yrs();
//		BigDecimal fullATC = new BigDecimal(77500.00);
//		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
//		BigDecimal lastATC = halfATC;
//		
//		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
//		assertEquals(expectATC, fullATC);
//	}
//	
//	
//
//	// 2017 paid full, 2018 paid full, 2019 pay half
//	@Test
//	public void testATC_DetainDate_AfterAnniversaryDate_Equal2Yrs_ReturnHalfATC() throws ParseException {
//		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
//		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//		//Date currentDate = sdf.parse("20/11/2019");
//		Date dueDate = sdf.parse("13/06/2019");
//		Date regDate = sdf.parse("13/06/2016");	
//		Date detainDate = sdf.parse("13/06/2017");
//		//RegMaster rm = prepare_RegMaster_RegDate_DetainDate_Passed2Yrs();
//		BigDecimal fullATC = new BigDecimal(77500.00);
//		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
//		BigDecimal lastATC = fullATC;
//		
//		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
//		assertTrue(expectATC.equals(fullATC));									
//	}
//
//	// 2018 paid full, 2019 paid full
//	@Test
//	public void testATC_DetainDate_AfterAnniversaryDate_Within2Yrs_ReturnFullATC() throws ParseException {
//		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
//		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//		//Date currentDate = sdf.parse("20/11/2019");
//		Date dueDate = sdf.parse("13/06/2019");
//		Date regDate = sdf.parse("13/06/2016");	
//		Date detainDate = sdf.parse("01/07/2017");
//		//RegMaster rm = prepare_RegMaster_RegDate_DetainDate_Passed2Yrs();
//		BigDecimal fullATC = new BigDecimal(77500.00);
//		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
//		BigDecimal lastATC = fullATC;
//				
//		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
//		assertTrue(expectATC.equals(fullATC));									
//	}
//
//
//	// 2018 paid full, 2019 paid full, 2020 pay half
//	@Test
//	public void testATC_DetainDate_AfterAnniversaryDate_JustAfter2Yrs_ReturnHalfATC() throws ParseException {
//		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
//		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//		//Date currentDate = sdf.parse("20/11/2019");
//		Date dueDate = sdf.parse("13/06/2020");
//		Date regDate = sdf.parse("13/06/2017"); 	
//		Date detainDate = sdf.parse("01/07/2017");
//		//RegMaster rm = prepare_RegMaster_RegDate_DetainDate_Passed2Yrs();
//		BigDecimal fullATC = new BigDecimal(77500.00);
//		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
//		BigDecimal lastATC = fullATC;
//		
//		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
//		assertTrue(expectATC.equals(halfATC));									
//	}
//
//	// 2018 paid full, 2019 paid full, 2020 pay half, 2021 pay full, 2022 pay half
//	// last ATC is full
//	@Test
//	public void testATC_DetainDate_AfterAnniversaryDate_FarAfter2Yrs_ReturnHalfATC() throws ParseException {
//		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
//		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//		//Date currentDate = sdf.parse("20/11/2019");
//		Date dueDate = sdf.parse("13/06/2021");
//		Date regDate = sdf.parse("13/06/2017");	 
//		Date detainDate = sdf.parse("01/07/2017");
//		//RegMaster rm = prepare_RegMaster_RegDate_DetainDate_Passed2Yrs();
//		BigDecimal fullATC = new BigDecimal(77500.00);
//		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
//		BigDecimal lastATC = fullATC;
//		
//		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
//		assertTrue(expectATC.equals(halfATC));									
//	}
//
//	// 2018 paid full, 2019 paid full, 2020 pay half, 2021 pay full
//	// last ATC is half
//	@Test
//	public void testATC_DetainDate_AfterAnniversaryDate_FarAfter2Yrs_ReturnFullATC() throws ParseException {
//		DemandNoteAtcService atcSvc = new DemandNoteAtcService();
//		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//		//Date currentDate = sdf.parse("20/11/2019");
//		Date dueDate = sdf.parse("13/06/2021");
//		Date regDate = sdf.parse("13/06/2017");
//		Date detainDate = sdf.parse("01/07/2017");
//		//RegMaster rm = prepare_RegMaster_RegDate_DetainDate_Passed2Yrs();
//		BigDecimal fullATC = new BigDecimal(77500.00);
//		BigDecimal halfATC = fullATC.multiply(new BigDecimal(0.5));
//		BigDecimal lastATC = halfATC;
//
//		BigDecimal expectATC = atcSvc.calcAtcAmt(regDate, detainDate, dueDate, fullATC, lastATC);
//		assertTrue(expectATC.equals(fullATC));									
//	}
//	


}
