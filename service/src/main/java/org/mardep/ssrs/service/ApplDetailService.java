package org.mardep.ssrs.service;

import javax.transaction.Transactional;

import org.mardep.ssrs.dao.sr.IApplDetailDao;
import org.mardep.ssrs.domain.sr.ApplDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ApplDetailService implements IApplDetailService {

	@Autowired
	IApplDetailDao applDetailDao;
	
	@Override
	public ApplDetail save(ApplDetail entity) {
		// TODO Auto-generated method stub
		ApplDetail savedEntity = null;
		savedEntity = applDetailDao.findById(entity.getApplNo());
		//savedEntity.setClassSociety(entity.getClassSociety());
		savedEntity.setCs1ClassSocCode(entity.getCs1ClassSocCode());
		savedEntity.setPrevName(entity.getPrevName());
		savedEntity.setPrevChiName(entity.getPrevChiName());
		savedEntity.setPrevPort(entity.getPrevPort());
		savedEntity.setPreOffNo(entity.getPreOffNo());
		savedEntity.setPrevPortCountry(entity.getPrevPortCountry());
		savedEntity.setAuditInsp(entity.getAuditInsp());
		savedEntity.setProposeRegDate(entity.getProposeRegDate());
		savedEntity.setCcCountryCodePrevReg(entity.getCcCountryCodePrevReg());
		savedEntity.setApplDate(entity.getApplDate());
		savedEntity.setHullNo(entity.getHullNo());
		savedEntity.setCfTime(entity.getCfTime());
		savedEntity.setPrevRegYear(entity.getPrevRegYear());
		savedEntity.setCosInfoState(entity.getCosInfoState());
		savedEntity.setUndertaking(entity.getUndertaking());
		savedEntity.setPrevFlag(entity.getPrevFlag());
		savedEntity.setNextFlag(entity.getNextFlag());
		savedEntity.setClientDeregReason(entity.getClientDeregReason());
		savedEntity.setClientDeregRemark(entity.getClientDeregRemark());
		applDetailDao.save(savedEntity);
		
		return savedEntity;
	}

	
}
