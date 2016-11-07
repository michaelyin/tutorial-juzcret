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
	  
	
	  
	  
}
