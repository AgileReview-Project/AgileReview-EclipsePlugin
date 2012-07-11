/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.controller;

import java.util.HashMap;

import org.agilereview.core.external.definition.IReviewDataReceiver;
import org.agilereview.core.external.definition.IStorageClient;

/**
 * 
 * @author Malte Brunnlieb (11.07.2012)
 */
public class ExtensionControllerFactory {
    
    /**
     * Mapping of {@link ExtensionPoint} to the unique {@link IExtensionController} instance
     */
    private static HashMap<ExtensionPoint, IExtensionController> extensionControllerMap = new HashMap<ExtensionPoint, IExtensionController>();
    
    /**
     * Extension point
     * @author Malte Brunnlieb (11.07.2012)
     */
    public enum ExtensionPoint {
        /**
         * {@link IReviewDataReceiver} extension point
         */
        ReviewDataReceiver,
        /**
         * {@link IStorageClient} extension point
         */
        StorageClient
    }
    
    /**
     * Creates or returns the unique instance of the {@link IExtensionController} for the given {@link ExtensionPoint}
     * @param ex {@link ExtensionPoint} the controller should be created for
     * @return unique instance of the related {@link IExtensionController}
     * @author Malte Brunnlieb (11.07.2012)
     */
    public static IExtensionController createExtensionController(ExtensionPoint ex) {
        switch (ex) {
        case StorageClient:
            return createAndReturn(ExtensionPoint.StorageClient, StorageController.class);
        case ReviewDataReceiver:
            return createAndReturn(ExtensionPoint.ReviewDataReceiver, RDRController.class);
        default:
            return null;
        }
    }
    
    /**
     * Creates a new {@link IExtensionController} for the given {@link ExtensionPoint} if it has not been created yet. If the
     * {@link IExtensionController} was created beforehand, the unique instance will be returned.
     * @param ex {@link IExtensionController} which should be created
     * @param clazz Class which should be instantiated if necessary
     * @return the instance of the related {@link IExtensionController}
     * @author Malte Brunnlieb (11.07.2012)
     */
    private static IExtensionController createAndReturn(ExtensionPoint ex, Class<?> clazz) {
        Object o;
        try {
            o = clazz.newInstance();
            IExtensionController ec = (IExtensionController) o;
            if (extensionControllerMap.get(ex) == null) {
                extensionControllerMap.put(ex, ec);
            }
            return ec;
        } catch (InstantiationException e) {
            // should not occur by construction
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // should not occur by construction
            e.printStackTrace();
        }
        return null; //should not occur by construction
    }
}
