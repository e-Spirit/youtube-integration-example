package to.be.renamed.connector;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PageInfo;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;
import com.google.api.services.youtube.model.Thumbnail;
import com.google.api.services.youtube.model.ThumbnailDetails;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class YoutubeStandardVideoSearchRequestTest {

	@Mock
	YouTube.Search.List _youtubeRequestList;

	@Test
	void getTotal() throws IOException {
		long requestSize = 20;
		String pageToken = "pagetoken";
		int totalResults = 40;
		when(_youtubeRequestList.setMaxResults(requestSize)).thenReturn(_youtubeRequestList);

		SearchListResponse searchListResponseMock = mock(SearchListResponse.class);
		when(_youtubeRequestList.execute()).thenReturn(searchListResponseMock);

		when(searchListResponseMock.getNextPageToken()).thenReturn(pageToken);
		PageInfo pageInfoMock = mock(PageInfo.class);
		when(searchListResponseMock.getPageInfo()).thenReturn(pageInfoMock);
		when(pageInfoMock.getTotalResults()).thenReturn(totalResults);

		SearchResult searchResultMock = getSearchResultMock();
		when(searchListResponseMock.getItems()).thenReturn(Collections.singletonList(searchResultMock));

		YoutubeStandardVideoSearchRequest youtubeStandardVideoSearchRequest = new YoutubeStandardVideoSearchRequest(_youtubeRequestList);

		youtubeStandardVideoSearchRequest.searchVideos(20);

		assertEquals(totalResults, youtubeStandardVideoSearchRequest.getTotal());
	}

	@Test
	void hasNext_TRUE() throws IOException {
		long requestSize = 20;
		int totalResults = 40;
		when(_youtubeRequestList.setMaxResults(requestSize)).thenReturn(_youtubeRequestList);

		SearchListResponse searchListResponseMock = mock(SearchListResponse.class);
		when(_youtubeRequestList.execute()).thenReturn(searchListResponseMock);

		when(searchListResponseMock.getNextPageToken()).thenReturn("pageToken");
		PageInfo pageInfoMock = mock(PageInfo.class);
		when(searchListResponseMock.getPageInfo()).thenReturn(pageInfoMock);
		when(pageInfoMock.getTotalResults()).thenReturn(totalResults);

		SearchResult searchResultMock = getSearchResultMock();
		when(searchListResponseMock.getItems()).thenReturn(Collections.singletonList(searchResultMock));

		YoutubeStandardVideoSearchRequest youtubeStandardVideoSearchRequest = new YoutubeStandardVideoSearchRequest(_youtubeRequestList);

		youtubeStandardVideoSearchRequest.searchVideos(20);

		assertTrue(youtubeStandardVideoSearchRequest.hasNext());
	}

	@Test
	void hasNext_NOT() throws IOException {
		long requestSize = 20;
		int totalResults = 40;
		when(_youtubeRequestList.setMaxResults(requestSize)).thenReturn(_youtubeRequestList);

		SearchListResponse searchListResponseMock = mock(SearchListResponse.class);
		when(_youtubeRequestList.execute()).thenReturn(searchListResponseMock);

		when(searchListResponseMock.getNextPageToken()).thenReturn("");
		PageInfo pageInfoMock = mock(PageInfo.class);
		when(searchListResponseMock.getPageInfo()).thenReturn(pageInfoMock);
		when(pageInfoMock.getTotalResults()).thenReturn(totalResults);

		SearchResult searchResultMock = getSearchResultMock();
		when(searchListResponseMock.getItems()).thenReturn(Collections.singletonList(searchResultMock));

		YoutubeStandardVideoSearchRequest youtubeStandardVideoSearchRequest = new YoutubeStandardVideoSearchRequest(_youtubeRequestList);

		youtubeStandardVideoSearchRequest.searchVideos(20);

		assertFalse(youtubeStandardVideoSearchRequest.hasNext());
	}

	@Test
	void searchVideos_SET_PAGETOKEN() throws IOException {
		long requestSize = 20;
		String pageToken = "pagetoken";
		int totalResults = 40;
		when(_youtubeRequestList.setMaxResults(requestSize)).thenReturn(_youtubeRequestList);
		when(_youtubeRequestList.setPageToken(anyString())).thenReturn(_youtubeRequestList);

		SearchListResponse searchListResponseMock = mock(SearchListResponse.class);
		when(_youtubeRequestList.execute()).thenReturn(searchListResponseMock);

		when(searchListResponseMock.getNextPageToken()).thenReturn(pageToken);
		PageInfo pageInfoMock = mock(PageInfo.class);
		when(searchListResponseMock.getPageInfo()).thenReturn(pageInfoMock);
		when(pageInfoMock.getTotalResults()).thenReturn(totalResults);

		SearchResult searchResultMock = getSearchResultMock();
		when(searchListResponseMock.getItems()).thenReturn(Collections.singletonList(searchResultMock));

		YoutubeStandardVideoSearchRequest youtubeStandardVideoSearchRequest = new YoutubeStandardVideoSearchRequest(_youtubeRequestList);
		youtubeStandardVideoSearchRequest.searchVideos(20);
		youtubeStandardVideoSearchRequest.searchVideos(20);

		verify(_youtubeRequestList).setPageToken(pageToken);
	}

	private SearchResult getSearchResultMock() {
		SearchResult searchResultMock = mock(SearchResult.class);
		SearchResultSnippet searchResultSnippetMock = mock(SearchResultSnippet.class);
		when(searchResultSnippetMock.getTitle()).thenReturn("Video Title");
		when(searchResultSnippetMock.getDescription()).thenReturn("Video Description");
		DateTime publishedAtMock = DateTime.parseRfc3339("2002-10-02T15:00:00Z");
		when(searchResultSnippetMock.getPublishedAt()).thenReturn(publishedAtMock);


		ThumbnailDetails thumbnailDetailsMock = mock(ThumbnailDetails.class);
		Thumbnail thumbnailMock = mock(Thumbnail.class);
		when(thumbnailMock.getUrl()).thenReturn("http://thumbnail.url");
		when(thumbnailDetailsMock.getDefault()).thenReturn(thumbnailMock);
		when(thumbnailDetailsMock.getHigh()).thenReturn(thumbnailMock);
		when(searchResultSnippetMock.getThumbnails()).thenReturn(thumbnailDetailsMock);

		when(searchResultMock.getSnippet()).thenReturn(searchResultSnippetMock);

		ResourceId resourceIdMock = mock(ResourceId.class);
		when(resourceIdMock.getVideoId()).thenReturn("videoId");
		when(searchResultMock.getId()).thenReturn(resourceIdMock);

		return searchResultMock;
	}
}
