package org.mardep.ssrs.dao.codetable;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.codetable.CrewListCover;
import org.mardep.ssrs.domain.codetable.CrewListCover006;
import org.mardep.ssrs.domain.codetable.CrewListCoverPK;
import org.springframework.stereotype.Repository;

@Repository
public class CrewListCoverJpaDao006 extends AbstractJpaDao<CrewListCover006, String> implements ICrewListCoverDao006 {

	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(final CriteriaBuilder cb, final Root<CrewListCover006> listRoot) {
		List<PredicateCriteria> list = new ArrayList<PredicateCriteria>();
		list.add(new PredicateCriteria("IMO_NO", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("SHIP_NAME", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("REG_PORT", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("OFF_NO", PredicateType.LIKE_IGNORE_CASE));

		return list;
	}
	
}