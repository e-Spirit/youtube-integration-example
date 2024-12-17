package to.be.renamed.integration;

import de.espirit.common.base.Logging;
import de.espirit.firstspirit.access.Language;
import de.espirit.firstspirit.agency.LanguageAgent;
import de.espirit.firstspirit.agency.UIAgent;
import de.espirit.firstspirit.module.Configuration;
import de.espirit.firstspirit.module.ProjectEnvironment;
import to.be.renamed.connector.YoutubeConnectorServiceFacade;

import to.be.renamed.integration.gui.ConfigurationAppPanel;
import to.be.renamed.projectconfig.access.ProjectAppConfigurationServiceFacade;

import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Set;

import javax.swing.*;


/**
 * The FirstSpirit Youtube integration project config.
 */
public class YoutubeIntegrationProjectConfig implements Configuration<ProjectEnvironment> {
    private ProjectEnvironment projectEnvironment;
    private YoutubeIntegrationConfig configuration;
    private ConfigurationAppPanel configurationPanel;
    // May not be necessary
    private Language language;
    private ProjectAppConfigurationServiceFacade moduleConfigurationAccessor;
    private YoutubeConnectorServiceFacade youtubeConnectorServiceFacade;

    @Override
    public void init(final String s, final String s1, final ProjectEnvironment projectEnvironment) {
        this.projectEnvironment = projectEnvironment;
        moduleConfigurationAccessor = new ProjectAppConfigurationServiceFacade(projectEnvironment.getBroker(), projectEnvironment.getProjectId());
        youtubeConnectorServiceFacade = new YoutubeConnectorServiceFacade(projectEnvironment.getBroker());

        final UIAgent uiAgent = projectEnvironment.getBroker().requestSpecialist(UIAgent.TYPE);
        language = uiAgent != null ? uiAgent.getDisplayLanguage() : null;

        if (language == null) {
            language = projectEnvironment.getBroker().requireSpecialist(LanguageAgent.TYPE).getMasterLanguage();
        }
    }

    @Override
    public boolean hasGui() {
        return true;
    }

    @Override
    public @Nullable JComponent getGui(final Frame frame) {
        if (configurationPanel == null) {
            configurationPanel = new ConfigurationAppPanel(configuration, projectEnvironment.getBroker());
        }
        if (frame != null) {
            return (JComponent) frame.add(configurationPanel.getConfigurationPanel());
        } else {
            return configurationPanel.getConfigurationPanel();
        }
    }

    @Override
    public void load() {
        Logging.logDebug("Loading project app configuration", getClass());
        configuration = moduleConfigurationAccessor.loadConfiguration();
    }

    @Override
    public void store() {
        Logging.logDebug("Storing project app configuration", getClass());
        final YoutubeIntegrationConfig updatedConfiguration = configurationPanel.getValue();
        moduleConfigurationAccessor.storeConfiguration(updatedConfiguration);
        configuration = updatedConfiguration;
        youtubeConnectorServiceFacade.removeConnector();
    }

    // Unimplemented because not needed
    @Override
    public Set<String> getParameterNames() {
        return null;
    }

    // Unimplemented because not needed
    @Override
    public @Nullable String getParameter(final String s) {
        return null;
    }

    @Override
    public ProjectEnvironment getEnvironment() {
        return projectEnvironment;
    }
}
