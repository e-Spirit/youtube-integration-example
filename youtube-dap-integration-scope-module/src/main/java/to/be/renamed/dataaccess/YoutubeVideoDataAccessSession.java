package to.be.renamed.dataaccess;

import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.access.Language;
import de.espirit.firstspirit.access.editor.ValueIndexer;
import de.espirit.firstspirit.agency.Image;
import de.espirit.firstspirit.agency.ImageAgent;
import de.espirit.firstspirit.agency.TransferAgent;
import de.espirit.firstspirit.client.plugin.dataaccess.DataAccessSession;
import de.espirit.firstspirit.client.plugin.dataaccess.DataAccessSessionBuilder;
import de.espirit.firstspirit.client.plugin.dataaccess.DataSnippetProvider;
import de.espirit.firstspirit.client.plugin.dataaccess.DataStreamBuilder;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.DataTemplating;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.JsonSupporting;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.SessionAspectMap;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.SessionAspectType;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.SessionBuilderAspectMap;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.SessionBuilderAspectType;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.UrlGenerationContext;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.UrlSupporting;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.ValueIndexing;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.transfer.HandlerHost;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.transfer.SupplierHost;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.transfer.TransferHandling;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.transfer.TransferSupplying;
import de.espirit.firstspirit.client.search.SegmentProvider;
import de.espirit.firstspirit.generate.functions.json.JsonGenerationContext;
import de.espirit.firstspirit.json.JsonElement;
import de.espirit.firstspirit.json.JsonObject;
import de.espirit.firstspirit.json.JsonPair;
import de.espirit.firstspirit.json.values.JsonStringValue;
import to.be.renamed.YoutubeVideo;
import to.be.renamed.connector.YoutubeConnectorServiceFacade;
import to.be.renamed.integration.YoutubeIntegrationIcons;


import java.util.*;

/**
 * Basic class to provide a Data Access Session to support the Data Access Plugin.
 * Please see FirstSpirit API for more information.
 */
public class YoutubeVideoDataAccessSession implements DataAccessSession<YoutubeVideo> {

	private final BaseContext _context;
	private final SessionAspectMap _aspects;

	private YoutubeVideoDataAccessSession(BaseContext context) {
		_context = context;


		_aspects = new SessionAspectMap();
		_aspects.put(TransferHandling.TYPE, new YoutubeVideoTransferHandlingAspect(_context));
		_aspects.put(TransferSupplying.TYPE, new YoutubeVideoTransferSupplyingAspect(_context));
		_aspects.put(DataTemplating.TYPE, new YoutubeVideoDataTemplatingAspect());
		_aspects.put(ValueIndexing.TYPE, new YoutubeVideoValueIndexingAspect());
		_aspects.put(JsonSupporting.TYPE, new YoutubeJsonReportingAspect());
		_aspects.put(UrlSupporting.TYPE, new YoutubeUrlSupportingAspect());
	}

	@Override
	public DataSnippetProvider<YoutubeVideo> createDataSnippetProvider() {
		return new YoutubeVideoDataSnippetProvider(_context);
	}

	@Override
	public DataStreamBuilder<YoutubeVideo> createDataStreamBuilder() {
		return new YoutubeVideoDataStream.Builder(_context);
	}

	@Override
	public <A> A getAspect(SessionAspectType<A> aspectType) {
		return _aspects.get(aspectType);
	}

	@Override
	public YoutubeVideo getData(String identifier) throws NoSuchElementException {
		List<YoutubeVideo> youtubeVideos = getData(Collections.singletonList(identifier));
		return youtubeVideos.isEmpty() ? null : youtubeVideos.get(0);
	}

	@Override
	public List<YoutubeVideo> getData(Collection<String> identifierList) {
		// No channels are required to retrieve videos.
		YoutubeConnectorServiceFacade youtubeConnectorServiceFacade = new YoutubeConnectorServiceFacade(_context);
		
		List<YoutubeVideo> youtubeVideos =  youtubeConnectorServiceFacade.getVideos(identifierList);
		//The resulting object list has to match the list of identifiers in size and order.
		if(youtubeVideos == null || youtubeVideos.isEmpty()) {
			List<YoutubeVideo> nullList = new ArrayList<>();
			for (int i = 0; i < identifierList.size(); i++) {
				nullList.add(null);
			}
			return nullList;
		}
		return youtubeVideos;
	}

	@Override
	public String getIdentifier(YoutubeVideo video) throws NoSuchElementException {
		return video.getId();
	}

	/**
	 * Basic class to internally support URLs.
	 * Please see FirstSpirit API for more information.
	 */
	public static class YoutubeUrlSupportingAspect implements UrlSupporting<YoutubeVideo> {

		@Override
		public Optional<String> getUrl(UrlGenerationContext urlGenerationContext, YoutubeVideo youtubeVideo) {
			String youtubeUrl = "https://www.youtube.com/watch?v=" + youtubeVideo.getId();
			return Optional.of(youtubeUrl);
		}
	}

	/**
	 * Basic class to support JSON representations in reports.
	 * Please see FirstSpirit API for more information.
	 */
	public static class YoutubeJsonReportingAspect implements JsonSupporting<YoutubeVideo> {

		@Override
		public JsonElement<?> handle(JsonGenerationContext jsonGenerationContext, YoutubeVideo youtubeVideo) {
			final JsonObject jsonResult = JsonObject.create();
			jsonResult.put(JsonPair.of("title", JsonStringValue.of(youtubeVideo.getTitle())));
			jsonResult.put(JsonPair.of("id", JsonStringValue.of(youtubeVideo.getId())));
			jsonResult.put(JsonPair.of("description", JsonStringValue.of(youtubeVideo.getDescription())));
			jsonResult.put(JsonPair.of("posterUrl", JsonStringValue.of(youtubeVideo.getPosterUrl())));
			jsonResult.put(JsonPair.of("thumbnailUrl", JsonStringValue.of(youtubeVideo.getThumbnailUrl())));
			jsonResult.put(JsonPair.of("publishedAt", JsonStringValue.of(youtubeVideo.getPublishedAt())));

			return jsonResult;
		}

		@Override
		public Class<YoutubeVideo> getSupportedClass() {
			return YoutubeVideo.class;
		}
	}

	/**
	 * Basic class to create a Data Access Session.
	 * Please see FirstSpirit API for more information.
	 */
	public static class Builder implements DataAccessSessionBuilder<YoutubeVideo> {

		private final SessionBuilderAspectMap _aspects = new SessionBuilderAspectMap();

		@Override
		public DataAccessSession<YoutubeVideo> createSession(BaseContext context) {
			return new YoutubeVideoDataAccessSession(context);
		}

		@Override
		public <A> A getAspect(SessionBuilderAspectType<A> aspectType) {
			return _aspects.get(aspectType);
		}
	}

	/**
	 * Basic class to implement report snippet representations.
	 * Please see FirstSpirit API for more information.
	 */
	public static class YoutubeVideoDataSnippetProvider implements DataSnippetProvider<YoutubeVideo> {

		private final BaseContext _context;
		private final Image<?> _icon;

		private YoutubeVideoDataSnippetProvider(BaseContext context) {
			_context = context;
			if (context.is(BaseContext.Env.WEBEDIT)) {
				_icon = null;
			} else {
				_icon = YoutubeIntegrationIcons.getVideo(context);
			}
		}

		@Override
		public Image<?> getIcon(YoutubeVideo video) {
			return _icon;
		}

		@Override
		public Image<?> getThumbnail(YoutubeVideo video, Language language) {
			ImageAgent imageAgent = _context.requireSpecialist(ImageAgent.TYPE);
			return imageAgent.getImageFromUrl(video.getThumbnailUrl());
		}

		@Override
		public String getHeader(YoutubeVideo video, Language language) {
			return video.getTitle();
		}

		@Override
		public String getExtract(YoutubeVideo video, Language language) {
			return video.getDescription();
		}
	}

	/**
	 * Basic class to internally handle data transfers, i.e. drag and drop, etc.
	 * Please see FirstSpirit API for more information.
	 */
	public static class YoutubeVideoTransferHandlingAspect implements TransferHandling<YoutubeVideo> {

		private final BaseContext _context;

		private YoutubeVideoTransferHandlingAspect(BaseContext context) {
			_context = context;
		}

		@Override
		public void registerHandlers(HandlerHost<YoutubeVideo> host) {
			TransferAgent transferAgent = _context.requireSpecialist(TransferAgent.TYPE);
			host.registerHandler(transferAgent.getRawValueType(YoutubeVideo.class), new YoutubeVideoHandler());
		}

		static class YoutubeVideoHandler implements HandlerHost.Handler<YoutubeVideo, YoutubeVideo> {

			@Override
			public List<YoutubeVideo> handle(List<YoutubeVideo> videoList) {
				return videoList;
			}
		}
	}

	/**
	 * Basic class to internally handle data transfers, i.e. drag and drop, etc.
	 * Please see FirstSpirit API for more information.
	 */
	public static class YoutubeVideoTransferSupplyingAspect implements TransferSupplying<YoutubeVideo> {

		private final BaseContext _context;

		private YoutubeVideoTransferSupplyingAspect(BaseContext context) {
			_context = context;
		}

		@Override
		public void registerSuppliers(SupplierHost<YoutubeVideo> host) {
			TransferAgent transferAgent = _context.requireSpecialist(TransferAgent.TYPE);
			host.registerSupplier(transferAgent.getRawValueType(YoutubeVideo.class), new YoutubeVideoSupplier());
			host.registerSupplier(transferAgent.getPlainTextType(), new YoutubeVideoTextSupplier());
			host.registerSupplier(transferAgent.getQuerySegmentType(), new YoutubeVideoSegmentSupplier());
		}

		static class YoutubeVideoSupplier implements SupplierHost.Supplier<YoutubeVideo, YoutubeVideo> {

			@Override
			public List<YoutubeVideo> supply(YoutubeVideo video) {
				return Collections.singletonList(video);
			}
		}

		static class YoutubeVideoTextSupplier implements SupplierHost.Supplier<YoutubeVideo, String> {

			@Override
			public List<String> supply(YoutubeVideo video) {
				return Collections.singletonList(video.getTitle());
			}
		}

		static class YoutubeVideoSegmentSupplier implements SupplierHost.Supplier<YoutubeVideo, SegmentProvider> {

			@Override
			public List<SegmentProvider> supply(YoutubeVideo video) {
				return Collections.singletonList(new YoutubeVideoQuerySegmentProvider(video.getId()));
			}

			static class YoutubeVideoQuerySegmentProvider implements SegmentProvider {

				private final String _segment;

				YoutubeVideoQuerySegmentProvider(String segment) {
					_segment = segment;
				}

				@Override
				public String getSegment() {
					return _segment;
				}
			}
		}
	}

	/**
	 * Basic class to implement report fly-outs.
	 * Please see FirstSpirit API for more information.
	 */
	public static class YoutubeVideoDataTemplatingAspect implements DataTemplating<YoutubeVideo> {

		@Override
		public String getTemplate(YoutubeVideo video, Language language) {
			return "<div style=\"width: 450px;\"><div style=\"font-size: 1.5em; line-height: 1.3;\">${title}</div><img src=\"${posterUrl}\" style=\"display: block; width: 100%; margin: 10px 0\"><div style=\"overflow: hidden; max-height: 7em; white-space: pre-wrap; word-break: break-word;\">${description}</div></div>";
		}

		@Override
		public void registerParameters(ParameterSet parameters, YoutubeVideo video, Language language) {
			parameters.addHtml("posterUrl", video.getPosterUrl());
			parameters.addHtml("title", video.getTitle());
			parameters.addHtml("description", video.getDescription());
		}
	}

	/**
	 * Basic class to internally handle report listings
	 * Please see FirstSpirit API for more information.
	 */
	public static class YoutubeVideoValueIndexingAspect implements ValueIndexing {

		@Override
		public void appendIndexData(String identifier, Language language, boolean recursive, ValueIndexer indexer) {
			indexer.append(ValueIndexer.VALUE_FIELD, identifier);
		}
	}
}
