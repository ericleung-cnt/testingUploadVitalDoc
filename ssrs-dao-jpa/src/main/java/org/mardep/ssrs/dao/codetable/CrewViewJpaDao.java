package org.mardep.ssrs.dao.codetable;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.domain.codetable.CrewPK;
import org.mardep.ssrs.domain.codetable.CrewView;
import org.springframework.stereotype.Repository;

@Repository
public class CrewViewJpaDao extends AbstractJpaDao<CrewView, String> implements ICrewViewDao {

	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(final CriteriaBuilder cb, final Root<CrewView> listRoot) {
//		List<PredicateCriteria> list = new ArrayList<PredicateCriteria>();
//		list.add(new PredicateCriteria("imoNo", PredicateType.LIKE_IGNORE_CASE));
//		list.add(new PredicateCriteria("shipName", PredicateType.LIKE_IGNORE_CASE));
//		list.add(new PredicateCriteria("regPort", PredicateType.LIKE_IGNORE_CASE));
//		list.add(new PredicateCriteria("offcialNo", PredicateType.LIKE_IGNORE_CASE));
//		list.add(new PredicateCriteria("crewName", PredicateType.LIKE_IGNORE_CASE));
//		list.add(new PredicateCriteria("serbNo", PredicateType.LIKE_IGNORE_CASE));
//
//		return list;
		return super.resolvePredicateCriteriaList(cb, listRoot);
	}

//	public CrewViewJpaDao() {
//		
//	}
	
}