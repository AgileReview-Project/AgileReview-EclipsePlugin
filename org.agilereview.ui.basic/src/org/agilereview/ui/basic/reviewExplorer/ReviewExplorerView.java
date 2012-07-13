/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.reviewExplorer;

import org.agilereview.ui.basic.Activator;
import org.agilereview.ui.basic.external.reviewDataReceiver.AbstractReviewDataReceiver;
import org.agilereview.ui.basic.external.reviewDataReceiver.AbstractReviewDataView;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;

/**
 * The Review Explorer is the view which shows all reviews as well as the files and folder which are commented in the corresponding reviews.
 * 
 * @author Thilo Rauch (28.03.2012)
 */
public class ReviewExplorerView extends AbstractReviewDataView {

    /**
     * The tree for showing the reviews
     */
    private TreeViewer treeViewer;

    /**
     * Current Instance used by the ViewPart. Part of the ReviewDataView pattern.
     */
    private static ReviewExplorerView instance;

    /**
     * Constructor used to capture the instance created by eclipse. Part of the ReviewDataView pattern.
     * @author Thilo Rauch (13.07.2012)
     */
    public ReviewExplorerView() {
        super();
        instance = this;
    }

    /**
     * GetInstance() method to provide access to the instance created by Eclipse. Part of the ReviewDataView pattern.
     * @return instance of this class created by eclipse
     * @author Thilo Rauch (13.07.2012)
     */
    public static ReviewExplorerView getInstance() {
        return instance;
    }

    @Override
    public void setFocus() {
    }

    /* (non-Javadoc)
     * @see org.agilereview.ui.basic.external.reviewDataReceiver.AbstractReviewDataView#buildUI(org.eclipse.swt.widgets.Composite)
     * @author Thilo Rauch (07.07.2012)
     */
    @Override
    protected void buildUI(final Composite parent, final Object initialInput) {
        // Create the treeview MULTI, H_SCROLL, V_SCROLL, and BORDER
        treeViewer = new TreeViewer(parent);
        treeViewer.setContentProvider(new REContentProvider());
        treeViewer.setLabelProvider(new RELabelProvider());
        treeViewer.setComparator(new REViewerComparator());
        // Needed so expanding/collapsing of IResources which are displayed multiple times works
        treeViewer.setUseHashlookup(true);
        if (initialInput != null) {
            treeViewer.setInput(initialInput);
        }

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
    }

    /* (non-Javadoc)
     * @see org.agilereview.ui.basic.external.reviewDataReceiver.AbstractReviewDataView#getReviewDataReceiverClass()
     * @author Thilo Rauch (07.07.2012)
     */
    @Override
    protected Class<? extends AbstractReviewDataReceiver> getReviewDataReceiverClass() {
        // TODO Auto-generated method stub
        return REDataReceiver.class;
    }

    /**
     * Sets the input of the ReviewExplorer completely new
     * @author Thilo Rauch (06.05.2012)
     */
    @Override
    protected void refreshInput(final Object input) {
        if (this.treeViewer != null) {
            ISelection selection = treeViewer.getSelection();
            // Save expansion state
            treeViewer.getControl().setRedraw(false);
            TreePath[] expandedElements = treeViewer.getExpandedTreePaths();

            // Refresh the input
            treeViewer.setInput(input);
            // treeViewer.refresh();

            // Expand nodes again
            for (Object o : expandedElements) {
                treeViewer.expandToLevel(o, 1);
            }
            treeViewer.getControl().setRedraw(true);
            treeViewer.getControl().redraw();

            //Reset selection
            treeViewer.setSelection(selection, true);
        }
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

}