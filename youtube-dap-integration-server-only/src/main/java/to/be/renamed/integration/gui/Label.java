package to.be.renamed.integration.gui;

/**
 * Stores the gui resource bundle keys
 */
public enum Label {
    TITLE("projectApp.title.label"),
    API_KEY("projectApp.apikey.label"),
    CHANNEL_IDS("projectApp.channelids.label"),
    VIDEO_CACHE_DURATION("projectApp.videocacheduration.label"),
    CHECK_SETTINGS("projectApp.checksettings.label"),
    IMPORT_SAMPLE("projectApp.importsample.label"),
    CLEAR_VIDEO_CACHE("projectApp.clearvideocache.label");

    private final String resourceBundleKey;

    Label(final String resourceBundleKey) {
        this.resourceBundleKey = resourceBundleKey;
    }

    public String getResourceBundleKey() {
        return resourceBundleKey;
    }
}
