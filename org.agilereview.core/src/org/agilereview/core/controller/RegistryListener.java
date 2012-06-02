/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Apache License v2.0 which accompanies this distribution, and is available
 * at http://www.apache.org/licenses/LICENSE-2.0.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.controller;

import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IRegistryEventListener;
import org.eclipse.core.runtime.Platform;

/**
 * 
 * @author Malte Brunnlieb (28.03.2012)
 */
public class RegistryListener implements IRegistryEventListener {
    
    /**
     * The extension point controller which should be notified about added or removed extensions
     */
    private final IExtensionController controller;
    
    /**
     * Creates a new instance of the {@link RegistryListener} listening on the given extensionId
     * @param extensionId the Id of the extensionPoint this instance listens to
     * @param controller {@link IExtensionController} which shoule be notified about added or removed extension
     * @author Malte Brunnlieb (02.06.2012)
     */
    public RegistryListener(String extensionId, IExtensionController controller) {
        this.controller = controller;
        Platform.getExtensionRegistry().addListener(this, extensionId);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IRegistryEventListener#added(org.eclipse.core.runtime.IExtension[])
     * @author Malte Brunnlieb (28.03.2012)
     */
    @Override
    public void added(IExtension[] extensions) {
        controller.checkForNewClients();
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IRegistryEventListener#removed(org.eclipse.core.runtime.IExtension[])
     * @author Malte Brunnlieb (28.03.2012)
     */
    @Override
    public void removed(IExtension[] extensions) {
        controller.checkForNewClients();
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IRegistryEventListener#added(org.eclipse.core.runtime.IExtensionPoint[])
     * @author Malte Brunnlieb (28.03.2012)
     */
    @Override
    public void added(IExtensionPoint[] extensionPoints) {
        // nothing need to be done here
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IRegistryEventListener#removed(org.eclipse.core.runtime.IExtensionPoint[])
     * @author Malte Brunnlieb (28.03.2012)
     */
    @Override
    public void removed(IExtensionPoint[] extensionPoints) {
        // nothing need to be done here
    }
}
