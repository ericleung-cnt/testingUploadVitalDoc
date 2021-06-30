package org.mardep.ssrs.dmi.codetable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mardep.ssrs.domain.codetable.Crew;
import org.mardep.ssrs.service.ICrewService;
import org.mardep.ssrs.service.ISeafarerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class CrewDMI extends AbstractCodeTableDMI<Crew> {

	private final String CREW_OPERATION_ID_GET_LIST = "GET_CREW_LIST_OF SHIP_YYMM";
	
	private final String OPERATIONID_UPLOAD_EXCEL= "UPLOAD_EXCEL";
	
    @Autowired
    ICrewService  crewService;
    
	@Autowired
	ISeafarerService seafarerService;
	
	@Autowired
	ICrewService  crewSerivce;
	
	
	@Override
	public DSResponse fetch(Crew entity, DSRequest dsRequest){
		String operationId = dsRequest.getOperationId();
		if (operationId.equals(CREW_OPERATION_ID_GET_LIST)) {
			Map suppliedValues = dsRequest.getClientSuppliedValues();
			String vesselId = (String) suppliedValues.get("vesselId");
			String coverYymm = (String) suppliedValues.get("coverYymm");
			List<Crew> crewList = seafarerService.getByVesselIdCoverYymm(vesselId, coverYymm);
			DSResponse dsResponse = new DSResponse();
			dsResponse.setData(crewList);
			dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
			return dsResponse;

		} else {
			return super.fetch(entity, dsRequest);
		}
	}

	@Override
	public DSResponse update(Crew entity, DSRequest dsRequest) throws Exception {
		String operationId = dsRequest.getOperationId();
		DSResponse dsResponse = new DSResponse();
		Map<String, Object> clientValues = dsRequest.getClientSuppliedValues();
		if(OPERATIONID_UPLOAD_EXCEL.equals(operationId)){
//			Map<String ,List<String>> errorMsg  = new HashMap<>();
			List<Crew> readEng2Excel = crewService.readEng2Excel(entity);
			dsResponse.setData(readEng2Excel);
//			dsResponse.setStatus(dsResponse.);
//			List<String> errors = new ArrayList<String>();
//			for(Map.Entry<String, List<String>> entry : errorMsg.entrySet()) {
//				for(String msg : entry.getValue()) {
//					errors.add(entry.getKey() +" : "+ msg);
//				}	
//			}
//			dsResponse.setErrors(errors);
			dsResponse.setStatus(dsResponse.STATUS_SUCCESS);
			return dsResponse;
			
		}else {
		entity.setCrewListCover(null);   
		entity.setCapacity(null);
		entity.setNationality(null);
		return super.update(entity, dsRequest);
		}
		
	}
	
	@Override
	public DSResponse add(Crew entity, DSRequest dsRequest) {
		DSResponse dsResponse = new DSResponse();
		dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
		try {
			Crew newCrew = seafarerService.add(entity);
			dsResponse.setTotalRows(1);
			dsResponse.setData(newCrew);
			return dsResponse;
		} catch (Exception ex){
			logger.error("Fail to add-{}, Exception-{}", new Object[]{entity, ex}, ex);
			return handleException(dsResponse, ex);
		}
		
//		return super.add(entity, dsRequest);
	}
	
}
