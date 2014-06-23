package org.agilereview.ui.basic.reviewExplorer.handler;

import java.io.IOException;

import org.agilereview.common.exception.ExceptionHandler;
import org.agilereview.common.ui.PlatformUITools;
import org.agilereview.core.external.storage.CommentingAPI;
import org.agilereview.core.external.storage.Review;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteReviewHandler extends AbstractHandler {
    
    /**
     * Logger instance
     */
    public static final Logger LOG = LoggerFactory.getLogger(DeleteReviewHandler.class);
    
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        // ISelection sel = HandlerUtil.getCurrentSelection(event);
        // Actually, the code above should be working. But as it sometimes gives the wrong selection in e4, use the line below
        ISelection sel = PlatformUITools.getActiveWorkbenchPage().getSelection();
        if (sel != null) {
            if (sel instanceof IStructuredSelection) {
                for (Object o : ((IStructuredSelection) sel).toArray()) {
                    if (o instanceof Review) {
                        try {
                            CommentingAPI.deleteReview((Review) o);
                        } catch (IOException e) {
                            LOG.error("Review could not be deleted! One of the reviewed files could not be read or written", e);
                            ExceptionHandler.warnUser("One of the reviewed files could not be read or written: " + e.getMessage());
                        }
                    }
                }
            }
        }
        
        return null;
    }
}
