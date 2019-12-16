package org.mardep.ssrs.dmi.seafarer;

import org.mardep.ssrs.domain.seafarer.License;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class LicenseDMI extends AbstractSeafarerDMI<License> {

	@Override
	public DSResponse fetch(License entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(License entity, DSRequest dsRequest) throws Exception {
		entity.setSeafarer(null);
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(License entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
