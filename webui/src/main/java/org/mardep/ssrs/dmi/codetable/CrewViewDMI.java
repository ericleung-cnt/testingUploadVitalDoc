package org.mardep.ssrs.dmi.codetable;

import java.util.Map;

import org.mardep.ssrs.domain.codetable.CrewView;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class CrewViewDMI extends AbstractCodeTableDMI<CrewView> {
	
	@Override
	public DSResponse fetch(CrewView entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(CrewView entity, DSRequest dsRequest) throws Exception {
		String operationId = dsRequest.getOperationId();
		DSResponse dsResponse = new DSResponse();
		Map<String, Object> clientValues = dsRequest.getClientSuppliedValues();
		return super.update(entity, dsRequest);			
	}
	
	@Override
	public DSResponse add(CrewView entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
