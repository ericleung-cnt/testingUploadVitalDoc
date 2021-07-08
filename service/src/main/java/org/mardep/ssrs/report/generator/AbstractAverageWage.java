package org.mardep.ssrs.report.generator;

import java.util.HashSet;
import java.util.Set;

import org.mardep.ssrs.dao.codetable.INationalityDao;
import org.mardep.ssrs.dao.seafarer.IRatingDao;
import org.springframework.beans.factory.annotation.Autowired;


public abstract class AbstractAverageWage extends AbstractReportGenerator{
	
	
	public static final String dollorCodeNotFoundErr ="Dollor Code %s Not Found";
	public Set<String> errMsg = new HashSet<>();

	@Autowired
	IRatingDao ratingDao;
	
	@Autowired
	INationalityDao nationalityDao;
	

}
