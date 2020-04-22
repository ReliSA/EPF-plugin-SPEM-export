package org.eclipse.epf.export.pattern;

import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.epf.export.pattern.domain.PatternProject;
import org.eclipse.epf.export.pattern.domain.PatternRole;
import org.eclipse.epf.export.pattern.domain.PatternTask;
import org.eclipse.epf.export.pattern.domain.PatternWorkProduct;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ExportPatternXMLService implements IExportPatternSpecificService {
	
	ExportPatternData data;
	
	ExportPatternLogger logger = null;
	
	public ExportPatternXMLService(ExportPatternData data, ExportPatternLogger logger) {
		this.data = data;
		this.logger = logger;
	}
	
	public static ExportPatternXMLService getInstance(ExportPatternData data, ExportPatternLogger logger) {	
		return new ExportPatternXMLService(data, logger);
	}
	
	public void export(List<PatternProject> patternProjects) {
		this.logger.logMessage("Exportig patterns to XML.");
		
		for (PatternProject patternProject : patternProjects) {
			createXML(patternProject);
		}
	}
	
	public void createXML(PatternProject patternProject) {
		
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			Document doc = docBuilder.newDocument();
			
			Element rootElement = doc.createElement("pattern");
			
			for (PatternTask task : patternProject.getPatternTasks().values()) {
				Element taskElement = doc.createElement("task");
				taskElement.setAttribute("name", task.getName());
				taskElement.setAttribute("guid", task.getGuid());
				taskElement.setAttribute("amount", task.getAmount());
				
				List<PatternWorkProduct> outputs = task.getOutputs();
				for (PatternWorkProduct output : outputs) {
					Element oElement = doc.createElement("output");
					oElement.setAttribute("guid", output.getGuid());
					taskElement.appendChild(oElement);
				}
				
				List<PatternRole> performers = task.getPerformers();
				for (PatternRole role : performers) {
					Element roleElement = doc.createElement("performer");
					roleElement.setAttribute("guid", role.getGuid());
					taskElement.appendChild(roleElement);
				}
				
				rootElement.appendChild(taskElement);
			}
			
			for (PatternWorkProduct workProduct : patternProject.getPatternWorkProducts().values()) {
				Element wpElement = doc.createElement("work_product");
				wpElement.setAttribute("name", workProduct.getName());
				wpElement.setAttribute("guid", workProduct.getGuid());
				
				rootElement.appendChild(wpElement);
			}
			
			for (PatternRole role : patternProject.getPatternRoles().values()) {
				Element roleElement = doc.createElement("role");
				roleElement.setAttribute("name", role.getName());
				roleElement.setAttribute("guid", role.getGuid());
				
				rootElement.appendChild(roleElement);
			}
			
			doc.appendChild(rootElement);
			
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			
			String path = data.getDirectory() + "\\" + patternProject.getName() + ".xml";
			StreamResult result = new StreamResult(new File(path));
			transformer.transform(source, result);
			this.logger.logMessage("Process exported.");
			
		} catch (ParserConfigurationException e) {
			this.logger.logError("Error during XML export initialization.", e);
		} catch (TransformerConfigurationException e) {
			this.logger.logError("Error during XML export.", e);
		} catch (TransformerException e) {
			this.logger.logError("Error during XML export.", e);
		}
	}
	
}
