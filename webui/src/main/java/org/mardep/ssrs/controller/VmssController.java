package org.mardep.ssrs.controller;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.mardep.ssrs.dao.codetable.IShipSubTypeDao;
import org.mardep.ssrs.dao.codetable.IShipTypeDao;
import org.mardep.ssrs.dao.sr.IRegMasterDao;
import org.mardep.ssrs.domain.codetable.ShipSubType;
import org.mardep.ssrs.domain.codetable.ShipType;
import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.service.ILvpfsService;
import org.mardep.ssrs.service.LvpfsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class VmssController {
	
	protected Logger logger = LoggerFactory.getLogger(VmssController.class);
	@Autowired
	IShipTypeDao stDao;
	
	@Autowired
	IShipSubTypeDao ssDao;
	
	@Autowired
	IRegMasterDao rmDao;
	
	@Autowired
	ILvpfsService lvpfsSvc;
	
	@RequestMapping("/getCodeDescription/{type}/{code}")
	public ResponseEntity<Map<String, String>> getCode(@PathVariable String type, @PathVariable String code) {
		logger.info("type=" + type + ",code=" + code);
		Map<String, String> map = new LinkedHashMap<String, String>();
		if (type != null) {
			switch(type) { 
			case "vesselType":
				ShipType st = stDao.findById(code);
				if (st != null) {
					map.put("descriptionEng", st.getStDesc());
					map.put("descriptionChi", "");
					map.put("modifiedDate", LvpfsService.format(st.getUpdatedDate()));
				} else {
					map.put("Message", "No Record");
				}
				break;
			case "vesselSubType":
				if (code != null) {
					ShipSubType entity = new ShipSubType();
					entity.setShipSubTypeCode(code);
					List<ShipSubType> list = ssDao.findByCriteria(entity);
					if (!list.isEmpty()) {
						for (ShipSubType ss : list) {
							if (code.equals(ss.getShipSubTypeCode())) {
								map.put("descriptionEng", ss.getSsDesc());
								map.put("descriptionChi", "");
								map.put("modifiedDate", LvpfsService.format(ss.getUpdatedDate()));
								break;
							}
							if (map.isEmpty()) {
								map.put("Message", "No Record");
							}
						}
					} else {
						map.put("Message", "No Record");
					}
				} else {
					map.put("Message", "ERROR");
				}
				break;
			default:
				map.put("Message", "Unknown type: " + type);
				break;
			}
		} else {
			map.put("Message", "Missing parameter 'type'");
		}
		vmssLogMapStr(map);
		ResponseEntity<Map<String, String>> re = new ResponseEntity<Map<String, String>>(map, HttpStatus.OK);
		return re;
	}

	@RequestMapping("/getVesselParticular/{type}/{code}")
	public ResponseEntity<Map<String, Object>> getVessel(@PathVariable String type, @PathVariable String code) {
		logger.info("type=" + type + ",code=" + code);
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		if (code == null || type == null) {
			map.put("Message", "Missing parameter 'code' or 'type'");
		} else {
			RegMaster criteria = new RegMaster();
			switch (type) {
			case "IMO":
				criteria.setImoNo(code);
				break;
			case "CALLSIGN":
				criteria.setCallSign(code);
				break;
			case "FILENO":
				break;
			default:
				map.put("Message", "Unknown type: " + type);
				break;
			}
			if (map.isEmpty()) {
				RegMaster rm = null;
				switch (type) {
				case "IMO": {
					List<RegMaster> list = rmDao.findByCriteria(criteria);
					sort(list);
					for (RegMaster item : list) {
						if (item.getImoNo().equals(code)) {
							rm = item;
							break;
						}
					}
					break;
				}
				case "CALLSIGN": {
					List<RegMaster> list = rmDao.findByCriteria(criteria);
					sort(list);
					for (RegMaster item : list) {
						if (item.getCallSign().equals(code)) {
							rm = item;
							break;
						}
					}
					break;
				}
				case "FILENO":
					if (code.length() > 4) {
						rm = rmDao.findById(code.replaceAll("_", "/"));
					}
					break;
				}
				if (rm == null) {
					map.put("Message", "No Record");
				} else {
					LvpfsService.write(rm, map);
				}
			}
		}
		vmssLogMapObj(map);
		ResponseEntity<Map<String, Object>> re = new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
		return re;
	}

	private void sort(List<RegMaster> list) {
		list.sort((a,b) -> {
			Date aRegDate = a.getRegDate();
			Date bRegDate = b.getRegDate();
			if (aRegDate == null) {
				aRegDate = new Date(0);
			}
			if (bRegDate == null) {
				bRegDate = new Date(0);
			}
			return -aRegDate.compareTo(bRegDate);
		});
	}

	private void vmssLogMapStr(Map<String, String> map){
		try {
			ObjectMapper mapper = new ObjectMapper();
			String jsonStr = mapper.writeValueAsString(map);
			lvpfsSvc.vmssLog(jsonStr, "--");	
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}
	}

	private void vmssLogMapObj(Map<String, Object> map){
		try {
			ObjectMapper mapper = new ObjectMapper();
			String jsonStr = mapper.writeValueAsString(map);
			lvpfsSvc.vmssLog(jsonStr, "--");	
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}
	}

}
