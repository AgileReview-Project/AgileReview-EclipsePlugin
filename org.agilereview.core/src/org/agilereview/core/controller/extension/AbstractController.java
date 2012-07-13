/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.controller.extension;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.agilereview.core.controller.IExtensionController;
import org.agilereview.core.external.definition.IStorageClient;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

/**
 * Abstract class for managing an extension point on extensions implementing or extending the parameter type
 * @param <T> super class of the extensions for this extension point
 * @author Malte Brunnlieb (11.07.2012)
 */
public abstract class AbstractController<T> implements IExtensionController {
    
    /**
     * Extension point id
     */
    private final String extensionID;
    /**
     * Mapping of names to objects of registered Extensions. Access to this map should be synchronized upon this object.
     */
    private final HashMap<String, T> registeredClients = new HashMap<String, T>();
    
    /**
     * Checks whether the controller was fully initialized (first checkForClients included)
     */
    private boolean initialized = false;
    
    /**
     * Creates a new instance of an {@link AbstractController}
     * @param extensionID extension point id
     * @author Malte Brunnlieb (22.03.2012)
     */
    AbstractController(String extensionID) {
        this.extensionID = extensionID;
        checkForNewClients();
    }
    
    /**
     * Starts searching for new extensions registered at the ExtensionPoint asynchronously
     * @author Malte Brunnlieb (02.06.2012)
     */
    public void checkForNewClients() {
        new Thread(this).start();
    }
    
    /**
     * Performs a check for new Extensions registered at the ExtensionPoint
     * @see java.lang.Runnable#run()
     * @author Malte Brunnlieb (30.05.2012)
     */
    @Override
    public void run() {
        synchronized (registeredClients) {
            IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(extensionID);
            if (config.length == 0) {
                registeredClients.clear();
                handleNoExtensionAvailable();
                return;
            }
            
            for (IConfigurationElement e : config) {
                registeredClients.put(e.getAttribute("name"), null);
            }
            
            initialized = true;
            
            doAfterCheckForClients();
        }
    }
    
    /**
     * Loads the extension class identified by its extension name
     * @param name the name of the extension which should be loaded
     * @return the {@link IStorageClient} instance loaded from the extension or null if the extension could not be instantiated
     * @author Malte Brunnlieb (31.05.2012)
     * @throws CoreException
     */
    @SuppressWarnings("unchecked")
    private T loadExtension(String name) throws CoreException {
        IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(extensionID);
        for (IConfigurationElement e : config) {
            if (e.getAttribute("name").equals(name)) {
                Object o = e.createExecutableExtension("class");
                return (T) o;
            }
        }
        
        return null;
    }
    
    /**
     * Returns the extension for the given name. If there is currently no instance of this extension, the extension is loaded.
     * @param name unique name for the extension to retrieve
     * @return the extension for the given name
     * @throws CoreException
     * @author Malte Brunnlieb (12.07.2012)
     */
    protected T getExtension(String name) throws CoreException {
        if (registeredClients.get(name) == null) {
            registeredClients.put(name, loadExtension(name));
        }
        return registeredClients.get(name);
    }
    
    /**
     * Returns the currently available storage clients
     * @return a list of the names of all available storage clients
     * @author Malte Brunnlieb (27.05.2012)
     */
    protected Set<String> getAvailableExtensions() {
        while (!initialized) {
        }
        synchronized (registeredClients) {
            return new HashSet<String>(registeredClients.keySet());
        }
    }
    
    /**
     * Will be called if no extension is available. <br> This class is intended to be overwritten. The default implementation does nothing.
     * @author Malte Brunnlieb (12.07.2012)
     */
    protected void handleNoExtensionAvailable() {
    }
    
    /**
     * Will be called after checking for new extensions. The function call is synchronized over all checkForNewClients calls. <br> This class is
     * intended to be overwritten. The default implementation does nothing.
     * @author Malte Brunnlieb (12.07.2012)
     */
    protected void doAfterCheckForClients() {
    }
}
