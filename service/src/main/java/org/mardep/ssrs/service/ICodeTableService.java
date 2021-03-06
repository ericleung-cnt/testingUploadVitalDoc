package org.mardep.ssrs.service;

import org.mardep.ssrs.domain.codetable.ShipManager;

/**
 * 
 * @author Leo.LIANG
 *
 */
public interface ICodeTableService extends IBaseService{

	//ShipManager findShipManagerByShipName(String name);

	ShipManager findShipManagerByShipName(String name, String addr1, String addr2, String addr3);
}
