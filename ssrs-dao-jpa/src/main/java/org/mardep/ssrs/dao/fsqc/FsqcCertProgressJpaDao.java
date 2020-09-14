package org.mardep.ssrs.dao.fsqc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.mardep.ssrs.domain.fsqc.FsqcCertProgress;
import org.springframework.stereotype.Repository;

@Repository
public class FsqcCertProgressJpaDao implements IFsqcCertProgressDao {

	private final String CERT_TYPE_BCC = "BCC";
	private final String CERT_TYPE_MSMC = "MSMC";
	private final String CERT_TYPE_DMLC_I = "MLC";
	private final String CERT_TYPE_PRQC = "PRQC";

	private final String PRQC_ASSESSMENT = "ASSESSMENT";
	private final String PRQC_PASSED = "PASSED";
	private final String PRQC_FAILED = "FAILED";
	private final String PRQC_PROGRESS = "PROGRESS";

	@PersistenceContext
	protected EntityManager em;

	@Override
	public String getCertTypeNameBcc(){
		return CERT_TYPE_BCC;
	}

	@Override
	public String getCertTypeNameMsmc(){
		return CERT_TYPE_MSMC;
	}

	@Override
	public String getCertTypeNameDmlcI(){
		return CERT_TYPE_DMLC_I;
	}

	@Override
	public String getCertTypeNamePrqc(){
		return CERT_TYPE_PRQC;
	}

	@Override
	public FsqcCertProgress get(String certType, String imo)  throws Exception {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		List<Object[]> rawList = new ArrayList<Object []>();
		String sql;
		try {
			if (CERT_TYPE_PRQC.equals(certType)){
				sql = "select Top 1 * from [dbo].[vPrqcListForSSRS]\r\n" + 
						"where imo_no = :imo and cert_type = :certType\r\n" + 
						"order by begin_date desc";
			} else {
			//String sql = "select Top 1 * from [hkcert].[dbo].[vWorkListForSSRS]\r\n" + 
				sql = "select Top 1 * from [dbo].[vWorkListForSSRS]\r\n" + 
						"where imo_no = :imo and cert_type = :certType\r\n" + 
						"order by begin_date desc";
			}
			Query query = em.createNativeQuery(sql)
					.setParameter("imo", imo)
					.setParameter("certType", certType);
			rawList = query.getResultList();
			if (rawList.size()>0) {
				FsqcCertProgress entity = new FsqcCertProgress();
				Object[] obj = rawList.get(0);
				entity.setCertType(obj[0].toString());
				entity.setImono(obj[1].toString());

				// if (obj[2]!=null){
				// 	entity.setCertStatus(obj[2].toString());
				// } else {
				// 	entity.setCertStatus(null);
				// }
				
				if (obj[3]!=null) {
					entity.setCertCompleteDate(sdf.parse(obj[3].toString()));
				} else {
					entity.setCertCompleteDate(null);
				}

				if (CERT_TYPE_PRQC.equals(certType)) {
					if (obj[6]!=null) {
						String workflowState = obj[6].toString().toUpperCase();
						if (PRQC_ASSESSMENT.equals(workflowState)){
							if (obj[2]!=null){
								entity.setCertStatus(obj[2].toString().toUpperCase());
							} else {
								entity.setCertStatus(workflowState);
							}
						}
					} else {
						entity.setCertStatus(PRQC_PROGRESS);
					}
					if (obj[5]!=null) {
						entity.setCertExpiryDate(sdf.parse(obj[5].toString()));
					} else {
						entity.setCertExpiryDate(null);
					}
				} else {
					if (obj[2]!=null){
						entity.setCertStatus(obj[2].toString().toUpperCase());
					} else {
						entity.setCertStatus(null);
					}
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
