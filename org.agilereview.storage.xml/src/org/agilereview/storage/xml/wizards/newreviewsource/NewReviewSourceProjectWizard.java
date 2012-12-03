package org.agilereview.storage.xml.wizards.newreviewsource;

import org.agilereview.storage.xml.Activator;
import org.agilereview.storage.xml.SourceFolderManager;
import org.agilereview.storage.xml.exception.ExceptionHandler;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.osgi.service.prefs.BackingStoreException;

/**
 * Wizard for creating a new AgileReview source project
 */
public class NewReviewSourceProjectWizard extends Wizard implements INewWizard {

	/**
	 * The first and sole page of the wizard 
	 */
	private NewReviewSourceProjectWizardPage page;
	/**
	 * Specifies whether the useDirectly check-box is initially selected or not (default: true)
	 */
	private boolean bUseDirectlyInitial = true;
	
	/**
	 * Specifies whether the user can choose if he wants to use the newly created AgileReview Source folder (default: yes)
	 */
	private boolean bFixUseDirectly = false;
	/**
	 * The name of the project created by this wizard
	 */
	private String createdProjectName = null;
	
	/**
	 * Constructor with arguments for customized calling
	 * @param useDirectlyInitial specifies whether the useDirectly check-box is initially selected or not
	 * @param fixUseDirectly Specifies whether the user can choose if he wants to use the newly created AgileReview Source folder
	 */
	public NewReviewSourceProjectWizard(boolean useDirectlyInitial, boolean fixUseDirectly) {
		super();
		setNeedsProgressMonitor(true);
		setWindowTitle("New AgileReview Source Project");
		this.bUseDirectlyInitial = useDirectlyInitial;
		this.bFixUseDirectly = fixUseDirectly;
	}
	/**
	 * Empty constructor for calling from eclipse
	 */
	public NewReviewSourceProjectWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}
	
	/**
	 * adds all needed pages to the wizard
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();
		page = new NewReviewSourceProjectWizardPage(bUseDirectlyInitial, bFixUseDirectly);
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		String projectName = page.getReviewSourceName();
		boolean useDirectly = page.getUseDirectly();
		
		boolean result = SourceFolderManager.createAndOpenSourceFolder(projectName);
		if (result) {
			createdProjectName = projectName;
		}
		
		if (useDirectly) {
			IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
			preferences.put(SourceFolderManager.SOURCEFOLDER_PROPERTYNAME, projectName);
			try {
				preferences.flush();
			} catch (BackingStoreException e) {
				String message = "AgileReview could not persistently set Review Source Folder.";
				ExceptionHandler.notifyUser(message);
			}			
		}
		
		return result;
	}
	/**
	 * Returns the "result" of this wizard: The name of the created project
	 * @return name of the created project or null if project was not created
	 */
	public String getCreatedProjectName() {
		return createdProjectName;
	}

}