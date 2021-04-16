package org.mardep.ssrs.dao.seafarer.impl.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dao.sr.RegMasterJpaDao;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import junit.framework.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:jpaDao-test.xml")
public class CallSignTest {
	
	@Test
	public void testGetNextCallSign() {
		RegMasterJpaDao rmDao = new RegMasterJpaDao();
		List<String> usedCallSignList = prepareCallSignList();
		String nextCallSign = rmDao.nextCallSignStr(usedCallSignList);
		Assert.assertEquals("VRUG9", nextCallSign);
	}
	
	private List<String> prepareCallSignList(){
		List<String> lst = Arrays.asList(
			//"VRUA7", "VRUA8", 
//			"VRUB8",
//			"VRUB9", 
			"VRUG8", "VRUJ7", "VRUL9", "VRUP8",
			"VRUR7", "VRUS5", "VRUT4", "VRUT5", "VRUV2",
			"VRUV6", "VRUV7", "VRUY8", "VRUY9", "VRUZ9",
			"VRVA2", "VRVD9", "VRVE7", "VRVE8", "VRVF4",
			"VRVF6", "VRVH8", "VRVI5", "VRVI6", "VRVI9",
			"VRVJ2", "VRVJ3", "VRVJ7", "VRVJ8", "VRVL4",
			"VRVL8", "VRVM5", "VRVN5", "VRVN8", "VRVP2",
			"VRVQ9", "VRWC8", "VRWE8", "VRWJ8", "VRWL5",
			"VRWM9", "VRWN4", "VRWN7", "VRWN8", "VRWN9",
			"VRWQ2", "VRWQ4", "VRWR5", "VRWR7", "VRWW5",
			"VRXB8", "VRXD8", "VRXE9", "VRXF5", "VRXG2",
			"VRXH5", "VRXH6", "VRXJ4", "VRXL8", "VRXL9",
			"VRXM2", "VRXO6", "VRXP6", "VRXQ5", "VRXR8"
		);
		return lst;
	}
}
