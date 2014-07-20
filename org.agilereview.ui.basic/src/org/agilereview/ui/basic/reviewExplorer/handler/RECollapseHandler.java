/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.reviewExplorer.handler;

import org.agilereview.common.ui.PlatformUITools;
import org.agilereview.ui.basic.reviewExplorer.ReviewExplorerView;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;

/**
 * This handler takes care of the command for selective collapse of all sub nodes of a given node in the ReviewExplorer (CommandId:
 * org.eclipse.ui.navigate.collapseAll)
 * @author Thilo Rauch (07.05.2012)
 */
public class RECollapseHandler extends AbstractHandler {
    
    /* (non-Javadoc)
     * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
     * @author Thilo Rauch (07.05.2012)
     */
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        // ISelection sel = HandlerUtil.getCurrentSelection(event);
        // Actually, the code above should be working. But as it sometimes gives the wrong selection in e4, use the line below
        ISelection selection = PlatformUITools.getActiveWorkbenchPage().getSelection();
        if (selection != null) {
            if (selection instanceof ITreeSelection) {
                // For each selected object, collapse all subnodes
                for (TreePath p : ((ITreeSelection) selection).getPaths()) {
                    ReviewExplorerView.getInstance().collapseAllSubNodes(p);
                }
            }
        }
        return null;
    }
    
}
