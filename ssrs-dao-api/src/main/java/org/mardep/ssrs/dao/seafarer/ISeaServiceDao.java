package org.mardep.ssrs.dao.seafarer;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.seafarer.CommonPK;
import org.mardep.ssrs.domain.seafarer.SeaService;

public interface ISeaServiceDao extends IBaseDao<SeaService, CommonPK> {

	SeaService findLatestBySeafarerId(String seafarerId);
	
}
