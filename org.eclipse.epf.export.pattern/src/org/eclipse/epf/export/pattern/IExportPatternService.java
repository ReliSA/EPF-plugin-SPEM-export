package org.eclipse.epf.export.pattern;

public interface IExportPatternService {
	
	public void export(org.eclipse.epf.uma.Process process) throws ExportPatternServiceException;

}
