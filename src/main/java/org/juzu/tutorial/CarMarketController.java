package org.juzu.tutorial;

import javax.inject.Inject;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import net.wyun.qys.service.PolicyService;
import net.wyun.qys.service.UserService;

public class CarMarketController {

	 private static final Log LOG = ExoLogger.getExoLogger(CarMarketController.class);
		
	  @Inject
	  UserService userService;
	  
	  @Inject
	  PolicyService policySvc;  
	  
	  
	  
	  
	  
}
