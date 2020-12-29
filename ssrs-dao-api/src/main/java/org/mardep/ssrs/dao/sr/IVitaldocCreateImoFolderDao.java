package org.mardep.ssrs.dao.sr;

import java.util.List;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.sr.VitaldocCreateImoFolder;

public interface IVitaldocCreateImoFolderDao extends IBaseDao<VitaldocCreateImoFolder, Long> {
	List<String> findImoOfNotCreatedFolder();
	void updateCreateResult(VitaldocCreateImoFolder entity);
	
	List<VitaldocCreateImoFolder> findNotProcessed();
}