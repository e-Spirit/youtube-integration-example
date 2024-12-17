package to.be.renamed.integration;

import de.espirit.common.base.Logging;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

/**
 * Class to outsource configurations
 */
public class YoutubeIntegrationConfig implements Serializable {

    private static final long serialVersionUID = 320693456205059647L;
    private static final int DEFAULT_CACHE_DURATION = 0;
    private final String apiKey;
    private final List<String> channelIds;
    private final int videoCacheDuration;

    /**
     * Initialize Configuration
     *
     * @param apiKey The api keys
     * @param channelIds The channel ids
     * @param videoCacheDuration The cache duration
     */
    public YoutubeIntegrationConfig(final String apiKey, final List<String> channelIds, final int videoCacheDuration) {
        this.apiKey = apiKey;
        this.channelIds = new ArrayList<>(channelIds);
        this.videoCacheDuration = videoCacheDuration;
    }

    public YoutubeIntegrationConfig() {
        this ("", new ArrayList<>(), 0);
    }

    public static YoutubeIntegrationConfig fromStrings(final String apiKey, final List<String> channelIds, final String videoCacheDuration) {
        int videCacheDuration = DEFAULT_CACHE_DURATION;

        try {
            videCacheDuration = parseInt(videoCacheDuration);
        } catch (NumberFormatException nfe) {
            Logging.logWarning("Unable to parse configured cache duration. Using default value '" + DEFAULT_CACHE_DURATION + "'.",
                               nfe, YoutubeIntegrationConfig.class);
        }
        return new YoutubeIntegrationConfig(apiKey, channelIds, videCacheDuration);
    }

    /**
     * @return Google API key
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * @return list of YouTube channel ids
     */
    public List<String> getChannelIds() {
        return new ArrayList<>(channelIds);
    }

    public String getChannelIdsAsString() {
	return String.join(",", channelIds);
    }

    public int getVideoCacheDuration() {
        return videoCacheDuration;
    }

    public String getVideoCacheDurationAsString() {
        return String.valueOf(videoCacheDuration);
    }
}
