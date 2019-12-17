package org.mardep.ssrs.report.generator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service("RPT_SR_021")
public class RPT_SR_021 extends AbstractSrReport {

	public RPT_SR_021() {
		super("RPT-SR-021.jrxml", null);
	}

	@Override
	public byte[] generate(Map<String, Object> params) throws Exception {
		SimpleDateFormat format = createDateFormat();
		Date from = (Date) params.get("reportFrom");
		Date to = (Date) params.get("reportTo");
		params.put("reportFrom", format.format(from));
		params.put("reportTo", format.format(to));
		List<Map<String, Object>> rows = rmDao.getRegistered(from, to);
		return jasperReportService.generateReport(getReportFileName(), rows, params);
	}
}
