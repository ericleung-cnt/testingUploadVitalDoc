package org.mardep.ssrs.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.castor.core.util.Assert;
import org.mardep.ssrs.dao.codetable.ICrewDao006;
import org.mardep.ssrs.dao.codetable.ICrewListCoverDao006;
import org.mardep.ssrs.domain.codetable.Crew006;
import org.mardep.ssrs.domain.codetable.CrewListCover;
import org.mardep.ssrs.domain.codetable.CrewListCover006;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import aj.org.objectweb.asm.Type;

@Service
public class CrewService extends AbstractService implements ICrewService {

	@Autowired
	IExcelUtils excelUtils;
	
	@Autowired
	ICrewDao006  crewDao;
	
	@Autowired
	ICrewListCoverDao006 coverDao;
	
	private static final  String notValidTypeErrorMsg = "%s %s is not Valid, expect %s";
	private static final  String requiredErrorMsg = "%s is mandatory Field";
	private static final  String maxLengthExceedErrorMsg = "%s %s is too long, trucate to %d";
	
	
	private static String[][] crewGrid = getCrewGridStructure();
	private static String[] shipRow = getShipStructure();
	private static Map<String, String> crewGridDataType = getExcelDataType();
	private static Map<String, Boolean> crewGridIsRequired = getRequiredField();   // if required 
	private static Map<String, Object> crewGridFallbackValues = getFallbakcValue(); 
	private static Map<String, Integer> crewGridMaxLeng =getMaxiumnLength();

	private static final String NameOfShip ="NameOfShip";
	private static final String OffcialNumber ="OffcialNumber";
	private static final String ImoNo ="ImoNo";
	private static final String PortOfRegistry ="PortOfRegistry";
	private static final String referenceNo ="referenceNo";
	private static final String crewName ="crewName";
	private static final String sex ="sex";
	private static final String address ="address";
	private static final String capacity ="capacity";
	private static final String engageDate ="engageDate";
	private static final String engagePlace ="engagePlace";
	private static final String serbNo ="serbNo";
	private static final String nationality ="nationality";
	private static final String nokName ="nokName";
	private static final String crewCert ="crewCert";
	private static final String dischargeDate ="dischargeDate";
	private static final String dischargePlace ="dischargePlace";
	private static final String birthDate ="birthDate";
	private static final String birthPlace ="birthPlace";
	private static final String nokAddress ="nokAddress";
	private static final String currency ="currency";
	private static final String salary ="salary";
	private static final String employDate ="employDate";
	private static final String employDuration ="employDuration";
	
	

	private static String[] getShipStructure() {
		String[] row = new String[20];
		row[2] = NameOfShip;
		row[6] = OffcialNumber;
		row[10] = ImoNo;
		row[13] = PortOfRegistry;
		return row;
	}





	private static String[][] getCrewGridStructure() {
		String[][] grid = new String[3][20];
		grid[0][0] = referenceNo;
		grid[0][2] = crewName;
		grid[0][5] = sex;
		grid[0][7] = address;
		grid[0][9] = capacity;
		grid[0][12] = engageDate;
		grid[0][13] = engagePlace;
		grid[1][2] = serbNo;
		grid[1][5] = nationality;
		grid[1][7] = nokName;
		grid[1][9] = crewCert;
		grid[1][12] = dischargeDate;
		grid[1][13] = dischargePlace;
		grid[2][2] = birthDate;
		grid[2][5] = birthPlace;
		grid[2][7] = nokAddress;
		grid[2][9] = currency;
		grid[2][10] = salary;
		grid[2][12] = employDate;
		grid[2][13] = employDuration;
		return grid;
	}
	
	private static Map<String, String> getExcelDataType() {
		Map<String, String> map = new HashMap<>();
		map.put(NameOfShip, "String");
		map.put(OffcialNumber, "String");
		map.put(ImoNo, "int");
		map.put(PortOfRegistry, "String");
		map.put(referenceNo, "int");
		map.put(crewName, "String");
		map.put(sex, "String");
		map.put(address, "String");
		map.put(capacity, "String");
		map.put(engageDate, "date");
		map.put(engagePlace, "String");
		map.put(serbNo, "String");
		map.put(nationality,"String");
		map.put(nokName,"String");
		map.put(crewCert, "String");
		map.put(dischargeDate, "date");
		map.put(dischargePlace, "String");
		map.put(birthDate, "date");
		map.put(birthPlace, "String");
		map.put(nokAddress, "String");
		map.put(currency, "String");
		map.put(salary, "bigDecimal");
		map.put(employDate, "date");
		map.put(employDuration, "double");
	   return map ;
	}
	private static Map<String, Boolean> getRequiredField() {
		Map<String, Boolean> map = new HashMap<>();
		map.put(birthDate, Boolean.TRUE);
		map.put(salary, Boolean.TRUE);
		map.put(nationality, Boolean.TRUE);
		map.put(capacity,  Boolean.TRUE);
		map.put(serbNo,  Boolean.TRUE);
		map.put(employDate,  Boolean.TRUE);
		return map;
	}
	
	private static Map<String, Object> getFallbakcValue() {
		final String fallbackDate = "1900-01-01";
		final int  fallbackInt = 0;
		final double  fallbackdouble =0;
		final BigDecimal fallbackBigdecimal = BigDecimal.valueOf(0);
		final String fallbackString = "";
		
		Map<String, Object> map = new HashMap<>();
		map.put(referenceNo, fallbackInt);
		map.put(crewName, fallbackString);
		map.put(sex, fallbackString);
		map.put(address, fallbackString);
		map.put(capacity, fallbackString);//required
		map.put(engageDate, fallbackDate);
		map.put(engagePlace, fallbackString);
		map.put(serbNo, fallbackString);
		map.put(nationality,fallbackString);//required
		map.put(nokName,fallbackString);
		map.put(crewCert, fallbackString);
		map.put(dischargeDate, fallbackDate);
		map.put(dischargePlace, fallbackString);
		map.put(birthDate, fallbackDate);  //required
		map.put(birthPlace, fallbackString);
		map.put(nokAddress, fallbackString);
		map.put(currency, fallbackString);
		map.put(salary, fallbackBigdecimal);//required
		map.put(employDate, fallbackDate);
		map.put(employDuration, fallbackdouble);
		
	   return map ;
	}
	
	private static Map<String, Integer> getMaxiumnLength() {
		Map<String, Integer> map = new HashMap<>();
		map.put(NameOfShip, 50);
		map.put(OffcialNumber, 50);
		map.put(ImoNo, 50);
		map.put(PortOfRegistry, 50);
//		map.put(referenceNo, "int");
		map.put(crewName, 50);
		map.put(sex, 10);
		map.put(address, 250);
		map.put(capacity, 50);
//		map.put(engageDate, "date");
		map.put(engagePlace,50);
		map.put(serbNo, 15);
		map.put(nationality,50);
		map.put(nokName,50);
		map.put(crewCert, 50);
//		map.put(dischargeDate, "date");
		map.put(dischargePlace, 50);
//		map.put(birthDate, "date");
		map.put(birthPlace, 50);
		map.put(nokAddress, 250);
		map.put(currency, 5);
//		map.put(salary, "bigDecimal");
//		map.put(employDate, "date");
//		map.put(employDuration, "double");
	   return map ;
	}
	
	
	
	
	
	@Transactional
	@Override
	public List<Crew006> readEng2Excel(CrewListCover entity,Map<String ,List<String>> errorMsg ) throws InvalidFormatException, IOException {
		logger.info("readEng2Excel");

		byte[] excel = entity.getExcelData();
		Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(excel));
		Sheet datatypeSheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = datatypeSheet.iterator();
//		String[][] crewGrid = getCrewGridStructure();
//		String[] shipRow = getShipStructure();
//		Map<String, String> crewGridDataType = getCrewGridDataType();
		final int startRow = 10;
		final int shipInfoRow = 3;
		Map<String, String> shipInfo = new HashMap<>();
		Map<String, Object> crewData = null;
		List<Map<String, Object>> crewDatalist = new ArrayList<>();
//		List<Map<String, Object>> crewDatalistObj = new ArrayList<>();
		List<Crew006> crewlist = new ArrayList<>();
		errorMsg.put("errors", new ArrayList<>());
		List<Crew006> findCrewsByImono= new ArrayList<>();
		boolean abort =false;
		while (iterator.hasNext()) {
			Row row = iterator.next();
			int rowNum = row.getRowNum();
			int lastCol = row.getLastCellNum();

			if (rowNum < startRow && rowNum != shipInfoRow) {
				continue;
			}
			if (rowNum == shipInfoRow) { // get ship data
				for (int col = 0; col < lastCol; col++) {
					Cell currCell = row.getCell(col,Row.RETURN_BLANK_AS_NULL);
					if (shipRow[col] != null) {
						shipInfo.put(shipRow[col], String.valueOf(validateCellValue(shipRow[col], excelUtils.getCellValue(currCell),errorMsg.get("shipInfo") )));
					}
				}
				continue;
			}

			if(shipInfo.get("ImoNo")==null) {
				errorMsg.get("errors").add(String.format("IMO Number is missing processing aborted"));
				abort = true;
				break;
			}
			int subRow = (rowNum - startRow) % 3; // every 3 row as a group
			if (subRow == 0) {
				crewData = new HashMap<>();
			}
			for (int col = 0; col < lastCol; col++) {
				Cell currCell = row.getCell(col,Row.RETURN_BLANK_AS_NULL);
//				logger.info("{}{}{}{}",
//						new Object[] { subRow, col, crewGrid[subRow][col], excelUtils.getCellValue(currCell) });
				if (crewGrid[subRow][col] != null) {
//					crewData.put(crewGrid[subRow][col], getAndParseCellValue(crewGrid[subRow][col],currCell,crewGridDataType[subRow][col],errorMsg));
					crewData.put(crewGrid[subRow][col],excelUtils.getCellValue(currCell));
				}
			}
			if (subRow == 2) {
//				if (crewData.get("crewName").equals("")) {
//					break; // stop when whole name is blank
//				}
				boolean containsOnlyRefNo= true;
				for(String key : crewData.keySet()) {
					if(key!= referenceNo &&crewData.get(key)!=null) {
						containsOnlyRefNo=false;
					}
				}
				if(containsOnlyRefNo) {
					break;
				}

//				crewData.put("rowNum", rowNum-1);
				crewDatalist.add(crewData);
			}

		}

		logger.info(shipInfo.toString());
		logger.info("find {} valid crew records"+crewDatalist.toString(),crewDatalist.size());
		
		//validate 
		for(Map<String, Object> map :crewDatalist) {
			String rowNum =  map.get(referenceNo).toString();
			errorMsg.put(rowNum ,new ArrayList<>());
			 for(Map.Entry<String, Object> entry : map.entrySet()) {
				 if(entry.getKey()!="rowNum") {
					 map.put(entry.getKey(), 
							 validateCellValue(entry.getKey(),entry.getValue(),errorMsg.get(rowNum)));					 
				 }
			 }
		}
		
		
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		crewDatalist.forEach(map->{
			map.put("imoNo",shipInfo.get(ImoNo));
			crewlist.add(objectMapper.convertValue(map,Crew006.class));
		});
		
		logger.info(errorMsg.toString());
		if(abort) {
			logger.info("readEng2Excel Abort because errors");
			return null;
		}
		

		return updateCrewList(shipInfo,crewlist);			

	}
	

	private List<Crew006> updateCrewList( Map<String, String>shipInfo, List<Crew006> crewlist) {
		List<Crew006>  updateRecords = new ArrayList<>();
		Crew006 query = new Crew006();
		query.setImoNo(shipInfo.get("ImoNo"));
		CrewListCover006 crewlistCover = coverDao.findById(shipInfo.get(ImoNo));
		if(crewlistCover==null) {
			 CrewListCover006 crewListCover006 = new CrewListCover006();
			 crewListCover006.setImoNo(shipInfo.get(ImoNo));
			 crewListCover006.setShipName(shipInfo.get(NameOfShip));
			 crewListCover006.setOffcialNo(shipInfo.get(OffcialNumber));
			 crewListCover006.setRegPort(shipInfo.get(PortOfRegistry));
			 coverDao.save(crewListCover006);
		}
		List<Crew006> findCrewsByImono = crewDao.findByCriteria(query);
		if(findCrewsByImono.size()==0) {
			crewlist.stream().forEach(o->o.setStatus(Crew006.STATUS_ACTIVE));
			 saveCrewList(crewlist);
			 return crewlist;
		}
		
		for(Crew006 crew :crewlist) {
			List<Crew006> findExistCrewRecords = findExistCrewRecords(crew,findCrewsByImono);
			if(findExistCrewRecords.size()>0) { // exist
				if(!findExistCrewRecords.get(0).equals(updateRecords)) {
					updateRecords.add(crew);
				}
				
				
			}else {
				findExistCrewRecords.stream().forEach(o->{
					o.setStatus(Crew006.STATUS_INACTIVE);
				});
				crew.setStatus(Crew006.STATUS_ACTIVE);
				updateRecords.add(crew);
			}
		}
		
		
		saveCrewList(updateRecords);
		return updateRecords;
		
	}




	@Transactional
	public void saveCrewList(List<Crew006> updateRecords) {
		//TODO: MAP data for capacity and nationality
		updateRecords.stream().forEach(o->crewDao.save(o));
		
		
	}









	private List<Crew006> findExistCrewRecords(Crew006 crew, List<Crew006> findCrewsByImono) {
		String serb =crew.getSerbNo();
		Date employDate = crew.getEmployDate();
		return findCrewsByImono.stream()
		.filter(o->StringUtils.equals(o.getSerbNo(),serb))
		.filter(o->o.getEmployDate().compareTo(employDate)==0)
		.collect(Collectors.toList());
	}





	private Object validateCellValue(String key , Object cellVal,  List<String> errorMsg){
		String dataType = crewGridDataType.get(key);
		Assert.notNull(dataType, key + " is null");
		boolean required = Boolean.TRUE.equals(crewGridIsRequired.get(key));
		if(required&&cellVal==null) {
			errorMsg.add(String.format(requiredErrorMsg, key));
			 Object object = crewGridFallbackValues.get(key);
			 Assert.notNull(object, key +" default value null ");
			 return object;
		}

//		final String fallbackDate = "1970-01-01";
//		final int  fallbackInt = 0;
//		final double  fallbackdouble =0;
//		final BigDecimal fallbackBigdecimal = BigDecimal.valueOf(0);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[yyyyMMdd][yyyy MM dd]");
		if(cellVal==null) {
			return cellVal;
		}
		String value = cellVal.toString(); 
	    if(dataType.equals("int")) {
	    	try {
	    		 return Double.valueOf(value).intValue();	    		
	    	}
	    	catch(NumberFormatException ex ) {
	    		errorMsg.add(String.format(notValidTypeErrorMsg, key,value, "Integer")); 
	    		return required?crewGridFallbackValues.get(key):null;
	    	}	
	    }else if (dataType.equals("date")) {
	    	try {
	    		LocalDate parse = LocalDate.parse(value, formatter);	
	    		return parse.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	    	}
	    	catch(Exception ex ) {
	    		errorMsg.add(String.format(notValidTypeErrorMsg, key,value, "yyyymmdd")); 
	    		return required?crewGridFallbackValues.get(key):null;
	    	}
	    }else if (dataType.equals("bigDecimal")) {
	    	try {
	    		return new BigDecimal(value);
	    	}
	    	catch(Exception ex ) {
	    		errorMsg.add(String.format(notValidTypeErrorMsg, key,value, "numeric")); 
	    		return required?crewGridFallbackValues.get(key):null;
	    	}
	    }else if (dataType.equals("double")) {
	    	try {
	    		return Double.valueOf(value);
	    	}
	    	catch(Exception ex ) {
	    		errorMsg.add(String.format(notValidTypeErrorMsg, key,value, "numeric")); 
	    		return required?crewGridFallbackValues.get(key):null;
	    	}
	    }else if (dataType.equals("String")){
	    	if(value.length()>crewGridMaxLeng.get(key)) {
	    		errorMsg.add(String.format(maxLengthExceedErrorMsg, key,value,crewGridMaxLeng.get(key))); 
	    		return value.substring(0, crewGridMaxLeng.get(key));
	    	}
	    }
		
		return value;
		
	}
	
	
//	public List<Map<String, String>> validateExcel(List<Map<String, String>> data){
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[yyyyMMdd][yyyy MM dd]");
//		getCrewGridDataType();
//	 for(Map<String, String> map : data) {
//		 for(Map.Entry<String, String>  entry :map.entrySet()) {
//			 
//		 }
//		 
//		 
//		 
//		 
//	 }
//		
//		
//	LocalDate.parse(str);	
//	return data;
//		
//	}
	
	
	public List<Map<String, String>> mapExcel(List<Map<String, String>> data){
		return data;
		
		
		
	}
	
	
	
	
	
	

}
