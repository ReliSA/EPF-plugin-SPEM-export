package org.eclipse.epf.export.pattern;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.epf.export.pattern.domain.PatternDiscipline;
import org.eclipse.epf.export.pattern.domain.PatternPhase;
import org.eclipse.epf.export.pattern.domain.PatternProject;
import org.eclipse.epf.export.pattern.domain.PatternRole;
import org.eclipse.epf.export.pattern.domain.PatternTask;
import org.eclipse.epf.export.pattern.domain.PatternWorkProduct;
import org.eclipse.epf.library.edit.TngAdapterFactory;
import org.eclipse.epf.library.edit.category.DisciplineCategoriesItemProvider;
import org.eclipse.epf.library.edit.process.BreakdownElementWrapperItemProvider;
import org.eclipse.epf.library.edit.process.RoleDescriptorWrapperItemProvider;
import org.eclipse.epf.library.edit.util.ProcessUtil;
import org.eclipse.epf.library.edit.util.TngUtil;
import org.eclipse.epf.uma.BreakdownElement;
import org.eclipse.epf.uma.Discipline;
import org.eclipse.epf.uma.MethodPlugin;
import org.eclipse.epf.uma.Phase;
import org.eclipse.epf.uma.Process;
import org.eclipse.epf.uma.RoleDescriptor;
import org.eclipse.epf.uma.Task;
import org.eclipse.epf.uma.WorkBreakdownElement;
import org.eclipse.epf.uma.WorkProduct;
import org.eclipse.jface.viewers.ITreeContentProvider;

public class ExportPatternMapService {
	
	private static HashMap<String, PatternWorkProduct> patternWorkProducts = new HashMap<String, PatternWorkProduct>();
	
	private static HashMap<String, PatternTask> patternTasks = new HashMap<String, PatternTask>();
	
	private static HashMap<String, PatternRole> patternRoles = new HashMap<String, PatternRole>();
	
	private static HashMap<String, PatternPhase> patternPhases = new HashMap<String, PatternPhase>();

public static PatternProject map(MethodPlugin methodPlugin) {
	
	patternWorkProducts.clear();
	patternTasks.clear();
	patternRoles.clear();
	patternPhases.clear();
	
			PatternProject patternProject = new PatternProject();
			
			DisciplineCategoriesItemProvider disciplineProvider = (DisciplineCategoriesItemProvider) TngUtil.getDisciplineCategoriesItemProvider(methodPlugin);
			Collection<Discipline> disciplines = disciplineProvider.getChildren(disciplineProvider);
			for (Discipline discipline : disciplines) {
				PatternDiscipline patternDiscipline = new PatternDiscipline();
				patternDiscipline.setName(discipline.getName());
			}
			
			List<WorkProduct> workProducts = ProcessUtil.getAllWorkProducts(methodPlugin);
			for (WorkProduct workProduct : workProducts) {				
				PatternWorkProduct patternWorkProduct = new PatternWorkProduct();
				patternWorkProduct.setGuid(workProduct.getGuid());
				patternWorkProduct.setName(workProduct.getName());
				patternWorkProduct.setMainDescription(workProduct.getPresentation().getMainDescription());
				patternWorkProducts.put(patternWorkProduct.getGuid(), patternWorkProduct);
			}
			
			List<Task> tasks = ProcessUtil.getAllTasks(methodPlugin);
			for (Task task : tasks) {
				PatternTask patternTask = new PatternTask();
				patternTask.setGuid(task.getGuid());
				patternTask.setName(task.getName());
				patternTask.setMainDescription(task.getPresentation().getMainDescription());
				
				patternTasks.put(patternTask.getGuid(), patternTask);
				
				List<WorkProduct> mandatoryInputs = task.getMandatoryInput();
				for (WorkProduct mandatoryInput : mandatoryInputs) {
					patternTask.getInputs().add(patternWorkProducts.get(mandatoryInput.getGuid()));
				}
				
				List<WorkProduct> optionalInputs = task.getOptionalInput();
				for (WorkProduct optionalInput : optionalInputs) {
					patternTask.getOptionalInputs().add(patternWorkProducts.get(optionalInput.getGuid()));
				}
				
				List<WorkProduct> outputs = task.getOutput();
				for (WorkProduct output : outputs) {
					patternTask.getOutputs().add(patternWorkProducts.get(output.getGuid()));
				}
				
//				List<org.eclipse.epf.uma.Role> performers = task.getPerformedBy();
//				for (org.eclipse.epf.uma.Role role : performers) {
//					Element roleElement = doc.createElement("performer");
//					roleElement.setAttribute("name", role.getName());
//					roleElement.setAttribute("guid", role.getGuid());
//					taskElement.appendChild(roleElement);
//				}
			}
			
			List<Process> processes = TngUtil.getAllProcesses(methodPlugin);
			for (Process process : processes) {
				
				ComposedAdapterFactory adapterFactory = null;
				
				adapterFactory = TngAdapterFactory.INSTANCE
						.createWBSComposedAdapterFactory();
				
				ITreeContentProvider contentProvider = new AdapterFactoryContentProvider(
						adapterFactory);
				
				if (process.getBreakdownElements().size() > 0) {
					inspectProcess(contentProvider, process, patternProject, null);
				}
				
			}
			
			patternProject.setPatternPhasesList(new ArrayList<PatternPhase>(patternPhases.values()));
			patternProject.setPatternRolesList(new ArrayList<PatternRole>(patternRoles.values()));
			patternProject.setPatternWorkProductList(new ArrayList<PatternWorkProduct>(patternWorkProducts.values()));
			patternProject.setPatternTaskList(new ArrayList<PatternTask>(patternTasks.values()));
			
			return patternProject;
	}
	
	/**
	 * 
	 * @param contentProvider
	 * @param breakdownElement
	 * @param doc
	 * @param xmlElement
	 */
	private static void inspectProcess(ITreeContentProvider contentProvider, BreakdownElement breakdownElement, PatternProject patternProject, Phase phase) {
		Object[] elements = contentProvider.getElements(breakdownElement);
		for (int i = 0; i < elements.length; i++) {
			Object element = elements[i];
			
			if (element instanceof Phase) {
				PatternPhase patternPhase = new PatternPhase();
				patternPhase.setName(((Phase) element).getName());
				patternPhases.put(((Phase) element).getGuid(), patternPhase);
				inspectProcess(contentProvider,
						(WorkBreakdownElement) element, patternProject, (Phase) element);
			} else if (element instanceof RoleDescriptorWrapperItemProvider) {
				RoleDescriptorWrapperItemProvider provider = (RoleDescriptorWrapperItemProvider) element;
				Object value = provider.getValue();
				if (value instanceof RoleDescriptor) {
				}
			} else if (element instanceof RoleDescriptor) {
			} else if (element instanceof BreakdownElementWrapperItemProvider) {
				BreakdownElementWrapperItemProvider provider = (BreakdownElementWrapperItemProvider) element;
				Object value = provider.getValue();
				if (value instanceof Phase) {
					inspectProcess(contentProvider,
							(WorkBreakdownElement) value, patternProject, (Phase) element);
				} else if (value instanceof Task) {
					inspectProcess(contentProvider,
							(WorkBreakdownElement) value, patternProject, null);
				} else if (value instanceof WorkBreakdownElement) {
					inspectProcess(contentProvider,
							(WorkBreakdownElement) value, patternProject, null);
				}
			} else if (element instanceof Task) {
				if (phase != null) {
					PatternTask patternTask = patternTasks.get(((Task) element).getGuid());
					PatternPhase patternPhase = patternPhases.get(phase.getGuid());
					patternPhase.getPhaseTasks().add(patternTask);
					patternTask.setPhase(patternPhase);
				}
				inspectProcess(contentProvider,
						(WorkBreakdownElement) element, patternProject, null);
			} else if (element instanceof WorkBreakdownElement) {
				inspectProcess(contentProvider,
						(WorkBreakdownElement) element, patternProject, null);
			}
		}
	}
	
}
