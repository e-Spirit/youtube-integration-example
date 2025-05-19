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
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateYoutubeMultiChannelVideoSearchRequestTest {

	@Mock
	YouTube _youTubeMock;

	@Test
	void createInstance_NO_QUERY() throws IOException {
		YouTube.Search searchMock = mock(YouTube.Search.class);
		YouTube.Search.List searchListMock = mock(YouTube.Search.List.class);
		when(searchListMock.setKey(anyString())).thenReturn(searchListMock);
		when(searchListMock.setType(anyList())).thenReturn(searchListMock);
		when(searchListMock.setFields(anyString())).thenReturn(searchListMock);
		when(searchMock.list(any())).thenReturn(searchListMock);
		when(_youTubeMock.search()).thenReturn(searchMock);

		Channel channelMock = getChannelMock();

		String apikey = "apikey";
		YoutubeMultiChannelVideoSearchRequest
			youtubeMultiChannelVideoSearchRequest = YoutubeMultiChannelVideoSearchRequest.createInstance(apikey, _youTubeMock, "", Collections.singletonList(channelMock));

		verify(searchListMock).setKey(apikey);
		assertNotNull(youtubeMultiChannelVideoSearchRequest);
	}

	@Test
	void createInstance_MULTI_CHANNEL() throws IOException {
		YouTube.Search searchMock = mock(YouTube.Search.class);
		YouTube.Search.List searchListMock = mock(YouTube.Search.List.class);
		when(searchListMock.setKey(anyString())).thenReturn(searchListMock);
		when(searchListMock.setType(anyList())).thenReturn(searchListMock);
		when(searchListMock.setFields(anyString())).thenReturn(searchListMock);
		when(searchMock.list(any())).thenReturn(searchListMock);
		when(_youTubeMock.search()).thenReturn(searchMock);

		Channel channelMock = getChannelMock();

		String apikey = "apikey";
		YoutubeMultiChannelVideoSearchRequest youtubeMultiChannelVideoSearchRequest = YoutubeMultiChannelVideoSearchRequest.createInstance(apikey, _youTubeMock, "", Arrays.asList(channelMock, channelMock));

		verify(searchListMock).setKey(apikey);
		assertNotNull(youtubeMultiChannelVideoSearchRequest);
	}

	@Test
	void createInstance_QUERY() throws IOException {
		YouTube.Search searchMock = mock(YouTube.Search.class);
		YouTube.Search.List searchListMock = mock(YouTube.Search.List.class);
		when(searchListMock.setKey(anyString())).thenReturn(searchListMock);
		when(searchListMock.setType(anyList())).thenReturn(searchListMock);
		when(searchListMock.setFields(anyString())).thenReturn(searchListMock);
		when(searchMock.list(any())).thenReturn(searchListMock);
		when(_youTubeMock.search()).thenReturn(searchMock);

		Channel channelMock = getChannelMock();

		String apikey = "apikey";
		String query = "query";
		YoutubeMultiChannelVideoSearchRequest youtubeMultiChannelVideoSearchRequest = YoutubeMultiChannelVideoSearchRequest.createInstance(apikey, _youTubeMock, query, Arrays.asList(channelMock, channelMock));

		verify(searchListMock).setKey(apikey);
		verify(searchListMock).setQ(query);
		assertNotNull(youtubeMultiChannelVideoSearchRequest);
	}

	@NotNull
	private Channel getChannelMock() {
		Channel channelMock = mock(Channel.class);
		ChannelSnippet channelSnippetMock = mock(ChannelSnippet.class);
		when(channelMock.getSnippet()).thenReturn(channelSnippetMock);
		when(channelSnippetMock.getTitle()).thenReturn("Channel Title");
		return channelMock;
	}
}