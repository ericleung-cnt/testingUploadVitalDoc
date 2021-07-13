package org.mardep.ssrs.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.castor.core.util.Assert;
import org.mardep.ssrs.dao.codetable.ICrewDao;
import org.mardep.ssrs.dao.codetable.ICrewListCoverDao;
import org.mardep.ssrs.dao.codetable.INationalityDao;
import org.mardep.ssrs.dao.codetable.INationalityMappingDao;
import org.mardep.ssrs.dao.codetable.IRankDao;
import org.mardep.ssrs.dao.codetable.IRankMappingDao;
import org.mardep.ssrs.domain.codetable.Crew;
import org.mardep.ssrs.domain.codetable.CrewListCover;
import org.mardep.ssrs.domain.codetable.Nationality;
import org.mardep.ssrs.domain.codetable.NationalityMapping;
import org.mardep.ssrs.domain.codetable.Rank;
import org.mardep.ssrs.domain.codetable.RankMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CrewService extends AbstractService implements ICrewService {

	@Autowired
	IExcelUtils excelUtils;
	
	@Autowired
	ICrewDao  crewDao;
	
	@Autowired
	ICrewListCoverDao coverDao;
	
	
	@Autowired
	INationalityDao nationalityDao ;
	
	@Autowired
	INationalityMappingDao nationalityMappingDao ;
	
	@Autowired
	IRankDao  rankDao;
	
	@Autowired
	IRankMappingDao rankMappingDao;
	
	
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
	private static final String capacity ="capacityBeforeMap";
	private static final String engageDate ="engageDate";
	private static final String engagePlace ="engagePlace";
	private static final String serbNo ="serbNo";
	private static final String nationality ="nationalityBeforeMap";
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
	private static final String validationErrors ="validationErrors";
	
	

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
		map.put(engageDate,  Boolean.TRUE);
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
	public List<Crew> readEng2Excel(Crew entity) throws InvalidFormatException, IOException {
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
		Map<String ,List<String>> errorMsg  = new HashMap<>();
//		List<Map<String, Object>> crewDatalistObj = new ArrayList<>();
		List<Crew> crewlist = new ArrayList<>();
		errorMsg.put("errors", new ArrayList<>());
		List<Crew> findCrewsByImono= new ArrayList<>();
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
				logger.warn("readEng2Excel Abort because errors");
				throw new IllegalArgumentException("IMO Number is missing or can't find, processing aborted");
				
			
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
				//stop reading if the group only contain reference No
				boolean containsOnlyRefNo= true;
				for(String key : crewData.keySet()) {
					if(key!= referenceNo &&crewData.get(key)!=null) {
						containsOnlyRefNo=false;
						break;
					}
				}
				if(containsOnlyRefNo) {
					break;
				}
				crewDatalist.add(crewData);
			}

		}

		logger.info(shipInfo.toString());
		logger.info("find {} valid crew records {}", new Object[] {crewDatalist.size(),crewDatalist.toString()});
		
		//validate 
		for(Map<String, Object> map :crewDatalist) {
			String refNo =  map.get(referenceNo).toString();
			errorMsg.put(refNo ,new ArrayList<>());
			 for(Map.Entry<String, Object> entry : map.entrySet()) {
				 if(entry.getKey()!="rowNum") {
					 map.put(entry.getKey(), 
							 validateCellValue(entry.getKey(),entry.getValue(),errorMsg.get(refNo)));					 
				 }
			 }
			 map.put(validationErrors, (errorMsg.get(refNo).size()>0) ? "Errors: "+String.join("; ", errorMsg.get(refNo)):null);
			 
		}
		
		
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		crewDatalist.forEach(map->{
			map.put("imoNo",shipInfo.get(ImoNo));
			crewlist.add(objectMapper.convertValue(map,Crew.class));
		});
		
		logger.warn(errorMsg.toString());

		

		return updateCrewList(shipInfo,crewlist);			

	}
	
	private Object validateCellValue(String key , Object cellVal,  List<String> errorMsg){
		String dataType = crewGridDataType.get(key);
		boolean required = Boolean.TRUE.equals(crewGridIsRequired.get(key));
		if(required&&cellVal==null) {
			errorMsg.add(String.format(requiredErrorMsg, key));
			 Object object = crewGridFallbackValues.get(key);
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
	
	/**
	 *  - if a branch new record (can't find with SEA date and SERB) in DB , insert it; if found others with same SERB, update them inactive)
		- if old record (can find matched SEA date and SERB) in DB, update it 
	 * @param shipInfo
	 * @param crewlist
	 * @return
	 */
	private List<Crew> updateCrewList( Map<String, String>shipInfo, List<Crew> crewlist) {
		crewlist=mapNationalityAndRank(crewlist);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd"); 
		List<Crew>  updateRecords = new ArrayList<>();
		List<Crew>  outDetedRecords = new ArrayList<>();
		//a record is duplicate if have same engage date and serbNo
		List<String> duplicateRecord =new ArrayList<>();
		Crew query = new Crew();
		query.setImoNo(shipInfo.get(ImoNo));
		CrewListCover crewlistCover = coverDao.findById(shipInfo.get(ImoNo));
		if(crewlistCover==null) {
			 CrewListCover crewListCover006 = new CrewListCover();
			 crewListCover006.setImoNo(shipInfo.get(ImoNo));
			 crewListCover006.setShipName(shipInfo.get(NameOfShip));
			 crewListCover006.setOffcialNo(shipInfo.get(OffcialNumber));
			 crewListCover006.setRegPort(shipInfo.get(PortOfRegistry));
			 coverDao.save(crewListCover006);
		}
		List<Crew> allCrewsByImono = crewDao.findByCriteria(query);
//		if(findCrewsByImono.size()==0) {
//			crewlist.stream().forEach(o->o.setStatus(Crew.STATUS_ACTIVE));
//			 saveCrewList(crewlist);
//			 return crewlist;
//		}
		
		for(Crew crew :crewlist) {
			String crewReaded = crew.getSerbNo()+ simpleDateFormat.format(crew.getEngageDate());
			List<Crew> existCrewFound = findExistCrewRecords(crew,allCrewsByImono);
			if(duplicateRecord.contains(crewReaded)) {
				if(existCrewFound.size()>0) {
					existCrewFound.get(0).setValidationErrors("Errors:duplicate Found and Ignored"+Objects.toString(existCrewFound.get(0).getValidationErrors(),""));
				}
				continue;
			}
			
			logger.debug(crew.getReferenceNo()+ "crewlist size"+existCrewFound.size() );
			
			if(existCrewFound.size()==0) {
				// new one 
//				existCrew.setStatus(Crew.STATUS_INACTIVE);
//				updateRecords.add(existCrew);
				crew.setStatus(Crew.STATUS_ACTIVE);
				updateRecords.add(crew);
				
				
				// if have old records
				outDetedRecords.addAll(allCrewsByImono.stream()
						.filter(o->o.getId()!=null)
						.filter(o->StringUtils.equals(o.getSerbNo(),crew.getSerbNo()))
						.filter(o->o.getEngageDate().before(crew.getEngageDate()))
						.collect(Collectors.toList()));
				
			}
			
			else  {
				//update exists
				for(Crew existCrew  : existCrewFound) {
				boolean equals = crew.equals(existCrew);	
				existCrew.setReferenceNo(crew.getReferenceNo());
				
				existCrew.setBirthPlace(crew.getBirthPlace());
				existCrew.setNationalitybeforeMap(crew.getNationalitybeforeMap());
				existCrew.setSerbNo(crew.getSerbNo());
				
				existCrew.setSex(crew.getSex());
				existCrew.setBirthDate(crew.getBirthDate());
				existCrew.setCrewName(crew.getCrewName());
				
				existCrew.setAddress(crew.getAddress());
				existCrew.setNokName(crew.getNokName());
				existCrew.setNokAddress(crew.getNokAddress());
				
				existCrew.setCrewCert(crew.getCrewCert());
				existCrew.setCapacityBeforeMap(crew.getCapacityBeforeMap());
				existCrew.setCurrency(crew.getCurrency());
				existCrew.setSalary(crew.getSalary());
				
				existCrew.setDischargeDate(crew.getDischargeDate());
				existCrew.setDischargePlace(crew.getDischargePlace());		
				existCrew.setEmployDate(crew.getEmployDate());
				existCrew.setEmployDuration(crew.getEmployDuration());
				existCrew.setEngageDate(crew.getEngageDate());
				existCrew.setEngagePlace(crew.getEngagePlace());	
				existCrew.setValidationErrors(crew.getValidationErrors());	
				if(existCrew.getId()!=null) {
					if(!equals) {
						logger.debug(crew.getReferenceNo()+""+ existCrew.getReferenceNo());
						existCrew.setValidationErrors("Update"+ Objects.toString( existCrew.getValidationErrors(),""));
					}
					updateRecords.add(existCrew);									
				}
				}
				
			}
			allCrewsByImono.add(crew);
			duplicateRecord.add(crewReaded);

		}
		
		for(Crew crew :outDetedRecords) {
			crew.setStatus(Crew.STATUS_INACTIVE);
			crew.setValidationErrors("update:status to Inactive");
			updateRecords.add(crew);	
		}
		
		
		 List<Crew> saveCrewList = saveCrewList(updateRecords);
		return saveCrewList;
		
	}


	private List<Crew> findExistCrewRecords(Crew crew, List<Crew> allCrewsByImono) {
		SimpleDateFormat  df = new SimpleDateFormat("yyyyMMdd");
		String serb =crew.getSerbNo();
		Date engageDate = crew.getEngageDate();
		return allCrewsByImono.stream()
		.filter(o->StringUtils.equals(o.getSerbNo(),serb))
		.filter(o-> {
			if(StringUtils.equals(df.format(o.getEngageDate()), df.format(engageDate))) {
				return true;
			}
			return false;
		})
		.collect(Collectors.toList());
		
//		.orElse(null);
//		.collect(Collectors.toList());
	}






	
	

	
	

	
	
	



	@Transactional
	public List<Crew> saveCrewList(List<Crew> records) {
//		records=mapNationalityAndRank(records);
		Map<Integer,Crew> uniqueMap = new LinkedHashMap<>();
 		logger.info("Update follwing {} crew list {}", new Object[] {records.size(),records.toString()});
		
		for(Crew o : records) {
			Long version1 = o.getVersion();
			if(version1==null) {
				o.setValidationErrors("New "+ Objects.toString(o.getValidationErrors(),""));
			}
			
			Crew saved = crewDao.save(o);
			saved.setValidationErrors(o.getValidationErrors());
			uniqueMap.put(saved.getId(),saved);
		}
		return new ArrayList<Crew>(uniqueMap.values());   
		
		
//		updateRecords.stream().forEach(o->{
//			Long version1 = o.getVersion();
//			Crew saved = crewDao.save(o);
//			Long version2 = saved.getVersion();
//			logger.info(version1+"");
//			logger.info(version2+"");
//			if(version1==null) {
//				saved.setValidationErrors("new "+ Objects.toString(saved.getValidationErrors(),""));
//			}else if (version1!=version2) {
//				saved.setValidationErrors("updated "+ Objects.toString(saved.getValidationErrors(),""));
//			}
//			updatedRecords.add(saved);
//			});
		
//		updatedRecords.removeIf(e->!seen.add(e.getId()));// remove duplicate 
		
//		Set<Crew> set = new HashSet<>(updatedRecords);
//		updatedRecords.clear();
//		updatedRecords.addAll(set);  // remove duplicate 
//		List<Crew> result = crewDao.getCrewById(updatedId);
//		for(Crew o :result) {
//			if(rowVersionMap.containsKey(o.getId())&& o.getVersion()!=rowVersionMap.get(o.getId())) {
//				o.setValidationErrors("updated "+o.getValidationErrors());
//			}
//		}
	
		
//		return  result;
	}









	private List<Crew> mapNationalityAndRank(List<Crew> updateRecords) {
		List<Nationality> findByAllNationality = nationalityDao.findAll();
		List<NationalityMapping> findByAllNationalityMapping = nationalityMappingDao.findAll();
		
		List<Rank> findAllRank = rankDao.findAll();
		List<RankMapping> findAllRankMapping = rankMappingDao.findAll();
		updateRecords.stream().forEach(crew->{
			if(crew.getNationalitybeforeMap()!=null) {
				String nationalityKey = crew.getNationalitybeforeMap();
				final String nationalityValue;
//				NationalityMapping mapping = new NationalityMapping();
//				mapping.setINPUT(nationalityKey);
				Optional<NationalityMapping> findMapping = findByAllNationalityMapping.stream().filter(o->o.getINPUT().contains(nationalityKey)).findFirst();
				if(findMapping.isPresent()) {
					nationalityValue= findMapping.get().getOUTPUT();
				}else {
					nationalityValue=null;
				}
				Optional<Nationality> findAny = findByAllNationality.stream().filter(nation->{
					if(nationalityValue !=null) {
						if(StringUtils.equalsIgnoreCase(nation.getChiDesc(), nationalityValue)) return true;
						if(StringUtils.equalsIgnoreCase(nation.getChiDesc(), nationalityValue)) return true;
						if(StringUtils.equalsIgnoreCase(nation.getEngDesc(), nationalityValue)) return true;
						if(StringUtils.equalsIgnoreCase(nation.getCountryChiDesc(), nationalityValue)) return true;
						if(StringUtils.equalsIgnoreCase(nation.getCountryEngDesc(), nationalityValue)) return true;
					}
					if(StringUtils.equalsIgnoreCase(nation.getChiDesc(), nationalityKey)) return true;
					if(StringUtils.equalsIgnoreCase(nation.getChiDesc(),nationalityKey)) return true;
					if(StringUtils.equalsIgnoreCase(nation.getEngDesc(), nationalityKey)) return true;
					if(StringUtils.equalsIgnoreCase(nation.getCountryChiDesc(), nationalityKey)) return true;
					if(StringUtils.equalsIgnoreCase(nation.getCountryEngDesc(), nationalityKey)) return true;
					return false;
				}).findAny();
				
				if(findAny.isPresent()) {
					crew.setNationality(findAny.get());   
					crew.setNationalityId(findAny.get().getId());
				}			
			}
			if(crew.getCapacityBeforeMap()!=null) {	
				String capacityKey = crew.getCapacityBeforeMap();
				String capacityValue;
//				final RankMapping mapping = new RankMapping();
//				mapping.setINPUT(capacityKey);
				Optional<RankMapping> findMapping = findAllRankMapping.stream().filter(o->o.getINPUT().contains(capacityKey)).findFirst();
				if(findMapping.isPresent()) {
					capacityValue= findMapping.get().getOUTPUT();
				}else {
					capacityValue=null;
				}
				
				Optional<Rank> findAny = findAllRank.stream().filter(nation->{
					if(capacityValue !=null) {
						if(StringUtils.equalsIgnoreCase(nation.getChiDesc(), capacityValue)) return true;
						if(StringUtils.equalsIgnoreCase(nation.getEngDesc(), capacityValue)) return true;
					}			
					if(StringUtils.equalsIgnoreCase(nation.getChiDesc(), capacityKey)) return true;
					if(StringUtils.equalsIgnoreCase(nation.getEngDesc(), capacityKey)) return true;
					return false;
				}).findAny();
				
				if(findAny.isPresent()) {
					crew.setCapacity(findAny.get());
					crew.setCapacityId(findAny.get().getId());
				}			
			}
			
				
			}
		);
		return updateRecords;
	}






	
	
	
	

}
