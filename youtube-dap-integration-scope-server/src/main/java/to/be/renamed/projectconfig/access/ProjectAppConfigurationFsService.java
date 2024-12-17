package to.be.renamed.projectconfig.access;


import com.espirit.moddev.fcaf.projectapp.ProjectAppConfigurationAccessor;

import to.be.renamed.integration.YoutubeIntegrationConfig;

/**
 * This is just a helper interface, because in the service implementation we can not return the class of the generic type.
 */
public interface ProjectAppConfigurationFsService extends ProjectAppConfigurationAccessor<YoutubeIntegrationConfig> {

}
