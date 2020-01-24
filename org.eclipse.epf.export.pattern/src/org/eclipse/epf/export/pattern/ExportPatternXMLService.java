package org.eclipse.epf.export.pattern;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.epf.library.edit.util.ProcessScopeUtil;
import org.eclipse.epf.uma.MethodConfiguration;
import org.eclipse.epf.uma.Process;
import org.eclipse.epf.uma.WorkOrder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ExportPatternXMLService implements IExportPatternService {
	
	private Set<WorkOrder> successors;
	
	ExportPatternData data;
	
	ExportPatternLogger logger = null;
	
	public ExportPatternXMLService(ExportPatternData data) {
		this.data = data;
		logger = new ExportPatternLogger(new File(System.getProperty("user.dir")));
	}
	
	public static ExportPatternXMLService getInstance(ExportPatternData data) {	
		return new ExportPatternXMLService(data);
	}
	
	public void export(Process process) {
		this.logger.logMessage(String.format("Exportig process %s", process.getName()));
		
		MethodConfiguration config = process.getDefaultContext();
		
		if (config == null) {
			config = ProcessScopeUtil.getInstance().loadScope(process);
		}
		
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			Document doc = docBuilder.newDocument();
			
			Element rootElement = doc.createElement("pattern");
			doc.appendChild(rootElement);
			
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("C:\\Users\\user\\Desktop\\exportfileeclipse.xml"));
			transformer.transform(source, result);
			this.logger.logMessage("Process exported.");
			
		} catch (ParserConfigurationException e) {
			this.logger.logError("Error during XML export initialization.", e);
		} catch (TransformerConfigurationException e) {
			this.logger.logError("Error during XML export.", e);
		} catch (TransformerException e) {
			this.logger.logError("Error during XML export.", e);
		}
		
		return;
	}
	
}
