/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.exception;

import org.agilereview.core.Activator;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

/**
 * Handles all exceptions of the core PlugIn. It informs the user about the problem occurred and logs it.
 * @author Malte Brunnlieb (24.03.2012)
 */
public class ExceptionHandler {
    
    /**
     * This method opens a message dialog in order to inform the user about the error occurred. Therefore the exception name and message will be
     * shown. The exception will be also logged to the PlugIn logger
     * @param ex Exception occurred
     * @author Malte Brunnlieb (24.03.2012)
     */
    public static void logAndNotifyUser(final Throwable ex) {
        Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, ex.getMessage(), ex));
        Display.getDefault().asyncExec(new Runnable() {
            
            @Override
            public void run() {
                MessageDialog.openError(Display.getCurrent().getActiveShell(), "An error occured", ex.toString() + ": " + ex.getMessage());
            }
        });
    }
    
    /**
     * This method opens a message dialog in order to inform the user about the error occurred. Therefore the exception name and the provided message
     * will be shown. The exception will be also logged to the PlugIn logger
     * @param ex Exception occurred
     * @param msg Message to be shown to the user
     * @author Malte Brunnlieb (24.03.2012)
     */
    public static void logAndNotifyUser(final String msg, final Throwable ex) {
        Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, ex.toString() + ": " + msg, ex));
        Display.getDefault().asyncExec(new Runnable() {
            
            @Override
            public void run() {
                MessageDialog.openError(Display.getCurrent().getActiveShell(), "An error occured", ex.toString() + ": " + msg);
            }
        });
    }
}
