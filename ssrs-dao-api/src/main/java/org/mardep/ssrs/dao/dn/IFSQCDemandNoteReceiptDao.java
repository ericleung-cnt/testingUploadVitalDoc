package org.mardep.ssrs.dao.dn;

import java.util.Date;
import java.util.List;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.dn.DemandNoteReceipt;
import org.mardep.ssrs.domain.dn.FSQCDemandNoteReceipt;

public interface IFSQCDemandNoteReceiptDao extends IBaseDao<FSQCDemandNoteReceipt, Long> {


	List<FSQCDemandNoteReceipt> findByReceiptNo(String receiptNo);

}
