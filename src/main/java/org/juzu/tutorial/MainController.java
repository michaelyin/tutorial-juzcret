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

public class MainController {//主函数
	
  private static final Log LOG = ExoLogger.getExoLogger(MainController.class);
	
  @Inject
  UserService userService;
  
  @Inject
  PolicyService policySvc;  

  @Inject
  @Path("index.gtmpl")
  Template index;
  
  @Inject
  @Path("interpretation.gtmpl")
  Template interpretation;
   
  @Inject
  @Path("content.gtmpl")
  Template content;
  
  @Inject
  @Path("zhengCeFaBu.gtmpl")
  Template zhengCeFaBu;
  
  @Inject
  @Path("map.gtmpl")
  Template map;
  
  @Inject
  @Path("guoJiaBiaozhun.gtmpl")
  Template guoJiaBiaozhun;
    
  @Inject
  @Path("biaoZhunFaBu.gtmpl")
  Template biaoZhunFaBu;
  

  
  @Inject
  @Path("jieDuUpload.gtmpl")
  Template jieDuUpload;
  /*----------------分割线----------------------*/
  
  
  @View
  public Response.Content interpretUpload() throws IOException{
	  return jieDuUpload.ok();
  }
  @View
  public Response.Content map_new() throws IOException{
	  return map.ok();
  }
  
  @Assets({"mapjs","mapcss"})
  @View
  public Response.Content map() throws IOException {
	  return map.ok();
  }
  
  @Assets({"zhengCeFaBujs","zhengCeFaBucss"})
  @View
  public Response.Content zhengCeFaBu()throws IOException{
	  return zhengCeFaBu.ok();
  }

  @Assets({"indexcss", "indexjs"})  
  @View
  public Response.Content index(SecurityContext securityContext) throws IOException {
	  String username = securityContext.getRemoteUser();//从服务器端获取用户名
	  LOG.info("user: " + username);
	  UserSetting setting = userService.getUserSetting(username);// ?
	  net.wyun.qys.model.User u = userService.loadUser(username);// ?
	  
	  try{//异常处理
		  
		  Identity identity = ConversationState.getCurrent().getIdentity();		
		  List<String> ms =UserUtil.getMemberships(identity);
	     	  
		  for (String m : ms) {
			  LOG.info("membership: " + m);
		  }
	  }catch(Exception e){
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
	  
      return index.ok();
  }
  @Assets({"interpretationjs","interpretationcss"})
  @View
  public Response.Content interpretation() throws IOException {
    return interpretation.ok();
  }
    
  
  @View
  public Response.Content content() throws IOException {
	  return content.ok();
  }
  
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
  public Response.Content back() throws IOException{
	  return map.ok();
  }
  @View
  public Response.Content notes_add_touch() throws IOException{
	  return jieDuUpload.ok();
  }
  /*----------下面是我的修改2016-10-27 15:43:18--------*/
  /*@Assets({"classifyFindcss","classifyFindjs"})
  @View
  public Response.Content content() throws IOException {
	  return content.ok();
  }
  
  @Assets({"mapjs","mapcss"})
  @View
  public Response.Content map() throws IOException {
	  return map.ok();
  }
  
  @Assets({"guoJiaBiaozhunjs","guoJiaBiaozhuncss"})
  @View
  public Response.Content guoJiaBiaozhun() throws IOException{
	  return guoJiaBiaozhun.ok();
  }
  
  @Assets({"classifyFindjs","classifyFindcss"})
  @View
  public Response.Content classifyFind() throws IOException{
	  return classifyFind.ok();
  }
  @View
  public Response.Content zhengCeFaBu()throws IOException{
	  return zhengCeFaBu.ok();
  }
  @View
  public Response.Content zhengCeUpload() throws IOException{
	  return zhengCeFaBu.ok();
  }
  
  @View
  public Response.Content back() throws IOException{
	  return map.ok();
  }
  
  @View
  public Response.Content notes_add_touch() throws IOException{
	  return jieDuUpload.ok();
  }
  
  //最新分类
  @View
  public Response.Content map_new() throws IOException{
	  return map.ok();
  }
  
 //最新标准
  @View
  public Response.Content zuiXinBiaoZhun() throws IOException{
	  return biaoZhunFaBu.ok();
  }
  
  //最新项目
  @View
  public Response.Content zuiXinXiangMu() throws IOException{
	  return xiangMuFaBu.ok();
  }*/
  
  
  /*-------------上面是我的修改2016-10-27 15:43:46-----------------*/
}
