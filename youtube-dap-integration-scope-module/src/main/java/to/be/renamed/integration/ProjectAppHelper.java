package to.be.renamed.integration;

import de.espirit.firstspirit.access.project.Project;
import de.espirit.firstspirit.agency.ModuleAdminAgent;
import de.espirit.firstspirit.agency.SpecialistsBroker;

import java.util.Collection;

public class ProjectAppHelper {
    public static final String MODULE_NAME = "YouTube-DAP-Integration";
    public static final String MODULE_DISPLAY_NAME = "YouTube DAP Integration";

    public static final String PROJECT_APP_NAME = MODULE_NAME + "-ProjectApp";
    public static final String PROJECT_APP_DISPLAY_NAME = MODULE_DISPLAY_NAME + " - Project App";

    public static final String WEBAPP_NAME = MODULE_NAME + "-WebApp";
    public static final String WEBAPP_DISPLAY_NAME = MODULE_DISPLAY_NAME + " - Web App";

    public static final String PROJECT_APP_CONFIG_FILE = "configuration.json";

    private ProjectAppHelper() {}

    public  static boolean isInstalled(final SpecialistsBroker broker, final long projectId) {
        final ModuleAdminAgent moduleAdminAgent = broker.requireSpecialist(ModuleAdminAgent.TYPE);
        final Collection<Project> projectAppUsages =
            moduleAdminAgent.getProjectAppUsages(MODULE_NAME, PROJECT_APP_NAME);

        return projectAppUsages.stream().anyMatch(project -> project.getId() == projectId);
    }
}
