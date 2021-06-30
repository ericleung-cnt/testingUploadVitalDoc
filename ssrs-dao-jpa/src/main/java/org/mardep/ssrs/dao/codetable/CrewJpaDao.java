package org.mardep.ssrs.dao.codetable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.codetable.Crew;
import org.mardep.ssrs.domain.codetable.CrewPK;
import org.springframework.stereotype.Repository;

@Repository
public class CrewJpaDao extends AbstractJpaDao<Crew, Integer> implements ICrewDao {



	public CrewJpaDao() {
	}

	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(final CriteriaBuilder cb, final Root<Crew> listRoot) {
		List<PredicateCriteria> list = new ArrayList<PredicateCriteria>();
		list.add(new PredicateCriteria("id", PredicateType.EQUAL));
		list.add(new PredicateCriteria("referenceNo", PredicateType.EQUAL));
		list.add(new PredicateCriteria("imoNo", PredicateType.LIKE_IGNORE_CASE));
//		list.add(new PredicateCriteria("imoNoEqual","imoNo", PredicateType.EQUAL));
		list.add(new PredicateCriteria("serbNo", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("SERB_NO", PredicateType.LIKE_IGNORE_CASE));
//		list.add(new PredicateCriteria("SERB_NOEqual","SERB_NO", PredicateType.EQUAL));
		list.add(new PredicateCriteria("sex", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("nationalitybeforeMap", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("nationalityId", PredicateType.EQUAL));
		list.add(new PredicateCriteria("birthDate", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("birthPlace", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("address", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("nokName", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("nokAddress", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("capacitybeforeMap", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("capacityId", PredicateType.EQUAL));
		list.add(new PredicateCriteria("crewCert", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("currency", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("salary", PredicateType.EQUAL));
		list.add(new PredicateCriteria("status", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("engageDate", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("engagePlace", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("employDate", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("employDuration", PredicateType.EQUAL));
		list.add(new PredicateCriteria("dischargeDate", PredicateType.LIKE_IGNORE_CASE));
		list.add(new PredicateCriteria("dischargePlace", PredicateType.LIKE_IGNORE_CASE));
		listRoot.fetch("nationality", JoinType.LEFT);
		listRoot.fetch("crewListCover", JoinType.LEFT);
		listRoot.fetch("capacity", JoinType.LEFT);
		return list;
//		return super.resolvePredicateCriteriaList(cb, listRoot);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Crew> findByVesselIdCover(String vesselId, String coverYymm) {
		Query q  = em.createQuery("select c from Crew c join fetch c.nationality join fetch c.rank where c.vesselId=:vesselId and c.coverYymm=:coverYymm");
		q.setParameter("vesselId", vesselId);
		q.setParameter("coverYymm", coverYymm);
		return (List<Crew>) q.getResultList();
	}

	@Override
	public List<?> getRankWiseCrewAverageWagesByNationality(Date reportDate, Long rankId) {
		Query q = em.createNativeQuery("select " +
		"r.ENG_DESC rank,  SS_ST_SHIP_TYPE_CODE, N.ENG_DESC nation, avg(C.SALARY) SALARY, count(C.SALARY) CNT_SALARY " +
		"from CREW c " +
		"inner join REG_MASTERS rm on c.VESSEL_ID = rm.APPL_NO " +
		"inner join RANK r on c.CAPACITY_ID = r.CAPACITY_ID " +
		"inner join NATIONALITY N on n.NATIONALITY_ID = c.NATIONALITY_ID " +
		"where :rankId is null or r.CAPACITY_ID = :rankId " +
		"group by SS_ST_SHIP_TYPE_CODE, c.CAPACITY_ID, r.ENG_DESC, N.ENG_DESC ");
		q.setParameter("rankId", rankId);
		return q.getResultList();
	}

}
