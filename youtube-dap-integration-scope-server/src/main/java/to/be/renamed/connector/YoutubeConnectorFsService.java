package to.be.renamed.connector;


import to.be.renamed.YoutubeVideo;
import to.be.renamed.integration.YoutubeIntegrationConfig;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public interface YoutubeConnectorFsService {
    void initSearchRequest(long projectId, @Nullable String query, @Nullable String channel);
    List<YoutubeVideo> searchVideos(long projectId, int count);
    int getTotal(long projectId);
    boolean hasNext(long projectId);
    List<YoutubeVideo> getVideos(long projectId, Collection<String> videoIds);
    List<YoutubeChannelDTO> getChannels(long projectId);
    void checkSettings(YoutubeIntegrationConfig configuration) throws IOException;
    boolean clearCache(long projectId);
    void removeConnector(long projectId);
}
