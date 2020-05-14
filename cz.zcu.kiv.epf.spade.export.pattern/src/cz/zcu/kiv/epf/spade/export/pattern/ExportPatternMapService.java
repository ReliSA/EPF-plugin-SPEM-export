package cz.zcu.kiv.epf.spade.export.pattern;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
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

import cz.zcu.kiv.epf.spade.export.pattern.domain.Descriptable;
import cz.zcu.kiv.epf.spade.export.pattern.domain.PatternArtifact;
import cz.zcu.kiv.epf.spade.export.pattern.domain.PatternDiscipline;
import cz.zcu.kiv.epf.spade.export.pattern.domain.PatternOutcome;
import cz.zcu.kiv.epf.spade.export.pattern.domain.PatternPhase;
import cz.zcu.kiv.epf.spade.export.pattern.domain.PatternProject;
import cz.zcu.kiv.epf.spade.export.pattern.domain.PatternRole;
import cz.zcu.kiv.epf.spade.export.pattern.domain.PatternTask;
import cz.zcu.kiv.epf.spade.export.pattern.domain.PatternWorkProduct;

/**
 * Class for mapping UMA to own data model.
 * 
 * @author Lenka Simeckova
 * @since 1.0
 *
 */
public class ExportPatternMapService {
	
	private static ExportPatternMapService exportPatternMapService = new ExportPatternMapService();

	private ExportPatternLogger logger;
	
	public static ExportPatternMapService getInstance(ExportPatternLogger loggerp) {
		exportPatternMapService.logger = loggerp;
		return exportPatternMapService;
	}

	/**
	 * Maps UMA to own data model.
	 * @param methodPlugins
	 * @param exportPatternLogger
	 * @return list of patternProject instances
	 */
	public List<PatternProject> map(Collection<MethodPlugin> methodPlugins,
			ExportPatternLogger exportPatternLogger) {

		logger = exportPatternLogger;

		List<PatternProject> patternProjects = new ArrayList<PatternProject>();

		for (MethodPlugin methodPlugin : methodPlugins) {
			PatternProject patternProject = new PatternProject();
			patternProject.setName(methodPlugin.getName());
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
				mapTask(patternProject, task);
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

			patternProjects.add(patternProject);
		}
		return patternProjects;
	}

	/**
	 * Maps task.
	 * @param patternProject
	 * @param task
	 */
	private void mapTask(PatternProject patternProject, Task task) {
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
				patternRole.setGuid(guid);
				patternRole.setName(role.getName());
				patternProject.getPatternRoles().put(guid, patternRole);
			}
			patternTask.getPerformers().add(patternRole);
		}
	}

	/**
	 * Parses process.
	 * @param contentProvider
	 * @param breakdownElement
	 * @param patternProject
	 * @param phase
	 */
	private void inspectProcess(ITreeContentProvider contentProvider, BreakdownElement breakdownElement,
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

	/**
	 * Maps work product.
	 * @param patternProject
	 * @param workProduct
	 * @param patternTask
	 */
	void mapWorkProduct(PatternProject patternProject, WorkProduct workProduct,
			PatternTask patternTask) {
		PatternWorkProduct patternWorkProduct = null;
		if (workProduct instanceof Artifact) {
			patternWorkProduct = new PatternArtifact();
		} else if (workProduct instanceof Outcome) {
			patternWorkProduct = new PatternOutcome();
		} else {
			logger.logWarning("Invalid work product type of " + workProduct.getName());
			return;
		}
		patternTask.getOutputs().add(patternWorkProduct);
		patternWorkProduct.setTask(patternTask);
		patternWorkProduct.setGuid(workProduct.getGuid());
		patternWorkProduct.setName(workProduct.getName());
		parseMainDescription(patternWorkProduct, workProduct.getPresentation().getMainDescription());
		patternProject.getPatternWorkProducts().put(patternWorkProduct.getGuid(), patternWorkProduct);
	}

	/**
	 * Parses Main Description field of Descriptable element
	 * @param descriptable
	 * @param mainDescription
	 */
	private void parseMainDescription(Descriptable descriptable, String mainDescription) {
		String[] lines = mainDescription.split(System.getProperty("line.separator"));
		for (String line : lines) {
			line = line.replaceAll("\\<.*?>", "").trim();
			
			if (!line.isEmpty()) {
				if (line.startsWith("keywords")) {
					String[] tokens = line.split("=")[1].split(",");
					if (descriptable instanceof PatternTask) {
						((PatternTask) descriptable).setTokens(tokens);
					} else if (descriptable instanceof PatternWorkProduct) {
						((PatternWorkProduct) descriptable).getTask().setTokens(tokens);
					} else {
						logger.logWarning(String.format("Element with GUID %s: Keywords attribute parsing error.",
								descriptable.getGuid()));
					}
				} else if (line.startsWith("amount")) {
					if (descriptable instanceof PatternTask) {
						((PatternTask) descriptable).setAmount(line);
					} else if (descriptable instanceof PatternWorkProduct) {
						((PatternWorkProduct) descriptable).getTask().setAmount(line);
					} else {
						logger.logWarning(String.format("Element with GUID %s: Amount attribute parsing error.",
								descriptable.getGuid()));
					}
				} else if (line.startsWith("type") && descriptable instanceof PatternOutcome) {
					((PatternOutcome) descriptable).setType(line.split("=")[1]);
				} else {
					logger.logWarning("Invalid parameter" + line);
				}
			}
		}
	}

	/**
	 * Parses method plug-in description (sign of inverted pattern).
	 * @param project
	 * @param description
	 */
	private void parseMethodPluginDescription(PatternProject project, String description) {
		String[] lines = description.split(System.getProperty("line.separator"));
		for (String line : lines) {
			line = line.replaceAll("\\<.*?>", "").trim();
			
			if (!line.isEmpty()) {
				if (line.startsWith("pattern")) {
					project.setPattern(Boolean.parseBoolean(line.split("=")[1]));
				} else {
					logger.logWarning("Invalid parameter" + line);
				}
			}
		}
	}

}
