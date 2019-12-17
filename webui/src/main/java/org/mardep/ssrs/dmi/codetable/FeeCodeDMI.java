package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.codetable.FeeCode;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class FeeCodeDMI extends AbstractCodeTableDMI<FeeCode> {

	@Override
	public DSResponse fetch(FeeCode entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(FeeCode entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(FeeCode entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
