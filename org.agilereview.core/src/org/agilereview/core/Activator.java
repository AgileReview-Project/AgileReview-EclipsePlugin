/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core;

import org.agilereview.core.controller.RDRController;
import org.agilereview.core.controller.RegistryListener;
import org.agilereview.core.controller.StorageController;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {
    
    /**
     * The plug-in ID
     */
    public static final String PLUGIN_ID = "org.agilereview.core"; //$NON-NLS-1$
    
    /**
     * The shared instance
     */
    private static Activator plugin;
    
    /**
     * The constructor
     */
    public Activator() {
    }
    
    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        registerListeners();
    }
    
    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }
    
    /**
     * Returns the shared instance
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }
    
    /**
     * Registers all necessary Listeners to the ExtensionRegistry
     * @author Malte Brunnlieb (24.05.2012)
     */
    private void registerListeners() {
        RegistryListener listener = new RegistryListener();
        Platform.getExtensionRegistry().addListener(listener, StorageController.ISTORAGECLIENT_ID);
        Platform.getExtensionRegistry().addListener(listener, RDRController.IREVIEWDATARECEIVER_ID);
    }
}
