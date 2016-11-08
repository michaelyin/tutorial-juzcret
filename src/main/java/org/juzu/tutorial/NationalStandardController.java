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

	private static final Log LOG = ExoLogger.getExoLogger(NationalStandardController.class);	
	
	  @Inject
	  UserService userService;
	  
	  @Inject
	  PolicyService policySvc;
	  
	  @Inject
	  @Path("standards.gtmpl")
	  Template standards;
	  
	  @Inject
	  @Path("standards_upload.gtmpl")
	  Template standards_upload;
	  
	  @Inject
	  @Path("new_standards.gtmpl")
	  Template new_standards;
	  
	  @Inject
	  @Path("content.gtmpl")
	  Template content;
	  
	/* ----------------------分割线-------------------------------- */
	  @Assets({"standardscss", "standardsjs"}) 
	  @View
	  public Response.Content standards() throws IOException{
		  return standards.ok();
	  }
	  @Assets({"new_standardscss","new_standardsjs"})
	  @View
	  public Response.Content new_standards() throws IOException{
		  return new_standards.ok();
	  }
	  
	  @View 
	  public Response.Content back() throws IOException{
		  return standards.ok();
	  }
	  @View 
	  public Response.Content content() throws IOException{
		  return content.ok();
	  }	  
	  
	  @View 
	  public Response.Content update_data() throws IOException{
		  return standards_upload.ok();
	  }
	  @View 
	  public Response.Content file_add() throws IOException{
		  
		  
		  
		  return standards_upload.ok();
	  }
	  @View 
	  public Response.Content file_delete() throws IOException{
		  return standards_upload.ok();
	  }
	  
	  @View 
	  public Response.Content notes_add_touch() throws IOException{
		  return standards_upload.ok();
	  }
	  
	
	  
	  
	  
}
