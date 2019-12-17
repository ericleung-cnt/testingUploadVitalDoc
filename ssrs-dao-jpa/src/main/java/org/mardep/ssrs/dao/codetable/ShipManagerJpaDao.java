package org.mardep.ssrs.dao.codetable;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.codetable.ShipManager;
import org.springframework.stereotype.Repository;

@Repository
public class ShipManagerJpaDao extends AbstractJpaDao<ShipManager, Long> implements IShipManagerDao {

	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(final CriteriaBuilder cb, final Root<ShipManager> listRoot) {
		List<PredicateCriteria> list = new ArrayList<PredicateCriteria>();
		list.add(new PredicateCriteria("id", PredicateType.EQUAL));
		list.add(new PredicateCriteria("shipMgrName", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("addr1", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("addr2", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("addr3", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("companyId", PredicateType.EQUAL));
		list.add(new PredicateCriteria("email", PredicateType.LIKE_IGNORE_CASE));

		return list;
	}
}
