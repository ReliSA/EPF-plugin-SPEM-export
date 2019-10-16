package org.eclipse.epf.export.pattern.ui.wizards;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.epf.export.pattern.ui.ExportPatternUIPlugin;
import org.eclipse.epf.export.pattern.ui.ExportPatternUIResources;
import org.eclipse.epf.ui.wizards.BaseWizardPage;
import org.eclipse.epf.uma.Process;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class SelectExportOptionsPage extends BaseWizardPage {
	
	public static final String PAGE_NAME = SelectExportOptionsPage.class
			.getName();
	
	protected Shell shell;
	
	protected Composite composite;
	
	protected Text processText;
	
	protected Text fileNameText;
	
	protected Text directoryText;
	
	protected Button browseButton;
	
	/**
	 * Creates a new instance.
	 */
	public SelectExportOptionsPage(String pageName) {
		super(pageName);
		setTitle(ExportPatternUIResources.selectExportDirWizardPage_title);
		setDescription(ExportPatternUIResources.selectExportDirWizardPage_text);
		setImageDescriptor(ExportPatternUIPlugin.getDefault().getImageDescriptor(
				"full/wizban/product32.gif")); //$NON-NLS-1$
	}

	/**
	 * Creates a new instance.
	 */
	public SelectExportOptionsPage() {
		this(PAGE_NAME);
	}

	public void createControl(Composite parent) {
		composite = createGridLayoutComposite(parent, 1);
		
		createLabel(composite, ExportPatternUIResources.selectedProcessLabel_text);
		processText = createText(composite, "");
		
		createLabel(composite, ExportPatternUIResources.fileNameLabel_text);
		fileNameText = new Text(composite, SWT.BORDER);
		
		createLabel(composite, ExportPatternUIResources.directoryLabel_text);
		directoryText = new Text(composite, SWT.BORDER);
		directoryText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (isValidPath(directoryText.getText().trim())) {
					setPageComplete(true);
					setErrorMessage(null);
				} else {
					setPageComplete(false);
					setErrorMessage(ExportPatternUIResources.invalidDirectory_error);
				}
			}
		});
		
		browseButton = new Button(composite, SWT.PUSH);
		browseButton.setLayoutData(new GridData(GridData.END));
		browseButton.setText(ExportPatternUIResources.browseButton_text);
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog fd = new DirectoryDialog(Display.getCurrent()
						.getActiveShell(), SWT.SAVE);
				String path = fd.open();
				boolean ok = false;
				if (path != null) {
					directoryText.setText(path);
					ok = isValidPath(path);
				}
				setPageComplete(ok);
				getWizard().getContainer().updateButtons();
			}
		});
		
		initControls();
		
		setControl(composite);
	}
	
	protected void initControls() {
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				try {
					DirectoryDialog dialog = new DirectoryDialog(shell, SWT.NONE);
					String selectedDir = dialog.open();
					if (selectedDir != null) {
						directoryText.setText(selectedDir);
					}
				} catch (Exception e) {
					ExportPatternUIPlugin.getDefault().getLogger().logError(e);
				}
			}
		});
	}
	
	/**
	 * @see org.eclipse.epf.ui.wizards.BaseWizardPage#onEnterPage(Object)
	 */
	public void onEnterPage(Object obj) {
		if (obj != null && obj instanceof Process) {
			Process process = (Process) obj;
			processText.setText(process.getName());
		}
	}
	
	/**
	 * Checks whether the user specific path is valid.
	 * 
	 * @param path
	 *            the user specific path
	 * @return <code>true</code> if the user specified path is valid.
	 */
	private boolean isValidPath(String path) {
		IPath ecPath = Path.fromOSString(path);
		return ecPath.isValidPath(path);
	}
	
	/**
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	public boolean isPageComplete() {
		return getFilename().length() > 0
				&& getPath().length() > 0;
	}
	
	/**
	 * Gets the user specified filename.
	 * 
	 * @return a filename
	 */
	public String getFilename() {
		return this.fileNameText.getText().trim();
	}
	
	/**
	 * Gets the user specified path.
	 * 
	 * @return an absolute path to the export location
	 */
	public String getPath() {
		return this.directoryText.getText().trim();
	}

}
