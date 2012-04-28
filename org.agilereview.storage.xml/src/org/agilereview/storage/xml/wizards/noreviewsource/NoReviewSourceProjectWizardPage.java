package org.agilereview.storage.xml.wizards.noreviewsource;

import java.util.ArrayList;
import java.util.List;

import org.agilereview.storage.xml.SourceFolderManager;
import org.agilereview.storage.xml.wizards.newreviewsource.NewReviewSourceProjectWizard;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.datatransfer.ExternalProjectImportWizard;

/**
 * The single page of the NewReview Wizard
 */
 final class NoReviewSourceProjectWizardPage extends WizardPage implements Listener {

	/**
	 * Drop-Down-Box to choose which AgileReview Source Project should be chosen
	 */
	private Combo comboChooseProject;
	/**
	 * Drop-Down-Box to choose which closed AgileReview Source Project should be opened
	 */
	private Combo comboClosedProjects;
	/**
	 * Button to open the AgileReview Source Project currently selected in <code>comboClosedProjects</code>
	 */
	private Button btOpenClosed;
	/**
	 * Button to create a new AgileReview Source Project
	 */
	private Button btCreateNew;
	/**
	 * Button to import projects into workspace
	 */
	private Button btImport;
	/**
	 * List with all open AgileReview Source Project in the workspace
	 */
	private List<String> listOpenARProjects;
	/**
	 * List with all closed AgileReview Source Project in the workspace
	 */
	private List<String> listClosedARProjects;	
	
	/**
	 * Creates a new page
	 */
	NoReviewSourceProjectWizardPage() {
		super("No Review Source Project");
		setTitle("No Review Source Project");
		setDescription("In order to use AgileReview a 'AgileReview Source Project' for storing your reviews is needed.");
	}

	/**
	 * @return the review ID entered
	 */
	String getReviewSourceName() {
		return this.comboChooseProject.getText();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.horizontalSpacing = 20;
		layout.verticalSpacing = 30;
		container.setLayout(layout);
		
		Label labelChoose = new Label(container, SWT.NONE);
		labelChoose.setText("Please choose a AgileReview Source Project:");
	
		comboChooseProject = new Combo(container, SWT.READ_ONLY | SWT.DROP_DOWN);
		comboChooseProject.addListener(SWT.Modify, this);
		
		Group bottom = new Group(container, SWT.NONE);
		bottom.setText("You may first want to do one of the following");
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = layout.numColumns;
		bottom.setLayoutData(gridData);
		GridLayout gridLayoutBottom = new GridLayout();
		gridLayoutBottom.numColumns = 2;
		gridLayoutBottom.horizontalSpacing = 20;
		gridLayoutBottom.verticalSpacing = 15;
		bottom.setLayout(gridLayoutBottom);
		
		comboClosedProjects = new Combo(bottom, SWT.READ_ONLY | SWT.DROP_DOWN);
		
		btOpenClosed = new Button(bottom, SWT.PUSH);
		btOpenClosed.setText("Open Project");
		btOpenClosed.addListener(SWT.Selection, this);		
				
		btCreateNew = new Button(bottom, SWT.PUSH);
		btCreateNew.setText("Create a new AgileReview Source Project");
		btCreateNew.addListener(SWT.Selection, this);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = gridLayoutBottom.numColumns;
		btCreateNew.setLayoutData(gridData);
		
		btImport = new Button(bottom, SWT.PUSH);
		btImport.setText("Import a Project");
		btImport.addListener(SWT.Selection, this);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = gridLayoutBottom.numColumns;
		btImport.setLayoutData(gridData);
		
		updateComboBoxes(null);

		// Required to avoid an error in the system
		setControl(container);
		setPageComplete(validatePage());
//		setErrorMessage(null);
	}
	
	/**
	 * Fills the comboBoxes with the currently available AgileReview Source Projects.
	 * @param prefProject project which should be selected in the 'Choose Project' comboBox. If no special one should be selected, <code>null</code> can be given
	 */
	private void updateComboBoxes(String prefProject) {
		// Get the elements
		listOpenARProjects = new ArrayList<String>();
		listClosedARProjects = new ArrayList<String>();
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] projArr = workspaceRoot.getProjects();
		for (IProject currProj : projArr) {
			try {
				if (currProj.hasNature(SourceFolderManager.AGILEREVIEW_NATURE)) {
					listOpenARProjects.add(currProj.getName());
				}
			} catch (CoreException e) {
				// Is thrown, if currProj is closed or does not exist --> CoreException actively used here
				if (currProj.exists()) {
					listClosedARProjects.add(currProj.getName());
				}
			}
		}
		
		comboChooseProject.setItems(listOpenARProjects.toArray(new String[listOpenARProjects.size()]));
		comboClosedProjects.setItems(listClosedARProjects.toArray(new String[listClosedARProjects.size()]));
		
		// Select the preferred Project
		if (prefProject != null) {
			String [] items = comboChooseProject.getItems();
			for (int i=0;i<items.length;i++) {
				if (items[i].equals(prefProject)) {
					comboChooseProject.select(i);
					break;
				}
			}
		}
//		if (comboChooseProject.getSelectionIndex() == -1) {
//			comboChooseProject.select(0);
//		}
		
		comboChooseProject.pack();
		comboClosedProjects.pack();
	}	
		
    /**
     * Returns whether this page's controls currently all contain valid 
     * values.
     *
     * @return <code>true</code> if all controls are valid, and
     *   <code>false</code> if at least one is invalid
     */
    private boolean validatePage() {
        String projectFieldContents = comboChooseProject.getText();
        if (projectFieldContents.isEmpty()) { //$NON-NLS-1$
            setErrorMessage("Please select a AgileReview Source Project to use");
            return false;
        }

        setErrorMessage(null);
        setMessage(null);
        return true;
    }
	
	@Override
	public void handleEvent(Event event) {
		if (event.widget == btOpenClosed) {
			if (comboClosedProjects.getSelectionIndex() != -1){
				IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
				IProject p = workspaceRoot.getProject(comboClosedProjects.getText());
				try {
					// TODO: use ProgressMonitor here
					p.open(null);
					while (!p.isOpen()) {}
					updateComboBoxes(p.getName());
				} catch	(CoreException e) {
					setErrorMessage(e.getLocalizedMessage());
				}
			}
			
		} else if (event.widget == btCreateNew) {
			NewReviewSourceProjectWizard revSourceW = new NewReviewSourceProjectWizard(false, true);
			WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), revSourceW);
			if (dialog.open() == Window.OK){
				if (revSourceW.getCreatedProjectName() != null) {
					updateComboBoxes(revSourceW.getCreatedProjectName());
				}
			}						
		} else if (event.widget == btImport) {
			// TODO: Do some general import here
			IWorkbench workbench = PlatformUI.getWorkbench();
			ExternalProjectImportWizard wizard = new ExternalProjectImportWizard();
			wizard.init(workbench, null);
			WizardDialog dialog = new WizardDialog(workbench.getActiveWorkbenchWindow().getShell(), wizard);
			if (dialog.open() == Window.OK){
				IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
				String newProject = null;
				for (IProject p: workspaceRoot.getProjects()) {
					try {
						// Take the first "new" AgileReview Source Project to find and interpret it as the imported one
						if (!listOpenARProjects.contains(p.getName()) && p.hasNature(SourceFolderManager.AGILEREVIEW_NATURE)) {
							newProject = p.getName();
							break;
						}
					} catch (CoreException e) {
						setErrorMessage(e.getLocalizedMessage());
					}
				}
				updateComboBoxes(newProject);
			}
		} else if(event.widget == comboChooseProject) {
			setPageComplete(validatePage());
		}
	}
	
}