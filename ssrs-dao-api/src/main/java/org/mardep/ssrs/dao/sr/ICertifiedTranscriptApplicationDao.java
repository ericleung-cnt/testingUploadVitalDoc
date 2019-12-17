package org.mardep.ssrs.dao.sr;

import java.util.List;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.codetable.Office;
import org.mardep.ssrs.domain.ocr.OcrEntityTranscript;
import org.mardep.ssrs.domain.sr.CertifiedTranscriptApplication;

public interface ICertifiedTranscriptApplicationDao extends IBaseDao<CertifiedTranscriptApplication, Long> {
	List<CertifiedTranscriptApplication> getAll();
	void remove(int id);
}
