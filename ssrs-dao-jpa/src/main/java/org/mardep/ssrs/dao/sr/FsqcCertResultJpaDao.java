package org.mardep.ssrs.dao.sr;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.Criteria;
import org.mardep.ssrs.dao.PagingCriteria;
import org.mardep.ssrs.dao.SortByCriteria;
import org.mardep.ssrs.domain.sr.EtoCoR;
import org.mardep.ssrs.domain.sr.FsqcCertResult;
import org.mardep.ssrs.domain.sr.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public class FsqcCertResultJpaDao extends AbstractJpaDao<FsqcCertResult, Long> implements IFsqcCertResultDao {

	@Override
	public FsqcCertResult findByApplNo(String applNo) {
		// TODO Auto-generated method stub
		try {
			List<FsqcCertResult> entities;
			String sql = "select fcr from FsqcCertResult fcr where fcr.applNo=:applNo";
			Query query = em.createQuery(sql)
					.setParameter("applNo", applNo);
			entities = query.getResultList();
			if (entities.size()>0) {
				return entities.get(0);
			} else {
				return null;
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			return null;
		}
	}
	
	@Override
	public List<FsqcCertResult> findByImo(String imo){
		try {
			List<FsqcCertResult> entities;
			String sql = "select fcr from FsqcCertResult fcr where fcr.imo=:imo";
			Query query = em.createQuery(sql)
					.setParameter("imo", imo);
			entities = query.getResultList();
			return entities;
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			return null;
		}		
	}
}
