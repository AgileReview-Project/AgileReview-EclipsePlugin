package org.agilereview.ui.basic.reviewExplorer.handler;

import org.agilereview.ui.basic.reviewExplorer.wizards.newReview.NewReviewWizard;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

public class NewReviewHandler extends AbstractHandler {
    
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        
        // Forward to wizard
        new WizardDialog(HandlerUtil.getActiveShell(event), new NewReviewWizard()).open();
        
        return null;
    }
    
}
