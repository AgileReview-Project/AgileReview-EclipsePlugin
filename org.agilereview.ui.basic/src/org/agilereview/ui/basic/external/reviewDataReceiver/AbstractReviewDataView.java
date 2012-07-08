/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.external.reviewDataReceiver;

import org.agilereview.core.external.definition.IStorageClient;
import org.agilereview.ui.basic.commentSummary.CommentSummaryView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

/**
 * 
 * @author Thilo Rauch (07.07.2012)
 */
public abstract class AbstractReviewDataView extends ViewPart {

    /**
     * The values describe the currently shown content of the {@link CommentSummaryView}
     * @author Malte Brunnlieb (30.05.2012)
     */
    private enum ViewContent {
        /**
         * Represents the state "No StorageClient connected"
         */
        DUMMY,
        /**
         * Represents the state where a StorageClient is available and review content will be shown
         */
        WORKING,
        /**
         * Represents the state where no UI has been created yet (inital value)
         */
        NONE
    };

    /**
     * Parent of this {@link ViewPart}
     */
    private Composite parent;
    //    /**
    //     * Identifies whether a content is currently connected or not
    //     */
    //    private boolean isContentConnected = false; // XXX volatile oder so?

    private Object viewInput;

    private ViewContent viewContent = ViewContent.NONE;

    //    /**
    //     * Current Instance used by the ViewPart
    //     */
    //    private static AbstractReviewDataView instance;
    //
    //    public static AbstractReviewDataView getInstance() {
    //        return instance;
    //    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     * @author Thilo Rauch (07.07.2012)
     */
    @Override
    public void createPartControl(Composite parent) {
        // XXX remove later
        System.out.println("View created");
        this.parent = parent;
        parent.setLayout(new GridLayout());
        // give sub-classes possibility to initialize
        initialize();
        // XXX Think about sync
        synchronized (this.parent) {
            AbstractReviewDataReceiver.bindViewOn(this, getReviewDataReceiverClass());
            // XXX remove later
            System.out.println("View side binding finsihed");
            buildRightUI();
        }

    }

    //    /**
    //     * Binds the given {@link ContentProvider} to the {@link CSTableViewer} instance of this view. If the parameter is net to null, the
    //     * {@link CommentSummaryView} will display a no {@link IStorageClient} registered message instead of a table
    //     * @param tableModel model for the {@link CSTableViewer}
    //     * @return The TableViewer the model was bound to. Will be null if the parameter has been set to null.
    //     * @author Malte Brunnlieb (27.05.2012)
    //     */
    //    public void dataReceiverChanged(boolean connected) {
    //        synchronized (parent) {
    //            isContentConnected = connected;
    //            buildRightUI();
    //        }
    //    }

    public void setInput(Object input) {
        synchronized (parent) {
            viewInput = input;
            buildRightUI();
            if (input != null) {
                refreshInput(input);
            }
        }
    }

    private void buildRightUI() {
        // XXX remove later
        System.out.println("Build right UI called with input " + viewInput);
        if (viewInput != null) {
            if (viewContent == ViewContent.WORKING)
                return;
            viewContent = ViewContent.WORKING;
            clearParent();
            buildUI(parent, viewInput);
        } else {
            if (viewContent == ViewContent.DUMMY)
                return;
            viewContent = ViewContent.DUMMY;
            clearParent();
            buildDummyUI();
        }
    }

    public abstract Class<? extends AbstractReviewDataReceiver> getReviewDataReceiverClass();

    protected abstract void refreshInput(Object reviewData);

    protected abstract void buildUI(Composite parent, Object initalInput);

    protected abstract void initialize();

    /**
     * Displays a message that no {@link IStorageClient} provides data
     * @author Malte Brunnlieb (27.05.2012)
     */
    private void buildDummyUI() {
        Display.getDefault().syncExec(new Runnable() {

            @Override
            public void run() {
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
                // XXX treeViewer = null;
                for (Control child : parent.getChildren()) {
                    child.dispose();
                }
                parent.layout();
            }
        });
    }
}
