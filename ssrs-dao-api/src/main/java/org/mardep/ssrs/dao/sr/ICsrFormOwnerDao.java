package org.mardep.ssrs.dao.sr;

import java.util.List;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.sr.CsrFormOwner;

public interface ICsrFormOwnerDao extends IBaseDao<CsrFormOwner, Long> {
	public List<CsrFormOwner> get(String imoNo, Integer formSeq);
}
