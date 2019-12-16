package org.mardep.ssrs.dmi.dn;

import org.mardep.ssrs.dmi.AbstractDMI;
import org.mardep.ssrs.domain.dn.DemandNoteRefund;
import org.mardep.ssrs.service.IBaseService;
import org.mardep.ssrs.service.IDemandNoteService;
import org.mardep.ssrs.service.IDnsService;
import org.mardep.ssrs.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class DemandNoteRefundDMI extends AbstractDMI<DemandNoteRefund>{

	@Autowired
	IDemandNoteService demandNoteService;

	@Autowired
	IDnsService dnsService;
	
	@Autowired
	MailService mailService;

	@Override
	protected IBaseService getBaseService(){
		return demandNoteService;
	}

	@Override
	public DSResponse fetch(DemandNoteRefund entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(DemandNoteRefund entity, DSRequest dsRequest) {
		return null;
	}

	@Override
	public DSResponse remove(DemandNoteRefund entity, DSRequest dsRequest) {
		return null;
	}

	@Override
	public DSResponse add(DemandNoteRefund entity, DSRequest dsRequest) {
		DSResponse dsResponse = new DSResponse();
		dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
		try{
			String operationId = dsRequest.getOperationId();
			String demandNoteNo = entity.getDemandNoteNo();
			if(DemandNoteOperationId.REFUND_DEMAND_NOTE.equals(operationId)){
				DemandNoteRefund refund = demandNoteService.refund(demandNoteNo, entity.getRefundAmount(), entity.getRemarks());
				dsResponse.setTotalRows(1);
				dsResponse.setData(refund);
				//Asyn
				dnsService.refundDemandNote(refund.getRefundId());
				
				mailService.sendReund(refund);
				return dsResponse;
			}
		}catch(Exception ex){
			logger.error("Fail to update-{}, Exception-{}", new Object[]{entity, ex}, ex);
			return handleException(dsResponse, ex);
		}
		return null;
	}

}
