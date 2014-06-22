package org.agilereview.storage.xml.wizards.noreviewsource;

import org.agilereview.common.exception.ExceptionHandler;
import org.agilereview.core.external.preferences.AgileReviewPreferences;
import org.agilereview.storage.xml.Activator;
import org.agilereview.storage.xml.persistence.SourceFolderManager;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;
import org.osgi.service.prefs.BackingStoreException;

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
     * Standard constructor. Equal to <code>new NoReviewSourceWizard(true)</code>
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
        this.page = new NoReviewSourceProjectWizardPage();
        addPage(this.page);
    }
    
    @Override
    public boolean performFinish() {
        this.chosenProjectName = this.page.getReviewSourceName();
        boolean result = this.chosenProjectName != null;
        if (result && this.setDirectly) {
            IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
            String oldSourceFolder = preferences.get(SourceFolderManager.SOURCEFOLDER_PROPERTYNAME, null);
            
            //if folder changed, set new source folder, reset active review, and open review list
            if (oldSourceFolder != null && !oldSourceFolder.equals(chosenProjectName)) {
                IEclipsePreferences corePreferences = InstanceScope.INSTANCE.getNode(AgileReviewPreferences.CORE_PLUGIN_ID);
                corePreferences.remove(AgileReviewPreferences.ACTIVE_REVIEW_ID);
                corePreferences.remove(AgileReviewPreferences.OPEN_REVIEWS);
                preferences.put(SourceFolderManager.SOURCEFOLDER_PROPERTYNAME, this.chosenProjectName);
                
                try {
                    preferences.flush();
                    corePreferences.flush();
                } catch (BackingStoreException e) {
                    String message = "AgileReview could not persistently set Review Source Project.";
                    ExceptionHandler.logAndNotifyUser(new Exception(message), Activator.PLUGIN_ID);
                }
            }
        }
        return result;
    }
    
    @Override
    public boolean performCancel() {
        boolean cancel = true;
        
        IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
        String currentReviewSourceName = preferences.get(SourceFolderManager.SOURCEFOLDER_PROPERTYNAME, null);
        
        if (currentReviewSourceName == null) {
            cancel = MessageDialog.openQuestion(getShell(), "Cancel Review Source Project selection", "Are you sure you want to cancel? "
                    + "Agilereview will not work until you create and choose an AgileReview Source Project.");
        }
        
        return cancel;
    }
    
    /**
     * Returns the name of the project chosen to be the current AgileReview Source Project
     * @return name of the project chosen
     */
    public String getChosenProjectName() {
        return this.chosenProjectName;
    }
    
}