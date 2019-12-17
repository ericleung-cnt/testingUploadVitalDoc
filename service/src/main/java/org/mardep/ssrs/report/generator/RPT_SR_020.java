package org.mardep.ssrs.report.generator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service("RPT_SR_020")
public class RPT_SR_020 extends AbstractSrReport {

	public RPT_SR_020() {
		super("RPT-SR-020.jrxml", null);
	}

	@Override
	public byte[] generate(Map<String, Object> params) throws Exception {
		SimpleDateFormat format = createDateFormat();
		Date from = (Date) params.get("reportFrom");
		Date to = (Date) params.get("reportTo");
		Map<String, Object> reportParams = new HashMap<>(params);
		reportParams.put("reportFrom", format.format(from));
		reportParams.put("reportTo", format.format(to));
		List<Map<String, Object>> rows = rmDao.getDeregistered(from, to);
		return jasperReportService.generateReport(getReportFileName(), rows, reportParams);
	}
}
