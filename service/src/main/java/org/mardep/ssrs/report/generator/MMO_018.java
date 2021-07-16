package org.mardep.ssrs.report.generator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.mardep.ssrs.dao.codetable.INationalityDao;
import org.mardep.ssrs.dao.codetable.IRankDao;
import org.mardep.ssrs.dao.codetable.IShipTypeDao;
import org.mardep.ssrs.domain.codetable.Nationality;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.report.IReportGenerator;
import org.mardep.ssrs.service.IJasperReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JRParameter;

/**
 * 
 * MMO Report-018: Average Monthly Wages by Rank/Rating by Nationality
 * 
 *
 */
@Service("RPT_MMO_018")
public class MMO_018 extends AbstractAverageWage implements IReportGenerator{

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	IShipTypeDao shipTypeDao;
	
	@Autowired
	IRankDao rankDao;
	
	@Autowired
	INationalityDao NationalityDao;

	@Autowired
	IJasperReportService jasper;
	
	@Override
	public String getReportFileName() {
		return "MMO_018_1.jrxml";
	}
	
	private HashMap<String, Object> newResult(){
		HashMap<String, Object> result = new HashMap<String, Object>();
		return result;
	}
	
	private List<Object> getDataSetFromDB(Date reportDateFrom, Date reportDateTo, Long nationalityId){
		List<Object> rows = rankDao.getAverageWagesByNationality(reportDateFrom, reportDateTo, nationalityId);
		return rows;
	}
	
	public List<Object> getDataSetMock(Date reportDateFrom, Date reportDateTo, Long nationalityId){
		List<Object> rows = new ArrayList<Object>();
		
		
		Wages wage1 = new Wages(); 
		wage1.ranking = "Master";
		wage1.nationalityId = BigDecimal.valueOf(13);
		wage1.currency = "USD";
		wage1.salary =BigDecimal.valueOf( 1.0);
		wage1.count = 1;
		rows.add(wage1);
		
		Wages wage2 = new Wages(); 
		wage2.ranking = "Master";
		wage2.nationalityId = BigDecimal.valueOf(13);
		wage2.currency = "USD";
		wage2.salary = BigDecimal.valueOf(1.1);
		wage2.count = 1;
		rows.add(wage2);
		
		Wages wage3 = new Wages(); 
		wage3.ranking = "Master";
		wage3.nationalityId = BigDecimal.valueOf(13);
		wage3.currency = "USD";
		wage3.salary = BigDecimal.valueOf(1.2);
		wage3.count = 1;
		rows.add(wage3);
		
		Wages wage4 = new Wages(); 
		wage4.ranking = "Master";
		wage4.nationalityId = BigDecimal.valueOf(13);
		wage4.currency = "JPY";
		wage4.salary = BigDecimal.valueOf(100);
		wage4.count = 1;
		rows.add(wage4);
		
		Wages wage5 = new Wages(); 
		wage5.ranking = "Chief Officer";
		wage5.nationalityId = BigDecimal.valueOf(13);
		wage5.currency = "HKD";
		wage5.salary = BigDecimal.valueOf(1.4);
		wage5.count = 1;
		rows.add(wage5);
		
		Wages wage6 = new Wages(); 
		wage6.ranking = "Officer 1";
		wage6.nationalityId = BigDecimal.valueOf(13);
		wage6.currency = "HKD";
		wage6.salary = BigDecimal.valueOf(1.4);
		wage6.count = 1;
		rows.add(wage6);
		
		Wages wage7 = new Wages(); 
		wage7.ranking = "Officer 2";
		wage7.nationalityId = BigDecimal.valueOf(13);
		wage7.currency = "HKD";
		wage7.salary = BigDecimal.valueOf(1.4);
		wage7.count = 1;
		rows.add(wage7);
		
		Wages wage8 = new Wages(); 
		wage8.ranking = "Officer 3";
		wage8.nationalityId = BigDecimal.valueOf(13);
		wage8.currency = "HKD";
		wage8.salary = BigDecimal.valueOf(1.4);
		wage8.count = 1;
		rows.add(wage8);
		
		Wages wage9 = new Wages(); 
		wage9.ranking = "Officer 4";
		wage9.nationalityId = BigDecimal.valueOf(13);
		wage9.currency = "HKD";
		wage9.salary = BigDecimal.valueOf(1.4);
		wage9.count = 1;
		rows.add(wage9);
		
		Wages wage10 = new Wages(); 
		wage10.ranking = "Officer 5";
		wage10.nationalityId = BigDecimal.valueOf(13);
		wage10.currency = "HKD";
		wage10.salary = BigDecimal.valueOf(1.4);
		wage10.count = 1;
		rows.add(wage10);
		
		Wages wage11 = new Wages(); 
		wage11.ranking = "Officer 6";
		wage11.nationalityId = BigDecimal.valueOf(13);
		wage11.currency = "HKD";
		wage11.salary = BigDecimal.valueOf(1.4);
		wage11.count = 1;
		rows.add(wage11);
		
		Wages wage12 = new Wages(); 
		wage12.ranking = "Officer 7";
		wage12.nationalityId = BigDecimal.valueOf(13);
		wage12.currency = "HKD";
		wage12.salary = BigDecimal.valueOf(1.4);
		wage12.count = 1;
		rows.add(wage12);
		
		Wages wage13 = new Wages(); 
		wage13.ranking = "Officer 8";
		wage13.nationalityId = BigDecimal.valueOf(13);
		wage13.currency = "HKD";
		wage13.salary = BigDecimal.valueOf(1.4);
		wage13.count = 1;
		rows.add(wage13);
		
		Wages wage14 = new Wages(); 
		wage14.ranking = "Officer 9";
		wage14.nationalityId = BigDecimal.valueOf(13);
		wage14.currency = "HKD";
		wage14.salary = BigDecimal.valueOf(1.4);
		wage14.count = 1;
		rows.add(wage14);
		
		Wages wage15 = new Wages(); 
		wage15.ranking = "Officer 10";
		wage15.nationalityId = BigDecimal.valueOf(13);
		wage15.currency = "HKD";
		wage15.salary = BigDecimal.valueOf(1.4);
		wage15.count = 1;
		rows.add(wage15);
		
		Wages wage16 = new Wages(); 
		wage16.ranking = "Officer 11";
		wage16.nationalityId = BigDecimal.valueOf(13);
		wage16.currency = "HKD";
		wage16.salary = BigDecimal.valueOf(1.4);
		wage16.count = 1;
		rows.add(wage16);
		
		Wages wage17 = new Wages(); 
		wage17.ranking = "Officer 12";
		wage17.nationalityId = BigDecimal.valueOf(13);
		wage17.currency = "HKD";
		wage17.salary = BigDecimal.valueOf(1.4);
		wage17.count = 1;
		rows.add(wage17);
		
		return rows;
	}
	
	public List<Object> getDataSet(Date reportDateFrom, Date reportDateTo, Long nationalityId) {
		return getDataSetFromDB(reportDateFrom, reportDateTo, nationalityId);
	}
	
	public List<HashMap<String, Object>> generateReportData(List<Object> rows, Map<String, Object> exchangeMap, Set dollorCodeNotFoundSet){
		
		HashMap<String, WagesSummary> prepareWages = new LinkedHashMap<String, WagesSummary>();
		//Map<String, Double> reportData = new HashMap<String, Double>();
		List<HashMap<String, Object>> results = new ArrayList<HashMap<String,Object>>();
		for (int i=0; i<rows.size(); i++) {
			//Wages wage = (Wages)rows.get(i);
			Wages wage = new Wages();
				wage.ranking = (String)((Object[]) rows.get(i))[0];
				wage.nationalityId = (BigDecimal)((Object[]) rows.get(i))[1];
				wage.salary = (BigDecimal)((Object[]) rows.get(i))[2];
				wage.currency = (String)((Object[]) rows.get(i))[3];
				wage.count = (int)((Object[]) rows.get(i))[5];
			
			if (!prepareWages.containsKey(wage.ranking)) {
				WagesSummary ws = new WagesSummary();
				ws.totalExchangedSalary=BigDecimal.ZERO;
				ws.totalCount=0;
				prepareWages.put(wage.ranking, ws);
			}
			WagesSummary wageSummary = prepareWages.get(wage.ranking);
			//BigDecimal rate = BigDecimal.valueOf( ((Number) exchangeMap.get(wage.currency)).doubleValue());
			
			if (exchangeMap.get(wage.currency)==null) {
				
				//rate = BigDecimal.valueOf(exchangeMap.get("OTHERS"));
				dollorCodeNotFoundSet.add(wage.currency);
				//errorMsg += String.format("%s have a unknown exchange rate.\n",wage.ranking);
			}else{
//				Double r = Double.valueOf(exchangeMap.get(wage.currency));
				BigDecimal rate = new BigDecimal( ((String) exchangeMap.get(wage.currency)));
//				BigDecimal rate = BigDecimal.valueOf(r);
				wageSummary.totalExchangedSalary = wageSummary.totalExchangedSalary.add(wage.salary.divide(rate,3,BigDecimal.ROUND_HALF_EVEN));
				wageSummary.totalCount += wage.count;
				prepareWages.put(wage.ranking, wageSummary);			
			}
		}
		
		int count = 1;	
		HashMap<String, Object> result = new HashMap<String, Object>();
		for (Map.Entry<String, WagesSummary> entry : prepareWages.entrySet()) {

			String rank = entry.getKey();
			WagesSummary wageSummary = entry.getValue();
			BigDecimal avg = wageSummary.totalExchangedSalary.divide(BigDecimal.valueOf(wageSummary.totalCount == 0 ? 1 : wageSummary.totalCount),3, BigDecimal.ROUND_HALF_EVEN);//.setScale(3, BigDecimal.ROUND_HALF_EVEN) ;
			result.put("rank"+count, rank);
			result.put("avg"+count, avg);

			count++;
			//ship to next row
			if (count>12 && prepareWages.size()>= count) {
				results.add(result);
				result = new HashMap<String, Object>();
				count = 1;
			}
			
		}
		//end of data set
		
			results.add(result);
			//reportData.put(rank, avg);
			return results;
}
		
		//return reportData;
	
	@Override
	public byte[] generate(Map<String, Object> inputParam) throws Exception {
		
		//Define inputParam
		Date reportDateFrom = (Date)inputParam.get("reportDateFrom");
		Date reportDateTo = (Date)inputParam.get("reportDateTo");
		Long nationalityId = (Long)inputParam.get("nationality");
		inputParam.put(JRParameter.REPORT_LOCALE,  new Locale("en", "US"));
		
			//rate inputParam missing
		
		//Define Mock exchange rate
		//Map<String, Double> exchangeMap = new HashMap<String, Double>(); 
//		exchangeMap.put("USD", 1.0);
//		exchangeMap.put("HKD", 7.78);
//		exchangeMap.put("RMB", 0.15477);
//		exchangeMap.put("EUR", 1.1855);
//		exchangeMap.put("GBP", 1.3833);
//		exchangeMap.put("JPY", 0.0090052);
		
		//Define custom exchange rate
		Map<String,Object> exchangeMap = (Map)inputParam.get("Currency");
		exchangeMap.keySet().removeIf(Objects::isNull);
		exchangeMap.values().removeAll(Collections.singleton(null));
		String exchangeRateString = exchangeMap.toString();
		inputParam.put("exchangeRate", exchangeRateString);
		
		//get data from mock Data set
		List<Object> rows = getDataSet(reportDateFrom, reportDateTo, nationalityId);
		Set<String> dollorCodeNotFoundSet = new HashSet<>();
		List<HashMap<String, Object>> results = generateReportData(rows, exchangeMap, dollorCodeNotFoundSet);
		//Map<String, Object> map = new HashMap<String, Object>();
		
		if(dollorCodeNotFoundSet.size()>0) {
			String msg = String.format(dollorCodeNotFoundErrMsg,String.join(",", dollorCodeNotFoundSet));
			inputParam.put("errorCode", msg);
		}
		String nationality_name = NationalityDao.findById(nationalityId).getCountryEngDesc();
		inputParam.put("nationality",nationality_name);
		String currentUser = UserContextThreadLocalHolder.getCurrentUserName();
		inputParam.put("userId", currentUser);
		return jasper.generateReport(getReportFileName(), results, inputParam);
		
//		Double avg_salary = 0.0;
//		Double total_rank_salary = 0.0;
//		int count = 0;
//		Double hkd = 0.12874;
//		Double rmb = 0.15477;
//		Double eur = 1.1855;
//		Double gbp = 1.3833;
//		Double jpy = 0.0090052;
//		
//		
//		List<Object> rows = getDataSet(reportDateFrom, reportDateTo, nationalityId); // rankDao.getAverageWagesByNationality(reportDateFrom, reportDateTo, nationalityId);
//
//		List<HashMap<String, Object>> results = new ArrayList<HashMap<String,Object>>();
//		
//		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("reportDateFrom", reportDateFrom);
//		params.put("reportDateTo", reportDateTo);
//		
//		Nationality nationality_name = NationalityDao.findById(nationalityId);
//		HashMap<String, List<Double>> rankMap = new HashMap<String, List<Double>>();
//		
//		for (int y = 0; y < rows.size(); y+=12) {
//			
//			//loop for average salary calculation and put in rankMap
//			for (int i=0; i<rows.size(); i++) {
//				String rank = (String)((Object[])rows.get(i))[0];
//				if (!rankMap.containsKey(rank)) {
//					List<Double> value = new ArrayList<Double>();
//					value.add(0.0d);
//					value.add(0.0d);
//					rankMap.put(rank, value);
//				}
//				double salary = (double)((Object[])rows.get(i))[2];
//				String currency = (String)((Object[])rows.get(i))[3];
//				int recCount = (int)((Object[])rows.get(i))[4];
//				List<Double> rankValue = rankMap.get(rank);
//				switch(currency){		
//					case "USD":
//						System.out.println("This is "+currency);
//						//count++;
//						break;
//					case "HKD":
//						System.out.println("This is "+currency);
//						salary *= hkd;
//						break;
//					case "RMB":
//						System.out.println("This is "+currency);
//						salary *= rmb;
//						break;
//					case "EUR":
//						System.out.println("This is "+currency);
//						salary *= eur;
//						break;
//					case "GBP":
//						System.out.println("This is "+currency);
//						salary *= gbp;
//						break;
//					case "JPY":
//						System.out.println("This is "+currency);
//						salary *= jpy;
//						break;
//					default:
//						System.out.println("This is unknow currency!!");
//				}
//				rankValue.set(0, rankValue.get(0)+salary);
//				rankValue.set(1, rankValue.get(1)+recCount);	
//			}
//2021.07.07
//			result.put("rank"+i, rankMap.getKey();
//			result.put("avg"+i, avg_salary);
//			
//			for (int j = 2; j <= 12; j++) {
//				HashMap<String, Object> result = new HashMap<String, Object>();
//				if (rows.size() >= i + j) {
//					Object[] rowArray = (Object[]) rows.get(i + j - 1);
//					result.put("rank" + j, rowArray[0]);
//					result.put("avg" + j, ((BigDecimal)rowArray[2]).intValue());	
//				} 
//			}
//			results.add(result);
//		}
//		return jasper.generateReport(getReportFileName(), results, inputParam);
//		}
//		
//		
////		HashMap<Object, List<Object>> RankMap = new HashMap<>();
//
// 		for (int i = 0; i < rows.size(); i+=12) {		
// 			HashMap<String, Object> result = new HashMap<String, Object>();
//// 			result.put("reportDateFrom", reportDateFrom);
//// 			result.put("reportDateTo", reportDateTo);
// 			result.put("nationality", nationality_name.getEngDesc());
// 			
// 			rank1
// 			avg1
// 			
//			for (int j=0; j<rows.size(); j++) {
//				String name = (String)((((Object[]) rows.get(j))[0]));
////				RankMap.put(((((Object[]) rows.get(i))[0])),((Object[]) rows.get(i)));
//				List<Object> list = RankMap.get(name);
//				if(list==null) {
//					list = new ArrayList<>();
//					RankMap.put(name,list);
//				}
//				list.add(rows.get(j));
//			}
//				
//			}
//			
//			//Calculate salary with exchange rate
//			total_rank_salary = 0.0;
//			count = 0;
//			avg_salary = 0.0;
//
//			//loop for accumulate total salary
//			for (int j = 2; j <= 12; j++) {
//
//				for (int x = 0; x < rows.size(); x++) {
//				Double salary = (Double)((Object[]) rows.get(j))[2];
//				// if they are same rank
//				if (((Object[]) rows.get(x))[0].equals(((Object[]) rows.get(i))[0])) {
//					String currency = (String)((Object[]) rows.get(j))[3];
//					switch(currency){
//					
//					case "USD":
//						System.out.println("This is "+currency);
//						count++;
//						break;
//					case "HKD":
//						System.out.println("This is "+currency);
//						salary *= hkd;
//						count++;
//						break;
//					case "RMB":
//						System.out.println("This is "+currency);
//						salary *= rmb;
//						count++;
//						break;
//					case "EUR":
//						System.out.println("This is "+currency);
//						salary *= eur;
//						count++;
//						break;
//					case "GBP":
//						System.out.println("This is "+currency);
//						salary *= gbp;
//						count++;
//						break;
//					case "JPY":
//						System.out.println("This is "+currency);
//						salary *= jpy;
//						count++;
//						break;
//					default:
//						System.out.println("This is unknow currency!!");
//					}
//				total_rank_salary += salary;
//				}
//				}
//			}
//			avg_salary = total_rank_salary/count;
//			result.put("rank"+i, ((Object[]) rows.get(i))[0]);
//			result.put("avg"+i, avg_salary);
// 2021.07.07			
//			/*
//			for (int j = 2; j <= 7; j++) {
//				if (rows.size() >= i + j) {
//					Object[] rowArray = (Object[]) rows.get(i + j - 1);
//					rating = rowArray[3];
//					if (rating.equals(lastRating)) {
//						result.put("rank" + j, rowArray[0]);
//						result.put("age" + j, ((BigDecimal)rowArray[2]).intValue());
//					} else {
//						i = i -7 + j - 1;
//						break;
//					}
//				} else {
//					break;
//				}
//			}*/
//			results.add(result);
//		}
//		return jasper.generateReport(getReportFileName(), results, inputParam);
//
//	}
//		
//		//List reportList = rankDao.getAverageWagesByNationality(reportDateFrom, reportDateTo, nationalityId);
//		return generate(reportList.size() > 1 ? reportList.subList(1, reportList.size()) : Collections.emptyList(), 
//				reportList.size() > 0 ? (HashMap<String, Object>) reportList.get(0) : new HashMap<>());
	}
	
	class Wages {
		public String ranking;
		public BigDecimal nationalityId;
		public BigDecimal salary;
		public String currency;
		public int count;
		//public double exchangedSalary;
	}

	class WagesSummary {
		//public String ranking;
		public BigDecimal totalExchangedSalary;
		public int totalCount;
	}
	
}
