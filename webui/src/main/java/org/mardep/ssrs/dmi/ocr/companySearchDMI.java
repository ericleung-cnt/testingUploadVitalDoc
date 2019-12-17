package org.mardep.ssrs.dmi.ocr;

import java.util.List;

import org.mardep.ssrs.domain.ocr.OcrEntityCompanySearch;
import org.mardep.ssrs.service.ICompanySearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSResponse;

@Component
public class companySearchDMI {
	
	@Autowired
	ICompanySearchService companySearchService;
	
	public DSResponse fetch() {
		List<OcrEntityCompanySearch> lst = companySearchService.getAll();
		DSResponse response = new DSResponse(lst);
		return response;
	}
}
