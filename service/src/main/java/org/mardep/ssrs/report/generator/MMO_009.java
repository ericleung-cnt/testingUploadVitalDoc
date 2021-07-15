package org.mardep.ssrs.report.generator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.mardep.ssrs.domain.codetable.CurrencyCode;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.report.IReportGenerator;
import org.mardep.ssrs.report.bean.KeyValue;
import org.mardep.ssrs.report.bean.MMO_Distribution;
import org.mardep.ssrs.report.bean.NationalityWagePojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JasperReport;

/**
 *
 * MMO Report-009: Average Monthly Wages by Rank/Rating
 *
 * @author Leo.LIANG
 *
 */
@Service("RPT_MMO_009")
public class MMO_009 extends AbstractAverageWage implements IReportGenerator{

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public String getReportFileName() {
		return "MMO_distributionWithMsg.jrxml";
	}

	@Override
	public byte[] generate(Map<String, Object> inputParam) throws Exception {
		Date reportDate = (Date)inputParam.get("reportDate");
		String rankingRating = (String)inputParam.get("rankingRating");
		Map<String,Double> currecyMap = (Map)inputParam.get("Currency");
		currecyMap.keySet().removeIf(Objects::isNull);
		
		logger.info("####### RPT_MMO_009  #########");
		logger.info("Report Date:{}", reportDate);
		logger.info("RankingRating, {}-{}", new Object[]{rankingRating});

		DecimalFormat format = new DecimalFormat("$#,###");
		List<Object[]> list = ratingDao.sumSalaryByRank(reportDate, rankingRating);
//		.stream().map(
//				o-> {
//						return new KeyValue((String)o[0], format.format(o[1]));
//					}
//				).collect(Collectors.toList());

		Set<String> dollorCodeNotFoundSet = new HashSet<>();
		String reportId = "SRS1120";
		String reportTitle = "Average Monthly Wages by Rank";
		String currentUser = UserContextThreadLocalHolder.getCurrentUserName();
		List<NationalityWagePojo> pojoList = new ArrayList<>();
		for(Object row : list) {
			Object[] array = (Object[]) row;
			NationalityWagePojo pojo = new NationalityWagePojo();
			pojo.setRank(array[0].toString());
//			pojo.setShipTypeCode(array[1].toString());
//			pojo.setNationality_ID(array[0].toString());
			pojo.setNationalityEngDesc((String) array[1]);
			pojo.setSalary( (BigDecimal)  array[2]);
			pojo.setCurrency((String) array[3]);
			pojo.setUSDsalary(BigDecimal.ZERO);
			String currency = pojo.getCurrency();

			if(currency!=null) {
				
				if(currency.equals(CurrencyCode.USD.name())){
					pojo.setUSDsalary(pojo.getSalary());
					pojoList.add(pojo);
				}
				else if(currecyMap.containsKey(currency)) {
				BigDecimal divisor = BigDecimal.valueOf( ((Number)currecyMap.get(currency)).doubleValue());
				pojo.setUSDsalary( pojo.getSalary().divide(divisor,3,BigDecimal.ROUND_HALF_EVEN));
				pojoList.add(pojo);
					
				}
				else {
					dollorCodeNotFoundSet.add(currency);
				}
			}else {
				pojoList.add(pojo);
			}
		}
		
		List<KeyValue> resultList=new ArrayList<>();
		Map<String, List<NationalityWagePojo>> groupByRank = pojoList.stream().collect(Collectors.groupingBy(o->o.getRank()));
		
		
		for(Map.Entry<String, List<NationalityWagePojo>> groupByRankmap :groupByRank.entrySet()) {
			String rank = groupByRankmap.getKey();
			BigDecimal sum = BigDecimal.ZERO;
			for(NationalityWagePojo o :groupByRankmap.getValue()) {
				sum = sum.add(o.getUSDsalary());
			}
			resultList.add(	new KeyValue(rank, (sum.divide(BigDecimal.valueOf(groupByRankmap.getValue().size()),3,RoundingMode.HALF_UP).toString())));
		}
		
		
		String title = "";
		if(rankingRating.equalsIgnoreCase("O")){
			title = "OFFICER";
		}else if(rankingRating.equalsIgnoreCase("R")){
			title = "RATING";
		}

		List<MMO_Distribution> distributionList = new ArrayList<MMO_Distribution>();
		distributionList.add(new MMO_Distribution(title, resultList,  ""));

		JasperReport kvJR = jasperReportService.getJasperReport("KeyValue.jrxml");
		JasperReport mmoPHJR = jasperReportService.getJasperReport("MMO_PrintHorizontal.jrxml");

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("printHorizontal", mmoPHJR);
		map.put("keyValue", kvJR);
		map.put(REPORT_ID, reportId);
		map.put(REPORT_TITLE, reportTitle);
		map.put(USER_ID, currentUser!=null?currentUser:"SYSTEM");
		if(dollorCodeNotFoundSet.size()>0) {
			String msg = String.format(dollorCodeNotFoundErrMsg,String.join(",", dollorCodeNotFoundSet));
			map.put(errorMsg, msg);
		}

		return generate(distributionList, map);
	}

}
