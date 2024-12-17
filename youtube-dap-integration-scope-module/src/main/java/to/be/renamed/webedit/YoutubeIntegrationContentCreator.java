package to.be.renamed.webedit;

import de.espirit.firstspirit.module.WebApp;
import de.espirit.firstspirit.module.WebEnvironment;
import de.espirit.firstspirit.module.descriptor.WebAppDescriptor;

import com.espirit.moddev.components.annotations.WebAppComponent;
import com.espirit.moddev.components.annotations.WebResource;

import static to.be.renamed.integration.ProjectAppHelper.WEBAPP_DISPLAY_NAME;
import static to.be.renamed.integration.ProjectAppHelper.WEBAPP_NAME;

/**
 * The FirstSpirit Youtube integration content creator WebApp.
 * Required to provide all necessary resources.
 */
@WebAppComponent(name = WEBAPP_NAME,
		displayName = WEBAPP_DISPLAY_NAME,
		description = "Web application that can be used in the ContentCreator.",
		webXml = "web/web.xml",
		xmlSchemaVersion = "6.0",
		webResources = {@WebResource(name = "webfiles", version = "", path = "web/", targetPath = "/yt"),
						@WebResource(name = "icons", version = "", path = "icons/", targetPath = "/yt/icons")})
public class YoutubeIntegrationContentCreator implements WebApp {

	@Override
	public void createWar() {
		// Nothing needs to be done here
	}

	@Override
	public void init(WebAppDescriptor webAppDescriptor, WebEnvironment webEnvironment) {
		// Nothing needs to be done here
	}

	@Override
	public void installed() {
		// Nothing needs to be done here
	}

	@Override
	public void uninstalling() {
		// Nothing needs to be done here
	}

	@Override
	public void updated(String s) {
		// Nothing needs to be done here
	}
}
