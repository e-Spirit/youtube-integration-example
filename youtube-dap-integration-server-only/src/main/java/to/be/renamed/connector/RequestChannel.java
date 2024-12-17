package to.be.renamed.connector;

import com.google.api.services.youtube.model.Channel;

public class RequestChannel {

	private final Channel _channel;
	private String _pageToken;
	private int _totalVideos;
	private int _consumedVideos;
	private boolean _consumed;

	/**
	 * Instantiates a new Request channel.
	 *
	 * @param channel the channel
	 */
	public RequestChannel(final Channel channel) {
		_channel = channel;
	}

	/**
	 * Gets channel.
	 *
	 * @return the channel
	 */
	public Channel getChannel() {
		return _channel;
	}

	/**
	 * Gets page token.
	 *
	 * @return the page token
	 */
	public String getPageToken() {
		return _pageToken;
	}

	/**
	 * Sets page token.
	 *
	 * @param pageToken the page token
	 */
	public void setPageToken(final String pageToken) {
		_pageToken = pageToken;
	}

	/**
	 * Gets total.
	 *
	 * @return the total
	 */
	public int getTotalVideos() {
		return _totalVideos;
	}

	/**
	 * Sets total.
	 *
	 * @param totalVideos the total
	 */
	public void setTotalVideos(final int totalVideos) {
		_totalVideos = totalVideos;
	}

	/**
	 * Gets consumed.
	 *
	 * @return the consumed
	 */
	public int getConsumed() {
		return _consumedVideos;
	}

	/**
	 * Sets consumed.
	 *
	 * @param consumedVideos the consumed
	 */
	public void setConsumedVideos(final int consumedVideos) {
		_consumedVideos = consumedVideos;
		if (_consumedVideos >= _totalVideos) {
			_consumed = true;
		}
	}

	public boolean isConsumed() {
		return _consumed;
	}

	public void setConsumed(final boolean consumed) {
		_consumed = consumed;
	}

	@Override
	public String toString() {
		return "RequestChannel{" +
				"_channel=" + _channel +
				", _pageToken='" + _pageToken + '\'' +
				", _total=" + _totalVideos +
				", _consumed=" + _consumedVideos +
				", isConsumed=" + _consumed +
				'}';
	}
}
