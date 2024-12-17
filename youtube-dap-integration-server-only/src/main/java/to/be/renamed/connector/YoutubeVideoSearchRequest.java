package to.be.renamed.connector;

import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemSnippet;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;
import com.google.api.services.youtube.model.ThumbnailDetails;

import to.be.renamed.YoutubeVideo;

import java.io.IOException;
import java.util.List;

/**
 * Request to retrieve videos from youtube.
 */
public interface YoutubeVideoSearchRequest {

	/**
	 * Create youtube video object.
	 *
	 * @param searchResult the search result
	 * @return the youtube video
	 */
	static YoutubeVideo createYoutubeVideo(final SearchResult searchResult) {
		SearchResultSnippet searchResultSnippet = searchResult.getSnippet();
		ThumbnailDetails searchResultThumbnails = searchResultSnippet.getThumbnails();
		return new YoutubeVideo(searchResult.getId().getVideoId(),
								searchResultSnippet.getTitle(),
								searchResultSnippet.getDescription(),
								searchResultThumbnails.getDefault().getUrl(),
								searchResultThumbnails.getHigh().getUrl(),
								searchResultSnippet.getPublishedAt().toStringRfc3339());
	}

	/**
	 * Create youtube video object.
	 *
	 * @param playlistItem the video list result
	 * @return the youtube video
	 */
	static YoutubeVideo createYoutubeVideo(final PlaylistItem playlistItem) {
		PlaylistItemSnippet snippet = playlistItem.getSnippet();
		ThumbnailDetails thumbnails = snippet.getThumbnails();
		return new YoutubeVideo(playlistItem.getContentDetails().getVideoId(),
								snippet.getTitle(),
								snippet.getDescription(),
								thumbnails.getDefault().getUrl(),
								thumbnails.getHigh().getUrl(),
								snippet.getPublishedAt().toStringRfc3339());
	}

	/**
	 * Gets total.
	 *
	 * @return the total
	 */
	int getTotal();

	/**
	 * Search videos.
	 *
	 * @param count the count
	 * @return the videos
	 * @throws IOException the io exception
	 */
	List<YoutubeVideo> searchVideos(final int count);

	/**
	 * Has next.
	 *
	 * @return the boolean
	 */
	boolean hasNext();
}
