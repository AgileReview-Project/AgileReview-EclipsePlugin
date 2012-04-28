package org.agilereview.storage.xml.handler;

import org.agilereview.storage.xml.wizards.noreviewsource.NoReviewSourceProjectWizard;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Handler for switching the Review Source Project in the Review Explorer. 
 * This Handler simply opens the preferences dialog.
 */
public class SwitchReviewSourceProjectHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		//TODO get this from Properties (externalize Strings?)
		NoReviewSourceProjectWizard dialog = new NoReviewSourceProjectWizard();
		WizardDialog wDialog = new WizardDialog(HandlerUtil.getActiveShell(event), dialog);
		wDialog.setBlockOnOpen(true);
		wDialog.open();
	
		return null;
	}

}