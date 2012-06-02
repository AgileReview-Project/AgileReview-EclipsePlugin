/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.controller;

/**
 * Interface to provide a common access point for all extension point controller
 * @author Malte Brunnlieb (02.06.2012)
 */
public interface IExtensionController extends Runnable {
    
    /**
     * Starts searching for new clients registered at the ExtensionPoint asynchronously
     * @author Malte Brunnlieb (02.06.2012)
     */
    void checkForNewClients();
}
