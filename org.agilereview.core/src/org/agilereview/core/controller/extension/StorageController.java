/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.controller.extension;

import java.util.Set;

import org.agilereview.core.controller.extension.ExtensionControllerFactory.ExtensionPoint;
import org.agilereview.core.exception.ExceptionHandler;
import org.agilereview.core.exception.ExtensionCreationException;
import org.agilereview.core.exception.NoStorageClientException;
import org.agilereview.core.external.definition.IReviewDataReceiver;
import org.agilereview.core.external.definition.IStorageClient;
import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Reply;
import org.agilereview.core.external.storage.Review;
import org.agilereview.core.external.storage.ReviewSet;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.PlatformUI;

/**
 * The {@link StorageController} manages the {@link IStorageClient}s for the org.agilereview.core.StorageClient ExtensionPoint and provides
 * functionalities of the {@link IStorageClient} interface for the currently active {@link IStorageClient}.
 * @author Malte Brunnlieb (22.03.2012)
 */
public class StorageController extends AbstractController<IStorageClient> implements IStorageClient {
    
    /**
     * ExtensionPoint id for extensions implementing {@link IStorageClient}
     */
    public static final String ISTORAGECLIENT_ID = "org.agilereview.core.StorageClient";
    
    /**
     * Currently active {@link IStorageClient} on which all operations will be called
     */
    private IStorageClient activeClient = null;
    
    /**
     * Creates a new instance of the {@link StorageController}
     * @author Malte Brunnlieb (22.03.2012)
     */
    StorageController() {
        super(ISTORAGECLIENT_ID);
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.core.controller.AbstractController#handleNoExtensionAvailable()
     * @author Malte Brunnlieb (12.07.2012)
     */
    @Override
    protected void handleNoExtensionAvailable() {
        ExceptionHandler.logAndNotifyUser(new NoStorageClientException("No StorageClient available"));
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.core.controller.AbstractController#doAfterCheckForClients()
     * @author Malte Brunnlieb (12.07.2012)
     */
    @Override
    protected void doAfterCheckForClients() {
        try {
            if (PlatformUI.getPreferenceStore().getBoolean("org.agilereview.testrunner.mock.storage")) { //TODO if client can be set via preferences -> use this
                setStorageClient("StorageMock");
            } else if (getFirstExtension() != null) {
                setStorageClient(getFirstExtension()); //TODO do not use last, but manage to set this client via preferences
            }
        } catch (ExtensionCreationException e) {
            ExceptionHandler.logAndNotifyUser(e.getLocalizedMessage(), e);
            e.printStackTrace();
        }
    }
    
    /**
     * Returns the first registered extension
     * @return the name for the first registered extension
     * @author Malte Brunnlieb (12.07.2012)
     */
    private String getFirstExtension() {
        Set<String> extensions = getAvailableExtensions();
        if (extensions.size() > 0) {
            String first = extensions.toArray(new String[0])[0];
            return first;
        }
        return null;
    }
    
    /**
     * Sets the storage client which should store and read review data. If there is no available storage client for the given name, nothing changes.
     * After setting a new storage client all {@link IReviewDataReceiver} will be notified.
     * @param name the name under which the storage client is registered
     * @throws ExtensionCreationException will be thrown when there is no extension registered with the current name or an error occurred while
     *             creating the intended extension
     * @author Malte Brunnlieb (27.05.2012)
     */
    public void setStorageClient(String name) throws ExtensionCreationException {
        if (activeClient != null && activeClient.equals(name)) return;
        
        Set<String> extensions = getAvailableExtensions();
        if (!extensions.contains(name)) {
            throw new ExtensionCreationException("The StorageClient with id " + name + " could not be found!");
        }
        
        try {
            activeClient = getUniqueExtension(name);
        } catch (CoreException e) {
            e.printStackTrace();
            throw new ExtensionCreationException("The StorageClient with id " + name + " could not be intantiated!");
        }
        
        RDRController c = (RDRController) ExtensionControllerFactory.getExtensionController(ExtensionPoint.ReviewDataReceiver);
        c.notifyAllClients(getAllReviews());
    }
    
    /**
     * This method will be forwarded to {@link IStorageClient#getAllReviews()} of the currently active {@link IStorageClient}
     * @see org.agilereview.core.external.definition.IStorageClient#getAllReviews()
     * @author Malte Brunnlieb (24.03.2012)
     */
    @Override
    public ReviewSet getAllReviews() {
        try {
            ReviewSet result = activeClient.getAllReviews();
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
            activeClient.addReview(review);
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
            String result = activeClient.getNewId(review);
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
            String result = activeClient.getNewId(comment);
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
            String result = activeClient.getNewId(reply);
            Assert.isNotNull(result);
            return result;
        } catch (Throwable ex) {
            ExceptionHandler.logAndNotifyUser("An unknown exception occured in StorageClient '" + activeClient
                    + "' while retrieving an ID for new reply", ex);
        }
        return null;
    }
    
}
