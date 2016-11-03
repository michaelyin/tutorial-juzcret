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
	  @Path("input_interpret.gtmpl")
	  Template input_interpret;
	  
	  @Inject
	  @Path("index.gtmpl")
	  Template index;
	  
	  @Inject
	  @Path("map.gtmpl")
	  Template map;
	  
	 /*----------------------分割线--------------------------------*/ 
	  
	  @Assets({"classifyFindcss","classifyFindjs"})
	  @View
	  public Response.Content content() throws IOException {
		  return content.ok();
	  }
	  
	  @Assets("input_interpretcss")
	  @View
	  public Response.Content interpret_upload() throws IOException {
	    return input_interpret.ok();
	  }
	  
	  @View
	  public Response.Content back() throws IOException{
		  return map.ok();
	  }
	  
	  @Assets({"indexcss", "indexjs"})  
	  @View
	  public Response.Content index(SecurityContext securityContext) throws IOException {
		  String username = securityContext.getRemoteUser();
		  LOG.info("user: " + username);
		  UserSetting setting = userService.getUserSetting(username);
		  net.wyun.qys.model.User u = userService.loadUser(username);
		  
		  try{			  
			  Identity identity = ConversationState.getCurrent().getIdentity();
			  //org.exoplatform.social.core.identity.model.Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, username, true);
			  List<String> ms =UserUtil.getMemberships(identity);
		     	  
			  for (String m : ms) {
				  LOG.info("membership: " + m);
			  }
		  }catch(Exception e){
			  LOG.error(e);
		  }		  
		  
		  if(setting != null){
			  LOG.info("user setting: " + setting.getUsername());
		  }
		  if(u != null){
			  LOG.info("user avatar: " + u.getAvatar());
		  }
		  LOG.info("save policy");
		  Policy policy = new Policy();
		  policy.setPolicyName("test db saving");
		  policy.setStartDate(new Date());
		  this.policySvc.save(policy);
		  
	      return index.ok();
	  }
	  
	  
}
