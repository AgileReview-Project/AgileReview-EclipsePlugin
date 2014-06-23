package org.agilereview.ui.basic.reviewExplorer.handler;

import org.agilereview.common.ui.PlatformUITools;
import org.agilereview.core.external.storage.Review;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

public class ActivateReviewHandler extends AbstractHandler {
    
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        // ISelection sel = HandlerUtil.getCurrentSelection(event);
        // Actually, the code above should be working. But as it sometimes gives the wrong selection in e4, use the line below
        ISelection sel = PlatformUITools.getActiveWorkbenchPage().getSelection();
        if (sel != null) {
            if (sel instanceof IStructuredSelection) {
                for (Object o : ((IStructuredSelection) sel).toArray()) {
                    if (o instanceof Review && ((Review) o).getIsOpen()) {
                        ((Review) o).setToActive();
                    }
                }
            }
        }
        return null;
    }
    
}
