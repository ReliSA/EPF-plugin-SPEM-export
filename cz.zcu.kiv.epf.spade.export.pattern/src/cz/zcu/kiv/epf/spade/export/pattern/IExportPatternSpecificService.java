package cz.zcu.kiv.epf.spade.export.pattern;

import java.util.List;

import cz.zcu.kiv.epf.spade.export.pattern.domain.PatternProject;

public interface IExportPatternSpecificService {

	public void export(List<PatternProject> patternProjects) throws ExportPatternServiceException;

}
