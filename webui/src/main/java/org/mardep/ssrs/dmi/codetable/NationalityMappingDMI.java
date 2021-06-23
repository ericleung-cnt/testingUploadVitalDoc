package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.codetable.NationalityMapping;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class NationalityMappingDMI extends AbstractCodeTableDMI<NationalityMapping> {

	@Override
	public DSResponse fetch(NationalityMapping entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(NationalityMapping entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(NationalityMapping entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
