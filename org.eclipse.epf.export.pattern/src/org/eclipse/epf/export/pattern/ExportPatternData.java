package org.eclipse.epf.export.pattern;

import org.eclipse.epf.export.services.PluginExportData;

public class ExportPatternData extends PluginExportData {
	
	private String directory;
	
	private boolean exportSql;
	
	private boolean exportXml;
	
	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public boolean isExportSql() {
		return exportSql;
	}

	public void setExportSql(boolean exportSql) {
		this.exportSql = exportSql;
	}

	public boolean isExportXml() {
		return exportXml;
	}

	public void setExportXml(boolean exportXml) {
		this.exportXml = exportXml;
	}
	
}
