package org.mardep.ssrs.dmi.sr;

import java.util.List;
import java.util.Map;

import org.mardep.ssrs.domain.sr.FsqcCertResult;
import org.mardep.ssrs.service.IEtoCorService;
import org.mardep.ssrs.service.IFsqcCertResultService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class FsqcCertResultDMI {

	@Autowired
	IFsqcCertResultService fsqcCertResultSvc;
		
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	public DSResponse fetch(FsqcCertResult entity, DSRequest dsRequest) {
		DSResponse dsResponse = new DSResponse();
		try {
			Map suppliedValues = dsRequest.getClientSuppliedValues();
			if (suppliedValues.containsKey("imoNo")) {
				String imo = suppliedValues.get("imoNo").toString();
				List<FsqcCertResult> entities = fsqcCertResultSvc.findByImo(imo);
				dsResponse.setData(entities);
				dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
			} else {
				dsResponse.setFailure("Missing IMO no.");
			}
			return dsResponse;
		} catch (Exception ex) {
			logger.error("Fail to fetch-{}, Exception-{}", new Object[]{entity, ex}, ex);			
			dsResponse.setStatus(DSResponse.STATUS_FAILURE);
			return dsResponse;							
		}
	}
}
