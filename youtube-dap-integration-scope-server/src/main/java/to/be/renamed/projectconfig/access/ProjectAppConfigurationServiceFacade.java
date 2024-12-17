package to.be.renamed.projectconfig.access;

import de.espirit.firstspirit.access.ServicesBroker;
import de.espirit.firstspirit.agency.SpecialistsBroker;
import to.be.renamed.integration.YoutubeIntegrationConfig;

public class ProjectAppConfigurationServiceFacade {

    private final ProjectAppConfigurationFsService projectAppConfigurationAccessor;
    private final long projectId;

    public ProjectAppConfigurationServiceFacade(final SpecialistsBroker specialistsBroker, final long projectId) {
        this.projectId = projectId;
        final ServicesBroker servicesBroker = specialistsBroker.requireSpecialist(ServicesBroker.TYPE);
        projectAppConfigurationAccessor = servicesBroker.getService(ProjectAppConfigurationFsService.class);
    }

    public YoutubeIntegrationConfig loadConfiguration() {
        return projectAppConfigurationAccessor.loadConfiguration(projectId, YoutubeIntegrationConfig.class);
    }

    public void storeConfiguration(YoutubeIntegrationConfig youtubeIntegrationConfig) {
        projectAppConfigurationAccessor.storeConfiguration(projectId, youtubeIntegrationConfig);
    }

    public long getProjectId() {
        return projectId;
    }
}
