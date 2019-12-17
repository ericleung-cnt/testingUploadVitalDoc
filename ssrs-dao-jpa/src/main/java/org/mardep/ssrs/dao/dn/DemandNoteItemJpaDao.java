package org.mardep.ssrs.dao.dn;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.dn.DemandNoteHeader;
import org.mardep.ssrs.domain.dn.DemandNoteItem;
import org.springframework.stereotype.Repository;

@Repository
public class DemandNoteItemJpaDao extends AbstractJpaDao<DemandNoteItem, Long> implements IDemandNoteItemDao {
	public DemandNoteItemJpaDao() {
		for (PredicateCriteria c : criteriaList) {
			switch (c.getKey()) {
			case "applNo":
			case "fcFeeCode":
				criteriaList.set(criteriaList.indexOf(c), new PredicateCriteria(c.getKey(), PredicateType.EQUAL));
				break;
			}
		}
	}
	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(final CriteriaBuilder cb, final Root<DemandNoteItem> listRoot) {
		listRoot.fetch("feeCode", JoinType.LEFT);
		listRoot.fetch("demandNoteHeader", JoinType.LEFT);
		return criteriaList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DemandNoteItem> findByDemandNoteNo(String demandNoteNo){
		Query q = em.createQuery("SELECT dni from DemandNoteItem dni left join fetch dni.feeCode dnif where dni.dnDemandNoteNo=:demandNoteNo order by dni.itemId");
		q.setParameter("demandNoteNo", demandNoteNo);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DemandNoteItem> findSrDnItems() {
		Query query = em.createQuery("select dni from DemandNoteItem dni where dni.applNo != '0' and dni.active = true ");
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DemandNoteItem> findUnusedByApplNo(String applNo) {
		Query query = em.createQuery("select dni from DemandNoteItem dni where (:applNo is null or dni.applNo = :applNo) and dni.dnDemandNoteNo is null");
		query.setParameter("applNo", applNo);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getOutstandingDn(List<String> imoNoList) {
		String jpel = "select rm.applNo, rm.regName, rm.regChiName, dni.generationTime, rm.atfDueDate, dni.amount, dni.itemId, rm.regNetTon, rm.imoNo "
				+ " from DemandNoteItem dni , RegMaster rm "
				+ "where dni.applNo = rm.applNo and dni.fcFeeCode = '01' and dni.dnDemandNoteNo is null "
				+ "and rm.atfDueDate between :todayNextYear and :tmrNextYear ";

		if (imoNoList == null) {
			jpel += "and (rm.deRegTime is null or rm.deRegTime between :today and :today2359 )";
		} else {
			jpel += "and rm.imoNo in (:imoNoList)";
		}
		Query query = em.createQuery(jpel);
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

		try {
			Date nextYr = format.parse(String.valueOf(Long.parseLong(format.format(new Date())) + 10000));
			query.setParameter("todayNextYear", nextYr);
			query.setParameter("tmrNextYear", new Date(nextYr.getTime() + 24L * 3600*1000));
			if (imoNoList == null) {
				Date today = format.parse(format.format(new Date()));
				query.setParameter("today", today);
				query.setParameter("today2359", new Date(today.getTime() + 24L * 3600*1000 - 1000));
			} else {
				query.setParameter("imoNoList", imoNoList);
			}
		} catch (ParseException e) {
			throw new RuntimeException("cannot get next year");
		}
		return query.getResultList();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DemandNoteItem> findCollected(Date from, Date to){
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT dni from DemandNoteItem dni ");
		sb.append("left join fetch dni.feeCode fc ");
		sb.append("left join fetch dni.demandNoteHeader dnh ");
		sb.append("where dnh.generationTime>=:from and dnh.generationTime<=:to and dnh.paymentStatus!=:paymentStatus");
		Query q = em.createQuery(sb.toString());
		q.setParameter("from", from);
		q.setParameter("to", to);
		q.setParameter("paymentStatus", DemandNoteHeader.PAYMENT_STATUS_OUTSTANDING);
		return q.getResultList();
	}
}
