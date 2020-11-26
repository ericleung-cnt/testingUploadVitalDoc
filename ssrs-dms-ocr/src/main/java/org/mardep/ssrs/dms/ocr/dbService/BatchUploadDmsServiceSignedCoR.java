package org.mardep.ssrs.dms.ocr.dbService;

import java.util.Map;

import org.mardep.ssrs.dms.ocr.service.IBatchUploadBaseService;
import org.mardep.ssrs.vitaldoc.IVitalDocClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BatchUploadDmsServiceSignedCoR implements IBatchUploadDmsServiceSignedCoR {

    @Autowired
	IVitalDocClient vdClient;

    @Autowired
    IBatchUploadBaseService baseSvc;

    @Override
    public boolean uploadDMS(String imo, String shipName, String officialNum, String docName, byte[] fileContent) throws Exception {
        // TODO Auto-generated method stub
        boolean success = true;
        try {
            Map<String, String> vitalDocProperties = baseSvc.createVitalDocPropertiesForSrIssuedDoc(imo, shipName, officialNum);
            vitalDocProperties.put("Issue type", "CoR");
            String vitaldocSessionId = vdClient.getVitaldocSessionId();
            Long docId = vdClient.uploadSignedCoR(vitaldocSessionId, vitalDocProperties, imo, docName, fileContent);    
            String shortcutPath = vdClient.getShortcutPathForSignedCoR(imo);
            success = vdClient.createShortcutInFsqcVitalDoc(vitaldocSessionId, imo, docId, shortcutPath);
            return success;
        } catch (Exception ex){
            //throw ex;
            return false;
        }
    }
    
}