/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.external.properties;

import org.agilereview.core.Activator;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * 
 * @author Malte Brunnlieb (28.04.2012)
 */
public class PropertyInterface {
	
	/**
	 * The preference store of this plugin, so other plugins can query it
	 */
	private IPreferenceStore prefs = null;
	
	/**
	 * Comma separated list of comment status
	 */
	public static final String COMMENT_STATUS = "org.agilereview.comment_status";
	/**
	 * Comma separated list of comment priorities
	 */
	public static final String COMMENT_PRIORITIES = "org.agilereview.comment_priorities";
	/**
	 * id of the currently active review
	 */
	public static final String ACTIVE_REVIEW_ID = "org.agilereview.active_review_id";
	
	public PropertyInterface () {
		this.prefs = Activator.getDefault().getPreferenceStore();
	}
	
	public String getPreferenceValue(String key) {
		return prefs.getString(key);
	}
}
