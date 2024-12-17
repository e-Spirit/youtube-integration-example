package to.be.renamed;

import java.io.Serializable;
import java.util.Objects;

/**
 * Youtube Video Data Object
 * This data object carries information about a single Youtube video and is used by the YoutubeVideoDataAccessPlugin.
 *
 * @see to.be.renamed.dataaccess.YoutubeVideoDataAccessPlugin
 * @see to.be.renamed.dataaccess.YoutubeVideoDataAccessSession
 * @see to.be.renamed.dataaccess.YoutubeVideoDataStream
 * @see to.be.renamed.dataaccess.YoutubeVideoSearchRequest
 */
public class YoutubeVideo implements Serializable {

	private static final long serialVersionUID = -7599323134744218333L;
	private final String _id;
	private final String _title;
	private final String _description;
	private final String _thumbnailUrl;
	private final String _posterUrl;
	private final String _publishedAt;

	/**
	 * Instantiates a new Youtube video data object.
	 *
	 * @param id           the ID that YouTube uses to uniquely identify the video
	 * @param title        the video's title
	 * @param description  the video's description
	 * @param thumbnailUrl the thumbnail url for this video
	 * @param posterUrl    the high quality image for this video
	 * @param publishedAt  the video's date and time of publication
	 */
	public YoutubeVideo(String id, String title, String description, String thumbnailUrl, String posterUrl, String publishedAt) {
		_id = id;
		_title = title;
		_description = description;
		_thumbnailUrl = thumbnailUrl;
		_posterUrl = posterUrl;
		_publishedAt = publishedAt;
	}

	/**
	 * Gets ID that YouTube uses to uniquely identify the video.
	 *
	 * @return the id
	 */
	public String getId() {
		return _id;
	}

	/**
	 * Gets video's title.
	 *
	 * @return the title
	 */
	public String getTitle() {
		return _title;
	}

	/**
	 * Gets video's description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return _description;
	}

	/**
	 * Gets thumbnail url for this video.
	 *
	 * @return the thumbnail url
	 */
	public String getThumbnailUrl() {
		return _thumbnailUrl;
	}

	/**
	 * Gets high quality image for this video.
	 *
	 * @return the poster url
	 */
	public String getPosterUrl() {
		return _posterUrl;
	}

	/**
	 * Returns the publish Date as RFC 3339 formatted String, e.g. '2002-10-02T15:00:00Z'
	 *
	 * @return RFC 3339 formatted Date as String
	 */
	public String getPublishedAt() {
		return _publishedAt;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if ((o == null || this.getClass() != o.getClass())) {
			return false;
		}

		final YoutubeVideo that = (YoutubeVideo) o;

		if (!_id.equals(that._id)) {
			return false;
		}
		if (!Objects.equals(_title, that._title)) {
			return false;
		}
		if (!Objects.equals(_description, that._description)) {
			return false;
		}
		if (!Objects.equals(_thumbnailUrl, that._thumbnailUrl)) {
			return false;
		}
		if (!Objects.equals(_posterUrl, that._posterUrl)) {
			return false;
		}
            return Objects.equals(_publishedAt, that._publishedAt);
        }

	@Override
	public int hashCode() {
		int result = _id.hashCode();
		result = 31 * result + (_title != null ? _title.hashCode() : 0);
		result = 31 * result + (_description != null ? _description.hashCode() : 0);
		result = 31 * result + (_thumbnailUrl != null ? _thumbnailUrl.hashCode() : 0);
		result = 31 * result + (_posterUrl != null ? _posterUrl.hashCode() : 0);
		result = 31 * result + (_publishedAt != null ? _publishedAt.hashCode() : 0);
		return result;
	}
}
