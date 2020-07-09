package org.mardep.ssrs.dao.fsqc;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.mardep.ssrs.domain.fsqc.FsqcCertProgress;
import org.springframework.stereotype.Repository;

@Repository
public class FsqcCertProgressJpaDao implements IFsqcCertProgressDao {

	@PersistenceContext
	protected EntityManager em;

	@Override
	public FsqcCertProgress get(String certType, String imo)  throws Exception {
		// TODO Auto-generated method stub
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		
			String sql = "select Top 1 * from [hkcert].[dbo].[vWorkListForSSRS]\r\n" + 
					"where imo_no = :imo and cert_type = :certType\r\n" + 
					"order by begin_date desc";
			Query query = em.createNativeQuery(sql)
					.setParameter("imo", imo)
					.setParameter("certType", certType);
			List<Object[]> rawList = query.getResultList();
			if (rawList.size()>0) {
				FsqcCertProgress entity = new FsqcCertProgress();
				Object[] obj = rawList.get(0);
				entity.setCertType(obj[0].toString());
				entity.setImono(obj[1].toString());
				entity.setCertStatus(obj[2].toString());
				if (obj[3]!=null) {
					entity.setCertCompleteDate(sdf.parse(obj[3].toString()));
				} else {
					entity.setCertCompleteDate(null);
				}
				return entity;				
			} else {
				return null;
			}
		} catch (Exception ex) {
			throw ex;
		}
	}

}
