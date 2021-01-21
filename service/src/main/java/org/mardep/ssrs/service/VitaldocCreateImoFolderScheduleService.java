package org.mardep.ssrs.service;

import java.util.List;

import javax.transaction.Transactional;

import org.mardep.ssrs.domain.sr.VitaldocCreateImoFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class VitaldocCreateImoFolderScheduleService implements IVitaldocCreateImoFolderScheduleService {
	
	Logger logger = LoggerFactory.getLogger(VitaldocCreateImoFolderScheduleService.class);
	
	@Autowired
	IVitaldocCreateImoFolderService svc;
	
	@Value("${VitaldocCreateImoFolderService.enable:false}")
	private boolean vitaldocCreateImoFolderServiceEnable;

	@Scheduled(cron="${VitaldocCreateImoFolderService.doAction.cron}")
	//@Transactional
	@Override
	public void doAction() {
		if (vitaldocCreateImoFolderServiceEnable) {
			logger.info("Vitaldoc Create Imo Folder");
			List<VitaldocCreateImoFolder> entities = svc.get10NotCreatedImoFolder();
			svc.createImoFolder(entities);
		}
	}
}
