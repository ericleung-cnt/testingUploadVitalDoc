package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.codetable.UserGroup2;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class UserGroup2DMI extends AbstractCodeTableDMI<UserGroup2> {

	@Override
	public DSResponse fetch(UserGroup2 entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(UserGroup2 entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(UserGroup2 entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
