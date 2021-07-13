package org.mardep.ssrs.dmi.seafarer;

import org.mardep.ssrs.domain.seafarer.SeafarerExtend;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class SeafarerExtendDMI extends AbstractSeafarerDMI<SeafarerExtend> {
	
	@Override
	public DSResponse fetch(SeafarerExtend entity, DSRequest dsRequest) {
		return super.fetch(entity, dsRequest);
	}
}
