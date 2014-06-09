/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.common.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.agilereview.common.Activator;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * Handles all exceptions of the core PlugIn. It informs the user about the problem occurred and logs it.
 * @author Malte Brunnlieb (24.03.2012)
 */
public class ExceptionHandler {
    
    /**
     * This method opens a message dialog in order to inform the user about the error occurred. Therefore the exception name and message will be
     * shown. The exception will be also logged to the PlugIn logger
     * @param ex Exception occurred
     * @param pluginID plugin id of the reporting plugin
     * @author Malte Brunnlieb (24.03.2012)
     */
    public static void logAndNotifyUser(final Throwable ex, String pluginID) {
        Activator.getDefault().getLog().log(new Status(Status.ERROR, pluginID, ex.getMessage(), ex));
        if (!PlatformUI.getPreferenceStore().getBoolean("org.agilereview.testrunner.active")) {
            Display.getDefault().asyncExec(new Runnable() {
                
                @Override
                public void run() {
                    MessageDialog.openError(Display.getCurrent().getActiveShell(), "An error occured", ex.toString() + ": " + ex.getMessage());
                }
            });
        }
    }
    
    /**
     * This method opens a message dialog in order to inform the user about the error occurred. Therefore the exception name and the provided message
     * will be shown. The exception will be also logged to the PlugIn logger
     * @param ex Exception occurred
     * @param msg Message to be shown to the user
     * @param pluginID plugin id of the reporting plugin
     * @author Malte Brunnlieb (24.03.2012)
     */
    public static void logAndNotifyUser(final String msg, final Throwable ex, String pluginID) {
        Activator.getDefault().getLog().log(new Status(Status.ERROR, pluginID, ex.toString() + ": " + msg, ex));
        if (!PlatformUI.getPreferenceStore().getBoolean("org.agilereview.testrunner.active")) {
            Display.getDefault().asyncExec(new Runnable() {
                
                @Override
                public void run() {
                    MessageDialog.openError(Display.getCurrent().getActiveShell(), "An error occured", ex.toString() + ": " + msg);
                }
            });
        }
    }
    
    /**
     * This method displays a message dialog with the given message as warning.
     * @param msg Message which will be shown to the user
     * @author Malte Brunnlieb (20.11.2012)
     */
    public static void warnUser(final String msg) {
        if (!PlatformUI.getPreferenceStore().getBoolean("org.agilereview.testrunner.active")) {
            Display.getDefault().asyncExec(new Runnable() {
                
                @Override
                public void run() {
                    MessageDialog.openWarning(Display.getCurrent().getActiveShell(), "Warning!", msg);
                }
            });
        }
    }
    
    /**
     * @return the string representation of the stacktrace of an exception.
     * @author Peter Reuter (09.06.2014)
     */
    public static String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
