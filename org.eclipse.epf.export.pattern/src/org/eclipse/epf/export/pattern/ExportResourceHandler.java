package org.eclipse.epf.export.pattern;

import java.io.File;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.epf.dataexchange.util.BaseResourceHandler;
import org.eclipse.epf.dataexchange.util.UrlInfo;
import org.eclipse.epf.uma.MethodPlugin;

/**
 * The resource handler for content in the exported library.
 * 
 * @author Jinhua Xi
 * @since 1.0
 */
public class ExportResourceHandler extends BaseResourceHandler {
	
	private static boolean localDebug = false;
	/**
	 * Creates a new instance.
	 */
	public ExportResourceHandler(File sourceLibRoot, File targetLibRoot) {
		super(sourceLibRoot, targetLibRoot);
	}

	/**
	 * resolve the url. For XML export, we should reserve the resource locations.
	 */
	public UrlInfo resolveFileUrl(Object owner, String srcUrl) throws Exception {
		return null;
	}

	public void copyResource(String arg0, EObject arg1, MethodPlugin arg2) {
		return;	
	}

}
