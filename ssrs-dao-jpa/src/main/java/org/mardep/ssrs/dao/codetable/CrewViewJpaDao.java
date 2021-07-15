package org.mardep.ssrs.dao.codetable;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.codetable.CrewPK;
import org.mardep.ssrs.domain.codetable.CrewView;
import org.springframework.stereotype.Repository;

@Repository
public class CrewViewJpaDao extends AbstractJpaDao<CrewView, String> implements ICrewViewDao {
	
	
	
//	public CrewViewJpaDao() {
//		super();
//		criteriaList.add(new PredicateCriteria("birthDateFrom", "birthDate",PredicateType.GREATER_OR_EQUAL));
//		criteriaList.add(new PredicateCriteria("birthDateTo", "birthDate", PredicateType.LESS_OR_EQUAL));
//		criteriaList.add(new PredicateCriteria("engageDateFrom", "engageDate", PredicateType.GREATER_OR_EQUAL));
//		criteriaList.add(new PredicateCriteria("engageDateTo", 	"engageDate", PredicateType.LESS_OR_EQUAL));
//		criteriaList.add(new PredicateCriteria("employDateFrom", "employDate", PredicateType.GREATER_OR_EQUAL));
//		criteriaList.add(new PredicateCriteria("employDateTo", 	"employDate", PredicateType.LESS_OR_EQUAL));
//		criteriaList.add(new PredicateCriteria("dischargeDateFrom", "dischargeDate", PredicateType.GREATER_OR_EQUAL));
//		criteriaList.add(new PredicateCriteria("dischargeDateTo", 	"dischargeDate", PredicateType.LESS_OR_EQUAL));
//	}


}