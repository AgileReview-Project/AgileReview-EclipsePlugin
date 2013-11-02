/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.commentSummary.handler;

import org.agilereview.common.exception.ExceptionHandler;
import org.agilereview.common.preferences.PreferencesAccessor;
import org.agilereview.core.external.exception.NoOpenEditorException;
import org.agilereview.core.external.exception.NullArgumentException;
import org.agilereview.core.external.preferences.AgileReviewPreferences;
import org.agilereview.core.external.storage.CommentingAPI;
import org.agilereview.ui.basic.Activator;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * 
 * @author Malte Brunnlieb (26.11.2012)
 */
public class NewCommentHandler extends AbstractHandler {
    
    /* (non-Javadoc)
     * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
     * @author Malte Brunnlieb (26.11.2012)
     */
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        
        PreferencesAccessor pa = new PreferencesAccessor();
        String author = pa.get(AgileReviewPreferences.AUTHOR);
        String activeReview = pa.get(AgileReviewPreferences.ACTIVE_REVIEW_ID);
        
        if (author.trim().isEmpty() || activeReview.trim().isEmpty()) {
            ExceptionHandler.warnUser("You cannot create a new comment as currently there is no active review or author specified."); //TODO offer support (create/activate new review or specify author)
        } else {
            try {
                CommentingAPI.createComment(author, activeReview);
            } catch (NoOpenEditorException e) {
                ExceptionHandler.warnUser("There is currently no editor open for commenting anything.");
            } catch (NullArgumentException e) {
                ExceptionHandler.logAndNotifyUser(e.getMessage(), e, Activator.PLUGIN_ID);
            }
        }
        
        return null;
    }
}
