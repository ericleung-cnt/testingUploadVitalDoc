package org.mardep.ssrs.dms.ocr.service;

import java.util.List;
import java.util.Map;

public interface IBatchUploadBaseService {
    public List<String> getFileList(String propertyName) ;
    public Map<String, String> createVitalDocPropertiesForSrIssuedDoc(String imo, String shipName, String officialNum);
    public String getFilenameFromFullPath(String fullPath);
}