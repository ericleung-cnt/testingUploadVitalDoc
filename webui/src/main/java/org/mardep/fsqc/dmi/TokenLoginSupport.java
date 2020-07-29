package org.mardep.fsqc.dmi;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jose4j.jwt.JwtClaims;
import org.mardep.ssrs.constant.Key;
import org.mardep.ssrs.constant.UserLoginResult;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.service.IUserService;
import org.mardep.fsqc.sso.ISecurityTokenProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("securityDmiTokenLoginSupport")
public class TokenLoginSupport {
	private final Logger logger = LoggerFactory.getLogger(TokenLoginSupport.class);

	@Value("${fsqc.sso.cookieSubdomain}")
	private String cookieSubdomain;

	@Value("${fsqc.sso.secCookieName:id_token}")
	private String secCookieName;

	@Value("${fsqc.httpsSupported:false}")
	private boolean secureCookieForToken;

	@Value("${fsqc.sso.expireTimeInSecond:28800}")
	private long expireTimeInSecond;

	@Autowired
	@Qualifier("fsqc-sso-jwtProvider")
	ISecurityTokenProvider securityTokenProvider;

	@Autowired
	private IUserService userService;

	public User tokenLogin(HttpServletRequest req, Map<String, Object> map) {
		Cookie secCookie = null;
		for (Cookie cookie : req.getCookies()) {
			if (cookie.getName().equals(secCookieName)) {
				secCookie = cookie;
				break;
			}
		}
		if (secCookie != null) {
			try {
				String remoteAddress = req.getRemoteAddr();
				Map<String, Object> loginMap = userService.tokenLogin(secCookie.getValue(), remoteAddress);
				if (loginMap.get(Key.LOGIN_RESULT).equals(UserLoginResult.SUCCESSFUL)) {
					User user = (User) loginMap.get(Key.CURRENT_USER);
					HttpSession httpSession = req.getSession();
					httpSession.setAttribute(Key.USER_ID, user.getId());
					httpSession.setAttribute(Key.CURRENT_USER, user);
					// httpSession.setAttribute(Key.LOGIN_TIME, new Date());
					httpSession.setAttribute(Key.LOGIN_TIME, loginMap.get(Key.LOGIN_TIME));
					httpSession.setAttribute(Key.IS_EXTERNAL, true);
					httpSession.setAttribute(Key.OFFICE_CODE, loginMap.get(Key.OFFICE_CODE));
					return user;
				} else {
					return null;
				}
			} catch (Throwable t) {
				logger.warn("token login failed", t);
				return null;
			}
		} else {
			return null;
		}
	}

	public void addSecTokenFromCookie(String userId, HttpServletResponse resp) {
		JwtClaims claims = new JwtClaims();
		claims.setSubject(userId);
		claims.setIssuedAtToNow();

		Cookie cookie = new Cookie(secCookieName, securityTokenProvider.createToken(claims));
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		cookie.setSecure(secureCookieForToken);
		cookie.setMaxAge((int) expireTimeInSecond);

		cookie.setDomain(cookieSubdomain);
		resp.addCookie(cookie);
	}

	public void removeSecTokenToCookie(HttpServletRequest req, HttpServletResponse resp) {
		for (Cookie cookie : req.getCookies()) {
			if (cookie.getName().equals(secCookieName)) {
				cookie.setHttpOnly(true);
				cookie.setPath("/");
				cookie.setSecure(secureCookieForToken);
				cookie.setMaxAge(0);
				cookie.setDomain(cookieSubdomain);
				resp.addCookie(cookie);
				break;
			}
		}
	}
}