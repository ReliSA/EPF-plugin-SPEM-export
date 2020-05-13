package cz.zcu.kiv.epf.spade.export.pattern;

import java.io.File;
import java.util.List;

import cz.zcu.kiv.epf.spade.export.pattern.domain.PatternProject;

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
	
	/**
	 * 
	 */
	public void export() {
		ExportPatternMapService exportPatternMapService = ExportPatternMapService.getInstance(logger);
		List<PatternProject> patternProjects = exportPatternMapService.map(data.getSelectedPlugins(), this.logger);
		ExportPatternSQLService service = ExportPatternSQLService.getInstance(data, logger);
		service.export(patternProjects);
		
	}

}
