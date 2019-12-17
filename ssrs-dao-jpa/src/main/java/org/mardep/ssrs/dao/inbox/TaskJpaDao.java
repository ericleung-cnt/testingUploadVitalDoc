package org.mardep.ssrs.dao.inbox;

import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.Criteria;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.inbox.Task;
import org.springframework.stereotype.Repository;

@Repository
public class TaskJpaDao extends AbstractJpaDao<Task, Long> implements ITaskDao {

	public TaskJpaDao() {
		criteriaList.add(new PredicateCriteria("actionBy", PredicateType.IS_NULL));
		criteriaList.add(new PredicateCriteria("name", PredicateType.IN));
	}
	@Override
	protected void resolveConditionList(Map<String, Criteria> map, CriteriaBuilder cb,
			List<Predicate> countPredicateList, Root<Task> countRoot, List<Predicate> listPredicateList,
			Root<Task> listRoot) {
		super.resolveConditionList(map, cb, countPredicateList, countRoot, listPredicateList, listRoot);
		countPredicateList.add(cb.isNull(countRoot.get("actionBy")));
		listPredicateList.add(cb.isNull(listRoot.get("actionBy")));
	}

}
