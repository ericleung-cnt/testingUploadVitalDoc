package org.mardep.ssrs.dms.ocr.action;

import java.util.List;

import javax.transaction.Transactional;

import org.mardep.ssrs.dao.sr.IVitaldocCreateImoFolderDao;
import org.mardep.ssrs.domain.sr.VitaldocCreateImoFolder;
import org.mardep.ssrs.vitaldoc.IVitalDocClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
//@Transactional
public class BatchActionServiceCreateImoFolder implements IBatchActionServiceCreateImoFolder {

	@Autowired
	IVitaldocCreateImoFolderDao createImoFolderDao;
	
    @Autowired
	IVitalDocClient vdClient;

	@Override
	public void doBatchCreateImoFolder() {
		// TODO Auto-generated method stub
		int createFolderLimit = 10;
		
		try {
			List<String> imos = createImoFolderDao.findImoOfNotCreatedFolder();
			if (imos!=null) {
	            String vitaldocSessionId = vdClient.getVitaldocSessionId();				
				for (String imo:imos) {
					VitaldocCreateImoFolder entity = new VitaldocCreateImoFolder();
					if (!onlyDigits(imo)) {
						entity.setImo(imo);
						entity.setImoFolderCreated("I");
						entity.setVitaldocReturn("invalid imo");
					} else {						
						String result = vdClient.cloneFsqcTemplate(vitaldocSessionId, imo);
						entity.setImo(imo);					
						if (result.equals("VITALDOC_CLONE_RESULT_SUCCESS")) {
							entity.setImoFolderCreated("Y");
							entity.setVitaldocReturn("");
						} else {
							entity.setImoFolderCreated("N");
							entity.setVitaldocReturn(result);
						}
					}
					createImoFolderDao.updateCreateResult(entity);
					
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public boolean onlyDigits(String str) 
    { 
        // Traverse the string from 
        // start to end 
		if (str.equals("-")||str.equals("_")) return false;
		int n = str.length();
        for (int i = 0; i < n; i++) { 
  
            // Check if character is 
            // digit from 0-9 
            // then return true 
            // else false 
            if (str.charAt(i) >= '0'
                && str.charAt(i) <= '9') { 
                return true; 
            } 
            else { 
                return false; 
            } 
        } 
        return false; 
    } 
}
