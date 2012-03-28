/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Apache License v2.0 which accompanies this distribution, and is available
 * at http://www.apache.org/licenses/LICENSE-2.0.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.controller;

import org.agilereview.core.Activator;
import org.agilereview.core.external.definition.IReviewDataReceiver;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IRegistryEventListener;
import org.eclipse.core.runtime.Status;

/**
 * 
 * @author Malte Brunnlieb (28.03.2012)
 */
public class RegistryListener implements IRegistryEventListener {
	
	/**
	 * Instance of {@link StorageController}
	 */
	private StorageController sC = StorageController.getInstance();
	/**
	 * Instance of {@link RDRController}
	 */
	private RDRController rdrC = RDRController.getInstance();
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IRegistryEventListener#added(org.eclipse.core.runtime.IExtension[])
	 * @author Malte Brunnlieb (28.03.2012)
	 */
	@Override
	public void added(IExtension[] extensions) {
		
		boolean newStorageClient = false;
		boolean newReviewDataReceiverClient = false;
		
		//check for new clients and provide latest data
		for (IExtension e : extensions) {
			if (e.getExtensionPointUniqueIdentifier().equals(StorageController.ISTORAGECLIENT_ID)) {
				newStorageClient = true;
			} else if (e.getExtensionPointUniqueIdentifier().equals(RDRController.IREVIEWDATARECEIVER_ID)) {
				for (IConfigurationElement ce : e.getConfigurationElements()) {
					try {
						final Object o = ce.createExecutableExtension("class");
						if (o instanceof IReviewDataReceiver) {
							final IReviewDataReceiver rdr = (IReviewDataReceiver) o;
							if (!rdrC.isRegistered(rdr)) {
								rdrC.notifyClient(rdr, sC.getAllReviews());
								newReviewDataReceiverClient = true;
							}
						}
					} catch (CoreException e1) {
						Activator.getDefault().getLog()
								.log(new Status(Status.WARNING, Activator.PLUGIN_ID, "Exception while creating extension " + ce.getName(), e1));
					}
				}
			}
		}
		
		//register all clients if there are new
		if (newStorageClient) {
			sC.checkForNewClients();
		}
		if (newReviewDataReceiverClient) {
			rdrC.checkForNewClients();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IRegistryEventListener#removed(org.eclipse.core.runtime.IExtension[])
	 * @author Malte Brunnlieb (28.03.2012)
	 */
	@Override
	public void removed(IExtension[] extensions) {
		
		boolean storageClientRemoved = false;
		boolean reviewDataReceiverClientRemoved = false;
		
		//check for new clients and provide latest data
		for (IExtension e : extensions) {
			if (e.getExtensionPointUniqueIdentifier().equals(StorageController.ISTORAGECLIENT_ID)) {
				storageClientRemoved = true;
			} else if (e.getExtensionPointUniqueIdentifier().equals(RDRController.IREVIEWDATARECEIVER_ID)) {
				reviewDataReceiverClientRemoved = true;
			}
		}
		
		//register all clients if there are removed ones
		if (storageClientRemoved) {
			sC.checkForNewClients();
		}
		if (reviewDataReceiverClientRemoved) {
			rdrC.checkForNewClients();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IRegistryEventListener#added(org.eclipse.core.runtime.IExtensionPoint[])
	 * @author Malte Brunnlieb (28.03.2012)
	 */
	@Override
	public void added(IExtensionPoint[] extensionPoints) {
		// nothing need to be done here
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IRegistryEventListener#removed(org.eclipse.core.runtime.IExtensionPoint[])
	 * @author Malte Brunnlieb (28.03.2012)
	 */
	@Override
	public void removed(IExtensionPoint[] extensionPoints) {
		// nothing need to be done here
	}
}
