package org.eclipse.epf.export.pattern;

import java.util.List;

import org.eclipse.epf.export.pattern.domain.PatternProject;

public interface IExportPatternSpecificService {

	public void export(List<PatternProject> patternProjects) throws ExportPatternServiceException;

}
