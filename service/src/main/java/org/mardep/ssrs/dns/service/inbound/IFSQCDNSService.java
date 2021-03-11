package org.mardep.ssrs.dns.service.inbound;

import org.mardep.ssrs.dns.pojo.inbound.createReceipt.ReceiptStatusRequest;
import org.mardep.ssrs.dns.pojo.inbound.createReceipt.ReceiptStatusResponse;
import org.mardep.ssrs.dns.pojo.inbound.updateDemandNoteStatus.DemandNoteStatusRequest;
import org.mardep.ssrs.dns.pojo.inbound.updateDemandNoteStatus.DemandNoteStatusResponse;
import org.mardep.ssrs.dns.pojo.inbound.updateRefundStatus.RefundStatusRequest;
import org.mardep.ssrs.dns.pojo.inbound.updateRefundStatus.RefundStatusResponse;

public interface IFSQCDNSService {
	public RefundStatusResponse processDnsRequest(RefundStatusRequest refundStatusRequest);
	public DemandNoteStatusResponse processDnsRequest(DemandNoteStatusRequest demandNoteStatusRequest);
	public ReceiptStatusResponse processDnsRequest(ReceiptStatusRequest receiptStatusRequest);
}
