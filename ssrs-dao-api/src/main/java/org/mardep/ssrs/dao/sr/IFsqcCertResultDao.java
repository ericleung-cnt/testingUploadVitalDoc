package org.mardep.ssrs.dao.sr;

import java.util.List;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.sr.FsqcCertResult;

public interface IFsqcCertResultDao extends IBaseDao<FsqcCertResult, Long> {
	public FsqcCertResult findByApplNo(String applNo);
	public List<FsqcCertResult> findByImo(String imo);
}
