package org.mardep.ssrs.reports;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class MMO_ReportTest {

	public List<Object> getDataSetMock(Date reportDateFrom, Date reportDateTo, Long nationalityId){
		List<Object> rows = new ArrayList<Object>();
		
		Wages wage1 = new Wages(); 
		wage1.ranking = "Master";
		wage1.nationalityId = 1L;
		wage1.currency = "USD";
		wage1.salary = 1.0;
		wage1.count = 1;
		rows.add(wage1);
		
		Wages wage2 = new Wages(); 
		wage2.ranking = "Master";
		wage2.nationalityId = 1L;
		wage2.currency = "USD";
		wage2.salary = 1.1;
		wage2.count = 1;
		rows.add(wage2);
		
		Wages wage3 = new Wages(); 
		wage3.ranking = "Master";
		wage3.nationalityId = 1L;
		wage3.currency = "USD";
		wage3.salary = 1.2;
		wage3.count = 1;
		rows.add(wage3);
		
		Wages wage4 = new Wages(); 
		wage4.ranking = "Master";
		wage4.nationalityId = 1L;
		wage4.currency = "USD";
		wage4.salary = 1.3;
		wage4.count = 1;
		rows.add(wage4);
		
		Wages wage5 = new Wages(); 
		wage5.ranking = "Chief Officer";
		wage5.nationalityId = 1L;
		wage5.currency = "HKD";
		wage5.salary = 1.4;
		wage5.count = 1;
		rows.add(wage5);
		
		return rows;
	}
	
	public Map<String, Double> generateReportData(List<Object> rows, Map<String, Double> exchangeMap){
		Map<String, WagesSummary> prepareWages = new HashMap<String, WagesSummary>();
		Map<String, Double> reportData = new HashMap<String, Double>();
		for (int i=0; i<rows.size(); i++) {
			Wages wage = (Wages)rows.get(i);
			if (!prepareWages.containsKey(wage.ranking)) {
				WagesSummary ws = new WagesSummary();
				ws.totalExchangedSalary=0.0;
				ws.totalCount=0;
				prepareWages.put(wage.ranking, ws);
			}
			WagesSummary wageSummary = prepareWages.get(wage.ranking);
			Double rate = exchangeMap.get(wage.currency);
			if (rate==null) {
				rate = exchangeMap.get("OTHERS");
			}
			wageSummary.totalExchangedSalary += wage.salary * rate;
			wageSummary.totalCount += wage.count;
			prepareWages.put(wage.ranking, wageSummary);			
		}
			
		for (Map.Entry<String, WagesSummary> entry : prepareWages.entrySet()) {
			String rank = entry.getKey();
			WagesSummary wageSummary = entry.getValue();
			Double avg = wageSummary.totalExchangedSalary / wageSummary.totalCount;
			reportData.put(rank, avg);
		}
		return reportData;
	}

	@Test
	public void averageWagesCurrencyTest() {
		Map<String, Double> exchangeMap = new HashMap<String, Double>(); 
		exchangeMap.put("USD", 1.0);
		exchangeMap.put("HKD", 7.78);
		
		List<Object> rows = getDataSetMock(null, null, null);
		Map<String, Double> reportData = generateReportData(rows, exchangeMap);
		System.out.println("hello");
	}	
}

class Wages {
	public String ranking;
	public Long nationalityId;
	public double salary;
	public String currency;
	public int count;
	//public double exchangedSalary;
}

class WagesSummary {
	//public String ranking;
	public double totalExchangedSalary;
	public int totalCount;
}	