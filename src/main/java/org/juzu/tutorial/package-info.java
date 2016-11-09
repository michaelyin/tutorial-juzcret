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

@juzu.Application
//@Portlet
@Bindings({
    @Binding(value = OrganizationService.class),
    @Binding(value = UserService.class),
    @Binding(value = PolicyService.class),
})



@Scripts({
    @Script(id = "indexjs", value = "js/index.js", location=AssetLocation.APPLICATION),
    @Script(id = "standardsjs",value ="js/standards.js",location=AssetLocation.APPLICATION),
    @Script(id = "new_standardsjs",value ="js/new_standards.js",location=AssetLocation.APPLICATION),
    @Script(id = "filedragjs",value="js/filedrag.js",location=AssetLocation.APPLICATION),
    @Script(id = "fileuploadjs",value="js/fileupload.js",location=AssetLocation.APPLICATION),
    @Script(id = "jquery-1.8.3.minjs",value="js/jquery-1.8.3.min.js",location=AssetLocation.APPLICATION),
    @Script(id = "jquery.filedropjs",value="js/jquery.filedrop.js",location=AssetLocation.APPLICATION),
    @Script(id = "jquery.formjs",value="js/jquery.form.js",location=AssetLocation.APPLICATION),
    @Script(id = "jquery.pagejs",value="js/jquery.page.js",location=AssetLocation.APPLICATION),
    @Script(id = "jszip.minjs",value="js/jszip.min.js",location=AssetLocation.APPLICATION),
    @Script(id = "libjs",value="js/lib.js",location=AssetLocation.APPLICATION),
    @Script(id = "nlibjs",value="js/nlib.js",location=AssetLocation.APPLICATION),
    @Script(id = "mouseoutjs",value="js/mouseout.js",location=AssetLocation.APPLICATION),
    @Script(id = "mouseoverjs",value="js/mouseover.js",location=AssetLocation.APPLICATION),
    @Script(id = "echartsjs",value="js/echarts.js",location=AssetLocation.APPLICATION),
    @Script(id = "echarts.minjs",value="js/echarts.min.js",location=AssetLocation.APPLICATION),
    @Script(id = "publicjs",value="js/public.js",location=AssetLocation.APPLICATION),
    @Script(id = "scriptjs",value="js/script.js",location=AssetLocation.APPLICATION),
    @Script(id = "searchjs",value="js/search.js",location=AssetLocation.APPLICATION),
    
    
    
})

@Stylesheets ({
    @Stylesheet(id = "indexcss", value = "styles/index.css", location = AssetLocation.APPLICATION),
    @Stylesheet(id = "standardscss", value = "styles/standards.css", location = AssetLocation.APPLICATION),
    @Stylesheet(id = "searchcss", value = "styles/search.css", location = AssetLocation.APPLICATION),
    @Stylesheet(id = "new_standardscss", value = "styles/new_standards.css", location = AssetLocation.APPLICATION),
    @Stylesheet(id = "assetscss", value = "styles/assets.css", location = AssetLocation.APPLICATION),
    
    

    
})




package org.juzu.tutorial;

//import juzu.plugin.asset.Assets;
import juzu.plugin.asset.Script;
import juzu.plugin.asset.Scripts;
import juzu.plugin.asset.Stylesheet;
import juzu.plugin.asset.Stylesheets;
import juzu.plugin.binding.Binding;
import juzu.plugin.binding.Bindings;
//import juzu.plugin.less4j.Less;
//import juzu.plugin.portlet.Portlet;
//import juzu.plugin.webjars.WebJar;
//import juzu.plugin.webjars.WebJars;
import net.wyun.qys.service.PolicyService;
import net.wyun.qys.service.UserService;
import juzu.asset.AssetLocation;

import org.exoplatform.services.organization.OrganizationService;
