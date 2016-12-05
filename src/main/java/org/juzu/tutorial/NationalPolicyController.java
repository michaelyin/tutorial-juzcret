/**
 * 
 */
package org.juzu.tutorial;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.json.JSONArray;
import org.json.JSONException;
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
import net.wyun.list.bean.File;
import net.wyun.qys.domain.JcrFileType;
import net.wyun.qys.domain.UserSetting;
import net.wyun.qys.domain.localpolicy.LocalPolicy;
import net.wyun.qys.domain.nationalpolicy.NPJcrFile;
import net.wyun.qys.domain.nationalpolicy.NPSourceType;
import net.wyun.qys.domain.nationalpolicy.NPTag;
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
	  DocumentsDataHelper documentsData;
	  
	  
	  @Inject
	  @Path("interpretation.gtmpl")
	  Template interpretation;
	  
	  @Inject
	  @Path("new_interpretation.gtmpl")
	  Template new_interpretation;
	  
	  @Inject
	  @Path("interpretation_upload.gtmpl")
	  Template interpretation_upload;
	  
	  @Inject
	  @Path("standard_content.gtmpl")
	  Template content;
	  
	  
	  @Resource
	  @Ajax
	  public Response.Content policyContent(String uuid) throws IOException{
		  NationalPolicy stan = null;
		  if(null != uuid && !uuid.isEmpty()){
			  stan = policySvc.findById(uuid);
		  }
		  
		  List<File> files = new ArrayList<File>();
		  Set<NPJcrFile> jcrFiles = stan.getJcrFiles();
		  for(NPJcrFile sjf:jcrFiles){
			  String jcrUuid = sjf.getUuid();
			  LOG.info("file name: " + sjf.getFileName());
			  File file = documentsData.getNode(jcrUuid);
			  files.add(file);
		  }
		  
		  
		  return content.with().set("stan", stan).set("files", files).ok();
	  }	  
	  /**
	   * with out user input, search_text is empty string, search_type is null
	   * so need to do data validation before searching.
	   * @param search_text
	   * @param search_type
	   * @return
	   */
	  
	  @Resource
	  @Ajax
	  public Response.Content search(String search_text, String[] search_type){
		  
		  search_text = search_text.trim();
		  LOG.info("keyword: " + search_text);
		  
		  Set<NPSourceType> types = new HashSet<NPSourceType>();
		  if(search_type != null){
			  for(String i:search_type){
				  LOG.info("type: " + i);  //StandardType in numbers
				  Integer n = Integer.parseInt(i);
				  types.add(NPSourceType.typeForValue(n));
			  }
		  }
		  
		  //with search types, query db to get qualified standard(s)
		 List<NationalPolicy> stanList = policySvc.findByTypes(types);
		 
		 if(stanList.isEmpty()){
			 //return directly
			 JSONObject mainObj = this.generateSearchResult(new HashSet<NationalPolicy>());
			 return Response.ok(mainObj.toString()).withMimeType("text/json").withCharset(Tools.UTF_8);
		 }
		 
		 Map<String, NationalPolicy> stanMap = new HashMap<String, NationalPolicy>();
		 for(NationalPolicy stan:stanList){
			 if(!stan.getE_uuid().isEmpty()) stanMap.put(stan.getE_uuid(), stan);
			 if(!stan.getT_uuid().isEmpty()) stanMap.put(stan.getT_uuid(), stan);
			 Set<NPJcrFile> jcrFiles = stan.getJcrFiles();
			 for(NPJcrFile sjf:jcrFiles){
				 stanMap.put(sjf.getUuid(), stan);
			 }
			 
		 }
		  
		  
		  //search jcr with keyword
		  Collection<SearchResult> connectorResults = null;
		  try {

			QysFileSearchServiceConnector fssc = new QysFileSearchServiceConnector(
					QysFileSearchServiceConnector.initFileSearchProp());
			//QysFileSearchServiceConnector.SEARCH_PATH = "/Groups/spaces";
		    //fssc.setSearchSubDir("standard");
			connectorResults = fssc.searchQys(search_text, "nationalpolicy");

	      } catch (Exception e) {
			LOG.error(e.getMessage(), e);
		  }
		  LOG.info("total jcr records found: " + connectorResults.size());
		  
		  Set<NationalPolicy> finalSet = new HashSet<NationalPolicy>();
		  for(SearchResult sr:connectorResults){
			  String uuid = sr.getDetail();
			  if(stanMap.containsKey(uuid)){
				  finalSet.add(stanMap.get(uuid));
			  }
		  }
		   
		  JSONObject mainObj = this.generateSearchResult(finalSet);
		 
		  
		  return Response.ok(mainObj.toString()).withMimeType("text/json").withCharset(Tools.UTF_8);
	  }
	  
	  private JSONObject generateSearchResult(Set<NationalPolicy> stanSets){
          JSONArray ja = new JSONArray();
		  
          for(NationalPolicy st:stanSets){
        	  ja.put(new JSONObject(st));
          }
		  
		  JSONObject mainObj = new JSONObject();
		  try {
			mainObj.put("resultList", ja);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		  return mainObj;
	  }
	  
	  
	  
	  private final static String ROOT_FOLDER = "fs/nationalpolicy/";
	  private final static String TEXT_T = "T";  //text
	  private final static String TEXT_E = "E";  //explanation
	  @Resource
	  @Ajax
	  public Response.Content upload(String policyName, String policyNum, Integer publishDept, 
			                         //String perm, String encrpLevel, String text, 
			                         //String author, String department, 
			                         String tag, Integer policyCategory, String InterpretationTxt,
			                         String policyTxt, List<FileItem> tfiles, List<FileItem> efiles,
			                         SecurityContext securityContext){
		  
		  LOG.info(", selectTag: " + tag + ", type: " + policyCategory);
		  String userName = securityContext.getRemoteUser();
		  
		  NationalPolicy p = new NationalPolicy();
		  p.setName(policyName);
		  p.setCreateDate(new Date());
		  p.setNum(policyNum);
		  p.setType(NPolicyType.typeForValue(policyCategory));
		  p.setSource(NPSourceType.typeForValue(publishDept));
		  p.setT_uuid("");
		  p.setE_uuid("");
		  p.setDepartment("qys");
		  p.setCreator(userName);
		  
		  NPTag npTag = new NPTag();
		  npTag.setTag(tag);
		  p.addNPTag(npTag);
		  
		  NationalPolicy newP = policySvc.save(p);
		  String pFolder = newP.getId();
		  
		  //create jcr folder here
		  boolean isCreated = documentsData.createNodeIfNotExist("Documents/" + ROOT_FOLDER, pFolder);
		  LOG.info(pFolder + " folder is created: " + isCreated);
		  
		  if(policyTxt != null && !policyTxt.isEmpty()){
			  String txtUuid = documentsData.storeContent(policyTxt, TEXT_T + pFolder + ".txt", ROOT_FOLDER, pFolder);
			  newP.setT_uuid(txtUuid);
		  }
		  
		  if(InterpretationTxt != null && !InterpretationTxt.isEmpty()){
			  String txtUuid = documentsData.storeContent(InterpretationTxt, TEXT_E + pFolder + ".txt", ROOT_FOLDER, pFolder);
			  newP.setE_uuid(txtUuid);
		  }
		  
		  //now save jcrfiles for original text
		  if(null != tfiles){
			  for(FileItem fi:tfiles){
	        	  LOG.info("file name: " + fi.getName());
	        	  
	        	  //need to get a jcr compliant file name if the name is in Chinese or have special characters
	        	  String jcrFileName = net.wyun.qys.util.Util.cleanNameUtil(fi.getName());
	        	  LOG.info("jcr file name: " + jcrFileName);
	        	  
	        	  String uuid = documentsData.storeFile(ROOT_FOLDER + pFolder , fi, documentsData.getSpaceName(), false, null);
	        	  NPJcrFile jFile = new NPJcrFile();
	    		  jFile.setFileName(fi.getName());
	    		  jFile.setUploadDate(new Date());
	    		  jFile.setUrl("temp/url");
	    		  jFile.setUuid(uuid);
	    		  jFile.setType(JcrFileType.TEXT);
	    		  newP.addNPJcrFile(jFile);
	          }
		  }
		  
		  if(null != efiles){
			  for(FileItem fi:efiles){
	        	  LOG.info("file name: " + fi.getName());
	        	  
	        	  //need to get a jcr compliant file name if the name is in Chinese or have special characters
	        	  String jcrFileName = net.wyun.qys.util.Util.cleanNameUtil(fi.getName());
	        	  LOG.info("jcr file name: " + jcrFileName + ", being pesisted.");
	        	  
	        	  String uuid = documentsData.storeFile(ROOT_FOLDER + pFolder , fi, documentsData.getSpaceName(), false, null);
	        	  NPJcrFile jFile = new NPJcrFile();
	    		  jFile.setFileName(fi.getName());
	    		  jFile.setUploadDate(new Date());
	    		  jFile.setUrl("temp/url");
	    		  jFile.setUuid(uuid);
	    		  jFile.setType(JcrFileType.EXPLANATION);
	    		  newP.addNPJcrFile(jFile);
	          }
		  }
		  policySvc.update(newP);
		  
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
