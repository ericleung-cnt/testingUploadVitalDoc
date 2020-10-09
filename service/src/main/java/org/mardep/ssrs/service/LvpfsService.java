package org.mardep.ssrs.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.math.BigDecimal;

import javax.transaction.Transactional;

import org.mardep.ssrs.dao.sr.IRegMasterDao;
import org.mardep.ssrs.domain.lvpfs.VmssUpdateVesselParticularReply;
import org.mardep.ssrs.domain.sr.RegMaster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Transactional
public class LvpfsService extends AbstractService implements ILvpfsService {

	private static String timeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS";
	
	@Autowired
	IRegMasterDao rmDao;

	URL url;
	int maxRetry = Integer.parseInt(System.getProperty("LvpfsService.maxRetry", "3"));

	@Autowired
	MailService mail;

	private ObjectMapper mapper = new ObjectMapper();

	public LvpfsService() throws MalformedURLException {
		url = new URL(System.getProperty("LvpfsService.pushUrl.update", "http://localhost:8080/ssrs/"));
	}

	public static void write(RegMaster rm, Map<String, Object> map) {
		map.put("vesselNameEng", rm.getRegName());
		map.put("vesselNameChi", rm.getRegChiName());
		map.put("callSign", rm.getCallSign());
		map.put("imoNo", rm.getImoNo());
		map.put("vesselFlag", "HKG");
		
		map.put("vesselType", rm.getShipTypeCode());
		map.put("vesselSubType", rm.getShipSubtypeCode());
		
		String loa = "";
		String loaft = "";
		String b = "";
		String bft= "";

		DecimalFormat decimal = new DecimalFormat("#.0");
		if (rm.getDimUnit() != null) {
			switch (rm.getDimUnit()) {
			case "F":
				if (rm.getLength() != null) {
					loaft = decimal.format(rm.getLength());
				}
				if (rm.getBreadth() != null) {
					bft = decimal.format(rm.getBreadth());
				}
				break;
			case "M":
			case "B":
				if (rm.getLength() != null) {
					loa = decimal.format(rm.getLength());
				}
				if (rm.getBreadth() != null) {
					b = decimal.format(rm.getBreadth());
				}
				break;
			} 
		}
		map.put("loa", loa);
		map.put("LoaFt", loaft);
		map.put("breadth", b);
		map.put("breadthFt", bft);
		
		if (rm.getGrossTon() != null) {
			//map.put("gt", decimal.format(rm.getGrossTon())); // decimal 4 1
			//BigDecimal gt = rm.getGrossTon();
			String gtStr = Integer.toString(rm.getGrossTon().intValue());
			map.put("gt", gtStr);
		} else {
			map.put("gt", null); 
		}
		if (rm.getRegNetTon() != null) {
			map.put("nt", decimal.format(rm.getRegNetTon())); // decimal 4 1
		} else {
			map.put("nt", null); 
		}
		map.put("mmsi", "");
		SimpleDateFormat df = new SimpleDateFormat(timeFormat);
		if (rm.getRegDate() != null) {
			map.put("registrationDate", df.format(rm.getRegDate()));
		} else {
			map.put("registrationDate", null);
		}
		
		if (rm.getDeRegTime() != null) {
			map.put("deregistrationDate", df.format(rm.getDeRegTime()));
		} else {
			map.put("deregistrationDate", null);
		}
		map.put("modifiedDate", df.format(rm.getUpdatedDate()));
	}

	public void send(int operation, String applNo)
			throws JsonProcessingException {
		logger.info("sending {} to lvpfs url {} {} {} {}", applNo, url.getProtocol(), url.getHost(), url.getFile(), url.getPort());
		RegMaster rm = rmDao.findById(applNo);
		LinkedHashMap<String,Object> linkedHashMap = new LinkedHashMap<String, Object>();
		linkedHashMap.put("shipID", applNo);
		write(rm, linkedHashMap);
		
		String jsonInputString = mapper.writeValueAsString(linkedHashMap);
		logger.info("callback operation obj {}", jsonInputString);
		int retry = 0;
		boolean sent = false;
		while (!sent && retry <= maxRetry) {
			try {
				//logger.info("lvpfs open connection start");
				URLConnection _conn = url.openConnection();
				//logger.info("lvpfs open connection end");
				HttpURLConnection con = (HttpURLConnection) _conn;
				con.setRequestMethod("POST");
				con.setRequestProperty("Content-Type", "application/json;charset=utf8");
				con.setRequestProperty("Accept", "application/json");
				con.setDoOutput(true);
				//logger.info("lvpfs ready to get output stream");
				try(OutputStream os = con.getOutputStream()) {
					byte[] input = jsonInputString.getBytes("utf-8");
					//logger.info("lvpfs ready to write output stream");
					os.write(input, 0, input.length);
				}
				try(BufferedReader br = new BufferedReader(
						new InputStreamReader(con.getInputStream(), "utf-8"))) {
					StringBuilder response = new StringBuilder();
					String responseLine = null;
					while ((responseLine = br.readLine()) != null) {
						response.append(responseLine.trim());
					}
					sent = true;
					String respStr = response.toString();
					//logger.info("lvpfs response process {}", respStr);
					VmssUpdateVesselParticularReply webReply = mapper.readValue(respStr, VmssUpdateVesselParticularReply.class);
					//logger.info("lvpf got shipId {}", webReply.getShipId());
					if (respStr.isEmpty()) {
						String failMsg = System.getProperty("LvpfsService.FailMsg", "lvpfs fail msg not in system property");
						logger.info("Send LVPFS failure {} {} ", failMsg, jsonInputString + "<br> " + respStr);
						if (failMsg != null) {
							logger.info("lvpfs going to send mail");
							mail.send(failMsg, "Send LVPFS failure", jsonInputString + "<br> " + respStr);
						}
					}
					//rmDao.logLvpfs(jsonInputString, string);
					logger.info("lvpfs going to vmsslog");
					vmssLog(jsonInputString, respStr);
					logger.error("LVPFS send {} - {}", jsonInputString, respStr);
				}
			} catch (JsonParseException e) {
				vmssLog(jsonInputString, e.getMessage());
				logger.error("LVPFS send {} - {}", jsonInputString, e.getMessage());
			} catch (IOException ex) {
				if (retry == maxRetry) {
					//rmDao.logLvpfs(jsonInputString, ex);
					vmssLog(jsonInputString, ex.getMessage());
					String failMsg = System.getProperty("LvpfsService.FailMsg");
					logger.info("Send LVPFS IO failure {} {} {} {}", url, ex.getMessage(), failMsg, jsonInputString);
					if (failMsg != null) {
						mail.send(failMsg, "Send LVPFS IO failure", jsonInputString);
					}
					break;
				} else {
					retry++;
					logger.info("retry " + retry + " " + jsonInputString);
				}
			} catch (Exception ex) {
				logger.error("Send LVPFS IO failure {} {} {}", url, ex, jsonInputString);
			}
		}
	}

	public static String format(Date updatedDate) {
		if (updatedDate == null) return null;
		return new SimpleDateFormat(timeFormat).format(updatedDate);
	}

	@Override
	public void vmssLog(String jsonStr, String vmssReply)
	{
		try {
			rmDao.logLvpfsResponse(jsonStr, vmssReply);		
		} catch ( Exception ex) {
			logger.error(ex.getMessage());
		}
	}

}
