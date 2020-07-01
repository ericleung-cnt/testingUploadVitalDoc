package org.mardep.ssrs.helperService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class MapHelperService implements IMapHelperService {
	
	@Override 
	public Date extractDateFromMap(Map<String, Object> map, String dateFormatStr, String key) throws Exception {
		try {
			Date resultDate = null;
			if (map.containsKey(key)) {
				Object objDate = map.get(key);
				if (objDate instanceof Date) {
					resultDate = (Date)objDate;
				} else if (objDate instanceof String) {
					SimpleDateFormat sdf = new SimpleDateFormat(dateFormatStr);
					resultDate = sdf.parse(objDate.toString());
				}
			}	
			return resultDate;
		} catch (Exception ex) {
			throw ex;
		}
	}
	
	@Override
	public Long extractLongFromMap(Map<String, Object> map, String key) throws Exception {
		try {
			Long lValue = null;
			if (map.containsKey(key)) {
				lValue = new Long(map.get(key).toString());				
			}
			return lValue;
			
		} catch (Exception ex) {
			throw ex;
		}
	}
	
	@Override
	public String extractStrFromMap(Map<String, Object> map, String key) throws Exception {
		try {
			String sValue = null;
			if (map.containsKey(key)) {
				sValue = map.get(key).toString();
			}
			return sValue;
		} catch (Exception ex) {
			throw ex;
		}
	}
}
