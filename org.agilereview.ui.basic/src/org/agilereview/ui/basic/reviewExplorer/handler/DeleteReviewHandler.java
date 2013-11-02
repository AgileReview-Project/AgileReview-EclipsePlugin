package org.agilereview.ui.basic.reviewExplorer.handler;

import org.agilereview.common.exception.ExceptionHandler;
import org.agilereview.core.external.exception.NoOpenEditorException;
import org.agilereview.core.external.storage.CommentingAPI;
import org.agilereview.core.external.storage.Review;
import org.agilereview.ui.basic.Activator;
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
                        try {
                            CommentingAPI.deleteReview(((Review) o).getId());
                        } catch (NoOpenEditorException e) {
                            ExceptionHandler
                                    .logAndNotifyUser(
                                            "At the moment all files containing comments from this review have to be open in the editor. This was not the case. It will be fixed in the future",
                                            e, Activator.PLUGIN_ID);
                        }
                    }
                }
            }
        }
        
        return null;
    }
}
