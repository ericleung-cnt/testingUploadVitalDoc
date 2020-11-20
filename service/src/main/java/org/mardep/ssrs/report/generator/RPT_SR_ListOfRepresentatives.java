package org.mardep.ssrs.report.generator;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mardep.ssrs.domain.srReport.RpShipOwner;
import org.mardep.ssrs.report.IReportGenerator;
import org.mardep.ssrs.srReportService.IListOfRpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("RPT_SR_ListOfRepresentatives")
@Transactional
public class RPT_SR_ListOfRepresentatives extends AbstractReportGenerator implements IReportGenerator {
	
	private final String PARAM_FROM_RP = "fromRP";
	private final String PARAM_TO_RP = "toRP";
	private final String PARAM_FROM_TO_RP = "fromToRP";
	
	private final String FIELD_RP_NAME = "rpName";
	private final String FIELD_RP_ADDR1 = "rpAddr1";
	private final String FIELD_RP_ADDR2 = "rpAddr2";
	private final String FIELD_RP_ADDR3 = "rpAddr3";
	private final String FIELD_OWNER_NAME = "ownerName";
	private final String FIELD_OWNER_ADDR1 = "ownerAddr1";
	private final String FIELD_OWNER_ADDR2 = "ownerAddr2";
	private final String FIELD_OWNER_ADDR3 = "ownerAddr3";
	private final String FIELD_SHIPNAME_ENG = "shipNameEng";
	private final String FIELD_SHIPNAME_CHI = "shipNameChi";
	private final String FIELD_TEL = "tel";
	private final String FIELD_FAX = "fax";
	private final String FIELD_TELEX = "telex";
	private final String FIELD_GROSS_TON = "grossTon";
	private final String FIELD_NET_TON = "netTon";
	private final String FIELD_OFFICIAL_NO = "officialNo";
	private final String FIELD_CALL_SIGN = "callSign" ;
	private final String FIELD_SURVEY_SHIP_TYPE = "surveyShipType";
	
	@Autowired
	IListOfRpService listOfRpService;
	
	@Override
	public String getReportFileName() {
		// TODO Auto-generated method stub
		return "RPT_SR_ListOfRepresentatives.jrxml";
	}

	@Override
	public byte[] generate(Map<String, Object> inputParams) throws Exception {
		Date forDate = (Date) inputParams.get("forDate");
	
		List<RpShipOwner> rpList = getReportContent(forDate);
		
		Map<String, Object> params = setupParams(rpList);
		
		List<Map<String, Object>> rows = setupRows(rpList);

		return jasperReportService.generateReport(getReportFileName(), rows, params);
	}
	
	private List<RpShipOwner> getReportContent(Date forDate){
		List<RpShipOwner> resultList = listOfRpService.getRpShipOwner(forDate);
		return resultList;
	}
	
	private Map<String, Object> setupParams(List<RpShipOwner> rpList){
		Map<String, Object> params = new HashMap<>();
		RpShipOwner firstRp = rpList.get(0);
		RpShipOwner lastRp = rpList.get(rpList.size()-1);
		//params.put(PARAM_FROM_RP, firstRp.getRpName());
		//params.put(PARAM_TO_RP, lastRp.getRpName());
		String fromToRP = "FROM   " + firstRp.getRpName() + "\t\t" + "TO   " + lastRp.getRpName();
		params.put(PARAM_FROM_TO_RP, fromToRP);
		return params;
	}
	
	private List<Map<String, Object>> setupRows(List<RpShipOwner> rpList) {
		List<Map<String, Object>> rows = new ArrayList<>();
		for (RpShipOwner rp : rpList) {
			Map<String, Object> row = new HashMap<>();
			
//			row.put(FIELD_RP_NAME, rp.getRpName());
//			row.put(FIELD_RP_ADDR1, rp.getRpAddr1());
//			row.put(FIELD_RP_ADDR2, rp.getRpAddr2());
//			row.put(FIELD_RP_ADDR3, rp.getRpAddr3());
			row.put(FIELD_RP_NAME, prepareRpInfo(rp));
//			row.put(FIELD_OWNER_NAME, rp.getOwnerName());
//			row.put(FIELD_OWNER_ADDR1, rp.getOwnerAddr1());
//			row.put(FIELD_OWNER_ADDR2, rp.getOwnerAddr2());
//			row.put(FIELD_OWNER_ADDR3, rp.getOwnerAddr3());
			row.put(FIELD_OWNER_NAME, prepareOwnerInfo(rp));
			row.put(FIELD_SHIPNAME_ENG, rp.getShipNameEng());
			row.put(FIELD_SHIPNAME_CHI, rp.getShipNameChi());
			row.put(FIELD_TEL, rp.getTel());
			row.put(FIELD_FAX, rp.getFax());
			row.put(FIELD_TELEX, rp.getTelex());
			row.put(FIELD_GROSS_TON, new DecimalFormat("#,###.00").format(isNull(rp.getGrossTon(), BigDecimal.ZERO)));
			row.put(FIELD_NET_TON, new DecimalFormat("#,###.00").format(isNull(rp.getNetTon(), BigDecimal.ZERO)));
			row.put(FIELD_OFFICIAL_NO, rp.getOfficialNo());
			row.put(FIELD_CALL_SIGN, rp.getCallSign());
			row.put(FIELD_SURVEY_SHIP_TYPE, rp.getSurveyShipType());

			rows.add(row);			
		}
		return rows;
	}
	
	private String prepareRpInfo(RpShipOwner rp) {
		StringBuilder rpInfo = new StringBuilder();
		rpInfo.append(rp.getRpName());
		rpInfo.append("\n");
		rpInfo.append(rp.getRpAddr1());
		if (rp.getRpAddr2()!=null && !rp.getRpAddr2().isEmpty()) {
			rpInfo.append("\n");
			rpInfo.append(rp.getRpAddr2());
		}
		if (rp.getRpAddr3()!=null && !rp.getRpAddr3().isEmpty()) {
			rpInfo.append("\n");
			rpInfo.append(rp.getRpAddr3());
		}
		return rpInfo.toString();
	}
	
	private String prepareOwnerInfo(RpShipOwner rp) {
		StringBuilder ownerInfo = new StringBuilder();
		ownerInfo.append(rp.getOwnerName());
		ownerInfo.append("\n");
		ownerInfo.append(rp.getOwnerAddr1());
		if (rp.getOwnerAddr2()!=null && !rp.getOwnerAddr2().isEmpty()) {
			ownerInfo.append("\n");
			ownerInfo.append(rp.getOwnerAddr2());
		}
		if (rp.getOwnerAddr3()!=null && !rp.getOwnerAddr3().isEmpty()) {
			ownerInfo.append("\n");
			ownerInfo.append(rp.getOwnerAddr3());
		}
		return ownerInfo.toString();
	}
	
	private Object isNull(Object value, Object replacement) {
		return value != null ? value : replacement;
	}

}
