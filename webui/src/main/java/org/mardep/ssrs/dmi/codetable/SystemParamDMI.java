package org.mardep.ssrs.dmi.codetable;

import java.util.Map;

import org.mardep.ssrs.domain.codetable.SystemParam;
import org.mardep.ssrs.service.ICodeTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class SystemParamDMI extends AbstractCodeTableDMI<SystemParam> {

	@Autowired
	ICodeTableService codeTableSvc;
	
	@Override
	public DSResponse fetch(SystemParam entity, DSRequest dsRequest){
		Map suppliedValues = dsRequest.getClientSuppliedValues();
		if (suppliedValues.containsKey("ParamId")) {
			String paramId = (String)suppliedValues.get("ParamId");
			SystemParam savedEntity = fetchSpecificParam(paramId);
			DSResponse dsResponse = new DSResponse();
			dsResponse.setData(savedEntity);
			dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
			return dsResponse;
		} else {
			return super.fetch(entity, dsRequest);			
		}
	}

	private SystemParam fetchSpecificParam(String paramId)
	{
		SystemParam entity = codeTableSvc.findSystemParamById(paramId);		
		return entity;
	}
	@Override
	public DSResponse update(SystemParam entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(SystemParam entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
