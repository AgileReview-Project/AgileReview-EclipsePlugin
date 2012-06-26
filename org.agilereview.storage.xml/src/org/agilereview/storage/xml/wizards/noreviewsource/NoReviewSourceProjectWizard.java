package org.agilereview.storage.xml.wizards.noreviewsource;

import org.agilereview.storage.xml.Activator;
import org.agilereview.storage.xml.SourceFolderManager;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;

/**
 * Wizard for choosing a AgileReview Source Project if non is existent
 */
public class NoReviewSourceProjectWizard extends Wizard implements IWizard {

	/**
	 * The first and sole page of the wizard
	 */
	private NoReviewSourceProjectWizardPage page;

	/**
	 * The name of the project created by this wizard
	 */
	private String chosenProjectName = null;
	/**
	 * Indicates, if the project should directly be set as 'AgileReview Source Project'
	 */
	private boolean setDirectly = true;

	/**
	 * Standard constructor. Equal to NoReviewSourceWizard(true)
	 */
	public NoReviewSourceProjectWizard() {
		this(true);
	}

	/**
	 * Constructor for this wizard
	 * @param setDirectly if <code>true</code> the project will be set as 'AgileReview Source Project' directly, else the name will only be provided
	 */
	public NoReviewSourceProjectWizard(boolean setDirectly) {
		super();
		setNeedsProgressMonitor(true);
		setWindowTitle("No AgileReview Source Project");
		this.setDirectly = setDirectly;
	}

	/**
	 * adds all needed pages to the wizard
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();
		page = new NoReviewSourceProjectWizardPage();
		addPage(page);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean performFinish() {
		chosenProjectName = page.getReviewSourceName();
		boolean result = chosenProjectName != null;
		if (result && setDirectly) {
			InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID).put(SourceFolderManager.SOURCEFOLDER_PROPERTYNAME, chosenProjectName);
		}
		return result;
	}

	@Override
	public boolean performCancel() {
		return MessageDialog.openQuestion(getShell(), "Cancel Review Source Project selection", "Are you sure you want to cancel? "
				+ "Agilereview will not work until you create and choose an AgileReview Source Project.");
	}

	/**
	 * Returns the name of the project chosen to be the current AgileReview Source Project
	 * @return name of the project chosen
	 */
	public String getChosenProjectName() {
		return chosenProjectName;
	}

}