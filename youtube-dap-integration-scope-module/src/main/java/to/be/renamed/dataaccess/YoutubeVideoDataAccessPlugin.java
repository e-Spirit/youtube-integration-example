package to.be.renamed.dataaccess;

import de.espirit.common.base.Logging;
import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.agency.Image;
import de.espirit.firstspirit.agency.OperationAgent;
import de.espirit.firstspirit.agency.ProjectAgent;
import de.espirit.firstspirit.client.plugin.dataaccess.DataAccessPlugin;
import de.espirit.firstspirit.client.plugin.dataaccess.DataAccessSessionBuilder;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.DataAccessAspectMap;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.DataAccessAspectType;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.ReportItemsProviding;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.Reporting;
import de.espirit.firstspirit.client.plugin.report.JavaClientExecutableReportItem;
import de.espirit.firstspirit.client.plugin.report.ReportContext;
import de.espirit.firstspirit.client.plugin.report.ReportItem;
import de.espirit.firstspirit.ui.operations.RequestOperation;
import de.espirit.firstspirit.webedit.plugin.report.WebeditExecutableReportItem;
import de.espirit.firstspirit.webedit.server.ClientScriptOperation;
import to.be.renamed.YoutubeVideo;
import to.be.renamed.integration.ProjectAppHelper;
import to.be.renamed.integration.YoutubeIntegrationIcons;

import com.espirit.moddev.components.annotations.PublicComponent;

import javax.swing.Icon;
import java.awt.Desktop;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;

/**
 * Basic class to provide a Data Access Plugin that can be used within FirstSpirit, i.e. reports,
 * input components, etc..  Please see FirstSpirit API for more information.
 */

@PublicComponent(name = "YoutubeVideoDataAccessPlugin",
		displayName = "Youtube Video Data Access Plugin",
		description = "Youtube Video Data Access Plugin")
public class YoutubeVideoDataAccessPlugin implements DataAccessPlugin<YoutubeVideo> {

    private static final Class<?> LOGGER = YoutubeVideoDataAccessPlugin.class;
	private final DataAccessAspectMap _aspects = new DataAccessAspectMap();

	@Override
	public void setUp(BaseContext context) {
		final long projectId = context.requireSpecialist(ProjectAgent.TYPE).getId();
		if (ProjectAppHelper.isInstalled(context, projectId)) {
			_aspects.put(Reporting.TYPE, new YouTubeVideoReportingAspect(context));
			_aspects.put(ReportItemsProviding.TYPE, new YoutubeVideoReportItemsProvidingAspect());
		}
	}

	@Override
	public void tearDown() {
		// Nothing needs to be done here
	}

	@Override
	public DataAccessSessionBuilder<YoutubeVideo> createSessionBuilder() {
		return new YoutubeVideoDataAccessSession.Builder();
	}

	@Override
	public <A> A getAspect(DataAccessAspectType<A> aspectType) {
		return _aspects.get(aspectType);
	}

	@Override
	public Image<?> getIcon() {
		return null;
	}

	@Override
	public String getLabel() {
		return "YouTube";
	}

	/**
	 * Basic class to set up a Content Creator Report. Please see FirstSpirit API for more information.
	 */
	public static class YouTubeVideoReportingAspect implements Reporting {

		private final BaseContext _context;

		private YouTubeVideoReportingAspect(BaseContext context) {
			_context = context;
		}

		@Override
		public Image<?> getReportIcon(boolean active) {
			if (_context.is(BaseContext.Env.WEBEDIT)) {
				return active ? YoutubeIntegrationIcons.getActive(_context) : YoutubeIntegrationIcons.getInactive(_context);
			} else {
				return active ? YoutubeIntegrationIcons.getInactive(_context) : YoutubeIntegrationIcons.getActive(_context);
			}
		}
	}

	public static class YoutubeVideoReportItemsProvidingAspect implements ReportItemsProviding<YoutubeVideo> {

		private final YoutubeVideoPreviewItem _clickItem;

		private YoutubeVideoReportItemsProvidingAspect() {
			_clickItem = new YoutubeVideoPreviewItem();
		}

		@Override
		public ReportItem<YoutubeVideo> getClickItem() {
			return _clickItem;
		}

		@Override
		public Collection<? extends ReportItem<YoutubeVideo>> getItems() {
			return Collections.emptyList();
		}

		/**
		 * Basic class to handle report icons, i.e. click event, etc.. Please see FirstSpirit API for more information.
		 */
		static class YoutubeVideoPreviewItem implements JavaClientExecutableReportItem<YoutubeVideo>, WebeditExecutableReportItem<YoutubeVideo> {

			@Override
			public boolean isVisible(ReportContext<YoutubeVideo> context) {
				return true;
			}

			@Override
			public boolean isEnabled(ReportContext<YoutubeVideo> context) {
				return true;
			}

			@Override
			public String getLabel(ReportContext<YoutubeVideo> context) {
				return null;
			}

			@Override
			public String getIconPath(ReportContext<YoutubeVideo> context) {
				return null;
			}

			@Override
			public Icon getIcon(ReportContext<YoutubeVideo> context) {
				return null;
			}

			/**
			 * This method is used to specify the performed action when a report item is clicked.
			 * Here, an overlay window with a video preview is shown when in Content Creator or a Browser window
			 * otherwise.
			 *
			 * @param context
			 */
			@Override
			public void execute(ReportContext<YoutubeVideo> context) {
				YoutubeVideo video = context.getObject();

				if (context.is(BaseContext.Env.WEBEDIT)) {

					//Execution in Content Creator (open JS overlay)

					ClientScriptOperation clientScript = context.requireSpecialist(OperationAgent.TYPE).getOperation(ClientScriptOperation.TYPE);
					String title = video.getTitle().replaceAll("(')", "\\\\'");

					String script = String.format("openYoutubePreview('%s', '%s')", title, video.getId());
					clientScript.perform(script, false);
				} else {

					//Execution in Site Architect (open browser window)

					String url = "https://www.youtube.com/watch?v=" + video.getId();
					try {
						Desktop desktop = Desktop.getDesktop();
						desktop.browse(new URI(url));
					} catch (final Exception e) {
						Logging.logError("Error opening video url", e, LOGGER);
						RequestOperation message = context.requireSpecialist(OperationAgent.TYPE).getOperation(RequestOperation.TYPE);
						message.setTitle("YouTube");
						message.perform(url);
					}
				}
			}
		}
	}
}
