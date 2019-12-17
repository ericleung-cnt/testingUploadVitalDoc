package org.mardep.ssrs.dao.sr;

import java.util.List;

import javax.persistence.Query;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.domain.sr.PreReservedName;
import org.springframework.stereotype.Repository;

@Repository
public class PreReservedNameJpaDao extends AbstractJpaDao<PreReservedName, Long> implements IPreReservedNameDao {

	@Override
	public List<PreReservedName> isReserved(List<String> names, boolean eng) {
		Query query = em.createQuery("SELECT p from PreReservedName p where p.name in (:names) "
				+ "and p.expiryTime > current_date() "
				+ "and p.releaseTime = null "
				+ "and p.language = '" + (eng ? PreReservedName.LANG_EN : PreReservedName.LANG_ZH)
				+ "' "
				+ "");
		query.setParameter("names", names);
		return query.getResultList();
	}

	@Override
	public List<PreReservedName> findPreReserveApps() {
		String query = "select p from PreReservedName p "
				+ "where p.releaseReason = null and p.expiryTime = null";
		return em.createQuery(query).getResultList();

	}

	@Override
	public List<PreReservedName> findNames(String name, String language) {
		String querystr = "select p from PreReservedName p "
				+ "where p.name = :name and p.language = :language ";

		Query query = em.createQuery(querystr);
		query.setParameter("name", name);
		query.setParameter("language", language);

		return query.getResultList();
	}

}
