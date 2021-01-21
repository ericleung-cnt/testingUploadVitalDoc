package org.mardep.ssrs.service;

import java.util.List;

import org.mardep.ssrs.domain.sr.VitaldocCreateImoFolder;

public interface IVitaldocCreateImoFolderService {
	List<VitaldocCreateImoFolder> get10NotCreatedImoFolder();
	void createImoFolder(List<VitaldocCreateImoFolder> entities);
}
