/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.reviewExplorer;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * This handler takes care of the command for selective expand of all sub nodes
 * of a given node in the ReviewExplorer (CommandId: org.eclipse.ui.navigate.expandAll)
 * @author Thilo Rauch (07.05.2012)
 */
public class REExpandHandler extends AbstractHandler {

    /* (non-Javadoc)
     * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
     * @author Thilo Rauch (07.05.2012)
     */
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        // Evaluate selection
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        if(selection != null) {
            if(selection instanceof ITreeSelection) {
                // For each selected object, expand all subnodes
                for(TreePath p : ((ITreeSelection) selection).getPaths()) {
                    ReviewExplorerView.getInstance().expandAllSubNodes(p);
                }
            }
        }
        return null;
    }

}
