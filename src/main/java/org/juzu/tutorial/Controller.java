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

import javax.inject.Inject;
import javax.jcr.LoginException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;

import org.exoplatform.commons.api.search.SearchService;
import org.exoplatform.commons.api.search.data.SearchContext;
import org.exoplatform.commons.api.search.data.SearchResult;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.web.controller.metadata.ControllerDescriptor;
import org.exoplatform.web.controller.metadata.DescriptorBuilder;
import org.exoplatform.web.controller.router.Router;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.Parameter;
import org.exoplatform.container.xml.PropertiesParam;
import org.exoplatform.container.xml.Property;
import org.exoplatform.webui.config.Param;
import org.exoplatform.services.wcm.search.connector.QysFileSearchServiceConnector;
import org.exoplatform.services.wcm.utils.WCMCoreUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Controller {

	public Controller() {
		super();
	}

	private static final Log LOG = ExoLogger.getExoLogger(Controller.class);

	@Inject
	SearchService searchService;

	@Inject
	UserService userService;

	@Inject
	StandardService standardSvc;

	@Inject
	@Path("index.gtmpl")
	Template index;


	@Assets({ "indexcss", "indexjs" })
	// @Assets("indexcss")
	@View
	public Response.Content index(SecurityContext securityContext) throws IOException {
		String username = securityContext.getRemoteUser();
		LOG.info("user: " + username);
		//UserSetting setting = userService.getUserSetting(username);
		//net.wyun.qys.model.User u = userService.loadUser(username);

		try {

			Identity identity = ConversationState.getCurrent().getIdentity();
			// org.exoplatform.social.core.identity.model.Identity identity =
			// identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME,
			// username, true);
			List<String> ms = UserUtil.getMemberships(identity);

			for (String m : ms) {
				LOG.info("membership: " + m);
			}
		} catch (Exception e) {
			LOG.error(e);
		}

		/*
		if (setting != null) {
			LOG.info("user setting: " + setting.getUsername());
		}
		if (u != null) {
			LOG.info("user avatar: " + u.getAvatar());
		}
		*/

		return index.ok();
	}


	/**
	 * This function is not working. leave it for future reference.
	 */
	@SuppressWarnings("unused")
	private void jcrSearch() {
		List<Node> ret = new ArrayList<Node>();

		SessionProvider sessionProvider = WCMCoreUtils.getUserSessionProvider();

		Session session;
		try {
			session = sessionProvider.getSession("collaboration", WCMCoreUtils.getRepository());
			String docPath = "/Groups/spaces/exo_kai_fa_zu";

			String q = "Select * from nt:file where jcr:path like " + docPath + "/%";
			LOG.info("query string: " + q);
			NodeIterator iter = session.getWorkspace().getQueryManager().createQuery(q, Query.SQL).execute().getNodes();

			while (iter.hasNext()) {

				Node n = iter.nextNode();
				LOG.info("node: " + n.getPath());
				ret.add(n);

			}
		} catch (RepositoryException e) {
			e.printStackTrace();
		}

	}

}
