package org.mardep.ssrs.dao.codetable;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.codetable.DocumentCheckList;
import org.springframework.stereotype.Repository;

@Repository
public class DocumentCheckListJpaDao extends AbstractJpaDao<DocumentCheckList, Long> implements IDocumentCheckListDao {
	
	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(final CriteriaBuilder cb, final Root<DocumentCheckList> listRoot) {
		List<PredicateCriteria> list = new ArrayList<PredicateCriteria>();
		list.add(new PredicateCriteria("id", PredicateType.EQUAL));
		list.add(new PredicateCriteria("type", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("name", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("desc", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("active", PredicateType.EQUAL));

		return list;
	}

}
