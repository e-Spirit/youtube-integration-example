package to.be.renamed.connector;

import com.espirit.moddev.components.annotations.ServiceComponent;

import de.espirit.common.base.Logging;
import de.espirit.firstspirit.agency.SpecialistsBroker;
import de.espirit.firstspirit.module.ServerEnvironment;
import de.espirit.firstspirit.module.Service;
import de.espirit.firstspirit.module.ServiceProxy;
import de.espirit.firstspirit.module.descriptor.ServiceDescriptor;

import to.be.renamed.YoutubeVideo;
import to.be.renamed.integration.YoutubeIntegrationConfig;
import to.be.renamed.projectconfig.access.ProjectAppConfigurationServiceFacade;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static to.be.renamed.connector.YoutubeConnectorFsServiceImpl.SERVICE_NAME;

@ServiceComponent(name = SERVICE_NAME, displayName = "YouTube Connector Service")
public class YoutubeConnectorFsServiceImpl implements YoutubeConnectorFsService, Service<YoutubeConnectorFsService> {

    protected static final String SERVICE_NAME = "YoutubeConnectorService";
    private static final Map<Long, YoutubeConnector> connectors = new HashMap<>();
    private boolean running;
    private SpecialistsBroker broker;

    // Needed for injecting a mock YoutubeConnector while testing
    protected final void addYoutubeConnector(long projectId, YoutubeConnector youtubeConnector) {
        connectors.put(projectId, youtubeConnector);
    }

    private YoutubeConnector getYoutubeConnectorInstance(long projectId) {
        try {
            connectors.computeIfAbsent(projectId, pId -> {
                Logging.logDebug(String.format("Creating Youtube Connector for project '%s'", projectId), getClass());
                ProjectAppConfigurationServiceFacade
                    projectAppConfigurationServiceFacade =
                    new ProjectAppConfigurationServiceFacade(broker, projectId);
                YoutubeIntegrationConfig configuration = projectAppConfigurationServiceFacade.loadConfiguration();
                return connectors.put(pId, new YoutubeConnector.Builder().apikey(configuration.getApiKey()).channels(configuration.getChannelIds())
                    .cacheDuration(configuration.getVideoCacheDuration())
                    .build());
            });
        } catch (ConcurrentModificationException e) {
            Logging.logDebug(String.format("%s %s Map already being computed, trying again.", e.getClass().getName(), e.getMessage()), getClass());
            return getYoutubeConnectorInstance(projectId);
        }

        return connectors.get(projectId);
    }

    @Override
    public void initSearchRequest(long projectId, @Nullable final String query, @Nullable final String channel) {
        getYoutubeConnectorInstance(projectId).initSearchRequest(query, channel);
    }

    @Override
    public List<YoutubeVideo> searchVideos(long projectId, final int count) {
        return getYoutubeConnectorInstance(projectId).searchVideos(count);
    }

    @Override
    public int getTotal(long projectId) {
        return getYoutubeConnectorInstance(projectId).getTotal();
    }

    @Override
    public boolean hasNext(long projectId) {
        return getYoutubeConnectorInstance(projectId).hasNext();
    }

    @Override
    public List<YoutubeVideo> getVideos(long projectId, final Collection<String> videoIds) {
        YoutubeConnector youtubeConnector = getYoutubeConnectorInstance(projectId);
        if (youtubeConnector == null) {
            return null;
        }
        return youtubeConnector.getCachedVideos(videoIds);
    }

    @Override
    public List<YoutubeChannelDTO> getChannels(long projectId) {
        YoutubeConnector youtubeConnector = getYoutubeConnectorInstance(projectId);
        if (youtubeConnector == null) {
            return null;
        }
        return youtubeConnector.getChannels();
    }

    @Override
    public void checkSettings(YoutubeIntegrationConfig configuration) throws IOException {
        new YoutubeConnector.Builder().apikey(configuration.getApiKey()).channels(configuration.getChannelIds()).checkSettings();
    }

    @Override
    public boolean clearCache(long projectId) {
        Logging.logDebug("Clearing cache", getClass());
        return getYoutubeConnectorInstance(projectId).clearCache();
    }

    @Override
    public void removeConnector(long projectId) {
        Logging.logDebug("Removing connector: " + projectId, YoutubeConnectorFsServiceImpl.class);
        connectors.remove(projectId);
    }

    @Override
    public void start() {
        running = true;
        Logging.logInfo(String.format("Started %s", SERVICE_NAME), getClass());
    }

    @Override
    public void stop() {
        connectors.clear();
        running = false;
        Logging.logInfo(String.format("Stopped %s", SERVICE_NAME), getClass());
    }

    @Override
    public boolean isRunning() {
        Logging.logInfo(running ? String.format("%s is running", SERVICE_NAME) : String.format("%s is stopped", SERVICE_NAME), getClass());
        return running;
    }

    @Override
    public @Nullable Class<? extends YoutubeConnectorFsService> getServiceInterface() {
        return YoutubeConnectorFsService.class;
    }

    // currently no proxy is implemented
    @Override
    public @Nullable Class<? extends ServiceProxy<YoutubeConnectorFsService>> getProxyClass() {
        return null;
    }

    @Override
    public void init(final ServiceDescriptor serviceDescriptor, final ServerEnvironment serverEnvironment) {
        broker = serverEnvironment.getBroker();
        Logging.logInfo(String.format("Initialized %s", SERVICE_NAME), getClass());
    }

    @Override
    public void installed() {
        Logging.logInfo(String.format("Installed %s", SERVICE_NAME), getClass());
    }

    @Override
    public void uninstalling() {
        connectors.clear();
        Logging.logInfo(String.format("Uninstalled %s", SERVICE_NAME), getClass());
    }

    @Override
    public void updated(final String s) {
        connectors.clear();
        Logging.logInfo(String.format("Updated %s", SERVICE_NAME), getClass());
    }
}
