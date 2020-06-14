package org.mardep.ssrs.service;

import org.mardep.ssrs.domain.sr.FsqcCertResult;

public interface IFsqcCertResultService  extends IBaseService {
	public FsqcCertResult findByApplNo(String applNo);
}
