package to.be.renamed.connector;


import de.espirit.firstspirit.access.ServicesBroker;
import de.espirit.firstspirit.agency.ProjectAgent;
import de.espirit.firstspirit.agency.SpecialistsBroker;
import to.be.renamed.YoutubeVideo;
import to.be.renamed.integration.YoutubeIntegrationConfig;
import to.be.renamed.projectconfig.access.ProjectAppConfigurationServiceFacade;

import org.jetbrains.annotations.Nullable;


import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class YoutubeConnectorServiceFacade {
    private final YoutubeConnectorFsService connectorFsService;
    private final long projectId;

    public YoutubeConnectorServiceFacade(SpecialistsBroker broker) {
        connectorFsService = broker.requireSpecialist(ServicesBroker.TYPE).getService(YoutubeConnectorFsService.class);
        projectId = broker.requireSpecialist(ProjectAgent.TYPE).getId();
        ProjectAppConfigurationServiceFacade projectAppConfigurationServiceFacade = new ProjectAppConfigurationServiceFacade(broker, projectId);
    }

    public void initSearchRequest(@Nullable final String query,
                                                      @Nullable final String channel) {
        connectorFsService.initSearchRequest(projectId, query, channel);
    }

    public List<YoutubeVideo> searchVideos(int count) {
        return connectorFsService.searchVideos(projectId, count);
    }
    public int getTotal() {
        return connectorFsService.getTotal(projectId);
    }
    public boolean hasNext() {
        return connectorFsService.hasNext(projectId);
    }

    public List<YoutubeVideo> getVideos(final Collection<String> videoIds) {
        return connectorFsService.getVideos(projectId, videoIds);
    }

    public List<YoutubeChannelDTO> getChannels() {
        return connectorFsService.getChannels(projectId);
    }

    public void checkSettings(YoutubeIntegrationConfig configuration) throws IOException {
        connectorFsService.checkSettings(configuration);
    }

    public boolean clearCache(long projectId) {
        return connectorFsService.clearCache(projectId);
    }

    public void removeConnector() {
        connectorFsService.removeConnector(projectId);
    }
}
