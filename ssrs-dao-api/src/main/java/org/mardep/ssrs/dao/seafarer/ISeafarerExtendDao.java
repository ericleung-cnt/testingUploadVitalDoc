package org.mardep.ssrs.dao.seafarer;

import java.util.List;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.seafarer.SeafarerExtend;

public interface ISeafarerExtendDao extends IBaseDao<SeafarerExtend, String>  {

	List<SeafarerExtend> getSeafarerExtendList();

}
