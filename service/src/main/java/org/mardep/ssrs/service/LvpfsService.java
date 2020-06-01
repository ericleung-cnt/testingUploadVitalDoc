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

import javax.transaction.Transactional;

import org.mardep.ssrs.dao.sr.IRegMasterDao;
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
			map.put("gt", decimal.format(rm.getGrossTon())); // decimal 4 1
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
				URLConnection _conn = url.openConnection();
				HttpURLConnection con = (HttpURLConnection) _conn;
				con.setRequestMethod("POST");
				con.setRequestProperty("Content-Type", "application/json;charset=utf8");
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
					sent = true;
					String string = response.toString();
					Boolean success = (Boolean) mapper.readValue(string, Map.class).get("success");
					if (!success) {
						String failMsg = System.getProperty("LvpfsService.FailMsg");
						logger.info("Send LVPFS failure {} {} ", failMsg, jsonInputString + "<br> " + string);
						if (failMsg != null) {
							mail.send(failMsg, "Send LVPFS failure", jsonInputString + "<br> " + string);
						}
					}
					rmDao.logLvpfs(jsonInputString, string);
				}
			} catch (JsonParseException e) {
				rmDao.logLvpfs(jsonInputString, e);
			} catch (IOException ex) {
				if (retry == maxRetry) {
					rmDao.logLvpfs(jsonInputString, ex);
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
			}
		}
	}

	public static String format(Date updatedDate) {
		if (updatedDate == null) return null;
		return new SimpleDateFormat(timeFormat).format(updatedDate);
	}

}
