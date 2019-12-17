package org.mardep.ssrs.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.mardep.ssrs.dao.codetable.IOperationTypeDao;
import org.mardep.ssrs.dao.codetable.IShipSubTypeDao;
import org.mardep.ssrs.dao.codetable.IShipTypeDao;
import org.mardep.ssrs.dao.sr.IApplDetailDao;
import org.mardep.ssrs.dao.sr.IBuilderMakerDao;
import org.mardep.ssrs.dao.sr.IOwnerDao;
import org.mardep.ssrs.dao.sr.IRegMasterDao;
import org.mardep.ssrs.dao.sr.IRepresentativeDao;
import org.mardep.ssrs.domain.codetable.OperationType;
import org.mardep.ssrs.domain.codetable.ShipSubType;
import org.mardep.ssrs.domain.codetable.ShipSubTypePK;
import org.mardep.ssrs.domain.codetable.ShipType;
import org.mardep.ssrs.domain.sr.ApplDetail;
import org.mardep.ssrs.domain.sr.BuilderMaker;
import org.mardep.ssrs.domain.sr.Owner;
import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.domain.sr.Representative;
import org.mardep.ssrs.domain.sr.SrEntityListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Transactional
public class FsqcService extends AbstractService implements IFsqcService {

	@Autowired
	IRegMasterDao rmDao;

	@Autowired
	IShipTypeDao stDao;

	@Autowired
	IShipSubTypeDao ssDao;

	@Autowired
	IOwnerDao ownerDao;
	@Autowired
	IRepresentativeDao rpDao;
	@Autowired
	IBuilderMakerDao bmDao;
	@Autowired
	IApplDetailDao adDao;
	@Autowired
	IOperationTypeDao otDao;

	public void send(int operation, String applNo)
			throws JsonProcessingException {

		HashMap<Object, Object> map = new HashMap<>();
		ApplDetail ad = null;
		Representative rp = null;
		RegMaster rm = rmDao.findById(applNo);
		map.put("imoNo", rm.getImoNo());
		map.put("regDate", rm.getRegDate());
		map.put("offNo", rm.getOffNo());
		map.put("callSign", rm.getOffNo());
		map.put("regName", rm.getRegName());
		map.put("grossTon", rm.getGrossTon());
		map.put("regNetTon", rm.getRegNetTon());
		map.put("length", rm.getLength());
		map.put("breadth", rm.getBreadth());
		map.put("depth", rm.getDepth());
		map.put("estSpeed", rm.getEstSpeed());
		map.put("buildDate", rm.getBuildDate());
		map.put("regStatus", rm.getRegStatus());
		map.put("regCName", rm.getRegChiName());
		map.put("deregTime", rm.getDeRegTime());
		map.put("engPower", rm.getEngPower());

//		 2019/09/11 10:15:47 advised by Jacky Leong, pls use these tag names
//		 - Operation Type Code: operTypeCode (get from Reg_Masters)
//		 - Survey Ship Type: surveyShipType (get from Reg_Masters)

		map.put("operTypeCode", rm.getOperationTypeCode());
		map.put("surveyShipType", rm.getSurveyShipType());

		if (rm.getShipTypeCode() != null) {
			ShipType type = stDao.findById(rm.getShipTypeCode());
			if (type != null) {
				map.put("stDesc", type.getStDesc());
			}
			if (rm.getShipSubtypeCode() != null) {
				ShipSubTypePK id = new ShipSubTypePK(rm.getShipTypeCode(), rm.getShipSubtypeCode());
				ShipSubType subtype = ssDao.findById(id);
				if (subtype != null) {
					map.put("ssDesc", subtype.getSsDesc());
				}
			}
		}
		if (rm.getOperationTypeCode() != null) {
			OperationType ot = otDao.findById(rm.getOperationTypeCode());
			if (ot != null) {
				map.put("operType", ot.getOtDesc());
//		 - Operation Type Description: otDesc (get from Operations)
				map.put("otDesc", ot.getOtDesc());
			}
		}
		if (ad == null) {
			ad = adDao.findById(applNo);
		}
		if (ad != null) {
			map.put("prevPort", ad.getPrevPort());
			map.put("cs", ad.getCs1ClassSocCode());
		}

		List<Owner> owners = ownerDao.findByApplId(applNo);
		String ownerName = "";
		String ownerCr = "";
		for (Owner owner : owners) {
			if (Owner.TYPE_DEMISE.equals(owner.getType())) {
				map.put("demiseName", owner.getName());
				map.put("demiseCR", owner.getOverseaCert() != null ? owner.getOverseaCert() : owner.getIncortCert());
			} else {
				ownerName += owner.getName() + ",";
				ownerCr += (owner.getOverseaCert() != null ? owner.getOverseaCert() : owner.getIncortCert()) + ",";
			}
		}
		if (ownerName.length() > 1) {
			ownerName = ownerName.substring(0, ownerName.length() - 1);
		}
		if (ownerCr.length() > 1) {
			ownerCr = ownerCr.substring(0, ownerCr.length() - 1);
		}
		map.put("ownerNames", ownerName);
		map.put("ownerCR", ownerCr);

//		 2019/09/11 17:43:55
//		Owner, Demise Charterer, RP each have their CR Number
//		For owner, its value should either be incorporate cert or oversea cert.
//		For demise charterer, the save as owner
//		For RP, its value should be in incorporate cert.
//		As all of them will be used in FSQC, suggest to include them all, and using ownerCR, demiseCR, rpCR respectively.

		BuilderMaker bmCriteria = new BuilderMaker();
		bmCriteria.setApplNo(applNo);
		String builders = bmDao.findNames(applNo);
		map.put("builderNames", builders);
		if (rp == null) {
			rp = rpDao.findById(applNo);
		}
		if (rp != null) {
			map.put("repName", rp.getName());
			map.put("rpCR", rp.getIncorpCert());
		}
		String jsonInputString = new ObjectMapper().writeValueAsString(map);
		logger.info("callback operation obj {}", jsonInputString);
		try {
			String key = "ShipRegService.pushUrl.add";
			switch (operation) {
			case SrEntityListener.OPER_UPDATE:
				key = "ShipRegService.pushUrl.update";
				break;
			case SrEntityListener.OPER_REMOVE:
				key = "ShipRegService.pushUrl.delete";
				break;
			}
			URL url = new URL(System.getProperty(key, "http://localhost:8080/ssrs/"));
			URLConnection _conn = url.openConnection();
			if (_conn instanceof HttpURLConnection) {
				HttpURLConnection con = (HttpURLConnection) _conn;
				con.setRequestMethod("POST");
				con.setRequestProperty("Content-Type", "application/json; utf-8");
				con.setRequestProperty("Accept", "application/json");
				con.setDoOutput(true);
				try(OutputStream os = con.getOutputStream()) {
					byte[] input = jsonInputString.getBytes("utf-8");
					os.write(input, 0, input.length);
				}
				try(BufferedReader br = new BufferedReader(
						new InputStreamReader(con.getInputStream(), "utf-8"))) {
					StringBuilder response = new StringBuilder();
					String responseLine = null;
					while ((responseLine = br.readLine()) != null) {
						response.append(responseLine.trim());
					}
					rmDao.logFsqc(jsonInputString, response.toString());
				}
			}
		} catch (MalformedURLException e) {
			rmDao.logFsqc(jsonInputString, e);
		} catch (IOException e) {
			rmDao.logFsqc(jsonInputString, e);
		}
	}

	}
