package to.be.renamed.connector;

import to.be.renamed.YoutubeVideo;
import to.be.renamed.integration.YoutubeIntegrationConfig;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class YoutubeConnectorFsServiceImplTest {
    YoutubeConnector mockYoutubeConnector = mock(YoutubeConnector.class);
    YoutubeIntegrationConfig mockYoutubeIntegrationConfig = mock(YoutubeIntegrationConfig.class);
    long testProjectId = 123L;
    YoutubeConnectorFsServiceImpl youtubeConnectorFsService;

    @BeforeEach
    void setUp() {
        youtubeConnectorFsService = new YoutubeConnectorFsServiceImpl();
        // Inject YoutubeConnector mock
        youtubeConnectorFsService.addYoutubeConnector(testProjectId, mockYoutubeConnector);
    }

    @Test
    void testInitSearchRequest() {
        String testSearchQuery = "testSearchQuery";
        String testChannel = "testChannel";
        youtubeConnectorFsService.initSearchRequest(testProjectId, testSearchQuery, testChannel);
        verify(mockYoutubeConnector, times(1)).initSearchRequest(testSearchQuery, testChannel);
    }

    @Test
    void testSearchVideos() {
        int testCount = 10;
        List<YoutubeVideo> expected = new ArrayList<>();
        when(mockYoutubeConnector.searchVideos(testCount)).thenReturn( expected);
        List<YoutubeVideo> result = youtubeConnectorFsService.searchVideos(testProjectId, testCount);
        assertEquals(expected, result, "Value is not as expected.\nExpected: " + expected + "\nActual: " + result);
    }

    @Test
    void testGetTotal() {
        int expected = 1;
        when(mockYoutubeConnector.getTotal()).thenReturn(expected);
        int result = youtubeConnectorFsService.getTotal(testProjectId);
        assertEquals(expected, result, "Value is not as expected.\nExpected: " + expected + "\nActual: " + result);
    }

    @Test
    void testHasNext() {
        boolean expected = true;
        when(mockYoutubeConnector.hasNext()).thenReturn(expected);
        boolean result = youtubeConnectorFsService.hasNext(testProjectId);
        assertTrue(result, "Value is not as expected.\nExpected: " + expected + "\nActual: " + result);
    }

    @Test
    void testGetVideos() {
        Collection<String> videoIds = new ArrayList<>();
        List<YoutubeVideo> expected = new ArrayList<>();
        when(mockYoutubeConnector.getVideos(videoIds)).thenReturn(expected);
        List<YoutubeVideo> result = youtubeConnectorFsService.getVideos(testProjectId, videoIds);
        assertEquals(expected, result, "Value is not as expected.\nExpected: " + expected + "\nActual: " + result);
    }

    @Test
    void testGetChannels() {
        List<YoutubeChannelDTO> expected = new ArrayList<>();
        when(mockYoutubeConnector.getChannels()).thenReturn(expected);
        List<YoutubeChannelDTO> result = youtubeConnectorFsService.getChannels(testProjectId);
        assertEquals(expected, result, "Value is not as expected.\nExpected: " + expected + "\nActual: " + result);
    }

    @Test
    void clearCache() {
        boolean expected = true;
        when(mockYoutubeConnector.clearCache()).thenReturn(expected);
        boolean result = youtubeConnectorFsService.clearCache(testProjectId);
        assertEquals(expected, result, "Value is not as expected.\nExpected: " + expected + "\nActual: " + result);
    }
}