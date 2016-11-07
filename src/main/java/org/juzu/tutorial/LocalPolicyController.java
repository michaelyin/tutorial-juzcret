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

public class LocalPolicyController {

	private static final Log LOG = ExoLogger.getExoLogger(LocalPolicyController.class);
	
	  @Inject
	  UserService userService;
	  
	  @Inject
	  PolicyService policySvc;
	  
	  
	  @Inject
	  @Path("zhengCeFaBu.gtmpl")
	  Template zhengCeFaBu;
	  
	 
	  
	  @Inject
	  @Path("map.gtmpl")
	  Template map;
	  
	  /*======================分割线=====================*/
	  
	  @View
	  public Response.Content zhengCeUpload() throws IOException{
		  return zhengCeFaBu.ok();
	  }
	  @View
	  public Response.Content back() throws IOException{
		  return map.ok();
	  }
	
	  @Assets({"mapjs","mapcss"})
	  @View
	  public Response.Content map() throws IOException {
		  return map.ok();
	  }
	  @View
	  public Response.Content map_new() throws IOException{
		  return map.ok();
	  }
	  
	 
	  
	  
	  
}
