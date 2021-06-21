package org.mardep.ssrs.dmi.codetable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mardep.ssrs.domain.codetable.Crew006;
import org.mardep.ssrs.domain.codetable.CrewListCover;
import org.mardep.ssrs.service.ICrewService;
import org.mardep.ssrs.service.IDemandNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class CrewListCoverDMI extends AbstractCodeTableDMI<CrewListCover> {
	
	private final String OPERATIONID_UPLOAD_EXCEL= "UPLOAD_EXCEL";
	
    @Autowired
    ICrewService  crewService;
	
	
	
	@Override
	public DSResponse fetch(CrewListCover entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(CrewListCover entity, DSRequest dsRequest) throws Exception {
		String operationId = dsRequest.getOperationId();
		DSResponse dsResponse = new DSResponse();
		Map<String, Object> clientValues = dsRequest.getClientSuppliedValues();
		if(OPERATIONID_UPLOAD_EXCEL.equals(operationId)){
			Map<String ,List<String>> errorMsg  = new HashMap<>();
			List<Crew006> readEng2Excel = crewService.readEng2Excel(entity, errorMsg);
			dsResponse.setData(readEng2Excel);
//			dsResponse.setStatus(dsResponse.);
			List<String> errors = new ArrayList<String>();
			for(Map.Entry<String, List<String>> entry : errorMsg.entrySet()) {
				for(String msg : entry.getValue()) {
					errors.add(entry.getKey() +" : "+ msg);
				}	
			}
			dsResponse.setErrors(errors);
			return dsResponse;
			
		}else {
			return super.update(entity, dsRequest);			
		}
		
	}
	
	@Override
	public DSResponse add(CrewListCover entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
