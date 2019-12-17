package org.mardep.ssrs.service;

import java.util.List;

import javax.transaction.Transactional;

import org.mardep.ssrs.dao.ocr.IOcrTranscriptDao;
import org.mardep.ssrs.dao.sr.ICertifiedTranscriptApplicationDao;
import org.mardep.ssrs.domain.ocr.OcrEntityTranscript;
import org.mardep.ssrs.domain.sr.CertifiedTranscriptApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CertifiedTranscriptApplicationService implements ICertifiedTranscriptApplicationService {

	@Autowired
	ICertifiedTranscriptApplicationDao transcriptDao;
	
	@Override
	public List<CertifiedTranscriptApplication> getAll() {
		// TODO Auto-generated method stub
		return transcriptDao.getAll();
	}

	@Override
	public void remove(int id) {
		// TODO Auto-generated method stub
		transcriptDao.remove(id);
	}

	@Override
	public CertifiedTranscriptApplication save(CertifiedTranscriptApplication entity) {
		// TODO Auto-generated method stub
		//return null;
		return transcriptDao.save(entity);
	}

	@Override
	public CertifiedTranscriptApplication getById(Long id) {
		// TODO Auto-generated method stub
		return transcriptDao.findById(id);
	}

}
