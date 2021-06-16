package org.mardep.ssrs.report.generator;

import org.springframework.stereotype.Service;

@Service("CSRFormMultiOwners")
public class CSRFormMultiOwnersGenerator extends CSRFormGenerator {

	@Override
	public String getReportFileName() {
		return "CSRFormMultiOwners.jrxml";
	}
}
