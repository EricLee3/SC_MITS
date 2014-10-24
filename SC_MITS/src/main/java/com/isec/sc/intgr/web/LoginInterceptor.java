package com.isec.sc.intgr.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.isec.sc.intgr.api.handler.ScOrderStatusHandler;

@Service
public class LoginInterceptor extends HandlerInterceptorAdapter {

	private static final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);
	
	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception{
		
		
		String path = req.getServletPath();
		logger.debug("[PATH]"+path);
		
		if(path.equals("/login.sc")){
			return true;
		}
		
		if(path.startsWith("/sc")){
			
			return true;
		}
		
		
		// Session Check
//		@SuppressWarnings("unchecked")
//		Map<String, Object> sesUserInfoMap = (HashMap<String, Object>)req.getSession().getAttribute("SES_USER_INFO");
		String sesUserId = (String)req.getSession().getAttribute("S_LOGIN_ID");
		logger.debug("[sesUserId]"+sesUserId);
		
		if(sesUserId == null){
			
			res.sendRedirect("/admin/login.html");
			return false;
		}else{
			return true;
		}
		
	}
	
	
}
