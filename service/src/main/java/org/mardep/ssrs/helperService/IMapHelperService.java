package org.mardep.ssrs.helperService;

import java.util.Date;
import java.util.Map;

public interface IMapHelperService {

	public Date extractDateFromMap(Map<String, Object> map, String dateFormatStr, String key) throws Exception;

	public Long extractLongFromMap(Map<String, Object> map, String key) throws Exception;

	public String extractStrFromMap(Map<String, Object> map, String key) throws Exception;

}
