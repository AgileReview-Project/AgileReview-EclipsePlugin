package org.agilereview.ui.basic.reviewExplorer.wizards.newReview;

import org.agilereview.core.external.storage.CommentingAPI;
import org.agilereview.core.external.storage.Review;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * Provides a wizard for creating a new Review via the NewWizard
 */
public class NewReviewWizard extends Wizard implements INewWizard {
    
    /**
     * The first and sole page of the wizard
     */
    private NewReviewWizardPage page1;
    
    /**
     * creates a new wizard
     */
    public NewReviewWizard() {
        super();
        setNeedsProgressMonitor(true);
        setWindowTitle("New Review");
    }
    
    /**
     * adds all needed pages to the wizard
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {
        super.addPages();
        page1 = new NewReviewWizardPage();
        addPage(page1);
    }
    
    /**
     * Execute the actual wizard command after all information was collected
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @Override
    public boolean performFinish() {
        boolean result = false;
        Review newRev = CommentingAPI.createReview();
        if (newRev != null) {
            newRev.setName(this.page1.getReviewName());
            newRev.setReference(this.page1.getReviewReference());
            newRev.setDescription(this.page1.getReviewDescription());
            newRev.setResponsibility(this.page1.getReviewResponsibility());
            result = true;
        }
        return result;
    }
    
    /**
     * not needed
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     */
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        // TODO: Auto-Perspective switch?
        
        //        if (ViewControl.getInstance().shouldSwitchPerspective()) {
        //            ViewControl.getInstance().switchPerspective();
        //        }
    }
}
