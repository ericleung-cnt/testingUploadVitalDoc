package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.codetable.ShipManager;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class ShipManagerDMI extends AbstractCodeTableDMI<ShipManager> {

	@Override
	public DSResponse fetch(ShipManager entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(ShipManager entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(ShipManager entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
