package org.eclipse.epf.export.pattern;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.epf.export.pattern.domain.Descriptable;
import org.eclipse.epf.export.pattern.domain.PatternArtifact;
import org.eclipse.epf.export.pattern.domain.PatternDiscipline;
import org.eclipse.epf.export.pattern.domain.PatternOutcome;
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
import org.eclipse.epf.uma.Artifact;
import org.eclipse.epf.uma.BreakdownElement;
import org.eclipse.epf.uma.Discipline;
import org.eclipse.epf.uma.MethodPlugin;
import org.eclipse.epf.uma.Outcome;
import org.eclipse.epf.uma.Phase;
import org.eclipse.epf.uma.Process;
import org.eclipse.epf.uma.RoleDescriptor;
import org.eclipse.epf.uma.Task;
import org.eclipse.epf.uma.WorkBreakdownElement;
import org.eclipse.epf.uma.WorkProduct;
import org.eclipse.jface.viewers.ITreeContentProvider;

public class ExportPatternMapService {

	private static ExportPatternLogger logger;

	public static PatternProject map(MethodPlugin methodPlugin, ExportPatternLogger exportPatternLogger) {

		logger = exportPatternLogger;

		PatternProject patternProject = new PatternProject();
		parseMethodPluginDescription(patternProject, methodPlugin.getBriefDescription());

		DisciplineCategoriesItemProvider disciplineProvider = (DisciplineCategoriesItemProvider) TngUtil
				.getDisciplineCategoriesItemProvider(methodPlugin);
		Collection<Discipline> disciplines = disciplineProvider.getChildren(disciplineProvider);
		for (Discipline discipline : disciplines) {
			PatternDiscipline patternDiscipline = new PatternDiscipline();
			patternDiscipline.setName(discipline.getName());
		}

		List<Task> tasks = ProcessUtil.getAllTasks(methodPlugin);
		for (Task task : tasks) {
			PatternTask patternTask = new PatternTask();
			patternTask.setGuid(task.getGuid());
			patternTask.setName(task.getName());
			parseMainDescription(patternTask, task.getPresentation().getMainDescription());

			patternProject.getPatternTasks().put(patternTask.getGuid(), patternTask);

			List<WorkProduct> outputs = task.getOutput();
			for (WorkProduct output : outputs) {
				mapWorkProduct(patternProject, output, patternTask);
			}

			List<org.eclipse.epf.uma.Role> performers = task.getPerformedBy();
			for (org.eclipse.epf.uma.Role role : performers) {
				String guid = role.getGuid();
				PatternRole patternRole = patternProject.getPatternRoles().get(guid);
				if (patternRole == null) {
					patternRole = new PatternRole();
					patternRole.setName(role.getName());
					patternProject.getPatternRoles().put(guid, patternRole);
				}
				patternTask.getPerformers().add(patternRole);
			}
		}

		List<Process> processes = TngUtil.getAllProcesses(methodPlugin);
		for (Process process : processes) {

			ComposedAdapterFactory adapterFactory = null;

			adapterFactory = TngAdapterFactory.INSTANCE.createWBSComposedAdapterFactory();

			ITreeContentProvider contentProvider = new AdapterFactoryContentProvider(adapterFactory);

			if (process.getBreakdownElements().size() > 0) {
				inspectProcess(contentProvider, process, patternProject, null);
			}

		}

		return patternProject;
	}

	/**
	 * 
	 * @param contentProvider
	 * @param breakdownElement
	 * @param doc
	 * @param xmlElement
	 */
	private static void inspectProcess(ITreeContentProvider contentProvider, BreakdownElement breakdownElement,
			PatternProject patternProject, Phase phase) {
		Object[] elements = contentProvider.getElements(breakdownElement);
		for (int i = 0; i < elements.length; i++) {
			Object element = elements[i];

			if (element instanceof Phase) {
				PatternPhase patternPhase = new PatternPhase();
				patternPhase.setName(((Phase) element).getName());
				patternProject.getPatternPhases().put(((Phase) element).getGuid(), patternPhase);
				inspectProcess(contentProvider, (WorkBreakdownElement) element, patternProject, (Phase) element);
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
					inspectProcess(contentProvider, (WorkBreakdownElement) value, patternProject, (Phase) element);
				} else if (value instanceof Task) {
					inspectProcess(contentProvider, (WorkBreakdownElement) value, patternProject, null);
				} else if (value instanceof WorkBreakdownElement) {
					inspectProcess(contentProvider, (WorkBreakdownElement) value, patternProject, null);
				}
			} else if (element instanceof Task) {
				if (phase != null) {
					PatternTask patternTask = patternProject.getPatternTasks().get(((Task) element).getGuid());
					PatternPhase patternPhase = patternProject.getPatternPhases().get(phase.getGuid());
					patternPhase.getPhaseTasks().add(patternTask);
					patternTask.setPhase(patternPhase);
				}
				inspectProcess(contentProvider, (WorkBreakdownElement) element, patternProject, null);
			} else if (element instanceof WorkBreakdownElement) {
				inspectProcess(contentProvider, (WorkBreakdownElement) element, patternProject, null);
			}
		}
	}
	
	private static void mapWorkProduct(PatternProject patternProject, WorkProduct workProduct, PatternTask patternTask) {
		PatternWorkProduct patternWorkProduct = null;
		if (workProduct instanceof Artifact) {
			patternWorkProduct = new PatternArtifact();
		} else if (workProduct instanceof Outcome) {
			patternWorkProduct = new PatternOutcome();
		} else {
			logger.logWarning("Invalid work product type of " + workProduct.getName());
		}
		patternTask.getOutputs().add(patternWorkProduct);
		patternWorkProduct.setTask(patternTask);
		patternWorkProduct.setGuid(workProduct.getGuid());
		patternWorkProduct.setName(workProduct.getName());
		parseMainDescription(patternWorkProduct, workProduct.getPresentation().getMainDescription());
		patternProject.getPatternWorkProducts().put(patternWorkProduct.getGuid(), patternWorkProduct);
	}

	private static void parseMainDescription(Descriptable descriptable, String mainDescription) {
		String[] lines = mainDescription.split(System.getProperty("line.separator"));
		for (String line : lines) {
			line = line.replaceAll("\\<.*?>","").trim();
			if (line.startsWith("keywords")) {
				String[] tokens = line.split("=")[1].split(",");
				if (descriptable instanceof PatternTask) {
					((PatternTask) descriptable).setTokens(tokens);
				} else if (descriptable instanceof PatternWorkProduct) {
					((PatternWorkProduct) descriptable).getTask().setTokens(tokens);
				} else {
					logger.logWarning(String.format("Element with GUID %s: Keywords attribute parsing error.", descriptable.getGuid()));
				}
			} else if (line.startsWith("amount")) {
				if (descriptable instanceof PatternTask) {
					((PatternTask) descriptable).setAmount(line);
				} else if (descriptable instanceof PatternWorkProduct) {
					((PatternWorkProduct) descriptable).getTask().setAmount(line);
				} else {
					logger.logWarning(String.format("Element with GUID %s: Amount attribute parsing error.", descriptable.getGuid()));
				}
			} else {
				logger.logWarning("Invalid parameter" + line);
			}
		}
	}
	
	private static void parseMethodPluginDescription(PatternProject project, String description) {
		String[] lines = description.split(System.getProperty("line.separator"));
		for (String line : lines) {
			line = line.replaceAll("\\<.*?>","").trim();
			if (line.startsWith("pattern")) {
				logger.logMessage("is pattern " + line.split("=")[1]);
				project.setPattern(Boolean.parseBoolean(line.split("=")[1]));
			} else {
				logger.logWarning("Invalid parameter" + line);
			}
		}
	}
	
}
