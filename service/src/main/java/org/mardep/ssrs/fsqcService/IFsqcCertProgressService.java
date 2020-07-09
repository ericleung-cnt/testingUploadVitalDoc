package org.mardep.ssrs.fsqcService;

import java.util.List;

import org.mardep.ssrs.domain.fsqc.FsqcCertProgress;

public interface IFsqcCertProgressService {
	List<FsqcCertProgress> get(String imo) throws Exception;
}
