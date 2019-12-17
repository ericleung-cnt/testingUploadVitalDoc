package org.mardep.ssrs.dmi.sr;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Base64;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

import org.mardep.ssrs.dao.sr.IRegMasterDao;
import org.mardep.ssrs.domain.sr.ApplDetail;
import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.vitaldoc.IVitalDocClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class ApplDetailDMI extends AbstractSrDMI<ApplDetail> {

	@Autowired
	IVitalDocClient vd;
	@Autowired
	IRegMasterDao rmDao;

	@Override
	public DSResponse fetch(ApplDetail entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(ApplDetail entity, DSRequest dsRequest) throws Exception {
		String operationId = dsRequest.getOperationId();
		Map clientSuppliedValues = dsRequest.getClientSuppliedValues();
		if ("updateDocChecklist".equals(operationId)) {
			for (Object keyObj : clientSuppliedValues.keySet()) {
				String key = (String) keyObj;
				String prefix = "content_upload_";
				if (key.startsWith(prefix)) {
					String str = (String) clientSuppliedValues.get(key);
					// data:application/vnd.ms-excel;base64,...
					byte[] decoded = new byte[0];
					if (str.startsWith("data:")) {
						int base64 = str.indexOf(";base64,");
						int plain = str.indexOf(",");
						if (base64 > -1) {
							decoded = Base64.getDecoder().decode(str.substring(base64 + 8).getBytes());
						} else if (plain > -1){
							decoded = str.substring(plain + 1).getBytes();
						}
					}
					// send decoded to dms
					RegMaster reg = rmDao.findById(entity.getApplNo());
					vd.uploadSrSupporting(reg.getImoNo(), reg.getOffNo(), key.substring(prefix.length()), decoded);
				}
			}
			return super.update(entity, dsRequest);
		}
		return super.update(entity, dsRequest);
	}

	@Override
	public DSResponse add(ApplDetail entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}


}
