package org.mardep.ssrs.dmi.sr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mardep.ssrs.dao.codetable.IClassSocietyDao;
import org.mardep.ssrs.domain.codetable.ClassSociety;
import org.mardep.ssrs.domain.sr.ApplDetail;
import org.mardep.ssrs.domain.sr.BuilderMaker;
import org.mardep.ssrs.domain.sr.Owner;
import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.domain.sr.Representative;
import org.mardep.ssrs.service.IDeRegService;
import org.mardep.ssrs.service.IInboxService;
import org.mardep.ssrs.service.IShipRegService;
import org.mardep.ssrs.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class RegMasterDMI extends AbstractSrDMI<RegMaster> {

	@Autowired
	IDeRegService deRegService;

	@Autowired
	MailService mailService;

	@Autowired
	IClassSocietyDao classSocDao;

	@Autowired
	IInboxService inbox;

	@Override
	public DSResponse fetch(RegMaster entity, DSRequest dsRequest){
		IShipRegService srService = (IShipRegService) getBaseService();
		Map clientSuppliedCriteria = dsRequest.getClientSuppliedCriteria();
		if ("AdvancedCriteria".equals(clientSuppliedCriteria.get("_constructor"))) {
			if ("and".equals(clientSuppliedCriteria.get("operator"))) {
				List<Map> criteria = (List) clientSuppliedCriteria.get("criteria");
				for (Map map : criteria) {
					if ("applNo".equals(map.get("fieldName")) &&
							"inSet".equals(map.get("operator"))) {
						List list = (List) map.get("value");
						List<RegMaster> result = srService.findByApplNoList(list);
						return new DSResponse(result, DSResponse.STATUS_SUCCESS);
					}
				}
			}
		}
		String operationId = dsRequest.getOperationId();
		if ("RegMasterDS_fetchData_checkNames".equals(operationId)) {
			Map<?,?> values = dsRequest.getClientSuppliedValues();
			List<Owner> owners = toOwners(values);
			entity = srService.check(entity, owners);
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		} else if ("RegMasterDS_fetchData_callSign".equals(operationId)) {
			String callSign = srService.getCallSign();
			entity.setCallSign(callSign);
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		} else if ("RegMasterDS_fetchData_offNo".equals(operationId)) {
			String offNo = srService.getOffNo();
			entity.setOffNo(offNo);
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		} else {
			return super.fetch(entity, dsRequest);
		}
	}

	@Override
	public DSResponse update(RegMaster entity, DSRequest dsRequest) throws Exception {
		IShipRegService srService = (IShipRegService) getBaseService();
		String operationId = dsRequest.getOperationId();
		Map clientValues = dsRequest.getClientSuppliedValues();
		Long taskId = (Long) clientValues.get("taskId");
		switch (operationId) {
		case "previewCoD":
		{
			deRegService.previewCoD(entity);
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		}
		case "deReg":
			deRegService.receive(entity.getApplNo(), false);
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		case "reReg":
			deRegService.receive(entity.getApplNo(), true);
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		case "changeReg":
			inbox.startWorkflow("changeDetails", entity.getApplNo(), "", "", "");
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		case "RegMasterDS_updateData_accept_changeDetails": {
			inbox.proceed(taskId, "accept", "");
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		}
		case "RegMasterDS_updateData_approve_changeDetails": {
			inbox.proceed(taskId, "approve", "");
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		}
		case "RegMasterDS_updateData_crossCheckReady_changeDetails": {
			inbox.proceed(taskId, "readyCrossCheckCod", "");
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		}
		case "RegMasterDS_updateData_complete_changeDetails": {
			entity = srService.completeChangeDetails(entity, taskId, getTx(clientValues));
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		}
		case "RegMasterDS_updateData_withdraw_changeDetails":{
			inbox.proceed(taskId, "withdraw", "");
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		}
		case "updateTrackCode":
			entity = srService.updateTrackCode(entity.getApplNo());
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		}
		if ("RegMasterDS_updateData_new".equals(operationId)) {
			ApplDetail details = toApplDetails(clientValues);
			List<Owner> owners = toOwners(clientValues);
			Representative rep = toRep(clientValues);

			RegMaster result = srService.create(entity, details, owners, rep);
			return new DSResponse(result, DSResponse.STATUS_SUCCESS);
		} else if ("RegMasterDS_updateData_requestAccept".equals(operationId)) {
			Map<?,?> values = clientValues;
			List<Owner> owners = toOwners(values);
			RegMaster result = srService.requestAccept(entity, owners, taskId);
			return new DSResponse(result, DSResponse.STATUS_SUCCESS);
		} else if ("RegMasterDS_updateData_accept".equals(operationId)) {
			RegMaster result = srService.accept(entity, taskId);
			return new DSResponse(result, DSResponse.STATUS_SUCCESS);
		} else if ("RegMasterDS_updateData_approveReady".equals(operationId)) {
			RegMaster result = srService.approveReady(entity, taskId);
			return new DSResponse(result, DSResponse.STATUS_SUCCESS);
		} else if ("RegMasterDS_updateData_corReady".equals(operationId)) {
			RegMaster result = srService.corReady(entity, taskId);
			return new DSResponse(result, DSResponse.STATUS_SUCCESS);
		} else if ("RegMasterDS_updateData_approve".equals(operationId)) {
			RegMaster result = srService.approve(entity, taskId);
			return new DSResponse(result, DSResponse.STATUS_SUCCESS);
		} else if ("RegMasterDS_updateData_complete".equals(operationId)) {
			RegMaster result = srService.complete(entity, taskId);
			return new DSResponse(result, DSResponse.STATUS_SUCCESS);
		} else if ("RegMasterDS_updateData_withdraw".equals(operationId) || "RegMasterDS_updateData_reject".equals(operationId)) {
			boolean byApplicant = "RegMasterDS_updateData_withdraw".equals(operationId);
			RegMaster result = srService.withdraw(entity, taskId, byApplicant);
			return new DSResponse(result, DSResponse.STATUS_SUCCESS);
		} else if ("RegMasterDS_updateData_reset".equals(operationId)) {
			RegMaster result = srService.reset(entity, taskId);
			return new DSResponse(result, DSResponse.STATUS_SUCCESS);
		} else if ("RegMasterDS_updateData_accept_reregdereg".equals(operationId)) { // Re reg De Reg actions
			Boolean reReg = (Boolean) clientValues.get("reReg");
			deRegService.accept(entity, taskId, reReg);
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		} else if ("RegMasterDS_updateData_approve_reregdereg".equals(operationId)) {
			Boolean reReg = (Boolean) clientValues.get("reReg");
			deRegService.approve(taskId, reReg);
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		} else if ("RegMasterDS_updateData_crossCheckReady_reregdereg".equals(operationId)) {
			Boolean reReg = (Boolean) clientValues.get("reReg");
			deRegService.ready(entity, taskId, reReg);
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		} else if ("RegMasterDS_updateData_complete_reregdereg".equals(operationId)) {
			Boolean reReg = (Boolean) clientValues.get("reReg");
			entity = deRegService.complete(entity, taskId, reReg, getTx(clientValues), entity.getDeRegTime());
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		} else if ("RegMasterDS_updateData_withdraw_reregdereg".equals(operationId)) {
			Boolean reReg = (Boolean) clientValues.get("reReg");
			deRegService.withdraw(taskId, reReg);
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		} else if ("RegMasterDS_updateData_complete_reregnewapp".equals(operationId)) {
			ApplDetail details = toApplDetails(clientValues);
			List<Owner> owners = toOwners(clientValues);
			Representative rep = toRep(clientValues);
			List<BuilderMaker> bmList = toBuilderMakers(clientValues);
			deRegService.completeNew(taskId, entity, details, owners, rep, bmList, getTx(clientValues), entity.getDeRegTime());
			return new DSResponse(entity, DSResponse.STATUS_SUCCESS);
		}
		return super.update(entity, dsRequest);
	}

	private ApplDetail toApplDetails(Map<?, ?> values) {
		ApplDetail details = new ApplDetail();
		Map<?,?> adMap = (Map<?,?>) values.get("applDetails");
		if (adMap != null) {
			setValues(details, adMap);
		}
		return details;
	}

	private Representative toRep(Map<?, ?> values) {
		Map<?,?> repMap = (Map<?,?>) values.get("representative");
		Representative rep = new Representative();
		setValues(rep, repMap);
		return rep;
	}

	private List<BuilderMaker> toBuilderMakers(Map<?,?> clientValues) {
		List<Map<?, ?>> maps = (List<Map<?,?>>) clientValues.get("builderMakers");
		List<BuilderMaker> list = new ArrayList<>();
		if (maps != null) {
			for (Map<?, ?> map:maps) {
				BuilderMaker bm = new BuilderMaker();
				setValues(bm, map);
				list.add(bm);
			}
		}
		return list;
	}

	private List<Owner> toOwners(Map<?,?> request) {
		List<Map<?, ?>> maps = (List<Map<?,?>>) request.get("owners");
		List<Owner> owners = new ArrayList<>();
		int seq = 1;
		for (Map<?,?> values : maps) {
			Owner owner = new Owner();
			setValues(owner, values);
			owner.setApplNo((String) request.get("applNo"));
			owners.add(owner);
		}
		return owners;
	}

	@Override
	public DSResponse add(RegMaster rm, DSRequest dsRequest) throws Exception {
		String operationId = dsRequest.getOperationId();

		switch (operationId) {
		case "emailCollectCoR": // PRG-SUPP-012	Email Owner to collect CoR
			mailService.collectCor(rm);
			break;
		case "emailClassSocCoR": // PRG-SUPP-013	Email Class Society for new CoR, updated CoR
			Map appDetails = (Map) dsRequest.getClientSuppliedValues().get("applDetails");
			if (appDetails != null) {
				String code = (String) appDetails.get("cs1ClassSocCode");
				if (code != null) {
					ClassSociety soc = classSocDao.findById( code);
					mailService.corNotify(soc, rm);
				}
			}
			break;
		case "emailRegMissingDoc":// PRG-SUPP-014	Email Owner to submit ship registration missing document
			mailService.submitMissingDoc(rm);
			break;
		case "emailOwnerAIP": // PRG-SUPP-015	Email Owner AIP
			mailService.sendOwnerAip(rm, dsRequest.getClientSuppliedValues());
			break;
		case "memoOfcaCoR": // PRG-SUPP-019	Memo to OFCA for change of CoR
			mailService.sendOfcaCorChange(rm);
			break;
		case "memoCosdCoR": // PRG-SUPP-020	Memo to CO/SD for change of CoR
			mailService.sendCoSdCorChange(rm);
			break;
		case "memoOfcaAip": // PRG-SUPP-021	Memo to OFCA for AIP, new CoR, updated CoR
			mailService.sendOfcaNewOrUpdateCorAip(rm);
			break;
		case "memoCosdAip": // PRG-SUPP-022	Memo to CO/SD for AIP, new CoR, updated CoR
			mailService.sendCoSdNewOrUpdateCorAip(rm);
			break;
		default:
			return super.add(rm, dsRequest);
		}
		return new DSResponse(DSResponse.STATUS_SUCCESS);
	}


}
