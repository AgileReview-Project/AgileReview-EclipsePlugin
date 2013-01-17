/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.controller;

import org.agilereview.common.ui.PlatformUITools;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;

/**
 * Controller for eclipse contexts
 * @author Malte Brunnlieb (17.01.2013)
 */
public class ContextController implements IPerspectiveListener {
    
    /**
     * The context service of the current workbench
     */
    private volatile IContextService contextService;
    /**
     * contextActivation for later deactivating
     */
    private volatile IContextActivation contextActivation;
    /**
     * The AgileReview perspective ID
     */
    private final String perspectiveId = "org.agilereview.perspective";
    /**
     * Context id handling the state of the AgileReview perspective
     */
    private final String perspectiveContextId = "org.agilereview.core.contexts.perspectiveopen";
    
    /**
     * Creates a new instance of the {@link ContextController}. The {@link ContextController} registers itself for necessary topics in the workbench.
     * @author Malte Brunnlieb (17.01.2013)
     */
    public ContextController() {
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        contextService = (IContextService) PlatformUITools.getWorkbench().getService(IContextService.class);
                        IPerspectiveDescriptor perspective = PlatformUITools.getActiveWorkbenchPage().getPerspective();
                        if (perspective != null && perspective.getId().equals(perspectiveId)) {
                            activateContext();
                        }
                        PlatformUITools.getActiveWorkbenchWindow().addPerspectiveListener(ContextController.this);
                    }
                });
            }
        }).start();
    }
    
    /**
     * Activates the perspective open context. If there already is a known context registered, it will be deactivated beforehand.
     * @author Malte Brunnlieb (17.01.2013)
     */
    private void activateContext() {
        if (contextActivation == null) {
            contextActivation = contextService.activateContext(perspectiveContextId);
            System.out.println("Context activated");
        } else {
            deactivateContext();
            activateContext();
        }
    }
    
    /**
     * Deactivates the perspective open context.
     * @author Malte Brunnlieb (17.01.2013)
     */
    private void deactivateContext() {
        if (contextActivation != null) {
            contextService.deactivateContext(contextActivation);
            contextActivation = null;
            System.err.println("Context deactivated");
        }
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.IPerspectiveListener#perspectiveActivated(org.eclipse.ui.IWorkbenchPage, org.eclipse.ui.IPerspectiveDescriptor)
     * @author Malte Brunnlieb (17.01.2013)
     */
    @Override
    public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
        if (perspective.getId().equals(perspectiveId)) {
            activateContext();
        } else {
            deactivateContext();
        }
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.IPerspectiveListener#perspectiveChanged(org.eclipse.ui.IWorkbenchPage, org.eclipse.ui.IPerspectiveDescriptor, java.lang.String)
     * @author Malte Brunnlieb (17.01.2013)
     */
    @Override
    public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) {
    }
    
}
