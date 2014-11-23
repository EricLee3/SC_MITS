package com.isec.sc.intgr.web;




import java.io.ByteArrayInputStream;
import java.text.MessageFormat;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.Document;

import com.isec.sc.intgr.api.delegate.SterlingApiDelegate;
import com.isec.sc.intgr.api.util.FileContentReader;
import com.isec.sc.intgr.api.xml.beans.OrganizationList;


@Controller
public class DefaultController {

	private static final Logger logger = LoggerFactory.getLogger(DefaultController.class);
	
	@Autowired private SterlingApiDelegate sterlingApiDelegate;
	
	
	@Value("${sc.api.login.template}")
	private String LOGIN_TEMPLATE;
	
	@Value("${sc.api.getOrganizationList.template}")
	private String GET_ORGANIZATION_LIST_TEMPLATE;
	
	/**
	 * 메인화면 이동
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = {"/","/index.do"})
	public String home() throws Exception{ 
		
		logger.debug("Redirect Index Page!!!");
		
//		return "redirect:index.html";
		return "index";
		
	}
	
	
	/**
	 * 로그인 화면 리다이렉트
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/login.do")
	public String goLogin() throws Exception{ 
		
		logger.debug("Redirect Login Page!!!");
		return "redirect:/admin/login.html";
		
	}
	
	
	/**
	 * 로그아웃 처리
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/logout.do")
	public String logout(HttpServletRequest req, HttpServletResponse res) throws Exception{ 
		
		
		HttpSession ses = req.getSession(false);
		
		logger.debug("[ses]"+ses);
		if(ses != null){
			ses.invalidate();
		}
		
		logger.debug("Redirect Login Page!!!");
		return "redirect:/admin/login.html";
		
	}
	
	
	/**
	 * 로그인 처리
	 * @param userid
	 * @param password
	 * @param req
	 * @param res
	 * @return
	 */
	@RequestMapping(value = "/login.sc")
	public ModelAndView login(@RequestParam String userid, @RequestParam String password,  HttpServletRequest req, HttpServletResponse res){
		
		logger.debug("[userid]"+userid);
//		logger.debug("[password]"+password);
		
		String orgCode = "";
		
		ModelAndView mav = new ModelAndView("jsonView");
		String succ = "01"; 
		
		String loginXMLTemplate = FileContentReader.readContent(getClass().getResourceAsStream(LOGIN_TEMPLATE));
		String inputXML = "";
		
		try{
			
			MessageFormat msg = new MessageFormat(loginXMLTemplate);
			inputXML = msg.format(new String[] {orgCode, userid, password} );
//			logger.debug("[inputXML]"+inputXML); 
			
			String outputXML = sterlingApiDelegate.comApiCall("login", inputXML);
			logger.debug("[outputXML]"+outputXML);
			
			
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(outputXML.getBytes("UTF-8")));
			XPath xp = XPathFactory.newInstance().newXPath();
			
			// 로그인실패
			if("Errors".equals(doc.getFirstChild().getNodeName())){
				
				logger.debug("[Error Message]"+outputXML);
				succ = "90";
				
			}else{
				
				// Session 저장
				succ = "01";
				
				/*
					<Login ActivateFlag="" BillingAddress_Key="" Business_Key=""
					    ChangePasswordLink="" ContactAddress_Key="" DaysUntilExpiration=""
					    DisplayUserID="" EnterpriseCode="" ExpiresInDays="" ImageFile=""
					    LocaleCode="" LoginID="" LongDesc="" Note_Key="" OrganizationCode=""
					    PWDLastChangedOn="" Password="" Preference_Key=""
					    RequirePasswordChange="" UserGroupID="" UserGroup_Description=""
					    UserGroup_Key="" UserGroup_Name="" UserName="" UserToken=""
					    UserType="" User_key="">
					    <!--The PasswordPolicyResult element will contain Password Policy Result attributes. -->
					    <PasswordPolicyResult/>
					</Login>				 * 
				 */
				String  S_LOGIN_ID = (String)xp.evaluate("@LoginID", doc.getDocumentElement(), XPathConstants.STRING);
				String  S_ORG_CODE = (String)xp.evaluate("@OrganizationCode", doc.getDocumentElement(), XPathConstants.STRING);
				
				
				// Input XML Creation
				/*
				   <?xml version="1.0" encoding="UTF-8"?>
					<Organization isEnterprise="{0}">
					<OrgRoleList>
						<OrgRole RoleKey="{1}"/>
					    </OrgRoleList>
						<DataAccessFilter UserId="{2}"/>
					</Organization>
				 */
				String getOrderList_input = FileContentReader.readContent(getClass().getResourceAsStream(GET_ORGANIZATION_LIST_TEMPLATE));
			    msg = new MessageFormat(getOrderList_input);
				inputXML = msg.format(new String[] {
						                                "Y",
						                                "ENTERPRISE",
						                                S_LOGIN_ID
													} );
				
			    String entOrgListOutPut = sterlingApiDelegate.comApiCall("getOrganizationList", inputXML);
				logger.debug("[entOrgListOutPut]"+entOrgListOutPut); 
				
				
				inputXML = msg.format(new String[] {
                        "Y",
                        "SELLER",
                        S_LOGIN_ID
					} );

				String sellerOrgListOutPut = sterlingApiDelegate.comApiCall("getOrganizationList", inputXML);
				logger.debug("[sellerOrgListOutPut]"+sellerOrgListOutPut); 
				
				
				// XML to JSON
				Serializer persister = new Persister();
				OrganizationList entOrgList =  persister.read(OrganizationList.class, entOrgListOutPut);
				OrganizationList sellerOrgList =  persister.read(OrganizationList.class, sellerOrgListOutPut);
				
				
				// TODO: 로그인시 판매조직 세션처리. 현재 ASPB고정값사용
//				String  S_SELL_CODE = "ASPB";
//				String  S_SELL_NAME = "Aspen Bay";
//				
//				if("DEFAULT".equals(S_ORG_CODE)){
//					S_ORG_CODE = "*";
//					S_SELL_CODE = "*";
//				}
//				logger.debug("[S_ORG_CODE]"+S_ORG_CODE);
//				logger.debug("[S_SELL_CODE]"+S_SELL_CODE);
//				logger.debug("[S_SELL_NAME]"+S_SELL_NAME);
//				
				// User의 소속그룹이 Default일 경우 전체조직을 조회할 수 있는 권한가짐
				if("DEFAULT".equals(S_ORG_CODE)){
					S_ORG_CODE = "*";
				}
				String  S_LOCALE = (String)xp.evaluate("@LocaleCode", doc.getDocumentElement(), XPathConstants.STRING);
				String  S_USER_NAME = (String)xp.evaluate("@UserName", doc.getDocumentElement(), XPathConstants.STRING);
				String  S_USER_GRP_NAME = (String)xp.evaluate("@UserGroup_Name", doc.getDocumentElement(), XPathConstants.STRING);
				logger.debug("[S_LOGIN_ID]"+S_LOGIN_ID);
				logger.debug("[S_LOCALE]"+S_LOCALE);
				logger.debug("[S_USER_NAME]"+S_USER_NAME);
				logger.debug("[S_USER_GRP_NAME]"+S_USER_GRP_NAME);
				
				HttpSession ses = req.getSession();
				ses.setAttribute("S_LOGIN_ID", S_LOGIN_ID);
				ses.setAttribute("S_LOCALE", S_LOCALE);
				ses.setAttribute("S_ORG_CODE", S_ORG_CODE);
				ses.setAttribute("S_USER_NAME", S_USER_NAME);
				ses.setAttribute("S_USER_GRP_NAME", S_USER_GRP_NAME);
				
				ses.setAttribute("S_ENT_ORG_LIST", entOrgList.getOrganization());
				ses.setAttribute("S_SELLER_ORG_LIST", sellerOrgList.getOrganization());
				
//				ses.setAttribute("S_SELL_CODE", S_SELL_CODE);
//				ses.setAttribute("S_SELL_NAME", S_SELL_NAME);
				
				ses.setMaxInactiveInterval(7200);
			}
			
			
		}catch(Exception e){
			
			e.printStackTrace();
			succ = "09";
		}
		
		
		
		
		
		mav.addObject("succ", succ);
		return mav;
	}
}
