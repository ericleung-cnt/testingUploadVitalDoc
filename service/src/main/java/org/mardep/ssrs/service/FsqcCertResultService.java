package org.mardep.ssrs.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.mardep.ssrs.dao.Criteria;
import org.mardep.ssrs.dao.PagingCriteria;
import org.mardep.ssrs.dao.SortByCriteria;
import org.mardep.ssrs.dao.sr.IFsqcCertResultDao;
import org.mardep.ssrs.domain.AbstractPersistentEntity;
import org.mardep.ssrs.domain.sr.FsqcCertResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class FsqcCertResultService extends AbstractService implements IFsqcCertResultService {

	@Autowired
	IFsqcCertResultDao fsqcCertResultDao;
	
	@Override
	public FsqcCertResult findByApplNo(String applNo) {
		// TODO Auto-generated method stub
		FsqcCertResult entity = fsqcCertResultDao.findByApplNo(applNo);
		return entity;
	}
	
	@Override
	public List<FsqcCertResult> findByImo(String imo) {
		List<FsqcCertResult> entities = fsqcCertResultDao.findByImo(imo);
		return entities;
	}
}
