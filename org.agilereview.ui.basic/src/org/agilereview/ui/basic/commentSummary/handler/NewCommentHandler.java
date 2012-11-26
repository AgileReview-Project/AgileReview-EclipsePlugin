/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.commentSummary.handler;

import org.agilereview.core.external.exception.EditorCurrentlyNotOpenException;
import org.agilereview.core.external.exception.FileNotSupportedException;
import org.agilereview.core.external.exception.NullArgumentException;
import org.agilereview.core.external.exception.UnknownException;
import org.agilereview.core.external.preferences.AgileReviewPreferences;
import org.agilereview.core.external.storage.PojoFactory;
import org.agilereview.ui.basic.tools.ExceptionHandler;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;

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
        
        IScopeContext[] scopes = new IScopeContext[] { InstanceScope.INSTANCE, DefaultScope.INSTANCE };
        String author = Platform.getPreferencesService().getString("org.agilereview.core", AgileReviewPreferences.AUTHOR, "", scopes);
        String activeReview = Platform.getPreferencesService().getString("org.agilereview.core", AgileReviewPreferences.ACTIVE_REVIEW_ID, "", scopes);
        
        if (author.trim().isEmpty() || activeReview.trim().isEmpty()) {
            ExceptionHandler.notifyUser(MessageDialog.WARNING,
                    "You cannot create a new comment as currently there is no active review or author specified."); //TODO offer support (create/activate new review or specify author)
        } else {
            try {
                PojoFactory.createComment(author, activeReview);
            } catch (FileNotSupportedException e) {
                ExceptionHandler.notifyUser(MessageDialog.WARNING, "The current file is not supported for commenting with multi line comments."); //TODO offer support for file support specification
            } catch (EditorCurrentlyNotOpenException e) {
                ExceptionHandler.notifyUser(MessageDialog.WARNING, "There is currently no editor open for commenting anything.");
            } catch (NullArgumentException e) {
                ExceptionHandler.logAndNotifyUser(e.getMessage(), e);
            } catch (UnknownException e) {
                ExceptionHandler.logAndNotifyUser(e.getMessage(), e);
                e.printStackTrace();
            }
        }
        
        return null;
    }
}
