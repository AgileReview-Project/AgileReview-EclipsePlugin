package org.agilereview.ui.basic.reviewExplorer.handler;

import org.agilereview.core.external.storage.Review;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

public class DeleteReviewHandler extends AbstractHandler {
    
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ISelection sel = HandlerUtil.getCurrentSelection(event);
        if (sel != null) {
            if (sel instanceof IStructuredSelection) {
                for (Object o : ((IStructuredSelection) sel).toArray()) {
                    if (o instanceof Review) {
                        Review r = (Review) o;
                        // TODO ReviewSet connection needed to delete Review
                        // Or CommentingAPI?
                    }
                }
            }
        }
        
        return null;
    }
    
}
