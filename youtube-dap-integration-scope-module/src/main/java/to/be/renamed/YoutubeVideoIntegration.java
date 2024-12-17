package to.be.renamed;

import de.espirit.firstspirit.module.Module;
import de.espirit.firstspirit.module.ServerEnvironment;
import de.espirit.firstspirit.module.descriptor.ModuleDescriptor;

import com.espirit.moddev.components.annotations.ModuleComponent;

/**
 * The FirstSpirit Youtube video integration module.
 */
@ModuleComponent()
public class YoutubeVideoIntegration implements Module {

	@Override
	public void init(ModuleDescriptor moduleDescriptor, ServerEnvironment serverEnvironment) {
	}

	@Override
	public void installed() {
		// Nothing needs to be done here.
	}

	@Override
	public void uninstalling() {
		// Nothing needs to be done here.
	}

	@Override
	public void updated(String s) {
		// Nothing needs to be done here.
	}
}
