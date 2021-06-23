package org.mardep.ssrs.dao.codetable;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.dao.SortByCriteria;
import org.mardep.ssrs.domain.codetable.Nationality;
import org.mardep.ssrs.domain.codetable.NationalityMapping;
import org.mardep.ssrs.domain.codetable.RankMapping;
import org.springframework.stereotype.Repository;

@Repository
public class RankMappingJpaDao extends AbstractJpaDao<RankMapping, Long> implements IRankMappingDao {

	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(final CriteriaBuilder cb, final Root<RankMapping> listRoot) {
		List<PredicateCriteria> list = new ArrayList<PredicateCriteria>();
		list.add(new PredicateCriteria("ID", PredicateType.EQUAL));
		list.add(new PredicateCriteria("INPUT", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("OUTPUT", PredicateType.LIKE_IGNORE_CASE));
		

		return list;
	}


}
