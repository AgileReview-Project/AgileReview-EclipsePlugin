/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Apache License v2.0 which accompanies this distribution, and is available
 * at http://www.apache.org/licenses/LICENSE-2.0.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.controller;

import org.agilereview.core.external.definition.IStorageClient;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IRegistryEventListener;

/**
 * 
 * @author Malte Brunnlieb (28.03.2012)
 */
public class RegistryListener implements IRegistryEventListener {
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IRegistryEventListener#added(org.eclipse.core.runtime.IExtension[])
	 * @author Malte Brunnlieb (28.03.2012)
	 */
	@Override
	public void added(IExtension[] extensions) {
		for (IExtension e : extensions) {
			if (e.getExtensionPointUniqueIdentifier().equals(StorageController.ISTORAGECLIENT_ID)) {
				for (IConfigurationElement ce : e.getConfigurationElements()) {
					final Object o = ce.createExecutableExtension("class");
					if (o instanceof IStorageClient) {
						if(!)
						final IStorageClient sc = (IStorageClient) o;
						registeredClients.put(sc.getName(), sc);
					}
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IRegistryEventListener#removed(org.eclipse.core.runtime.IExtension[])
	 * @author Malte Brunnlieb (28.03.2012)
	 */
	@Override
	public void removed(IExtension[] extensions) {
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IRegistryEventListener#added(org.eclipse.core.runtime.IExtensionPoint[])
	 * @author Malte Brunnlieb (28.03.2012)
	 */
	@Override
	public void added(IExtensionPoint[] extensionPoints) {
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IRegistryEventListener#removed(org.eclipse.core.runtime.IExtensionPoint[])
	 * @author Malte Brunnlieb (28.03.2012)
	 */
	@Override
	public void removed(IExtensionPoint[] extensionPoints) {
		// TODO Auto-generated method stub
		
	}
	
}
