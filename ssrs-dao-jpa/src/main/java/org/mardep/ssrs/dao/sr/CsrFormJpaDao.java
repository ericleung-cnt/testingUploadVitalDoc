package org.mardep.ssrs.dao.sr;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.sr.CsrForm;
import org.mardep.ssrs.domain.sr.CsrFormPK;
import org.springframework.stereotype.Repository;

@Repository
public class CsrFormJpaDao extends AbstractJpaDao<CsrForm, CsrFormPK> implements ICsrFormDao {
	private List<PredicateCriteria> criteriaList = new ArrayList<>();;

	public CsrFormJpaDao() {
		criteriaList.add(new PredicateCriteria("imoNo", PredicateType.EQUAL));
		criteriaList.add(new PredicateCriteria("formSeq", PredicateType.EQUAL));
	}

	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(CriteriaBuilder cb, Root<CsrForm> listRoot) {
		return criteriaList;
	}
}
