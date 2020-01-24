package org.eclipse.epf.export.pattern;

import java.util.Collection;
import java.util.List;

public class ExportPatternData {
	
	private String fileName;
	
	private String directory;
	
	private String patternName;
	
	protected List selectedConfigs;
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}
	
	public String getPatternName() {
		return patternName;
	}

	public void setPatternName(String patternName) {
		this.patternName = patternName;
	}
	
	/**
	 * Gets the user selected method configurations.
	 * 
	 * @return a list of method configurations
	 */
	public List getSelectedConfigs() {
		return selectedConfigs;
	}
	
	/**
	 * Sets the user selected method configurations.
	 * 
	 * @param configs
	 *            a list of method configurations
	 */
	public void setSelectedConfigs(List configs) {
		selectedConfigs = configs;
	}
}
