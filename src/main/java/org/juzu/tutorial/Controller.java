/*
 * Copyright 2013 eXo Platform SAS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.juzu.tutorial;

import juzu.Path;
import juzu.View;
import juzu.plugin.asset.Assets;
import juzu.request.SecurityContext;
import juzu.Response;
import juzu.template.Template;
import net.wyun.qys.domain.Policy;
import net.wyun.qys.domain.UserSetting;
import net.wyun.qys.service.PolicyService;
import net.wyun.qys.service.UserService;
import net.wyun.qys.util.UserUtil;

import javax.inject.Inject;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.social.core.manager.IdentityManager;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class Controller {
	
  private static final Log LOG = ExoLogger.getExoLogger(Controller.class);
	
  @Inject
  UserService userService;
  
  @Inject
  PolicyService policySvc;
  
 // @Inject
 // private IdentityManager identityManager;
//-----下面是我的修改2016-10-27 15:40:38-------------------
  @Inject
  @Path("classifyFind.gtmpl")
  Template classifyFind;
  
  
  
  //----------上面是我的修改2016-10-27 15:41:11---------------------
  @Inject
  @Path("index.gtmpl")
  Template index;
  
  @Inject
  @Path("interpretation.gtmpl")
  Template interpretation;
  
  @Inject
  @Path("input_interpret.gtmpl")
  Template input_interpret;

  @Assets({"indexcss", "indexjs"})
  //@Assets("indexcss")
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
	  
	  //try policySvc
	  LOG.info("save policy");
	  Policy policy = new Policy();
	  policy.setPolicyName("test db saving");
	  policy.setStartDate(new Date());
	  this.policySvc.save(policy);
	  
      return index.ok();
  }
  
  @View
  public Response.Content interpret() throws IOException {
    return interpretation.ok();
  }
  
  @Assets("input_interpretcss")
  @View
  public Response.Content interpret_upload() throws IOException {
    return input_interpret.ok();
  }
  
  //----------下面是我的修改2016-10-27 15:43:18--------
  @Assets({"classifyFindcss","classifyFindjs"})
  @View
  public Response.Content content() throws IOException {
	  return content.ok();
  }
  
  //-------------上面是我的修改2016-10-27 15:43:46-----------------
}
