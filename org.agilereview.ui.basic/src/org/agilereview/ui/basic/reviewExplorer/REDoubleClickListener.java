/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.reviewExplorer;

import org.agilereview.common.exception.ExceptionHandler;
import org.agilereview.core.external.preferences.AgileReviewPreferences;
import org.agilereview.core.external.storage.Review;
import org.agilereview.ui.basic.Activator;
import org.agilereview.ui.basic.tools.PreferencesAccessor;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.ide.IDE;

/**
 * Controls what will happen if the user double-clicks an item in the {@link ReviewExplorerView}. There are multiple possibilities: 1. A
 * {@link Review} is clicked 1.1 The review is open 1.1.1 The review is not active --> activate it 1.1.2 The review is active --> expand/collapse the
 * review node 1.2 The review is closed --> open it 2. A {@link IFile} is clicked --> Open the file 3. Else --> expand/collapse the clicked node
 * @author Thilo Rauch (12.05.2012)
 */
public class REDoubleClickListener implements IDoubleClickListener {
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
     * @author Thilo Rauch (12.05.2012)
     */
    @Override
    public void doubleClick(DoubleClickEvent event) {
        ISelection sel = event.getSelection();
        if (sel instanceof ITreeSelection) {
            TreePath path = ((ITreeSelection) sel).getPaths()[0];
            Object o = path.getLastSegment();
            if (event.getViewer() instanceof TreeViewer) {
                TreeViewer treeViewer = (TreeViewer) event.getViewer();
                // case 1: A Review is clicked
                if (o instanceof Review) {
                    if (((Review) o).getIsOpen()) {
                        // case 1.1 The review is open
                        String activeReview = new PreferencesAccessor().get(AgileReviewPreferences.ACTIVE_REVIEW_ID);
                        if (activeReview.equals(((Review) o).getId())) {
                            // case 1.1.2
                            expandOrCollapse(treeViewer, o);
                        } else {
                            // case 1.1.1
                            // Review is open -> activate it
                            executeCommand("org.agilereview.core.activateReview"); // TODO String auslagern?
                        }
                    } else {
                        // case 1.2: If the review is closed -> open it
                        executeCommand("org.agilereview.core.openCloseReview"); // TODO String auslagern?
                    }
                } else if (o instanceof IFile) {
                    // case 2: An IFile is selected --> open it
                    openIFileInEditor((IFile) o);
                } else {
                    // case 3: anything else --> just expand or collapse
                    // On Double-Click there can only be one item selected
                    expandOrCollapse(treeViewer, path);
                }
            }
        }
    }
    
    /**
     * Opens the given {@link IFile} in an editor.
     * @param file File to open
     * @author Thilo Rauch (12.05.2012)
     */
    private void openIFileInEditor(IFile file) {
        if (file.exists()) {
            try {
                IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), file);
            } catch (PartInitException e) {
                ExceptionHandler.logAndNotifyUser(e, Activator.PLUGIN_ID);
            } catch (NullPointerException e) {
                // A NullPointer may occur, if the workbench window (or page) is currently not accessible
                ExceptionHandler.logAndNotifyUser("The file could not be opened. Please make sure the eclipse main window has the focus", e,
                        Activator.PLUGIN_ID);
            }
        } else {
            ExceptionHandler.warnUser("Could not open file '" + file.getFullPath()
                    + "'!\nFile not existent in workspace or respective project may be closed!");
        }
    }
    
    /**
     * Executes the given command and handles the exceptions
     * @param commandId command to execute
     * @author Thilo Rauch (12.05.2012)
     */
    private void executeCommand(String commandId) {
        // Execute activation command
        try {
            IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(IHandlerService.class);
            handlerService.executeCommand(commandId, null);
        } catch (ExecutionException e) {
            String msg = "Problems occured executing command \"" + commandId + "\", after double-click in ReviewExplorer";
            Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, msg, e));
        } catch (NotDefinedException e) {
            String msg = "Command \"" + commandId + "\" is not defined, after double-click in ReviewExplorer";
            Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, msg, e));
        } catch (NotEnabledException e) {
            String msg = "Command  \"" + commandId + "\" is not enabled, after double-click in ReviewExplorer";
            Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, msg, e));
        } catch (NotHandledException e) {
            String msg = "Command  \"" + commandId + "\" is not handled, after double-click in ReviewExplorer";
            Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, msg, e));
        }
    }
    
    /**
     * Helper method to expand or collapse (based on the current state) the given element of the given TreeViewer
     * @param treeViewer TreeViewer in which the element is displayed
     * @param element element to collapse/expand
     * @author Thilo Rauch (12.05.2012)
     */
    private void expandOrCollapse(TreeViewer treeViewer, Object element) {
        if (treeViewer.getExpandedState(element)) {
            treeViewer.collapseToLevel(element, 1);
        } else {
            treeViewer.expandToLevel(element, 1);
        }
    }
    
}
