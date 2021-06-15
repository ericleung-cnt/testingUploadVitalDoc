package org.mardep.ssrs.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

//import javax.transaction.Transactional;

import org.mardep.fsqc.domain.dn.FSQCDemandNoteHeader;
import org.mardep.fsqc.domain.dn.FSQCDemandNoteReceipt;
import org.mardep.fsqc.domain.dn.FSQCDemandNoteRefund;
import org.mardep.ssrs.dao.dn.IDemandNoteHeaderDao;
import org.mardep.ssrs.dao.dn.IDemandNoteReceiptDao;
import org.mardep.ssrs.dao.dn.IDemandNoteRefundDao;
import org.mardep.ssrs.dao.dn.IFSQCDemandNoteHeaderDao;
import org.mardep.ssrs.dao.dn.IFSQCDemandNoteReceiptDao;
import org.mardep.ssrs.dao.dn.IFSQCDemandNoteRefundDao;
import org.mardep.ssrs.dns.pojo.BaseReturnResult;
import org.mardep.ssrs.dns.pojo.ResultCode;
import org.mardep.ssrs.dns.pojo.common.CreateReceiptAction;
import org.mardep.ssrs.dns.pojo.common.ReceiptInfo;
import org.mardep.ssrs.dns.pojo.common.RefundAction;
import org.mardep.ssrs.dns.pojo.inbound.createReceipt.ReceiptStatusRequest;
import org.mardep.ssrs.dns.pojo.inbound.createReceipt.ReceiptStatusResponse;
import org.mardep.ssrs.dns.pojo.inbound.createReceipt.ReceiptStatusResult;
import org.mardep.ssrs.dns.pojo.inbound.updateDemandNoteStatus.DemandNoteStatus;
import org.mardep.ssrs.dns.pojo.inbound.updateDemandNoteStatus.DemandNoteStatusRequest;
import org.mardep.ssrs.dns.pojo.inbound.updateDemandNoteStatus.DemandNoteStatusResponse;
import org.mardep.ssrs.dns.pojo.inbound.updateDemandNoteStatus.DemandNoteStatusResult;
import org.mardep.ssrs.dns.pojo.inbound.updateRefundStatus.RefundStatus;
import org.mardep.ssrs.dns.pojo.inbound.updateRefundStatus.RefundStatusRequest;
import org.mardep.ssrs.dns.pojo.inbound.updateRefundStatus.RefundStatusResponse;
import org.mardep.ssrs.dns.pojo.inbound.updateRefundStatus.RefundStatusResult;
import org.mardep.ssrs.dns.pojo.inbound.updateRefundStatus.UpdateRefundStatus;
import org.mardep.ssrs.dns.service.inbound.IFSQCDNSService;
import org.mardep.ssrs.dns.service.inbound.IDnService;
import org.mardep.ssrs.dns.service.inbound.IReceiptService;
import org.mardep.ssrs.dns.service.inbound.IRefundService;
import org.mardep.ssrs.domain.constant.Cons;
import org.mardep.ssrs.domain.constant.DemandNoteRefundStatus;
import org.mardep.ssrs.domain.constant.ReceiptStatus;
import org.mardep.ssrs.domain.dn.DemandNoteHeader;
import org.mardep.ssrs.domain.dn.DemandNoteReceipt;
import org.mardep.ssrs.domain.dn.DemandNoteRefund;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("fsqcDnsInboundService")
@Transactional("transactionManager_fsqcdb")
//@Transactional
public class FSQCDnsInboundService extends DemandNoteService implements IFSQCDNSService {

//	@Autowired
//	IMailService mailService;

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	IFSQCDemandNoteReceiptDao demandNoteReceiptDao;

	@Autowired
	IFSQCDemandNoteRefundDao demandNoteRefundDao;
	
	@Autowired
	IFSQCDemandNoteHeaderDao demandNoteHeaderDao;
	
	

	@Override
	public DemandNoteStatusResponse processDnsRequest(DemandNoteStatusRequest demandNoteStatusRequest) {
		logger.info("#process:{}", demandNoteStatusRequest);
		DemandNoteStatusResponse demandNoteStatusResponse = new DemandNoteStatusResponse();
		DemandNoteStatusResult demandNoteStatusResult = new DemandNoteStatusResult();
		BaseReturnResult brr;
		if (demandNoteStatusRequest.getUpdateDNStatus() == null){
			brr = new BaseReturnResult(ResultCode.RC_98000);
			demandNoteStatusResult.setBaseReturnResult(brr);
			demandNoteStatusResponse.setBaseResult(demandNoteStatusResult);
			logger.info("event={}; resultCode={}; error={};", "Update Demand Note Status", brr.getResultCode().getCode(), brr.getDescription());
			return demandNoteStatusResponse;
		}
		try {
			DemandNoteStatus req = demandNoteStatusRequest.getUpdateDNStatus();
			String demandNoteId = req.getDnNo();
			logger.info("Upload Write-off, DemandNoteID:[{}]", new Object[]{demandNoteId});
			demandNoteStatusResult.setDnNo(demandNoteId);
			if (req.getDnNo() == null){
				brr = new BaseReturnResult(ResultCode.RC_90402);
				demandNoteStatusResult.setBaseReturnResult(brr);
				demandNoteStatusResponse.setBaseResult(demandNoteStatusResult);
				logger.info("event={}; resultCode={}; error={};", "Update Demand Note Status", brr.getResultCode().getCode(), brr.getDescription());
				return demandNoteStatusResponse;
			}
			if (req.getBillCode() == null || !Cons.DNS_BILL_CODE.equals(req.getBillCode())){
				brr = new BaseReturnResult(ResultCode.RC_90403);
				demandNoteStatusResult.setBaseReturnResult(brr);
				demandNoteStatusResponse.setBaseResult(demandNoteStatusResult);
				logger.info("event={}; resultCode={}; error={};", "Update Demand Note Status", brr.getResultCode().getCode(), brr.getDescription());
				return demandNoteStatusResponse;
			}
			if (req.getUserCode() == null){
				brr = new BaseReturnResult(ResultCode.RC_90404);
				demandNoteStatusResult.setBaseReturnResult(brr);
				demandNoteStatusResponse.setBaseResult(demandNoteStatusResult);
				logger.info("event={}; resultCode={}; error={};", "Update Demand Note Status", brr.getResultCode().getCode(), brr.getDescription());
				return demandNoteStatusResponse;
			}
			if (req.getOfficeCode() == null){
				brr = new BaseReturnResult(ResultCode.RC_90405);
				demandNoteStatusResult.setBaseReturnResult(brr);
				demandNoteStatusResponse.setBaseResult(demandNoteStatusResult);
				logger.info("event={}; resultCode={}; error={};", "Update Demand Note Status", brr.getResultCode().getCode(), brr.getDescription());
				return demandNoteStatusResponse;
			}
			FSQCDemandNoteHeader demandNoteHeader =demandNoteHeaderDao.findById(demandNoteId);
			if (demandNoteHeader == null){
				brr = new BaseReturnResult(ResultCode.RC_00401);
				demandNoteStatusResult.setBaseReturnResult(brr);
				demandNoteStatusResponse.setBaseResult(demandNoteStatusResult);
				logger.info("event={}; resultCode={}; error={};", "Update Demand Note Status", brr.getResultCode().getCode(), brr.getDescription());
				return demandNoteStatusResponse;
			}else if(demandNoteHeader.getCwStatus()!=null){
				logger.warn("current CW status:{}", demandNoteHeader.getCwStatus());
				brr = new BaseReturnResult(ResultCode.RC_00401);
				demandNoteStatusResult.setBaseReturnResult(brr);
				demandNoteStatusResponse.setBaseResult(demandNoteStatusResult);
				logger.info("event={}; resultCode={}; error={};", "Update Demand Note Status", brr.getResultCode().getCode(), brr.getDescription());
				return demandNoteStatusResponse;
			}


			demandNoteStatusResult.setDnNo(demandNoteId);
			if (req.getDnStatus()!= 11) {//11 = Write-off, from 4.2.2	Update Demand Note Record Status
				//TODO return error
				brr = new BaseReturnResult(ResultCode.RC_00000);
				demandNoteStatusResult.setBaseReturnResult(brr);
				demandNoteStatusResponse.setBaseResult(demandNoteStatusResult);
				return demandNoteStatusResponse;
			}

			demandNoteHeader.setCwStatus(Cons.CW_STATUS_W);
			demandNoteHeader.setCwBy(UserContextThreadLocalHolder.getCurrentUserName());
			demandNoteHeader.setCwTime(req.getWriteOffDate());
			demandNoteHeader.setCwRemark(req.getRemark());
			demandNoteHeader.setStatus(DemandNoteHeader.STATUS_WRITTEN_OFF);
			demandNoteHeader = demandNoteHeaderDao.save(demandNoteHeader);				
			
			//TODO
//			EmailTemplate dnsEmailTemplate = applicationContext.getBean("dnsEmailTemplate", EmailTemplate.class);
//			Map<String, String> params = new HashMap<String, String>();
//			String content = "Demand Note " + demandNoteId + " is written off from DNS";
//			params.put("subject", content);
//			params.put("content", content);
//			dnsEmailTemplate.setParam(params);
//			Map<String, byte[]> attachments = new HashMap<String, byte[]>();
//			mailService.send(dnsEmailTemplate, attachments);

			brr = new BaseReturnResult(ResultCode.RC_00000);
			demandNoteStatusResult.setBaseReturnResult(brr);
			demandNoteStatusResponse.setBaseResult(demandNoteStatusResult);
			return demandNoteStatusResponse;
		} catch (Exception e) {
			logger.error("fail to process refund request", e);
			brr = new BaseReturnResult(ResultCode.RC_99000);
			demandNoteStatusResult.setBaseReturnResult(brr);
			demandNoteStatusResponse.setBaseResult(demandNoteStatusResult);
		}
		return demandNoteStatusResponse;
	}

	/**
	 * process upload receipt and cancel receipt from DNS
	 *
	 */
	@Override
	public ReceiptStatusResponse processDnsRequest(ReceiptStatusRequest receiptStatusRequest){
		logger.info("#process:{}", receiptStatusRequest);
		ReceiptStatusResponse receiptStatusResponse = new ReceiptStatusResponse();
		ReceiptStatusResult receiptStatusResult = new ReceiptStatusResult();
		BaseReturnResult brr;
		if (receiptStatusRequest.getCreateReceipt().getReceipt() == null){
			brr = new BaseReturnResult(ResultCode.RC_98000);
			receiptStatusResult.setBaseReturnResult(brr);
			receiptStatusResponse.setBaseResult(receiptStatusResult);
			logger.info("event={}; resultCode={}; error={};", "Create/Cancel Receipt", brr.getResultCode().getCode(), brr.getDescription());
			return receiptStatusResponse;
		}
		try {
			ReceiptInfo req = receiptStatusRequest.getCreateReceipt().getReceipt();
			String dnNo = req.getDnNo();
			String receiptNo = req.getReceiptNo();
			logger.info("Upload/Cancel Receipt, DemandNoteID:[{}], ReceiptNO.:[{}]", new Object[]{dnNo, receiptNo});
			receiptStatusResult.setDnNo(dnNo);
			receiptStatusResult.setReceiptNo(receiptNo);

			if (dnNo == null || dnNo.trim().length()==0){
				brr = new BaseReturnResult(ResultCode.RC_90603);
				receiptStatusResult.setBaseReturnResult(brr);
				receiptStatusResponse.setBaseResult(receiptStatusResult);
				logger.info("event={}; resultCode={}; error={};", "Create Receipt", brr.getResultCode().getCode(), brr.getDescription());
				return receiptStatusResponse;
			}
			if (receiptNo == null || receiptNo.trim().length()==0){
				brr = new BaseReturnResult(ResultCode.RC_90601);
				receiptStatusResult.setBaseReturnResult(brr);
				receiptStatusResponse.setBaseResult(receiptStatusResult);
				logger.info("event={}; resultCode={}; error={};", "Create/Cancel Receipt", brr.getResultCode().getCode(), brr.getDescription());
				return receiptStatusResponse;
			}
			if (req.getReceiptDate() == null){
				brr = new BaseReturnResult(ResultCode.RC_90602);
				receiptStatusResult.setBaseReturnResult(brr);
				receiptStatusResponse.setBaseResult(receiptStatusResult);
				logger.info("event={}; resultCode={}; error={};", "Create/Cancel Receipt", brr.getResultCode().getCode(), brr.getDescription());
				return receiptStatusResponse;
			}
			if (req.getMachineID() == null){
				brr = new BaseReturnResult(ResultCode.RC_90604);
				receiptStatusResult.setBaseReturnResult(brr);
				receiptStatusResponse.setBaseResult(receiptStatusResult);
				logger.info("event={}; resultCode={}; error={};", "Create/Cancel Receipt", brr.getResultCode().getCode(), brr.getDescription());
				return receiptStatusResponse;
			}
			if (req.getReceiptAmount() == null){
				brr = new BaseReturnResult(ResultCode.RC_90606);
				receiptStatusResult.setBaseReturnResult(brr);
				receiptStatusResponse.setBaseResult(receiptStatusResult);
				logger.info("event={}; resultCode={}; error={};", "Create/Cancel Receipt", brr.getResultCode().getCode(), brr.getDescription());
				return receiptStatusResponse;
			}
			if (receiptStatusRequest.getCreateReceipt().getAction() == null){
				brr = new BaseReturnResult(ResultCode.RC_90607);
				receiptStatusResult.setBaseReturnResult(brr);
				receiptStatusResponse.setBaseResult(receiptStatusResult);
				logger.info("event={}; resultCode={}; error={};", "Create/Cancel Receipt", brr.getResultCode().getCode(), brr.getDescription());
				return receiptStatusResponse;
			}
			FSQCDemandNoteHeader demandNoteHeader = demandNoteHeaderDao.findById(dnNo);
			if (CreateReceiptAction.U.equals(receiptStatusRequest.getCreateReceipt().getAction())){
				//U for new Receipt
				List<FSQCDemandNoteReceipt> rList = demandNoteReceiptDao.findByReceiptNo(receiptNo);
				if (rList.size() > 0){
					brr = new BaseReturnResult(ResultCode.RC_00602);
					receiptStatusResult.setBaseReturnResult(brr);
					receiptStatusResponse.setBaseResult(receiptStatusResult);
					logger.info("event={}; resultCode={}; error={};", "Create Receipt", brr.getResultCode().getCode(), brr.getDescription());
					return receiptStatusResponse;
				}

				Date receiptDate = req.getReceiptDate();
				String receiptStatus = ReceiptStatus.INSERT.getCode();
				boolean futureReceipt = false;
				if (IReceiptService.PENDING_REF == receiptStatusRequest.getCreateReceipt().getReceipt().getPaymentRef()){
					receiptStatusRequest.getCreateReceipt().getReceipt().setPaymentRef("");
					futureReceipt = true;
					receiptStatus = ReceiptStatus.PENDING.getCode();
				}

				FSQCDemandNoteReceipt demandNoteReceipt = new FSQCDemandNoteReceipt();
				demandNoteReceipt.setDemandNoteNo(dnNo);
				demandNoteReceipt.setReceiptNo(req.getReceiptNo());
				demandNoteReceipt.setStatus(receiptStatus);
				demandNoteReceipt.setInputTime(receiptDate);
				demandNoteReceipt.setAmount(req.getReceiptAmount());
				demandNoteReceipt.setRemark(req.getPaymentRef());
				demandNoteReceipt.setMachineCode(req.getMachineID());	// 2019.10.18 add adding of machine code

				if (req.getPaymentList()!=null && req.getPaymentList().size() > 0){
					if (req.getPaymentList().get(0).getPaymentType() == null){
						brr = new BaseReturnResult(ResultCode.RC_90605);
						receiptStatusResult.setBaseReturnResult(brr);
						receiptStatusResponse.setBaseResult(receiptStatusResult);
						logger.info("event={}; resultCode={}; error={};", "Create Receipt", brr.getResultCode().getCode(), brr.getDescription());
						return receiptStatusResponse;
					}
					demandNoteReceipt.setPaymentType(req.getPaymentList().get(0).getPaymentType());
				}
				if (req.getChargeList()!=null && req.getChargeList().size() > 0){
					demandNoteReceipt.setAnnualFeeCode(req.getChargeList().get(0).getRevenueType());
				}
				demandNoteReceiptDao.save(demandNoteReceipt);


				if (demandNoteHeader.getAmountPaid() == null){
					demandNoteHeader.setAmountPaid(BigDecimal.ZERO);
				}
				BigDecimal receiptAmount = demandNoteReceipt.getAmount();
				BigDecimal demandNoteAmount = demandNoteHeader.getAmount();
				BigDecimal totalPaid = receiptAmount.add(demandNoteHeader.getAmountPaid());
				if(!futureReceipt){
					// no need to update demand note paid amount if receipt value date > current date;
					demandNoteHeader.setAmountPaid(totalPaid);
				}
				demandNoteHeader = demandNoteHeaderDao.save(demandNoteHeader);

				if (receiptAmount.compareTo(demandNoteAmount) > 0){
					//TODO Overpaid
					logger.warn("It is Overpaid receipt!");
//					EmailTemplate dnsEmailTemplate = applicationContext.getBean("dnsEmailTemplate", EmailTemplate.class);
//					Map<String, String> params = new HashMap<String, String>();
//					String content = "Overpaid Receipt " + demandNoteReceipt.getReceiptNo() + " of Demand Note " + demandNoteReceipt.getDemandNoteId() + " is created from DNS";
//					params.put("subject", content);
//					params.put("content", content);
//					dnsEmailTemplate.setParam(params);
//					Map<String, byte[]> attachments = new HashMap<String, byte[]>();
//					mailService.send(dnsEmailTemplate, attachments);
				} else if (receiptAmount.compareTo(demandNoteAmount) < 0){
					logger.warn("It is Partial-paid receipt!");
//					Partial-paid
//					EmailTemplate dnsEmailTemplate = applicationContext.getBean("dnsEmailTemplate", EmailTemplate.class);
//					Map<String, String> params = new HashMap<String, String>();
//					String content = "Partial-paid Receipt " + demandNoteReceipt.getReceiptNo() + " of Demand Note " + demandNoteReceipt.getDemandNoteId() + " is created from DNS";
//					params.put("subject", content);
//					params.put("content", content);
//					dnsEmailTemplate.setParam(params);
//					Map<String, byte[]> attachments = new HashMap<String, byte[]>();
//					mailService.send(dnsEmailTemplate, attachments);
				} else {
					if (totalPaid.compareTo(demandNoteAmount) == 1){
						logger.warn("The DemandNote becomes Overpaid!");
//						EmailTemplate dnsEmailTemplate = applicationContext.getBean("dnsEmailTemplate", EmailTemplate.class);
//						Map<String, String> params = new HashMap<String, String>();
//						String content = "Double-paid Receipt " + demandNoteReceipt.getReceiptNo() + " of Demand Note " + demandNoteReceipt.getDemandNoteId() + " is created from DNS";
//						params.put("subject", content);
//						params.put("content", content);
//						dnsEmailTemplate.setParam(params);
//						Map<String, byte[]> attachments = new HashMap<String, byte[]>();
//						mailService.send(dnsEmailTemplate, attachments);
					} else if (totalPaid.compareTo(demandNoteAmount) == -1){
						logger.warn("The DemandNote still Partial-Paid!");
//						EmailTemplate dnsEmailTemplate = applicationContext.getBean("dnsEmailTemplate", EmailTemplate.class);
//						Map<String, String> params = new HashMap<String, String>();
//						String content = "Partial-paid Receipt " + demandNoteReceipt.getReceiptNo() + " of Demand Note " + demandNoteReceipt.getDemandNoteId() + " is created from DNS";
//						params.put("subject", content);
//						params.put("content", content);
//						dnsEmailTemplate.setParam(params);
//						Map<String, byte[]> attachments = new HashMap<String, byte[]>();
//						mailService.send(dnsEmailTemplate, attachments);
					}
				}

				if (demandNoteHeader.getCwStatus() != null){
					logger.warn("upload Receipt, but the DemandNote is Cancelled or Write-off!");
					brr = new BaseReturnResult(ResultCode.RC_90608);
					receiptStatusResult.setBaseReturnResult(brr);
					receiptStatusResponse.setBaseResult(receiptStatusResult);
					logger.info("event={}; resultCode={}; error={};", "Create Receipt", brr.getResultCode().getCode(), brr.getDescription());
					updatePaymentStatus(demandNoteHeader);

					return receiptStatusResponse;
				}
				brr = new BaseReturnResult(ResultCode.RC_00000);
				receiptStatusResult.setBaseReturnResult(brr);
				receiptStatusResponse.setBaseResult(receiptStatusResult);

			} else if (CreateReceiptAction.C.equals(receiptStatusRequest.getCreateReceipt().getAction())){
				//C for cancel Receipt
				List<FSQCDemandNoteReceipt> rList = demandNoteReceiptDao.findByReceiptNo(receiptNo);
				FSQCDemandNoteReceipt r = null;
				for (FSQCDemandNoteReceipt rr : rList){
					if (dnNo.equals(rr.getDemandNoteNo())){
						r = rr;
						break;
					}
				}
				if (r == null || rList.size() <= 0){
					brr = new BaseReturnResult(ResultCode.RC_90601);
					receiptStatusResult.setBaseReturnResult(brr);
					receiptStatusResponse.setBaseResult(receiptStatusResult);
					logger.info("event={}; resultCode={}; error={};", "Cancel Receipt", brr.getResultCode().getCode(), brr.getDescription());
					return receiptStatusResponse;
				}


				r.setStatus(ReceiptStatus.CANCELLED.getCode());
				r.setCancelDate(new Date());
				r.setCancelById("SYSTEM");
				r.setCanAdjStatus("Y");
				r.setCanAdjTime(r.getCancelDate());
				r.setCanAdjBy(r.getCancelById());
				r.setCanAdjRemark(req.getPaymentRef());
				r.setRemark(req.getPaymentRef());
				demandNoteReceiptDao.save(r);

				BigDecimal receiptAmount = r.getAmount();
				BigDecimal dnPaidAmount = demandNoteHeader.getAmountPaid();
				BigDecimal totalPaid = dnPaidAmount.subtract(receiptAmount);
				demandNoteHeader.setAmountPaid(totalPaid);
				demandNoteHeader = demandNoteHeaderDao.save(demandNoteHeader);
				logger.info("Cancel Receipt, the DemandNote PaidAmount changed from [{}] to [{}]", new Object[]{dnPaidAmount, totalPaid});

				brr = new BaseReturnResult(ResultCode.RC_00000);
				receiptStatusResult.setBaseReturnResult(brr);
				receiptStatusResponse.setBaseResult(receiptStatusResult);


//				EmailTemplate dnsEmailTemplate = applicationContext.getBean("dnsEmailTemplate", EmailTemplate.class);
//				Map<String, String> params = new HashMap<String, String>();
//				String content = "Receipt " + r.getReceiptNo() + " of Demand Note " + r.getDemandNoteId() + " is cancelled from DNS";
//				params.put("subject", content);
//				params.put("content", content);
//				dnsEmailTemplate.setParam(params);
//				Map<String, byte[]> attachments = new HashMap<String, byte[]>();
//				mailService.send(dnsEmailTemplate, attachments);

			}
			receiptStatusResponse.setBaseResult(receiptStatusResult);
			demandNoteHeader = updatePaymentStatus(demandNoteHeader);
			return receiptStatusResponse;
		} catch (Exception e) {
			logger.error("fail to process receipt request", e);
			brr = new BaseReturnResult(ResultCode.RC_99000);
			receiptStatusResult.setBaseReturnResult(brr);
			receiptStatusResponse.setBaseResult(receiptStatusResult);
			return receiptStatusResponse;
		}
	}

	public FSQCDemandNoteHeader updatePaymentStatus(FSQCDemandNoteHeader demandNoteHeader) {
		FSQCDemandNoteReceipt criteria = new FSQCDemandNoteReceipt();
		criteria.setDemandNoteNo(demandNoteHeader.getDemandNoteNo());
		List<FSQCDemandNoteReceipt> receipts = demandNoteReceiptDao.findByCriteria(criteria);
		FSQCDemandNoteRefund refundCriteria = new FSQCDemandNoteRefund();
		refundCriteria.setDemandNoteNo(demandNoteHeader.getDemandNoteNo());
		List<FSQCDemandNoteRefund> refunds = demandNoteRefundDao.findByCriteria(refundCriteria);
		processDnStates(demandNoteHeader, receipts, refunds);
		logger.info("update payment status demandNote {} payment status {} amount paid {}  status {}", demandNoteHeader.getId(), demandNoteHeader.getPaymentStatus(), demandNoteHeader.getAmountPaid(), demandNoteHeader.getStatus());
		demandNoteHeader.setUpdatedBy("DNS");
		return demandNoteHeaderDao.save(demandNoteHeader);
	}
	
	public static String processDnStates(FSQCDemandNoteHeader demandNoteHeader, List<FSQCDemandNoteReceipt> receipts, List<FSQCDemandNoteRefund> refunds) {
		BigDecimal amountPaid = BigDecimal.ZERO;
		BigDecimal amountRefuned = BigDecimal.ZERO;
		boolean receiptCancelled = false;
		boolean hasRefund = false;
		for (FSQCDemandNoteReceipt receipt : receipts) {
			if (!ReceiptStatus.PENDING.getCode().equals(receipt.getStatus())) {
				if (receipt.getCanAdjStatus() == null) {
					amountPaid = amountPaid.add(receipt.getAmount());
				} else {
					receiptCancelled = true;
				}
			}
		}
		for (FSQCDemandNoteRefund refund : refunds) {
			if (DemandNoteRefundStatus.APPROVED.toString().equals(refund.getRefundStatus())) {
				hasRefund = true;
				amountRefuned = amountRefuned.add(refund.getRefundAmount());
				amountPaid = amountPaid.subtract(refund.getRefundAmount());
			}
		}
		BigDecimal headerAmt = demandNoteHeader.getAmount();
		if (headerAmt == null) {
			headerAmt = BigDecimal.ZERO;
		}
		demandNoteHeader.setAmountPaid(amountPaid);
		if (!receiptCancelled && amountRefuned.compareTo(amountPaid)>=0) {
			demandNoteHeader.setStatus(DemandNoteHeader.STATUS_REFUNDED);
		}

//		if (receipt amount + refund amount == 0 ) {
//		    if (has cancel receipt or ebs flag != "1")
//		    then "outstanding"
//		    else "auto pay arrange"
//		} else if (amount - receipt amount + refund amount < 0) {
//		    "overpaid"
//		} else {
//		    if (amount - receipt amount + refund amount > 0) {
//		    then "partial"
//		    else "paid"
//		}
		if (amountPaid.doubleValue() == 0 ) {
		    if (receiptCancelled || !"1".equals(demandNoteHeader.getEbsFlag())) {
		    	demandNoteHeader.setPaymentStatus(DemandNoteHeader.PAYMENT_STATUS_OUTSTANDING);
		    } else {
		    	demandNoteHeader.setPaymentStatus(DemandNoteHeader.PAYMENT_STATUS_AUTOPAY_ARRANGED);
		    }
		} else if (headerAmt.subtract(amountPaid).doubleValue() < 0) {
	    	demandNoteHeader.setPaymentStatus(DemandNoteHeader.PAYMENT_STATUS_OVERPAID);
		} else {
			if (headerAmt.subtract(amountPaid).doubleValue() > 0) {
		    	demandNoteHeader.setPaymentStatus(DemandNoteHeader.PAYMENT_STATUS_PARTIAL);
			} else {
		    	demandNoteHeader.setPaymentStatus(DemandNoteHeader.PAYMENT_STATUS_PAID);
			}
		}

		return demandNoteHeader.getPaymentStatus();
	}

	/**
	 * process refund request from DNS
	 *
	 */
	@Override
	public RefundStatusResponse processDnsRequest(RefundStatusRequest refundStatusRequest){
		logger.info("RefundService#process:{}", refundStatusRequest);
		UpdateRefundStatus urs = refundStatusRequest.getUpdateRefundStatus();
		RefundStatus req = urs.getRefund();
		if (req == null){
			return refundResult("", ResultCode.RC_98000);
		}
		String demandNoteId = req.getDnNo();
		try {
			logger.info("Refund, DemandNoteID:[{}]", new Object[]{demandNoteId});
			if (demandNoteId == null || demandNoteId.isEmpty()){
				return refundResult(demandNoteId, ResultCode.RC_90502);
			}
			if (req.getBillCode() == null || ! Cons.DNS_BILL_CODE.equals(req.getBillCode())){
				return refundResult(demandNoteId, ResultCode.RC_90503);
			}
			if (req.getUserCode() == null){
				return refundResult(demandNoteId, ResultCode.RC_90504);
			}
			if (req.getOfficeCode() == null){
				return refundResult(demandNoteId, ResultCode.RC_90505);
			}

			FSQCDemandNoteHeader dnHeader = demandNoteHeaderDao.findById(demandNoteId);
			if (dnHeader == null){
				return refundResult(demandNoteId, ResultCode.RC_00501);
			}

			if (RefundAction.A.equals(urs.getAction())){
				// TODO
//				EmailTemplate dnsEmailTemplate = applicationContext.getBean("dnsEmailTemplate", EmailTemplate.class);
//				Map<String, String> params = new HashMap<String, String>();
//				String content = "Receipt " + receipt.getReceiptNo() + " of Demand Note " + demandNoteId + " is refunded from DNS";
//				params.put("subject", content);
//				params.put("content", content);
//				dnsEmailTemplate.setParam(params);
//				Map<String, byte[]> attachments = new HashMap<String, byte[]>();
//				mailService.send(dnsEmailTemplate, attachments);

				// commented and refactored on 2019.09.29
//				DemandNoteRefund refund = null;
//				String refId = req.getRefId();
//				if (refId != null && refId.matches("\\d\\d*")) {
//					refund = refundDao.findById(Long.parseLong(refId));
//				}
//				if (refund == null) {
//					refund = new DemandNoteRefund();
//				}
//				refund.setStatus(String.valueOf(req.getDnStatus()));
//				refund.setRefundAmount(req.getReFundAmount());
//				refund.setVoucherNo(req.getRefundVouchorNo());
//				refund.setRemarks(req.getRemark());
//				refund.setRepayDate(req.getRePayDate());
//				refund.setVoucherDate(req.getReVouDate());
//				refund.setUserCode(req.getUserCode());
//				refund.setDemandNoteNo(demandNoteId);
//				refund = refundDao.save(refund);
				handleApprovedRefundRequest(req);
			} else if (RefundAction.R.equals(urs.getAction())) {
				handleRejectedRefundRequest(req);
			}else{
//					TODO how about RefundAction!=A ?
				logger.warn("TODO! Refund action {}", urs.getAction()); //TODO
			}
			updatePaymentStatus(dnHeader);
			return refundResult(demandNoteId, ResultCode.RC_00000);
		} catch (Exception e) {
			logger.error("fail to process refund request", e);
			return refundResult(demandNoteId, ResultCode.RC_99000);
		}
	}

	private RefundStatusResponse refundResult(String demandNoteId, ResultCode code) {
		RefundStatusResponse refundStatusResponse = new RefundStatusResponse();
		RefundStatusResult refundStatusResult = new RefundStatusResult();
		BaseReturnResult brr = new BaseReturnResult(code);
		refundStatusResult.setBaseReturnResult(brr);
		refundStatusResult.setDnNo(demandNoteId);
		refundStatusResponse.setBaseResult(refundStatusResult);
		logger.info("event={}; resultCode={}; error={};", "Refund Receipt", brr.getResultCode().getCode(), brr.getDescription());
		return refundStatusResponse;
	}

	public void handleApprovedRefundRequest(RefundStatus updateRefundObj) {
		User user = new User();
		user.setId("DNS");
		UserContextThreadLocalHolder.setCurrentUser(user);

		FSQCDemandNoteRefund refund = null;
		String refId = updateRefundObj.getRefId();
		String demandNoteId = updateRefundObj.getDnNo();
		if (demandNoteId != null) {
			demandNoteId = demandNoteId.trim();
		}

		if (refId != null && refId.matches("\\d\\d*")) {
			refund = demandNoteRefundDao.findById(Long.parseLong(refId));
		}
		if (refund == null) {
			refund = new FSQCDemandNoteRefund();
			refund.setRemarks("Refund trigger by Finance");
			refund.setRefundStatus(DemandNoteRefundStatus.APPROVED.toString());
			refund.setRefundAmount(updateRefundObj.getReFundAmount());
			refund.setVoucherNo(updateRefundObj.getRefundVouchorNo());
			refund.setDnsRemark(updateRefundObj.getRemark());
			refund.setRepayDate(updateRefundObj.getRePayDate());
			refund.setVoucherDate(updateRefundObj.getReVouDate());
			refund.setUserCode(updateRefundObj.getUserCode());
			refund.setDemandNoteNo(demandNoteId);
			refund = demandNoteRefundDao.save(refund);
		} else {
			//refund.setStatus(String.valueOf(updateRefundObj.getDnStatus()));
			refund.setRefundStatus(DemandNoteRefundStatus.APPROVED.toString());
			refund.setRefundAmount(updateRefundObj.getReFundAmount());
			refund.setVoucherNo(updateRefundObj.getRefundVouchorNo());
			refund.setDnsRemark(updateRefundObj.getRemark());
			refund.setRepayDate(updateRefundObj.getRePayDate());
			refund.setVoucherDate(updateRefundObj.getReVouDate());
			refund.setUserCode(updateRefundObj.getUserCode());
			refund.setDemandNoteNo(demandNoteId);
			refund = demandNoteRefundDao.save(refund);
		}
	}

	public void handleRejectedRefundRequest(RefundStatus updateRefundObj) {
		User user = new User();
		user.setId("DNS");
		UserContextThreadLocalHolder.setCurrentUser(user);

		FSQCDemandNoteRefund refund = null;
		String refId = updateRefundObj.getRefId();
		String demandNoteId = updateRefundObj.getDnNo();
		if (demandNoteId != null) {
			demandNoteId = demandNoteId.trim();
		}

		if (refId != null && refId.matches("\\d\\d*")) {
			refund = demandNoteRefundDao.findById(Long.parseLong(refId));
		}
		if (refund == null) {
			refund = new FSQCDemandNoteRefund();
			refund.setRemarks("Refund trigger by Finance");
			refund.setRefundStatus(DemandNoteRefundStatus.REJECTED.toString());
			refund.setRefundAmount(updateRefundObj.getReFundAmount());
			refund.setVoucherNo(updateRefundObj.getRefundVouchorNo());
			refund.setDnsRemark(updateRefundObj.getRemark());
			refund.setRepayDate(updateRefundObj.getRePayDate());
			refund.setVoucherDate(updateRefundObj.getReVouDate());
			refund.setUserCode(updateRefundObj.getUserCode());
			refund.setDemandNoteNo(demandNoteId);
			refund = demandNoteRefundDao.save(refund);
		} else {
			//refund.setStatus(String.valueOf(updateRefundObj.getDnStatus()));
			refund.setRefundStatus(DemandNoteRefundStatus.REJECTED.toString());
			refund.setRefundAmount(updateRefundObj.getReFundAmount());
			refund.setVoucherNo(updateRefundObj.getRefundVouchorNo());
			refund.setDnsRemark(updateRefundObj.getRemark());
			refund.setRepayDate(updateRefundObj.getRePayDate());
			refund.setVoucherDate(updateRefundObj.getReVouDate());
			refund.setUserCode(updateRefundObj.getUserCode());
			refund.setDemandNoteNo(demandNoteId);
			refund = demandNoteRefundDao.save(refund);
		}
	}
}
