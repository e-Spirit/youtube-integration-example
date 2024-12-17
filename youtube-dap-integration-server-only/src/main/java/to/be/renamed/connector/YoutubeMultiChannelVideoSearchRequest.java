package to.be.renamed.connector;

import de.espirit.common.base.Logging;
import de.espirit.common.tools.Strings;
import to.be.renamed.YoutubeVideo;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Request to retrieve video from more than one channel.
 */
public class YoutubeMultiChannelVideoSearchRequest implements YoutubeVideoSearchRequest {

	private static final Class<?> LOGGER = YoutubeMultiChannelVideoSearchRequest.class;
	private final YouTube.Search.List _youtubeRequestList;
	private final List<RequestChannel> _channels;
	private int _total = -1;
	private boolean _hasNext = true;

	/**
	 * Instantiates a new Youtube multi channel video search request.
	 *
	 * @param youtubeRequestList the youtube request list
	 * @param channels           the channels
	 */
	public YoutubeMultiChannelVideoSearchRequest(final YouTube.Search.List youtubeRequestList, final List<RequestChannel> channels) {
		_youtubeRequestList = youtubeRequestList;
		_channels = new ArrayList<>(channels);
	}

	/**
	 * Create instance youtube multi channel video search request.
	 *
	 * @param apiKey   the api key
	 * @param youtube  the youtube
	 * @param query    the query
	 * @param channels the channels
	 * @return the youtube multi channel video search request
	 * @throws IOException the io exception
	 */
	public static YoutubeMultiChannelVideoSearchRequest createInstance(final String apiKey, final YouTube youtube, final String query, final List<Channel> channels) throws IOException {
		Logging.logInfo(String.format("Create new request with query: '%s' and channel(s) '%s'", query,
									  channels.stream().map(channel -> channel.getSnippet().getTitle()).collect(Collectors.joining(","))), LOGGER);
		YouTube.Search.List youtubeRequestList = youtube.search()
				.list(List.of("snippet"))
				.setKey(apiKey)
				.setType(List.of("video"))
				.setFields("items(id/videoId,snippet(description,thumbnails/default/url,thumbnails/high/url,title,publishedAt)),nextPageToken,pageInfo");
		if (Strings.notEmpty(query)) {
			youtubeRequestList.setQ(query);
		}
		List<RequestChannel> requestChannelList = channels.stream().map(RequestChannel::new).collect(Collectors.toList());
		return new YoutubeMultiChannelVideoSearchRequest(youtubeRequestList, requestChannelList);
	}

	@Override
	public int getTotal() {
		return _total;
	}

	private void setTotal() {
		if (_total < 0) {
			_total = _channels.stream().map(RequestChannel::getTotalVideos).reduce(0, Integer::sum);
		}
	}

	/**
	 * Supplied a list of Youtube videos.
	 *
	 * @param count the number of videos to retrieve.
	 * @return a list of youtube videos
	 */
	@Override
	public List<YoutubeVideo> searchVideos(final int count) {
		List<YoutubeVideo> resultList = new ArrayList<>();
		while (resultList.size() < count && _hasNext) {
			int requestSize = count - resultList.size();
			// All channels that still provide videos
			List<RequestChannel> requestChannelList = _channels.stream().filter(Predicate.not(RequestChannel::isConsumed)).collect(Collectors.toList());
			// How many videos to request per channel
			int channelRequestSize = requestSize / requestChannelList.size();
			int channelRequestSizeFactor = requestSize % requestChannelList.size();
			Iterator<RequestChannel> iterator = requestChannelList.iterator();
			while (iterator.hasNext()) {
				final RequestChannel requestChannel = iterator.next();
				int currentRequestSize = channelRequestSize;
				if (channelRequestSizeFactor > 0) {
					currentRequestSize++;
					channelRequestSizeFactor--;
				}
				try {
					resultList.addAll(executeYoutubeVideosRequest(requestChannel, currentRequestSize));
				} catch (IOException e) {
					Logging.logError("Error requesting videos", e, LOGGER);
					_hasNext = false;
					return resultList;
				}
			}
			_hasNext = _channels.stream().anyMatch(Predicate.not(RequestChannel::isConsumed));
		}
		setTotal();
		return resultList;
	}

	@Nullable
	private List<YoutubeVideo> executeYoutubeVideosRequest(final RequestChannel requestChannel, final int currentRequestSize) throws IOException {
		_youtubeRequestList.setMaxResults(Long.valueOf(currentRequestSize));
		if (Strings.notEmpty(requestChannel.getPageToken())) {
			_youtubeRequestList.setPageToken(requestChannel.getPageToken());
		}
		_youtubeRequestList.setChannelId(requestChannel.getChannel().getId());
		SearchListResponse searchListResponse = _youtubeRequestList.execute();
		requestChannel.setPageToken(searchListResponse.getNextPageToken());
		requestChannel.setTotalVideos(searchListResponse.getPageInfo().getTotalResults());
		List<SearchResult> searchResults = searchListResponse.getItems();
		if (searchResults.isEmpty()) {
			requestChannel.setConsumed(true);
		} else {
			requestChannel.setConsumedVideos(requestChannel.getConsumed() + searchResults.size());
		}
		Logging.logTrace(requestChannel.toString(), LOGGER);
		return searchResults.stream().map(YoutubeVideoSearchRequest::createYoutubeVideo).collect(Collectors.toList());
	}

	@Override
	public boolean hasNext() {
		return _hasNext;
	}

}
