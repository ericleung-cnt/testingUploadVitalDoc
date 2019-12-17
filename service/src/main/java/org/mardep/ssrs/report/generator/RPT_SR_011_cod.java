package org.mardep.ssrs.report.generator;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.mardep.ssrs.domain.sr.Mortgage;
import org.mardep.ssrs.domain.sr.Owner;
import org.springframework.stereotype.Service;

@Service("CertOfD")
public class RPT_SR_011_cod extends RPT_SR_011 {
	@Override
	public String getReportFileName() {
		return "CoD.jrxml";
	}

	@Override
	protected void processMortgages(String applNo, Date reportDate, boolean printMortgage,
			HashMap<Object, Object> regMaster, List<Owner> owners) {
		if (printMortgage) {
			List<Mortgage> mortgages = mortgageSrv.findMortgages(applNo, reportDate);
			if (mortgages.size()>0) {
				String mortgageAgreement = "";
				for (int i=0; i<mortgages.size(); i++) {
					Mortgage mortgage = mortgages.get(i);
					mortgageAgreement = mortgageAgreement + mortgage.getAgreeTxt() + "\n";
				}
				regMaster.put("mortgageAgreement", mortgageAgreement);
			} else {
				regMaster.put("mortgageAgreement", "");
			}
		}

	}
}
