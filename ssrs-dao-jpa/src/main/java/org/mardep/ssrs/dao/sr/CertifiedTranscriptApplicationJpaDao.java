package org.mardep.ssrs.dao.sr;

import java.util.List;

import javax.persistence.Query;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.domain.sr.CertifiedTranscriptApplication;
import org.springframework.stereotype.Repository;

@Repository
public class CertifiedTranscriptApplicationJpaDao extends AbstractJpaDao<CertifiedTranscriptApplication, Long> implements ICertifiedTranscriptApplicationDao {

	@Override
	public List<CertifiedTranscriptApplication> getAll() {
		// TODO Auto-generated method stub
		try {
			Query query = em.createQuery("select c from CertifiedTranscriptApplication c order by c.id", 
					CertifiedTranscriptApplication.class);
			List<CertifiedTranscriptApplication> list = (List<CertifiedTranscriptApplication>) query.getResultList();
			return list;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public void remove(int id) {
		// TODO Auto-generated method stub
		
	}

}
