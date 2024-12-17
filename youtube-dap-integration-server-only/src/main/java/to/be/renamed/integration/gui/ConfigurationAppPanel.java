package to.be.renamed.integration.gui;

import de.espirit.common.base.Logging;
import de.espirit.firstspirit.access.store.Store;
import de.espirit.firstspirit.access.store.StoreElement;
import de.espirit.firstspirit.access.store.templatestore.SectionTemplate;
import de.espirit.firstspirit.access.store.templatestore.SectionTemplates;
import de.espirit.firstspirit.access.store.templatestore.TemplateStoreRoot;
import de.espirit.firstspirit.agency.ProjectAgent;
import de.espirit.firstspirit.agency.SpecialistsBroker;
import de.espirit.firstspirit.agency.StoreAgent;
import to.be.renamed.connector.YoutubeConnectorServiceFacade;
import to.be.renamed.integration.YoutubeIntegrationConfig;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;

import javax.swing.*;

import static to.be.renamed.integration.gui.Label.API_KEY;
import static to.be.renamed.integration.gui.Label.CHANNEL_IDS;
import static to.be.renamed.integration.gui.Label.CHECK_SETTINGS;
import static to.be.renamed.integration.gui.Label.CLEAR_VIDEO_CACHE;
import static to.be.renamed.integration.gui.Label.IMPORT_SAMPLE;
import static to.be.renamed.integration.gui.Label.TITLE;
import static to.be.renamed.integration.gui.Label.VIDEO_CACHE_DURATION;

/**
 * Configuration panel for the project configuration.
 */
public class ConfigurationAppPanel extends AbstractConfigurationPanel<YoutubeIntegrationConfig> {
    private final JTextField apiKey;
    private final JTextField channelIds;
    private final JTextField videoCacheDuration;

    private final SpecialistsBroker broker;

    /**
     * Creates a configuration panel for the project app configuration.
     * Combines general, bridge and report tabs.
     * @param youtubeIntegrationConfig The current configuration values
     * @param broker The broker for requesting services or providers
     */
    public ConfigurationAppPanel(final YoutubeIntegrationConfig youtubeIntegrationConfig, SpecialistsBroker broker) {
        super();

        this.broker = broker;

        JLabel title = new JLabel();
        addComponent(title, TITLE);

        apiKey = new JTextField(youtubeIntegrationConfig.getApiKey(), TEXTFIELD_COLUMNS);
        addComponent(apiKey, API_KEY);

        channelIds = new JTextField(youtubeIntegrationConfig.getChannelIdsAsString(), TEXTFIELD_COLUMNS);
        addComponent(channelIds, CHANNEL_IDS);

        videoCacheDuration = new JTextField(youtubeIntegrationConfig.getVideoCacheDurationAsString(), TEXTFIELD_COLUMNS);
        addComponent(videoCacheDuration, VIDEO_CACHE_DURATION);

        addButton(CHECK_SETTINGS, checkSettings);
        addButton(IMPORT_SAMPLE, importSample, broker);
        addButton(CLEAR_VIDEO_CACHE, clearVideoCache);
    }

    private void checkSettingsWithService(YoutubeIntegrationConfig config) throws IOException {
        YoutubeConnectorServiceFacade youtubeConnectorServiceFacade = new YoutubeConnectorServiceFacade(broker);
        youtubeConnectorServiceFacade.checkSettings(config);
    }

    private boolean clearCache() {
        YoutubeConnectorServiceFacade youtubeConnectorServiceFacade = new YoutubeConnectorServiceFacade(broker);
        return youtubeConnectorServiceFacade.clearCache(broker.requireSpecialist(ProjectAgent.TYPE).getId());
    }

    public JComponent getConfigurationPanel() {
        return getPanel();
    }

    /**
     * Provides the ProjectAppConfiguration based on the panels input fields of each tab.
     * @return The values from the panels input fields of each tab packed as a ProjectAppConfiguration object.
     */
    @Override
    public YoutubeIntegrationConfig getValue() {
        return YoutubeIntegrationConfig.fromStrings(apiKey.getText(), channelIdsToList(channelIds.getText()), videoCacheDuration.getText());
    }

    private List<String> channelIdsToList(String channelIdsString) {
        return Arrays.stream(channelIdsString.split(",")).map(String::trim).collect(Collectors.toList());
    }

    Consumer<ActionEvent> checkSettings = e -> {
        String apiKey = getValue().getApiKey();
        if (apiKey != null && !apiKey.isEmpty()) {
            String channelIds = getValue().getChannelIdsAsString();

            try {
                List<String> channelIdList = new ArrayList<>();
                if (!channelIds.isEmpty()) {
                    channelIdList = channelIdsToList(channelIds);
                }
                checkSettingsWithService(new YoutubeIntegrationConfig(apiKey, channelIdList, 0));
                JOptionPane.showMessageDialog(null, "Connection successful!");
            } catch (Exception exception) {
                Logging.logError(exception.getMessage(), exception, getClass());
                JOptionPane.showMessageDialog(null, exception.getMessage());
            }
        }
    };

    BiConsumer<ActionEvent, SpecialistsBroker> importSample = (e, broker) -> {
        ZipFile zipFile = null;
        try {
            File tempFile = File.createTempFile("export_youtube_video", ".zip");
            tempFile.deleteOnExit();

            Files.copy(getClass().getResourceAsStream("/files/export_youtube_video.zip"), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            zipFile = new ZipFile(tempFile);

            TemplateStoreRoot templateStore = (TemplateStoreRoot) broker.requireSpecialist(StoreAgent.TYPE).getStore(Store.Type.TEMPLATESTORE);
            SectionTemplates sectionTemplates = templateStore.getSectionTemplates();
            StoreElement element = sectionTemplates.importStoreElement(zipFile, null);

            if (element instanceof SectionTemplate) {
                SectionTemplate sectionTemplate = (SectionTemplate) element;
                sectionTemplate.setLock(true);
                sectionTemplate.save();
                sectionTemplate.setLock(false);
                String message = String.format("Sample SectionTemplate successfully imported! (uid = %s)", sectionTemplate.getUid());
                JOptionPane.showMessageDialog(null, message);
            }

        } catch (Exception exception) {
            Logging.logError(exception.getMessage(), exception, getClass());
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException exception) {
                    Logging.logError("Could not close zip file.", exception, getClass());
                }
            }
        }
    };

    Consumer<ActionEvent> clearVideoCache = e -> {
        if (clearCache()) {
            JOptionPane.showMessageDialog(null, "Cache cleared!");
        } else {
            JOptionPane.showMessageDialog(null, "Cache could not be cleared");
        }
    };
}
