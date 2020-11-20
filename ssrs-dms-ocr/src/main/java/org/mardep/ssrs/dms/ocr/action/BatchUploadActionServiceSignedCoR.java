package org.mardep.ssrs.dms.ocr.action;

import java.util.List;

import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.dao.sr.IRegMasterDao;
import org.mardep.ssrs.dms.ocr.dbService.IBatchUploadDmsServiceSignedCoR;
import org.mardep.ssrs.dms.ocr.service.IBatchUploadBaseService;
import org.mardep.ssrs.dms.ocr.service.IBatchUploadServiceSignedCoR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BatchUploadActionServiceSignedCoR implements IBatchUploadActionServiceSignedCoR {

    @Autowired
    IRegMasterDao rmDao;

    @Autowired
    IBatchUploadBaseService baseSvc;
    
    @Autowired
    IBatchUploadServiceSignedCoR uploadSvc;
    
    @Autowired
    IBatchUploadDmsServiceSignedCoR uploadDmsSvc;
    
    @Override
    public void doBatchUpload() {
        // TODO Auto-generated method stub
        try {
            List<String> pathnames = baseSvc.getFileList("BatchUploadPath_SignedCoR");
            for (String pathname : pathnames){
                //HKSRCert HK4389.pdf
                String filename = baseSvc.getFilenameFromFullPath(pathname);
                String officialNum = filename.substring(9,11) + "-" + filename.substring(11,15);
                RegMaster rmEntity = new RegMaster();
                rmEntity.setOffNo(officialNum);
                List<RegMaster> entityList = rmDao.findByCriteria(rmEntity);
                if (entityList.size()>0){
                    RegMaster savedEntity = entityList.get(0);
                    byte[] fileContent = uploadSvc.getFileContent(pathname);
                    if (fileContent!=null){
                        String imo = savedEntity.getImoNo();
                        String shipName = savedEntity.getRegName();
                        uploadDmsSvc.uploadDMS(imo, shipName, officialNum, filename, fileContent);
                    }
                }
            }
        } catch (Exception ex) {

        }
    }
    
}