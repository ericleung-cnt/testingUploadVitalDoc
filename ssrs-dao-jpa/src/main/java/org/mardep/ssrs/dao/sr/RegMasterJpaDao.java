package org.mardep.ssrs.dao.sr;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.ParseException;
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
import org.springframework.stereotype.Repository;

@Repository
public class RegMasterJpaDao extends AbstractJpaDao<RegMaster, String> implements IRegMasterDao {
	private FeeCode transcriptAppFeeCode;

	public RegMasterJpaDao() {
		criteriaList.add(new PredicateCriteria("representativeName", PredicateType.LIKE_IGNORE_CASE));
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
		Query query = em.createNativeQuery("select b.score + 1 from (select ROW_NUMBER() OVER(ORDER BY CALL_SIGN ASC) AS Row,  (ASCII(substring(CALL_SIGN, 2, 1))- 65)  * 8 * 26  * 26+ (ASCII(substring(CALL_SIGN, 3, 1))- 65) * 8 * 26 + (ASCII(substring(CALL_SIGN, 4, 1))- 65) * 8 + (ASCII(substring(CALL_SIGN, 5, 1)) - 50) score from REG_MASTERS where CALL_SIGN is not null union select 0, -1 ) a , (select ROW_NUMBER()  OVER(ORDER BY CALL_SIGN ASC) +1 AS Row, (ASCII(substring(CALL_SIGN, 2, 1))- 65)  * 8 * 26  * 26+ (ASCII(substring(CALL_SIGN, 3, 1))- 65) * 8 * 26 + (ASCII(substring(CALL_SIGN, 4, 1))- 65) * 8 + (ASCII(substring(CALL_SIGN, 5, 1)) - 50) score from REG_MASTERS where CALL_SIGN is not null union select 1, -1) b where a.row = b.row and a.score - b.score > 1");
		List<?> resultList = query.getResultList();
		int score = resultList.size() == 0 ? 0: (Integer) resultList.get(0);

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

	@Override
	public List<Map<String, Object>> getShipsByShipTypes(Date reportDate) {
		String sql = "SELECT ss2.st_ship_type_code ship_code,ss2.ss_desc ship_desc, \n" +
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
		"AND RM1.REG_DATE <= :reportDate \n" +
		"GROUP BY rm1.ss_st_ship_type_code \n" +
		",ss2.st_ship_type_code \n" +
		",ss2.ss_desc";
		Query query = em.createNativeQuery(sql);

		query.setParameter("reportDate", reportDate);
		List<Object[]> list = query.getResultList();

		List<Map<String, Object>> rows = new ArrayList<>();
		for (Object[] arr : list) {
			Map<String, Object> map = new HashMap<>();
			map.put("shipType",arr[0]);
			map.put("shipSubtype", arr[1]);
			map.put("nos1", arr[3]);
			map.put("grt1", arr[4]);
			map.put("nrt1", arr[5]);
			map.put("nos2", arr[6]);
			map.put("grt2", arr[7]);
			map.put("nrt2", arr[8]);
			map.put("nos3", arr[9]);
			map.put("grt3", arr[10]);
			map.put("nrt3", arr[11]);
			rows.add(map);
		}
		return rows;
	}

	@Override
	public List<Map<String, Object>> getDeregistered(Date from, Date to) {
		return reportByDate(from, to, "deRegTime", "D");
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
						if (subtype.getShipSubTypeCode().equals(rm.getShipSubtypeCode()) && subtype.getShipTypeCode().equals(rm.getShipSubType())) {
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
				+ " where dnItem.generationTime >= :reportDateFrom and "
				+ " dnItem.generationTime <= :reportDateTo and "
				+ " dnItem.applNo = rm.applNo and dnItem.fcFeeCode = '01' ";

		Function<Integer, BigDecimal> normalAtcFunction = getAtcCalculator();
		List<?> resultList = em.createQuery(ql).setParameter("reportDateFrom", reportDateFrom).setParameter("reportDateTo", reportDateTo).getResultList();
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
		DecimalFormat decimalFormat = new DecimalFormat("#,###.00");
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
			BigDecimal normal = normalAtcFunction.apply(rm.getRegNetTon().intValue());
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
				"SHIPS ON THE REGISTER AS AT 2-DEC-1990",//1990-12-02 23:59:59 GMT+8 660153599
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


		Query query = em.createNativeQuery("select '1', isnull(count(*), 0) COUNT_OF, isnull(sum(gross_ton), 0) SUM_OF from REG_MASTERS where REG_DATE < '1990-12-03' " +
"union select '2', isnull(count(*), 0), isnull(sum(gross_ton), 0) from REG_MASTERS where DEREG_TIME >= '1990-12-03' " +
"union select '3', isnull(count(*), 0), isnull(sum(gross_ton), 0) from REG_MASTERS where REG_DATE >= '1990-12-03' and APPL_NO_SUF = 'P' " +
"union select '4', isnull(count(*), 0), isnull(sum(gross_ton), 0) from REG_MASTERS where REG_DATE >= '1990-12-03' and APPL_NO_SUF = 'F' " +
"union select '5', isnull(count(*), 0), isnull(sum(rm.gross_ton - h.gross_ton),0)  from REG_MASTERS_HIST H inner join TRANSACTIONS T on H.TX_ID = T.AT_SER_NUM and T.TC_TXN_CODE in ('52', '58') inner join REG_MASTERS rm on h.APPL_NO = rm.APPL_NO " +
"union select '6', isnull(count(*), 0), isnull(sum(gross_ton), 0) from REG_MASTERS where REG_DATE <= :reportDate and REG_STATUS = 'R' " +
"union select '7', round(100.0 * isnull(count(*), 0) / (select isnull(count(*), 0) from REG_MASTERS rm where rm.APPL_NO_SUF = 'F'), 0), isnull(100* sum(gross_ton), 0) / (select isnull(sum(gross_ton), 0) from REG_MASTERS rm where rm.APPL_NO_SUF = 'F') from REG_MASTERS rm where rm.APPL_NO_SUF = 'F' and exists (select * from REG_MASTERS_HIST h where h.APPL_NO_SUF = 'P' and rm.APPL_NO = h.APPL_NO) ");
		query.setParameter("reportDate", reportDate);
		List<?> list = query.getResultList();

		DecimalFormat f = new DecimalFormat("#");
		for (int i = 0; i < titles.length; i++) {
			String title = titles[i];
			Map<String, Object> row = new HashMap<>();
			row.put("rowTitle", title.replace(":reportDate", format.format(reportDate)));
			row.put("noOfShip", f.format(((Object[]) list.get(i))[1]));
			row.put("totalGrossTons", ((Object[]) list.get(i))[2]);
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
	public List<Map<String, Object>> getCompanyRanking(Date reportDate) {
		List<Map<String, Object>> rows = new ArrayList<>();
		Query query = em.createNativeQuery(
				"select convert(varchar(4), row_number() over (order by sum(gross_ton) desc)) as rank, ow.owner_name as ownername, count(appl_no) as countappl, sum(gross_ton) as sumgt "
				+ "from reg_masters rm "
				+ "inner join owners ow "
				+ "on rm.appl_no = ow.rm_appl_no and ow.OWNER_TYPE = 'C' \r\n" +
				"where rm.reg_status = 'R' and rm.reg_date  < :reportDate\r\n" +
				"group by ow.OWNER_NAME order by sum(gross_ton) desc");
		query.setParameter("reportDate", reportDate);
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
		List<?> registered = ownerShipReport(Transaction.CODE_REGISTRATION, reportDate);
		List<Map<String, ?>> rpt = new ArrayList<>();
		for (int i = 0; i < registered.size(); i++) {
			Object[] array = (Object[]) registered.get(i);
			ownerRptType1(addRow, rpt, array);
		}
		reports.add(rpt);
		// 02 dereg
		List<?> deRegistered = ownerShipReport(Transaction.CODE_DE_REG, reportDate);
		rpt = new ArrayList<>();
		for (int i = 0; i < deRegistered.size(); i++) {
			Object[] array = (Object[]) deRegistered.get(i);
			ownerRptType1(addRow, rpt, array);
		}
		reports.add(rpt);
		// 03 change of owner
		List<?> changeOwner = ownerShipReport(Transaction.CODE_CHG_OWNER_OTHERS, reportDate);
		rpt = new ArrayList<>();
		for (int i = 0; i < changeOwner.size(); i++) {
			Object[] array = (Object[]) changeOwner.get(i);
			ownerRptType1(addRow, rpt, array);
		}
		reports.add(rpt);
		// 04 owner name
		List<?> changeOwnerNames = ownerShipReport(Transaction.CODE_CHG_OWNER_NAME, reportDate);
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
		// 05 owner addr
		List<?> changeOwnerAddrs = ownerShipReport(Transaction.CODE_CHG_OWNER_ADDR, reportDate);
		rpt = new ArrayList<>();
		for (int i = 0; i < changeOwnerAddrs.size(); i++) {
			Object[] array = (Object[]) changeOwnerAddrs.get(i);
			List<?> list = ownersHist((BigInteger) array[0]);
			for (Object item : list) {
				Object[] owner = (Object[]) item;
				if (!Owner.TYPE_DEMISE.equals(owner[3])) {
					addRow.apply(rpt, new Object[]{
							"shipNameEng", array[2],
							"shipNameChi", array[3],
							"shipType", array[9],
							"on", array[4],
							"grt", (array[5] != null ? array[5].toString() : ""),
							"date", array[6],
							"owner", owner[1],
							"address", owner[2],
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
					addRow.apply(rpt, new Object[]{
							"shipNameEng", array[2],
							"shipNameChi", array[3],
							"shipType", array[9],
							"on", array[4],
							"grt", (array[5] != null ? array[5].toString() : ""),
							"date", array[6],
							"charterer", owner[1],
							"address", owner[2],
					});
				}
			}
		}
		reports.add(rpt);
		// 08 rep
		List<?> rpOthers = ownerShipReport(Transaction.CODE_CHG_RP_OTHERS, reportDate);
		rpt = new ArrayList<>();
		for (int i = 0; i < rpOthers.size(); i++) {
			Object[] array = (Object[]) rpOthers.get(i);
			addRow.apply(rpt, new Object[]{
					"shipNameEng", array[2],
					"shipNameChi", array[3],
					"shipType", array[9],
					"on", array[4],
					"grt", (array[5] != null ? array[5].toString() : ""),
					"date", array[6],
					"representative", array[7] ,
					"address", array[8],
			});
		}
		reports.add(rpt);
		// 09 rep name
		List<?> rpNames = ownerShipReport(Transaction.CODE_CHG_RP_NAME, reportDate);
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
		List<?> rpAddrs = ownerShipReport(Transaction.CODE_CHG_RP_ADDR, reportDate);
		rpt = new ArrayList<>();
		for (int i = 0; i < rpAddrs.size(); i++) {
			Object[] array = (Object[]) rpAddrs.get(i);
			addRow.apply(rpt, new Object[]{
					"shipNameEng", array[2],
					"shipNameChi", array[3],
					"shipType", array[9],
					"on", array[4],
					"grt", (array[5] != null ? array[5].toString() : ""),
					"date", array[6],
					"representative", array[7],
					"address", array[8],
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
		addRow.apply(rpt, new Object[]{
				"shipNameEng", array[2],
				"shipNameChi", array[3],
				"shipType", array[9],
				"on", array[4],
				"grt", (array[5] != null ? array[5].toString() : ""),
				"date", array[6],
				"owner", ownerName.trim(),
				"address", ownerAddr.trim(),
		});
	}

	/**
	 * @param txCode
	 * @param reportDate
	 * @return tx_id, appl_no, reg_name, cname, off_no, grt, date_change, rpName, rpAddr, stDesc
	 */
	private List<?> ownerShipReport(String txCode, Date reportDate) {
		Query query = em.createNativeQuery("select tx.AT_SER_NUM, tx.rm_appl_no, rm.reg_name, "
				+ "rm.reg_cname cname , rm.off_no, rm.gross_ton, tx.date_change, " +
				"rp.rep_name1, isnull(rp.address1 + ' ', '') + isnull(rp.address2 + ' ', '') + isnull(rp.address3, '')  rp_addr, " +
				"ST.ST_DESC " +
				"from Transactions  tx " +
				"inner join REG_MASTERS_HIST rm on rm.appl_no = tx.rm_appl_no and tx.at_ser_num = rm.tx_id " +
				"left outer join SHIP_TYPES st on rm.SS_ST_SHIP_TYPE_CODE = st.SHIP_TYPE_CODE " +
				"inner join REPRESENTATIVES_HIST rp on rp.rm_appl_no = tx.rm_appl_no and tx.at_ser_num = rp.tx_id " +
				"where tc_txn_code in :txCode " +
				"and substring(convert(varchar, date_change, 23), 1, 7) = substring(convert(varchar, :reportDate, 23), 1, 7) ");
		query.setParameter("txCode", Arrays.asList(txCode.split("\\,")));
		query.setParameter("reportDate", reportDate);
		return query.getResultList();
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
			result.put(applNo, atcCalculator.apply(regMaster.getRegNetTon().intValue()));
		}
		return result;
	}

	@Override
	public List<RegMaster> ebsShipReg(String vesselName, String officialNo, String imoNo) {
		Query query = em.createQuery("select rm from RegMaster rm LEFT JOIN FETCH rm.shipType "
				+ "where rm.regStatus in ('A', 'R') and (:vesselName is null or rm.regName = :vesselName) "
				+ "and (:imoNo is null or rm.imoNo = :imoNo) and (:officialNo is null or rm.offNo = :officialNo ) "
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
				+ " and ad.APPL_DATE "+ "< :reportDate) a,		"
				+ " (select count(*) shipInPipeline, min(gross_ton) rangeFrom, max(gross_ton) rangeTo "
				+ "from REG_MASTERS rm inner join APPL_DETAILS ad on rm.appl_NO = ad.RM_APPL_NO "
				+ "where ot_oper_type_code = 'OGV' and reg_status = 'A'  "
				+ "and ad.APPL_DATE < :reportDate) b");

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
				+ "from (select FORMAT (REG_DATE, 'yyyy-MM') monthYear, 1 noOfShips  "
				+ "from REG_MASTERS rm inner join APPL_DETAILS ad on rm.appl_NO = ad.RM_APPL_NO   "
				+ "where rm.reg_status = 'A'  "
				+ "and ad.APPL_DATE < :reportDate "
				+ "and REG_DATE is not null ) a group by monthYear");
		query.setParameter("reportDate", reportDate);
		for (Object row : query.getResultList()) {
			HashMap<Object, Object> map = new HashMap<>();
			map.put("monthYear", ((Object[]) row)[0]);
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
				+ "and ad.appl_date < :reportDate "
				+ "order by rm.reg_name";
		Query query = em.createNativeQuery(sql);
		query.setParameter("reportDate", reportDate);
		List<Map<String, Object>> list = new ArrayList<>();
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
		for (Object r : query.getResultList()) {
			Object[] row = (Object[]) r;
			Map<String, Object> map = new HashMap<>();
			map.put("shipNameEng", row[0]);
			map.put("shipNameChi", row[1]);
			map.put("shipType", row[2]);
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
		SimpleDateFormat format = new SimpleDateFormat("yyyy");
		String thisYear = format.format(reportDate);
		String nextYear = String.valueOf(Integer.parseInt(thisYear) + 1);
		Date dateFrom;
		Date dateTo;
		try {
			dateFrom = format.parse(thisYear);
			dateTo = format.parse(nextYear );
		} catch (ParseException e) {

			throw new RuntimeException("unknown exception parsing date");
		}

		Map<String, Object> annualReport = new HashMap<>();
		getAnnualReport(" and reg_status = 'R'", dateFrom, "1", annualReport);
		getAnnualReport(" and format(dereg_time, 'yyyy') = '" + thisYear + "' and reg_status = 'D' ", dateTo, "2", annualReport);
		getAnnualReport(" and format(reg_date, 'yyyy') = '" + thisYear + "' and reg_status = 'R' ", dateTo, "3", annualReport);
		getAnnualReport(" and reg_status = 'R'", dateTo, "4", annualReport);
		return Arrays.asList(annualReport);
	}
	private Map<String, Object> getAnnualReport(String condition, Date date, String suffix, Map<String, Object> annualReport) {
		String sqlString = "SELECT COUNT(1) noOfShips, isnull(sum(reg_net_ton), 0) netTonnage, isnull(sum(gross_ton), 0) grossTonnage "
				+ "FROM reg_masters_HIST RM "
				+ "INNER JOIN (SELECT appl_no, MAX(TX_ID) TX_ID FROM reg_masters_HIST "
				+ "WHERE TX_ID <= ("
				+ "select max(AT_SER_NUM) from TRANSACTIONS " +
				"where " +
				"convert(datetime2, convert(varchar, DATE_CHANGE, 23) + ' ' + " +
				"case " +
				"when len(HOUR_CHANGE) >= 4 then substring(HOUR_CHANGE, 1,2) + ':' + substring(HOUR_CHANGE, 3,4) " +
				"when len(HOUR_CHANGE) = 3 then '0' + substring(HOUR_CHANGE, 1,1) + ':' + substring(HOUR_CHANGE, 2,3) " +
				"when len(HOUR_CHANGE) = 2 then '00:' + HOUR_CHANGE " +
				"when len(HOUR_CHANGE) = 1 then '00:0' + HOUR_CHANGE " +
				"else '00:00' end) = ( " +
				"select max(convert(datetime2, convert(varchar, DATE_CHANGE, 23) + ' ' + " +
				"case " +
				"when len(HOUR_CHANGE) >= 4 then substring(HOUR_CHANGE, 1,2) + ':' + substring(HOUR_CHANGE, 3,4) " +
				"when len(HOUR_CHANGE) = 3 then '0' + substring(HOUR_CHANGE, 1,1) + ':' + substring(HOUR_CHANGE, 2,3) " +
				"when len(HOUR_CHANGE) = 2 then '00:' + HOUR_CHANGE " +
				"when len(HOUR_CHANGE) = 1 then '00:0' + HOUR_CHANGE " +
				"else '00:00' end)) tx_time " +
				"from TRANSACTIONS " +
				"where convert(datetime2, convert(varchar, DATE_CHANGE, 23) + ' ' + " +
				"case " +
				"when len(HOUR_CHANGE) >= 4 then substring(HOUR_CHANGE, 1,2) + ':' + substring(HOUR_CHANGE, 3,4) " +
				"when len(HOUR_CHANGE) = 3 then '0' + substring(HOUR_CHANGE, 1,1) + ':' + substring(HOUR_CHANGE, 2,3) " +
				"when len(HOUR_CHANGE) = 2 then '00:' + HOUR_CHANGE " +
				"when len(HOUR_CHANGE) = 1 then '00:0' + HOUR_CHANGE " +
				"else '00:00' end)  <= :before " +
				") "
				+ ") GROUP BY appl_no) SS "
				+ "ON RM.APPL_no = SS.APPL_NO AND RM.TX_ID = SS.TX_ID ";
		Query query = em.createNativeQuery(sqlString + condition);
		query.setParameter("before", date);
		Object[] row = (Object[]) query.getResultList().get(0);

		annualReport.put("noOfShips" + suffix, row[0]);
		annualReport.put("netTonnage" + suffix, row[1]);
		annualReport.put("grossTonnage" + suffix, row[2]);
		return annualReport;
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
				"   INNER JOIN " +
				"   ( " +
				"      SELECT " +
				"      appl_no, " +
				"      MAX(TX_ID) TX_ID " +
				"      FROM reg_masters_HIST " +
				"      WHERE TX_ID <= ("
				+ "select max(AT_SER_NUM) from TRANSACTIONS " +
				"where " +
				"convert(datetime2, convert(varchar, DATE_CHANGE, 23) + ' ' + " +
				"case " +
				"when len(HOUR_CHANGE) >= 4 then substring(HOUR_CHANGE, 1,2) + ':' + substring(HOUR_CHANGE, 3,4) " +
				"when len(HOUR_CHANGE) = 3 then '0' + substring(HOUR_CHANGE, 1,1) + ':' + substring(HOUR_CHANGE, 2,3) " +
				"when len(HOUR_CHANGE) = 2 then '00:' + HOUR_CHANGE " +
				"when len(HOUR_CHANGE) = 1 then '00:0' + HOUR_CHANGE " +
				"else '00:00' end) = ( " +
				"select max(convert(datetime2, convert(varchar, DATE_CHANGE, 23) + ' ' + " +
				"case " +
				"when len(HOUR_CHANGE) >= 4 then substring(HOUR_CHANGE, 1,2) + ':' + substring(HOUR_CHANGE, 3,4) " +
				"when len(HOUR_CHANGE) = 3 then '0' + substring(HOUR_CHANGE, 1,1) + ':' + substring(HOUR_CHANGE, 2,3) " +
				"when len(HOUR_CHANGE) = 2 then '00:' + HOUR_CHANGE " +
				"when len(HOUR_CHANGE) = 1 then '00:0' + HOUR_CHANGE " +
				"else '00:00' end)) tx_time " +
				"from TRANSACTIONS " +
				"where convert(datetime2, convert(varchar, DATE_CHANGE, 23) + ' ' + " +
				"case " +
				"when len(HOUR_CHANGE) >= 4 then substring(HOUR_CHANGE, 1,2) + ':' + substring(HOUR_CHANGE, 3,4) " +
				"when len(HOUR_CHANGE) = 3 then '0' + substring(HOUR_CHANGE, 1,1) + ':' + substring(HOUR_CHANGE, 2,3) " +
				"when len(HOUR_CHANGE) = 2 then '00:' + HOUR_CHANGE " +
				"when len(HOUR_CHANGE) = 1 then '00:0' + HOUR_CHANGE " +
				"else '00:00' end)  <= :before " +
				") "
				+ ") " +
				"      GROUP BY appl_no " +
				"   ) " +
				"   SS ON RM.APPL_no = SS.APPL_NO " +
				"   AND RM.TX_ID = SS.TX_ID " +
				"   and rm.reg_status = 'R' " +
				") " +
				"b on b.gross_ton between a.tonFrom " +
				"and a.tonTo " +
				"group by a.tonFrom,a.tonTo " +
				"order by a.tonFrom desc ";
		Query query = em.createNativeQuery(sqlString);
		query.setParameter("before", reportDate);
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
		return em.createQuery("From RegMaster rm where rm.applNo in (:applNoList)").setParameter("applNoList", list).getResultList();
	}
	@Override
	public Long[] getTakeUpRate(Date fromDate, Date toDate) {
		Query query = em.createNativeQuery("select 1, count(*) cnt " +
				"from demand_note_items ini " +
				"where ini.generation_time between :fromDate and :toDate " +
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
			"   where TRACK_CODE = :trackCode " +
			") " +
			"and rmh.TX_ID = " +
			"( " +
			"   select max(TX_ID) " +
			"   from REG_MASTERS_HIST " +
			"   where APPL_NO IN " +
			"   ( " +
			"      select APPL_NO " +
			"      from REG_MASTERS_HIST " +
			"      where TRACK_CODE = :trackCode " +
			"   ) " +
			") " +
			"and rmh.TRACK_CODE = :trackCode ");
		query.setParameter("trackCode", trackCode);
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
	public void logFsqc(String jsonInputString, String string) {
		Query query = em.createNativeQuery("insert into FSQC_RESPONSE (SENT_DATE, SENT, REPLY, SUCCESS) values (?,?,?,?)");
		query.setParameter(1, new Date());
		query.setParameter(2, jsonInputString);
		query.setParameter(3, string);
		query.setParameter(4, "Y");
		query.executeUpdate();
	}
	@Override
	public void logFsqc(String jsonInputString, Exception e) {
		Query query = em.createNativeQuery("insert into FSQC_RESPONSE (SENT_DATE, SENT, REPLY, SUCCESS) values (?,?,?,?)");
		query.setParameter(1, new Date());
		query.setParameter(2, jsonInputString);
		query.setParameter(3, e.getMessage());
		query.setParameter(4, "N");
		query.executeUpdate();

	}

}