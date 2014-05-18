/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.external.reviewDataReceiver;

import org.agilereview.core.external.definition.IStorageClient;
import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Review;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.part.ViewPart;

/**
 * This class is part of the framework for providing views with review information. Extend this abstract class and implement the abstract methods to
 * get a view which should display review data. If you use this class and {@link AbstractReviewDataReceiver} then both classes will connect to each
 * other so the review data will be pushed into the view. Both, View and DataReceiver, are created by eclipse but these abstract classes handle nearly
 * every aspect regarding instance control for you.
 * 
 * The only thing the extending class has to do is capture the instance created by the framework and provide it trough a static getInstance() method.
 * Example
 * 
 * <p><blockquote><pre> <code> class MyView extends AbstractReviewDataView { <br> &nbsp; private static MyView instance;
 * 
 * &nbsp; public MyView() { super(); instance = this; }
 * 
 * &nbsp; public static MyView getInstance() { return instance; } }
 * 
 * </code> </blockquote></pre></p>
 * 
 * @author Thilo Rauch (07.07.2012)
 */
public abstract class AbstractReviewDataView extends ViewPart {
    
    /**
     * The values describe the currently shown content of the {@link AbstractReviewDataView}
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
    protected Composite parent;
    
    /**
     * Layout initially set on the parent. Saved for restoring after a dummy UI was shown
     */
    private Layout intialLayout;
    
    /**
     * Data shown by this view (as it was set by the {@link AbstractReviewDataReceiver}. If <code>null</code>, then now storage client is connected
     */
    private Object viewInput;
    
    /**
     * Enum {@link ViewContent} indicating what is currently shown in the UI
     */
    private ViewContent viewContent = ViewContent.NONE;
    
    /**
     * Simple object for thread synchronization
     */
    private final Object syncObject = new Object();
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     * @author Thilo Rauch (07.07.2012)
     */
    @Override
    public final void createPartControl(Composite parent) {
        this.parent = parent;
        // Capture old layout
        intialLayout = parent.getLayout();
        AbstractReviewDataReceiver.bindViewOn(this, getReviewDataReceiverClass());
        buildRightUI();
    }
    
    /**
     * Method for the {@link AbstractReviewDataReceiver} to push the review data into the view. Input is either null (indicating no StorageClient) or
     * review data. The input may have been transformed by the user (using the
     * {@link AbstractReviewDataReceiver#transformData(org.agilereview.core.external.storage.ReviewSet) transformData} method of the
     * AbstractReviewDataReceiver. Is called asynchronously in the UI-Thread.
     * @param input
     * @author Thilo Rauch (13.07.2012)
     */
    final void setInput(final Object input) {
        synchronized (syncObject) {
            viewInput = input;
            buildRightUI();
            if (input != null) {
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        refreshInput(input);
                    }
                });
            }
        }
    }
    
    /**
     * Method for the binding framework to find the data receiver corresponding to this view. Should look like (with regard to sample):
     * <p><blockquote>@Override<br>public Class<? extends AbstractReviewDataReceiver> getReviewDataReceiverClass() {<br> <dd>return
     * MyViewDataReceiver.class;<br>}</blockquote></p>
     * @return Class of the corresponding data receiver
     * @author Thilo Rauch (13.07.2012)
     */
    protected abstract Class<? extends AbstractReviewDataReceiver> getReviewDataReceiverClass();
    
    /**
     * Method for the user to build the view's UI.
     * @param parent parent component to add the UI controls to
     * @param initalInput inital input to the view (if something was pushed to the view before this method was called). May be null.
     * @author Thilo Rauch (13.07.2012)
     */
    protected abstract void buildUI(Composite parent, Object initalInput);
    
    /**
     * Method for refreshing the input on the UI. Is called by the framework when new review data is pushed from the Storage but can also be called by
     * the user. A typical use of this method is to call setInput() on a viewer shown in the view (possibly doing other stuff before and after).
     * @param reviewData new data
     * @author Thilo Rauch (13.07.2012)
     */
    protected abstract void refreshInput(Object reviewData);
    
    /**
     * Build either the DummyUI or the user-defined UI (based on {@link #viewInput}). If the requested UI is already shown, nothing is done.
     * @author Thilo Rauch (13.07.2012)
     */
    private void buildRightUI() {
        synchronized (syncObject) {
            // Ensure execution in UI thread
            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    if (viewInput != null) {
                        if (viewInput instanceof Comment || viewInput instanceof Review) {
                            if (viewContent == ViewContent.WORKING) return;
                            viewContent = ViewContent.WORKING;
                            clearParent();
                            buildUI(parent, viewInput);
                        } else {
                            if (viewContent == ViewContent.NONE) return;
                            viewContent = ViewContent.NONE;
                            clearParent();
                            buildUI(parent, viewInput);
                        }
                    } else {
                        if (viewContent == ViewContent.DUMMY) return;
                        viewContent = ViewContent.DUMMY;
                        clearParent();
                        buildDummyUI();
                    }
                    parent.layout();
                }
            });
        }
    }
    
    /**
     * Displays a message that no {@link IStorageClient} is connected to provide data.
     * @author Malte Brunnlieb (27.05.2012)
     */
    private void buildDummyUI() {
        parent.setLayout(new GridLayout());
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
    
    /**
     * Disposes all children of the current parent, resetting the layout
     * @author Malte Brunnlieb (27.05.2012)
     */
    protected void clearParent() {
        for (Control child : parent.getChildren()) {
            child.dispose();
        }
        parent.setLayout(intialLayout);
    }
}
