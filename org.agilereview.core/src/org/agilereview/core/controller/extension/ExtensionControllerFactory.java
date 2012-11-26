/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.controller.extension;

import java.util.HashMap;

import org.agilereview.core.controller.IExtensionController;
import org.agilereview.core.external.definition.IEditorParser;
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
     * Available extension points
     * @author Malte Brunnlieb (11.07.2012)
     */
    public enum ExtensionPoint {
        /**
         * extension point for {@link IReviewDataReceiver}
         */
        ReviewDataReceiver,
        /**
         * extension point for {@link IStorageClient}
         */
        StorageClient,
        /**
         * extension point for {@link IEditorParser}
         */
        EditorParser
    }
    
    /**
     * Creates or returns the unique instance of the {@link IExtensionController} for the given {@link ExtensionPoint}
     * @param ex {@link ExtensionPoint} the controller should be created for
     * @return unique instance of the related {@link IExtensionController}
     * @author Malte Brunnlieb (11.07.2012)
     */
    public static IExtensionController getExtensionController(ExtensionPoint ex) {
        switch (ex) {
        case StorageClient:
            return getController(ExtensionPoint.StorageClient, StorageController.class);
        case ReviewDataReceiver:
            return getController(ExtensionPoint.ReviewDataReceiver, RDRController.class);
        case EditorParser:
            return getController(ExtensionPoint.EditorParser, EditorParserController.class);
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
    private static IExtensionController getController(ExtensionPoint ex, Class<?> clazz) {
        try {
            if (!extensionControllerMap.containsKey(ex)) {
                extensionControllerMap.put(ex, (IExtensionController) clazz.newInstance());
            }
            return extensionControllerMap.get(ex);
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
