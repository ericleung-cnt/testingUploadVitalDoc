package org.mardep.ssrs.fsqcService;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.mardep.ssrs.dao.fsqc.IFsqcCertProgressDao;
import org.mardep.ssrs.domain.fsqc.FsqcCertProgress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class FsqcCertProgressService implements IFsqcCertProgressService {

	// private final String CERT_TYPE_BCC = "BCC";
	// private final String CERT_TYPE_MSMC = "MSMC";
	// private final String CERT_TYPE_DMLC_I = "DMLC-I";
	// private final String CERT_TYPE_PRQC = "PRQC";

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	IFsqcCertProgressDao certProgressDao;
	
	@Override
	public List<FsqcCertProgress> get(String imo) throws Exception {
		// TODO Auto-generated method stub
		List<FsqcCertProgress> progressList = new ArrayList<FsqcCertProgress>();
		FsqcCertProgress certBCC = getBCC(imo);
		FsqcCertProgress certMSMC = getMSMC(imo);
		FsqcCertProgress certDMLC = getDMLC_I(imo);
		FsqcCertProgress certPRQC = getPRQC(imo);
		
		if (certBCC != null) progressList.add(certBCC);
		if (certMSMC != null) progressList.add(certMSMC);
		if (certDMLC != null) progressList.add(certDMLC);
		if (certPRQC != null) progressList.add(certPRQC);
		
		return progressList;
	}

	private FsqcCertProgress getBCC(String imo) throws Exception {
		try {
			FsqcCertProgress entity = certProgressDao.get(certProgressDao.getCertTypeNameBcc(), imo);
			return entity;
		} catch (Exception ex) {
			logger.error("Fail to fetch BCC IMO: {}, Exception: {}", imo, ex);
			throw ex;
		}
	}
	
	private FsqcCertProgress getMSMC(String imo) throws Exception {
		try {
			FsqcCertProgress entity = certProgressDao.get(certProgressDao.getCertTypeNameMsmc(), imo);
			return entity;
		} catch (Exception ex) {
			logger.error("Fail to fetch MSMC IMO: {}, Exception: {}", imo, ex);
			throw ex;
		}
	}
	
	private FsqcCertProgress getDMLC_I(String imo) throws Exception {
		try {
			FsqcCertProgress entity = certProgressDao.get(certProgressDao.getCertTypeNameDmlcI(), imo);
			return entity;
		} catch (Exception ex) {
			logger.error("Fail to fetch DMLC-I IMO: {}, Exception: {}", imo, ex);
			throw ex;
		}
	}
	
	private FsqcCertProgress getPRQC(String imo) throws Exception{
		try {
			FsqcCertProgress entity = certProgressDao.get(certProgressDao.getCertTypeNamePrqc(), imo);
			return entity;
		} catch (Exception ex) {
			logger.error("Fail to fetch PRQC IMO: {}, Exception: {}", imo, ex);
			throw ex;
		}
	}
}
