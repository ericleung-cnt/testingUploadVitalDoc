package org.mardep.ssrs.dao.dn;

import java.util.Date;
import java.util.List;

import org.mardep.fsqc.domain.dn.FSQCDemandNoteReceipt;
import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.dn.DemandNoteReceipt;

public interface IFSQCDemandNoteReceiptDao extends IBaseDao<FSQCDemandNoteReceipt, Long> {


	List<FSQCDemandNoteReceipt> findByReceiptNo(String receiptNo);

}
