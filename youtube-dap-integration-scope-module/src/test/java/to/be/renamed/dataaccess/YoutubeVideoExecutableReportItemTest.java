package to.be.renamed.dataaccess;

import to.be.renamed.YoutubeVideo;

import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.agency.OperationAgent;
import de.espirit.firstspirit.client.plugin.report.ReportContext;
import de.espirit.firstspirit.webedit.server.ClientScriptOperation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class YoutubeVideoExecutableReportItemTest {

	static ReportContext<YoutubeVideo> _reportContextMock;
	static ClientScriptOperation _clientScriptOperationMock;

	@BeforeAll
	static void beforeAll() {
		_reportContextMock = mock(ReportContextYoutubeVideo.class);
		when(_reportContextMock.is(BaseContext.Env.WEBEDIT)).thenReturn(true);

		OperationAgent operationAgentMock = mock(OperationAgent.class);
		when(_reportContextMock.requireSpecialist(OperationAgent.TYPE)).thenReturn(operationAgentMock);

		_clientScriptOperationMock = mock(ClientScriptOperation.class);
		when(operationAgentMock.getOperation(ClientScriptOperation.TYPE)).thenReturn(_clientScriptOperationMock);
	}

	@Test
	void execute() {
		YoutubeVideo videoMock = mock(YoutubeVideo.class);
		when(videoMock.getId()).thenReturn("id");
		when(videoMock.getTitle()).thenReturn("title");
		when(videoMock.getDescription()).thenReturn("description");
		when(videoMock.getThumbnailUrl()).thenReturn("http://thumbnail.url");
		when(videoMock.getPosterUrl()).thenReturn("http://poster.url");
		when(videoMock.getPublishedAt()).thenReturn("2002-10-02T15:00:00Z");


		when(_reportContextMock.getObject()).thenReturn(videoMock);

		YoutubeVideoDataAccessPlugin.YoutubeVideoReportItemsProvidingAspect.YoutubeVideoPreviewItem youtubeVideoPreviewItem = new YoutubeVideoDataAccessPlugin.YoutubeVideoReportItemsProvidingAspect.YoutubeVideoPreviewItem();
		youtubeVideoPreviewItem.execute(_reportContextMock);

		verify(_clientScriptOperationMock).perform("openYoutubePreview('title', 'id')", false);
	}

	@Test
	void execute_TITLE_APOSTROPHE() {
		YoutubeVideo videoMock = mock(YoutubeVideo.class);
		when(videoMock.getId()).thenReturn("id");
		when(videoMock.getTitle()).thenReturn("title'apostrophe");
		when(videoMock.getDescription()).thenReturn("description");
		when(videoMock.getThumbnailUrl()).thenReturn("http://thumbnail.url");
		when(videoMock.getPosterUrl()).thenReturn("http://poster.url");
		when(videoMock.getPublishedAt()).thenReturn("2002-10-02T15:00:00Z");


		when(_reportContextMock.getObject()).thenReturn(videoMock);

		YoutubeVideoDataAccessPlugin.YoutubeVideoReportItemsProvidingAspect.YoutubeVideoPreviewItem youtubeVideoPreviewItem = new YoutubeVideoDataAccessPlugin.YoutubeVideoReportItemsProvidingAspect.YoutubeVideoPreviewItem();
		youtubeVideoPreviewItem.execute(_reportContextMock);

		verify(_clientScriptOperationMock).perform("openYoutubePreview('title\\'apostrophe', 'id')", false);
	}

	//private interface just created to avoid unchecked type operations compiler warnings
	private interface ReportContextYoutubeVideo extends ReportContext<YoutubeVideo> {
	}

}
