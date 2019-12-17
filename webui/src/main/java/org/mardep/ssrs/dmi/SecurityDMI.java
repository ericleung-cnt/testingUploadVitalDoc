package org.mardep.ssrs.dmi;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.mardep.ssrs.constant.Key;
import org.mardep.ssrs.constant.UserLoginResult;
import org.mardep.ssrs.domain.user.SystemFunc;
import org.mardep.ssrs.domain.user.SystemFuncKey;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.domain.user.UserRole;
import org.mardep.ssrs.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSResponse;

@Component
public class SecurityDMI {

	private final Logger logger = LoggerFactory.getLogger(SecurityDMI.class);

	@Autowired
	private IUserService userService;

	final EnumSet<SystemFuncKey> srSet = EnumSet.of(SystemFuncKey.SR_CREATE, SystemFuncKey.SR_VIEW, SystemFuncKey.SR_UPDATE);
	final EnumSet<SystemFuncKey> mmoSet = EnumSet.of(SystemFuncKey.RENEW_SEAFARER_REGISTRATION, SystemFuncKey.MMO_VIEW, SystemFuncKey.MMO_UPDATE);
	final EnumSet<SystemFuncKey> financeSet = EnumSet.of(SystemFuncKey.FINANCE_CREATE, SystemFuncKey.FINANCE_VIEW, SystemFuncKey.FINANCE_UPDATE);
	final EnumSet<SystemFuncKey> codeTableSet = EnumSet.of(SystemFuncKey.CODETABLE_CREATE, SystemFuncKey.CODETABLE_VIEW, SystemFuncKey.CODETABLE_UPDATE);

	final EnumSet<SystemFuncKey> dnsSet = EnumSet.of(SystemFuncKey.DNS_VIEW);

	public Map<String, Object> getPIA(){
		Map<String, Object> piaMap = new HashMap<>();
		piaMap.put(Key.PIA_ENG,"The Marine Department (\"MD\") is concerned to ensure that all personal data submitted through MD website are handled in accordance with the relevant provisions of the Personal Data (Privacy) Ordinance.");
		piaMap.put(Key.PIA_TCHI,"海事處會確保所有透過海事處網站遞交的個人資料，均按照《個人資料(私隱)條例》的有關條文處理。");
		piaMap.put(Key.PIA_SCHI,"海事处会确保所有透过海事处网站递交的个人资料，均按照《个人资料(私隐)条例》的有关条文处理。");
		return piaMap;
	}

	public Map<String, Object> checkLogin(HttpServletRequest req) {
		Map<String, Object> map = new HashMap<>();
		String userId = (String) req.getSession().getAttribute(Key.USER_ID);
		if (userId != null) {
			loginOk(userId, map);
		} else {
			map.put("status", DSResponse.STATUS_LOGIN_REQUIRED);
		}
		return map;
	}

	public Map<String, Object> login(HttpServletRequest httpServletRequest, String userId, String password, boolean isExternal){
		Map<String, Object> map = new HashMap<>();
		HttpSession httpSession = httpServletRequest.getSession();
		String remoteAddress = httpServletRequest.getRemoteAddr();
		Map<String, Object> loginMap = userService.login(userId, password, isExternal, remoteAddress);
		if (loginMap.get(Key.LOGIN_RESULT).equals(UserLoginResult.SUCCESSFUL)) {
			httpSession.setAttribute(Key.USER_ID, userId);
			httpSession.setAttribute(Key.CURRENT_USER, loginMap.get(Key.CURRENT_USER));
			//httpSession.setAttribute(Key.LOGIN_TIME, new Date());
			httpSession.setAttribute(Key.LOGIN_TIME, loginMap.get(Key.LOGIN_TIME));
			httpSession.setAttribute(Key.IS_EXTERNAL, isExternal);
			loginOk(userId, map);
		}else{
			map.put(Key.MESSAGE, ((UserLoginResult)loginMap.get(Key.LOGIN_RESULT)).getMessage());
		}
		return map;
	}

	private void loginOk(String userId, Map<String, Object> map) {
		map.put("userId", userId);
		map.put("status", DSResponse.STATUS_LOGIN_SUCCESS);
		map.put("loginTime", new Date());
		map.put("ssrsEnv", System.getProperty("ssrsEnv", "unknown"));
		map.put("ssrsVersion", "0.0.1");
		map.put("ssrsBuildTime", "19980108");
		// TODO check user access

		Set<String> roles= new HashSet<String>();
		List<UserRole> urList = userService.findUserRoleByUserId(userId);
		for(UserRole userRole:urList){
			roles.add(userRole.getRole().getRoleCode());
		}
		String roleListString = StringUtils.join(roles, ", ");
		logger.debug("RoleList:[{}]", roleListString);
		map.put(Key.ROLE_LIST_STRING, roleListString);
		map.put("userGroups", roleListString);// TODO

		List<String> tabList = new ArrayList<String>();
		tabList.add("{\"id\":\"inboxTab\",\"title\":\"Inbox\",\"viewURL\":\"js/inbox/inbox.js\"}");
		Set<SystemFuncKey> userSfk = new HashSet<SystemFuncKey>();
		if(urList!=null && urList.size()>0){
			Set<String> funcs= new HashSet<String>();
			Set<String> taskNames = new HashSet<String>();
			List<SystemFunc> sfList = userService.findSystemFuncByUserId(userId);
			for(SystemFunc sf:sfList){
				String fKey = sf.getKey();
				SystemFuncKey systemFuncKey = SystemFuncKey.getEnum(fKey);
				if (systemFuncKey != null) {
					userSfk.add(systemFuncKey);
				}
				if (fKey.startsWith("INBOX_")) {
					taskNames.add(fKey.substring("INBOX_".length()));
				}
				funcs.add(sf.getKey());
			}
			String funcListString = StringUtils.join(funcs, ", ");
			logger.debug("FuncList:[{}]", funcListString);
			map.put(Key.FUNCTION_LIST_STRING, funcListString);
			map.put(Key.USER_FUNC_LIST, userSfk);// TODO
			map.put(Key.TASK_NAMES, taskNames);
		}
		for(SystemFuncKey k:srSet){
			if(userSfk.contains(k)){
				tabList.add("{\"id\":\"srTab\",\"title\":\"SR\",\"viewURL\":\"js/sr/sr.js\"}");
				break;
			}
		}
		for(SystemFuncKey k:mmoSet){
			if(userSfk.contains(k)){
				tabList.add("{\"id\":\"mmoTab\",\"title\":\"MMO\",\"viewURL\":\"js/mmo/mmo.js\"}");
				break;
			}
		}
		for(SystemFuncKey k:financeSet){
			if(userSfk.contains(k)){
				tabList.add("{\"id\":\"finTab\",\"title\":\"Finance\",\"viewURL\":\"js/fin/fin.js\"}");
				break;
			}
		}
		for(SystemFuncKey k:codeTableSet){
			if(userSfk.contains(k)){
				tabList.add("{\"id\":\"codeTab\",\"title\":\"Code table\",\"viewURL\":\"js/code/code.js\"}");
				break;
			}
		}
		for(SystemFuncKey k:dnsSet){
			if(userSfk.contains(k)){
				tabList.add("{\"id\":\"dnsTab\",\"title\":\"SOAP\",\"viewURL\":\"js/dns/dns.js\"}");
				break;
			}
		}

		map.put("tabs", tabList);

//		map.put("tabs", Arrays.asList(
//				"{\"id\":\"systemTab\",\"title\":\"System\",\"viewURL\":\"systemIndex.js\"}",
//				"{\"id\":\"srTab\",\"title\":\"SR\",\"viewURL\":\"js/sr/sr.js\"}",
//				"{\"id\":\"mmoTab\",\"title\":\"MMO\",\"viewURL\":\"js/mmo/mmo.js\"}",
//				"{\"id\":\"finTab\",\"title\":\"Finance\",\"viewURL\":\"js/fin/fin.js\"}",
//				"{\"id\":\"codeTab\",\"title\":\"Code table\",\"viewURL\":\"js/code/code.js\"}"
//				));

	}


	public void logout(HttpServletRequest req){
		HttpSession httpSession = req.getSession();
//		httpSession.setAttribute(Key.USER_ID, userId);
//		httpSession.setAttribute(Key.CURRENT_USER, loginMap.get(Key.CURRENT_USER));
//		httpSession.setAttribute(Key.LOGIN_TIME, new Date());
		String userId = (String) req.getSession().getAttribute(Key.USER_ID);
		Date loginTime = (Date) req.getSession().getAttribute(Key.LOGIN_TIME);
		userService.logout( userId,
				loginTime,
				new Date(),
				req.getRemoteAddr());
		req.getSession().invalidate();
	}

	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

}
