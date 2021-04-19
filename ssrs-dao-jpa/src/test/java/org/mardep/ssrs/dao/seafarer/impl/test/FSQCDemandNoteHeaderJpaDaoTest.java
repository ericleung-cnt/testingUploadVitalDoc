package org.mardep.ssrs.dao.seafarer.impl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.fsqc.domain.dn.FSQCDemandNoteHeader;
import org.mardep.ssrs.dao.dn.IFSQCDemandNoteHeaderDao;
import org.mardep.ssrs.domain.constant.Cons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:jpaDao-test.xml")
public class FSQCDemandNoteHeaderJpaDaoTest {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private IFSQCDemandNoteHeaderDao headerDao;
	
	
	@Test
	@Transactional
	@Rollback(false)
	public void testCreate() {
		logger.info("#test create..................");
 
		FSQCDemandNoteHeader  header  = new FSQCDemandNoteHeader();
		header.setImono("9676929");
		header.setDemandNoteNo("051700000001192234");
		header.setGenerationTime(new Date());
		header.setAmount(BigDecimal.ONE);
		
		headerDao.save(header);		
	}
	
	@Test
	public void testSelect() {
		logger.info("#test select..................");
		 
		FSQCDemandNoteHeader  header  = new FSQCDemandNoteHeader();
		header.setDemandNoteNo("051900000086216");
		List<FSQCDemandNoteHeader> findByCriteria = headerDao.findByCriteria(header);
		assertEquals(findByCriteria.size(), 1);
		assertEquals(findByCriteria.get(0).getImono().trim(),"9430454");
	}
	
	@Test
	public void testSelect2() {
		logger.info("#test select..................");
		 
		FSQCDemandNoteHeader  header  = new FSQCDemandNoteHeader();
		header.setDemandNoteNo("051900000086216");
		 FSQCDemandNoteHeader findById = headerDao.findById("051900000085219");
		assertNotNull(findById);
	}
	
	
	@Test
	public void testStarts() {
		logger.info("#test select..................");
		
//		FSQCDemandNoteHeader  header  = new FSQCDemandNoteHeader();
//		header.setDemandNoteNo("051900000086216");
//		 FSQCDemandNoteHeader findById = headerDao.findById("051900000085219");
		assertTrue("051900000086216".startsWith(Cons.FSQC_DN_NO_PREFIX));
	}
	
	

	
	
	
	
	
	
	
}
