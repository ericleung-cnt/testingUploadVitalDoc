package org.mardep.ssrs.dao.sr;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.sr.ApplDetail;
import org.springframework.stereotype.Repository;

@Repository
public class ApplDetailJpaDao extends AbstractJpaDao<ApplDetail, String> implements IApplDetailDao {
	private List<PredicateCriteria> criteriaList = new ArrayList<>();

	public ApplDetailJpaDao() {
		criteriaList.add(new PredicateCriteria("applNo", PredicateType.EQUAL));
	}
	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(CriteriaBuilder cb, Root<ApplDetail> listRoot) {
		return criteriaList;
	}
}
