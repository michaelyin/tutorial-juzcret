/**
 * 
 */
package org.juzu.tutorial;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;

import org.apache.commons.fileupload.FileItem;
import org.exoplatform.commons.api.search.SearchService;
import org.exoplatform.commons.api.search.data.SearchResult;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.wcm.search.connector.QysFileSearchServiceConnector;
import org.exoplatform.services.wcm.utils.WCMCoreUtils;
import org.json.JSONObject;

import juzu.Path;
import juzu.Resource;
import juzu.Response;
import juzu.View;
import juzu.impl.common.Tools;
import juzu.plugin.ajax.Ajax;
import juzu.plugin.asset.Assets;
import juzu.request.SecurityContext;
import juzu.template.Template;
import net.wyun.qys.domain.UserSetting;
import net.wyun.qys.domain.nationalpolicy.NPSourceType;
import net.wyun.qys.domain.nationalpolicy.NPolicyType;
import net.wyun.qys.domain.nationalpolicy.NationalPolicy;
import net.wyun.qys.domain.standard.StanJcrFile;
import net.wyun.qys.domain.standard.StanTag;
import net.wyun.qys.domain.standard.Standard;
import net.wyun.qys.domain.standard.StandardType;
import net.wyun.qys.service.NationalPolicyService;
import net.wyun.qys.service.StandardService;
import net.wyun.qys.service.UserService;
import net.wyun.qys.util.UserUtil;

/**
 * @author michael
 *
 */
public class NationalPolicyController {

	private static final Log LOG = ExoLogger.getExoLogger(Controller.class);
	  
	  @Inject
	  SearchService searchService;
		
	  @Inject
	  NationalPolicyService policySvc;
	  
	  
	  @Inject
	  @Path("interpretation.gtmpl")
	  Template interpretation;
	  
	  @Inject
	  @Path("new_interpretation.gtmpl")
	  Template new_interpretation;
	  
	  @Inject
	  @Path("interpretation_upload.gtmpl")
	  Template interpretation_upload;
	  
	  
	  private final static String ROOT_FOLDER = "fs/standard/";
	  @Resource
	  @Ajax
	  public Response.Content upload(String policyName, String policyNum, Integer selectPublishDept, 
			                         //String perm, String encrpLevel, String text, 
			                         //String author, String department, 
			                         String selectTag, Integer policyCategory, 
			                         String text, List<FileItem> tfiles, List<FileItem> efiles,
			                         SecurityContext securityContext){
		  
		  LOG.info(", selectTag: " + selectTag + ", type: " + policyCategory);
		  String userName = securityContext.getRemoteUser();
		  
		  NationalPolicy newP = new NationalPolicy();
		  
		  //save text to jcr as a file
		  JSONObject jo = new JSONObject(newP);
		  if(jo.has("class")){
			  jo.remove("class");
		  }
		  
		  String json = jo.toString();
		  LOG.info("json: " + json);
		  
		  return Response.ok(json).withMimeType("text/json").withCharset(Tools.UTF_8);
	  }
	  
	  @Assets({"interpretationcss", "interpretationjs"})  
	  @View
	  public Response.Content interpretation() throws IOException{
		  return interpretation.ok();
	  }
	  
	  @Assets({"interpretation_uploadjs","interpretation_uploadcss"})
	  @View
	  public Response.Content upload_form() throws IOException{
		  return interpretation_upload.ok();
	  }
	  
	  @Assets({"new_interpretationjs","new_interpretationcss"})
	  @View
	  public Response.Content new_interpretation() throws IOException{
		  return new_interpretation.ok();
	  }

	  
}
