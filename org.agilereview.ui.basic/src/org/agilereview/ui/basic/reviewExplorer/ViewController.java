/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.reviewExplorer;

import org.agilereview.common.exception.ExceptionHandler;
import org.agilereview.core.external.storage.CommentingAPI;
import org.agilereview.core.external.storage.Review;
import org.agilereview.core.external.storage.constants.ReviewSetMetaDataKeys;
import org.agilereview.ui.basic.Activator;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 * Handles events in ReviewExplorer
 * @author Thilo Rauch (18.05.2014)
 */
class ViewController implements IDoubleClickListener, ISelectionChangedListener {
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
     * @author Thilo Rauch (18.05.2014)
     */
    @Override
    public void selectionChanged(SelectionChangedEvent event) {
        ISelection selection = event.getSelection();
        if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
            Object firstElement = ((IStructuredSelection) selection).getFirstElement();
            if (firstElement instanceof Review) {
                CommentingAPI.storeMetaValue(ReviewSetMetaDataKeys.SHOW_IN_DETAIL_VIEW, firstElement);
            }
        }
    }
    
    /**
     * Controls what will happen if the user double-clicks an item in the {@link ReviewExplorerView}. There are multiple possibilities: 1. A
     * {@link Review} is clicked 1.1 The review is open 1.1.1 The review is not active --> activate it 1.1.2 The review is active --> expand/collapse
     * the review node 1.2 The review is closed --> open it 2. A {@link IFile} is clicked --> Open the file 3. Else --> expand/collapse the clicked
     * node
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
                    Review r = (Review) o;
                    if (r.getIsOpen()) {
                        // case 1.1 The review is open
                        if (r.getIsActive()) {
                            // case 1.1.2
                            expandOrCollapse(treeViewer, o);
                        } else {
                            // case 1.1.1
                            // Review is open -> activate it
                            r.setToActive();
                        }
                    } else {
                        // case 1.2: If the review is closed -> open it
                        r.setIsOpen(true);
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
