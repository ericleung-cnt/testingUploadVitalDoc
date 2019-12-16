package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.codetable.Clinic;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class ClinicDMI extends AbstractCodeTableDMI<Clinic> {

	@Override
	public DSResponse fetch(Clinic entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(Clinic entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(Clinic entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
