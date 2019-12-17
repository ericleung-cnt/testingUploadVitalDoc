package org.mardep.ssrs.report.generator;

import org.springframework.stereotype.Service;

@Service("RPT_SR_013")
public class RPT_SR_013 extends AbstractSrReport {

	public RPT_SR_013() {
		super("RPT-SR-013.jrxml", (rmDao, reportDate) -> {
			return rmDao.getShipsByShipTypes(reportDate);
		});

	}
}
