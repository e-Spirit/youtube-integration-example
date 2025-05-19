package to.be.renamed.connector;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelSnippet;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateYoutubeStandardVideoSearchRequestTest {

	@Mock
	YouTube _youTubeMock;

	@Test
	void createInstance_SIMPLE() throws IOException {
		YouTube.Search searchMock = mock(YouTube.Search.class);
		YouTube.Search.List searchListMock = mock(YouTube.Search.List.class);
		when(searchListMock.setKey(anyString())).thenReturn(searchListMock);
		when(searchListMock.setType(anyList())).thenReturn(searchListMock);
		when(searchListMock.setFields(anyString())).thenReturn(searchListMock);
		when(searchMock.list(any())).thenReturn(searchListMock);
		when(_youTubeMock.search()).thenReturn(searchMock);

		String apikey = "apikey";
		YoutubeStandardVideoSearchRequest
			youtubeStandardVideoSearchRequest = YoutubeStandardVideoSearchRequest.createInstance(apikey, _youTubeMock, "", null);
		verify(searchListMock).setKey(apikey);

		assertNotNull(youtubeStandardVideoSearchRequest);
	}

	@Test
	void createInstance_NO_QUERY() throws IOException {
		YouTube.Search searchMock = mock(YouTube.Search.class);
		YouTube.Search.List searchListMock = mock(YouTube.Search.List.class);
		when(searchListMock.setKey(anyString())).thenReturn(searchListMock);
		when(searchListMock.setType(anyList())).thenReturn(searchListMock);
		when(searchListMock.setChannelId(anyString())).thenReturn(searchListMock);
		when(searchListMock.setFields(anyString())).thenReturn(searchListMock);
		when(searchMock.list(any())).thenReturn(searchListMock);
		when(_youTubeMock.search()).thenReturn(searchMock);

		String channelID = "Channel 1";
		Channel channelMock = getChannelMock(channelID);

		String apikey = "apikey";
		YoutubeStandardVideoSearchRequest youtubeStandardVideoSearchRequest = YoutubeStandardVideoSearchRequest.createInstance(apikey, _youTubeMock, "", channelMock);
		verify(searchListMock).setKey(apikey);
		verify(searchListMock).setChannelId(channelID);

		assertNotNull(youtubeStandardVideoSearchRequest);
	}

	@Test
	void createInstance_NO_CHANNEL() throws IOException {
		YouTube.Search searchMock = mock(YouTube.Search.class);
		YouTube.Search.List searchListMock = mock(YouTube.Search.List.class);
		when(searchListMock.setKey(anyString())).thenReturn(searchListMock);
		when(searchListMock.setType(anyList())).thenReturn(searchListMock);
		when(searchListMock.setQ(anyString())).thenReturn(searchListMock);
		when(searchListMock.setFields(anyString())).thenReturn(searchListMock);
		when(searchMock.list(any())).thenReturn(searchListMock);
		when(_youTubeMock.search()).thenReturn(searchMock);

		String apikey = "apikey";
		String query = "query";
		YoutubeStandardVideoSearchRequest youtubeStandardVideoSearchRequest = YoutubeStandardVideoSearchRequest.createInstance(apikey, _youTubeMock, query, null);
		verify(searchListMock).setKey(apikey);
		verify(searchListMock).setQ(query);

		assertNotNull(youtubeStandardVideoSearchRequest);
	}

	@Test
	void createInstance() throws IOException {
		YouTube.Search searchMock = mock(YouTube.Search.class);
		YouTube.Search.List searchListMock = mock(YouTube.Search.List.class);
		when(searchListMock.setKey(anyString())).thenReturn(searchListMock);
		when(searchListMock.setType(anyList())).thenReturn(searchListMock);
		when(searchListMock.setQ(anyString())).thenReturn(searchListMock);
		when(searchListMock.setChannelId(anyString())).thenReturn(searchListMock);
		when(searchListMock.setFields(anyString())).thenReturn(searchListMock);
		when(searchMock.list(any())).thenReturn(searchListMock);
		when(_youTubeMock.search()).thenReturn(searchMock);

		String channelID = "Channel 1";
		Channel channelMock = getChannelMock(channelID);

		String apikey = "apikey";
		String query = "query";
		YoutubeStandardVideoSearchRequest youtubeStandardVideoSearchRequest = YoutubeStandardVideoSearchRequest.createInstance(apikey, _youTubeMock, query, channelMock);
		verify(searchListMock).setKey(apikey);
		verify(searchListMock).setQ(query);
		verify(searchListMock).setChannelId(channelID);

		assertNotNull(youtubeStandardVideoSearchRequest);
	}

	@NotNull
	private Channel getChannelMock(final String channelID) {
		Channel channelMock = mock(Channel.class);
		when(channelMock.getId()).thenReturn(channelID);
		ChannelSnippet channelSnippetMock = mock(ChannelSnippet.class);
		when(channelMock.getSnippet()).thenReturn(channelSnippetMock);
		when(channelSnippetMock.getTitle()).thenReturn("Channel Title");
		return channelMock;
	}
}