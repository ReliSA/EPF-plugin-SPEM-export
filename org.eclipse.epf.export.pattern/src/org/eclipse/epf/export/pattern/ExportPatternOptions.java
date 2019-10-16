package org.eclipse.epf.export.pattern;

import org.eclipse.epf.export.ExportProcessOptions;

/**
 * The export Microsoft Project options.
 * 
 * @author Lenka Simeckova
 * @since 1.0
 */
public class ExportPatternOptions extends ExportProcessOptions {

	private String patternName;

	public String getPatternName() {
		return patternName;
	}

	public void setPatternName(String patternName) {
		this.patternName = patternName;
	}

}
