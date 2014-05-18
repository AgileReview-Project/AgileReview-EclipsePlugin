/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.common.properties.parser;

import java.util.regex.Pattern;

/**
 * 
 * @author Malte Brunnlieb (18.05.2014)
 */
public class CommentTagRegexBuilder {
    
    public static final String RAW_TAG_REGEX = "-?(\\?)?" + Pattern.quote(keySeparator) + "(.+?)" + Pattern.quote(keySeparator) + "(\\?)?(-)?";
    
}
