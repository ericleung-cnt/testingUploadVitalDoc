package org.mardep.ssrs.dao.seafarer;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.seafarer.CommonPK;
import org.mardep.ssrs.domain.seafarer.License;

public interface ILicenseDao extends IBaseDao<License, CommonPK> {

	License findLatestBySeafarerId(String seafarerId);

}
