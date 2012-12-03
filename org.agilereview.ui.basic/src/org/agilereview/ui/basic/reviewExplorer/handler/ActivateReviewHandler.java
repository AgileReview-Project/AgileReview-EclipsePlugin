package org.agilereview.ui.basic.reviewExplorer.handler;

import org.agilereview.core.external.preferences.AgileReviewPreferences;
import org.agilereview.core.external.storage.Review;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

public class ActivateReviewHandler extends AbstractHandler {
    
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ISelection sel = HandlerUtil.getCurrentSelection(event);
        if (sel != null) {
            if (sel instanceof IStructuredSelection) {
                for (Object o : ((IStructuredSelection) sel).toArray()) {
                    if (o instanceof Review && ((Review) o).getIsOpen()) {
                        IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode("org.agilereview.core");
                        preferences.put(AgileReviewPreferences.ACTIVE_REVIEW_ID, ((Review) o).getId());
                    }
                }
            }
        }
        return null;
    }
    
}
