/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.common.ui;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * This class provides some helper functions in order to minimize code overhead
 * @author Malte Brunnlieb (06.12.2012)
 */
public class PlatformUITools {
    
    /**
     * Waits for the {@link IWorkbench} to appear and returns it
     * @return {@link IWorkbench} of the IDE
     * @author Malte Brunnlieb (06.12.2012)
     */
    public static IWorkbench getWorkbench() {
        IWorkbench workbench;
        while ((workbench = PlatformUI.getWorkbench()) == null) {
        }
        return workbench;
    }
    
    /**
     * Waits for the active {@link IWorkbenchWindow} and returns it
     * @return the active {@link IWorkbenchWindow} of the UI
     * @author Malte Brunnlieb (06.12.2012)
     */
    public static IWorkbenchWindow getActiveWorkbenchWindow() {
        IWorkbench workbench = getWorkbench();
        IWorkbenchWindow workbenchWindow;
        while ((workbenchWindow = workbench.getActiveWorkbenchWindow()) == null) {
        }
        return workbenchWindow;
    }
    
    /**
     * Waits for the active {@link IWorkbenchPage} and returns it
     * @return the active {@link IWorkbenchPage} of the UI
     * @author Malte Brunnlieb (06.12.2012)
     */
    public static IWorkbenchPage getActiveWorkbenchPage() {
        IWorkbenchWindow workbenchWindow = getActiveWorkbenchWindow();
        IWorkbenchPage page;
        while ((page = workbenchWindow.getActivePage()) == null) {
        }
        return page;
    }
    
}
