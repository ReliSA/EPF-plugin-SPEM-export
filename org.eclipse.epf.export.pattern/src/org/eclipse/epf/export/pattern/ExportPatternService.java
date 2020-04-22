package org.eclipse.epf.export.pattern;

import java.io.File;
import java.util.List;

import org.eclipse.epf.export.pattern.domain.PatternProject;

public class ExportPatternService {
	
	ExportPatternData data;
	
	ExportPatternLogger logger;
	
	public ExportPatternService(ExportPatternData data) {
		this.data = data;
		logger = new ExportPatternLogger(new File(System.getProperty("user.dir")));
	}
	
	public static ExportPatternService getInstance(ExportPatternData data) {	
		return new ExportPatternService(data);
	}
	
	public void export() {
		List<PatternProject> patternProjects = ExportPatternMapService.map(data.getSelectedPlugins(), this.logger);
		
		if (data.isExportSql()) {
			ExportPatternSQLService service = ExportPatternSQLService.getInstance(data, logger);
			service.export(patternProjects);
		}
		
		if (data.isExportXml()) {
			ExportPatternXMLService service = ExportPatternXMLService.getInstance(data, logger);
			service.export(patternProjects);
		}
		
	}

}
