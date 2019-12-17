package org.mardep.ssrs.dmi.seafarer;

import org.mardep.ssrs.domain.seafarer.Employment;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class EmploymentDMI extends AbstractSeafarerDMI<Employment> {

	@Override
	public DSResponse fetch(Employment entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(Employment entity, DSRequest dsRequest) throws Exception {
		entity.setSeafarer(null);
//		entity.setPermittedcompany(null);
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(Employment entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
