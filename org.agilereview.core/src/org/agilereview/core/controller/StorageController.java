/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.agilereview.core.exception.ExceptionHandler;
import org.agilereview.core.exception.NoStorageClientDefinedException;
import org.agilereview.core.external.definition.IReviewDataReceiver;
import org.agilereview.core.external.definition.IStorageClient;
import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Reply;
import org.agilereview.core.external.storage.Review;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.PlatformUI;

/**
 * The {@link StorageController} manages the {@link IStorageClient}s for the org.agilereview.core.StorageClient ExtensionPoint and provides
 * functionalities of the {@link IStorageClient} interface for the currently active {@link IStorageClient}.
 * @author Malte Brunnlieb (22.03.2012)
 */
public class StorageController implements IExtensionController, IStorageClient {
    
    /**
     * ExtensionPoint id for extensions implementing {@link IStorageClient}
     */
    public static final String ISTORAGECLIENT_ID = "org.agilereview.core.StorageClient";
    
    /**
     * Singleton instance of {@link StorageController}
     */
    private static final StorageController instance = new StorageController();
    /**
     * Mapping of names to objects of registered {@link IStorageClient}s
     */
    private final HashMap<String, IStorageClient> registeredClients = new HashMap<String, IStorageClient>();
    /**
     * Currently active {@link IStorageClient} on which all operations will be called
     */
    private String activeClient = null;
    
    /**
     * Creates a new instance of the {@link StorageController}
     * @author Malte Brunnlieb (22.03.2012)
     */
    private StorageController() {
        checkForNewClients();
    }
    
    /**
     * Returns the unique instance of the {@link StorageController}
     * @return the unique instance of the {@link StorageController}
     * @author Malte Brunnlieb (22.03.2012)
     */
    public static StorageController getInstance() {
        return instance;
    }
    
    /**
     * Starts searching for new {@link IReviewDataReceiver}s registered at the ExtensionPoint asynchronously
     * @author Malte Brunnlieb (02.06.2012)
     */
    public void checkForNewClients() {
        new Thread(this).start();
    }
    
    /**
     * Performs a check for new StorageClients registered at the ExtensionPoint
     * @see java.lang.Runnable#run()
     * @author Malte Brunnlieb (30.05.2012)
     */
    @Override
    public void run() {
        IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(ISTORAGECLIENT_ID);
        if (config.length == 0) {
            ExceptionHandler.logAndNotifyUser(new NoStorageClientDefinedException("No StorageClient available")); //TODO perhaps offer some help
            registeredClients.clear();
            return;
        }
        
        String firstClient = null;
        for (IConfigurationElement e : config) {
            registeredClients.put(e.getAttribute("name"), null);
            if (firstClient != null) {
                firstClient = e.getAttribute("name");
            }
        }
        
        if (PlatformUI.getPreferenceStore().getBoolean("org.agilereview.testrunner.mock.storage")) { //TODO if client can be set via preferences -> use this
            setStorageClient("StorageMock");
        } else if (firstClient != null) {
            setStorageClient(firstClient); //TODO do not use last, but manage to set this client via preferences
        }
    }
    
    /**
     * Returns the currently available storage clients
     * @return a list of the names of all available storage clients
     * @author Malte Brunnlieb (27.05.2012)
     */
    public Set<String> getAvailableStorageClients() {
        return new HashSet<String>(registeredClients.keySet());
    }
    
    /**
     * Sets the storage client which should store and read review data. If there is no available storage client for the given name, nothing changes.
     * After setting a new storage client all {@link IReviewDataReceiver} will be notified.
     * @param name the name under which the storage client is registered
     * @return true, if the storage client could be set<br>false, otherwise
     * @author Malte Brunnlieb (27.05.2012)
     */
    public boolean setStorageClient(String name) {
        System.out.println("setStorageClient " + name);
        if (registeredClients.containsKey(name)) {
            if (registeredClients.get(name) == null) {
                registeredClients.put(name, loadExtension(name));
            }
            if (registeredClients.get(name) != null) {
                activeClient = name;
                RDRController.getInstance().notifyAllClients(getAllReviews());
                return true;
            }
        }
        return false;
    }
    
    /**
     * Loads the extension class identified by its extension name
     * @param name the name of the extension which should be loaded
     * @return the {@link IStorageClient} instance loaded from the extension or null if the extension could not be instantiated
     * @author Malte Brunnlieb (31.05.2012)
     */
    private IStorageClient loadExtension(String name) {
        
        IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(ISTORAGECLIENT_ID);
        for (IConfigurationElement e : config) {
            if (e.getAttribute("name").equals(name)) {
                try {
                    Object o = e.createExecutableExtension("class");
                    if (o instanceof IStorageClient) { return (IStorageClient) o; }
                } catch (CoreException ex) {
                    ExceptionHandler.logAndNotifyUser("An error occurred while instantiating StorageClient " + name + " defined by class "
                            + e.getAttribute("class"), ex);
                    break;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Checks whether the given {@link IStorageClient} is already registered
     * @param sc {@link IStorageClient} to be checked for registration
     * @return true, if the given {@link IStorageClient} is already registered<br>false, otherwise
     * @author Malte Brunnlieb (28.03.2012)
     */
    public boolean isRegistered(IStorageClient sc) {
        return registeredClients.containsValue(sc);
    }
    
    /**
     * This method will be forwarded to {@link IStorageClient#getAllReviews()} of the currently active {@link IStorageClient}
     * @see org.agilereview.core.external.definition.IStorageClient#getAllReviews()
     * @author Malte Brunnlieb (24.03.2012)
     */
    @Override
    public List<Review> getAllReviews() {
        try {
            List<Review> result = registeredClients.get(activeClient).getAllReviews();
            Assert.isNotNull(result);
            return result;
        } catch (Throwable ex) {
            ExceptionHandler.logAndNotifyUser("An unknown exception occured in StorageClient '" + activeClient + "' while retrieving all comments",
                    ex);
        }
        return null;
    }
    
    /**
     * This method will be forwarded to {@link IStorageClient#addReview(Review)} of the currently active {@link IStorageClient}
     * @see org.agilereview.core.external.definition.IStorageClient#addReview(Review)
     * @author Malte Brunnlieb (28.04.2012)
     */
    @Override
    public void addReview(Review review) {
        try {
            registeredClients.get(activeClient).addReview(review);
        } catch (Throwable ex) {
            ExceptionHandler.logAndNotifyUser("An unknown exception occured in StorageClient '" + activeClient + "' while adding review '"
                    + review.getId() + "'", ex);
        }
    }
    
    /**
     * This method will be forwarded to {@link IStorageClient#getNewId(Review)} of the currently active {@link IStorageClient}
     * @see org.agilereview.core.external.definition.IStorageClient#getNewId(Review)
     * @author Malte Brunnlieb (28.04.2012)
     */
    @Override
    public String getNewId(Review review) {
        try {
            String result = registeredClients.get(activeClient).getNewId(review);
            Assert.isNotNull(result);
            return result;
        } catch (Throwable ex) {
            ExceptionHandler.logAndNotifyUser("An unknown exception occured in StorageClient '" + activeClient
                    + "' while retrieving an ID for new review", ex);
        }
        return null;
    }
    
    /**
     * This method will be forwarded to {@link IStorageClient#getNewId(Comment)} of the currently active {@link IStorageClient}
     * @see org.agilereview.core.external.definition.IStorageClient#getNewId(Comment)
     * @author Malte Brunnlieb (28.04.2012)
     */
    @Override
    public String getNewId(Comment comment) {
        try {
            String result = registeredClients.get(activeClient).getNewId(comment);
            Assert.isNotNull(result);
            return result;
        } catch (Throwable ex) {
            ExceptionHandler.logAndNotifyUser("An unknown exception occured in StorageClient '" + activeClient
                    + "' while retrieving an ID for new comment", ex);
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.core.external.definition.IStorageClient#getNewId(org.agilereview.core.external.storage.Reply)
     * @author Malte Brunnlieb (28.04.2012)
     */
    @Override
    public String getNewId(Reply reply) {
        try {
            String result = registeredClients.get(activeClient).getNewId(reply);
            Assert.isNotNull(result);
            return result;
        } catch (Throwable ex) {
            ExceptionHandler.logAndNotifyUser("An unknown exception occured in StorageClient '" + activeClient
                    + "' while retrieving an ID for new reply", ex);
        }
        return null;
    }
}
