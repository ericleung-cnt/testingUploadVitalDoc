package org.mardep.ssrs.service;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.mardep.ssrs.dao.sr.IVitaldocCreateImoFolderDao;
import org.mardep.ssrs.domain.sr.VitaldocCreateImoFolder;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VitaldocCreateImoFolderService implements IVitaldocCreateImoFolderService {
	
	@Autowired
	IVitaldocCreateImoFolderDao dao;
	
	@Override
	public List<VitaldocCreateImoFolder> get10NotCreatedImoFolder(){
		List<VitaldocCreateImoFolder> entities = dao.get10NotCreatedImoFolder();
		return entities;
	}
	
	@Transactional
	@Override
	public void createImoFolder(List<VitaldocCreateImoFolder> entities) {
		if (entities!=null && entities.size()>0) {
			User user = new User();
			user.setId("ScheduleService");
			UserContextThreadLocalHolder.setCurrentUser(user);
			for (VitaldocCreateImoFolder entity : entities) {
//				String imo = entity.getImo();
//				entity.setImoFolderCreated("Y");
//				entity.setVitaldocReturn("vitaldocReturn");
//				dao.save(entity);
				logCreateImoFolder(entity, "Y", "vitaldocReturn");
			}
		}
	}
	
	private void logCreateImoFolder(VitaldocCreateImoFolder entity, String imoFolderCreated, String vitaldocReturn) {
		VitaldocCreateImoFolder savedEntity = dao.findById(entity.getId());
		savedEntity.setImoFolderCreated(imoFolderCreated);
		savedEntity.setVitaldocReturn(vitaldocReturn);
		dao.save(savedEntity);
	}
}
