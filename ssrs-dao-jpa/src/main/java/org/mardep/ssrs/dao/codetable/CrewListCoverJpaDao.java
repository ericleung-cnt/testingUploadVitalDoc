package org.mardep.ssrs.dao.codetable;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.codetable.CrewListCover;
import org.mardep.ssrs.domain.codetable.CrewListCover;
import org.mardep.ssrs.domain.codetable.CrewListCoverPK;
import org.springframework.stereotype.Repository;

@Repository
public class CrewListCoverJpaDao extends AbstractJpaDao<CrewListCover, String> implements ICrewListCoverDao {

	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(final CriteriaBuilder cb, final Root<CrewListCover> listRoot) {
		List<PredicateCriteria> list = new ArrayList<PredicateCriteria>();
		list.add(new PredicateCriteria("imoNo", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("shipName", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("regPort", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("offcialNo", PredicateType.LIKE_IGNORE_CASE));

		return list;
	}
	
}