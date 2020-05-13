package cz.zcu.kiv.epf.spade.export.pattern;

import org.eclipse.epf.export.services.PluginExportData;

public class ExportPatternConfig extends PluginExportData {
	
	public ExportPatternConfig() {
		super();
	}
	
	/** Target directory for export **/
	private String directory;
	
	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}
	
}
