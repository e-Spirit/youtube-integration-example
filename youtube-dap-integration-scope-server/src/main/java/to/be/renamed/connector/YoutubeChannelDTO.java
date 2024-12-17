package to.be.renamed.connector;

import java.io.Serializable;

public class YoutubeChannelDTO implements Serializable {
    private static final long serialVersionUID = -6099056995705891792L;
    private final String title;
    private final String channelId;

    private YoutubeChannelDTO() {
        title = null;
        channelId = null;
    }
    public YoutubeChannelDTO(final String title, final String channelId) {
        this.title = title;
        this.channelId = channelId;
    }

    public String getTitle() {
        return title;
    }

    public String getChannelId() {
        return channelId;
    }
}
