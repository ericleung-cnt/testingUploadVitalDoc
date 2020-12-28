package org.mardep.ssrs.service;

import java.util.List;

import org.mardep.ssrs.domain.sr.VitaldocCreateImoFolder;

public interface IVitaldocCreateImoFolderService  extends IBaseService {
	public List<VitaldocCreateImoFolder> findNotProcessed();
	public void save(VitaldocCreateImoFolder entity);
}
