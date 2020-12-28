package org.mardep.ssrs.dao.sr;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.Criteria;
import org.mardep.ssrs.dao.PagingCriteria;
import org.mardep.ssrs.dao.SortByCriteria;
import org.mardep.ssrs.domain.sr.EtoCoR;
import org.mardep.ssrs.domain.sr.Transaction;
import org.mardep.ssrs.domain.sr.VitaldocCreateImoFolder;
import org.springframework.stereotype.Repository;

@Repository
//@Transactional
public class VitaldocCreateImoFolderJpaDao extends AbstractJpaDao<VitaldocCreateImoFolder, Long> implements IVitaldocCreateImoFolderDao {

	@Override
	public List<VitaldocCreateImoFolder> findNotProcessed(){
		try {
			String sql = "select v from VitaldocCreateImoFolder v " +
					//"where imo_folder_created is null and imo<>'-' and imo<>'_' order by imo";
					"where v.imoFolderCreated is null and v.imo not LIKE '%[^0123456789]%' order by v.imo desc";
				Query q = em.createQuery(sql);
				//List<String> imos = new ArrayList<String>();
				List<VitaldocCreateImoFolder> entities = q.getResultList();
				return entities;			
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			return null;
		}
	}
	
	@Override
	public List<String> findImoOfNotCreatedFolder(){
		try {
			String sql = "select top 10 imo from VITALDOC_CREATE_IMO_FOLDER " +
				//"where imo_folder_created is null and imo<>'-' and imo<>'_' order by imo";
				"where imo_folder_created is null and imo not LIKE '%[^0123456789]%' order by imo";
			Query q = em.createNativeQuery(sql);
			List<String> imos = new ArrayList<String>();
			List<String> rawList = q.getResultList();
			for (String arr:rawList) {
				if (arr!=null) {
					String imo = arr.toString();
					imos.add(imo);					
				}
			}
			return imos;
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			return null;
		}
	}
	
	private VitaldocCreateImoFolder findByImo(String imo) {
		try {
			String sql = "select v from VitaldocCreateImoFolder v where v.imo=:imo";
			Query q = em.createQuery(sql)
					.setParameter("imo", imo);
			List<VitaldocCreateImoFolder> entities = q.getResultList();
			if (entities!=null && entities.size()>0) {
				return entities.get(0);
			} else {
				return null;
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			return null;
		}
	}
	
	@Override
	public void updateCreateResult(VitaldocCreateImoFolder entity) {
		try {
//			VitaldocCreateImoFolder savedEntity = findByImo(entity.getImo());
			String sql = "select v from VitaldocCreateImoFolder v where v.imo=:imo";
			Query q = em.createQuery(sql)
					.setParameter("imo", entity.getImo());
			List<VitaldocCreateImoFolder> entities = q.getResultList();
			if (entities!=null && entities.size()>0) {
				VitaldocCreateImoFolder savedEntity = entities.get(0);
				if (savedEntity!=null) {
					savedEntity.setImoFolderCreated(entity.getImoFolderCreated());;
					savedEntity.setVitaldocReturn(entity.getVitaldocReturn());
					//super.save(savedEntity);
					em.merge(savedEntity);
				}
			}
//		try {
//			EntityTransaction tx = null;
//			tx = em.getTransaction();
//			tx.begin();
//			String sql = "update VITALDOC_CREATE_IMO_FOLDER " +
//					"set IMO_FOLDER_CREATED=:imoFolderCreated, VITALDOC_RETURN=:vitaldocReturn " +
//					"where imo=:imo";
//			Query q = em.createNativeQuery(sql)
//					.setParameter("imoFolderCreated", entity.getImoFolderCreated())
//					.setParameter("vitaldocReturn", entity.getVitaldocReturn())
//					.setParameter("imo", entity.getImo());
//			q.executeUpdate();
//			tx.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
}
