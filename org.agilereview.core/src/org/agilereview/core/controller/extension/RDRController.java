/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Apache License v2.0 which accompanies this distribution, and is available
 * at http://www.apache.org/licenses/LICENSE-2.0.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.controller.extension;

import java.util.Set;

import org.agilereview.core.exception.ExceptionHandler;
import org.agilereview.core.external.definition.IReviewDataReceiver;
import org.agilereview.core.external.storage.Review;
import org.agilereview.core.external.storage.ReviewSet;
import org.eclipse.core.runtime.CoreException;

/**
 * The {@link RDRController} manages the {@link IReviewDataReceiver}s for the org.agilereview.core.ReviewDataReceiver ExtensionPoint and provides
 * functionality for notifying all registered {@link IReviewDataReceiver} about changed backend data.
 * @author Malte Brunnlieb (28.03.2012)
 */
public class RDRController extends AbstractController<IReviewDataReceiver> {
    
    /**
     * ExtensionPoint id for extensions implementing {@link IReviewDataReceiver}
     */
    public static final String IREVIEWDATARECEIVER_ID = "org.agilereview.core.ReviewDataReceiver";
    
    /**
     * Creates a new instance of {@link RDRController}
     * @author Malte Brunnlieb (28.03.2012)
     */
    RDRController() {
        super(IREVIEWDATARECEIVER_ID);
    }
    
    /**
     * Notifies the registered {@link IReviewDataReceiver} with the new data provided by the backend
     * @param rdr {@link IReviewDataReceiver} which should be notified
     * @param newData list of {@link Review}s
     * @author Malte Brunnlieb (28.03.2012)
     */
    void notifyClient(IReviewDataReceiver rdr, ReviewSet newData) {
        System.out.println("Set review data " + rdr.getClass().getName() + " - ReviewSize = " + newData.size());
        rdr.setReviewData(newData);
    }
    
    /**
     * Notifies all registered {@link IReviewDataReceiver}s with the new data provided by the backend
     * @param newData list of {@link Review}s
     * @author Malte Brunnlieb (28.03.2012)
     */
    void notifyAllClients(ReviewSet newData) {
        Set<String> clients = getAvailableExtensions();
        System.out.println("Clients Length = " + clients.size());
        for (String client : clients) {
            try {
                notifyClient(getExtension(client), newData);
            } catch (CoreException e) {
                ExceptionHandler.logAndNotifyUser("The ReviewDataReceiver '" + client
                        + "' could not be instantiated! This can lead to inconsistent data views.", e);
                e.printStackTrace();
            }
        }
    }
    
}
