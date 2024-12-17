package to.be.renamed.executable;

import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.access.script.Executable;
import de.espirit.firstspirit.agency.TransferAgent;
import de.espirit.firstspirit.forms.FormField;
import de.espirit.firstspirit.ui.gadgets.aspects.transfer.CommodityContainer;
import de.espirit.firstspirit.ui.gadgets.aspects.transfer.TransferType;
import to.be.renamed.YoutubeVideo;

import com.espirit.moddev.components.annotations.PublicComponent;


import java.io.Writer;
import java.util.List;
import java.util.Map;

/**
 * The FirstSpirit Youtube video drop executable.
 * Executable that can be used in FS_BUTTONS as onDrop action.
 * <pre>{@code
 * 	<FS_BUTTON name="st_dropVideo" alwaysEnabled="no" hFill="yes" onDrop="class:DropYoutubeVideoExecutable" useLanguages="no">
 * 		<DROPTYPES>
 * 			<MIME classname="YoutubeVideo"/>
 * 		</DROPTYPES>
 * 		<LANGINFOS>
 * 			<LANGINFO lang="*" label="Drop Video here"/>
 * 		</LANGINFOS>
 * 		<PARAMS>
 * 			<PARAM name="id">#field.st_videoId</PARAM>
 * 			<PARAM name="title">#field.st_title</PARAM>
 * 			<PARAM name="description">#field.st_description</PARAM>
 * 		</PARAMS>
 * 	</FS_BUTTON>
 * }</pre>
 */
@PublicComponent(name = "DropYoutubeVideoExecutable",
		displayName = "Youtube Drop Video Executable",
		description = "Executable that can be used in FS_BUTTONS as onDrop action.")
public class YoutubeVideoDropExecutable implements Executable {

	/**
	 * The constant that contains ID of the parameter for the video id form field.
	 * e.g.: <pre>{@code <PARAM name="id">#field.st_videoId</PARAM>}</pre>
	 */
	private static final String PARAM_ID = "id";
	/**
	 * The constant that contains ID of the parameter for the video title form field.
	 * e.g.: <pre>{@code <PARAM name="title">#field.st_title</PARAM>}</pre>
	 */
	private static final String PARAM_TITLE = "title";
	/**
	 * The constant that contains ID of the parameter for the video description form field.
	 * e.g.: <pre>{@code <PARAM name="description">#field.st_description</PARAM>}</pre>
	 */
	private static final String PARAM_DESCRIPTION = "description";

	@Override
	public Object execute(final Map<String, Object> parameter) {
		BaseContext context = (BaseContext) parameter.get("context");
		if (parameter.containsKey("drop") && parameter.containsKey("dropdata")) {
			TransferAgent transferAgent = context.requireSpecialist(TransferAgent.TYPE);
			TransferType<YoutubeVideo> videoTransferType = transferAgent.getRawValueType(YoutubeVideo.class);
			CommodityContainer dropdata = (CommodityContainer) parameter.get("dropdata");
			List<YoutubeVideo> videos = dropdata.get(videoTransferType);

			if (!videos.isEmpty()) {
				processVideo(videos.get(0), parameter);
			}
		}
		return null;
	}

	/**
	 * Sets the information from the dropped object to the form.
	 *
	 * @param video
	 * @param paramMap
	 */
	private void processVideo(final YoutubeVideo video, final Map<String, Object> paramMap) {
		if (video != null) {
			if (paramMap.containsKey(PARAM_ID)) {
				((FormField<?>) paramMap.get(PARAM_ID)).set(video.getId());
			}
			if (paramMap.containsKey(PARAM_TITLE)) {
				((FormField<?>) paramMap.get(PARAM_TITLE)).set(video.getTitle());
			}
			if (paramMap.containsKey(PARAM_DESCRIPTION)) {
				((FormField<?>) paramMap.get(PARAM_DESCRIPTION)).set(video.getDescription());
			}
		}
	}

	@Override
	public Object execute(final Map<String, Object> parameter, final Writer out, final Writer err) {
		return execute(parameter);
	}
}
