package org.mardep.ssrs.dao.sr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.domain.sr.Owner;
import org.mardep.ssrs.domain.sr.OwnerPK;
import org.springframework.stereotype.Repository;

@Repository
public class OwnerJpaDao extends AbstractJpaDao<Owner, OwnerPK> implements IOwnerDao {

	@Override
	public List<Owner> findByImo(String imoNo) {
		Query query = em.createQuery("select owner from Owner owner, RegMaster rm where owner.applNo = rm.applNo and rm.imoNo = :imoNo");
		query.setParameter("imoNo", imoNo);
		return query.getResultList();
	}

	@Override
	public List<Owner> findByApplId(String applNo) {
		Query query = em.createQuery("select owner from Owner owner where owner.applNo = :applNo");
		query.setParameter("applNo", applNo);
		return query.getResultList();
	}

}
