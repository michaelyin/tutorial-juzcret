package org.juzu.tutorial;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;

import juzu.Path;
import juzu.Response;
import juzu.View;
import juzu.plugin.asset.Assets;
import juzu.request.SecurityContext;
import juzu.template.Template;
import net.wyun.qys.domain.Policy;
import net.wyun.qys.domain.UserSetting;
import net.wyun.qys.service.PolicyService;
import net.wyun.qys.service.UserService;
import net.wyun.qys.util.UserUtil;

public class NationalStandardController {

	private static final Log LOG = ExoLogger.getExoLogger(MainController.class);
	
	  @Inject
	  UserService userService;
	  
	  @Inject
	  PolicyService policySvc;
	  
	  @Inject
	  @Path("content.gtmpl")
	  Template content;
	  
	  
	  
	  @Inject
	  @Path("map.gtmpl")
	  Template map;
	  
	  @Inject
	  @Path("guoJiaBiaozhun.gtmpl")
	  Template guoJiaBiaozhun;
	  @Inject
	  @Path("biaoZhunFaBu.gtmpl")
	  Template biaoZhunFaBu;
	  
	 /*----------------------分割线--------------------------------*/ 
	  
	  
	  @Assets({"guoJiaBiaozhunjs","guoJiaBiaozhuncss"})
	  @View
	  public Response.Content guoJiaBiaozhun() throws IOException{
		  return guoJiaBiaozhun.ok();
	  }
	  @View
	  public Response.Content zuiXinBiaoZhun() throws IOException{
		  return biaoZhunFaBu.ok();
	  }
	  
	  @View
	  public Response.Content interpretUpload() throws IOException{
		  return guoJiaBiaozhun.ok();
	  }
	  
	  @Assets({"classifyFindcss","classifyFindjs"})
	  @View
	  public Response.Content content() throws IOException {
		  return content.ok();
	  }
	  
	  @View
	  public Response.Content back() throws IOException{
		  return map.ok();
	  }
	  
	  
	  
	  
}
