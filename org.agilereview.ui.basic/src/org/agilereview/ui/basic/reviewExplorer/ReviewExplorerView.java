/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.reviewExplorer;

import java.util.ArrayList;
import java.util.List;

import org.agilereview.core.external.definition.IReviewDataReceiver;
import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Review;
import org.agilereview.core.external.storage.ReviewList;
import org.agilereview.ui.basic.Activator;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * The Review Explorer is the view which shows all reviews as well as the files and folder which are commented in the corresponding reviews.
 * 
 * @author Thilo Rauch (28.03.2012)
 */
public class ReviewExplorerView extends ViewPart implements IReviewDataReceiver {
    
    /**
     * All review data
     */
    private List<Review> globalData = new ArrayList<Review>();
    /**
     * The tree for showing the reviews
     */
    private TreeViewer treeViewer;
    /**
     * Current Instance used by the ViewPart
     */
    private static ReviewExplorerView instance = null;
    
    /**
     * Returns the current instance of the ReviewExplorer
     * @return current instance
     * @author Thilo Rauch (06.05.2012)
     */
    public static ReviewExplorerView getInstance() {
        return instance;
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.core.external.definition.IReviewDataReceiver#setReviewData(java.util.List)
     * @author Thilo Rauch (28.03.2012)
     */
    @Override
    public void setReviewData(ReviewList reviews) {
        this.globalData = reviews;
        try {
            // delete old resource markers
            ResourcesPlugin.getWorkspace().getRoot().deleteMarkers(REContentProvider.AGILE_REVIEW_MARKER_ID, false, IResource.DEPTH_INFINITE);
            // now prepare the new markers
        } catch (CoreException e) {
            String msg = "ReviewExplorer: setReviewData(): CoreException while deleting markers. A resource may not be available.";
            Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, msg, e));
        }
        for (Review r : globalData) {
            for (Comment c : r.getComments()) {
                try {
                    c.getCommentedFile().createMarker(REContentProvider.AGILE_REVIEW_MARKER_ID);
                } catch (CoreException e) {
                    String msg = "ReviewExplorer: setReviewData(): CoreException while adding marker to '" + c.getCommentedFile() + "'."
                            + "The resource may not be available.";
                    Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, msg, e));
                }
            }
        }
        if (instance != null && treeViewer != null) {
            refreshInput();
        }
    }
    
    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     * @author Thilo Rauch (06.05.2012)
     */
    @Override
    public void createPartControl(Composite parent) {
        instance = this;
        
        // Create the treeview MULTI, H_SCROLL, V_SCROLL, and BORDER
        treeViewer = new TreeViewer(parent);
        treeViewer.setContentProvider(new REContentProvider());
        treeViewer.setLabelProvider(new RELabelProvider());
        treeViewer.setComparator(new REViewerComparator());
        // Needed so expanding/collapsing of IResources which are displayed multiple times works
        treeViewer.setUseHashlookup(true);
        
        // TODO: Ähnliches Konstrukt in der neuen Architektur?
        // treeViewer.addSelectionChangedListener(ViewControl.getInstance());
        refreshInput();
        
        treeViewer.addDoubleClickListener(new REDoubleClickListener());
        // TODO: Still supported? 
        PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, Activator.PLUGIN_ID + ".ReviewExplorer");
        
        // Create a popup menu
        MenuManager menuManager = new MenuManager();
        Menu menu = menuManager.createContextMenu(treeViewer.getControl());
        // Set the MenuManager
        treeViewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuManager, treeViewer);
        getSite().setSelectionProvider(treeViewer);
        
        // register view
        // TODO Ähnliches Konzept? ViewControl.registerView(this.getClass());
    }
    
    /**
     * Sets the input of the ReviewExplorer completely new
     * @author Thilo Rauch (06.05.2012)
     */
    public void refreshInput() {
        // Save previous selection
        ISelection selection = this.treeViewer.getSelection();
        // Save expansion state
        this.treeViewer.getControl().setRedraw(false);
        TreePath[] expandedElements = this.treeViewer.getExpandedTreePaths();
        
        // Refresh the input
        treeViewer.setInput(globalData);
        treeViewer.refresh();
        
        // Expand nodes again
        this.treeViewer.refresh();
        for (Object o : expandedElements) {
            this.treeViewer.expandToLevel(o, 1);
        }
        this.treeViewer.getControl().setRedraw(true);
        this.treeViewer.getControl().redraw();
        
        //Reset selection
        this.treeViewer.setSelection(selection, true);
    }
    
    /**
     * Expands all sub nodes of the passed node
     * @param selection node which should be expanded
     */
    public void expandAllSubNodes(Object selection) {
        treeViewer.expandToLevel(selection, TreeViewer.ALL_LEVELS);
    }
    
    /**
     * Collapses all sub nodes of the passed node
     * @param selection node which should be expanded
     */
    public void collapseAllSubNodes(Object selection) {
        treeViewer.collapseToLevel(selection, TreeViewer.ALL_LEVELS);
    }
    
    // TODO: Überlegen wofür das war...
    //	/**
    //	 * Validates the ReviewExplorer selection in order to update the CONTAINS_CLOSED_REVIEW variable of the {@link SourceProvider}
    //	 */
    //	public void validateExplorerSelection() {
    //		selectionChanged(new SelectionChangedEvent(this.treeViewer, this.treeViewer.getSelection()));
    //	}
    //	
    //	/**
    //	 * Will be called by the {@link ViewControl} when the selection was changed and changes
    //	 * @param event
    //	 * @see de.tukl.cs.softech.agilereview.views.ViewControl#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
    //	 */
    //	public void selectionChanged(SelectionChangedEvent event) {
    //		if (event.getSelection() instanceof IStructuredSelection) {
    //			IStructuredSelection sel = (IStructuredSelection) event.getSelection();
    //			Iterator<?> it = sel.iterator();
    //			boolean containsClosedReview = false, firstReviewIsActive = false, firstIteration = true;
    //			while (it.hasNext() && !containsClosedReview) {
    //				Object o = it.next();
    //				if (o instanceof MultipleReviewWrapper) {
    //					if (!((MultipleReviewWrapper) o).isOpen()) {
    //						containsClosedReview = true;
    //					}
    //					if (firstIteration) {
    //						if (PropertiesManager.getPreferences().getString(PropertiesManager.EXTERNAL_KEYS.ACTIVE_REVIEW).equals(
    //								((MultipleReviewWrapper) o).getReviewId())) {
    //							firstReviewIsActive = true;
    //						}
    //					}
    //				}
    //				firstIteration = false;
    //			}
    //			ISourceProviderService isps = (ISourceProviderService) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getService(
    //					ISourceProviderService.class);
    //			SourceProvider sp1 = (SourceProvider) isps.getSourceProvider(SourceProvider.CONTAINS_CLOSED_REVIEW);
    //			sp1.setVariable(SourceProvider.CONTAINS_CLOSED_REVIEW, containsClosedReview);
    //			SourceProvider sp2 = (SourceProvider) isps.getSourceProvider(SourceProvider.IS_ACTIVE_REVIEW);
    //			sp2.setVariable(SourceProvider.IS_ACTIVE_REVIEW, firstReviewIsActive);
    //		}
    //	}
    
    @Override
    public void setFocus() {
    }
    
}