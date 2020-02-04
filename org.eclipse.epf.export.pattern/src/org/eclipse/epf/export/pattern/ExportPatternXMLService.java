package org.eclipse.epf.export.pattern;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.relation.Role;
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
	
	List<Task> tasks;
	List<WorkProduct> workProducts;
	Set<CustomCategory> customCategories;
	List<MethodPackage> methodPackages;
	List<Process> processes;
	
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
		this.logger.logMessage("Exportig patterns.");
		
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
			
			tasks = ProcessUtil.getAllTasks(methodPlugin);
			
			workProducts = ProcessUtil.getAllWorkProducts(methodPlugin);
			
			processes = TngUtil.getAllProcesses(methodPlugin);
			
			customCategories = TngUtil.getAllCustomCategories(methodPlugin);
			this.logger.logMessage("custom categories");
			for (CustomCategory customCategory : customCategories) {
				this.logger.logMessage("name: " + customCategory.getName());
			}
			
			methodPackages =  methodPlugin.getMethodPackages();
			
			createXML(methodPlugin);
		}
		
		
		return;
	}
	
	/**
	 * 
	 * @param methodPlugin
	 */
	public void createXML(MethodPlugin methodPlugin) {
		
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			Document doc = docBuilder.newDocument();
			
			Element rootElement = doc.createElement("pattern");
			rootElement.setAttribute("name", methodPlugin.getName());
			
			for (Task task : tasks) {
//				this.logger.logMessage("appending task");
				Element taskElement = doc.createElement("task");
				taskElement.setAttribute("name", task.getName());
				taskElement.setAttribute("guid", task.getGuid());
				taskElement.setAttribute("main_description", task.getPresentation().getMainDescription());
				
				List<WorkProduct> mandatoryInputs = task.getMandatoryInput();
				for (WorkProduct mandatoryInput : mandatoryInputs) {
					Element miElement = doc.createElement("mandatory_input");
					miElement.setAttribute("guid", mandatoryInput.getGuid());
					taskElement.appendChild(miElement);
				}
				
				List<WorkProduct> optionalInputs = task.getOptionalInput();
				for (WorkProduct optionalInput : optionalInputs) {
					Element oiElement = doc.createElement("optional_input");
					oiElement.setAttribute("guid", optionalInput.getGuid());
					taskElement.appendChild(oiElement);
				}
				
				List<WorkProduct> outputs = task.getOutput();
				for (WorkProduct output : outputs) {
					Element oElement = doc.createElement("output");
					oElement.setAttribute("guid", output.getGuid());
					taskElement.appendChild(oElement);
				}
				
				List<org.eclipse.epf.uma.Role> performers = task.getPerformedBy();
				for (org.eclipse.epf.uma.Role role : performers) {
					Element roleElement = doc.createElement("performer");
					roleElement.setAttribute("name", role.getName());
					roleElement.setAttribute("guid", role.getGuid());
					taskElement.appendChild(roleElement);
				}
				
				rootElement.appendChild(taskElement);
			}
			
			for (WorkProduct workProduct : workProducts) {
				Element wpElement = doc.createElement("work_product");
				wpElement.setAttribute("name", workProduct.getName());
				wpElement.setAttribute("guid", workProduct.getGuid());
				wpElement.setAttribute("main_description", workProduct.getPresentation().getMainDescription());
				
				rootElement.appendChild(wpElement);
			}
			
			for (Process process : processes) {
				Element processElement = doc.createElement("process");
				processElement.setAttribute("name", process.getName());
				processElement.setAttribute("ordering_guide", process.getOrderingGuide());
				
				ComposedAdapterFactory adapterFactory = null;
				
				adapterFactory = TngAdapterFactory.INSTANCE
						.createWBSComposedAdapterFactory();
				
				ITreeContentProvider contentProvider = new AdapterFactoryContentProvider(
						adapterFactory);
				
				if (process.getBreakdownElements().size() > 0) {
					inspectProcess(contentProvider, process, doc, processElement);
				}
				
				rootElement.appendChild(processElement);
			}
			doc.appendChild(rootElement);
			
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			
			String path = data.getDirectory() + "\\" + methodPlugin.getName() + ".xml";
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
	
	/**
	 * 
	 * @param contentProvider
	 * @param breakdownElement
	 * @param doc
	 * @param xmlElement
	 */
	private void inspectProcess(ITreeContentProvider contentProvider, BreakdownElement breakdownElement, Document doc, Element xmlElement) {
		Object[] elements = contentProvider.getElements(breakdownElement);
		for (int i = 0; i < elements.length; i++) {
			Object element = elements[i];
			Element beElement = doc.createElement("breakdown_element");
			xmlElement.appendChild(beElement);
			
			if (element instanceof RoleDescriptorWrapperItemProvider) {
				RoleDescriptorWrapperItemProvider provider = (RoleDescriptorWrapperItemProvider) element;
				Object value = provider.getValue();
				if (value instanceof RoleDescriptor) {
					beElement.setAttribute("name", ((RoleDescriptor) value).getName());
					beElement.setAttribute("guid", ((RoleDescriptor) value).getGuid());
					beElement.setAttribute("type", "RoleDescriptor");
				}
			} else if (element instanceof RoleDescriptor) {
				beElement.setAttribute("name", ((RoleDescriptor) element).getName());
				beElement.setAttribute("guid", ((RoleDescriptor) element).getGuid());
				beElement.setAttribute("type", "RoleDescriptor");
			} else if (element instanceof BreakdownElementWrapperItemProvider) {
				BreakdownElementWrapperItemProvider provider = (BreakdownElementWrapperItemProvider) element;
				Object value = provider.getValue();
				if (value instanceof WorkBreakdownElement) {
					beElement.setAttribute("name", ((WorkBreakdownElement) value).getName());
					beElement.setAttribute("guid", ((WorkBreakdownElement) value).getGuid());
					beElement.setAttribute("type", "WorkBreakdownElement");
					inspectProcess(contentProvider,
							(WorkBreakdownElement) value, doc, beElement);
				}
			} else if (element instanceof WorkBreakdownElement) {
				beElement.setAttribute("name", ((WorkBreakdownElement) element).getName());
				beElement.setAttribute("guid", ((WorkBreakdownElement) element).getGuid());
				beElement.setAttribute("type", "WorkBreakdownElement");
				inspectProcess(contentProvider,
						(WorkBreakdownElement) element, doc, beElement);
			}
		}
	}
	
}
