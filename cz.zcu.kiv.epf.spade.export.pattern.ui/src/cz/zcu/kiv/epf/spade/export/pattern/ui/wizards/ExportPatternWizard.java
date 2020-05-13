package cz.zcu.kiv.epf.spade.export.pattern.ui.wizards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.epf.ui.wizards.BaseWizard;
import org.eclipse.epf.uma.MethodPlugin;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

import cz.zcu.kiv.epf.spade.export.pattern.ExportPatternConfig;
import cz.zcu.kiv.epf.spade.export.pattern.ExportPatternService;
import cz.zcu.kiv.epf.spade.export.pattern.ui.ExportPatternUIResources;

/**
 * The Export Pattern wizard.
 * 
 * @author Lenka Simeckova
 * @since 1.0
 */
public class ExportPatternWizard extends BaseWizard implements IExportWizard {

	/**
	 * The wizard ID.
	 */
	public static final String WIZARD_ID = ExportPatternWizard.class.getName();

	/**
	 * The Publish Configuration wizard extension point ID.
	 */
	public static final String WIZARD_EXTENSION_POINT_ID = "org.eclipse.epf.export.pattern.ui.exportPatternWizard"; //$NON-NLS-1$	

	/** The wizard page that prompts the user to select a process. **/
	protected SelectPluginPage selectPluginPage;
	
	/** The wizard page for specifying target export directory. **/
	protected SelectExportOptionsPage selectExportOptionsPage;
	
	protected ExportPatternConfig patternData = new ExportPatternConfig();
	
	/**
	 * Creates a new instance.
	 */
	public ExportPatternWizard() {
	}

	/**
	 * @see org.eclipse.epf.ui.wizards.BaseWizard#init(IWorkbench,
	 *      IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		super.init(workbench, selection);
		setWindowTitle(ExportPatternUIResources.exportPatternWizard_title);
	}

	/**
	 * @see org.eclipse.epf.ui.wizards.BaseWizard#getWizardExtenderExtensionPointId()
	 */
	public String getWizardExtenderExtensionPointId() {
		return WIZARD_EXTENSION_POINT_ID;
	}
	
	/**
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	public void addPages() {
		if (wizardExtender == null) {
			
			selectPluginPage = new SelectPluginPage(patternData);
			selectExportOptionsPage = new SelectExportOptionsPage(patternData);
			
			super.addPage(selectPluginPage);
			super.addPage(selectExportOptionsPage);
			
			
		} else {
			List<IWizardPage> wizardPages = new ArrayList<IWizardPage>();
			
			IWizardPage page = wizardExtender
					.getReplaceWizardPage(SelectPluginPage.PAGE_NAME);
			if (page != null) {
				wizardPages.add(page);
			} else {
				selectPluginPage = new SelectPluginPage(patternData);
				wizardPages.add(selectPluginPage);
			}
			IWizardPage selectExportOptionsPage = wizardExtender
					.getReplaceWizardPage(SelectExportOptionsPage.PAGE_NAME);
			if (selectExportOptionsPage != null) {
				wizardPages.add(selectExportOptionsPage);
			} else {
				selectExportOptionsPage = new SelectExportOptionsPage(patternData);
				wizardPages.add(selectExportOptionsPage);
			}
			
			super.getNewWizardPages(wizardPages);
			
			for (Iterator<IWizardPage> it = wizardPages.iterator(); it
					.hasNext();) {
				IWizardPage wizardPage = it.next();
				super.addPage(wizardPage);
			}

			wizardExtender.initWizardPages(wizardPages);
		}
	}
	
	/**
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean canFinish() {
		if (wizardExtender != null) {
			if (! wizardExtender.canFinish()) {
				return false;
			}
		}
		return getContainer().getCurrentPage() == selectExportOptionsPage
				&& selectExportOptionsPage.isPageComplete();
	}
	
	/**
	 * @see org.eclipse.epf.ui.wizards.BaseWizard#doFinish()
	 */
	public boolean doFinish() {
		return exportPattern(patternData.selectedPlugins,
				ExportPatternService.getInstance(patternData));
	}
	
	/**
	 * 
	 * @param selectedPlugins
	 * @param service
	 * @return
	 */
	public boolean exportPattern(Collection<MethodPlugin> selectedPlugins,
			ExportPatternService service) {
		if (selectedPlugins == null || service == null) {
			throw new IllegalArgumentException();
		}
		
		service.export();
		
		return true;
	}
	
}

