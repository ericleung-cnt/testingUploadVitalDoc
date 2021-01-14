package org.mardep.ssrs.dao.sr;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.domain.sr.VitaldocCreateImoFolder;
import org.springframework.stereotype.Repository;

@Repository
public class VitaldocCreateImoFolderJpaDao extends AbstractJpaDao<VitaldocCreateImoFolder, Long> implements IVitaldocCreateImoFolderDao {

	@Override
	public List<VitaldocCreateImoFolder> get10NotCreatedImoFolder(){
		try {
				String sql = "select top 10 from VITALDOC_CREATE_IMO_FOLDER where IMO_FOLDER_CREATED is null or IMO_FOLDER_CREATED<>'Y'";
				Query q = em.createNativeQuery(sql);
				List<Object[]> objList = q.getResultList();
				List<VitaldocCreateImoFolder> entities = new ArrayList<VitaldocCreateImoFolder>();
				if (objList.size()>0) {
					for (Object[] obj:objList) {
						VitaldocCreateImoFolder entity = new VitaldocCreateImoFolder();
						entity.setImo(obj[0].toString());
						entities.add(entity);
					}
				}
				return entities;
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			return null;			
		}
	}

}
