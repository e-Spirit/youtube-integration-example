package to.be.renamed.dataaccess;

import de.espirit.common.tools.Strings;
import de.espirit.firstspirit.access.BaseContext;

import de.espirit.firstspirit.client.plugin.dataaccess.DataStream;
import de.espirit.firstspirit.client.plugin.dataaccess.DataStreamBuilder;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.Filterable;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.StreamBuilderAspectMap;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.StreamBuilderAspectType;
import de.espirit.firstspirit.client.plugin.report.Parameter;
import de.espirit.firstspirit.client.plugin.report.ParameterMap;
import de.espirit.firstspirit.client.plugin.report.ParameterSelect;
import de.espirit.firstspirit.client.plugin.report.ParameterText;

import to.be.renamed.YoutubeVideo;
import to.be.renamed.connector.YoutubeChannelDTO;
import to.be.renamed.connector.YoutubeConnectorServiceFacade;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The FirstSpirit Youtube video data stream.
 * Please see FirstSpirit API for more information.
 */
public class YoutubeVideoDataStream implements DataStream<YoutubeVideo> {

    private final YoutubeConnectorServiceFacade youtubeConnectorServiceFacade;

    public YoutubeVideoDataStream(final YoutubeConnectorServiceFacade youtubeConnectorServiceFacade) {
        this.youtubeConnectorServiceFacade = youtubeConnectorServiceFacade;
    }

    @Override
    public void close() {
        // Nothing
    }

    @Override
    public List<YoutubeVideo> getNext(int count) {
        if (!youtubeConnectorServiceFacade.hasNext()) {
            return Collections.emptyList();
        }
        return youtubeConnectorServiceFacade.searchVideos(count);
    }

    @Override
    public int getTotal() {
        return youtubeConnectorServiceFacade.getTotal();
    }

    @Override
    public boolean hasNext() {
        return youtubeConnectorServiceFacade.hasNext();
    }

    /**
     * The FirstSpirit Youtube video data stream builder.
     * Please see FirstSpirit API for more information.
     */
    public static class Builder implements DataStreamBuilder<YoutubeVideo> {

        private final FilterableAspect _filterableAspect;
        private final StreamBuilderAspectMap _aspects;
        private final YoutubeConnectorServiceFacade youtubeConnectorServiceFacade;

        /**
         * Instantiates a new Builder.
         *
         * @param context the context
         */
        Builder(BaseContext context) {
            youtubeConnectorServiceFacade = new YoutubeConnectorServiceFacade(context);

            _aspects = new StreamBuilderAspectMap();

            List<ParameterSelect.SelectItem> selectItems = new ArrayList<>();
                List<YoutubeChannelDTO> youtubeChannels = youtubeConnectorServiceFacade.getChannels();
                if (youtubeChannels != null && !youtubeChannels.isEmpty()) {
                    ParameterSelect.SelectItem selectItemAll = Parameter.Factory.createSelectItem("All Channels", "all");
                    selectItems.add(selectItemAll);

                    for (final YoutubeChannelDTO youtubeChannel : youtubeChannels) {
                        ParameterSelect.SelectItem
                            selectItem =
                            Parameter.Factory.createSelectItem(youtubeChannel.getTitle(), youtubeChannel.getChannelId());
                        selectItems.add(selectItem);
                    }
                }

            _filterableAspect = new FilterableAspect(selectItems);
            _aspects.put(Filterable.TYPE, _filterableAspect);
        }

        @Override
        public DataStream<YoutubeVideo> createDataStream() {
            YoutubeVideoDataStream youtubeVideoDataStream = new YoutubeVideoDataStream(youtubeConnectorServiceFacade);
            youtubeConnectorServiceFacade.initSearchRequest(_filterableAspect.getQuery(), _filterableAspect.getChannel());
            return youtubeVideoDataStream;
        }

        @Override
        public <A> A getAspect(StreamBuilderAspectType<A> aspectType) {
            return _aspects.get(aspectType);
        }
    }

    /**
     * Aspect to provide filters for a data stream.
     * Please see FirstSpirit API for more information.
     */
    public static class FilterableAspect implements Filterable {

        private ParameterText _query;
        private ParameterSelect _channel;

        private ParameterMap _filter;

        private FilterableAspect(List<ParameterSelect.SelectItem> selectItems) {
            if (selectItems != null && !selectItems.isEmpty()) {
                _channel = Parameter.Factory.createSelect("channelFilterSelect", selectItems, "all");
            }
            _query = Parameter.Factory.createText("query", "", "");
        }

        @Override
        public List<Parameter<?>> getDefinedParameters() {
            List<Parameter<?>> pList = new ArrayList<>();
            pList.add(_query);
            if (_channel != null) {
                pList.add(_channel);
            }

            return pList;
        }

        @Override
        public void setFilter(ParameterMap filter) {
            _filter = filter;
        }

        /**
         * Gets the query string.
         *
         * @return the query string
         */
        @Nullable
        String getQuery() {
            String query = _filter.get(_query);
            return !Strings.isEmpty(query) ? query : null;
        }

        /**
         * Gets the selected channel.
         *
         * @return the channel id
         */
        @Nullable
        String getChannel() {
            String channel = null;
            if (_channel != null) {
                channel = _filter.get(_channel);
            }
            return !Strings.isEmpty(channel) ? channel : null;
        }
    }
}
