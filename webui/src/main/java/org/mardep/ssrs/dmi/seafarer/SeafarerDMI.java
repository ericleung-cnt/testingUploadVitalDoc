package org.mardep.ssrs.dmi.seafarer;

import org.mardep.ssrs.domain.seafarer.Seafarer;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class SeafarerDMI extends AbstractSeafarerDMI<Seafarer> {

	@Override
	public DSResponse fetch(Seafarer entity, DSRequest dsRequest){
		logger.info("SeafarerID:{}", entity!=null?entity.getId():null);
		switch (dsRequest.getOperation()) {
		case "singleFetch":
			if(entity!=null && entity.getId()!=null){
				String seafarerId = entity.getId();
				try{
					DSResponse dsResponse = new DSResponse();
					dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
					Seafarer seafarer = seafarerService.findById(Seafarer.class, seafarerId);
					seafarer.setNationality(null);
					if(seafarer!=null){
						dsResponse.setData(seafarer);
					}
					return dsResponse;
				}catch(Exception ex){
					handleException(ex);
				}
			}
			break;
		default:
		}
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(Seafarer entity, DSRequest dsRequest) throws Exception {
		entity.setNationality(null);
		return super.update(entity, dsRequest);
	}

	@Override
	public DSResponse add(Seafarer entity, DSRequest dsRequest) throws Exception {
		if (entity.getSeqNo() == null) {
			entity.setSeqNo(0);
		}
		return super.add(entity, dsRequest);
	}


}
