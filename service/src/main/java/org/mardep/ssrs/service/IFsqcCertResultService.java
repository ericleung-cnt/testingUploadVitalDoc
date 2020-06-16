package org.mardep.ssrs.service;

import java.util.List;

import org.mardep.ssrs.domain.sr.FsqcCertResult;

public interface IFsqcCertResultService  extends IBaseService {
	public FsqcCertResult findByApplNo(String applNo);

	public List<FsqcCertResult> findByImo(String imo);
}
