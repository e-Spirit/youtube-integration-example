package to.be.renamed.connector;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.ThumbnailDetails;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import to.be.renamed.YoutubeVideo;
import to.be.renamed.integration.YoutubeIntegrationConfig;

import de.espirit.common.base.Logging;
import de.espirit.common.tools.Strings;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

/**
 * Youtube base class supports core features, such as searching and retrieving videos.
 */
public class YoutubeConnector {

    private static final String APP_NAME = "FirstSpirit YouTube Integration";
    private static final Class<?> LOGGER = YoutubeConnector.class;
    private static final int MAXIMUM_CACHE_SIZE = 1000;
    private static final String REQUEST_NOT_INITIALIZED_ERROR = "SearchRequest not initialized, initialize first!";
    private final YouTube youtube;
    private final List<Channel> channels;
    private final String apiKey;
    private final int cacheDuration;
    private YoutubeVideoSearchRequest searchRequest;
    private final LoadingCache<Collection<String>, List<YoutubeVideo>> referencedVideos;

    /**
     * Instantiates a new Youtube connector.
     *
     * @param youtube  the youtube
     * @param channels the channels
     * @param apiKey   the api key
     * @param cacheDuration the cache duration
     */
    public YoutubeConnector(final YouTube youtube, final List<Channel> channels, final String apiKey, final int cacheDuration) {
        this.youtube = youtube;
        this.channels = new ArrayList<>(channels);
        this.apiKey = apiKey;
        this.cacheDuration = cacheDuration;
        referencedVideos = initCache();
    }

    public List<YoutubeVideo> getCachedVideos(Collection<String> videoIds) {
        try {
            Logging.logDebug("Trying to load videos from cache: " + String.join(",", videoIds), getClass());
            return referencedVideos.get(videoIds);
        } catch (ExecutionException e) {
            Logging.logError("Error accessing cache.", e, getClass());
            return Collections.emptyList();
        }
    }

    /**
     * Initializes the YoutubeVideoSearchRequest.
     * This must be called before using searchVideos, getTotal and hasNext
     * @param query The search query
     * @param channel The YouTube channel
     */
    public void initSearchRequest(@Nullable String query, @Nullable String channel) {
        searchRequest = getSearchRequest(query, channel);
    }

    public List<YoutubeVideo> searchVideos(int count) {
        if (searchRequest == null) {
            throw new YoutubeConnectorException(REQUEST_NOT_INITIALIZED_ERROR);
        }
        return searchRequest.searchVideos(count);
    }

    public int getTotal() {
        if (searchRequest == null) {
            throw new YoutubeConnectorException(REQUEST_NOT_INITIALIZED_ERROR);
        }
        return searchRequest.getTotal();
    }

    public boolean hasNext() {
        if (searchRequest == null) {
            throw new YoutubeConnectorException(REQUEST_NOT_INITIALIZED_ERROR);
        }
        return searchRequest.hasNext();
    }

    /**
     * Gets search request.
     *
     * @param query   the query
     * @param channel the channel
     * @return the search request
     */
    protected YoutubeVideoSearchRequest getSearchRequest(@Nullable String query, @Nullable String channel) {
        YoutubeVideoSearchRequest request = null;
        try {
            if (channels.isEmpty()) {
                // no channel configured
                request = YoutubeStandardVideoSearchRequest.createInstance(apiKey, youtube, query, null);
            } else {
                List<Channel> queryChannels = getQueryChannel(channel);
                if (Strings.isEmpty(query)) {
                    // No request term
                    request = YoutubeChannelVideosRequest.createInstance(apiKey, youtube, queryChannels);
                } else {
                    if (queryChannels.isEmpty()) {
                        request = YoutubeStandardVideoSearchRequest.createInstance(apiKey, youtube, query, null);
                    } else if (queryChannels.size() == 1) {
                        request = YoutubeStandardVideoSearchRequest.createInstance(apiKey, youtube, query, queryChannels.get(0));
                    } else {
                        request = YoutubeMultiChannelVideoSearchRequest.createInstance(apiKey, youtube, query, channels);
                    }
                }
            }
        } catch (IOException e) {
            Logging.logError("Unable to create Request", e, LOGGER);
        }
        return request;
    }

    /**
     * Provides a list of videos for the specified IDs.
     *
     * @param videoIds list of video ids
     * @return a list of youtube videos or an empty list
     */
    public List<YoutubeVideo> getVideos(Collection<String> videoIds) {
        List<YoutubeVideo> videos = new ArrayList<>();
        if (!videoIds.isEmpty()) {
            Logging.logDebug("Requesting videos for ids: " + String.join(",", videoIds), getClass());
            try {
                VideoListResponse response = youtube.videos()
                    .list(List.of("snippet"))
                    .setKey(apiKey)
                    .setId(List.of(Strings.implode(videoIds, ", ")))
                    .setMaxResults((long) videoIds.size())
                    .setFields("items(id,snippet(description,thumbnails/default/url,thumbnails/high/url,title,publishedAt))")
                    .execute();

                List<Video> videoList = response.getItems();
                // Iterate over original videoIds input, to ensure correct order and replace missing videos (e.g. deleted) with null
                for (String id : videoIds) {
                    Video matchingVideo = videoList.stream().filter(video -> video.getId().equals(id)).findFirst().orElse(null);
                    if (matchingVideo != null) {
                        VideoSnippet snippet = matchingVideo.getSnippet();
                        ThumbnailDetails thumbnails = snippet.getThumbnails();
                        videos.add(
                            new YoutubeVideo(matchingVideo.getId(), snippet.getTitle(), snippet.getDescription(), thumbnails.getDefault().getUrl(),
                                             thumbnails.getHigh().getUrl(), snippet.getPublishedAt().toStringRfc3339()));
                    } else {
                        Logging.logInfo("Video with id [" + id + "] missing!", LOGGER);
                        videos.add(null);
                    }
                }
            } catch (GoogleJsonResponseException e) {
                Logging.logError("Google API Error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage(), e, LOGGER);
            } catch (IOException e) {
                Logging.logError("IOException when retrieving a Youtube video.", e, LOGGER);
            }
        }
        return videos;
    }

    /**
     * Gets channels.
     *
     * @return the channels
     */
    public List<YoutubeChannelDTO> getChannels() {
        List<YoutubeChannelDTO> transferableChannels = new ArrayList<>();
        for (Channel channel : channels) {
            transferableChannels.add(new YoutubeChannelDTO(channel.getSnippet().getTitle(), channel.getId()));
        }

        return transferableChannels;
    }

    private List<Channel> getQueryChannel(final String channelId) {
        if (Strings.notEmpty(channelId) && !"all".equals(channelId) && channels.size() > 1) {
            for (final Channel channel : channels) {
                if (channel.getId().equals(channelId)) {
                    return Collections.singletonList(channel);
                }
            }
            return Collections.emptyList();
        }
        return new ArrayList<>(channels);
    }

    /**
     * Builder to create a new YouTubeConnector instance.
     */
    public static class Builder {

        private String apiKey;
        private List<String> channelIds;
        private int cacheDuration;

        /**
         * Set the apikey and channelids based on the YoutubeIntegrationConfig
         *
         * @param youtubeIntegrationConfig the youtube integration config
         * @return Builder builder
         */
        public Builder config(YoutubeIntegrationConfig youtubeIntegrationConfig) {
            apiKey = youtubeIntegrationConfig.getApiKey();
            channelIds = youtubeIntegrationConfig.getChannelIds();
            cacheDuration = youtubeIntegrationConfig.getVideoCacheDuration();
            return this;
        }

        /**
         * Apikey builder.
         *
         * @param apiKey the api key
         * @return the builder
         */
        public Builder apikey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        /**
         * Channels builder.
         *
         * @param channelIds the channel ids
         * @return the builder
         */
        public Builder channels(List<String> channelIds) {
            this.channelIds = new ArrayList<>(channelIds);
            return this;
        }

        /**
         *
         * @param cacheDuration The cache duration in minutes
         * @return the builder
         */
        public Builder cacheDuration(int cacheDuration) {
            this.cacheDuration = cacheDuration;
            return this;
        }

        /**
         * Build youtube connector.
         *
         * @return the youtube connector
         */
        public YoutubeConnector build() {
            if (Strings.notEmpty(apiKey)) {
                YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), request -> {
                }).setApplicationName(APP_NAME).build();
                List<Channel> youtubeChannels = new ArrayList<>();
                if (channelIds != null && !channelIds.isEmpty()) {
                    try {
                        youtubeChannels = getYoutubeChannels(youtube);
                    } catch (IOException e) {
                        Logging.logError("Youtube channel retrieval error", e, LOGGER);
                    }
                }
                return new YoutubeConnector(youtube, youtubeChannels, apiKey, cacheDuration);
            }
            return null;
        }

        /**
         * Helper method to verify the specified api key and channel ids.
         *
         * @throws IOException the io exception
         */
        public void checkSettings() throws IOException {
            if (Strings.isEmpty(apiKey)) {
                throw new IllegalArgumentException("YoutTube API KEY is missing");
            }
            YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), request -> {
            }).setApplicationName(APP_NAME).build();
            youtube.i18nLanguages()
                .list(List.of("snippet"))
                .setKey(apiKey)
                .setFields("etag")
                .execute();
            if (channelIds == null || channelIds.isEmpty()) {
                Logging.logInfo("No channels configured", LOGGER);
            } else {
                getYoutubeChannels(youtube);
            }
        }

        /**
         * Gets YouTube channels.
         *
         * @param youtube the youtube
         * @return the youtube channels
         * @throws IOException the io exception
         */
        List<Channel> getYoutubeChannels(@NotNull final YouTube youtube) throws IOException {
            List<Channel> result = new ArrayList<>();
            for (final String channelId : channelIds) {
                if (channelId.isBlank()) continue;
                ChannelListResponse channels = youtube.channels()
                    .list(List.of("snippet", "contentDetails"))
                    .setKey(apiKey)
                    .setId(List.of(channelId))
                    .execute();
                List<Channel> responseChannelList = channels.getItems();
                if (responseChannelList.size() == 1) {
                    Channel channel = responseChannelList.get(0);
                    if (!channelId.equals(channel.getId())) {
                        throw new IllegalArgumentException("Incomplete ChannelId, try: " + channel.getId());
                    } else {
                        result.add(channel);
                    }
                } else {
                    throw new IllegalArgumentException("Unknown ChannelId");
                }
            }
            return result;
        }

    }

    private LoadingCache<Collection<String>, List<YoutubeVideo>> initCache() {
        return CacheBuilder.newBuilder()
            .maximumSize(MAXIMUM_CACHE_SIZE)
            .expireAfterAccess(cacheDuration, TimeUnit.MINUTES)
            .build(
                new CacheLoader<Collection<String>, List<YoutubeVideo>>() {
                    @Override
                    public List<YoutubeVideo> load(final Collection<String> videoIds) throws Exception {
                        return getVideos(videoIds);
                    }
                }
            );
    }

    public boolean clearCache() {
        try {
            referencedVideos.invalidateAll();
            return true;
        } catch (Exception e) {
            Logging.logError("Could not clear cache.", e, getClass());
            return false;
        }
    }
}
