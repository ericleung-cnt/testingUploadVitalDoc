package org.mardep.ssrs.dmi.fsqc;

import java.util.List;
import java.util.Map;

import org.mardep.ssrs.domain.fsqc.FsqcCertProgress;
import org.mardep.ssrs.fsqcService.IFsqcCertProgressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class FsqcCertProgressDMI {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	IFsqcCertProgressService certProgressSvc;
	
	public DSResponse fetch(FsqcCertProgress entity, DSRequest dsRequest) {
		DSResponse dsResponse = new DSResponse();
		try {
			Map suppliedValues = dsRequest.getClientSuppliedValues();
			if (suppliedValues.containsKey("imoNo")) {
				String imoNo = suppliedValues.get("imoNo").toString();
				List<FsqcCertProgress> progressList = certProgressSvc.get(imoNo);
				dsResponse.setData(progressList);
				dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
			} else {
				dsResponse.setFailure("missing IMO number");
			}
			return dsResponse;
		} catch (Exception ex) {
			logger.error("Fail to fetch FSQC cert progress, exception: {}", ex);
			dsResponse.setStatus(DSResponse.STATUS_FAILURE);
			return dsResponse;
		}
	}
}
