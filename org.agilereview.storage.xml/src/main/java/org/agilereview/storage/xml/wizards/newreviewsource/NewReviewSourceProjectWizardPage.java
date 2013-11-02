package org.agilereview.storage.xml.wizards.newreviewsource;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * The single page of the NewReview Wizard
 */
final class NewReviewSourceProjectWizardPage extends WizardPage implements ModifyListener {

	/**
	 * the text field for retrieving the id
	 */
	private Text name;
	/**
	 * Checkbox button to determine if folder should be used directly
	 */
	private Button use;
	/**
	 * Specifies whether the useDirectly check-box is initially selected or not
	 */
	private boolean bUseDirectlyInitial;
	/**
	 * Specifies whether the user can choose if he wants to use the newly created AgileReview Source folder
	 */
	private boolean bFixUseDirectly;

	
	/**
	 * Creates a new page
	 * @param useDirectlyInitial specifies whether the useDirectly check-box is initially selected or not
	 * @param fixUseDirectly specifies whether the user can choose if he wants to use the newly created AgileReview Source folder
	 */
	NewReviewSourceProjectWizardPage(boolean useDirectlyInitial, boolean fixUseDirectly) {
		super("New AgileReview Source Project");
		setTitle("New AgileReview Source Project");
		setDescription("This wizard creates a new AgileReview Source Project.");
		this.bUseDirectlyInitial = useDirectlyInitial;
		this.bFixUseDirectly = fixUseDirectly;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		
		// Textfield + Label
		Label lReviewId = new Label(container, SWT.NULL);
		lReviewId.setText("AgileReview Source Project-Name*:");
		name = new Text(container, SWT.BORDER | SWT.SINGLE);
		name.setText("AgileReview Source Project");
		name.selectAll();
		name.setToolTipText("'AgileReview Source Project'-Name must be set.");
		name.addModifyListener(this);
		name.setLayoutData(gd);
		
		// Check-Box
		use = new Button(container, SWT.CHECK);
		use.setText("Use this project after creation.");
		use.setSelection(true);
		use.setLayoutData(gd);
		use.setSelection(bUseDirectlyInitial);
		use.setEnabled(!bFixUseDirectly);
		
		// Required to avoid an error in the system
		setControl(container);
		this.modifyText(null);
		setErrorMessage(null);
	}
	
	/**
	 * @return the review ID entered
	 */
	String getReviewSourceName() {
		return this.name.getText().trim();
	}
	
	/**
	 * @return the review reference entered
	 */
	boolean getUseDirectly() {
		return this.use.getSelection();
	}
	
	
    /**
     * Returns whether this page's controls currently all contain valid 
     * values.
     *
     * @return <code>true</code> if all controls are valid, and
     *   <code>false</code> if at least one is invalid
     */
    private boolean validatePage() {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();

        String projectFieldContents = name.getText();
        if (projectFieldContents.equals("")) { //$NON-NLS-1$
            setErrorMessage("Name for the new Review Source Project can not be empty!");
            return false;
        }

        IStatus nameStatus = workspace.validateName(projectFieldContents, IResource.PROJECT);
        if (!nameStatus.isOK()) {
            setErrorMessage(nameStatus.getMessage());
            return false;
        }
 
        IProject project = workspace.getRoot().getProject(projectFieldContents);
        
        if (project.exists()) {
            setErrorMessage("Review Source Project does already exist");
            return false;
        }

        setErrorMessage(null);
        setMessage(null);
        return true;
    }
	

	@Override
	public void modifyText(ModifyEvent e) {
		setPageComplete(validatePage());		
	}
}