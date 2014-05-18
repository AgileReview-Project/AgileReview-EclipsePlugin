/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.common.properties.parser;

import java.io.IOException;
import java.util.Properties;

import org.agilereview.common.properties.exception.CommonPropertiesTechnicalRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class enables access to all configurable parser related properties
 * @author Malte Brunnlieb (18.05.2014)
 */
public class ParserProperties {
    
    /**
     * Logger implementation
     */
    private static final Logger LOG = LoggerFactory.getLogger(ParserProperties.class);
    
    /**
     * Returns a new instance of the {@link Properties} for accessing all parser related configuration
     * @return a new instance of the parser {@link Properties}
     * @author Malte Brunnlieb (18.05.2014)
     */
    public static Properties newParserProperties() {
        Properties properties = new Properties();
        try {
            properties.load(ParserProperties.class.getResourceAsStream("/resources/parser/parser.properties"));
        } catch (IOException e) {
            throw new CommonPropertiesTechnicalRuntimeException();
        }
        return properties;
    }
    
    /**
     * Key Separator for deprecated review / author / comment key notation
     */
    public final static String KEY_SEPARATOR = "parser.keySeparator";
    
    /**
     * Marker sign to distinguish start and end tags from each other
     */
    public final static String START_END_TAG_MARKER_SIGN = "parser.startEndTagMarkerSign";
    
    /**
     * Marker sign to mark the commented line to be removed after comment removal
     */
    public final static String LINE_REMOVAL_MARKER_SIGN = "parser.lineRemovalMarkerSign";
}
