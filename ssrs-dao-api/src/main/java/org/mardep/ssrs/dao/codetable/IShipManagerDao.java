package org.mardep.ssrs.dao.codetable;

import java.util.List;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.codetable.ShipManager;

public interface IShipManagerDao extends IBaseDao<ShipManager, Long> {

	ShipManager findByName(String name, String addr1, String addr2, String addr3);
	
	List<ShipManager> getAll();
}
