package org.mardep.ssrs.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.mardep.ssrs.dao.Criteria;
import org.mardep.ssrs.dao.PagingCriteria;
import org.mardep.ssrs.dao.SortByCriteria;
import org.mardep.ssrs.dao.sr.IVitaldocCreateImoFolderDao;
import org.mardep.ssrs.domain.AbstractPersistentEntity;
import org.mardep.ssrs.domain.sr.VitaldocCreateImoFolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class VitaldocCreateImoFolderService extends AbstractService  implements IVitaldocCreateImoFolderService {

	@Autowired
	IVitaldocCreateImoFolderDao createImoFolderDao;
	
	@Override
	public List<VitaldocCreateImoFolder> findNotProcessed(){
		List<VitaldocCreateImoFolder> entities = createImoFolderDao.findNotProcessed();
		return entities;
	}
	
	@Override
	public void save(VitaldocCreateImoFolder entity) {
		//createImoFolderDao.save(entity);
		super.save(entity);
	}
}
