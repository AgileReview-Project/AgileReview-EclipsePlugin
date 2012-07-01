/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.reviewExplorer;

import org.agilereview.core.external.definition.IStorageClient;
import org.agilereview.core.external.storage.Review;
import org.agilereview.core.external.storage.ReviewSet;
import org.agilereview.ui.basic.Activator;
import org.agilereview.ui.basic.commentSummary.CSTableViewer;
import org.agilereview.ui.basic.commentSummary.CommentSummaryView;
import org.agilereview.ui.basic.commentSummary.table.ContentProvider;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * The Review Explorer is the view which shows all reviews as well as the files and folder which are commented in the corresponding reviews.
 * 
 * @author Thilo Rauch (28.03.2012)
 */
public class ReviewExplorerView extends ViewPart {

    /**
     * The values describe the currently shown content of the {@link ReviewExplorerView}
     * @author Malte Brunnlieb (30.05.2012)
     */
    private enum ViewContentState {
        /**
         * Stays for the state "No StorageClient connected"
         */
        DISCONNECTED,
        /**
         * Stays for the state where a StorageClient is available and review content will be shown
         */
        CONNECTED
    };

    /**
     * The tree for showing the reviews
     */
    private TreeViewer treeViewer;
    /**
     * Current Instance used by the ViewPart
     */
    private static ReviewExplorerView instance;
    /**
     * The content provider for the {@link TreeViewer}
     */
    private REContentProvider contentProvider;
    /**
     * Parent of this {@link ViewPart}
     */
    private Composite parent;
    /**
     * Identifies the current state of the UI
     */
    private ViewContentState contentState;

    public static ReviewExplorerView getInstance() {
        return instance;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     * @author Thilo Rauch (06.05.2012)
     */
    @Override
    public void createPartControl(Composite parent) {
        this.parent = parent;
        parent.setLayout(new GridLayout());
        REContentProvider.bindView(this);
        // XXX Warum sync?
        synchronized (this.parent) {
            if (contentProvider == null) {
                buildDummyUI();
            } else {
                buildWorkingUI();
            }
        }
    }

    private void buildWorkingUI() {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                // If we are already connected, no need to create anything
                if (contentState == ViewContentState.CONNECTED)
                    return;
                // parent.setLayout(null);
                clearParent();
                contentState = ViewContentState.CONNECTED;
                // Create the treeview MULTI, H_SCROLL, V_SCROLL, and BORDER
                treeViewer = new TreeViewer(parent);
                treeViewer.setContentProvider(contentProvider);
                treeViewer.setLabelProvider(new RELabelProvider());
                treeViewer.setComparator(new REViewerComparator());
                // Needed so expanding/collapsing of IResources which are displayed multiple times works
                treeViewer.setUseHashlookup(true);

                GridData gd = new GridData();
                gd.grabExcessHorizontalSpace = true;
                gd.grabExcessVerticalSpace = true;
                gd.horizontalAlignment = GridData.FILL;
                gd.verticalAlignment = GridData.FILL;
                // gd.widthHint = parent.getMonitor().getClientArea().width;
                treeViewer.getTree().setLayoutData(gd);
                parent.layout();

                // TODO: Ähnliches Konstrukt in der neuen Architektur?
                // treeViewer.addSelectionChangedListener(ViewControl.getInstance());
                ReviewSet set = new ReviewSet();
                set.add(new Review("blubb"));
                refreshInput(set);

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
        });
    }

    /**
     * Displays a message that no {@link IStorageClient} provides data
     * @author Malte Brunnlieb (27.05.2012)
     */
    private void buildDummyUI() {
        Display.getDefault().syncExec(new Runnable() {

            @Override
            public void run() {
                if (contentState == ViewContentState.DISCONNECTED)
                    return;
                clearParent();
                contentState = ViewContentState.DISCONNECTED;

                Label label = new Label(parent, SWT.WRAP | SWT.CENTER);
                label.setText("No data available as currently no StorageClient is connected.");
                GridData gd = new GridData();
                gd.grabExcessHorizontalSpace = true;
                gd.grabExcessVerticalSpace = true;
                gd.horizontalAlignment = GridData.CENTER;
                gd.verticalAlignment = GridData.CENTER;
                gd.widthHint = parent.getMonitor().getClientArea().width;
                label.setLayoutData(gd);
                parent.pack();
            }
        });
    }

    /**
     * Disposes all children of the current parent.
     * @author Malte Brunnlieb (27.05.2012)
     */
    private void clearParent() {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                treeViewer = null;
                for (Control child : parent.getChildren()) {
                    child.dispose();
                }
                parent.layout();
            }
        });
    }

    /**
     * Binds the given {@link ContentProvider} to the {@link CSTableViewer} instance of this view. If the parameter is net to null, the
     * {@link CommentSummaryView} will display a no {@link IStorageClient} registered message instead of a table
     * @param tableModel model for the {@link CSTableViewer}
     * @return The TableViewer the model was bound to. Will be null if the parameter has been set to null.
     * @author Malte Brunnlieb (27.05.2012)
     */
    public void bindContentModel(REContentProvider contentModel) {
        synchronized (parent) {
            contentProvider = contentModel;
            if (contentProvider != null) {
                if (treeViewer == null) {
                    buildWorkingUI();
                }
            } else {
                buildDummyUI();
            }
        }
    }

    /**
     * Sets the input of the ReviewExplorer completely new
     * @author Thilo Rauch (06.05.2012)
     */
    public void refreshInput(final Object input) {
        if (this.treeViewer != null) {
            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    // Save previous selection
                    ISelection selection = treeViewer.getSelection();
                    // Save expansion state
                    treeViewer.getControl().setRedraw(false);
                    TreePath[] expandedElements = treeViewer.getExpandedTreePaths();

                    // Refresh the input
                    treeViewer.setInput(input);
                    treeViewer.refresh();

                    // Expand nodes again
                    for (Object o : expandedElements) {
                        treeViewer.expandToLevel(o, 1);
                    }
                    treeViewer.getControl().setRedraw(true);
                    treeViewer.getControl().redraw();

                    //Reset selection
                    treeViewer.setSelection(selection, true);
                }
            });
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

    @Override
    public void setFocus() {
    }

}