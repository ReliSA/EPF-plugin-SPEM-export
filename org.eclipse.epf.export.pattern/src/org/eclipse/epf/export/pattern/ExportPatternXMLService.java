package org.eclipse.epf.export.pattern;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.management.relation.Role;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.epf.export.pattern.domain.PatternPhase;
import org.eclipse.epf.export.pattern.domain.PatternProject;
import org.eclipse.epf.export.pattern.domain.PatternTask;
import org.eclipse.epf.export.pattern.domain.PatternWorkProduct;
import org.eclipse.epf.library.edit.TngAdapterFactory;
import org.eclipse.epf.library.edit.process.BreakdownElementWrapperItemProvider;
import org.eclipse.epf.library.edit.process.RoleDescriptorWrapperItemProvider;
import org.eclipse.epf.library.edit.util.ConfigurableComposedAdapterFactory;
import org.eclipse.epf.library.edit.util.ModelStructure;
import org.eclipse.epf.library.edit.util.ProcessScopeUtil;
import org.eclipse.epf.library.edit.util.ProcessUtil;
import org.eclipse.epf.library.edit.util.TngUtil;
import org.eclipse.epf.uma.Activity;
import org.eclipse.epf.uma.BreakdownElement;
import org.eclipse.epf.uma.CustomCategory;
import org.eclipse.epf.uma.DeliveryProcess;
import org.eclipse.epf.uma.Kind;
import org.eclipse.epf.uma.MethodConfiguration;
import org.eclipse.epf.uma.MethodElementProperty;
import org.eclipse.epf.uma.MethodPackage;
import org.eclipse.epf.uma.MethodPlugin;
import org.eclipse.epf.uma.Phase;
import org.eclipse.epf.uma.Process;
import org.eclipse.epf.uma.RoleDescriptor;
import org.eclipse.epf.uma.Task;
import org.eclipse.epf.uma.TaskDescriptor;
import org.eclipse.epf.uma.UmaFactory;
import org.eclipse.epf.uma.UmaPackage;
import org.eclipse.epf.uma.WorkBreakdownElement;
import org.eclipse.epf.uma.WorkOrder;
import org.eclipse.epf.uma.WorkProduct;
import org.eclipse.epf.uma.WorkProductDescriptor;
import org.eclipse.epf.uma.util.UmaUtil;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ExportPatternXMLService implements IExportPatternService {
	
	ExportPatternData data;
	
	ExportPatternLogger logger = null;
	
	static List<PatternProject> patternProjects = new ArrayList<PatternProject>();
	
	public ExportPatternXMLService(ExportPatternData data) {
		this.data = data;
		logger = new ExportPatternLogger(new File(System.getProperty("user.dir")), "xml");
	}
	
	public static ExportPatternXMLService getInstance(ExportPatternData data) {	
		return new ExportPatternXMLService(data);
	}
	
	/**
	 * 
	 */
	public void export() {
		this.logger.logMessage("Exportig patterns to XML.");
		
//		MethodConfiguration config = selectedPlugins.getDefaultContext();
//		
//		if (config == null) {
//			config = ProcessScopeUtil.getInstance().loadScope(selectedPlugins);
//		}
		
		for (MethodPlugin methodPlugin : data.getSelectedPlugins()) {
			
			
			this.logger.logMessage("core content");
			MethodPackage pkg_core_content = UmaUtil.findMethodPackage(methodPlugin,
					ModelStructure.DEFAULT.coreContentPath);
			this.logger.logMessage("name: " + pkg_core_content.getName());
			
			 UmaUtil.findMethodPackage(methodPlugin,
						ModelStructure.DEFAULT.processContributionPath);
			
			Set<CustomCategory> customCategories = TngUtil.getAllCustomCategories(methodPlugin);
			this.logger.logMessage("custom categories");
			for (CustomCategory customCategory : customCategories) {
				this.logger.logMessage("name: " + customCategory.getName());
			}
			
			this.logger.logMessage("method packages");
			List<MethodPackage> methodPackages =  methodPlugin.getMethodPackages();
			for (MethodPackage methodPackage : methodPackages) {
				this.logger.logMessage(methodPackage.getName());
			}
			
			TngUtil.getDisciplineCategoriesItemProvider(methodPlugin);
			
			MethodPackage pkg_disciplines = UmaUtil.findMethodPackage(methodPlugin,
					ModelStructure.DEFAULT.disciplineDefinitionPath);
			
			PatternProject patternProject = ExportPatternMapService.map(methodPlugin, this.logger);
			createXML(methodPlugin, patternProject);
		}
		
		return;
	}
	
	/**
	 * 
	 * @param methodPlugin
	 */
	public void createXML(MethodPlugin methodPlugin, PatternProject patternProject) {
		this.logger.logMessage(String.valueOf(patternProject.getPatternTasks().values().size()));
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(PatternProject.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			 
		    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		    
		    //Marshal the employees list in console
		    jaxbMarshaller.marshal(patternProject, System.out);

		    //Marshal the employees list in file
			String path = data.getDirectory() + "\\" + methodPlugin.getName() + ".xml";
			this.logger.logMessage(path);
		    jaxbMarshaller.marshal(patternProject, new File(path));
		} catch (JAXBException e) {
			this.logger.logError(e.getMessage(), e);
			e.printStackTrace();
		}
	    
	}
	
}
