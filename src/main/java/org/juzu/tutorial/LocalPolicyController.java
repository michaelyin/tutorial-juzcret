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
	  @Path("index.gtmpl")
	  Template index;
	  
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
	  
	  @Assets({"indexcss", "indexjs"})  
	  @View
	  public Response.Content index(SecurityContext securityContext) throws IOException {
		  String username = securityContext.getRemoteUser();//从服务器端获取用户名
		  LOG.info("user: " + username);
		  UserSetting setting = userService.getUserSetting(username);// ?
		  net.wyun.qys.model.User u = userService.loadUser(username);// ?
		  
		  try{//异常处理
			  
			  Identity identity = ConversationState.getCurrent().getIdentity();//获取验证信息
			  //org.exoplatform.social.core.identity.model.Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, username, true);
			  List<String> ms =UserUtil.getMemberships(identity);//把验证信息放到集合中
		     	  
			  for (String m : ms) {
				  LOG.info("membership: " + m);//用增强for循环把成员信息放到日志中
			  }
		  }catch(Exception e){//捕获异常信息并提示
			  LOG.error(e);
		  }
		  
		  
		  if(setting != null){//setting不为空则获取setting的用户名信息到日志中
			  LOG.info("user setting: " + setting.getUsername());
		  }
		  if(u != null){//u不为空则获取u的唯一标识到日志中
			  LOG.info("user avatar: " + u.getAvatar());
		  }
		  
		  //try policySvc
		  LOG.info("save policy");
		  Policy policy = new Policy();//实例化一个policy
		  policy.setPolicyName("test db saving");//实例化赋初值
		  policy.setStartDate(new Date());//并加上日期
		  this.policySvc.save(policy);//把policy的信息保存
		  
	      return index.ok();//最后返回到index页面
	  }
	  
	  
	  
}
