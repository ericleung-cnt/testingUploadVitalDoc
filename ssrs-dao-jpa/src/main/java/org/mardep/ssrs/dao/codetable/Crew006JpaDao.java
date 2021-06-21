package org.mardep.ssrs.dao.codetable;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.codetable.Crew006;
import org.springframework.stereotype.Repository;

@Repository
public class Crew006JpaDao extends AbstractJpaDao<Crew006, Integer> implements ICrewDao006 {

	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(final CriteriaBuilder cb, final Root<Crew006> listRoot) {
		List<PredicateCriteria> list = new ArrayList<PredicateCriteria>();
		list.add(new PredicateCriteria("ID", PredicateType.EQUAL));
		list.add(new PredicateCriteria("REF_NO", PredicateType.EQUAL));
		list.add(new PredicateCriteria("IMO_NO", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("CREW_NAME", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("SERB_NO", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("SEX", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("NATIONALITY", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("NATIONALITY_ID", PredicateType.EQUAL));
		list.add(new PredicateCriteria("BIRTH_DATE", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("BIRTH_PLACE", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("CREW_ADDRESS", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("NOK_NAME", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("NOK_ADDRESS", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("CAPACITY", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("CAPACITY_ID", PredicateType.EQUAL));
		list.add(new PredicateCriteria("CREW_CERT", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("CURRENCY", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("SALARY", PredicateType.EQUAL));
		list.add(new PredicateCriteria("STATUS", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("ENGAGE_DATE", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("ENGAGE_PLACE", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("EMPLOY_DATE", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("EMPLOY_DURATION", PredicateType.EQUAL));
		list.add(new PredicateCriteria("DISCHARGE_DATE", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("DISCHARGE_PLACE", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("CREATE_BY", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("CREATE_DATE", PredicateType.EQUAL));
		list.add(new PredicateCriteria("LASTUPD_BY", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("LASTUPD_DATE", PredicateType.EQUAL));
		list.add(new PredicateCriteria("ROWVERSION", PredicateType.EQUAL));
		return list;
	}



}
