package org.mardep.ssrs.service;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.mardep.ssrs.dao.sr.IVitaldocCreateImoFolderDao;
import org.mardep.ssrs.domain.sr.VitaldocCreateImoFolder;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.vitaldoc.IVitalDocClient;
import org.mardep.ssrs.vitaldoc.VitalDocClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VitaldocCreateImoFolderService implements IVitaldocCreateImoFolderService {
	
	protected static final Logger logger = LoggerFactory.getLogger(VitalDocClient.class);
	
	@Autowired
	IVitaldocCreateImoFolderDao dao;
	
	@Autowired
	IVitalDocClient vitaldocClient;
	
	@Override
	public List<VitaldocCreateImoFolder> get10NotCreatedImoFolder(){
		List<VitaldocCreateImoFolder> entities = dao.get10NotCreatedImoFolder();
		return entities;
	}
	
	@Transactional
	@Override
	public void createImoFolder(List<VitaldocCreateImoFolder> entities) {
		if (entities!=null && entities.size()>0) {
			try {
				User user = new User();
				user.setId("ScheduleService");
				UserContextThreadLocalHolder.setCurrentUser(user);
				for (VitaldocCreateImoFolder entity : entities) {
//				String imo = entity.getImo();
//				entity.setImoFolderCreated("Y");
//				entity.setVitaldocReturn("vitaldocReturn");
//				dao.save(entity);
					//String imo = entity.getImo();
					//Thread t = new Thread(() -> threadCreateImoFolder(entity));
				    //t.start();					
//					String vitaldocReturn = vitaldocClient.cloneFsqcTemplate(imo);
//					String imoFolderCreated = "Y";
//					if (vitaldocReturn.equals(vitaldocClient.getCloneResultFail())) {
//						imoFolderCreated = "N";
//					}
//					logCreateImoFolder(entity, imoFolderCreated, vitaldocReturn);
					threadCreateImoFolder(entity);
				}
			} catch (Exception ex) {
				logger.error("create imo folder: " + ex.getMessage());
			}
		}
	}
	
	private void threadCreateImoFolder(VitaldocCreateImoFolder entity) {
		try {
			String imo = entity.getImo();
			logger.info("Vitaldoc Create Imo Folder for imo:" + imo);
			String vitaldocReturn = vitaldocClient.cloneFsqcTemplate(imo);
			String imoFolderCreated = "Y";
			//if (vitaldocReturn.equals(vitaldocClient.getCloneResultFail())) {
			if (!vitaldocReturn.equals(vitaldocClient.getCloneResultSuccess()) && 
					!vitaldocReturn.equals(vitaldocClient.getCloneResultAlreadyExist())){				
				imoFolderCreated = "N";
			}
			logger.info("Vitaldoc Create Imo Folder for imo:" + imo + "," + vitaldocReturn);
			logCreateImoFolder(entity, imoFolderCreated, vitaldocReturn);
		} catch (Exception ex) {
			logger.error("create imo folder: " + ex.getMessage());
		}
	}
	
	private void logCreateImoFolder(VitaldocCreateImoFolder entity, String imoFolderCreated, String vitaldocReturn) {
		VitaldocCreateImoFolder savedEntity = dao.findById(entity.getId());
		savedEntity.setImoFolderCreated(imoFolderCreated);
		savedEntity.setVitaldocReturn(vitaldocReturn);
		dao.save(savedEntity);
	}
}
