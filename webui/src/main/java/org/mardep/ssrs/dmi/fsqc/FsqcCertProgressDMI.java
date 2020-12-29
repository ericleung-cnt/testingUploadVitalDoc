package org.mardep.ssrs.dmi.fsqc;

import java.util.List;
import java.util.Map;

import org.mardep.ssrs.domain.fsqc.FsqcCertProgress;
import org.mardep.ssrs.domain.sr.VitaldocCreateImoFolder;
import org.mardep.ssrs.fsqcService.IFsqcCertProgressService;
import org.mardep.ssrs.service.IVitaldocCreateImoFolderService;
import org.mardep.ssrs.vitaldoc.IVitalDocClient;
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
	IVitalDocClient vitaldocClient;
	
	@Autowired
	IVitaldocCreateImoFolderService createImoFolderSvc;
	
	@Autowired
	IFsqcCertProgressService certProgressSvc;
	
	public DSResponse fetch(FsqcCertProgress entity, DSRequest dsRequest) {
		DSResponse dsResponse = new DSResponse();
		try {
//			Map suppliedValues = dsRequest.getClientSuppliedValues();
//			if (suppliedValues.containsKey("imoNo")) {
//				String imoNo = suppliedValues.get("imoNo").toString();
//				List<FsqcCertProgress> progressList = certProgressSvc.get(imoNo);
//				dsResponse.setData(progressList);
//				dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
//			} else {
//				dsResponse.setFailure("missing IMO number");
//			}
			createImoFolder();
			return dsResponse;
		} catch (Exception ex) {
			logger.error("Fail to fetch FSQC cert progress, exception: {}", ex);
			dsResponse.setStatus(DSResponse.STATUS_FAILURE);
			return dsResponse;
		}
	}
	
	private void createImoFolder() {
		try {
			List<VitaldocCreateImoFolder> entities = createImoFolderSvc.findNotProcessed();
			if (entities!=null && entities.size()>0) {
				vitaldocClient.setDmsUrl("https://10.37.115.146/vdws/VD_WS_Server.asmx");
				vitaldocClient.setUsername("administrator");
				vitaldocClient.setPassword("AES:Y53NB7qhKcrwrPJgq6HsMQ==");
				vitaldocClient.afterPropertiesSet();
	            String vitaldocSessionId = vitaldocClient.getVitaldocSessionId();				
				for (VitaldocCreateImoFolder entity:entities) {
					String imo = entity.getImo();
					if (imo!=null && !imo.isEmpty()){
						String result = vitaldocClient.cloneFsqcTemplate(vitaldocSessionId, imo);
						//String result = "";
						entity.setImo(imo);					
						if (result.equals("VITALDOC_CLONE_RESULT_SUCCESS")) {
							entity.setImoFolderCreated("Y");
							entity.setVitaldocReturn("");
						} else {
							entity.setImoFolderCreated("N");
							entity.setVitaldocReturn(result);
						}
						createImoFolderSvc.save(entity);
						System.out.println("imo:" + entity.getImo() + " result:" + result);
						logger.debug("imo:" + entity.getImo() + " result:" + result);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
}
