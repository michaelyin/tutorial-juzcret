package org.juzu.tutorial;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;

import org.apache.commons.fileupload.FileItem;
import org.exoplatform.commons.api.search.SearchService;
import org.exoplatform.commons.api.search.data.SearchResult;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.wcm.search.connector.QysFileSearchServiceConnector;
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
import net.wyun.qys.domain.localpolicy.LPJcrFile;
import net.wyun.qys.domain.localpolicy.LPSourceType;
import net.wyun.qys.domain.localpolicy.LPTag;
import net.wyun.qys.domain.localpolicy.LPolicyType;
import net.wyun.qys.domain.localpolicy.Province;
import net.wyun.qys.domain.nationalpolicy.NPJcrFile;
import net.wyun.qys.domain.nationalpolicy.NPSourceType;
import net.wyun.qys.domain.nationalpolicy.NationalPolicy;
import net.wyun.qys.domain.localpolicy.LocalPolicy;

import net.wyun.qys.service.LocalPolicyService;
import net.wyun.qys.service.UserService;

public class LocalPolicyController {

	public LocalPolicyController() {
		super();
		// TODO Auto-generated constructor stub
	}

private static final Log LOG = ExoLogger.getExoLogger(LocalPolicyController.class);
  
  @Inject
  SearchService searchService;
	
  @Inject
  UserService userService;
  
  @Inject
  LocalPolicyService policySvc;
  
  @Inject
  DocumentsDataHelper documentsData;
  
  
  @Inject
  @Path("new_local.gtmpl")
  Template new_local;
  
  @Inject
  @Path("local_upload.gtmpl")
  Template local_upload;
  
  @Inject
  @Path("map.gtmpl")
  Template map;
  
  @Inject
  @Path("standard_content.gtmpl")
  Template content;
  
  @Resource
  @Ajax
  public Response.Content policyContent(String uuid) throws IOException{
	  LocalPolicy stan = null;
	  if(null != uuid && !uuid.isEmpty()){
		  stan = policySvc.findById(uuid);
	  }
	  
	  List<File> files = new ArrayList<File>();
	  Set<LPJcrFile> jcrFiles = stan.getJcrFiles();
	  for(LPJcrFile sjf:jcrFiles){
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
  public Response.Content search(String search_text, String[] search_type, String province_str){
	  
	  search_text = search_text.trim();
	  LOG.info("keyword: " + search_text + "provinces: " + province_str);
	  
	  Set<LPolicyType> types = new HashSet<LPolicyType>();
	  if(search_type != null){
		  for(String st:search_type){
			  LOG.info("type: " + st);  //StandardType in numbers
			  LPolicyType lpt = LPolicyType.其他;
			  try{
				  lpt = LPolicyType.valueOf(st);
			  }catch(Exception e){
				  LOG.error("type: " + st, e);
				  lpt = LPolicyType.其他;
			  }
			  types.add(lpt);
		  }
	  }
	  Set<Province> sProv = new HashSet<Province>();
	  if(!province_str.isEmpty()){
		  String[] prvs = province_str.split(";");
		  for(String st:prvs){
			  st = st.trim();
			  if(st.isEmpty()) continue;
			  sProv.add(Province.valueOf(st));
		  }
	  }
	  
	  //with search types, query db to get qualified standard(s)
	 List<LocalPolicy> stanList = policySvc.findByTypes(types, sProv);
	 
	 if(stanList.isEmpty()){
		 //return directly
		 JSONObject mainObj = this.generateSearchResult(new HashSet<LocalPolicy>());
		 return Response.ok(mainObj.toString()).withMimeType("text/json").withCharset(Tools.UTF_8);
	 }
	 
	 Map<String, LocalPolicy> stanMap = new HashMap<String, LocalPolicy>();
	 for(LocalPolicy stan:stanList){
		 if(!stan.getE_uuid().isEmpty()) stanMap.put(stan.getE_uuid(), stan);
		 if(!stan.getT_uuid().isEmpty()) stanMap.put(stan.getT_uuid(), stan);
		 Set<LPJcrFile> jcrFiles = stan.getJcrFiles();
		 for(LPJcrFile sjf:jcrFiles){
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
		connectorResults = fssc.searchQys(search_text, "localpolicy");

      } catch (Exception e) {
		LOG.error(e.getMessage(), e);
	  }
	  LOG.info("total jcr records found: " + connectorResults.size());
	  
	  //sorting by create_t
	  final Comparator<LocalPolicy> REV_DATE_COMP = new Comparator<LocalPolicy>() {
		    @Override
		    public int compare(LocalPolicy d1, LocalPolicy d2) {
		        return d2.getCreateDate().compareTo(d1.getCreateDate());
		    }
	  };   
	  Set<LocalPolicy> finalSet = new TreeSet<LocalPolicy>(REV_DATE_COMP);
	  for(SearchResult sr:connectorResults){
		  String uuid = sr.getDetail();
		  if(stanMap.containsKey(uuid)){
			  finalSet.add(stanMap.get(uuid));
		  }
	  }
	   
	  JSONObject mainObj = this.generateSearchResult(finalSet);
	 
	  
	  return Response.ok(mainObj.toString()).withMimeType("text/json").withCharset(Tools.UTF_8);
  }
  
  private JSONObject generateSearchResult(Set<LocalPolicy> stanSets){
      JSONArray ja = new JSONArray();
	  
      for(LocalPolicy st:stanSets){
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
  
  
  private final static String ROOT_FOLDER = "fs/localpolicy/";
  private final static String TEXT_T = "T";  //text
  private final static String TEXT_E = "E";  //explanation
  @Resource
  @Ajax
  public Response.Content upload(String policyName, String policyNum, Integer publishDept, 
		                         String selectProvince, String selectcity,  
		                         //String author, String department, 
		                         String tag, String policyCategory, String InterpretationTxt,
		                         String policyTxt, List<FileItem> tfiles, List<FileItem> efiles,
		                         SecurityContext securityContext){
	  
	  LOG.info(", selectTag: " + tag + ", type: " + policyCategory);
	  String userName = securityContext.getRemoteUser();
	  
	  LPolicyType lpt = LPolicyType.valueOf(policyCategory);
	  
	  LocalPolicy p = new LocalPolicy();
	  p.setName(policyName);
	  p.setCreateDate(new Date());
	  p.setNum(policyNum);
	  p.setType(lpt);
	  p.setCity(selectcity);
	  p.setProvince(Province.valueOf(selectProvince));
	  p.setSource(LPSourceType.typeForValue(publishDept));
	  p.setT_uuid("");
	  p.setE_uuid("");
	  p.setDepartment("qys");
	  p.setCreator(userName);
	  
	  LPTag npTag = new LPTag();
	  npTag.setTag(tag);
	  p.addLPTag(npTag);
	  
	  LocalPolicy newP = policySvc.save(p);
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
        	  LPJcrFile jFile = new LPJcrFile();
    		  jFile.setFileName(fi.getName());
    		  jFile.setUploadDate(new Date());
    		  jFile.setUrl("temp/url");
    		  jFile.setUuid(uuid);
    		  jFile.setType(JcrFileType.TEXT);
    		  newP.addLPJcrFile(jFile);
          }
	  }
	  
	  if(null != efiles){
		  for(FileItem fi:efiles){
        	  LOG.info("file name: " + fi.getName());
        	  
        	  //need to get a jcr compliant file name if the name is in Chinese or have special characters
        	  String jcrFileName = net.wyun.qys.util.Util.cleanNameUtil(fi.getName());
        	  LOG.info("jcr file name: " + jcrFileName + ", being pesisted.");
        	  
        	  String uuid = documentsData.storeFile(ROOT_FOLDER + pFolder , fi, documentsData.getSpaceName(), false, null);
        	  LPJcrFile jFile = new LPJcrFile();
    		  jFile.setFileName(fi.getName());
    		  jFile.setUploadDate(new Date());
    		  jFile.setUrl("temp/url");
    		  jFile.setUuid(uuid);
    		  jFile.setType(JcrFileType.EXPLANATION);
    		  newP.addLPJcrFile(jFile);
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
  
  @Assets({"new_localjs","new_interpretationcss"})
  @View
  public Response.Content new_local() throws IOException{
	  return new_local.ok();
  }
  
  //最新地方政策的上传页面
  @Assets({"local_uploadjs","local_uploadcss"})
  @View
  public Response.Content  upload_form() throws IOException{
	  return local_upload.ok();
  }
  
  
  @Assets({/*"echartsjs",*/ "jquerypage", "mapjs", "getmapdatajs", "mapcss"})
  @View
  public Response.Content map() throws IOException{
	  return map.ok();
  }
  
  
}
