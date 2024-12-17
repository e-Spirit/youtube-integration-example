package to.be.renamed.connector;

import de.espirit.common.base.Logging;
import de.espirit.common.tools.Strings;
import to.be.renamed.YoutubeVideo;


import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class YoutubeChannelVideosRequest implements YoutubeVideoSearchRequest {

	private static final Class<?> LOGGER = YoutubeChannelVideosRequest.class;
	private final YouTube.PlaylistItems.List _playList;
	private int _total = -1;
	private boolean _hasNext = true;
	private List<RequestChannel> _channels;

	/**
	 * @param playList the youtube request list
	 * @param channels list of channels
	 */
	public YoutubeChannelVideosRequest(final YouTube.PlaylistItems.List playList, final List<RequestChannel> channels) {
		_playList = playList;
		_channels = new ArrayList<>(channels);
	}

	/**
	 * Create instance youtube list video request.
	 *
	 * @param apiKey   the api key
	 * @param youtube  the youtube
	 * @param channels list of channels
	 * @return the youtube standard video search request
	 * @throws IOException the io exception
	 */
	public static YoutubeChannelVideosRequest createInstance(final String apiKey, final YouTube youtube, final List<Channel> channels) throws IOException {
		Logging.logInfo(String.format("List videos for channel(s) '%s'", channels.stream().map(channel -> channel.getSnippet().getTitle()).collect(Collectors.joining(","))), LOGGER);
		YouTube.PlaylistItems.List playList = youtube.playlistItems()
				.list(List.of("snippet", "contentDetails"))
				.setKey(apiKey);
		List<RequestChannel> requestChannelList = channels.stream().map(RequestChannel::new).collect(Collectors.toList());
		return new YoutubeChannelVideosRequest(playList, requestChannelList);
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

	private List<YoutubeVideo> executeYoutubeVideosRequest(final RequestChannel requestChannel, final int currentRequestSize) throws IOException {
		_playList.setMaxResults(Long.valueOf(currentRequestSize));
		if (Strings.notEmpty(requestChannel.getPageToken())) {
			_playList.setPageToken(requestChannel.getPageToken());
		}
		_playList.setPlaylistId(requestChannel.getChannel().getContentDetails().getRelatedPlaylists().getUploads());
		PlaylistItemListResponse channelListResponse = _playList.execute();
		requestChannel.setPageToken(channelListResponse.getNextPageToken());
		requestChannel.setTotalVideos(channelListResponse.getPageInfo().getTotalResults());
		List<PlaylistItem> searchResults = channelListResponse.getItems();
		if (searchResults.isEmpty()) {
			requestChannel.setConsumed(true);
		} else {
			requestChannel.setConsumedVideos(requestChannel.getConsumed() + searchResults.size());
		}
		Logging.logDebug(requestChannel.toString(), LOGGER);
		return searchResults.stream().map(YoutubeVideoSearchRequest::createYoutubeVideo).collect(Collectors.toList());
	}

	@Override
	public boolean hasNext() {
		return _hasNext;
	}
}
