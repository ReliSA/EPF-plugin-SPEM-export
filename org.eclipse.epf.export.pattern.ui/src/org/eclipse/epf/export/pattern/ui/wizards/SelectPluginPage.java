package org.eclipse.epf.export.pattern.ui.wizards;

import org.eclipse.epf.export.services.PluginExportData;
import org.eclipse.jface.wizard.IWizardPage;

/**
 * A wizard page that prompts the user to select one or more method plug-ins to export.
 * 
 * @author Lenka Simeckova
 * @since 1.0
 */
public class SelectPluginPage extends
		org.eclipse.epf.export.wizards.SelectPluginPage {

	/**
	 * Creates a new instance.
	 */
	public SelectPluginPage(PluginExportData data) {
		super(data);
	}

	/**
	 * @see org.eclipse.jface.wizard.WizardPage#getNextPage()
	 */
	public IWizardPage getNextPage() {
		super.saveDataToModel();
		ExportPatternWizard wizard = (ExportPatternWizard) getWizard();
		SelectExportOptionsPage page = wizard.selectExportOptionsPage;
		page.onEnterPage(null);
		return page;
	}

}
