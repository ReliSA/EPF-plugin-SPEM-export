package org.eclipse.epf.export.pattern.ui.wizards;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.epf.authoring.ui.wizards.SelectProcessPage;
import org.eclipse.epf.common.ui.util.MsgBox;
//import org.eclipse.epf.common.utils.FileUtil;
import org.eclipse.epf.export.pattern.ExportPatternData;
import org.eclipse.epf.export.pattern.ExportPatternPlugin;
import org.eclipse.epf.export.pattern.ExportPatternXMLService;
import org.eclipse.epf.export.pattern.ui.ExportPatternUIResources;
import org.eclipse.epf.export.services.ConfigurationExportData;
import org.eclipse.epf.export.services.ConfigurationExportService;
import org.eclipse.epf.export.services.PluginExportData;
import org.eclipse.epf.export.xml.ExportXMLResources;
import org.eclipse.epf.export.xml.preferences.ExportXMLPreferences;
import org.eclipse.epf.library.LibraryService;
import org.eclipse.epf.library.services.SafeUpdateController;
import org.eclipse.epf.library.ui.LibraryUIManager;
import org.eclipse.epf.library.ui.wizards.OpenLibraryWizard;
import org.eclipse.epf.ui.wizards.BaseWizard;
import org.eclipse.epf.uma.MethodConfiguration;
import org.eclipse.epf.uma.MethodLibrary;
import org.eclipse.epf.uma.Process;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

//import com.ibm.icu.util.Calendar;

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

	// The wizard page that prompts the user to select a process.
	protected SelectProcessPage selectProcessPage;
	
	protected SelectExportOptionsPage selectExportOptionsPage;

	protected PluginExportData pluginData = new PluginExportData();
	
	protected ExportPatternData patternData = new ExportPatternData();
	
	private String currLibPathToResume;
	
	private File tempExportFolder;
	
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
			
			selectProcessPage = new SelectProcessPage();
			selectExportOptionsPage = new SelectExportOptionsPage();
			
			super.addPage(selectProcessPage);
			super.addPage(selectExportOptionsPage);
			
			
		} else {
			List<IWizardPage> wizardPages = new ArrayList<IWizardPage>();
			
			IWizardPage page = wizardExtender
					.getReplaceWizardPage(SelectProcessPage.PAGE_NAME);
			if (page != null) {
				wizardPages.add(page);
			} else {
				selectProcessPage = new SelectProcessPage();
				wizardPages.add(selectProcessPage);
			}
			IWizardPage selectExportOptionsPage = wizardExtender
					.getReplaceWizardPage(SelectExportOptionsPage.PAGE_NAME);
			if (selectExportOptionsPage != null) {
				wizardPages.add(selectExportOptionsPage);
			} else {
				selectExportOptionsPage = new SelectExportOptionsPage();
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
	
	public boolean doFinish() {
		return exportPattern(selectProcessPage.getProcess(),
				ExportPatternXMLService.getInstance(patternData));
	}
	
	public boolean exportPattern(Process process,
			ExportPatternXMLService service) {
		if (process == null || service == null) {
			throw new IllegalArgumentException();
		}


		service = ExportPatternXMLService.getInstance(patternData);
		
		service.export(process);
		
		return true;
	}
	
}

