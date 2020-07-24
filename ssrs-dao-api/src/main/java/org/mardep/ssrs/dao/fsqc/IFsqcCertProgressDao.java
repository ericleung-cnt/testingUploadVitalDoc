package org.mardep.ssrs.dao.fsqc;

import java.util.List;

import org.mardep.ssrs.domain.fsqc.FsqcCertProgress;

public interface IFsqcCertProgressDao {

	FsqcCertProgress get(String certType, String imo) throws Exception;
	
	String getCertTypeNameBcc();
	String getCertTypeNameMsmc();
	String getCertTypeNameDmlcI();
	String getCertTypeNamePrqc();

}
