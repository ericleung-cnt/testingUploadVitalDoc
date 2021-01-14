package org.mardep.ssrs.service;

import java.util.List;

import javax.transaction.Transactional;

import org.mardep.ssrs.dao.sr.IVitaldocCreateImoFolderDao;
import org.mardep.ssrs.domain.sr.VitaldocCreateImoFolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class VitaldocCreateImoFolderService implements IVitaldocCreateImoFolderService {
	
	@Autowired
	IVitaldocCreateImoFolderDao dao;
	
	@Override
	public List<VitaldocCreateImoFolder> get10NotCreatedImoFolder(){
		List<VitaldocCreateImoFolder> entities = dao.get10NotCreatedImoFolder();
		return entities;
	}
}
