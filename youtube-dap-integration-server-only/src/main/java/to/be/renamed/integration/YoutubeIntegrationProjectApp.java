package to.be.renamed.integration;

import de.espirit.firstspirit.module.ProjectApp;
import de.espirit.firstspirit.module.ProjectEnvironment;
import de.espirit.firstspirit.module.descriptor.ProjectAppDescriptor;

import com.espirit.moddev.components.annotations.ProjectAppComponent;

import static to.be.renamed.integration.ProjectAppHelper.PROJECT_APP_DISPLAY_NAME;
import static to.be.renamed.integration.ProjectAppHelper.PROJECT_APP_NAME;

/**
 * The FirstSpirit Youtube integration project app.
 * Can be added to a FirstSpirit project to enable and configure Youtube integration.
 */
@ProjectAppComponent(name = PROJECT_APP_NAME,
		displayName = PROJECT_APP_DISPLAY_NAME,
		description = "Project application to configure the Youtube integration module.",
		configurable = YoutubeIntegrationProjectConfig.class)
public class YoutubeIntegrationProjectApp implements ProjectApp {

	@Override
	public void init(ProjectAppDescriptor descriptor, ProjectEnvironment environment) {
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
	public void updated(String oldVersionString) {
		// Nothing needs to be done here
	}
}
