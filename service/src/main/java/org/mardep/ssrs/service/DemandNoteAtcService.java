package org.mardep.ssrs.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;





public class DemandNoteAtcService {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	private Date addYrsToDate(Date requestDate, int years) {
		Calendar requestDateC = Calendar.getInstance();
		requestDateC.setTime(requestDate);
		requestDateC.add(Calendar.YEAR, years);
		return requestDateC.getTime();
	}
	
	private int compareMonthDayBetweenDates(Date date1, Date date2) {
		int compareResult = 0;
		
		Calendar date1C = Calendar.getInstance();
		date1C.setTime(date1);
		Calendar date2C = Calendar.getInstance();
		date2C.setTime(date2);
		if (date1C.get(Calendar.MONTH) < date2C.get(Calendar.MONTH)) {
			compareResult = -1;
		} else if (date1C.get(Calendar.MONTH)==date2C.get(Calendar.MONTH)) {
			if (date1C.get(Calendar.DAY_OF_MONTH) < date2C.get(Calendar.DAY_OF_MONTH)) {
				compareResult = -1;
			} else if (date1C.get(Calendar.DAY_OF_MONTH) == date2C.get(Calendar.DAY_OF_MONTH)) {
				compareResult = 0;
			} else if (date1C.get(Calendar.DAY_OF_MONTH) > date2C.get(Calendar.DAY_OF_MONTH)) {
				compareResult = 1;
			}
		} else if (date1C.get(Calendar.MONTH) > date2C.get(Calendar.MONTH)) {
			compareResult = 1;
		}
		return compareResult;
	}
	
	private int compareOnlyDayBetweenDates(Date date1, Date date2) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date newDate1 = sdf.parse(sdf.format(date1));
			Date newDate2 = sdf.parse(sdf.format(date2));
			return newDate1.compareTo(newDate2);
		} catch (Exception ex) {
			return -1;
		}
		
	}
	private BigDecimal noDetainATC(BigDecimal fullATC, BigDecimal lastATC) {
		BigDecimal noDetainATC = null;
		BigDecimal atcCompareFactor = fullATC.multiply(new BigDecimal("0.7"));
		//if (lastATC >= fullATC) {
		if (lastATC.compareTo(atcCompareFactor)>0) {
			//MathContext mc = new MathContext(1,RoundingMode.DOWN);
			noDetainATC = fullATC.multiply(new BigDecimal("0.5")).setScale(1, RoundingMode.FLOOR);
		} else {
			noDetainATC = fullATC;
		}
		return noDetainATC;
	}
	
	public BigDecimal calcAtcAmt(Date regDate, Date detainDate, Date dueDate, BigDecimal fullATC, BigDecimal lastATC) {

		BigDecimal calcATC = null;
		BigDecimal halfATC = fullATC.multiply(new BigDecimal("0.5")).setScale(1, RoundingMode.FLOOR);
		int compareResult = 0;
		Date regDate2Yrs = addYrsToDate(regDate, 1);
		compareResult = compareOnlyDayBetweenDates(regDate2Yrs, dueDate);
		if (compareResult>=0) {  // within first 2 years, must be full ,( > 1st year, = 2nd year)
//			calcATC = fullATC;
			return fullATC;
		} 
		if(detainDate!=null) {
			logger.info("detainDate!=null");
//			Date dueDate2YrsBefore = addYrsToDate(dueDate, -2);
//			compareResult = compareOnlyDayBetweenDates(detainDate, dueDate2YrsBefore); 
//			if (compareResult >= 0 &&detainDate.before(dueDate) ) {  //detained within 2 years (including dueDate2YrsBefore)
//				return fullATC;
//			}else {
//				//not detained in last 2 years
//				Calendar c1 = Calendar.getInstance();
//				c1.setTime(detainDate);
//				Calendar c2 = Calendar.getInstance();
//				c2.setTime(dueDate);
//				compareResult = c2.get(Calendar.YEAR)-c1.get(Calendar.YEAR);  
//				
//				
//				SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
//				Date aniDay ;
//				Date detainDateNoyear;
//				try {
//					aniDay = sdf.parse(sdf.format(dueDate));
//					detainDateNoyear = sdf.parse(sdf.format(detainDate));
//					//detained equal or after anniversary day 
//					if(detainDateNoyear.equals(aniDay)||detainDateNoyear.after(aniDay)) {
//						if (compareResult %2 == 1) {  
//							//detained equal or after anniversary date 
//							return halfATC;   
//						}else {
//							return fullATC;
//						}
//					}else {
//						if (compareResult %2 == 0) {  
//							//detained equal or after anniversary date 
//							return halfATC;   
//						}else {
//							return fullATC;
//						}
//					}
//				} catch (ParseException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//
//				return fullATC;
//				
//			}
			return calcDetainAtcAmt(regDate, detainDate, dueDate, fullATC, halfATC);
		}else {
//			if(lastATC.equals(fullATC)) {
//				return halfATC;
//			}else{
//				return fullATC;					
//			}
			// 2021-04-13
//			Calendar c1 = Calendar.getInstance();
//			c1.setTime(regDate);
//			if (c1.get(Calendar.YEAR) < 2007) { //  ship reg before 2007 should start first half payment in 2007
//				c1.set(Calendar.YEAR, 2005);
//			}
//			Calendar c2 = Calendar.getInstance();
//			c2.setTime(dueDate);
//			compareResult = c2.get(Calendar.YEAR)-c1.get(Calendar.YEAR);  
//			System.out.println("compareResult "+ compareResult);
//			if (compareResult %2 == 0) {  
//				return halfATC;   
//			}else {
//				return fullATC;
//			}
			// 2021-04-13
			return calcNonDetainAtcAmt(regDate, dueDate, fullATC, halfATC);
		}
	}
	
	public BigDecimal calcDetainAtcAmt(Date regDate, Date detainDate, Date dueDate, BigDecimal fullAtc, BigDecimal halfAtc) {
		Date detainAnniversaryDate = calcDetainAnniversaryDate(regDate, detainDate);
		BigDecimal calcAtc = null;

		if (detainAnniversaryDate!=null) {
			Calendar calDetain = Calendar.getInstance();
			calDetain.setTime(detainAnniversaryDate);
			Calendar calDue = Calendar.getInstance();
			calDue.setTime(dueDate);
			int yearDiff = calDue.get(Calendar.YEAR) - calDetain.get(Calendar.YEAR);
			
			if (yearDiff<0 || (yearDiff%2)==0) {
				calcAtc = fullAtc;
			} else {
				calcAtc = halfAtc;
			}			
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		logger.info("calcDetainAtcAmt: ");
		logger.info("regDate: " + sdf.format(regDate));
		logger.info("detainDate: " + sdf.format(detainDate));
		logger.info("dueDate: " + sdf.format(dueDate));
		logger.info("detainAnniversaryDate: " + sdf.format(detainAnniversaryDate));
		logger.info("atc amt: " + calcAtc.toString());
		return calcAtc;
	}
	
	public Date calcDetainAnniversaryDate(Date regDate, Date detainDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date detainAnniversaryDate = null;
		try {
			Calendar calDetain = Calendar.getInstance();
			calDetain.setTime(detainDate);
			Calendar calRegDate = Calendar.getInstance();
			calRegDate.setTime(regDate);
			// calDetain.get(Calendar.YEAR) +1 or +2 is to advance year to something like firstAnniversaryDate
			if ((calDetain.get(Calendar.MONTH)<calRegDate.get(Calendar.MONTH))
					|| (calDetain.get(Calendar.MONTH)==calRegDate.get(Calendar.MONTH) 
							&& calDetain.get(Calendar.DAY_OF_MONTH)<calRegDate.get(Calendar.DAY_OF_MONTH))) {
				calDetain.set(calDetain.get(Calendar.YEAR)+1, calRegDate.get(Calendar.MONTH), calRegDate.get(Calendar.DAY_OF_MONTH));
			} else {
				calDetain.set(calDetain.get(Calendar.YEAR)+2, calRegDate.get(Calendar.MONTH), calRegDate.get(Calendar.DAY_OF_MONTH));
			}
			//return calDetain.getTime();
			return convertDateIfLeapYear(calDetain.get(Calendar.YEAR), calDetain.getTime());
		} catch (Exception ex) {
			return null;
		}
	}
	
	public BigDecimal calcNonDetainAtcAmt(Date regDate, Date dueDate, BigDecimal fullAtc, BigDecimal halfAtc) {
		Date firstAnniversaryDate = calcFirstAnniversaryDate(regDate);
		BigDecimal calcAtc = null;
		if (firstAnniversaryDate!=null) {
			Calendar calFirst = Calendar.getInstance();
			calFirst.setTime(firstAnniversaryDate);
			Calendar calDue = Calendar.getInstance();
			calDue.setTime(dueDate);
			int yearDiff = calDue.get(Calendar.YEAR) - calFirst.get(Calendar.YEAR);
			
			if (yearDiff<0 || (yearDiff%2)==0) {
				calcAtc = fullAtc;
			} else {
				calcAtc = halfAtc;
			}			
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		logger.info("calcNonDetainAtcAmt: ");
		logger.info("regDate: " + sdf.format(regDate));
		logger.info("dueDate: " + sdf.format(dueDate));
		logger.info("FirstAnniversaryDate: " + sdf.format(firstAnniversaryDate));
		logger.info("atc amt: " + calcAtc.toString());
		return calcAtc;
	}
	
	public Date calcFirstAnniversaryDate(Date regDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date firstAnniversaryDate = null;
		try {
			Date regDateCutOff = sdf.parse("2006-02-01");
			Calendar calRegDate = Calendar.getInstance();
			calRegDate.setTime(regDate);
			Calendar calCutOff = Calendar.getInstance();
			calCutOff.setTime(regDateCutOff);
			if (regDate.after(regDateCutOff)) {
				firstAnniversaryDate = convertDateIfLeapYear(calRegDate.get(Calendar.YEAR)+1, regDate);
			} else {
				if (calRegDate.get(Calendar.YEAR)<calCutOff.get(Calendar.YEAR) 
						&& calRegDate.get(Calendar.MONTH)>=calCutOff.get(Calendar.MONTH)
						&& calRegDate.get(Calendar.DAY_OF_MONTH)>=calCutOff.get(Calendar.DAY_OF_MONTH)) {
							firstAnniversaryDate = convertDateIfLeapYear(calCutOff.get(Calendar.YEAR), regDate);
				} else {
					firstAnniversaryDate = convertDateIfLeapYear(calCutOff.get(Calendar.YEAR)+1, regDate);
				}
			}
			return firstAnniversaryDate;
		} catch (Exception ex) {
			return null;
		}
	}
	
	private Date convertDateIfLeapYear(int year, Date srcDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String regDateStr = sdf.format(srcDate);
		try {
			Date resultDate = null;
			Calendar cal = Calendar.getInstance();
			cal.setTime(srcDate);
			if (cal.get(Calendar.MONTH)==2 && cal.get(Calendar.DAY_OF_MONTH)==29) {
				resultDate = sdf.parse(String.valueOf(year)+"-02-28");
			} else {
				resultDate = sdf.parse(String.valueOf(year) + regDateStr.substring(4,10));
			}
			return resultDate;
		} catch (Exception ex) {
			return null;
		}
	}
//	public BigDecimal calcAtcAmt(Date regDate, Date detainDate, Date dueDate, BigDecimal fullATC, BigDecimal lastATC) {
//		BigDecimal calcATC = null;
//		BigDecimal halfATC = fullATC.multiply(new BigDecimal("0.5")).setScale(1, RoundingMode.FLOOR);
//		int compareResult = 0;
//
//		if (detainDate != null) {
//			Date detainDate3Yrs = addYrsToDate(detainDate, 3);
//		
//			//compareResult = detainDate3Yrs.compareTo(dueDate);	// detain date should have no impact ATC after 3 yrs 
//			compareResult = compareOnlyDayBetweenDates(detainDate3Yrs, dueDate);
//			if (compareResult<=0) {
//				detainDate = null;
//			} 
//		}
//		if (detainDate==null) {
//			Date regDate2Yrs = addYrsToDate(regDate, 2);
//			compareResult = compareOnlyDayBetweenDates(regDate2Yrs, dueDate);
//			//if (regDate2Yrs.compareTo(dueDate) > 0 ) {
//			if (compareResult>=0) {
//				calcATC = fullATC;
//			} else {
//				calcATC = noDetainATC(fullATC, lastATC);
//			}
//		} else {
//			Date dueDate2YrsBefore = addYrsToDate(dueDate, -2);
//			//int compareResult = compareMonthDayBetweenDates(detainDate, dueDate2YrsBefore);
//			//compareResult = detainDate.compareTo(dueDate2YrsBefore);
//			compareResult = compareOnlyDayBetweenDates(detainDate, dueDate2YrsBefore);
//			if (compareResult <= 0) {
//				calcATC = halfATC;
//			} else if (compareResult > 0) {
//				calcATC = fullATC;
//			}
////			Date detainDate2Yrs = addYrsToDate(detainDate, 2);
////			int compareResult = compareMonthDayBetweenDates(detainDate2Yrs, currentDate);
////			if (compareResult < 0) {
////				calcATC = fullATC;
////			} else if (compareResult >= 0) {
////				calcATC = halfATC;				
////			} 
////			int compareResult = compareMonthDayBetweenDates(detainDate, regDate);
////			if (compareResult < 0) {
////				if (detainDate2Yrs.compareTo(currentDate) > 0) {
////					calcATC = fullATC;
////				} else {
////					calcATC = noDetainATC(fullATC, lastATC);
////				}
////			} else if (compareResult == 0) {
////				if (detainDate2Yrs.compareTo(currentDate)>0) {
////					calcATC = fullATC;
////				} else {
////					calcATC = noDetainATC(fullATC, lastATC);
////				}
////			} else if (compareResult > 0) {
////				if (detainDate2Yrs.compareTo(currentDate)>0) {
////					calcATC = fullATC;
////				} else {
////					Date detainDate3Yrs = addYrsToDate(detainDate, 3);
////					if (detainDate3Yrs.compareTo(currentDate) > 0) {
////						calcATC = halfATC; // fullATC;
////					} else {
////						calcATC = noDetainATC(fullATC, lastATC);
////					}
////				}
////			}
//		}
//		return calcATC;
//	}

}
