package org.mardep.ssrs.dms.ocr.dbService;

import java.io.IOException;

import javax.transaction.Transactional;

import org.mardep.ssrs.dao.ocr.IOcrRegMasterDao;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlCos;
import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.vitaldoc.IVitalDocClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class OcrDbServiceCos implements IOcrDbServiceCos {

	@Autowired
	protected IOcrRegMasterDao regMasterDao;

	@Autowired
	IVitalDocClient vd;

	@Override
	public void save(OcrXmlCos xml, byte[] pdf) throws IOException {
		// TODO Auto-generated method stub
		User user = new User();
		user.setId("OCR");
		UserContextThreadLocalHolder.setCurrentUser(user);

		RegMaster entityRegMaster = regMasterDao.getByShipNameAndIMO(xml.getShipName(), xml.getImoNumber());
		if (entityRegMaster!=null) {
			entityRegMaster.setHowProp(xml.getHowPropelled());
			entityRegMaster.setMaterial(xml.getMaterialOfHull());
			entityRegMaster.setGrossTon(xml.getShipGrossTonnage());
			entityRegMaster.setRegNetTon(xml.getShipNetTonnage());
			entityRegMaster.setBuildDate(xml.getKeelLaidDate());
			entityRegMaster.setLength(xml.getShipLength());
			entityRegMaster.setBreadth(xml.getShipBreadth());
			entityRegMaster.setDepth(xml.getShipDepth());
			entityRegMaster.setEngSetNum(xml.getEngSetNum());
			entityRegMaster.setNoOfShafts(xml.getNoOfShafts());
			entityRegMaster.setEngPower(xml.getTotalEnginePower());
			entityRegMaster.setEngMaker(xml.getEngineMake());
			entityRegMaster.setEstSpeed(xml.getBuilderNameAddress());
			long docId = vd.uploadCos("TODO",entityRegMaster.getRegName(), entityRegMaster.getImoNo(), pdf);
			if (docId <= 0) {
				throw new IOException("upload failure");
			}

			regMasterDao.save(entityRegMaster);
		}
	}
}
