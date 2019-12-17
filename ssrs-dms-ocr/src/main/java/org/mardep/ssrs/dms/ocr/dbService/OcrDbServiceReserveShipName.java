package org.mardep.ssrs.dms.ocr.dbService;

import java.io.IOException;
import java.text.ParseException;

import javax.transaction.Transactional;

import org.mardep.ssrs.dms.ocr.xml.OcrXmlReserveShipName;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlReserveShipName_ProposedName;
import org.mardep.ssrs.domain.sr.PreReservedName;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.service.IReservationService;
import org.mardep.ssrs.vitaldoc.IVitalDocClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class OcrDbServiceReserveShipName implements IOcrDbServiceReserveShipName {

//	@Autowired
//	protected IOcrPreReserveShipNameDao reserveShipNameDao;

	@Autowired
	protected IReservationService reservationService;
	
	@Autowired IVitalDocClient vd;

	@Override
	public void save(OcrXmlReserveShipName xml, byte[] pdf) throws IOException, ParseException {
		// TODO Auto-generated method stub
		User user = new User();
		user.setId("OCR");
		UserContextThreadLocalHolder.setCurrentUser(user);

		if (xml.getProposedNameList()!=null && !xml.getProposedNameList().isEmpty()) {
			for (OcrXmlReserveShipName_ProposedName proposedName : xml.getProposedNameList()) {
				if (proposedName.getProposedEngName()!=null && !proposedName.getProposedEngName().isEmpty()) {
					PreReservedName entity = new PreReservedName();
					entity.setOwnerName(xml.getNameAndAddressOfOwner());
					entity.setAddress1(xml.getNameAndAddressOfOwner());
					entity.setApplName(xml.getNameAndAddressOfApplicant());
					entity.setName(proposedName.getProposedEngName());
					entity.setLanguage("EN");
					entity.setExpiryTime(xml.getExpiryDate());
					reservationService.savePreReservation(entity);
				}
				if (proposedName.getProposedChiName()!=null && !proposedName.getProposedChiName().isEmpty()) {
					PreReservedName entity = new PreReservedName();
					entity.setOwnerName(xml.getNameAndAddressOfOwner());
					entity.setAddress1(xml.getNameAndAddressOfOwner());
					entity.setApplName(xml.getNameAndAddressOfApplicant());
					entity.setName(proposedName.getProposedChiName());
					entity.setLanguage("ZH");
					entity.setExpiryTime(xml.getExpiryDate());
					reservationService.savePreReservation(entity);
				}
//				long docId = vd.uploadShipNameReservation("TODO", xml.getNameAndAddressOfApplicant(), xml.getNameAndAddressOfOwner(), pdf);
//				if (docId <= 0) {
//					throw new IOException("upload failure");
//				}
			}
		}
	}

}
