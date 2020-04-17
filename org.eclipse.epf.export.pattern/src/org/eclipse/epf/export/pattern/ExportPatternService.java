package org.eclipse.epf.export.pattern;

import java.io.File;

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
		ExportPatternSQLService service = ExportPatternSQLService.getInstance(data, logger);
		service.export();
	}

}
