package to.be.renamed.connector;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.ChannelSnippet;

import to.be.renamed.integration.YoutubeIntegrationConfig;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class YoutubeConnectorBuildTest {

	public static final String APIKEY = "apikey";
	public static final String CHANNEL_1 = "Channel 1";
	static YouTube _youTubeMock;
	static YouTube.Channels.List _channelsListMock;
	@Mock
        YoutubeIntegrationConfig _youtubeIntegrationConfig;

	@BeforeAll
	static void beforeAll() throws IOException {
		_youTubeMock = mock(YouTube.class);
		YouTube.Channels channelsMock = mock(YouTube.Channels.class);

		_channelsListMock = mock(YouTube.Channels.List.class);
		when(_channelsListMock.setKey(anyString())).thenReturn(_channelsListMock);
		when(_channelsListMock.setFields(anyString())).thenReturn(_channelsListMock);
		when(_channelsListMock.setId(anyList())).thenReturn(_channelsListMock);

		when(channelsMock.list(any())).thenReturn(_channelsListMock);

		when(_youTubeMock.channels()).thenReturn(channelsMock);
	}

	@Test
	void builder_EMPTY_API_KEY() {
		when(_youtubeIntegrationConfig.getApiKey()).thenReturn("");
		YoutubeConnector youtubeConnector = new YoutubeConnector.Builder().config(_youtubeIntegrationConfig).build();

		assertNull(youtubeConnector);
	}

	@Test
	void builder_NULL_API_KEY() {
		when(_youtubeIntegrationConfig.getApiKey()).thenReturn(null);
		YoutubeConnector youtubeConnector = new YoutubeConnector.Builder().config(_youtubeIntegrationConfig).build();

		assertNull(youtubeConnector);
	}

	@Test
	void builder_ONLY_API_KEY() {
		YoutubeConnector youtubeConnector = new YoutubeConnector.Builder().apikey(APIKEY).build();

		assertNotNull(youtubeConnector);
	}

	@Test
	void builder_NO_CHANNELS() {
		when(_youtubeIntegrationConfig.getApiKey()).thenReturn(APIKEY);
		when(_youtubeIntegrationConfig.getChannelIds()).thenReturn(null);
		YoutubeConnector youtubeConnector = new YoutubeConnector.Builder().config(_youtubeIntegrationConfig).build();

		assertNotNull(youtubeConnector);
		assertTrue(youtubeConnector.getChannels().isEmpty());
	}

	@Test
	void builder_WITH_CHANNELS() throws IOException {
		when(_youtubeIntegrationConfig.getApiKey()).thenReturn(APIKEY);
		when(_youtubeIntegrationConfig.getChannelIds()).thenReturn(Collections.singletonList(CHANNEL_1));
		Channel channel = mock(Channel.class);
		ChannelSnippet channelSnippet = mock(ChannelSnippet.class);
		YoutubeConnector.Builder spyBuilder = spy(new YoutubeConnector.Builder());
		doReturn(Collections.singletonList(channel)).when(spyBuilder).getYoutubeChannels(any());
		doReturn("testId").when(channel).getId();
		doReturn(channelSnippet).when(channel).getSnippet();
		doReturn("testTitle").when(channelSnippet).getTitle();

		YoutubeConnector youtubeConnector = spyBuilder.config(_youtubeIntegrationConfig).build();

		assertNotNull(youtubeConnector);
		assertFalse(youtubeConnector.getChannels().isEmpty());
		assertEquals(1, youtubeConnector.getChannels().size());
	}

	@Test
	void builder_getYoutubeChannels_NO_CHANNEL() throws IOException {
		when(_youtubeIntegrationConfig.getApiKey()).thenReturn("apikey");
		when(_youtubeIntegrationConfig.getChannelIds()).thenReturn(Collections.emptyList());

		List<Channel> youtubeChannels = new YoutubeConnector.Builder().config(_youtubeIntegrationConfig).getYoutubeChannels(_youTubeMock);

		assertNotNull(youtubeChannels);
		assertTrue(youtubeChannels.isEmpty());
	}

	@Test
	void builder_getYoutubeChannels_ONE_CHANNEL() throws IOException {
		when(_youtubeIntegrationConfig.getApiKey()).thenReturn(APIKEY);
		when(_youtubeIntegrationConfig.getChannelIds()).thenReturn(Collections.singletonList(CHANNEL_1));

		ChannelListResponse channelListResponseMock = mock(ChannelListResponse.class);
		when(_channelsListMock.execute()).thenReturn(channelListResponseMock);

		Channel channel = mock(Channel.class);
		when(channelListResponseMock.getItems()).thenReturn(Collections.singletonList(channel));
		when(channel.getId()).thenReturn(CHANNEL_1);

		List<Channel> youtubeChannels = new YoutubeConnector.Builder().config(_youtubeIntegrationConfig).getYoutubeChannels(_youTubeMock);

		assertNotNull(youtubeChannels);
		assertFalse(youtubeChannels.isEmpty());
		assertEquals(1, youtubeChannels.size());
		assertEquals("Channel 1", youtubeChannels.get(0).getId());
	}

	@Test
	void builder_getYoutubeChannels_MULTI_CHANNEL_RESPONSE() throws IOException {
		when(_youtubeIntegrationConfig.getApiKey()).thenReturn("apikey");
		when(_youtubeIntegrationConfig.getChannelIds()).thenReturn(Collections.singletonList(CHANNEL_1));

		ChannelListResponse channelListResponseMock = mock(ChannelListResponse.class);
		when(_channelsListMock.execute()).thenReturn(channelListResponseMock);

		Channel channel = mock(Channel.class);
		when(channelListResponseMock.getItems()).thenReturn(Arrays.asList(channel, channel));

		YoutubeConnector.Builder builder = new YoutubeConnector.Builder().config(_youtubeIntegrationConfig);
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			builder.getYoutubeChannels(_youTubeMock);
		});

		assertTrue(exception.getMessage().contains("Unknown ChannelId"));
	}

	@Test
	void builder_getYoutubeChannels_NO_CHANNEL_RESPONSE() throws IOException {
		when(_youtubeIntegrationConfig.getApiKey()).thenReturn("apikey");
		when(_youtubeIntegrationConfig.getChannelIds()).thenReturn(Collections.singletonList(CHANNEL_1));

		ChannelListResponse channelListResponseMock = mock(ChannelListResponse.class);
		when(_channelsListMock.execute()).thenReturn(channelListResponseMock);

		when(channelListResponseMock.getItems()).thenReturn(Collections.emptyList());

		YoutubeConnector.Builder builder = new YoutubeConnector.Builder().config(_youtubeIntegrationConfig);
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			builder.getYoutubeChannels(_youTubeMock);
		});

		assertTrue(exception.getMessage().contains("Unknown ChannelId"));
	}

	@Test
	void builder_getYoutubeChannels_CHANNEL_ID_MISMATCH() throws IOException {
		when(_youtubeIntegrationConfig.getApiKey()).thenReturn("apikey");
		when(_youtubeIntegrationConfig.getChannelIds()).thenReturn(Collections.singletonList(CHANNEL_1));

		ChannelListResponse channelListResponseMock = mock(ChannelListResponse.class);
		when(_channelsListMock.execute()).thenReturn(channelListResponseMock);

		Channel channel = mock(Channel.class);
		when(channel.getId()).thenReturn("Channel mismatch");
		when(channelListResponseMock.getItems()).thenReturn(Arrays.asList(channel));

		YoutubeConnector.Builder builder = new YoutubeConnector.Builder().config(_youtubeIntegrationConfig);
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			builder.getYoutubeChannels(_youTubeMock);
		});

		assertTrue(exception.getMessage().contains("Incomplete ChannelId"));
	}

}