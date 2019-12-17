package org.mardep.ssrs.dmi.seafarer;

import org.mardep.ssrs.domain.seafarer.SeaService;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class SeaServiceDMI extends AbstractSeafarerDMI<SeaService> {

	@Override
	public DSResponse fetch(SeaService entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(SeaService entity, DSRequest dsRequest) throws Exception {
		entity.setSeafarer(null);
		entity.setShipType(null);
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(SeaService entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
