package org.mardep.ssrs.report.generator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.report.IReportGenerator;
import org.mardep.ssrs.report.bean.KeyValue;
import org.mardep.ssrs.report.bean.MMO_Distribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.FormatFactory;

/**
 * Distribution of Crew by Nationality by Rank/Rating (mmo7)
 * @author fsqcc
 *
 */
@Service("RPT_MMO_007")
public class MMO_007 extends AbstractKeyValue implements IReportGenerator{

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public String getReportFileName() {
		return "MMO_distribution.jrxml";
	}
	
	@Override
	public byte[] generate(Map<String, Object> inputParam) throws Exception {
		Date reportDate = (Date)inputParam.get("reportDate");
		String reportDateDisplay = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(reportDate);
		logger.info("####### RPT_MMO_007  #########");
		logger.info("Report Date:{}", reportDate);
		
		List<KeyValue> officerList = ratingDao.countRank(reportDate, "O").stream().map(o->new KeyValue((String)o[1], ((Integer)o[2]).toString())).collect(Collectors.toList());
		List<KeyValue> ratingList = ratingDao.countRank(reportDate, "R").stream().map(o->new KeyValue((String)o[1], ((Integer)o[2]).toString())).collect(Collectors.toList());
	
		int officerSum = officerList.stream().mapToInt(kv -> (Integer.valueOf(kv.getValue()))).sum();
		int ratingSum = ratingList.stream().mapToInt(kv -> (Integer.valueOf(kv.getValue()))).sum();

		String reportId = "SRS1100";
		String reportTitle = "Distribution of Crew Nationality of Hong Kong Ships "+reportDateDisplay;
		String currentUser = UserContextThreadLocalHolder.getCurrentUserName();
		
		String summary1 = String.format("TOTAL :       %s", officerSum);
		String summary2 = String.format("TOTAL :       %s", ratingSum);
		List<MMO_Distribution> DistributionList = new ArrayList<MMO_Distribution>();
		// update 2019.06.26, correct alignment of ratingList to rating summary, officerList to officer summary
		DistributionList.add(new MMO_Distribution("OFFICER SUMMARY", officerList,  summary1));
		DistributionList.add(new MMO_Distribution("RATING SUMMARY", ratingList,  summary2));
		
		
		JasperReport kvJR = jasperReportService.getJasperReport("KeyValue.jrxml");
		JasperReport mmoPHJR = jasperReportService.getJasperReport("MMO_PrintHorizontal.jrxml");

		Map<String, Object> map = new HashMap<String, Object>();
		map.put(REPORT_ID, reportId);
		map.put(REPORT_TITLE, reportTitle);
		map.put(USER_ID, currentUser!=null?currentUser:"SYSTEM");
		map.put("SUBREPORT_DIR", "./");
//		map.put(JRParameter.REPORT_FORMAT_FACTORY, "./");
//		map.put(JRParameter.REPORT_LOCALE,  new Locale("en", "US"));
		map.put("printHorizontal", mmoPHJR);
		map.put("keyValue", kvJR);


		return generate(DistributionList, map);
	}
	
}
