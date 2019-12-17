package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.user.SystemFunc;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class SystemFuncDMI extends AbstractCodeTableDMI<SystemFunc> {

	@Override
	public DSResponse fetch(SystemFunc entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(SystemFunc entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(SystemFunc entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
