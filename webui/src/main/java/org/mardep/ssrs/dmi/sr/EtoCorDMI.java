package org.mardep.ssrs.dmi.sr;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mardep.ssrs.domain.sr.EtoCoR;
import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.service.IEtoCorService;
import org.mardep.ssrs.service.IShipRegService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class EtoCorDMI {
	
	@Autowired
	IEtoCorService etoCorSvc;
	
	@Autowired
	IShipRegService srSvc;
	
	private final String OPERATION_INSERT_MULTI_PRO_REG_COR = "INSERT_MULTI_PRO_REG_COR";
	private final String OPERATION_INSERT_MULTI_FULL_REG_COR = "INSERT_MULTI_FULL_REG_COR";
	private final String OPERATION_UPDATE_VALID_COR = "UPDATE_VALID_COR";
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	public DSResponse fetch(EtoCoR entity, DSRequest dsRequest) {
		DSResponse dsResponse = new DSResponse();
		try {
			Map suppliedValues = dsRequest.getClientSuppliedValues();
			if (suppliedValues.containsKey("applNo")) {
				String applNo = suppliedValues.get("applNo").toString();
				List<EtoCoR> resultList = etoCorSvc.findEtoCorList(applNo);
				dsResponse.setData(resultList);
				dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
			} else {
				dsResponse.setStatus(DSResponse.STATUS_FAILURE);
			}
			return dsResponse;
		}catch (Exception ex) {
			logger.error("Fail to fetch-{}, Exception-{}", new Object[]{entity, ex}, ex);			
			dsResponse.setStatus(DSResponse.STATUS_FAILURE);
			return dsResponse;				
		}	
	}
	
	public DSResponse update(EtoCoR entity, DSRequest dsRequest) {
		DSResponse dsResponse = new DSResponse();
		try {
			String operationId = dsRequest.getOperationId();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			
			if (OPERATION_INSERT_MULTI_PRO_REG_COR.equals(operationId)) {
				List<EtoCoR> etoCoRs = new ArrayList<EtoCoR>();
				
				Map suppliedValues = dsRequest.getClientSuppliedValues();
				List<Map<?,?>> values = (List<Map<?,?>>) suppliedValues.get("etoCorList");
				for (int i=0; i<values.size(); i++) {
					EtoCoR etoCor = new EtoCoR();
					String applNo = values.get(i).get("applNo").toString();
					Date regDate = sdf.parse(values.get(i).get("regDate").toString());
					Date issueDate = sdf.parse(values.get(i).get("issueDate").toString());
					Long registrarId = new Long(values.get(i).get("registrar").toString());
					String trackCode = srSvc.prepareTrackCode(applNo);
					etoCor.setApplNo(applNo);
					etoCor.setRegDate(regDate);
					etoCor.setCertIssueDate(issueDate);
					etoCor.setRegistrarId(registrarId);
					etoCor.setTrackCode(trackCode);
					
					etoCoRs.add(etoCor);
				}
				
				etoCorSvc.replaceMultiEtoCoR_ProReg(etoCoRs, etoCoRs.get(0).getApplNo());
				List<EtoCoR> resultList = etoCorSvc.findEtoCorList(etoCoRs.get(0).getApplNo(), "P");
				dsResponse.setData(resultList);
			} else if (OPERATION_INSERT_MULTI_FULL_REG_COR.equals(operationId)) {
				List<EtoCoR> etoCoRs = new ArrayList<EtoCoR>();
				
				Map suppliedValues = dsRequest.getClientSuppliedValues();
				List<Map<?,?>> values = (List<Map<?,?>>) suppliedValues.get("etoCorList");
				for (int i=0; i<values.size(); i++) {
					EtoCoR etoCor = new EtoCoR();
					String applNo = values.get(i).get("applNo").toString();
					Date regDate = sdf.parse(values.get(i).get("regDate").toString());
					Date issueDate = sdf.parse(values.get(i).get("issueDate").toString());
					Long registrarId = new Long(values.get(i).get("registrarId").toString());
					String trackCode = srSvc.prepareTrackCode(applNo);
					etoCor.setApplNo(applNo);
					etoCor.setRegDate(regDate);
					etoCor.setCertIssueDate(issueDate);
					etoCor.setRegistrarId(registrarId);
					etoCor.setTrackCode(trackCode);
					
					etoCoRs.add(etoCor);
				}
				
				etoCorSvc.replaceMultiEtoCoR_FullReg(etoCoRs, etoCoRs.get(0).getApplNo());
				List<EtoCoR> resultList = etoCorSvc.findEtoCorList(etoCoRs.get(0).getApplNo(), "F");
				dsResponse.setData(resultList);
			} else if (OPERATION_UPDATE_VALID_COR.equals(operationId)) {
				SimpleDateFormat sdfRegDate = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				Map suppliedValues = dsRequest.getClientSuppliedValues();
				
				String regDateStr = suppliedValues.get("regDate").toString();
				Long id = new Long(suppliedValues.get("id").toString());
				String applNo = suppliedValues.get("applNo").toString();
				String applNoSuf = suppliedValues.get("applNoSuf").toString();
				Long registrarId = new Long(suppliedValues.get("registrarId").toString());
				String trackCode = suppliedValues.get("trackCode").toString();
				
				EtoCoR etoCor = new EtoCoR();
				etoCor.setApplNo(applNo);
				etoCor.setApplNoSuf(applNoSuf);
				etoCor.setRegistrarId(registrarId);
				etoCor.setTrackCode(trackCode);
				etoCor.setId(id);
				
				etoCorSvc.updateValidEtoCoR(entity);
				RegMaster rm = srSvc.assignRegDateTrackCode(entity.getApplNo(), entity.getApplNoSuf(), sdfRegDate.parse(regDateStr), entity.getRegistrarId(), entity.getTrackCode());
			//} else if ("REPLACE_MULTI_COR".equals(operationId)) {
				//RegMaster rm = srSvc.findById(RegMaster.class, entity.getApplNo());
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("applNo", rm.getApplNo());
				map.put("regStatus", rm.getRegStatus());
				map.put("registrar", rm.getRegistrar().toString());
				map.put("certified", false);
				map.put("paymentRequired", false);
				map.put("reason","");
				map.put("printMortgage", true);
				map.put("zip", false);
				map.put("issueDate", new SimpleDateFormat("dd/MM/yyyy").format(rm.getRegDate()));
				srSvc.uploadCoRToVitalDoc(map);
			}
			dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
			return dsResponse;
		} catch (Exception ex) {
			logger.error("Fail to update-{}, Exception-{}", new Object[]{entity, ex}, ex);			
			dsResponse.setStatus(DSResponse.STATUS_FAILURE);
			return dsResponse;				
		}	
	}
}
