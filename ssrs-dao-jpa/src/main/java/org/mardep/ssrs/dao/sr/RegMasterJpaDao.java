package org.mardep.ssrs.dao.sr;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.Query;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.codetable.FeeCode;
import org.mardep.ssrs.domain.codetable.ReasonCode;
import org.mardep.ssrs.domain.codetable.ShipSubType;
import org.mardep.ssrs.domain.codetable.ShipType;
import org.mardep.ssrs.domain.dn.DemandNoteItem;
import org.mardep.ssrs.domain.sr.Owner;
import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.domain.sr.Transaction;
import org.mardep.ssrs.domain.srReport.RegisteredShip;
import org.mardep.ssrs.domain.srReport.RegisteredShipOwner;
import org.mardep.ssrs.domain.srReport.SummaryOfShipsByShipType;
import org.springframework.stereotype.Repository;

@Repository
public class RegMasterJpaDao extends AbstractJpaDao<RegMaster, String> implements IRegMasterDao {
	private FeeCode transcriptAppFeeCode;

	public RegMasterJpaDao() {
		criteriaList.add(new PredicateCriteria("representativeName", PredicateType.LIKE_IGNORE_CASE));
		criteriaList.add(new PredicateCriteria("regName", PredicateType.STARTSWITH));
		criteriaList.add(new PredicateCriteria("offNo", PredicateType.EQUAL));
	}
	
	@Override
	public List<RegMaster> searchUsedName(String field, List<String> values) {
		Query query = em.createQuery("select r from RegMaster r where r."
				+ field
				+ " in :names and r.regStatus is not null and r.regStatus in ('A', 'R')");
		query.setParameter("names", values);
		return query.getResultList();
	}
	@Override
	public String maxApplNo(int year) {
		Query query = em.createQuery("select max(applNo) from RegMaster r where r.applNo like '"
				+ year
				+ "/%'");
		List<?> list = query.getResultList();
		String applNo = (String) list.get(0);
		return applNo;
	}
	@Override
	public String maxOffNo() {
		Query query = em.createQuery("select max(offNo) from RegMaster rm where rm.offNo like 'HK-%'");
		List<?> resultList = query.getResultList();
		return resultList.size() == 0 ? null : (String) resultList.get(0);
	}
	@Override
	public String nextCallSign() {
		Query query = em.createNativeQuery("select call_sign FROM REG_MASTERS where CALL_SIGN is not null and " +
				"(REG_STATUS is null or REG_STATUS in ('A', 'R')) and call_sign >= ( " +
				"select top 1 call_sign from REG_MASTERS where CALL_SIGN is not null and " +
				"(REG_STATUS is null or REG_STATUS in ('A', 'R'))  order by APPL_NO desc ) order by call_sign ");
		List<String> resultList = query.getResultList();
		Function<String, Integer> signScore = new Function<String, Integer>() {
			public Integer apply(String callSign) {
				if (callSign.length() < 5) {
					callSign += "     ";
				}
				return (callSign.charAt(1)- 65)  * 8 * 26  * 26 +
						(callSign.charAt(2)- 65)  * 8 * 26 +
						(callSign.charAt(3)- 65)  * 8  +
						(callSign.charAt(4)- 50);
			}
		};
		int score = 0;
		if (resultList.size() == 0) {
		} else if (resultList.size() == 1) {
			score = signScore.apply(resultList.get(0)) + 1;
		} else {
			String lastSign = resultList.get(0);
			for (int i = 1; i < resultList.size(); i++) {
				Integer last = signScore.apply(lastSign);
				Integer next = signScore.apply(resultList.get(i));
				if (next - last > 1) {
					score = last + 1;
					break;
				} else {
					score = last + 1;
					lastSign = resultList.get(i);
				}
			}
		}
		String callSign = "V" + (char) ((score / 8 / 26 / 26) + 65) + (char) ((score / 8 / 26) % 26 + 65)  + (char) ((score / 8) % 26 + 65) + (char) ((score % 8) + 50);
		return callSign ;
	}

	@Override
	public List<Map<String, Object>> getRegistrationType(Date reportDate) {
		String sql = "select case \n"+
				"when regtypeid = 1 then 'PROVISIONAL WITHOUT DEMISE CHARTERER'\n"+
				"when regtypeid = 2 then 'PROVISIONAL WITH DEMISE CHARTERER' \n"+
				"when regtypeid = 3 then 'INITIAL PERMANENT WITHOUT DEMISE CHARTERER'\n"+
				"when regtypeid = 4 then 'INITIAL PERMANENT WITH DEMISE CHARTERER' \n"+
				"when regtypeid = 5 then 'FROM PROVISIONAL TO PERMANENT WITHOUT DEMISE CHARTERER'\n"+
				"when regtypeid = 6 then 'FROM PROVISIONAL TO PERMANENT WITH DEMISE CHARTERER' \n"+
				"when regtypeid = 7 then 'RE-REGISTRATION OF TRANSITIONAL SHIP WITHOUT DEMISE CHARTERER'\n"+
				"when regtypeid = 8 then 'RE-REGISTRATION OF TRANSITIONAL SHIP WITH DEMISE CHARTERER' \n"+
				"when regtypeid = 9 then 'RE-REGISTRATION OF NON-TRANSITIONAL SHIP WITHOUT DEMISE CHARTERER' \n"+
				"when regtypeid = 10 then 'RE-REGISTRATION OF NON-TRANSITIONAL SHIP WITH DEMISE CHARTERER' \n"+
				"when regtypeid = 11 then 'REGISTRATION ANEW WITHOUT DEMISE CHARTERER' \n"+
				"when regtypeid = 12 then 'REGISTRATION ANEW WITH DEMISE CHARTERER' \n"+
				"when regtypeid = 13 then 'TRANSITIONAL SHIP' \n"+
				"end as regType, convert(decimal(20,2), isnull(t2.totalGrossTons, 0)) totalGrossTons, convert(decimal(20,2), isnull(t2.totalNetTons, 0)) totalNetTons, isnull(t2.noOfShip, 0) noOfShip\n"+
				"from (select top(13)  row_number() over (order by object_id) as regtypeid\n"+
				"from sys.columns) t0\n"+
				"left join \n"+
				"(select regType, sum(gross_ton) totalGrossTons, sum(reg_net_ton) totalNetTons, count(appl_no) noOfShip from (select appl_no, reg_net_ton, gross_ton, case\n"+
				"when TRANSIT_IND is null and reg_regn_type = 'X' and APPL_NO_SUF = 'F' and owners.rm_appl_no is null then 3 \n"+
				"when TRANSIT_IND is null and reg_regn_type = 'X' and APPL_NO_SUF = 'F' and owners.rm_appl_no is not null then 4 \n"+
				"when TRANSIT_IND is null and reg_regn_type = 'T' and owners.rm_appl_no is null then 5 \n"+
				"when TRANSIT_IND is null and reg_regn_type = 'T' and owners.rm_appl_no is not null then 6 \n"+
				"when TRANSIT_IND is null and reg_regn_type = 'O' and owners.rm_appl_no is null then 7\n"+
				"when TRANSIT_IND is null and reg_regn_type = 'O' and owners.rm_appl_no is not null then 8\n"+
				"when TRANSIT_IND is null and reg_regn_type = 'N' and owners.rm_appl_no is null then 9 \n"+
				"when TRANSIT_IND is null and reg_regn_type = 'N' and owners.rm_appl_no is not null then 10\n"+
				"when TRANSIT_IND is null and reg_regn_type = 'A' and owners.rm_appl_no is null then 11 \n"+
				"when TRANSIT_IND is null and reg_regn_type = 'A' and owners.rm_appl_no is not null then 12\n"+
				"when TRANSIT_IND is not null then 13\n"+
				"end as regType\n"+
				"from REG_MASTERS rm left join (select rm_appl_no from owners where owner_type = 'D' group by rm_appl_no) owners on owners.rm_appl_no = rm.appl_no\n"+
				"WHERE (TRANSIT_IND is null and (reg_regn_type in ('T','O','N','A') or  (reg_regn_type = 'X' and APPL_NO_SUF = 'F')) or TRANSIT_IND is not null) \n"+
				"and ((reg_status = 'D' and dereg_time > :reportDate) or reg_status = 'R') \n"+
				"and reg_date <= :reportDate\n"+
				"union\n"+
				"select appl_no, reg_net_ton, gross_ton, case\n"+
				"when owners.rm_appl_no is null then 1 else 2 end\n"+
				"from REG_MASTERS rm left join (select rm_appl_no from owners where owner_type = 'D' group by rm_appl_no) owners on owners.rm_appl_no = rm.appl_no\n"+
				"WHERE TRANSIT_IND is null and APPL_NO_SUF = 'P'\n"+
				"and ((reg_status = 'D' and dereg_time > :reportDate) or reg_status = 'R')\n"+
				"and reg_date <= :reportDate ) t1 group by regType) t2 on t2.regType = t0.regtypeid\n";

		Query query = em.createNativeQuery(sql);
		query.setParameter("reportDate", reportDate);
		List<Object[]> list = query.getResultList();

		List<Map<String, Object>> rows = new ArrayList<>();
		for (Object[] arr : list) {
			Map<String, Object> map = new HashMap<>();
			map.put("regType", arr[0]);
			map.put("totalGrossTons", arr[1]);
			map.put("totalNetTons",arr[2]);
			map.put("noOfShip", arr[3]);
			rows.add(map);
		}
		return rows;
	}

	@Override
	public List<Map<String, Object>> getBreakDownNoAndGrtOfShipsByType(Date reportDate) {
		List<Map<String, Object>> rows = new ArrayList<>();
		DecimalFormat format = new DecimalFormat("###,###");
		for (int i = 0; i < 16; i++) {
			Map<String, Object> row = new HashMap<>();
			// rangeOfTonnage
			String start = "       "+format.format(i * 10000 + 1);
			String end = i == 15 ? "  ABOVE":"       "+format.format((i + 1) * 10000);
			String rangeOfTonnage = start.substring(start.length() - 7) + " - " + end.substring(end.length() - 7);
			row.put("rangeOfTonnage", rangeOfTonnage);
			for (int j = 1; j < 7; j++) {
				row.put("joined" + j, 0);
				row.put("left" + j, 0);
			}
			rows.add(row);
		}
		Query query = em.createNativeQuery("select isnull(ton, 0) ton, convert(varchar(50), isnull(SS_ST_SHIP_TYPE_CODE, '-')) shiptype, count(*) joined, count(dereg_time) dereg from (select dereg_time, SS_ST_SHIP_TYPE_CODE, case when REG_NET_TON > 150001 then 16 else  floor((REG_NET_TON + 10000)/ 10000) end ton from REG_MASTERS where REG_DATE <= :reportDate) A group by ton, SS_ST_SHIP_TYPE_CODE ");
		query.setParameter("reportDate", reportDate);
		List<Object[]> list = query.getResultList();
		List<String> types = Arrays.asList(ShipType.CARGO_SHIP, ShipType.TANKER, ShipType.YACHT, ShipType.TUG, ShipType.PASSENGER_SHIP);
		for (Object[] row : list) {
			int ton = ((BigDecimal) row[0]).intValue() + 1;
			int type = types.indexOf(row[1]);
			int joined = (Integer) row[2];
			int left = (Integer) row[3];
			if (type == -1) {
				type = 5;
			}
			type++;
			rows.get(ton - 1).put("joined" + type, joined);
			rows.get(ton - 1).put("left" + type, left);
		}
		return rows;
	}

	private SummaryOfShipsByShipType findSummaryByShipCode(List<SummaryOfShipsByShipType> summaries, String shipCode, String shipDesc) {
		SummaryOfShipsByShipType entity;
		List<SummaryOfShipsByShipType> entities = summaries.parallelStream()
													.filter(item->item.getShipcode().equals(shipCode) && item.getShipdesc().equals(shipDesc))
													.collect(Collectors.toList());
		if (entities.size()>0) {
			entity = entities.get(0);
		} else {
			entity = new SummaryOfShipsByShipType();
			entity.setShipcode(shipCode);
			entity.setShipdesc(shipDesc);
			switch (shipCode) {
				case "CARGO SHIP":
					entity.setSubtotal("TOTAL OF CARGO SHIP :");
					break;
				case "PASSENGER SHIP":
					entity.setSubtotal("TOTAL OF PASSENGER SHIP :");
					break;
				case "TANKER":
					entity.setSubtotal("TOTAL OF TANKER :");
					break;
				case "TUG":
					entity.setSubtotal("TOTAL OF TUG :");
					break;
				case "YACHT":
					entity.setSubtotal("TOTAL OF YACHT :");
					break;
			}

			entity.setOgross(new BigDecimal("0.00"));
			entity.setOnet(new BigDecimal("0.00"));
			entity.setLgross(new BigDecimal("0.00"));
			entity.setLnet(new BigDecimal("0.00"));
			entity.setRgross(new BigDecimal("0.00"));
			entity.setRnet(new BigDecimal("0.00"));
			summaries.add(entity);
		}
		return entity;
	}

	private List<SummaryOfShipsByShipType> getSummaryOfShipsByShipTypeAsAtDate(Date asAtDate){
		try {
//			String sql = "select rm.APPL_NO, rm.GROSS_TON, rm.REG_NET_TON, rmh.GROSS_TON as history_gross_ton, rmh.REG_NET_TON as history_net_ton, " +
//						"(case when rmh.GROSS_TON is null then rm.GROSS_TON else rmh.GROSS_TON end) as real_gross_ton, " +
//						"(case when rmh.REG_NET_TON is null then rm.REG_NET_TON else rmh.REG_NET_TON end) as real_net_ton, " +
//						"rm.OT_OPER_TYPE_CODE, op.OT_DESC, " +
//						"rm.SS_ST_SHIP_TYPE_CODE, st.ST_DESC, " +
//						"rm.SS_SHIP_SUBTYPE_CODE, ss.SS_DESC from REG_MASTERS rm \n" +
//						"left join OPERATION_TYPES op on op.OPER_TYPE_CODE = rm.OT_OPER_TYPE_CODE \n" +
//						"left join SHIP_TYPES st on st.SHIP_TYPE_CODE = rm.SS_ST_SHIP_TYPE_CODE \n" +
//						"left join SHIP_SUBTYPES ss on ss.SHIP_SUBTYPE_CODE = rm.SS_SHIP_SUBTYPE_CODE \n" +
//						"left join (select ROW_NUMBER() over (partition by rm_appl_no order by rm_appl_no, at_ser_num desc) as rowNum, RM_APPL_NO, AT_SER_NUM from TRANSACTIONS \n" +
//						"	where txn_time <= :asAtDate) lr on lr.RM_APPL_NO = rm.appl_no and lr.rowNum = 1 \n" +
//						"left join REG_MASTERS_HIST rmh on rmh.TX_ID = lr.AT_SER_NUM \n" +
//						"where (rm.REG_STATUS = 'R' and rm.reg_date <= :asAtDate) or (rm.REG_STATUS = 'D' and rm.DEREG_TIME > :asAtDate) \n" +
//						"order by rm.APPL_NO";
			String sql = "select rm.APPL_NO, rm.gton1, rm.nton1, rm.history_gross_ton, rm.history_net_ton, \n" +
						 "rm.gross_ton, rm.net_ton, \n" +
						 "rm.OT_OPER_TYPE_CODE, rm.OT_DESC, \n" +
						 "rm.SS_ST_SHIP_TYPE_CODE, st.ST_DESC, \n" +
						 "ss.SHIP_SUBTYPE_CODE, ss.SS_DESC from SHIP_SUBTYPES ss \n" +
						 "inner join SHIP_TYPES st on st.SHIP_TYPE_CODE = ss.ST_SHIP_TYPE_CODE \n" +
						 "left join ( \n" +
							"select rm.APPL_NO, rm.GROSS_TON as gton1, rm.REG_NET_TON as nton1, \n"+
							"rmh.GROSS_TON as history_gross_ton, rmh.REG_NET_TON as history_net_ton, \n" +
								"(case when rmh.GROSS_TON is null then rm.GROSS_TON else rmh.GROSS_TON end) as gross_ton, \n" +
								"(case when rmh.REG_NET_TON is null then rm.REG_NET_TON else rmh.REG_NET_TON end) as net_ton, \n" +
								"rm.OT_OPER_TYPE_CODE, op.OT_DESC, rm.SS_ST_SHIP_TYPE_CODE, rm.SS_SHIP_SUBTYPE_CODE from REG_MASTERS rm \n" +
							"inner join OPERATION_TYPES op on op.OPER_TYPE_CODE = rm.OT_OPER_TYPE_CODE \n" +
							//"inner join SHIP_TYPES st on st.SHIP_TYPE_CODE = rm.SS_ST_SHIP_TYPE_CODE \n" +
							"left join (select ROW_NUMBER() over (partition by rm_appl_no order by rm_appl_no, at_ser_num desc) as rowNum, RM_APPL_NO, AT_SER_NUM from TRANSACTIONS \n" +
								"where DATE_CHANGE <= :asAtDate) lr on lr.RM_APPL_NO = rm.appl_no and lr.rowNum=1 \n" +
							"left join REG_MASTERS_HIST rmh on rmh.TX_ID = lr.AT_SER_NUM \n" +
							"where (rm.REG_STATUS = 'R' and (rm.reg_date <= :asAtDate or rm.prov_reg_date <= :asAtDate)) " +
							"or (rm.REG_STATUS = 'D' and rm.DEREG_TIME > :asAtDate) \n" +
						") rm on rm.SS_SHIP_SUBTYPE_CODE = ss.SHIP_SUBTYPE_CODE \n" +
						"order by rm.appl_no" ;
			Query query = em.createNativeQuery(sql);
			query.setParameter("asAtDate", asAtDate);
			List<Object[]> rawList = query.getResultList();
			List<SummaryOfShipsByShipType> summaryList = new ArrayList<SummaryOfShipsByShipType>();

			for (Object[] arr : rawList) {
				SummaryOfShipsByShipType entity = findSummaryByShipCode(summaryList, arr[10].toString(), arr[12].toString());
				String opTypeCode = "";
				if (arr[7]!=null) {
					opTypeCode = arr[7].toString();
				}
				switch (opTypeCode) {
					case "OGV":
						entity.setOnos(entity.getOnos()+1);
						entity.setOgross(entity.getOgross().add(new BigDecimal(arr[5].toString())));
						entity.setOnet(entity.getOnet().add(new BigDecimal(arr[6].toString())));
						break;
					case "LCS":
						entity.setLnos(entity.getLnos()+1);
						entity.setLgross(entity.getLgross().add(new BigDecimal(arr[5].toString())));
						entity.setLnet(entity.getLnet().add(new BigDecimal(arr[6].toString())));
						break;
					case "RTV":
						entity.setRnos(entity.getRnos()+1);
						entity.setRgross(entity.getRgross().add(new BigDecimal(arr[5].toString())));
						entity.setRnet(entity.getRnet().add(new BigDecimal(arr[6].toString())));
						break;
				}
			}
			return summaryList;
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			return new ArrayList<SummaryOfShipsByShipType>();
		}
	}

	@Override
	public List<Map<String, Object>> getShipsByShipTypes(Date reportDate) {
		String sql = "SELECT ST.ST_DESC ship_code,ss2.ss_desc ship_desc, \n" +
		"case when ss2.st_ship_type_code = '" + ShipType.CARGO_SHIP + "' then 'TOTAL OF CARGO SHIP :'  \n" +
		"when ss2.st_ship_type_code = '" + ShipType.PASSENGER_SHIP + "' then 'TOTAL OF PASSENGER SHIP :' \n" +
		"when ss2.st_ship_type_code = '" + ShipType.TANKER + "' then 'TOTAL OF TANKER :' \n" +
		"when ss2.st_ship_type_code = '" + ShipType.TUG + "' then 'TOTAL OF TUG :' \n" +
		"when ss2.st_ship_type_code = '" + ShipType.YACHT + "' then 'TOTAL OF YACHT :' end sub_total \n" +
		",sum(case when rm1.ot_oper_type_code  is null then 0 when rm1.ot_oper_type_code = 'OGV' then 1 else 0 end) onos \n" +
		",sum(case when rm1.ot_oper_type_code IS NULL THEN 0 WHEN rm1.ot_oper_type_code = 'OGV' THEN gross_ton ELSE 0 END) ogross \n" +
		",sum(case when rm1.ot_oper_type_code is null then 0 when rm1.ot_oper_type_code = 'OGV' then reg_net_ton else 0 end) onet \n" +
		",sum(case when rm1.ot_oper_type_code is null then 0 when rm1.ot_oper_type_code = 'RTV' then 1 else 0 end) rnos \n" +
		",sum(case when rm1.ot_oper_type_code is null then 0 when rm1.ot_oper_type_code = 'RTV' then gross_ton else 0 end) rgross \n" +
		",sum(case when rm1.ot_oper_type_code is null then 0 when rm1.ot_oper_type_code = 'RTV' then reg_net_ton else 0 end) rnet \n" +
		",sum(case when rm1.ot_oper_type_code is null then 0 when rm1.ot_oper_type_code = 'LCS' then 1 else 0 end) lnos \n" +
		",sum(case when rm1.ot_oper_type_code is null then 0 when rm1.ot_oper_type_code = 'LCS' then gross_ton else 0 end) lgross \n" +
		",sum(case when rm1.ot_oper_type_code is null then 0 when rm1.ot_oper_type_code = 'LCS' then reg_net_ton else 0 end) lnet \n" +
		",count(rm1.appl_no) tnos \n" +
		",sum(case when gross_ton is null then 0 else gross_ton end) tgross \n" +
		",sum(case when reg_net_ton is null then 0 else reg_net_ton end) tnet \n" +
		"FROM  \n" +
		"ship_subtypes ss2 LEFT JOIN reg_masters rm1 ON  ss2.st_ship_type_code = rm1.ss_st_ship_type_code \n" +
		"AND ss2.ship_subtype_code = rm1.ss_ship_subtype_code \n" +
//		"AND convert(varchar, RM1.REG_DATE, 112) <= :reportDate AND RM1.REG_STATUS = 'R' \n" +
		//"AND RM1.REG_DATE <= :reportDate AND ( RM1.REG_STATUS = 'R' or (RM1.REG_STATUS = 'D' AND RM1.DEREG_TIME > :reportDate ))\n" +
		"AND (" +
		"(RM1.REG_DATE <= :reportDate AND RM1.REG_STATUS = 'R') " +
		"or " +
		"(RM1.REG_STATUS = 'D' AND RM1.DEREG_TIME > :reportDate ))\n" +
		" inner join SHIP_TYPES ST on ss2.ST_SHIP_TYPE_CODE = ST.SHIP_TYPE_CODE " +
		"GROUP BY rm1.ss_st_ship_type_code \n" +
		",ss2.st_ship_type_code, ST.ST_DESC \n" +
		",ss2.ss_desc";
//		Query query = em.createNativeQuery(sql);
//
////		query.setParameter("reportDate", new SimpleDateFormat("yyyyMMdd").format(reportDate));
//		query.setParameter("reportDate", reportDate);
//		List<Object[]> rawlist = query.getResultList();

//		List<SummaryOfShipsByShipType> list = new ArrayList<SummaryOfShipsByShipType>();
//		for (Object[] arr : rawlist) {
//			SummaryOfShipsByShipType item = new SummaryOfShipsByShipType();
//			item.setShipcode(arr[0].toString());
//			item.setShipdesc(arr[1].toString());
//			item.setSubtotal(arr[2].toString());
//			item.setOnos((int)arr[3]);
//			item.setOgross(new BigDecimal(arr[4].toString()));
//			item.setOnet(new BigDecimal(arr[5].toString()));
//			item.setRnos((int)arr[6]);
//			item.setRgross(new BigDecimal(arr[7].toString()));
//			item.setRnet(new BigDecimal(arr[8].toString()));
//			item.setLnos((int)arr[9]);
//			item.setLgross(new BigDecimal(arr[10].toString()));
//			item.setLnet(new BigDecimal(arr[11].toString()));
//
//			list.add(item);
//		}


		List<Map<String, Object>> rows = new ArrayList<>();
//		//for (Object[] arr : list) {
//		for(SummaryOfShipsByShipType arr : list) {
//			Map<String, Object> map = new HashMap<>();
//			map.put("shipType", arr.getShipcode());// arr[0]);
//			map.put("shipSubtype", arr.getShipdesc());// arr[1]);
//			map.put("nos1", arr.getOnos()); //arr[3]);
//			map.put("grt1", arr.getOgross()); //arr[4]);
//			map.put("nrt1", arr.getOnet()); //arr[5]);
//			map.put("nos2", arr.getRnos());//arr[6]);
//			map.put("grt2", arr.getRgross()); //arr[7]);
//			map.put("nrt2", arr.getRnet());// arr[8]);
//			map.put("nos3", arr.getLnos()); //arr[9]);
//			map.put("grt3", arr.getLgross()); //arr[10]);
//			map.put("nrt3", arr.getLnet()); // arr[11]);
//			rows.add(map);
//		}
		List<SummaryOfShipsByShipType> summaries = getSummaryOfShipsByShipTypeAsAtDate(reportDate);
		for (SummaryOfShipsByShipType summary : summaries) {
			Map<String, Object> map = new HashMap<>();
			map.put("shipType", summary.getShipcode());
			map.put("shipSubtype", summary.getShipdesc());
			map.put("nos1", summary.getOnos());
			map.put("grt1", summary.getOgross());
			map.put("nrt1", summary.getOnet());
			map.put("nos2", summary.getRnos());
			map.put("grt2", summary.getRgross());
			map.put("nrt2", summary.getRnet());
			map.put("nos3", summary.getLnos());
			map.put("grt3", summary.getLgross());
			map.put("nrt3", summary.getLnet());
			rows.add(map);
		}
		rows.sort((a,b) -> {
			int result = ((String) a.get("shipType")).compareTo((String) b.get("shipType"));
			if (result == 0) {
				result = ((String) a.get("shipSubtype")).compareTo((String) b.get("shipSubtype"));
			}
			return result;
		} );
		return rows;
	}

	@Override
	public List<Map<String, Object>> getDeregistered(Date from, Date to) {
		return reportByDate(from, to, "deRegTime", "D");
	}

	private List<RegisteredShip> getRegisteredShips(Date fromDate, Date toDate){
		List<RegisteredShip> ships = new ArrayList<RegisteredShip>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		SimpleDateFormat sdfProvRegDate = new SimpleDateFormat("yyyy-MM-dd");
		try {
			String sql = "select rm.APPL_NO, rm.OFF_NO, rm.REG_DATE, rm.reg_status, rm.DEREG_TIME, \n" +
				"	( case when rmh.GROSS_TON is null then rm.GROSS_TON else rmh.GROSS_TON end ) as gross_ton, \n" +
				"	( case when rmh.REG_NET_TON is null then rm.REG_NET_TON else rmh.REG_NET_TON end ) as net_ton, \n" +
				"	( case when rmh.reg_name is null then rm.reg_name else rmh.reg_name end ) as reg_name, \n" +
				"	( case when rmh.reg_cname is null then rm.reg_cname else rmh.reg_cname end ) as reg_cname, \n" +
				"	lr.AT_SER_NUM, ss.SS_DESC, rm.prov_reg_date \n" +
				"from reg_masters rm \n" +
				"inner join SHIP_SUBTYPES ss on ss.SHIP_SUBTYPE_CODE = rm.SS_SHIP_SUBTYPE_CODE \n" +
				"left join ( \n" +
				"	select ROW_NUMBER() over (partition by rm_appl_no order by rm_appl_no, at_ser_num desc) as rowNum, RM_APPL_NO, AT_SER_NUM from TRANSACTIONS\r\n" +
				"		where DATE_CHANGE <= :toDate \n" +
				"	) lr on lr.RM_APPL_NO = rm.appl_no and lr.rowNum = 1 \n" +
				"left join REG_MASTERS_HIST rmh on rmh.TX_ID = lr.AT_SER_NUM \n" +
				"where (rm.reg_status = 'R' or rm.reg_status = 'D') \n" +
				//"	and rm.reg_date > :fromDate \n" +
				//"	and rm.reg_date <= :toDate \n" +
				" and ((rm.prov_reg_date is null and rm.reg_date between :fromDate and :toDate) " + 
				" or (rm.prov_reg_date between :fromDate and :toDate)) " +
				"	and ((rm.dereg_time is null or rm.DEREG_TIME > :toDate) or " + 
				"(rm.dereg_time is not null and rm.dereg_time between :fromDate and :toDate)) \n" +
				"   or (rm.reg_status='D' and rm.REG_DATE>:fromDate and rm.REG_DATE<=:toDate " +
				"       and rm.DEREG_TIME>:fromDate and rm.DEREG_TIME<=:toDate )" +
				"order by rm.REG_DATE";
			Query query = em.createNativeQuery(sql);
			query.setParameter("fromDate", fromDate);
			query.setParameter("toDate", toDate);
			List<Object[]> rawList = query.getResultList();
			for (Object[] arr : rawList) {
				RegisteredShip ship = new RegisteredShip();
				ship.setApplNo(arr[0].toString());
				ship.setOfficalNo(arr[1].toString());
				ship.setRegDate(sdf.parse(arr[2].toString()));
				ship.setShipNameEng(arr[7].toString());
				ship.setShipNameChi(arr[8] == null ? "" : arr[8].toString());
				ship.setGrossTon(new BigDecimal(arr[5].toString()));
				ship.setTxId(arr[9] == null ? 0 : Long.parseLong(arr[9].toString()));
				ship.setShipSubType(arr[10].toString());
				if (arr[11]!=null) {
					Date provRegDate = sdfProvRegDate.parse(arr[11].toString());
					if (ship.getRegDate().compareTo(toDate)>0) {
						ship.setRegDate(provRegDate);
					}
				}
				ships.add(ship);
			}
			return ships;
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
			return ships;
		}
	}

	@Override
	public List<RegisteredShipOwner> getRegisteredShipOwnersCurrent(List<String> applNoList){
		List<RegisteredShipOwner> allOwners = new ArrayList<RegisteredShipOwner>();
		for (int i=0; i<applNoList.size(); i+=100) {
			int batchSize = 100;
			if ((i+batchSize)>applNoList.size()) {
				batchSize = applNoList.size() - i;
			}
			List<String> partialList = applNoList.subList(i, i + batchSize);
			List<RegisteredShipOwner> partialOwners = getPartialRegisteredShipOwners(partialList);
					
			allOwners.addAll(partialOwners);
		}
		return allOwners;
	}

	private List<RegisteredShipOwner> getPartialRegisteredShipOwners(List<String> applNoList){
		List<RegisteredShipOwner> owners = new ArrayList<RegisteredShipOwner>();
		try {
			if (!applNoList.isEmpty()) {
			// [0] appl no
			// [1] owner name
			// [2] owner type
			// [3] int_mixed
			// [4] address1
			// [5] address2
			// [6] address3
				String sql = "select rm_appl_no, owner_name, owner_type, int_mixed, address1, address2, address3 from owners \n" +
					"where (isnull(int_mixed,0) > 0 or owner_type = 'D') and rm_appl_no in (:applNoList)";
				Query query = em.createNativeQuery(sql);
				query.setParameter("applNoList", applNoList);
				List<Object[]> rawList = query.getResultList();
				for (Object[] arr : rawList) {
					RegisteredShipOwner owner = new RegisteredShipOwner();
					owner.setApplNo(arr[0].toString());
					owner.setOwnerName(arr[1].toString());
					owner.setOwnerType(arr[2].toString());
					owner.setOwnerInterest(arr[3] == null ? new BigDecimal(0) : new BigDecimal(arr[3].toString()));
					owner.setAddress1(arr[4] == null ? "" : arr[4].toString());
					owner.setAddress2(arr[5] == null ? "" : arr[5].toString());
					owner.setAddress3(arr[6] == null ? "" : arr[6].toString());
					owners.add(owner);
				}
				return owners;
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
			return owners;
		}
		return owners;
	}
	
	@Override
	public List<RegisteredShipOwner> getRegisteredShipOwnersHistory(List<Long> txIdList){
		List<RegisteredShipOwner> allOwners = new ArrayList<RegisteredShipOwner>();
		for (int i=0; i<txIdList.size(); i+=100) {
			int batchSize = 100;
			if ((i+batchSize)>txIdList.size()) {
				batchSize = txIdList.size() - i;
			}
			List<Long> partialList = txIdList.subList(i, i + batchSize);
			List<RegisteredShipOwner> partialOwners = getPartialRegisteredShipOwnersHistory(partialList);
			allOwners.addAll(partialOwners);
		}
		
		return allOwners;
	}

	private List<RegisteredShipOwner> getPartialRegisteredShipOwnersHistory(List<Long> txIdList){
		List<RegisteredShipOwner> owners = new ArrayList<RegisteredShipOwner>();
		if (!txIdList.isEmpty()) {
			// [0] appl no
			// [1] owner name
			// [2] owner type
			// [3] int_mixed
			// [4] address1
			// [5] address2
			// [6] address3
			String sql = "select rm_appl_no, owner_name, owner_type, int_mixed, address1, address2, address3 from owners_hist \n" +
					"where (isnull(int_mixed, 0) > 0 or owner_type = 'D') and tx_id in (:txIdList)";
			Query query = em.createNativeQuery(sql);
			query.setParameter("txIdList", txIdList);
			List<Object[]> rawList = query.getResultList();
			for (Object[] arr : rawList) {
				RegisteredShipOwner owner = new RegisteredShipOwner();
				owner.setApplNo(arr[0].toString());
				owner.setOwnerName(arr[1].toString());
				owner.setOwnerType(arr[2].toString());
				owner.setOwnerInterest(arr[3] == null ? new BigDecimal(0) : new BigDecimal(arr[3].toString()));
				owner.setAddress1(arr[4] == null ? "" : arr[4].toString());
				owner.setAddress2(arr[5] == null ? "" : arr[5].toString());
				owner.setAddress3(arr[6] == null ? "" : arr[6].toString());
				
				owners.add(owner);
			}
		}
		return owners;
	}

	@Override
	public List<RegisteredShip> getRegisteredShipsSnapshot(Date fromDate, Date toDate){
		List<RegisteredShip> ships = getRegisteredShips(fromDate, toDate);
		List<String> applNoList = ships.stream().map(urEntity -> urEntity.getApplNo()).collect(Collectors.toList());
		List<Long> txIdList = ships.stream().map(urEntity->urEntity.getTxId()).collect(Collectors.toList());
		List<RegisteredShipOwner> shipOwners = getRegisteredShipOwnersCurrent(applNoList);

		for (RegisteredShip ship : ships) {
			List<RegisteredShipOwner> owners = shipOwners.parallelStream()
					.filter(item->item.getApplNo().equals(ship.getApplNo()))
					.collect(Collectors.toList());
			ship.setOwners(owners);
		}

		List<RegisteredShipOwner> shipOwnersHistory = getRegisteredShipOwnersHistory(txIdList);
		for (RegisteredShip ship : ships) {
			List<RegisteredShipOwner> owners = shipOwnersHistory.parallelStream()
					.filter(item->item.getApplNo().equals(ship.getApplNo()))
					.collect(Collectors.toList());
			if (owners.size()>0) {
				ship.setOwners(owners);
			}
		}

		return ships;
	}

	@Override
	public List<Map<String, Object>> getRegistered(Date from, Date to) {
		return reportByDate(from, to, "regDate", "R");
	}


	private List<Map<String, Object>> reportByDate(Date from, Date to, String byDate, String regStatus) {
		String ql = queryByDate(byDate);
		Query qry = em.createQuery(ql);
		qry.setParameter("dateFrom", from).setParameter("dateTo", to);
		qry.setParameter("regStatus", regStatus);
		List<Owner> list = qry.getResultList();
		List<ReasonCode> reasons = em.createQuery("select reason from ReasonCode reason").getResultList();
		List<ShipSubType> subtypes = em.createQuery("select ss from ShipSubType ss").getResultList();
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
		List<Map<String,Object>> result = new ArrayList<>();
		list.forEach(owner -> {
			RegMaster rm = owner.getRegMaster();
			if (result.size() > 0 && result.get(result.size() - 1).get("applNo").equals(rm.getApplNo())) {
				Map<String, Object> last = result.get(result.size() - 1);
				Object lastChar = last.get("charterer");
				last.put("charterer", lastChar + "\n" + owner.getName() + ("D".equals(owner.getType()) ? "\n(DEMISE CHARTERER)" :""));
			} else {
				HashMap<String, Object> row = new HashMap<>();
				row.put("shipNameEng", rm.getRegName());
				row.put("shipNameChi", rm.getRegChiName());
				Date regDate = rm.getRegDate();
				if (regDate != null) {
					row.put("joinDate", format.format(regDate));
				}
				row.put("offNo", rm.getOffNo());
				row.put("grossTonnage", rm.getGrossTon());
				Date deRegTime = rm.getDeRegTime();
				if (deRegTime != null) {
					row.put("closingDate", format.format(deRegTime));
				}
				row.put("closingReason", "");
				if (rm.getRcReasonCode() != null && rm.getRcReasonType() != null) {
					reasons.forEach(reason -> {
						if (reason.getReasonType().equals(rm.getRcReasonType()) && reason.getReasonCode().equals(rm.getRcReasonCode())) {
							row.put("closingReason", reason.getEngDesc());
						}
					});
				}
				if (rm.getShipSubType() != null && rm.getShipSubtypeCode() != null) {
					subtypes.forEach(subtype -> {
						if (subtype.getShipSubTypeCode().equals(rm.getShipSubtypeCode()) && subtype.getShipTypeCode().equals(rm.getShipTypeCode())) {
							row.put("subtypedesc", "\n(" + subtype.getSsDesc() + ")");
						}
					});
				}
				if (row.get("subtypedesc") == null) {
					row.put("subtypedesc", "");
				}
				row.put("charterer", owner.getName() + ("D".equals(owner.getType()) ? "\n(DEMISE CHARTERER)" :""));
				String applNo = rm.getApplNo();
				row.put("applNo", applNo);
				result.add(row);
			}
		});
		return result;
	}
	private String queryByDate(String byDate) {
		String ql = "select owner from Owner owner join fetch owner.regMaster "
				+ " where owner.regMaster.regStatus = :regStatus and owner.regMaster."
				+ byDate
				+ " <= :dateTo and owner.regMaster."
				+ byDate
				+ " >= :dateFrom"
				+ " order by owner.regMaster."
				+ byDate
				+ ","
				+ " owner.regMaster.applNo";
		return ql;
	}

	@Override
	public List<Map<String, ?>> getDiscountAtf(Date reportDateFrom, Date reportDateTo) {
		String ql = "select dnItem, rm from DemandNoteItem dnItem, RegMaster rm join fetch dnItem.demandNoteHeader "
				+ " where dnItem.demandNoteHeader.generationTime >= :reportDateFrom and "
				+ " dnItem.demandNoteHeader.generationTime <= :reportDateTo and "
				+ " dnItem.applNo = rm.applNo and dnItem.fcFeeCode = '01'"
				+ " order by convert(decimal, dnItem.dnDemandNoteNo) asc ";

		Function<Integer, BigDecimal> normalAtcFunction = getAtcCalculator();
		List<?> resultList = em.createQuery(ql).setParameter("reportDateFrom", reportDateFrom).setParameter("reportDateTo", reportDateTo).getResultList();
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
		DecimalFormat decimalFormat = new DecimalFormat("$#,###.00");
		List<Map<String, ?>> rows = new ArrayList<>();
		for (Object obj : resultList) {
			Object[] array = (Object[]) obj;
			DemandNoteItem item = (DemandNoteItem) array[0];
			RegMaster rm = (RegMaster) array[1];
			HashMap<String, Object> row = new HashMap<>();
			row.put("applNo", item.getApplNo());
			row.put("shipName", rm.getRegName());
			row.put("imoNo", rm.getImoNo());
			row.put("officialNo", rm.getOffNo());
			row.put("invoiceNo", item.getDemandNoteHeader().getDemandNoteNo());
			row.put("invoiceDate", format.format(item.getDemandNoteHeader().getGenerationTime()));
			BigDecimal amount = item.getAmount();
			row.put("billedAmount", decimalFormat.format(amount));
			BigDecimal regNetTon = rm.getRegNetTon();
			BigDecimal normal = normalAtcFunction.apply(regNetTon != null ?  regNetTon.intValue() : 0);
			row.put("normalAmount", decimalFormat.format(normal));
			row.put("noReducion", normal.subtract(amount).signum() == 1 ? "":"*");

			rows.add(row);
		}
		return rows;
	}
	private Function<Integer, BigDecimal> getAtcCalculator() {
		List<Object[]> atfTable = em.createNativeQuery("select ATF_TON_LO,ATF_TON_HI,ATF_FEE from ANNUAL_TON_FEES order by ATF_TON_LO").getResultList();
		Integer minAtf = (Integer) atfTable.get(0)[1];
		Integer maxAtf = (Integer) atfTable.get(atfTable.size() -1)[0];
		BigDecimal base = (BigDecimal) atfTable.get(0)[2];
		Function<Integer, BigDecimal> normalAtcFunction = new Function<Integer, BigDecimal>() {

			@Override
			public BigDecimal apply(Integer regNetTon) {
				if (regNetTon <= minAtf) {
					return base;
				} else if (regNetTon >= maxAtf) {
					return (BigDecimal) atfTable.get(atfTable.size() -1)[2];
				}
				double atf = base.doubleValue();
				for (int i = 1; i < atfTable.size() -1; i++) {
					Integer from = (Integer) atfTable.get(i)[0];
					Integer to = (Integer) atfTable.get(i)[1];
					BigDecimal rate = (BigDecimal)atfTable.get(i)[2];
					Integer lastTo = (Integer)atfTable.get(i -1)[1];
					if (from >= regNetTon) {
						break;
					} else {

					}
					if (to >= regNetTon) {
						atf += (regNetTon - lastTo)* rate.doubleValue();
						break;
					} else {
						atf += (to - lastTo)* rate.doubleValue();
					}
				}
				return new BigDecimal(atf);
			}
		};
		return normalAtcFunction;
	}

	@Override
	public List<Map<String, Object>> getNoAndTonnage(Date reportDate) {
		List<Map<String, Object>> rows = new ArrayList<>();
		String[] titles = new String[] {
//				"SHIPS ON THE REGISTER AS AT 2-DEC-1990",//1990-12-02 23:59:59 GMT+8 660153599
				"SHIPS LEFT THE REGISTER SINCE 3-DEC-1990", // 1990-12-03 00:00:00 660153600
				// select count(dereg_time) - (select count(*) from rEG_MASTERS where REG_REGN_TYPE IN ('O','N') ),
				// sum(reg_net_ton) - (select sum(reg_net_ton) from rEG_MASTERS where REG_REGN_TYPE IN ('O','N') )
				//from REG_MASTERS where dereg_time is not null
				"SHIPS PROVISIONALLY REGISTERED SINCE 3-DEC-1990*",
				"SHIPS FULLY REGISTERED SINCE 3-DEC-1990*", // select count(*) from REG_MASTERS where APPL_NO_SUF = 'F'
				"CHANGES (+/-) DUE TO ALTERATION OF TONNAGE OF SHIPS ON THE REGISTER",
				"SHIPS ON THE REGISTER AS AT :reportDate",
		"PERCENTAGE OF PROVISIONAL REGISTRATION HAVING CHANGED TO FULL REGISTRATION"
				};
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);


		Query query = em.createNativeQuery(
//				"select '1', isnull(count(*), 0) COUNT_OF, isnull(sum(gross_ton), 0) SUM_OF from REG_MASTERS where REG_DATE < '1990-12-03' " +
//"union " +
"select '2', ISNULL(COUNT(*), 0) COUNT_OF, ISNULL(SUM(GROSS_TON), 0) SUM_OF FROM REG_MASTERS_HIST RM INNER JOIN TRANSACTIONS T ON RM.TX_ID = T.AT_SER_NUM AND TC_TXN_CODE = '73' AND CONVERT(VARCHAR, DATE_CHANGE, 112) BETWEEN '19901203' AND :reportDate " +
"union select '3', ISNULL(COUNT(*), 0), ISNULL(SUM(GROSS_TON), 0) FROM REG_MASTERS_HIST RM INNER JOIN TRANSACTIONS T ON RM.TX_ID = T.AT_SER_NUM AND TC_TXN_CODE = '71' AND APPL_NO_SUF = 'P' AND CONVERT(VARCHAR, DATE_CHANGE, 112) BETWEEN '19901203' AND :reportDate " +
"union select '4', ISNULL(COUNT(*), 0), ISNULL(SUM(GROSS_TON), 0) FROM REG_MASTERS_HIST RM INNER JOIN TRANSACTIONS T ON RM.TX_ID = T.AT_SER_NUM AND TC_TXN_CODE = '71' AND APPL_NO_SUF = 'F' AND CONVERT(VARCHAR, DATE_CHANGE, 112) BETWEEN '19901203' AND :reportDate " +
"union select '5', isnull(count(*), 0), isnull(sum(rm.gross_ton - h.gross_ton),0)  from REG_MASTERS_HIST H inner join TRANSACTIONS T on H.TX_ID = T.AT_SER_NUM and CONVERT(VARCHAR, DATE_CHANGE, 112) <= :reportDate and T.TC_TXN_CODE in ('52', '58') inner join REG_MASTERS rm on h.APPL_NO = rm.APPL_NO " +
"union select '6', count(*), isnull(sum(gross_ton), 0) from Reg_masters_hist RM inner join (select rm_appl_no, max(at_ser_num) tx_id from Transactions where CONVERT(VARCHAR, DATE_CHANGE, 112) <= :reportDate group by rm_appl_no) T on rm.tx_id = t.tx_id where reg_status = 'R' " +
"union select '7', 0,100.0*count(t2.at_ser_num)/count(t.at_ser_num) FROM REG_MASTERS_HIST RM INNER JOIN TRANSACTIONS T ON RM.TX_ID = T.AT_SER_NUM AND TC_TXN_CODE = '71' and APPL_NO_SUF = 'P' AND CONVERT(VARCHAR, DATE_CHANGE, 112) <= :reportDate left outer join TRANSACTIONS t2 on t.rm_appl_no = t2.rm_appl_no and t2.TC_TXN_CODE = '71' and t2.at_ser_num > t.at_ser_num ");
		query.setParameter("reportDate", new SimpleDateFormat("yyyyMMdd").format(reportDate));
		List<?> list = query.getResultList();

		DecimalFormat f = new DecimalFormat("#");
		for (int i = 0; i < titles.length; i++) {
			String title = titles[i];
			Map<String, Object> row = new HashMap<>();
			row.put("rowTitle", title.replace(":reportDate", format.format(reportDate)));
			if (title.contains("PERCENTAGE")) {
				row.put("noOfShip", new DecimalFormat("#.##").format(((Object[]) list.get(i))[2])+"%");
				row.put("totalGrossTons", null);
			} else {
				row.put("noOfShip", f.format(((Object[]) list.get(i))[1]));
				row.put("totalGrossTons", ((Object[]) list.get(i))[2]);
			}
			rows.add(row);
		}
		return rows;
	}

	@Override
	public List<Map<String, Object>> getOwnerCatergory(Date reportDate) {
		List<Map<String, Object>> onerow = new ArrayList<>();
		HashMap<String, Object> content = new HashMap<>();

		// 1st col I
		// 2nd col C incorp HK
		// 3rd col C reg hk
		Query query = em.createNativeQuery("select "
				+ "isnull(sum(case when owner.status = 'I' and rm.APPL_NO_SUF = 'P' and owner.owner_type <> 'D' then 1 else 0 end), 0) prov_individual_owner, "
				+ "isnull(sum(case when owner.status = 'C' and rm.APPL_NO_SUF = 'P' and owner.owner_type <> 'D' and owner.INCORT_CERT is not null then 1 else 0 end), 0) prov_co_incorp_owner, "
				+ "isnull(sum(case when owner.status = 'C' and rm.APPL_NO_SUF = 'P' and owner.owner_type <> 'D' and owner.OVERSEA_CERT is not null then 1 else 0 end), 0) prov_co_reg_owner, "
				+ "isnull(sum(case when owner.status = 'I' and rm.APPL_NO_SUF = 'P' and owner.owner_type = 'D' then 1 else 0 end), 0) prov_individual_demise, "
				+ "isnull(sum(case when owner.status = 'C' and rm.APPL_NO_SUF = 'P' and owner.owner_type = 'D' and owner.INCORT_CERT is not null then 1 else 0 end), 0) prov_co_incorp_demise, "
				+ "isnull(sum(case when owner.status = 'C' and rm.APPL_NO_SUF = 'P' and owner.owner_type = 'D' and owner.OVERSEA_CERT is not null then 1 else 0 end), 0) prov_co_reg_demise, "
				+ "isnull(sum(case when owner.status = 'I' and rm.APPL_NO_SUF = 'F' and owner.owner_type <> 'D' then 1 else 0 end), 0) full_individual_owner, "
				+ "isnull(sum(case when owner.status = 'C' and rm.APPL_NO_SUF = 'F' and owner.owner_type <> 'D' and owner.INCORT_CERT is not null then 1 else 0 end), 0) full_co_incorp_owner, "
				+ "isnull(sum(case when owner.status = 'C' and rm.APPL_NO_SUF = 'F' and owner.owner_type <> 'D' and owner.OVERSEA_CERT is not null then 1 else 0 end), 0) full_co_reg_owner, "
				+ "isnull(sum(case when owner.status = 'I' and rm.APPL_NO_SUF = 'F' and owner.owner_type = 'D' then 1 else 0 end), 0) full_individual_demise, "
				+ "isnull(sum(case when owner.status = 'C' and rm.APPL_NO_SUF = 'F' and owner.owner_type = 'D' and owner.INCORT_CERT is not null then 1 else 0 end), 0) full_co_incorp_demise, "
				+ "isnull(sum(case when owner.status = 'C' and rm.APPL_NO_SUF = 'F' and owner.owner_type = 'D' and owner.OVERSEA_CERT is not null then 1 else 0 end), 0) full_co_reg_demise, "
				+ "1 dummy "
				+ "from OWNERS owner, REG_MASTERS rm "
				+ "where rm.appl_no = owner.rm_appl_no "
				+ "and rm.reg_status in ('R', 'D') "
				+ "and ((rm.reg_date >= '1990-12-04 00:00:00' "
				+ "and rm.reg_date <= :reportDate and rm.APPL_NO_SUF = 'F') "
				+ " or (rm.PROV_REG_DATE >= '1990-12-04 00:00:00'"
				+ " and rm.PROV_REG_DATE <= :reportDate and rm.APPL_NO_SUF = 'P' ) ) ");

		query.setParameter("reportDate", reportDate);
		Object[] array = (Object[]) query.getResultList().get(0);

		content.put("individual1", array[0]);
		content.put("coIncorp1", array[1]);
		content.put("coRegistered1", array[2]);
		content.put("individual2", array[3]);
		content.put("coIncorp2", array[4]);
		content.put("coRegistered2", array[5]);
		content.put("individual3", array[6]);
		content.put("coIncorp3", array[7]);
		content.put("coRegistered3", array[8]);
		content.put("individual4", array[9]);
		content.put("coIncorp4", array[10]);
		content.put("coRegistered4", array[11]);

		onerow.add(content);
		return onerow;
	}

	@Override
	public List<Map<String, Object>> getTonnageDistribution(Date reportDate) {
		List<Map<String, Object>> rows = new ArrayList<>();
		String sql = "select "
				+ "sum (case when reg_net_ton <= 1 then 1 else 0 end) n1,"
				+ "sum (case when reg_net_ton between 2 and 1000 then 1 else 0 end) n2,"
				+ "sum (case when reg_net_ton between 1001 and 2000 then 1 else 0 end) n3,"
				+ "sum (case when reg_net_ton between 2001 and 3000 then 1 else 0 end) n4,"
				+ "sum (case when reg_net_ton between 3001 and 4000 then 1 else 0 end) n5,"
				+ "sum (case when reg_net_ton between 4001 and 5000 then 1 else 0 end) n6,"
				+ "sum (case when reg_net_ton between 5001 and 6000 then 1 else 0 end) n7,"
				+ "sum (case when reg_net_ton between 6001 and 7000 then 1 else 0 end) n8,"
				+ "sum (case when reg_net_ton between 7001 and 8000 then 1 else 0 end) n9,"
				+ "sum (case when reg_net_ton between 8001 and 9000 then 1 else 0 end) n10,"
				+ "sum (case when reg_net_ton between 9001 and 10000 then 1 else 0 end) n11,"
				+ "sum (case when reg_net_ton > 10000 then 1 else 0 end) n12,"
				+ "sum (case when gross_ton <= 1 then 1 else 0 end) g1,"
				+ "sum (case when gross_ton between 2 and 1000 then 1 else 0 end) g2,"
				+ "sum (case when gross_ton between 1001 and 2000 then 1 else 0 end) g3,"
				+ "sum (case when gross_ton between 2001 and 3000 then 1 else 0 end) g4,"
				+ "sum (case when gross_ton between 3001 and 4000 then 1 else 0 end) g5,"
				+ "sum (case when gross_ton between 4001 and 5000 then 1 else 0 end) g6,"
				+ "sum (case when gross_ton between 5001 and 6000 then 1 else 0 end) g7,"
				+ "sum (case when gross_ton between 6001 and 7000 then 1 else 0 end) g8,"
				+ "sum (case when gross_ton between 7001 and 8000 then 1 else 0 end) g9,"
				+ "sum (case when gross_ton between 8001 and 9000 then 1 else 0 end) g10,"
				+ "sum (case when gross_ton between 9001 and 10000 then 1 else 0 end) g11,"
				+ "sum (case when gross_ton > 10000 then 1 else 0 end) g12 "
				+ "from reg_masters where reg_status ='R'";
		Query query = em.createNativeQuery(sql);
		String[] titles = new String[]{
				"Below     1",
				"   2 - 1000",
				"1001 - 2000",
				"2001 - 3000",
				"3001 - 4000",
				"4001 - 5000",
				"5001 - 6000",
				"6001 - 7000",
				"7001 - 8000",
				"8001 - 9000",
				"9001 - 10000",
				"OVER  10000",
		};
		Object[] result = (Object[]) query.getResultList().get(0);
		for (int i = 0; i < titles.length; i++) {
			Map<String, Object> row = new HashMap<>();
			row.put("range1", titles[i]);
			row.put("noOfShips1", result[i+0]);
			row.put("range2", titles[i]);
			row.put("noOfShips2", result[i+12]);
			rows.add(row);
		}
		return rows;
	}

	@Override
	public List<Map<String, Object>> getTonnageDistributionNewScale(Date reportDate) {
		List<Map<String, Object>> rows = new ArrayList<>();
		String sql = "select "
				+ "sum (case when reg_net_ton <= 500 then 1 else 0 end) n1,"
				+ "sum (case when reg_net_ton between 500 and 1000 then 1 else 0 end) n2,"
				+ "sum (case when reg_net_ton between 1001 and 2000 then 1 else 0 end) n3,"
				+ "sum (case when reg_net_ton between 2001 and 3000 then 1 else 0 end) n4,"
				+ "sum (case when reg_net_ton between 3001 and 4000 then 1 else 0 end) n5,"
				+ "sum (case when reg_net_ton between 4001 and 5000 then 1 else 0 end) n6,"
				+ "sum (case when reg_net_ton between 5001 and 10000 then 1 else 0 end) n7,"
				+ "sum (case when reg_net_ton between 10001 and 15000 then 1 else 0 end) n8,"
				+ "sum (case when reg_net_ton between 15001 and 20000 then 1 else 0 end) n9,"
				+ "sum (case when reg_net_ton between 20001 and 25000 then 1 else 0 end) n10,"
				+ "sum (case when reg_net_ton between 25001 and 30000 then 1 else 0 end) n11,"
				+ "sum (case when reg_net_ton between 30001 and 35000 then 1 else 0 end) n12,"
				+ "sum (case when reg_net_ton between 35001 and 40000 then 1 else 0 end) n13,"
				+ "sum (case when reg_net_ton between 40001 and 45000 then 1 else 0 end) n14,"
				+ "sum (case when reg_net_ton between 45001 and 50000 then 1 else 0 end) n15,"
				+ "sum (case when reg_net_ton between 50001 and 55000 then 1 else 0 end) n16,"
				+ "sum (case when reg_net_ton between 55001 and 60000 then 1 else 0 end) n17,"
				+ "sum (case when reg_net_ton between 60001 and 65000 then 1 else 0 end) n18,"
				+ "sum (case when reg_net_ton between 65001 and 85000 then 1 else 0 end) n19,"
				+ "sum (case when reg_net_ton between 85001 and 115000 then 1 else 0 end) n20,"
				+ "sum (case when reg_net_ton between 115001 and 145000 then 1 else 0 end) n21,"
				+ "sum (case when reg_net_ton between 145001 and 175000 then 1 else 0 end) n22,"
				+ "sum (case when reg_net_ton between 175001 and 200000 then 1 else 0 end) n23,"
				+ "sum (case when reg_net_ton > 200000 then 1 else 0 end) n24,"
				+ "sum (case when gross_ton <= 500 then 1 else 0 end) g1,"
				+ "sum (case when gross_ton between 500 and 1000 then 1 else 0 end) g2,"
				+ "sum (case when gross_ton between 1001 and 2000 then 1 else 0 end) g3,"
				+ "sum (case when gross_ton between 2001 and 3000 then 1 else 0 end) g4,"
				+ "sum (case when gross_ton between 3001 and 4000 then 1 else 0 end) g5,"
				+ "sum (case when gross_ton between 4001 and 5000 then 1 else 0 end) g6,"
				+ "sum (case when gross_ton between 5001 and 10000 then 1 else 0 end) g7,"
				+ "sum (case when gross_ton between 10001 and 15000 then 1 else 0 end) g8,"
				+ "sum (case when gross_ton between 15001 and 20000 then 1 else 0 end) g9,"
				+ "sum (case when gross_ton between 20001 and 25000 then 1 else 0 end) g10,"
				+ "sum (case when gross_ton between 25001 and 30000 then 1 else 0 end) g11,"
				+ "sum (case when gross_ton between 30001 and 35000 then 1 else 0 end) g12,"
				+ "sum (case when gross_ton between 35001 and 40000 then 1 else 0 end) g13,"
				+ "sum (case when gross_ton between 40001 and 45000 then 1 else 0 end) g14,"
				+ "sum (case when gross_ton between 45001 and 50000 then 1 else 0 end) g15,"
				+ "sum (case when gross_ton between 50001 and 55000 then 1 else 0 end) g16,"
				+ "sum (case when gross_ton between 55001 and 60000 then 1 else 0 end) g17,"
				+ "sum (case when gross_ton between 60001 and 65000 then 1 else 0 end) g18,"
				+ "sum (case when gross_ton between 65001 and 85000 then 1 else 0 end) g19,"
				+ "sum (case when gross_ton between 85001 and 115000 then 1 else 0 end) g20,"
				+ "sum (case when gross_ton between 115001 and 145000 then 1 else 0 end) g21,"
				+ "sum (case when gross_ton between 145001 and 175000 then 1 else 0 end) g22,"
				+ "sum (case when gross_ton between 175001 and 200000 then 1 else 0 end) g23,"
				+ "sum (case when gross_ton > 200000 then 1 else 0 end) g24 "
				+ "from reg_masters where (reg_status ='R' and reg_date<=:reportDate) or (reg_status = 'D' and reg_date>:reportDate)";
		Query query = em.createNativeQuery(sql)
				.setParameter("reportDate", reportDate);
		String[] titles = new String[]{
				"Below    500",
				"   500 - 1000",
				"  1001 - 2000",
				"  2001 - 3000",
				"  3001 - 4000",
				"  4001 - 5000",
				"  5001 - 10000",
				" 10001 - 15000",
				" 15001 - 20000",
				" 20001 - 25000",
				" 25001 - 30000",
				" 30001 - 35000",
				" 35001 - 40000",
				" 40001 - 45000",
				" 45001 - 50000",
				" 50001 - 55000",
				" 55001 - 60000",
				" 60001 - 65000",
				" 65001 - 85000",
				" 85001 - 115000",
				"115001 - 145000",
				"145001 - 175000",
				"175001 - 200000",
				"OVER     200000",
		};
		Object[] result = (Object[]) query.getResultList().get(0);
		for (int i = 0; i < titles.length; i++) {
			Map<String, Object> row = new HashMap<>();
			row.put("range1", titles[i]);
			row.put("noOfShips1", result[i+0]);
			row.put("range2", titles[i]);
			row.put("noOfShips2", result[i+24]);
			rows.add(row);
		}
		return rows;
	}

	@Override
	public List<Map<String, Object>> getCompanyRanking(Date reportDate) {
		List<Map<String, Object>> rows = new ArrayList<>();
		Query query = em.createNativeQuery("select convert(varchar(4), row_number() over (order by sumgt desc)) as rank, ownername, countappl, sumgt from (select AGENT_NAME1 + case when AGENT_NAME2 is null then '' else ' ' + agent_name2 end ownername, count(*) countappl, sum(gross_ton) sumgt from REG_MASTERS_HIST M " +
"inner join (select RM_APPL_NO _APPL_NO, max(AT_SER_NUM) _TX_ID from TRANSACTIONS where convert(varchar, DATE_CHANGE, 112) <= :reportDate group by RM_APPL_NO) TX on M.APPL_NO = TX._APPL_NO and M.TX_ID = TX._TX_ID " +
"inner join AGENTS on AGT_AGENT_CODE = AGENT_CODE and REG_STATUS = 'R' group by AGENT_NAME1, AGENT_NAME2) X order by sumgt desc ");
		query.setParameter("reportDate", new SimpleDateFormat("yyyyMMdd").format(reportDate));
		List<Object[]> list = query.getResultList();
		for (Object[] owner : list) {
			Map<String, Object> row = new HashMap<>();
			row.put("rank", owner[0]);
			row.put("name", owner[1]);
			row.put("noOfShips", owner[2]);
			row.put("totalGrt", owner[3]);
			rows.add(row);
		}
		return rows;
	}

	@Override
	public List<List<Map<String, ?>>> getOwnershipReport(Date reportDate) {
		List<List<Map<String, ?>>> reports = new ArrayList<>();
		BiFunction<List<Map<String, ?>>, Object[], List<Map<String, ?>>> addRow = new BiFunction<List<Map<String, ?>>, Object[], List<Map<String, ?>>>() {
			@Override
			public List<Map<String, ?>> apply(List<Map<String, ?>> rpt, Object[] kv) {
				HashMap<String, Object> row = new HashMap<String, Object>();
				for (int i = 0; i < kv.length; i+=2) {
					row.put((String) kv[i], kv[i+1]);
				}
				rpt.add(row);
				return rpt;
			}
		};
		// 01 registered
		List<?> registered = ownerOpenCloseReport(Transaction.CODE_REGISTRATION, reportDate);
		List<Map<String, ?>> rpt = new ArrayList<>();
		for (int i = 0; i < registered.size(); i++) {
			Object[] array = (Object[]) registered.get(i);
			ownerRptType2(addRow, rpt, array);
		}
		reports.add(rpt);
		// 02 dereg
		List<?> deRegistered = ownerOpenCloseReport(Transaction.CODE_DE_REG, reportDate);
		rpt = new ArrayList<>();
		for (int i = 0; i < deRegistered.size(); i++) {
			Object[] array = (Object[]) deRegistered.get(i);
			ownerRptType2(addRow, rpt, array);
		}
		reports.add(rpt);
		// 03 change of owner
		//List<?> changeOwner = ownerShipReport(Transaction.CODE_CHG_OWNER_OTHERS, reportDate);
		List<?> changeOwner = transferOwnershipReport("14,15,19", reportDate);
		rpt = new ArrayList<>();
		for (int i = 0; i < changeOwner.size(); i++) {
			Object[] array = (Object[]) changeOwner.get(i);
			ownerRptType2(addRow, rpt, array);
		}
		reports.add(rpt);
		// 04 owner name
		List<?> changeOwnerNames = ownerOpenCloseReport(Transaction.CODE_CHG_OWNER_NAME, reportDate);
		rpt = new ArrayList<>();
		for (int i = 0; i < changeOwnerNames.size(); i++) {
			Object[] array = (Object[]) changeOwnerNames.get(i);
			List<?> list = ownersHist((BigInteger) array[0]);
			for (Object item : list) {
				Object[] owner = (Object[]) item;
				if (!Owner.TYPE_DEMISE.equals(owner[3])) {
					List<?> oldOwner = oldOwner((Number) array[0], (Number) owner[0]);
					addRow.apply(rpt, new Object[]{
							"shipNameEng", array[2],
							"shipNameChi", array[3] != null ? array[3] : "",
							"shipType", array[9] != null ? array[9] : "",
							"on", array[4] != null ? array[4] : "",
							"grt", (array[5] != null ? array[5].toString() : ""),
							"date", array[6],
							"oldName", oldOwner.isEmpty() ? "" : ((Object[])oldOwner.get(0))[0],
							"newName", owner[2],
					});
				}
			}
		}
		reports.add(rpt);
		// 05 owner addr
		//ownerShipReport3(Transaction.CODE_CHG_OWNER_ADDR, reportDate);
		//ownerShipReport2(Transaction.CODE_CHG_OWNER_ADDR, reportDate);
		//ownerShipReport1(Transaction.CODE_CHG_OWNER_ADDR, reportDate);
		List<?> changeOwnerAddrs = ownerShipReport(Transaction.CODE_CHG_OWNER_ADDR, reportDate);
		rpt = new ArrayList<>();
		for (int i = 0; i < changeOwnerAddrs.size(); i++) {
			Object[] array = (Object[]) changeOwnerAddrs.get(i);
			List<?> list = ownersHist((BigInteger) array[0]);
			for (Object item : list) {
				Object[] owner = (Object[]) item;
				if (!Owner.TYPE_DEMISE.equals(owner[3])) {
					String ownerAddr = owner[1] + "\n" + owner[2];
					addRow.apply(rpt, new Object[]{
							"shipNameEng", array[2],
							"shipNameChi", array[3],
							"shipType", array[9],
							"on", array[4],
							"grt", (array[5] != null ? array[5].toString() : ""),
							"date", array[6],
							"owner", ownerAddr
							//"owner", owner[1],
							//"address", owner[2],
					});
				}
			}
		}
		reports.add(rpt);
		// 06 demise name
		List<?> demiseNames = ownerShipReport(Transaction.CODE_CHG_OWNER_NAME, reportDate);
		rpt = new ArrayList<>();
		for (int i = 0; i < demiseNames.size(); i++) {
			Object[] array = (Object[]) demiseNames.get(i);
			List<?> list = ownersHist((BigInteger) array[0]);
			for (Object item : list) {
				Object[] owner = (Object[]) item;
				if (Owner.TYPE_DEMISE.equals(owner[3])) {
					List<?> oldOwner = oldOwner((Number) array[0], (Number) owner[0]);
					addRow.apply(rpt, new Object[]{
							"shipNameEng", array[2],
							"shipNameChi", array[3],
							"shipType", array[9],
							"on", array[4],
							"grt", (array[5] != null ? array[5].toString() : ""),
							"date", array[6],
							"oldName", ((Object[])oldOwner.get(0))[0],
							"newName", owner[2],
					});
				}
			}
		}
		reports.add(rpt);
		// 07 demise addr
		List<?> demiseAddrs = ownerShipReport(Transaction.CODE_CHG_OWNER_ADDR, reportDate);
		rpt = new ArrayList<>();
		for (int i = 0; i < demiseAddrs.size(); i++) {
			Object[] array = (Object[]) demiseAddrs.get(i);
			List<?> list = ownersHist((BigInteger) array[0]);
			for (Object item : list) {
				Object[] owner = (Object[]) item;
				if (Owner.TYPE_DEMISE.equals(owner[3])) {
					String chartererAddr = owner[1] + "\n" + owner[2];
					addRow.apply(rpt, new Object[]{
							"shipNameEng", array[2],
							"shipNameChi", array[3],
							"shipType", array[9],
							"on", array[4],
							"grt", (array[5] != null ? array[5].toString() : ""),
							"date", array[6],
							"charterer", chartererAddr
							//"charterer", owner[1],
							//"address", owner[2],
					});
				}
			}
		}
		reports.add(rpt);
		// 08 rep
		List<?> rpOthers = rpReport(Transaction.CODE_CHG_RP_OTHERS, reportDate);
		rpt = new ArrayList<>();
		for (int i = 0; i < rpOthers.size(); i++) {
			Object[] array = (Object[]) rpOthers.get(i);
			String rpAddr = array[7] + "\n" + array[8];
			addRow.apply(rpt, new Object[]{
					"shipNameEng", array[2],
					"shipNameChi", array[3],
					"shipType", array[9],
					"on", array[4],
					"grt", (array[5] != null ? array[5].toString() : ""),
					"date", array[6],
					"representative", rpAddr
					//"representative", array[7] ,
					//"address", array[8],
			});
		}
		reports.add(rpt);
		// 09 rep name
		List<?> rpNames = rpReport(Transaction.CODE_CHG_RP_NAME, reportDate);
		rpt = new ArrayList<>();
		for (int i = 0; i < rpNames.size(); i++) {
			Object[] array = (Object[]) rpNames.get(i);

			addRow.apply(rpt, new Object[]{
					"shipNameEng", array[2],
					"shipNameChi", array[3],
					"shipType", array[9],
					"on", array[4],
					"grt", (array[5] != null ? array[5].toString() : ""),
					"date", array[6],
					"newName", array[7],
					"oldName", oldRpName((Number) array[0])
			});
		}
		reports.add(rpt);
		// 10 rep addr
		List<?> rpAddrs = rpReport(Transaction.CODE_CHG_RP_ADDR, reportDate);
		rpt = new ArrayList<>();
		for (int i = 0; i < rpAddrs.size(); i++) {
			Object[] array = (Object[]) rpAddrs.get(i);
			String rpAddr = array[7] + "\n" + array[8];
			addRow.apply(rpt, new Object[]{
					"shipNameEng", array[2],
					"shipNameChi", array[3],
					"shipType", array[9],
					"on", array[4],
					"grt", (array[5] != null ? array[5].toString() : ""),
					"date", array[6],
					"representative", rpAddr
					//"representative", array[7],
					//"address", array[8],
			});
		}
		reports.add(rpt);
		// 11 ship name
		List<?> shipNames = ownerShipReport("55,56,60", reportDate);
		rpt = new ArrayList<>();
		for (int i = 0; i < shipNames.size(); i++) {
			Object[] array = (Object[]) shipNames.get(i);

			addRow.apply(rpt, new Object[]{
					"newName", array[2] + " " + (array[3] == null ? "" : array[3]),
					"on", array[4],
					"grt", (array[5] != null ? array[5].toString() : ""),
					"date", array[6],
					"oldName", oldShipName((Number) array[0])
			});
		}
		reports.add(rpt);

		return reports;
	}
	private void ownerRptType1(BiFunction<List<Map<String, ?>>, Object[], List<Map<String, ?>>> addRow,
			List<Map<String, ?>> rpt, Object[] array) {
		List<?> list = ownersHist((BigInteger) array[0]);
		String ownerName = "";
		String ownerAddr = "";
		for (Object item : list) {
			Object[] owner = (Object[]) item;
			if (!Owner.TYPE_DEMISE.equals(owner[3])) {
				ownerName = owner[1] + "\n";
				ownerAddr =  owner[2] + "\n";
			}
		}
		
		String ownerNameAddress = ownerName + ownerAddr;
		addRow.apply(rpt, new Object[]{
				"shipNameEng", array[2],
				"shipNameChi", array[3],
				"shipType", array[9],
				"on", array[4],
				"grt", (array[5] != null ? array[5].toString() : ""),
				"date", array[6],
				"owner", ownerNameAddress.trim()
				//"owner", ownerName.trim(),
				//"address", ownerAddr.trim(),
		});
	}
	private void ownerRptType2(BiFunction<List<Map<String, ?>>, Object[], List<Map<String, ?>>> addRow,
			List<Map<String, ?>> rpt, Object[] array) {
		//List<?> list = ownersHist((BigInteger) array[0]);
		String ownerName = "";
		String ownerAddr = "";
//		for (Object item : list) {
//			Object[] owner = (Object[]) item;
//			if (!Owner.TYPE_DEMISE.equals(owner[3])) {
//				ownerName = owner[1] + "\n";
//				ownerAddr =  owner[2] + "\n";
//			}
//		}
		String ownerNameAddress = (String)array[7] + "\n" + (String)array[8] ; //ownerName + ownerAddr;
		addRow.apply(rpt, new Object[]{
				"shipNameEng", array[2],
				"shipNameChi", array[3],
				"shipType", array[9],
				"on", array[4],
				"grt", (array[5] != null ? array[5].toString() : ""),
				"date", array[6],
				"owner", ownerNameAddress.trim()
				//"owner", ownerName.trim(),
				//"address", ownerAddr.trim(),
		});
	}

	/**
	 * @param txCode
	 * @param reportDate
	 * @return tx_id, appl_no, reg_name, cname, off_no, grt, date_change, rpName, rpAddr, stDesc
	 */
	private List<?> ownerShipReport(String txCode, Date reportDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		String dateStr = sdf.format(reportDate);
		Query query = em.createNativeQuery("select tx.AT_SER_NUM, tx.rm_appl_no, rm.reg_name, "
				+ "rm.reg_cname cname , rm.off_no, rm.gross_ton, tx.date_change, " +
				"ow.owner_name, isnull(ow.address1 + ' ', '') + isnull(ow.address2 + ' ', '') + isnull(ow.address3, '')  ow_addr, " +
				"SS.SS_DESC " +
				"from Transactions  tx " +
				"inner join REG_MASTERS_HIST rm on rm.appl_no = tx.rm_appl_no and tx.at_ser_num = rm.tx_id " +
				"left outer join SHIP_SUBTYPES ss on ss.ST_SHIP_TYPE_CODE = rm.SS_ST_SHIP_TYPE_CODE and ss.SHIP_SUBTYPE_CODE = rm.SS_SHIP_SUBTYPE_CODE " +
				"inner join OWNERS_HIST ow on ow.rm_appl_no = tx.rm_appl_no and tx.at_ser_num = ow.tx_id " +
				"where tc_txn_code in :txCode " +
				"and substring(convert(varchar, date_change, 23), 1, 7) = substring(convert(varchar, :reportDate, 23), 1, 7) " +
				//"and substring(convert(varchar, date_change, 23), 1, 7) = :reportDate "
				//"and left(convert(varchar, tx.date_change),7) = :reportDate " +
				"order by tx.date_change asc ");
		query.setParameter("txCode", Arrays.asList(txCode.split("\\,")));
		query.setParameter("reportDate", dateStr);
		//List<?> lst = query.getResultList();
		List<Object[]> lst = query.getResultList();
		System.out.println("txCode:" + txCode);
		System.out.println("reportDate:" + dateStr);
		System.out.println("lst size:" + lst.size());
		return lst;
	}

	private List<?> ownerOpenCloseReport(String txCode, Date reportDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		String dateStr = sdf.format(reportDate);
		Query query = em.createNativeQuery("select tx.AT_SER_NUM, tx.rm_appl_no, rm.reg_name, "
				+ "rm.reg_cname cname , rm.off_no, rm.gross_ton, tx.date_change, " +
				"ow.owner_name, isnull(ow.address1 + ' ', '') + isnull(ow.address2 + ' ', '') + isnull(ow.address3, '')  ow_addr, " +
				"SS.SS_DESC " +
				"from Transactions  tx " +
				"inner join REG_MASTERS_HIST rm on rm.appl_no = tx.rm_appl_no and tx.at_ser_num = rm.tx_id " +
				"left outer join SHIP_SUBTYPES ss on ss.ST_SHIP_TYPE_CODE = rm.SS_ST_SHIP_TYPE_CODE and ss.SHIP_SUBTYPE_CODE = rm.SS_SHIP_SUBTYPE_CODE " +
				"inner join OWNERS_HIST ow on ow.rm_appl_no = tx.rm_appl_no and tx.at_ser_num = ow.tx_id " +
				"where tc_txn_code in :txCode " +
				"and substring(convert(varchar, date_change, 23), 1, 7) = substring(convert(varchar, :reportDate, 23), 1, 7) " +
				"and ow.owner_type<>'D' and ow.int_mixed>0" +
				//"and substring(convert(varchar, date_change, 23), 1, 7) = :reportDate "
				//"and left(convert(varchar, tx.date_change),7) = :reportDate " +
				"order by tx.date_change asc ");
		query.setParameter("txCode", Arrays.asList(txCode.split("\\,")));
		query.setParameter("reportDate", dateStr);
		//List<?> lst = query.getResultList();
		List<Object[]> lst = query.getResultList();
		System.out.println("txCode:" + txCode);
		System.out.println("reportDate:" + dateStr);
		System.out.println("lst size:" + lst.size());
		return lst;
	}

	private List<?> transferOwnershipReport(String txCode, Date reportDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		String dateStr = sdf.format(reportDate);
		Query query = em.createNativeQuery("select tx.AT_SER_NUM, tx.rm_appl_no, rm.reg_name, "
				+ "rm.reg_cname cname , rm.off_no, rm.gross_ton, tx.date_change, " +
				"ow.owner_name, isnull(ow.address1 + ' ', '') + isnull(ow.address2 + ' ', '') + isnull(ow.address3, '')  ow_addr, " +
				"SS.SS_DESC " +
				"from Transactions  tx " +
				"inner join REG_MASTERS_HIST rm on rm.appl_no = tx.rm_appl_no and tx.at_ser_num = rm.tx_id " +
				"left outer join SHIP_SUBTYPES ss on ss.ST_SHIP_TYPE_CODE = rm.SS_ST_SHIP_TYPE_CODE and ss.SHIP_SUBTYPE_CODE = rm.SS_SHIP_SUBTYPE_CODE " +
				"inner join OWNERS_HIST ow on ow.rm_appl_no = tx.rm_appl_no and tx.at_ser_num = ow.tx_id " +
				"where tc_txn_code in :txCode " +
				"and substring(convert(varchar, date_change, 23), 1, 7) = substring(convert(varchar, :reportDate, 23), 1, 7) " +
				"and ow.owner_type<>'D' and ow.int_mixed>0" +
				//"and substring(convert(varchar, date_change, 23), 1, 7) = :reportDate "
				//"and left(convert(varchar, tx.date_change),7) = :reportDate " +
				"order by tx.date_change asc ");
		query.setParameter("txCode", Arrays.asList(txCode.split("\\,")));
		query.setParameter("reportDate", dateStr);
		//List<?> lst = query.getResultList();
		List<Object[]> lst = query.getResultList();
		System.out.println("txCode:" + txCode);
		System.out.println("reportDate:" + dateStr);
		System.out.println("lst size:" + lst.size());
		return lst;
	}
	
//	private List<?> ownerShipReport1(String txCode, Date reportDate) {
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
//		String dateStr = sdf.format(reportDate);
//		Query query = em.createNativeQuery("select tx.AT_SER_NUM, tx.rm_appl_no, rm.reg_name, "
//				+ "rm.reg_cname cname , rm.off_no, rm.gross_ton, tx.date_change, " +
//				"ow.owner_name, isnull(ow.address1 + ' ', '') + isnull(ow.address2 + ' ', '') + isnull(ow.address3, '')  ow_addr " +
//				//"SS.SS_DESC " +
//				"from Transactions  tx " +
//				"inner join REG_MASTERS_HIST rm on rm.appl_no = tx.rm_appl_no and tx.at_ser_num = rm.tx_id " +
//				//"left outer join SHIP_SUBTYPES ss on ss.ST_SHIP_TYPE_CODE = rm.SS_ST_SHIP_TYPE_CODE and ss.SHIP_SUBTYPE_CODE = rm.SS_SHIP_SUBTYPE_CODE " +
//				"inner join OWNERS_HIST ow on ow.rm_appl_no = tx.rm_appl_no and tx.at_ser_num = ow.tx_id " +
//				"where tc_txn_code = '18' " +
//				//"and substring(convert(varchar, date_change, 23), 1, 7) = substring(convert(varchar, :reportDate, 23), 1, 7) "
//				//"and substring(convert(varchar, date_change, 23), 1, 7) = :reportDate "
//				"and left(convert(varchar, tx.date_change),7) = '2020-06' " +
//				"order by tx.date_change asc ");
//		//query.setParameter("txCode", Arrays.asList(txCode.split("\\,")));
//		//query.setParameter("reportDate", dateStr);
//		//List<?> lst = query.getResultList();
//		List<Object[]> lst = query.getResultList();
//		System.out.println("owner ship report 1");
//		System.out.println("txCode:" + txCode);
//		System.out.println("reportDate:" + dateStr);
//		System.out.println("lst size:" + lst.size());
//		return lst;
//	}
//	private List<?> ownerShipReport2(String txCode, Date reportDate) {
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
//		String dateStr = sdf.format(reportDate);
//		Query query = em.createNativeQuery("select tx.AT_SER_NUM, tx.rm_appl_no, rm.reg_name, "
//				+ "rm.reg_cname cname , rm.off_no, rm.gross_ton, tx.date_change, " +
//				"ow.owner_name, isnull(ow.address1 + ' ', '') + isnull(ow.address2 + ' ', '') + isnull(ow.address3, '')  ow_addr, " +
//				"SS.SS_DESC " +
//				"from Transactions  tx " +
//				"inner join REG_MASTERS_HIST rm on rm.appl_no = tx.rm_appl_no and tx.at_ser_num = rm.tx_id " +
//				"left outer join SHIP_SUBTYPES ss on ss.ST_SHIP_TYPE_CODE = rm.SS_ST_SHIP_TYPE_CODE and ss.SHIP_SUBTYPE_CODE = rm.SS_SHIP_SUBTYPE_CODE " +
//				"inner join OWNERS_HIST ow on ow.rm_appl_no = tx.rm_appl_no and tx.at_ser_num = ow.tx_id " +
//				"where tc_txn_code = :txCode " +
//				//"and substring(convert(varchar, date_change, 23), 1, 7) = substring(convert(varchar, :reportDate, 23), 1, 7) "
//				//"and substring(convert(varchar, date_change, 23), 1, 7) = :reportDate "
//				"and left(convert(varchar, tx.date_change),7) = '2020-05' " +
//				"order by tx.date_change asc ");
//		query.setParameter("txCode", Arrays.asList(txCode.split("\\,")));
//		//query.setParameter("reportDate", dateStr);
//		//List<?> lst = query.getResultList();
//		List<Object[]> lst = query.getResultList();
//		System.out.println("owner ship report 2");
//		System.out.println("txCode:" + txCode);
//		System.out.println("reportDate:" + dateStr);
//		System.out.println("lst size:" + lst.size());
//		return lst;
//	}
//	private List<?> ownerShipReport3(String txCode, Date reportDate) {
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
//		String dateStr = sdf.format(reportDate);
//		Query query = em.createNativeQuery("select tx.AT_SER_NUM, tx.rm_appl_no, rm.reg_name, "
//				+ "rm.reg_cname cname , rm.off_no, rm.gross_ton, tx.date_change, " +
//				"ow.owner_name, isnull(ow.address1 + ' ', '') + isnull(ow.address2 + ' ', '') + isnull(ow.address3, '')  ow_addr, " +
//				"SS.SS_DESC " +
//				"from Transactions  tx " +
//				"inner join REG_MASTERS_HIST rm on rm.appl_no = tx.rm_appl_no and tx.at_ser_num = rm.tx_id " +
//				"left outer join SHIP_SUBTYPES ss on ss.ST_SHIP_TYPE_CODE = rm.SS_ST_SHIP_TYPE_CODE and ss.SHIP_SUBTYPE_CODE = rm.SS_SHIP_SUBTYPE_CODE " +
//				"inner join OWNERS_HIST ow on ow.rm_appl_no = tx.rm_appl_no and tx.at_ser_num = ow.tx_id " +
//				"where tc_txn_code = '18' " +
//				//"and substring(convert(varchar, date_change, 23), 1, 7) = substring(convert(varchar, :reportDate, 23), 1, 7) "
//				//"and substring(convert(varchar, date_change, 23), 1, 7) = :reportDate "
//				//"and left(convert(varchar, tx.date_change),7) = :reportDate " +
//				"order by tx.date_change asc ");
//		//query.setParameter("txCode", Arrays.asList(txCode.split("\\,")));
//		//query.setParameter("reportDate", dateStr);
//		//List<?> lst = query.getResultList();
//		List<Object[]> lst = query.getResultList();
//		System.out.println("owner ship report 3");
//		System.out.println("txCode:" + txCode);
//		System.out.println("reportDate:" + dateStr);
//		System.out.println("lst size:" + lst.size());
//		return lst;
//	}

	private List<?> rpReport(String txCode, Date reportDate) {
		Query query = em.createNativeQuery("select tx.AT_SER_NUM, tx.rm_appl_no, rm.reg_name, "
				+ "rm.reg_cname cname , rm.off_no, rm.gross_ton, tx.date_change, " +
				"rp.rep_name1, isnull(rp.address1 + ' ', '') + isnull(rp.address2 + ' ', '') + isnull(rp.address3, '')  rp_addr, " +
				"SS.SS_DESC " +
				"from Transactions  tx " +
				"inner join REG_MASTERS_HIST rm on rm.appl_no = tx.rm_appl_no and tx.at_ser_num = rm.tx_id " +
				"left outer join SHIP_SUBTYPES ss on ss.ST_SHIP_TYPE_CODE = rm.SS_ST_SHIP_TYPE_CODE and ss.SHIP_SUBTYPE_CODE = rm.SS_SHIP_SUBTYPE_CODE " +
				"inner join REPRESENTATIVES_HIST rp on rp.rm_appl_no = tx.rm_appl_no and tx.at_ser_num = rp.tx_id " +
				"where tc_txn_code in :txCode " +
				"and substring(convert(varchar, date_change, 23), 1, 7) = substring(convert(varchar, :reportDate, 23), 1, 7) "
				+ "order by tx.date_change asc ");
		query.setParameter("txCode", Arrays.asList(txCode.split("\\,")));
		query.setParameter("reportDate", reportDate);
		List<?> lst = query.getResultList();
		return lst;
	}

	/**
	 * @param txid
	 * @return ownerSeq, ownerName, addr, ownerType
	 */
	private List<?> ownersHist(BigInteger txid) {
		Query query = em.createNativeQuery("select OWNER_SEQ_NO, ow.owner_name, isnull(ow.address1 + ' ', '')  + isnull(ow.address2 + ' ', '') + isnull(ow.address3, '')  addr, owner_type "
				+ "from owners_hist ow where ow.tx_id = :txId");
		query.setParameter("txId", txid);
		return query.getResultList();
	}
	/**
	 * @param txId
	 * @param ownerSeq
	 * @return name, addr
	 */
	private List<?> oldOwner(Number txId, Number ownerSeq) {
		Query query = em.createNativeQuery("select ow.owner_name, isnull(ow.address1 + ' ', '')  + isnull(ow.address2 + ' ', '') + isnull(ow.address3, '')  addr "
				+ "from owners_hist ow "
				+ "where ow.tx_id = ("
				+ "  select max(at_ser_num) from transactions "
				+ "  where rm_appl_no = (select rm_appl_no from transactions where at_ser_num = :txId) "
				+ "  and at_ser_num < :txId) and owner_seq_no = :ownerSeq");
		query.setParameter("txId", txId);
		query.setParameter("ownerSeq", ownerSeq);
		return query.getResultList();
	}
	private String oldRpName(Number txId) {
		Query query = em.createNativeQuery("select ow.rep_name1 "
				+ "from representatives_hist ow  "
				+ "where ow.tx_id = ("
				+ "  select max(at_ser_num) from transactions "
				+ "  where rm_appl_no = (select rm_appl_no from transactions where at_ser_num = :txId) "
				+ "  and at_ser_num < :txId) ");
		query.setParameter("txId", txId);
		return (String) query.getSingleResult();
	}

	private String oldShipName(Number txId) {
		Query query = em.createNativeQuery("select reg_name + isnull(' ' + reg_cname, '') name "
				+ "from reg_masters_hist ow   "
				+ "where ow.tx_id = ("
				+ "  select max(at_ser_num) from transactions "
				+ "  where rm_appl_no = (select rm_appl_no from transactions where at_ser_num = :txId) "
				+ "  and at_ser_num < :txId) ");
		query.setParameter("txId", txId);
		return (String) query.getSingleResult();
	}


	@Override
	public String getIntTotAt(String applNo, Date regTime) {
		Query query = em.createNativeQuery("select concat (convert(varchar(5), INT_TOT), (case when INT_UNIT = 'S' then ' Shares' when INT_UNIT = '%' then ' Percents' when INT_UNIT = 'P' then ' Parts' when INT_UNIT = 'R' then ' Fractions' end)) affected\r\n" +
				"from REG_MASTERS_HIST where APPL_NO = :applNo  and LASTUPD_DATE = (select Max(LASTUPD_DATE) from REG_MASTERS_HIST where APPL_NO = :applNo and LASTUPD_DATE <= :onOrBefore)");
		query.setParameter("applNo", applNo);
		query.setParameter("onOrBefore", regTime);

		List list = query.getResultList();
		return list.isEmpty() ? "-" : (String) list.get(0);
	}

	@Override
	public RegMaster findByApplId(String applNo, Date reportDate) {
		List<RegMaster> list = findHistory(applNo, reportDate);
		return list.isEmpty() ? null : list.get(0);
	}

	@Override
	public RegMaster findByApplIdOnly(String applNo) {
		List<RegMaster> list = em.createQuery("select rm from RegMaster m where rm.applNo = :applNo")
				.setParameter("applNo", applNo)
				.getResultList();
		return list.isEmpty() ? null : list.get(0);
	}

	@Override
	public List<RegMaster> searchVessel4Transcript(String vesselName) {
		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put("vesselName", vesselName +"%");
		return searchRmHistory("and (REG_NAME like :vesselName or REG_CNAME like :vesselName )", parameters, "order by reg_name", false);
	}
	private List<RegMaster> searchRmHistory(String where, Map<String, Object> parameters, String order, boolean first) {
		String sql = (/*use txid*/first ? "select distinct top(1) " : "select distinct ")
				+ " APPL_NO, APPL_NO_SUF, CALL_SIGN, GROSS_TON, IMO_NO, REG_NET_TON, OFF_NO, REG_STATUS, REG_CNAME, REG_NAME, ST_DESC, REG_DATE \n" +
				"from REG_MASTERS_HIST RM inner join SHIP_TYPES ST ON  RM.SS_ST_SHIP_TYPE_CODE = ST.SHIP_TYPE_CODE \n" +
				"where RM.OFF_NO is not null and exists (select 1 from (select APPL_NO, max(TX_ID) TX_ID from REG_MASTERS_HIST group by APPL_NO) maxtx where maxtx.APPL_NO = rm.APPL_NO and maxtx.TX_ID = rm.TX_ID) and REG_STATUS in ('R', 'D') and not exists (select * from SSRS_TRAN_LOCKS locks where locks.RM_APPL_NO = rm.APPL_NO)"/*use txid*/;
		Query sqlQuery = em.createNativeQuery(sql + where + " " +  order);
		for (String key : parameters.keySet()) {
			sqlQuery.setParameter(key, parameters.get(key));
		}
		ArrayList<RegMaster> result = new ArrayList<>();
		for (Object row : sqlQuery.getResultList()) {
			Object[] values = (Object[]) row;
			RegMaster rm = new RegMaster();
			rm.setApplNo((String) values[0]);
			rm.setApplNoSuf((String) values[1]);
			rm.setCallSign((String) values[2]);
			rm.setGrossTon((BigDecimal) values[3]);
			rm.setImoNo((String) values[4]);
			rm.setRegNetTon((BigDecimal) values[5]);
			rm.setOffNo((String) values[6]);
			rm.setRegStatus((String) values[7]);
			rm.setRegChiName((String) values[8]);
			rm.setRegName((String) values[9]);
			ShipType shipType = new ShipType();
			shipType.setStDesc((String) values[10]);
			rm.setShipType(shipType);
			rm.setRegDate((Date) values[11]);
			result.add(rm);
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.mardep.ssrs.dao.sr.IRegMasterDao#retrieveVessel4Transcript(java.lang.String, java.lang.String)
	 */
	@Override
	public RegMaster retrieveVessel4Transcript(String officialNo, String vesselName) {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("officialNo", officialNo);
		parameters.put("vesselName", vesselName);
		List<RegMaster> list = searchRmHistory("and REG_NAME = :vesselName and OFF_NO = :officialNo", parameters, " order by reg_date desc", true);
		return list.isEmpty() ? null : list.get(0);
	}

	@Override
	public RegMaster retrieveVesselByIMO(String imo) {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("imo", imo);
		List<RegMaster> list = searchRmHistory("and IMO_NO = :imo", parameters, " order by reg_date desc", true);
		return list.isEmpty() ? null : list.get(0);
	}
	@Override
	public FeeCode validateTranscriptApp(String applNo, Calendar inputDate) {
		RegMaster rm = findByApplId(applNo, inputDate.getTime());
		if (rm != null) {
			if (transcriptAppFeeCode == null) {
				String jpql = "select feeCode from FeeCode feeCode where engDesc = 'Copy of, or extract from, any entry in the register' and active = 'Y'";
				List list = em.createQuery(jpql).getResultList();
				if (!list.isEmpty()) {
					transcriptAppFeeCode = (FeeCode) list.get(0);
					return transcriptAppFeeCode;
				}
			} else {
				return transcriptAppFeeCode;
			}
		}
		return null;
	}
	@Override
	public Map<String, BigDecimal> calculateAtc(String[] applNoList) {
		HashMap<String, BigDecimal> result = new HashMap<String, BigDecimal>();
		Function<Integer, BigDecimal> atcCalculator = getAtcCalculator();
		for (String applNo: applNoList) {
			RegMaster regMaster = findById(applNo);
			BigDecimal regNetTon = regMaster.getRegNetTon();
			if (regNetTon == null) {
				result.put(applNo, BigDecimal.ZERO);
			} else {
				result.put(applNo, atcCalculator.apply(regNetTon.intValue()));
			}
		}
		return result;
	}

	@Override
	public BigDecimal calculateRegFee(BigDecimal grossTon) {
		Query query = em.createNativeQuery("select top (1) RFS_FEE  from FIRST_REG_FEES where round(:gt, 0) >= RFS_TON_LO and round(:gt, 0) <= RFS_TON_HI");
		query.setParameter("gt", grossTon);
		List resultList = query.getResultList();
		return (BigDecimal) resultList.get(0);
	}

	@Override
	public List<RegMaster> ebsShipReg(String vesselName, String officialNo, String imoNo) {
		Query query = em.createQuery("select rm from RegMaster rm LEFT JOIN FETCH rm.shipType "
				+ "where rm.regStatus in ('A', 'R') and (:vesselName is null or :vesselName = '' or rm.regName = :vesselName) "
				+ "and (:imoNo is null or :imoNo = '' or rm.imoNo = :imoNo) and (:officialNo is null or :officialNo = '' or rm.offNo = :officialNo ) "
				+ "order by rm.regName");
		query.setParameter("vesselName", vesselName);
		query.setParameter("officialNo", officialNo);
		query.setParameter("imoNo", imoNo);
		return query.getResultList();
	}

	@Override
	public Map<String, Object> getPipelineSummary(Date reportDate) {
		Query query = em.createNativeQuery("select totalShip, totalGrossTonnage, shipInPipeline, rangeFrom, rangeTo  from"
				+ " (select count(*) totalShip , sum(gross_ton) totalGrossTonnage from REG_MASTERS rm inner join APPL_DETAILS ad on rm.appl_NO = ad.RM_APPL_NO  where rm.reg_status = 'A' "
				//+ " and ad.PROPOSE_REG_DATE is not null "
				//+ " and ad.PROPOSE_REG_DATE "+ "< :reportDate"
				+ " and ad.create_date <= :reportDate "
				+ ") a,		"
				+ " (select count(*) shipInPipeline, min(gross_ton) rangeFrom, max(gross_ton) rangeTo "
				+ "from REG_MASTERS rm inner join APPL_DETAILS ad on rm.appl_NO = ad.RM_APPL_NO "
				+ "where ot_oper_type_code = 'OGV' and reg_status = 'A'  "
				//+ "and ad.PROPOSE_REG_DATE is not null "
				//+ "and ad.PROPOSE_REG_DATE < :reportDate"
				+ " and ad.create_date <= :reportDate "
				+ ") b");

		query.setParameter("reportDate", reportDate);
		Object[] row = (Object[]) query.getResultList().get(0);
		HashMap<String, Object> summary = new HashMap<>();
		DecimalFormat format = new DecimalFormat("#,###");
		Function<Object, String> func = new Function<Object, String>() {
			@Override
			public String apply(Object t) {
				if (t == null) {
					return "0";
				} else {
					return format.format((Number) t);
				}
			}
		};
		summary.put("totalShip", func.apply(row[0]));
		summary.put("totalGrossTonnage", func.apply(row[1]));
		summary.put("shipInPipeline", func.apply(row[2]));
		summary.put("rangeFrom", func.apply(row[3]));
		summary.put("rangeTo", func.apply(row[4]));
		return summary;
	}

	@Override
	public List<?> getPipelineDetails(Date reportDate) {
		ArrayList<Object> list = new ArrayList<>();
		Query query = em.createNativeQuery("select monthYear, count(*) noOfShips "
				+ "from (select FORMAT (ad.PROPOSE_REG_DATE, 'yyyy-MM') monthYear, 1 noOfShips  "
				+ "from REG_MASTERS rm inner join APPL_DETAILS ad on rm.appl_NO = ad.RM_APPL_NO   "
				+ "where rm.reg_status = 'A'  "
				//+ "and ad.PROPOSE_REG_DATE is not null "
				//+ "and ad.PROPOSE_REG_DATE < :reportDate "
				//+ "and REG_DATE is not null "
				+ " and ad.create_date <= :reportDate "
				+ ") a group by monthYear");
		query.setParameter("reportDate", reportDate);
		for (Object row : query.getResultList()) {
			HashMap<Object, Object> map = new HashMap<>();
			map.put("monthYear", ((Object[]) row)[0]==null ? "'No propose reg date'" : ((Object[]) row)[0]);
			map.put("noOfShips", ((Object[]) row)[1]);
			list.add(map);
		}

		return list;
	}

	@Override
	public List<Map<String, Object>> getPipelineDetailRows(Date reportDate) {
		String sql = "select rm.reg_name, rm.reg_cname, rm.survey_ship_type, "
				+ "rm.GROSS_TON, a.agent_name1,  ad.propose_reg_date, '' applicant "
				+ "from reg_masters rm inner join appl_details ad    "
				+ "on rm.appl_no  = ad.rm_appl_no "
				+ "left outer join AGENTS a on a.agent_code = rm.agt_agent_code "
				+ "where (rm.reg_status = 'A' or rm.reg_date > :reportDate) "
				//+ "and ad.PROPOSE_REG_DATE is not null "
				//+ "and ad.PROPOSE_REG_DATE < :reportDate "
				+ " and ad.create_date <= :reportDate "
				+ "order by rm.reg_name";
		Query query = em.createNativeQuery(sql);
		query.setParameter("reportDate", reportDate);
		List<Map<String, Object>> list = new ArrayList<>();
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
		for (Object r : query.getResultList()) {
			Object[] row = (Object[]) r;
			Map<String, Object> map = new HashMap<>();
			map.put("shipNameEng", row[0]);
			map.put("shipNameChi", row[1]==null ? "" : row[1]);
			map.put("shipType", row[2]==null ? "" : row[2]);
			map.put("grt", row[3]);
			map.put("agent", row[4] == null ? "" : row[4]);
			map.put("proposedDate", row[5] == null ? "" : formatter.format(row[5]));
			map.put("applicant", row[6]);
			list.add(map);

		}

		return list;
	}

	@Override
	public List<Map<String, Object>> getShipRegAnnualReport(Date reportDate) {
		String sqlString = "select '1', count(*) cnt, sum(reg_net_ton) nt, sum(gross_ton) gt from REG_MASTERS_HIST rm inner join ( " +
				"select max(at_ser_num) tx_id, rm_appl_no appl_no from TRANSACTIONS T where year(DATE_CHANGE) < :before group by RM_APPL_NO) tx on rm.appl_no = tx.appl_no and rm.tx_id = tx.tx_id " +
				"where rm.REG_STATUS = 'R' and year(rm.REG_DATE) <= :before " +
				"union " +
				"select '2', count(*), sum(reg_net_ton), sum(gross_ton) from REG_MASTERS_HIST rm inner join ( " +
				"select max(at_ser_num) tx_id, rm_appl_no appl_no from TRANSACTIONS T where year(DATE_CHANGE) < :before + 1 group by RM_APPL_NO) tx on rm.appl_no = tx.appl_no and rm.tx_id = tx.tx_id " +
				"where year(rm.REG_DATE) = :before " +
				"union " +
				"select '3', count(*), sum(reg_net_ton), sum(gross_ton) from REG_MASTERS_HIST rm inner join ( " +
				"select max(at_ser_num) tx_id, rm_appl_no appl_no from TRANSACTIONS T where year(DATE_CHANGE) < :before + 1 group by RM_APPL_NO) tx on rm.appl_no = tx.appl_no and rm.tx_id = tx.tx_id " +
				"where year(rm.DEREG_TIME) = :before " +
				"union " +
				"select '4', count(*), sum(reg_net_ton), sum(gross_ton) from REG_MASTERS_HIST rm inner join ( " +
				"select max(at_ser_num) tx_id, rm_appl_no appl_no from TRANSACTIONS T where year(DATE_CHANGE) < :before + 1 group by RM_APPL_NO) tx on rm.appl_no = tx.appl_no and rm.tx_id = tx.tx_id " +
				"where rm.REG_STATUS = 'R' ";
		Query query = em.createNativeQuery(sqlString);
		query.setParameter("before", Integer.parseInt(new SimpleDateFormat("yyyy").format(reportDate)));
		List<Object[]> rows = query.getResultList();
		Map<String, Object> annualReport = new HashMap<>();
		for (Object[] row:rows) {
			annualReport.put("noOfShips" + row[0], row[1]);
			annualReport.put("netTonnage" + row[0], row[2]);
			annualReport.put("grossTonnage" + row[0], row[3]);
		}
		return Arrays.asList(annualReport);
	}

	@Override
	public List<Map<String, Object>> getShipRegAnnualReportDetail(Date reportDate) {

		String sqlString = "select " +
				"a.tonFrom,a.tonTo,count(b.appl_no) noOfShips " +
				"from " +
				"( " +
				"   select " +
				"   rowno, " +
				"   case when rowno < 4 then 175000 - (RowNo * 30000) when rowno < 18 then 90000 - (RowNo * 5000) when rowno < 22 then 22000 - (RowNo * 1000) when rowno < 24 then 11500 - (RowNo * 500) else 0 end tonFrom, " +
				"   case when rowno < 4 then 175000 - " +
				"   ( " +
				"      (RowNo - 1) * 30000 " +
				"   ) " +
				"   when rowno = 4 then 85000 when rowno < 19 then 90000 - " +
				"   ( " +
				"      (RowNo - 1) * 5000 " +
				"   ) " +
				"   when rowno < 23 then 22000 - ((RowNo - 1) * 1000) else 500 end tonTo " +
				"   from " +
				"   ( " +
				"      select " +
				"      top(23) ROW_NUMBER() OVER(ORDER BY name ASC) AS RowNo " +
				"      from sys.columns " +
				"   ) " +
				"   r " +
				") " +
				"a " +
				"left join " +
				"( " +
				"   SELECT " +
				"   rm.appl_no, " +
				"   gross_ton " +
				"   FROM reg_masters_hist RM " +
				"   where exists (select 1 from (select APPL_NO, max(TX_ID) TX_ID from REG_MASTERS_HIST R inner join TRANSACTIONS T on T.AT_SER_NUM = R.TX_ID where convert(varchar, T.DATE_CHANGE, 112) <= :before group by APPL_NO) summary " +
				"   where summary.TX_ID = RM.TX_ID and summary.APPL_NO = RM.APPL_NO) " +
				"   and rm.reg_status = 'R' " +
				") " +
				"b on b.gross_ton between a.tonFrom " +
				"and a.tonTo " +
				"group by a.tonFrom,a.tonTo " +
				"order by a.tonFrom desc ";
		Query query = em.createNativeQuery(sqlString);
		query.setParameter("before", new SimpleDateFormat("yyyyMMdd").format(reportDate));
		List<Map<String, Object>> annualReport = new ArrayList<>();
		DecimalFormat format = new DecimalFormat("#,###");
		for (Object r: query.getResultList()) {
			Object[] row = (Object[]) r;
			Map<String, Object> map = new HashMap<>();
			map.put("gtFrom", format.format(row[0]));
			map.put("gtTo", format.format(row[1]));
			map.put("noOfShips", row[2]);
			annualReport.add(map);
		}

		return annualReport;
	}

	@Override
	protected Path getPath(Root<RegMaster> listRoot, String[] keyArray) {
		if (keyArray.length == 1 && keyArray[0].equals("representativeName")) {
			return super.getPath(listRoot, new String[]{"rp", "name"});
		}
		return super.getPath(listRoot, keyArray);
	}

	@Override
	protected String orderFieldMap(String key) {
		if ("representativeName".equals(key)) {
			return "rp/name";
		}
		return super.orderFieldMap(key);
	}

	@Override
	public List<RegMaster> findByApplNoList(List list) {
		return em.createQuery("select rm from RegMaster rm where rm.applNo in (:applNoList)").setParameter("applNoList", list).getResultList();
	}
	@Override
	public Long[] getTakeUpRate(Date fromDate, Date toDate) {
		Query query = em.createNativeQuery("select 1, count(*) cnt " +
				"from demand_note_items ini " +
//				"where ini.generation_time between :fromDate and :toDate " +
				"where ini.generation_time >=:fromDate and ini.generation_time<:toDate " +
				"and ini.fc_fee_code in ('11','12') " +
				"union " +
				"select 2, count(distinct imo_no) cnt " +
				"from reg_masters " +
				"where reg_status = 'R'; ");
		query.setParameter("fromDate", fromDate);
		query.setParameter("toDate", toDate);
		List resultList = query.getResultList();

		return new Long[]{((Integer) ((Object[]) resultList.get(0))[1]).longValue(),
				((Integer) ((Object[]) resultList.get(1))[1]).longValue(),};
	}

	@Override
	public RegMaster checkTrackCode(String trackCode) throws IllegalStateException {
		Query query = em.createNativeQuery("select " +
			"REG_NAME, REG_CNAME, IMO_NO, REG_DATE, SS_ST_SHIP_TYPE_CODE, " +
			"SURVEY_SHIP_TYPE, GROSS_TON, REG_NET_TON, REG_STATUS, PROV_EXP_DATE, CHARTER_EDATE, APPL_NO_SUF " +
			"from REG_MASTERS_HIST rmh " +
			"left outer join OWNERS_HIST owh on rmh.APPL_NO = owh.RM_APPL_NO and rmh.TX_ID = owh.TX_ID and OWNER_TYPE = 'D' " +
			"where rmh.APPL_NO IN " +
			"( " +
			"   select APPL_NO " +
			"   from REG_MASTERS_HIST " +
			"   where replace(TRACK_CODE, '-', '') = :trackCode " +
			") " +
			"and rmh.TX_ID = " +
			"( " +
			"   select max(TX_ID) " +
			"   from REG_MASTERS_HIST " +
			"   where APPL_NO IN " +
			"   ( " +
			"      select APPL_NO " +
			"      from REG_MASTERS_HIST " +
			"      where replace(TRACK_CODE, '-', '')  = :trackCode " +
			"   ) " +
			") " +
			"and replace(rmh.TRACK_CODE, '-', '')  = :trackCode ");
		query.setParameter("trackCode", trackCode.replaceAll("\\-", ""));
		List<?> list = query.getResultList();
		if (!list.isEmpty()) {
//		"REG_NAME, REG_CNAME, IMO_NO, REG_DATE, SS_ST_SHIP_TYPE_CODE, " +
//		"SURVEY_SHIP_TYPE, GROSS_TON, REG_NET_TON, REG_STATUS, PROV_EXP_DATE, CHARTER_EDATE, APPL_NO_SUF " +
			Object[] array = (Object[]) list.get(0);
			RegMaster regMaster = new RegMaster();
			regMaster.setRegName((String) array[0]);
			regMaster.setRegChiName((String) array[1]);
			regMaster.setImoNo((String) array[2]);
			regMaster.setRegDate((Date) array[3]);
			regMaster.setShipTypeCode((String) array[4]);
			regMaster.setSurveyShipType((String) array[5]);
			regMaster.setGrossTon((BigDecimal) array[6]);
			regMaster.setRegNetTon((BigDecimal) array[7]);
			String regStatus = (String) array[8];
			regMaster.setRegStatus(regStatus);
			Date provExpDate = (Date) array[9];
			Date edate = (Date) array[10];
			String suf = (String) array[11];
			regMaster.setProvExpDate(provExpDate);
			if ("P".equals(suf) && !"D".equals(regStatus)) {
				if (provExpDate != null && System.currentTimeMillis() > provExpDate.getTime()) {
					// throw expire
					throw new IllegalStateException("provisional expired");
				}
			}
			if (edate != null && System.currentTimeMillis() > edate.getTime()) {
				// throw expire
				throw new IllegalStateException("chartered period expired");
			}
			return regMaster;
		}
		return null;
	}
	@Override
	public void logFsqcResponse(String jsonInputString, String string) {
		Query query = em.createNativeQuery("insert into FSQC_RESPONSE (SENT_DATE, SENT, REPLY, SUCCESS) values (?,?,?,?)");
		query.setParameter(1, new Date());
		query.setParameter(2, jsonInputString);
		query.setParameter(3, string);
		query.setParameter(4, "Y");
		query.executeUpdate();
	}
	@Override
	public void logFsqcResponse(String jsonInputString, Exception e) {
		Query query = em.createNativeQuery("insert into FSQC_RESPONSE (SENT_DATE, SENT, REPLY, SUCCESS) values (?,?,?,?)");
		query.setParameter(1, new Date());
		query.setParameter(2, jsonInputString);
		query.setParameter(3, e.getMessage());
		query.setParameter(4, "N");
		query.executeUpdate();

	}

	@Override
	public void logLvpfsResponse(String jsonInputString, String string) {
		Query query = em.createNativeQuery("insert into LVPFS_RESPONSE (SENT_DATE, SENT_MSG, REPLY_MSG, SUCCESS) values (?,?,?,?)");
		query.setParameter(1, new Date());
		query.setParameter(2, jsonInputString);
		query.setParameter(3, string);
		query.setParameter(4, "Y");
		query.executeUpdate();
	}
	
	@Override
	public void logLvpfsResponse(String jsonInputString, Exception e) {
		Query query = em.createNativeQuery("insert into LVPFS_RESPONSE (SENT_DATE, SENT_MSG, REPLY_MSG, SUCCESS) values (?,?,?,?)");
		query.setParameter(1, new Date());
		query.setParameter(2, jsonInputString);
		query.setParameter(3, e.getMessage());
		query.setParameter(4, "N");
		query.executeUpdate();

	}

	@Override
	public RegMaster findForCsr(String imoNo){
		try{
			StringBuffer sb = new StringBuffer();
			sb.append("select rm from RegMaster rm where rm.imoNo =:imoNo order by rm.applNo desc");
			Query query = em.createQuery(sb.toString());
			query.setParameter("imoNo", imoNo);
			query.setMaxResults(1);
			return (RegMaster) query.getSingleResult();
		}catch(Exception ex){
			return null;
		}
	}
	
	@Override
	public List<RegisteredShip> getRegisteredShipsAsAtMonthEnd(Date toDate){
		List<RegisteredShip> ships = new ArrayList<RegisteredShip>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		// [0] appl no
		// [1] official no
		// [2] reg date
		// [3] reg status
		// [4] dereg time
		// [5] call sign
		// [6] survey ship type
		// [7] gross ton
		// [8] net ton
		// [9] reg name
		// [10] reg cname
		// [11] tx id
		// [12] sub type
		// [13] sub subtype
		try {
			String sql = "select rm.APPL_NO, rm.OFF_NO, rm.REG_DATE, rm.reg_status, rm.DEREG_TIME, rm.CALL_SIGN, rm.SURVEY_SHIP_TYPE, \n" +
				"	( case when rmh.GROSS_TON is null then rm.GROSS_TON else rmh.GROSS_TON end ) as gross_ton, \n" +
				"	( case when rmh.REG_NET_TON is null then rm.REG_NET_TON else rmh.REG_NET_TON end ) as net_ton, \n" +
				"	( case when rmh.reg_name is null then rm.reg_name else rmh.reg_name end ) as reg_name, \n" +
				"	( case when rmh.reg_cname is null then rm.reg_cname else rmh.reg_cname end ) as reg_cname, \n" +
				"	lr.AT_SER_NUM, st.ST_DESC, ss.SS_DESC \n" +
				"from reg_masters rm \n" +
				"inner join SHIP_TYPES st on st.SHIP_TYPE_CODE = rm.SS_ST_SHIP_TYPE_CODE \n" +				
				"inner join SHIP_SUBTYPES ss on ss.SHIP_SUBTYPE_CODE = rm.SS_SHIP_SUBTYPE_CODE \n" +				
				"left join ( \n" +
				"	select ROW_NUMBER() over (partition by rm_appl_no order by rm_appl_no, at_ser_num desc) as rowNum, RM_APPL_NO, AT_SER_NUM from TRANSACTIONS\r\n" +
				"		where DATE_CHANGE <= :toDate \n" +
				"	) lr on lr.RM_APPL_NO = rm.appl_no and lr.rowNum = 1 \n" +
				"left join REG_MASTERS_HIST rmh on rmh.TX_ID = lr.AT_SER_NUM \n" +
				"where (rm.reg_status = 'R' or rm.reg_status = 'D') \n" +
				//"	and rm.reg_date > :fromDate \n" +
				"	and rm.reg_date <= :toDate \n" +
				"	and (rm.dereg_time is null or rm.DEREG_TIME > :toDate) \n" +
				"order by rm.REG_NAME";
			Query query = em.createNativeQuery(sql);
			//query.setParameter("fromDate", fromDate);
			query.setParameter("toDate", toDate);
			List<Object[]> rawList = query.getResultList();
			for (Object[] arr : rawList) {
				RegisteredShip ship = new RegisteredShip();
				
				ship.setApplNo(arr[0].toString());
				ship.setOfficalNo(arr[1]==null ? "" : arr[1].toString());
				ship.setCallSign(arr[5]==null ? "" : arr[5].toString());
				ship.setSurveyShipType(arr[6]==null ? "" : arr[6].toString());
				ship.setGrossTon(arr[7]==null ? new BigDecimal(0) : new BigDecimal(arr[7].toString()));
				ship.setNetTon(arr[8]==null ? new BigDecimal(0) : new BigDecimal(arr[8].toString()));
				ship.setShipNameEng(arr[9]==null ? "" : arr[9].toString());
				ship.setTxId(arr[11] == null ? 0 : Long.parseLong(arr[11].toString()));
				ship.setShipType(arr[12] == null ? "" : arr[12].toString());
				ship.setShipSubType(arr[13] == null ? "" : arr[13].toString());
				ships.add(ship);
			}
			return ships;
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
			return ships;
		}
	}
	
	@Override
	public List<RegMaster> findByImo(String imoNo) {
		List<RegMaster> list = em.createQuery("from RegMaster rm where rm.imoNo = :imoNo")
				.setParameter("imoNo", imoNo)
				.getResultList();
		return list.isEmpty() ? null : list;
	}

	@Override
	public RegMaster findRegMasterHistory(Long txId) throws Exception {
		try {
			String sql = "select TX_ID, APPL_NO, APPL_NO_SUF from REG_MASTERS_HIST where TX_ID = :txId";
			Query query = em.createNativeQuery(sql)
					.setParameter("txId", txId);
			List<Object[]> list = query.getResultList();
			Object[] arr = list.get(0);
			RegMaster entity = new RegMaster();
			entity.setApplNo(arr[1].toString());
			entity.setApplNoSuf(arr[2].toString());
			return entity;
		} catch (Exception ex) {
			logger.error("Fail to fetch RegMaster History-{}, Exception-{}", new Object[]{txId, ex}, ex);
			throw ex;
		}
	}


	@Override
	public void logFsqcRequest(String jsonRcvRequest, String jsonReplyResult) {
		Query query = em.createNativeQuery("insert into FSQC_REQUEST (RCV_DATE, REQUEST, REPLY_TO_FSQC) values (?,?,?)");
		query.setParameter(1, new Date());
		query.setParameter(2, jsonRcvRequest);
		query.setParameter(3, jsonReplyResult);
		query.executeUpdate();
	}

}