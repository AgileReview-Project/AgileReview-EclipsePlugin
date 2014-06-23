/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.reviewExplorer.handler;

import org.agilereview.common.ui.PlatformUITools;
import org.agilereview.core.external.storage.Review;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * 
 * @author Thilo Rauch (28.07.2012)
 */
public class OpenCloseReviewHandler extends AbstractHandler {
    
    /* (non-Javadoc)
     * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
     * @author Thilo Rauch (28.07.2012)
     */
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        // ISelection sel = HandlerUtil.getCurrentSelection(event);
        // Actually, the code above should be working. But as it sometimes gives the wrong selection in e4, use the line below
        ISelection sel = PlatformUITools.getActiveWorkbenchPage().getSelection();
        if (sel != null) {
            if (sel instanceof IStructuredSelection) {
                for (Object o : ((IStructuredSelection) sel).toArray()) {
                    if (o instanceof Review) {
                        Review r = (Review) o;
                        r.setIsOpen(!r.getIsOpen());
                    }
                }
            }
        }
        return null;
    }
}
