package org.mardep.ssrs.service;

import java.util.List;

import org.mardep.ssrs.domain.ocr.OcrEntityCompanySearch;

public interface ICompanySearchService {
	List<OcrEntityCompanySearch> getAll();
}
