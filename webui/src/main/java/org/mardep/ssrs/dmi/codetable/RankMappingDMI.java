package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.codetable.RankMapping;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class RankMappingDMI extends AbstractCodeTableDMI<RankMapping> {

	@Override
	public DSResponse fetch(RankMapping entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(RankMapping entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(RankMapping entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
