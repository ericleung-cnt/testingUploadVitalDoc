package org.mardep.ssrs.dao.sr;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.domain.sr.VitaldocCreateImoFolder;
import org.springframework.stereotype.Repository;

@Repository
public class VitaldocCreateImoFolderJpaDao implements IVitaldocCreateImoFolderDao {

	@PersistenceContext(unitName="ssrsPU")
	protected EntityManager em;

	@Override
	public List<VitaldocCreateImoFolder> get10NotCreatedImoFolder(){
		try {
				//String sql = "select top 10 * from VITALDOC_CREATE_IMO_FOLDER where IMO_FOLDER_CREATED is null or IMO_FOLDER_CREATED<>'Y'";
				String sql = "select top 10 * from VITALDOC_CREATE_IMO_FOLDER where IMO_FOLDER_CREATED is null";
				Query q = em.createNativeQuery(sql);
				List<Object[]> objList = q.getResultList();
				List<VitaldocCreateImoFolder> entities = new ArrayList<VitaldocCreateImoFolder>();
				if (objList.size()>0) {
					for (Object[] obj:objList) {
						VitaldocCreateImoFolder entity = new VitaldocCreateImoFolder();
						int logId = (int)obj[0];
						entity.setLogId(logId);
						entity.setImo(obj[1].toString());
						entities.add(entity);
					}
				}
				return entities;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;			
		}
	}

	@Override
	public VitaldocCreateImoFolder findById(int logId) {
		try {
			String sql = "select f from VitaldocCreateImoFolder f where f.logId=:logId";
			Query q = em.createQuery(sql)
					.setParameter("logId", logId);
			List<VitaldocCreateImoFolder> entities = q.getResultList();
			if (entities.size()>0) {
				return entities.get(0);
			} else {
				return null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;			
		}
	}
	
	@Override
	public VitaldocCreateImoFolder save(VitaldocCreateImoFolder entity) {	
		try {
			VitaldocCreateImoFolder savedEntity = em.merge(entity);
			return savedEntity;
		} catch (Exception ex){
			ex.printStackTrace();
			return null;
		}
	}
}
