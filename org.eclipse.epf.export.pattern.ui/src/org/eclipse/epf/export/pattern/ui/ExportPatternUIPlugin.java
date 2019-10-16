package org.eclipse.epf.export.pattern.ui;

import org.eclipse.epf.common.ui.AbstractPlugin;
import org.osgi.framework.BundleContext;

/**
 * The Export Pattern UI plug-in class.
 * 
 * @author Lenka Simeckova
 * @since 1.0
 */
public class ExportPatternUIPlugin extends AbstractPlugin {

	// The shared instance.
	private static ExportPatternUIPlugin plugin;

	/**
	 * Creates a new instance.
	 */
	public ExportPatternUIPlugin() {
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
	public static ExportPatternUIPlugin getDefault() {
		return plugin;
	}

}
