package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.codetable.Office;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class OfficeDMI extends AbstractCodeTableDMI<Office> {

	@Override
	public DSResponse fetch(Office entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(Office entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(Office entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}