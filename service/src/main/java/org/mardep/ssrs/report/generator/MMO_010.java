package org.mardep.ssrs.report.generator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.mardep.ssrs.dao.codetable.ICrewDao;
import org.mardep.ssrs.dao.codetable.IRankDao;
import org.mardep.ssrs.domain.codetable.CurrencyCode;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.report.IReportGenerator;
import org.mardep.ssrs.report.bean.MMO_010Bean;
import org.mardep.ssrs.report.bean.NationalityWagePojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * MMO Report-010: Average Monthly Wages of Rank-Wise Crew by Nationality
 *
 * @author Leo.LIANG
 *
 */
@Service("RPT_MMO_010")
public class MMO_010 extends AbstractAverageWage implements IReportGenerator{

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	ICrewDao crewDao;

	@Autowired
	IRankDao rankDao;

	@Override
	public String getReportFileName() {
		return "MMO_010.jrxml";
	}

	@Override
	public byte[] generate(Map<String, Object> inputParam) throws Exception {
		Date reportDate = (Date)inputParam.get("reportDate");
		String reportDateDisplay = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(reportDate);
		Long rankId = (Long)inputParam.get("rank");

		logger.info("####### RPT_MMO_010  #########");
		logger.info("Report Date:{}", reportDate);
		logger.info("Rank, {}-{}", rankId);

		String reportId = "SRS1140";
		String reportTitle = "Average Monthly Wages(USD) of Rank-Wise Crew by Nationality "+ reportDateDisplay;
		String currentUser = UserContextThreadLocalHolder.getCurrentUserName();
		Map<String,Object> currecyMap = (Map)inputParam.get("Currency");
		currecyMap.keySet().removeIf(Objects::isNull);
		String exchangeRateString = currecyMap.toString();
		
		
		List<?> list = crewDao.getRankWiseCrewAverageWagesByNationality(reportDate, rankId);
		Map<String, Map<String, MMO_010Bean>> ranks = new HashMap<>();
		List<String> typeSeq = new ArrayList<>();
		List<Map<String, Object>> reportData = new ArrayList<>();
		Map<String, Map<String, Integer>> counts = new HashMap<>();
		Map<String, Map<String, BigDecimal>> totalSalary = new HashMap<>();
		DecimalFormat format = new DecimalFormat("$#,###");
		List<NationalityWagePojo> pojoList = new ArrayList<>();
		Set<String> dollorCodeNotFoundSet = new HashSet<>();
		//conver to USD dollor 
		for(Object row : list) {
			Object[] array = (Object[]) row;
			NationalityWagePojo pojo = new NationalityWagePojo();
			pojo.setRank(array[0].toString());
			pojo.setShipTypeCode(array[1].toString());
//			pojo.setNationality_ID(array[0].toString());
			pojo.setNationalityEngDesc((String) array[2]);
			pojo.setCurrency((String) array[4]);
			pojo.setSalary( (BigDecimal)  array[3]);
			pojo.setUSDsalary(BigDecimal.ZERO);
			String currency = pojo.getCurrency();

			if(currency!=null) {
				
				if(currency.equals(CurrencyCode.USD.name())){
					pojo.setUSDsalary(pojo.getSalary());
					pojoList.add(pojo);
				}
				else if(currecyMap.containsKey(currency)) {
				//BigDecimal divisor = BigDecimal.valueOf( ((Number)currecyMap.get(currency)).doubleValue());
				BigDecimal divisor = new BigDecimal( ((String)currecyMap.get(currency)));
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
		
		for (Object row : list) {
			Object[] array = (Object[]) row; //"r.ENG_DESC,  SS_ST_SHIP_TYPE_CODE, N.ENG_DESC, avg(C.SALARY) SALARY "
			int indexOf = typeSeq.indexOf(array[1]);
			if (indexOf == -1) {
				typeSeq.add((String) array[1]);
			}
		}
		
		Map<String, List<NationalityWagePojo>> groupByRank = pojoList.stream().collect(Collectors.groupingBy(o->o.getRank()));
		
		
		for(Map.Entry<String, List<NationalityWagePojo>> groupByRankmap :groupByRank.entrySet()) {
			 String rank = groupByRankmap.getKey();
			 List<NationalityWagePojo> groupByRankList = groupByRankmap.getValue();
			

		//grouping by shiptypeCode
		Map<String, List<NationalityWagePojo>> groupByShipType = groupByRankList.stream().collect(Collectors.groupingBy(o->o.getShipTypeCode()));
		
		for(Map.Entry<String, List<NationalityWagePojo>> map :groupByShipType.entrySet()) {
			 String key = map.getKey();
			 List<NationalityWagePojo> groupByShipTypeList = map.getValue();
			 int countByShipType =groupByShipTypeList.size();
//			 String rank =groupByShipTypeList.get(0).getRank();
			 String shipType =key;
			 
//			 groupBy nation and sum 
			 Map<String, List<NationalityWagePojo>> groupByNation = groupByShipTypeList.stream().collect(Collectors.groupingBy(o->o.getNationalityEngDesc()));
				Map<String, MMO_010Bean> nationalities = ranks.get(rank);
				if (nationalities == null) {
					ranks.put(rank, (nationalities = new HashMap<>()));
				}
				
			 for(Map.Entry<String, List<NationalityWagePojo>> map2 :groupByNation.entrySet()) {
				  for( NationalityWagePojo pojo : map2.getValue() ) {
					  int indexOf = typeSeq.indexOf(shipType);
					  String nation = pojo.getNationalityEngDesc();
					  MMO_010Bean subbean = nationalities.get(nation);
						if (subbean == null) {
							nationalities.put(nation, (subbean = new MMO_010Bean()));
							subbean.setRank(rank);
							subbean.setNationality(nation);
							if (typeSeq.size() > 0) {
								subbean.setType1(typeSeq.get(0));
							}
							if (typeSeq.size() > 1) {
								subbean.setType2(typeSeq.get(1));
							}
							if (typeSeq.size() > 2) {
								subbean.setType3(typeSeq.get(2));
							}
							if (typeSeq.size() > 3) {
								subbean.setType4(typeSeq.get(3));
							}
							if (typeSeq.size() > 4) {
								subbean.setType5(typeSeq.get(4));
							}
							if (typeSeq.size() > 5) {
								subbean.setType6(typeSeq.get(5));
							}
							if (typeSeq.size() > 6) {
								subbean.setType7(typeSeq.get(6));
							}
							HashMap<String, Object> reportRow = new HashMap<>();
							reportRow.put("bean", subbean);
							reportData.add(reportRow);
						}
						switch (indexOf) {
						case 0:
							  
							subbean.setSumSalary1(subbean.getSumSalary1().add(pojo.getUSDsalary()));
							subbean.setCount1(countByShipType);
							break;
						case 1:
							subbean.setSumSalary2(subbean.getSumSalary2().add(pojo.getUSDsalary()));
							subbean.setCount2(countByShipType);
							break;
						case 2:
							subbean.setSumSalary3(subbean.getSumSalary3().add(pojo.getUSDsalary()));
							subbean.setCount3(countByShipType);
							break;
						case 3:
							subbean.setSumSalary4(subbean.getSumSalary4().add(pojo.getUSDsalary()));
							subbean.setCount4(countByShipType);
							break;
						case 4:
							subbean.setSumSalary5(subbean.getSumSalary5().add(pojo.getUSDsalary()));
							subbean.setCount5(countByShipType);
							break;
						case 5:
							subbean.setSumSalary6(subbean.getSumSalary6().add(pojo.getUSDsalary()));
							subbean.setCount6(countByShipType);
							break;
						case 6:
							subbean.setSumSalary7(subbean.getSumSalary7().add(pojo.getUSDsalary()));
							subbean.setCount7(countByShipType);
							break;
						default:
							break;
						}
						
					  
				  }
				 
				 
				 
			 }
			 
			 
			 
		}
		
	}
		
		
		
		
		

//		
//		for (Object row : list) {
//			Object[] array = (Object[]) row; //"r.ENG_DESC,  SS_ST_SHIP_TYPE_CODE, N.ENG_DESC, avg(C.SALARY) SALARY "
//
//			String rank = (String) array[0];
//			String shipType = (String) array[1];
//			String nation = (String) array[2];
//			BigDecimal salary = (BigDecimal) array[3];
//			int count = (int) array[4];
//
//			Map<String, MMO_010Bean> nationalities = ranks.get(rank);
//			if (nationalities == null) {
//				ranks.put(rank, (nationalities = new HashMap<>()));
//			}
//			int indexOf = typeSeq.indexOf(shipType);
//			MMO_010Bean subbean = nationalities.get(nation);
//			if (subbean == null) {
//				nationalities.put(nation, (subbean = new MMO_010Bean()));
//				subbean.setRank(rank);
//				subbean.setNationality(nation);
//				if (typeSeq.size() > 0) {
//					subbean.setType1(typeSeq.get(0));
//				}
//				if (typeSeq.size() > 1) {
//					subbean.setType2(typeSeq.get(1));
//				}
//				if (typeSeq.size() > 2) {
//					subbean.setType3(typeSeq.get(2));
//				}
//				if (typeSeq.size() > 3) {
//					subbean.setType4(typeSeq.get(3));
//				}
//				if (typeSeq.size() > 4) {
//					subbean.setType5(typeSeq.get(4));
//				}
//				if (typeSeq.size() > 5) {
//					subbean.setType6(typeSeq.get(5));
//				}
//				if (typeSeq.size() > 6) {
//					subbean.setType7(typeSeq.get(6));
//				}
//				HashMap<String, Object> reportRow = new HashMap<>();
//				reportRow.put("bean", subbean);
//				reportData.add(reportRow);
//			}
//			switch (indexOf) {
//			case 0:
//				subbean.setSalary1(format.format(salary));
//				subbean.setCount1(count);
//				break;
//			case 1:
//				subbean.setSalary2(format.format(salary));
//				subbean.setCount2(count);
//				break;
//			case 2:
//				subbean.setSalary3(format.format(salary));
//				subbean.setCount3(count);
//				break;
//			case 3:
//				subbean.setSalary4(format.format(salary));
//				subbean.setCount4(count);
//				break;
//			case 4:
//				subbean.setSalary5(format.format(salary));
//				subbean.setCount5(count);
//				break;
//			case 5:
//				subbean.setSalary6(format.format(salary));
//				subbean.setCount6(count);
//				break;
//			case 6:
//				subbean.setSalary7(format.format(salary));
//				subbean.setCount7(count);
//				break;
//			default:
//				break;
//			}
//
//			Map<String, BigDecimal> typeSalaryMap = totalSalary.get(rank);
//			if (typeSalaryMap == null) {
//				totalSalary.put(rank, (typeSalaryMap = new HashMap<>()));
//			}
//			BigDecimal total = typeSalaryMap.get(shipType);
//			typeSalaryMap.put(shipType, ((total == null) ? BigDecimal.ZERO : total).add(salary.multiply(new BigDecimal(count))));
//
//			Map<String, Integer> typeCountMap = counts.get(rank);
//			if (typeCountMap == null) {
//				counts.put(rank, (typeCountMap = new HashMap<>()));
//			}
//			Integer noOfCrews = typeCountMap.get(shipType);
//			typeCountMap.put(shipType, (noOfCrews == null) ? count : noOfCrews + count);
//		}
//
//		for (Map<String, Object> row : reportData) {
//			MMO_010Bean bean = (MMO_010Bean) row.get("bean");
//			if (bean.getType1() != null) {
//				BigDecimal total = totalSalary.get(bean.getRank()).get(bean.getType1());
//				if (total != null) {
//					Integer count = counts.get(bean.getRank()).get(bean.getType1());
//					bean.setCount1(count);
//					if (count != 0) {
//						bean.setAvg1(total.divide(new BigDecimal(count), 2, RoundingMode.HALF_UP));
//					} else {
//						bean.setAvg1(BigDecimal.ZERO);
//					}
//				} else {
//					bean.setAvg1(null);
//					bean.setCount1(0);
//				}
//			} else {
//				bean.setAvg1(null);
//				bean.setCount1(0);
//			}
//			if (bean.getType2() != null) {
//				BigDecimal total = totalSalary.get(bean.getRank()).get(bean.getType2());
//				if (total != null) {
//					Integer count = counts.get(bean.getRank()).get(bean.getType2());
//					bean.setCount2(count);
//					if (count != 0) {
//						bean.setAvg2(total.divide(new BigDecimal(count), 2, RoundingMode.HALF_UP));
//					} else {
//						bean.setAvg2(BigDecimal.ZERO);
//					}
//				} else {
//					bean.setAvg2(null);
//					bean.setCount2(0);
//				}
//			} else {
//				bean.setAvg2(null);
//				bean.setCount2(0);
//			}
//			if (bean.getType3() != null) {
//				BigDecimal total = totalSalary.get(bean.getRank()).get(bean.getType3());
//				if (total != null) {
//					Integer count = counts.get(bean.getRank()).get(bean.getType3());
//					bean.setCount3(count);
//					if (count != 0) {
//						bean.setAvg3(total.divide(new BigDecimal(count), 2, RoundingMode.HALF_UP));
//					} else {
//						bean.setAvg3(BigDecimal.ZERO);
//					}
//				} else {
//					bean.setAvg3(null);
//					bean.setCount3(0);
//				}
//			} else {
//				bean.setAvg3(null);
//				bean.setCount3(0);
//			}
//			if (bean.getType4() != null) {
//				BigDecimal total = totalSalary.get(bean.getRank()).get(bean.getType4());
//				if (total != null) {
//					Integer count = counts.get(bean.getRank()).get(bean.getType4());
//					bean.setCount4(count);
//					if (count != 0) {
//						bean.setAvg4(total.divide(new BigDecimal(count), 2, RoundingMode.HALF_UP));
//					} else {
//						bean.setAvg4(BigDecimal.ZERO);
//					}
//				} else {
//					bean.setAvg4(null);
//					bean.setCount4(0);
//				}
//			} else {
//				bean.setAvg4(null);
//				bean.setCount4(0);
//			}
//			if (bean.getType5() != null) {
//				BigDecimal total = totalSalary.get(bean.getRank()).get(bean.getType5());
//				if (total != null) {
//					Integer count = counts.get(bean.getRank()).get(bean.getType5());
//					bean.setCount5(count);
//					if (count != 0) {
//						bean.setAvg5(total.divide(new BigDecimal(count), 2, RoundingMode.HALF_UP));
//					} else {
//						bean.setAvg5(BigDecimal.ZERO);
//					}
//				} else {
//					bean.setAvg5(null);
//					bean.setCount5(0);
//				}
//			} else {
//				bean.setAvg5(null);
//				bean.setCount5(0);
//			}
//			if (bean.getType6() != null) {
//				BigDecimal total = totalSalary.get(bean.getRank()).get(bean.getType6());
//				if (total != null) {
//					Integer count = counts.get(bean.getRank()).get(bean.getType6());
//					bean.setCount6(count);
//					if (count != 0) {
//						bean.setAvg6(total.divide(new BigDecimal(count), 2, RoundingMode.HALF_UP));
//					} else {
//						bean.setAvg6(BigDecimal.ZERO);
//					}
//				} else {
//					bean.setAvg6(null);
//					bean.setCount6(0);
//				}
//			} else {
//				bean.setAvg6(null);
//				bean.setCount6(0);
//			}
//			if (bean.getType7() != null) {
//				BigDecimal total = totalSalary.get(bean.getRank()).get(bean.getType7());
//				if (total != null) {
//					Integer count = counts.get(bean.getRank()).get(bean.getType7());
//					bean.setCount7(count);
//					if (count != 0) {
//						bean.setAvg7(total.divide(new BigDecimal(count), 2, RoundingMode.HALF_UP));
//					} else {
//						bean.setAvg7(BigDecimal.ZERO);
//					}
//				} else {
//					bean.setAvg7(null);
//					bean.setCount7(0);
//				}
//			} else {
//				bean.setAvg7(null);
//				bean.setCount7(0);
//			}
//		}

		Map<String, Object> map = new HashMap<String, Object>();
		if(dollorCodeNotFoundSet.size()>0) {
			String msg = String.format(dollorCodeNotFoundErrMsg,String.join(",", dollorCodeNotFoundSet));
			map.put(errorMsg, msg);
		}
		
		map.put(REPORT_ID, reportId);
		map.put(REPORT_TITLE, reportTitle);
		map.put(USER_ID, currentUser!=null?currentUser:"SYSTEM");
		map.put("exchangeRate",exchangeRateString);
		Collections.sort(reportData, (a,b)->{
			Map<String, Object> mapA = (Map<String, Object>) a;
			Map<String, Object> mapB = (Map<String, Object>) b;
			MMO_010Bean beanA = (MMO_010Bean) mapA.get("bean");
			MMO_010Bean beanB = (MMO_010Bean) mapB.get("bean");
			int result = beanA.getRank().compareTo(beanB.getRank());
			if (result == 0) {
				return beanA.getNationality().compareTo(beanB.getNationality());
			} else {
				return result;
			}
		});
		return generate(reportData, map);
	}

}
