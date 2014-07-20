/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.detail;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class of a Comment or Review representation, which automatically provides IPartListener and FocusListener to save all modified data
 * persistently. Furthermore a listener implementation is provided for a revert action by setting the object Data to "revert" and for a save action by
 * setting the object Data to "save"
 * @param <E> type which would be displayed by this AbstractDetail
 * @author Thilo Rauch (20.07.2014)
 */
public abstract class AbstractDetail<E> extends Composite implements FocusListener, IWorkbenchListener {
    
    //    /**
    //     * This set represents all components which should adapt the composite background color
    //     */
    //    protected HashSet<Control> bgComponents = new HashSet<Control>();
    
    private static final Logger LOG = LoggerFactory.getLogger(AbstractDetail.class);
    
    protected E detailObject;
    
    /**
     * Creates a new AbstractDetail Composite onto the given parent with the specified SWT styles
     * @param parent onto the ReviewDetail Composite will be added
     * @param style with which this Composite will be styled
     */
    protected AbstractDetail(Composite parent, int style) {
        super(parent, style);
        initUI();
        PlatformUI.getWorkbench().addWorkbenchListener(this);
    }
    
    /**
     * this method will be automatically called by the constructor and should contain the initialization of the UI
     */
    protected abstract void initUI();
    
    /**
     * saveChanges will be called by the IPartListener and FocusListener and should contain a save routine on object level of the current modified
     * data
     */
    protected abstract void saveChanges();
    
    /**
     * fills all contents of the given input into the detail view
     * @param input which should be displayed
     */
    protected abstract void fillContents();
    
    /**
     * fills all contents of the given input into the detail view
     * @param input which should be displayed
     */
    public void setDetailObject(E input) {
        detailObject = input;
        fillContents();
    }
    
    protected E getDetailObject() {
        return detailObject;
    }
    
    //    /**
    //     * Converts all line breaks either \n or \r to \r\n line breaks
    //     * @param in the string which line breaks should be converted
    //     * @return the converted string
    //     */
    //    protected String convertLineBreaks(String in) {
    //        return in.replaceAll("\r\n|\r|\n", System.getProperty("line.separator"));
    //    }
    //    
    //    /**
    //     * // * Changes the background color for this AbstractDetail. //
    //     */
    //    protected void refreshBackgroundColor() {
    //        Color bg = determineBackgroundColor();
    //        this.setBackground(bg);
    //        String osName = System.getProperty("os.name");
    //        for (Control c : bgComponents) {
    //            //only paint comboboxes and buttons when the running system is windows
    //            //as on linux the background is also set for the components itself
    //            if (c instanceof Combo || c instanceof Button) {
    //                if (osName.contains("windows")) {
    //                    c.setBackground(bg);
    //                }
    //            } else {
    //                c.setBackground(bg);
    //            }
    //        }
    //    }
    //    
    //    /**
    //     * Determines the background color of the view. Will always be asked when new a new input will be displayed at the current view.
    //     * @return Background color for the view
    //     */
    //    protected abstract Color determineBackgroundColor();
    
    /**
     * saves every changes made in the current Detail View
     * @param part will be forwarded from the {@link DetailView}
     * @see org.eclipse.ui.IPartListener2#partClosed(org.eclipse.ui.IWorkbenchPartReference)
     */
    protected void partClosedOrDeactivated(IWorkbenchPart part) {
        if (part instanceof DetailView) {
            saveChanges();
            // save the change persistently
            LOG.debug("Save event in detail view of type {}", this.getClass());
        }
    }
    
    //    /*
    //     * (non-Javadoc)
    //     * @see org.eclipse.swt.widgets.Composite#setFocus()
    //     */
    //    @Override
    //    public abstract boolean setFocus();
    
    /**
     * not in use
     * @see org.eclipse.swt.events.FocusListener#focusGained(org.eclipse.swt.events.FocusEvent)
     */
    @Override
    public void focusGained(FocusEvent e) {
        LOG.debug("Focus gained: {}", e.getSource().getClass().toString());
    }
    
    /**
     * save current changes in objects
     * @see org.eclipse.swt.events.FocusListener#focusLost(org.eclipse.swt.events.FocusEvent)
     */
    @Override
    public void focusLost(FocusEvent e) {
        LOG.debug("Focus lost: {}", e.getSource().getClass().toString());
        saveChanges();
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchListener#preShutdown(org.eclipse.ui.IWorkbench, boolean)
     * @author Thilo Rauch (20.07.2014)
     */
    @Override
    public boolean preShutdown(IWorkbench workbench, boolean forced) {
        // Save changes on shutdown, as no focus-lost event is caused then
        saveChanges();
        LOG.debug("Workbench shutdown detected (pre)");
        return true;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchListener#postShutdown(org.eclipse.ui.IWorkbench)
     * @author Thilo Rauch (20.07.2014)
     */
    @Override
    public void postShutdown(IWorkbench workbench) {
        // do nothing
    }
}