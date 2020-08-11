package org.mardep.ssrs.service;

import java.io.IOException;
import java.util.Date;

import org.mardep.ssrs.fsqcwebService.pojo.FsqcShipDetainData;
import org.mardep.ssrs.fsqcwebService.pojo.FsqcShipResultData;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 *
 * @author
 *
 */
public interface IFsqcService extends IBaseService{

	void sendShipParticulars(int operation, String applNo) throws JsonProcessingException;

	public void updateShipDetainFromFSQC(FsqcShipDetainData ship_data, FsqcShipResultData result);

	public boolean checkSSRS_WhiteList(String ipAddress);
	
	public void sendRequestFsqcCert(String imo) throws Exception;

	public void sendRequestFsqcPrqc(String imo, String applNo, String groupOwner, String country, Date applicationDate, Date proposedRegDate, String remarks) throws Exception;

	void simulateCertReply(String imo, String applNo, String certType, String certResult, Date resultDate) throws Exception;

}
