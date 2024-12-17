package to.be.renamed.connector;

import de.espirit.common.base.Logging;
import de.espirit.common.tools.Strings;
import to.be.renamed.YoutubeVideo;


import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.SearchListResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Request to retrieve videos from a channel or all YouTube videos.
 */
public class YoutubeStandardVideoSearchRequest implements YoutubeVideoSearchRequest {

	private static final Class<?> LOGGER = YoutubeStandardVideoSearchRequest.class;
	private final YouTube.Search.List _youtubeRequestList;
	private String _youtubePageToken;
	private int _total = -1;
	private boolean _hasNext = true;

	/**
	 * Instantiates a new Youtube standard video search request.
	 *
	 * @param youtubeRequestList the youtube request list
	 */
	public YoutubeStandardVideoSearchRequest(final YouTube.Search.List youtubeRequestList) {
		_youtubeRequestList = youtubeRequestList;
	}

	/**
	 * Create instance youtube standard video search request.
	 *
	 * @param apiKey  the api key
	 * @param youtube the youtube
	 * @param query   the query
	 * @param channel the channel
	 * @return the youtube standard video search request
	 * @throws IOException the io exception
	 */
	public static YoutubeStandardVideoSearchRequest createInstance(final String apiKey, final YouTube youtube, final String query, final Channel channel) throws IOException {
		Logging.logInfo(String.format("Create new request with query: '%s' and channel '%s'", query, channel), LOGGER);
		YouTube.Search.List youtubeRequestList = youtube.search()
				.list(List.of("snippet"))
				.setKey(apiKey)
				.setType(List.of("video"))
				.setFields("items(id/videoId,snippet(description,thumbnails/default/url,thumbnails/high/url,title,publishedAt)),nextPageToken,pageInfo");
		if (Strings.notEmpty(query)) {
			youtubeRequestList.setQ(query);
		}
		if (channel != null) {
			Logging.logDebug(String.format("Set channel: '%s'[%s]", channel.getSnippet().getTitle(), channel.getId()), LOGGER);
			youtubeRequestList.setChannelId(channel.getId());
		}
		return new YoutubeStandardVideoSearchRequest(youtubeRequestList);
	}

	@Override
	public int getTotal() {
		return _total;
	}

	@Override
	public List<YoutubeVideo> searchVideos(final int count) {
		_youtubeRequestList.setMaxResults((long) count);
		if (Strings.notEmpty(_youtubePageToken)) {
			_youtubeRequestList.setPageToken(_youtubePageToken);
		}
		try {
			SearchListResponse searchListResponse = _youtubeRequestList.execute();
			_youtubePageToken = searchListResponse.getNextPageToken();
			_total = searchListResponse.getPageInfo().getTotalResults();
			if (Strings.isEmpty(_youtubePageToken)) {
				_hasNext = false;
			}
			return searchListResponse.getItems().stream().map(YoutubeVideoSearchRequest::createYoutubeVideo).collect(Collectors.toList());
		} catch (IOException e) {
			Logging.logError("Error requesting videos", e, LOGGER);
			_hasNext = false;
		}
		return Collections.emptyList();
	}

	@Override
	public boolean hasNext() {
		return _hasNext;
	}
}
