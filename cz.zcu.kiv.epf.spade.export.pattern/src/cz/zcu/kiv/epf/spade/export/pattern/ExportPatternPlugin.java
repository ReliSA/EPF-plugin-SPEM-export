package cz.zcu.kiv.epf.spade.export.pattern;

import org.eclipse.epf.common.ui.AbstractPlugin;
import org.osgi.framework.BundleContext;

/**
 * The Export Pattern plug-in class.
 * 
 * @author Lenka Simeckova
 * @since 1.0
 */
public class ExportPatternPlugin extends AbstractPlugin {

	/** The shared instance. **/
	private static ExportPatternPlugin plugin;

	/**
	 * Creates a new instance.
	 */
	public ExportPatternPlugin() {
		super();
		plugin = this;
	}

	/**
	 * @see AbstractPlugin#start(BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * @see AbstractPlugin#stop(BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static ExportPatternPlugin getDefault() {
		return plugin;
	}

}
