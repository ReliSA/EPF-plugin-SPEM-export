package cz.zcu.kiv.epf.spade.export.pattern.ui;

import org.eclipse.osgi.util.NLS;

/**
 * The Export Pattern UI message resource bundle accessor class.
 * 
 * @author Lenka Simeckova
 * @since 1.0
 */
public final class ExportPatternUIResources extends NLS {

	private static String BUNDLE_NAME = ExportPatternUIResources.class.getPackage()
			.getName()
			+ ".Resources"; //$NON-NLS-1$

	private ExportPatternUIResources() {
		// Do not instantiate
	}

	public static String exportPatternWizard_title;
	public static String selectExportDirWizardPage_title;
	public static String selectExportDirWizardPage_text;
	public static String selectedProcessLabel_text;
	public static String directoryLabel_text;
	public static String browseButton_text;
	public static String invalidDirectory_error;
	public static String overwriteText_msg1;
	public static String exportingXML_text;
	public static String exportXMLWizard_reviewLog;

	static {
		NLS.initializeMessages(BUNDLE_NAME, ExportPatternUIResources.class);
	}

}