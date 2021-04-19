package org.mardep.ssrs.dao.dn;

import java.util.List;

import javax.persistence.Query;

import org.mardep.fsqc.domain.dn.FSQCDemandNoteReceipt;
import org.mardep.ssrs.dao.AbstractJpaDao_fsqcdb;
import org.springframework.stereotype.Repository;

@Repository
public class FSQCDemandNoteReceiptJpaDao extends AbstractJpaDao_fsqcdb<FSQCDemandNoteReceipt, Long> implements IFSQCDemandNoteReceiptDao {



	@SuppressWarnings("unchecked")
	@Override
	public List<FSQCDemandNoteReceipt> findByReceiptNo(String receiptNo){
		Query q = em.createQuery("SELECT dnr from FSQCDemandNoteReceipt dnr where dnr.receiptNo=:receiptNo order by dnr.receiptId");
		q.setParameter("receiptNo", receiptNo);
		return q.getResultList();
	}



}
