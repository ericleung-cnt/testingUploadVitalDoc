package org.mardep.ssrs.dms.ocr.dbService;

public interface IBatchUploadDmsServiceSignedCoR{
    boolean uploadDMS(String imo, String shipName, String officialNum, String docName, byte[] fileContent) throws Exception ;
}