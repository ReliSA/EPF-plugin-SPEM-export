package org.eclipse.epf.export.pattern;

import java.io.File;
import org.eclipse.epf.dataexchange.util.FileLogger;


/**
 * Logger class for pattern export.
 * 
 * @author Lenka Simeckova
 * @since 1.0
 *
 */
public class ExportPatternLogger extends FileLogger {

	private static final String LOGS_FOLDER = "logs\\export\\pattern\\"; //$NON-NLS-1$
	
	/**
	 * Creates new instance of logger.
	 */
	public ExportPatternLogger(File logRoot, String specFolder) {
		super(new File(logRoot, LOGS_FOLDER + specFolder));
	}
}
