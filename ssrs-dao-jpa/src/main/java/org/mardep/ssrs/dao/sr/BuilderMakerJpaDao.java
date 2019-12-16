package org.mardep.ssrs.dao.sr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.sr.BuilderMaker;
import org.mardep.ssrs.domain.sr.BuilderMakerPK;
import org.mardep.ssrs.domain.sr.Owner;
import org.springframework.stereotype.Repository;

@Repository
public class BuilderMakerJpaDao extends AbstractJpaDao<BuilderMaker, BuilderMakerPK> implements IBuilderMakerDao {
	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(CriteriaBuilder cb, Root<BuilderMaker> listRoot) {
		ArrayList<PredicateCriteria> criteria = new ArrayList<PredicateCriteria>();
		criteria.add(new PredicateCriteria("applNo", PredicateType.EQUAL));
		criteria.add(new PredicateCriteria("builderCode", PredicateType.EQUAL));
		criteria.add(new PredicateCriteria("name", PredicateType.EQUAL));
		return criteria;
	}

	@Override
	public String findNames(String applNo) {

		Query query = em.createNativeQuery("select BUILDER_NAME1 FROM BUILDER_MAKERS where RM_APPL_NO = ?");
		query.setParameter(1, applNo);
		List resultList = query.getResultList();

		StringBuilder buffer = new StringBuilder();
		for (Object row : resultList) {
			String name = (String) row;
			buffer.append(name).append(",");
		}
		if (buffer.length() > 0) {
			return buffer.substring(0, buffer.length() - 1);
		}
		return null;
	}

	@Override
	public List<BuilderMaker> findByApplId(String applNo) {
		Query query = em.createQuery("select builder from BuilderMaker builder where builder.applNo = :applNo");
		query.setParameter("applNo", applNo);
		return query.getResultList();
	}


}
