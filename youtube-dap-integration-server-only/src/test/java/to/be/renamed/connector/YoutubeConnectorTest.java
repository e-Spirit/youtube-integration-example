package to.be.renamed.connector;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelSnippet;

import to.be.renamed.YoutubeVideo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class YoutubeConnectorTest {

	public static final String APIKEY = "apikey";
	public static final String CHANNEL_1 = "Channel 1";
        public static final int NO_CACHE = 0;

	static YouTube _youTubeMock;

	@BeforeAll
	static void beforeAll() throws IOException {
		_youTubeMock = mock(YouTube.class);

		// For search requests
		YouTube.Search searchMock = mock(YouTube.Search.class);
		when(_youTubeMock.search()).thenReturn(searchMock);
		YouTube.Search.List searchListMock = mock(YouTube.Search.List.class);
		when(searchMock.list(any())).thenReturn(searchListMock);

		when(searchListMock.setKey(anyString())).thenReturn(searchListMock);
		when(searchListMock.setType(anyList())).thenReturn(searchListMock);
		when(searchListMock.setFields(anyString())).thenReturn(searchListMock);



		// For list requests
		YouTube.PlaylistItems playlistItemsMock = mock(YouTube.PlaylistItems.class);
		when(_youTubeMock.playlistItems()).thenReturn(playlistItemsMock);
		YouTube.PlaylistItems.List playlistItemsListMock = mock(YouTube.PlaylistItems.List.class);
		when(playlistItemsMock.list(any())).thenReturn(playlistItemsListMock);

		when(playlistItemsListMock.setKey(anyString())).thenReturn(playlistItemsListMock);
		when(playlistItemsListMock.setFields(anyString())).thenReturn(playlistItemsListMock);
	}

	@Test
	void getSearchRequest_NO_CHANNEL() {
		YoutubeConnector youtubeConnector = new YoutubeConnector(_youTubeMock, Collections.emptyList(), APIKEY, NO_CACHE);

		YoutubeVideoSearchRequest searchRequest = youtubeConnector.getSearchRequest("searchPattern", "");

		assertNotNull(searchRequest);
		assertTrue(searchRequest instanceof YoutubeStandardVideoSearchRequest);
	}

	@Test
	void getSearchRequest_ONE_CHANNEL() {
		Channel channelMock = mock(Channel.class);
		when(channelMock.getId()).thenReturn(CHANNEL_1);
		ChannelSnippet channelSnippetMock = mock(ChannelSnippet.class);
		when(channelMock.getSnippet()).thenReturn(channelSnippetMock);
		when(channelSnippetMock.getTitle()).thenReturn("Channel Title");

		YoutubeConnector youtubeConnector = new YoutubeConnector(_youTubeMock, Collections.singletonList(channelMock), APIKEY, NO_CACHE);
		YoutubeVideoSearchRequest searchRequest = youtubeConnector.getSearchRequest("searchPattern", "");

		assertNotNull(searchRequest);
		assertTrue(searchRequest instanceof YoutubeStandardVideoSearchRequest);
	}

	@Test
	void getSearchRequest_SEARCH_SINGLE_CHANNEL() {
		Channel channelMock = mock(Channel.class);
		when(channelMock.getId()).thenReturn(CHANNEL_1);
		ChannelSnippet channelSnippetMock = mock(ChannelSnippet.class);
		when(channelMock.getSnippet()).thenReturn(channelSnippetMock);
		when(channelSnippetMock.getTitle()).thenReturn("Channel Title");

		YoutubeConnector youtubeConnector = new YoutubeConnector(_youTubeMock, Collections.singletonList(channelMock), APIKEY, NO_CACHE);
		YoutubeVideoSearchRequest searchRequest = youtubeConnector.getSearchRequest("searchPattern", CHANNEL_1);

		assertNotNull(searchRequest);
		assertTrue(searchRequest instanceof YoutubeStandardVideoSearchRequest);
	}

	@Test
	void getSearchRequest_SEARCH_IN_ONE_CHANNEL() {
		Channel channelMock = mock(Channel.class);
		when(channelMock.getId()).thenReturn(CHANNEL_1);
		ChannelSnippet channelSnippetMock = mock(ChannelSnippet.class);
		when(channelMock.getSnippet()).thenReturn(channelSnippetMock);
		when(channelSnippetMock.getTitle()).thenReturn("Channel Title");

		YoutubeConnector youtubeConnector = new YoutubeConnector(_youTubeMock, Arrays.asList(channelMock, channelMock), APIKEY, NO_CACHE);
		YoutubeVideoSearchRequest searchRequest = youtubeConnector.getSearchRequest("searchPattern", CHANNEL_1);

		assertNotNull(searchRequest);
		assertTrue(searchRequest instanceof YoutubeStandardVideoSearchRequest);
	}

	@Test
	void getSearchRequest_SEARCH_IN_UNKNOWN_CHANNEL() {
		Channel channelMock = mock(Channel.class);
		when(channelMock.getId()).thenReturn("Unknown channel");

		YoutubeConnector youtubeConnector = new YoutubeConnector(_youTubeMock, Arrays.asList(channelMock, channelMock), APIKEY, NO_CACHE);
		YoutubeVideoSearchRequest searchRequest = youtubeConnector.getSearchRequest("searchPattern", CHANNEL_1);

		assertNotNull(searchRequest);
		assertTrue(searchRequest instanceof YoutubeStandardVideoSearchRequest);
	}

	@Test

	void getSearchRequest_SEARCH_IN_ALL_CHANNELS() {
		Channel channelMock = mock(Channel.class);
		ChannelSnippet channelSnippetMock = mock(ChannelSnippet.class);
		when(channelMock.getSnippet()).thenReturn(channelSnippetMock);
		when(channelSnippetMock.getTitle()).thenReturn("Channel Title");

		YoutubeConnector youtubeConnector = new YoutubeConnector(_youTubeMock, Arrays.asList(channelMock, channelMock), APIKEY, NO_CACHE);
		YoutubeVideoSearchRequest searchRequest = youtubeConnector.getSearchRequest("searchPattern", "all");

		assertNotNull(searchRequest);
		assertTrue(searchRequest instanceof YoutubeMultiChannelVideoSearchRequest);
	}

	@Test
	void getSearchRequest_SEARCH_ALL_CHANNELS() {
		Channel channelMock = mock(Channel.class);
		ChannelSnippet channelSnippetMock = mock(ChannelSnippet.class);
		when(channelMock.getSnippet()).thenReturn(channelSnippetMock);
		when(channelSnippetMock.getTitle()).thenReturn("Channel Title");

		YoutubeConnector youtubeConnector = new YoutubeConnector(_youTubeMock, Arrays.asList(channelMock, channelMock), APIKEY, NO_CACHE);
		YoutubeVideoSearchRequest searchRequest = youtubeConnector.getSearchRequest("searchPattern", "");

		assertNotNull(searchRequest);
		assertTrue(searchRequest instanceof YoutubeMultiChannelVideoSearchRequest);
	}

	@Test
	void getSearchRequest_ONE_CHANNEL_NO_QUERY_TERM() {
		Channel channelMock = mock(Channel.class);
		ChannelSnippet channelSnippetMock = mock(ChannelSnippet.class);
		when(channelMock.getSnippet()).thenReturn(channelSnippetMock);
		when(channelSnippetMock.getTitle()).thenReturn("Channel Title");

		YoutubeConnector youtubeConnector = new YoutubeConnector(_youTubeMock, Arrays.asList(channelMock, channelMock), APIKEY, NO_CACHE);
		YoutubeVideoSearchRequest searchRequest = youtubeConnector.getSearchRequest("", "");

		assertNotNull(searchRequest);
		assertTrue(searchRequest instanceof YoutubeChannelVideosRequest);
	}

	@Test
	void getSearchRequest_MULTIPLE_CHANNEL_NO_QUERY_TERM() {
		Channel channelMock = mock(Channel.class);
		ChannelSnippet channelSnippetMock = mock(ChannelSnippet.class);
		when(channelMock.getSnippet()).thenReturn(channelSnippetMock);
		when(channelSnippetMock.getTitle()).thenReturn("Channel Title");

		YoutubeConnector youtubeConnector = new YoutubeConnector(_youTubeMock, Arrays.asList(channelMock, channelMock), APIKEY, NO_CACHE);
		YoutubeVideoSearchRequest searchRequest = youtubeConnector.getSearchRequest("", "all");

		assertNotNull(searchRequest);
		assertTrue(searchRequest instanceof YoutubeChannelVideosRequest);
	}

	@Test
	void initSearchRequest_ANY_ARGUMENT() {
		String arg1 = "param1";
		String arg2 = "param2";
		YoutubeConnector youtubeConnector = spy(new YoutubeConnector(_youTubeMock, Collections.emptyList(), APIKEY, NO_CACHE));
		youtubeConnector.initSearchRequest(arg1, arg2);

		// getSearchRequest is already tested, we only test if it is invoked with the correct arguments
		verify(youtubeConnector, times(1)).getSearchRequest(arg1, arg2);
	}

	@Test
	void searchVideos_SEARCHREQUEST_INITIALIZED() {
		String arg1 = "param1";
		String arg2 = "param2";
		int count = 10;
		List<YoutubeVideo> expected = new ArrayList<>();
		
		// Create the connector spy
		YoutubeConnector youtubeConnector = spy(new YoutubeConnector(_youTubeMock, Collections.emptyList(), APIKEY, NO_CACHE));
		
		// Create the search request mock
		YoutubeStandardVideoSearchRequest searchRequestMock = mock(YoutubeStandardVideoSearchRequest.class);
		
		// Configure the mock to return expected results
		when(searchRequestMock.searchVideos(count)).thenReturn(expected);
		
		// Configure getSearchRequest to return our mock BEFORE calling initSearchRequest
		doReturn(searchRequestMock).when(youtubeConnector).getSearchRequest(arg1, arg2);
		
		// Now initialize the search request
		youtubeConnector.initSearchRequest(arg1, arg2);
		
		// Execute the method under test
		List<YoutubeVideo> result = youtubeConnector.searchVideos(count);
		
		// Verify the results
		verify(searchRequestMock, times(1)).searchVideos(count);
		assertEquals(expected, result);
	}
}
