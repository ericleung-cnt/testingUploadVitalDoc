package org.mardep.ssrs.service;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang.reflect.FieldUtils;
import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.dao.codetable.ICrewDao;
import org.mardep.ssrs.dao.seafarer.IMedicalDao;
import org.mardep.ssrs.dao.seafarer.IPreviousSerbDao;
import org.mardep.ssrs.dao.seafarer.IRegDao;
import org.mardep.ssrs.dao.seafarer.ISeafarerDao;
import org.mardep.ssrs.domain.AbstractPersistentEntity;
import org.mardep.ssrs.domain.codetable.Crew;
import org.mardep.ssrs.domain.seafarer.PreviousSerb;
import org.mardep.ssrs.domain.seafarer.Reg;
import org.mardep.ssrs.domain.seafarer.Seafarer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * @author Leo.LIANG
 *
 */
@Service
@Transactional
public class SeafarerService extends AbstractService implements ISeafarerService{

	@Autowired
	ISeafarerDao seafarerDao;
	
	@Autowired
	IPreviousSerbDao previousSerbDao;

	@Autowired
	ICrewDao crewDao;

	@Autowired
	IRegDao regDao;

	@Autowired
	IMedicalDao medicalDao;
	
	@Override
	public Reg renew(Reg reg){
		String seafarerId = reg.getSeafarerId();
		logger.info("#Renew Seafarer Registration, SeafarerID:[{}]", new Object[]{seafarerId});
		logger.info("RegDate:[{}], ExpiryDate:[{}], CancelDate:[{}]", new Object[]{reg.getRegDate(), reg.getRegExpiry(), reg.getRegCancel()});
		Integer nextSeqNo = 1;
		
		Reg latestReg = regDao.findLatestBySeafarerId(seafarerId);
		if(latestReg!=null){
			logger.info("Latest Reg's seqNo:{]", latestReg.getSeqNo());
			nextSeqNo = latestReg.getSeqNo() + 1;
		}
		logger.info("Next SeqNo:[{}]", new Object[]{nextSeqNo});
		reg.setSeqNo(nextSeqNo);
		Reg newReg = regDao.save(reg);
		return newReg;
	}
	
	
	@Override
	public PreviousSerb reIssueSerb(String seafarerId, String newSerbNo, Date serbDate){
		Seafarer s = seafarerDao.findById(seafarerId);
		s.setSerbNo(newSerbNo);
		s.setSerbDate(serbDate);
		return reIssueSerb(s);
	}
	@Override
	public PreviousSerb reIssueSerb(Seafarer currentSeafarer){
		String seafarerId = currentSeafarer.getId();
		String serbNo = currentSeafarer.getSerbNo();  // passed from UI, means it is update SERB NO.
		logger.info("#Re Issue SERB, SeafarerID:[{}], SerbNO:[{}]", new Object[]{seafarerId, serbNo});
		
		Integer nextSeqNo = 1;
		Date serbDate = currentSeafarer.getSerbDate(); // TODO how to handle if it always Null even not set in Reg.
		PreviousSerb latestPS = previousSerbDao.findLatestBySeafarerId(seafarerId);
		if(latestPS!=null){
			logger.info("Latest PreviousSerb seqNo:{]", nextSeqNo);
			nextSeqNo = latestPS.getSeqNo() + 1;
		}
	
		Reg latestReg = regDao.findLatestBySeafarerId(seafarerId);
		if(latestReg!=null){
			serbDate = latestReg.getRegDate();
			logger.info("Latest Reg's regDate:[{}]", serbDate);
		} 
		logger.info("Next SeqNo:{}, and SerbDate is:{}", new Object[]{nextSeqNo, serbDate});
		PreviousSerb newPs = new PreviousSerb();
		newPs.setSeafarerId(seafarerId);
		newPs.setSeqNo(nextSeqNo);
		newPs.setSerbNo(serbNo);
		newPs.setSerbDate(serbDate);
		PreviousSerb dbPreviousSerb = previousSerbDao.save(newPs);
		
		Seafarer s = seafarerDao.save(currentSeafarer);
		dbPreviousSerb.setSeafarer(s);
		return dbPreviousSerb;
	}


	@Override
	public Crew add(Crew crew) {
		String vesselId = crew.getVesselId();
		String coverYymm = crew.getCoverYymm();
		logger.info("#addCrew, Vessel ID:[{}], CoverYYMM:[{}", new Object[]{vesselId, coverYymm});
		List<Crew> crewList = crewDao.findByVesselIdCover(vesselId, coverYymm);
		Integer refNo = crewList.size()+1;
		crew.setReferenceNo(refNo);
		Crew newCrew = crewDao.save(crew);
		newCrew.getNationality();
		newCrew.getRank();
		return newCrew;
	}


	@Override
	public AbstractPersistentEntity save(AbstractPersistentEntity entity) {
		try {
			if(entity instanceof Seafarer){
				// TODO
				Seafarer s = (Seafarer)entity;
				s.setSeqNo(0);
				return super.save(entity);
			}
			Field seafarerIdfield = FieldUtils.getField(entity.getClass(), "seafarerId", true);
			String seafarerId = (String)seafarerIdfield.get(entity);
			logger.info("#Add to SeafarerID:[{}]", new Object[]{seafarerId});

			
			Field seqNoField = FieldUtils.getField(entity.getClass(), "seqNo", true);
			Integer seqNo = (Integer)seqNoField.get(entity);
			IBaseDao  baseDao = getDao(entity.getClass());
			if(seqNo==null){
				Integer nextSeqNo = Integer.valueOf(1);
				AbstractPersistentEntity latestEntity = baseDao.findLatestBySeafarerId(seafarerId);
				if(latestEntity!=null){
					Integer latestSeqNo = (Integer)seqNoField.get(latestEntity);
					logger.info("Latest seqNo:{]", latestSeqNo);
					nextSeqNo = latestSeqNo + 1;
				}
				logger.info("Next SeqNo:[{}]", new Object[]{nextSeqNo});
				seqNoField.set(entity, nextSeqNo);
			}
			AbstractPersistentEntity newEntity = baseDao.save(entity);
			return newEntity;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}


	@Override
	public List<Crew> getByVesselIdCoverYymm(String vesselId, String coverYymm) {
		// TODO Auto-generated method stub
		List<Crew> crewList = crewDao.findByVesselIdCover(vesselId, coverYymm);
		return crewList;
	}
	
	
	
}
