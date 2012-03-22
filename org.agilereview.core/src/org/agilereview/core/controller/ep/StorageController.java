/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.controller.ep;

/**
 * 
 * @author Malte Brunnlieb (22.03.2012)
 */
public class StorageController {
	
	/**
	 * Singleton instance of {@link StorageController}
	 */
	private static final StorageController instance = new StorageController();
	
	/**
	 * Creates a new instance of the {@link StorageController}
	 * @author Malte Brunnlieb (22.03.2012)
	 */
	private StorageController() {
		
	}
	
	/**
	 * Returns the unique instance of the {@link StorageController}
	 * @return the unique instance of the {@link StorageController}
	 * @author Malte Brunnlieb (22.03.2012)
	 */
	public static StorageController getInstance() {
		return instance;
	}
}
