/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.controller;

import java.util.HashMap;
import java.util.List;

import org.agilereview.core.exception.ExceptionHandler;
import org.agilereview.core.exception.NoStorageClientDefinedException;
import org.agilereview.core.external.definition.IStorageClient;
import org.agilereview.core.external.storage.Review;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

/**
 * The {@link StorageController} manages the {@link IStorageClient}s for the org.agilereview.core.StorageClient ExtensionPoint and provides
 * functionalities of the {@link IStorageClient} interface for the currently active {@link IStorageClient}.
 * @author Malte Brunnlieb (22.03.2012)
 */
public class StorageController implements IStorageClient {
	
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
	private final String activeClient = null;
	
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
	 * Performs a check for new StorageClients registered at the ExtensionPoint
	 * @author Malte Brunnlieb (24.03.2012)
	 */
	public void checkForNewClients() {
		IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(ISTORAGECLIENT_ID);
		if (config.length == 0) {
			ExceptionHandler.notifyUser(new NoStorageClientDefinedException("No StorageClient available")); //TODO perhaps offer some help
			registeredClients.clear();
			return;
		}
		
		try {
			for (IConfigurationElement e : config) {
				final Object o = e.createExecutableExtension("class");
				if (o instanceof IStorageClient) {
					final IStorageClient sc = (IStorageClient) o;
					registeredClients.put(sc.getName(), sc);
				}
			}
		} catch (CoreException ex) {
			ExceptionHandler.notifyUser("An eclipse internal error occurred while determining StorageClients", ex);
		}
	}
	
	/**
	 * Checks whether the given {@link IStorageClient} is already registered
	 * @param sc {@link IStorageClient} to be checked for registration
	 * @return true, if the given {@link IStorageClient} is already registered<br>false, otherwise
	 * @author Malte Brunnlieb (28.03.2012)
	 */
	public boolean isRegistered(IStorageClient sc) {
		return registeredClients.containsKey(sc.getName());
	}
	
	/**
	 * This method calls the {@link IStorageClient#getName()} method for the currently active {@link IStorageClient} in a save way
	 * @see IStorageClient#getName()
	 * @author Malte Brunnlieb (24.03.2012)
	 */
	@Override
	public String getName() {
		try {
			return registeredClients.get(activeClient).getName();
		} catch (Throwable ex) {
			ExceptionHandler.notifyUser("An unknown exception occured in StorageClient '" + activeClient + "' while retrieving the its name", ex);
		}
		return null;
	}
	
	/**
	 * This method calls the {@link IStorageClient#getAllReviews()} method for the currently active {@link IStorageClient} in a save way
	 * @see org.agilereview.core.external.definition.IStorageClient#getAllReviews()
	 * @author Malte Brunnlieb (24.03.2012)
	 */
	@Override
	public List<Review> getAllReviews() {
		try {
			return registeredClients.get(activeClient).getAllReviews();
		} catch (Throwable ex) {
			ExceptionHandler.notifyUser("An unknown exception occured in StorageClient '" + activeClient + "' while retrieving all comments", ex);
		}
		return null;
	}
}
