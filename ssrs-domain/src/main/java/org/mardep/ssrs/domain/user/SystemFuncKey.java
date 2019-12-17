package org.mardep.ssrs.domain.user;

import java.util.EnumMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Leo.LIANG
 *
 */
public enum SystemFuncKey {


	SR_VIEW,
	SR_CREATE,
	SR_UPDATE,

	MMO_VIEW,
	MMO_CREATE,
	RE_ISSUE_SERB,
	RENEW_SEAFARER_REGISTRATION,
	MMO_UPDATE,

	FINANCE_VIEW,
	FINANCE_CREATE,
	FINANCE_UPDATE,

	CODETABLE_VIEW,
	CODETABLE_CREATE,
	CODETABLE_UPDATE,

	CREATE_DEMAND_NOTE,
	CANCEL_DEMAND_NOTE,

	DNS_VIEW,
	GEN_REPORT,
	FINANCE_REFUND,
	;
	protected static final Logger logger = LoggerFactory.getLogger(SystemFuncKey.class);
	public static SystemFuncKey getEnum(String value) {
		try{
			SystemFuncKey key = SystemFuncKey.valueOf(value);
			return key;
		}catch(Exception ex){
			logger.error("Enum [{}] not mapping", value);
		}
		return null;
    }

	public static Map<SystemFuncKey, String> getSystemFuncMap(){
		Map<SystemFuncKey, String> enumMap = new EnumMap<SystemFuncKey, String>(SystemFuncKey.class);
		for(SystemFuncKey key:SystemFuncKey.values()){
			enumMap.put(key, key.toString());
		}
		return enumMap;
	}

}
