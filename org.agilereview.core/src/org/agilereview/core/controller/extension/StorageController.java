/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.controller.extension;

import java.util.Set;

import org.agilereview.common.exception.ExceptionHandler;
import org.agilereview.core.Activator;
import org.agilereview.core.controller.extension.ExtensionControllerFactory.ExtensionPoint;
import org.agilereview.core.exception.ExtensionCreationException;
import org.agilereview.core.exception.NoStorageClientException;
import org.agilereview.core.external.definition.IReviewDataReceiver;
import org.agilereview.core.external.definition.IStorageClient;
import org.agilereview.core.external.storage.Review;
import org.agilereview.core.external.storage.ReviewSet;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link StorageController} manages the {@link IStorageClient}s for the org.agilereview.core.StorageClient ExtensionPoint and provides
 * functionalities of the {@link IStorageClient} interface for the currently active {@link IStorageClient}.
 * @author Malte Brunnlieb (22.03.2012)
 */
public class StorageController extends AbstractController<IStorageClient> implements IStorageClient {
    
    /**
     * Logger instance
     */
    private static final Logger LOG = LoggerFactory.getLogger(StorageController.class);
    
    /**
     * ExtensionPoint id for extensions implementing {@link IStorageClient}
     */
    public static final String ISTORAGECLIENT_ID = "org.agilereview.core.StorageClient";
    
    /**
     * Currently active {@link IStorageClient} on which all operations will be called
     */
    private IStorageClient activeClient = null;
    /**
     * The id of the active {@link IStorageClient}
     */
    private String activeClientId = "";
    /**
     * Cached {@link ReviewSet}
     */
    private ReviewSet cachedReviewSet = null;
    /**
     * Boolean flag which indicates a changed {@link IStorageClient} in order to refresh the cached {@link ReviewSet}
     */
    private boolean storageClientChanged = true;
    
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
        ExceptionHandler.logAndNotifyUser(new NoStorageClientException("No StorageClient available"), Activator.PLUGIN_ID);
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
                LOG.debug("Set storage client 'StorageMock'");
            } else if (getFirstExtension() != null) {
                setStorageClient(getFirstExtension()); //TODO do not use first, but manage to set this client via preferences
                LOG.debug("Set storage client '{}'", getFirstExtension());
            }
        } catch (ExtensionCreationException e) {
            LOG.error("Fail to create an instance of the StorageClient", e);
            ExceptionHandler.logAndNotifyUser(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
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
        if (this.activeClient != null && this.activeClientId.equals(name)) { return; }
        
        Set<String> extensions = getAvailableExtensions();
        if (!extensions.contains(name)) { throw new ExtensionCreationException("The StorageClient with id " + name + " could not be found!"); }
        
        try {
            this.activeClient = getUniqueExtension(name);
            this.activeClientId = name;
            this.storageClientChanged = true;
        } catch (CoreException e) {
            LOG.error("The StorageClient with id '{}' could not be intantiated!", name, e);
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
            if (this.storageClientChanged) {
                this.cachedReviewSet = this.activeClient.getAllReviews();
                this.storageClientChanged = false;
            }
            ReviewSet result = this.cachedReviewSet;
            Assert.isNotNull(result);
            return result;
        } catch (Throwable ex) {
            LOG.error("An unknown exception occured in StorageClient '{}' while retrieving all comments", activeClient, ex);
            ExceptionHandler.logAndNotifyUser("An unknown exception occured in StorageClient '" + this.activeClient
                    + "' while retrieving all comments", ex, Activator.PLUGIN_ID);
        }
        return null;
    }
    
    /**
     * This method will be forwarded to {@link IStorageClient#getNewReviewId()} of the currently active {@link IStorageClient}
     * @see org.agilereview.core.external.definition.IStorageClient#getNewReviewId()
     * @author Malte Brunnlieb (28.04.2012)
     */
    @Override
    public String getNewReviewId() {
        try {
            // XXX peter: add null check, I encountered a nullpointer while adding a review without SourceFolder
            String result = this.activeClient.getNewReviewId();
            Assert.isNotNull(result);
            return result;
        } catch (Throwable ex) {
            LOG.error("An unknown exception occured in StorageClient '{}' while retrieving an ID for new review", activeClient, ex);
            ExceptionHandler.logAndNotifyUser("An unknown exception occured in StorageClient '" + this.activeClient
                    + "' while retrieving an ID for new review", ex, Activator.PLUGIN_ID);
        }
        return null;
    }
    
    /**
     * This method will be forwarded to {@link IStorageClient#getNewCommentId(String, Review)} of the currently active {@link IStorageClient}
     * @see org.agilereview.core.external.definition.IStorageClient#getNewCommentId(String, Review)
     * @author Malte Brunnlieb (28.04.2012)
     */
    @Override
    public String getNewCommentId(String author, Review review) {
        try {
            String result = this.activeClient.getNewCommentId(author, review);
            Assert.isNotNull(result);
            return result;
        } catch (Throwable ex) {
            LOG.error("An unknown exception occured in StorageClient '{}' while retrieving an ID for new comment", activeClient, ex);
            ExceptionHandler.logAndNotifyUser("An unknown exception occured in StorageClient '" + this.activeClient
                    + "' while retrieving an ID for new comment", ex, Activator.PLUGIN_ID);
        }
        return null;
    }
    
    /**
     * This method will be forwarded to {@link IStorageClient#getNewReplyId(Object)} of the currently active {@link IStorageClient}
     * @see org.agilereview.core.external.definition.IStorageClient#getNewReplyId(Object)
     * @author Malte Brunnlieb (28.04.2012)
     */
    @Override
    public String getNewReplyId(Object parent) {
        try {
            String result = this.activeClient.getNewReplyId(parent);
            Assert.isNotNull(result);
            return result;
        } catch (Throwable ex) {
            LOG.error("An unknown exception occured in StorageClient '{}' while retrieving an ID for new reply", activeClient, ex);
            ExceptionHandler.logAndNotifyUser("An unknown exception occured in StorageClient '" + this.activeClient
                    + "' while retrieving an ID for new reply", ex, Activator.PLUGIN_ID);
        }
        return null;
    }
    
}
