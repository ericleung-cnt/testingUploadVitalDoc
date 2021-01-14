package org.mardep.ssrs.report.generator;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JasperReport;

@Service("RPT_SR_018")
public class RPT_SR_018 extends AbstractSrReport {

	public RPT_SR_018() {
		super("RPT-SR-018.jrxml", null);
	}

	@Override
	public byte[] generate(Map<String, Object> inputParam) throws Exception {
		Date reportDate = (Date) inputParam.get("reportDate");
		inputParam.put("reportMonth", new SimpleDateFormat("yyyy-MM").format(reportDate));
		List<List<Map<String, ?>>> ownershipReports = rmDao.getOwnershipReport(reportDate);
		inputParam.put("userId", UserContextThreadLocalHolder.getCurrentUserId());
		DecimalFormat format = new DecimalFormat("00");
		HashMap row = new HashMap();
		for (int i = 0; i < ownershipReports.size(); i++) {
			List<Map<String, ?>> subrpt = (List<Map<String, ?>>) ownershipReports.get(i);
			JasperReport jasperReport = jasperReportService.getJasperReport("RPT-SR-018-subreport"
					+ format.format(i+1)
					+ ".jrxml");
			inputParam.put("SUBREPORT_" + (i+1), jasperReport);
//			if (i==9) {
//				if (ownershipReports.get(i).size()>0) {
//					inputParam.put("showNil10", "false");
//				} else {
//					inputParam.put("showNil10", "true");
//				}
//			}
			setShowNil(i, inputParam, ownershipReports);
			row.put("SUBREPORTDS_" + (i+1), ownershipReports.get(i));
		}

		return jasperReportService.generateReport("RPT-SR-018.jrxml", Arrays.asList(row), inputParam);
	}
	
	private void setShowNil(int i, Map<String, Object> inputParam, List<List<Map<String, ?>>> ownershipReports) {
		switch (i) {
			case 0:
				if (ownershipReports.get(i).size()>0) {
					inputParam.put("showNil01", "false");
				} else {
					inputParam.put("showNil01", "true");
				}
				break;
			case 1:
				if (ownershipReports.get(i).size()>0) {
					inputParam.put("showNil02", "false");
				} else {
					inputParam.put("showNil02", "true");
				}
				break;
			case 2:
				if (ownershipReports.get(i).size()>0) {
					inputParam.put("showNil03", "false");
				} else {
					inputParam.put("showNil03", "true");
				}
				break;
			case 3:
				if (ownershipReports.get(i).size()>0) {
					inputParam.put("showNil04", "false");
				} else {
					inputParam.put("showNil04", "true");
				}
				break;
			case 4:
				if (ownershipReports.get(i).size()>0) {
					inputParam.put("showNil05", "false");
				} else {
					inputParam.put("showNil05", "true");
				}
				break;
			case 5:
				if (ownershipReports.get(i).size()>0) {
					inputParam.put("showNil06", "false");
				} else {
					inputParam.put("showNil06", "true");
				}
				break;
			case 6:
				if (ownershipReports.get(i).size()>0) {
					inputParam.put("showNil07", "false");
				} else {
					inputParam.put("showNil07", "true");
				}
				break;
			case 7:
				if (ownershipReports.get(i).size()>0) {
					inputParam.put("showNil08", "false");
				} else {
					inputParam.put("showNil08", "true");
				}
				break;
			case 8:
				if (ownershipReports.get(i).size()>0) {
					inputParam.put("showNil09", "false");
				} else {
					inputParam.put("showNil09", "true");
				}
				break;
			case 9:
				if (ownershipReports.get(i).size()>0) {
					inputParam.put("showNil10", "false");
				} else {
					inputParam.put("showNil10", "true");
				}
				break;
			case 10:
				if (ownershipReports.get(i).size()>0) {
					inputParam.put("showNil11", "false");
				} else {
					inputParam.put("showNil11", "true");
				}
				break;
		}
			
	}
}
