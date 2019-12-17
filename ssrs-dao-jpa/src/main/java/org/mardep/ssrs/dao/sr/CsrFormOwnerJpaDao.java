package org.mardep.ssrs.dao.sr;

import java.util.List;

import javax.persistence.Query;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.domain.sr.CsrFormOwner;
import org.springframework.stereotype.Repository;

@Repository
public class CsrFormOwnerJpaDao extends AbstractJpaDao<CsrFormOwner, Long> implements ICsrFormOwnerDao {

	@Override
	public List<CsrFormOwner> get(String imoNo, Integer formSeq) {
		// TODO Auto-generated method stub
		try {
			Query query = em.createQuery("select o from CsrFormOwner o where o.imoNo=:imoNo and o.formSeq=:formSeq")
					.setParameter("imoNo", imoNo)
					.setParameter("formSeq", formSeq);
			List<CsrFormOwner> lst = (List<CsrFormOwner>) query.getResultList();
			return lst;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

}
