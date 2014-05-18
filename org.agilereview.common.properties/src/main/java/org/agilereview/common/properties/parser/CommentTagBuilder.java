/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.common.properties.parser;

import java.util.Properties;

/**
 * Using the {@link CommentTagBuilder} you can generate a specific instance of a comment tag assuring the conventions such that every plug-in will be
 * able to interpret your tags afterwards.
 * @author Malte Brunnlieb (18.05.2014)
 */
public class CommentTagBuilder {
    
    private static Properties parserProperties;
    
    private final String startTag;
    private final String endTag;
    private boolean isStartTag;
    private boolean isEndTag;
    private boolean cleanupLineWithCommentRemoval;
    
    /**
     * Creates a new {@link CommentTagBuilder} instance
     * @param multilineCommentStartSign the multi-line start sign (e.g. /* for java)
     * @param multilineCommentEndSign the multi-line end sign (e.g. {@literal *}/ for java)
     * @author Malte Brunnlieb (18.05.2014)
     */
    private CommentTagBuilder(String multilineCommentStartSign, String multilineCommentEndSign) {
        startTag = multilineCommentStartSign;
        endTag = multilineCommentEndSign;
        parserProperties = ParserProperties.newParserProperties();
    }
    
    public CommentTagBuilder isStartTag() {
        isStartTag = true;
        return this;
    }
    
    public CommentTagBuilder isEndTag() {
        isEndTag = true;
        return this;
    }
    
    public CommentTagBuilder isSingleLine() {
        isStartTag = true;
        isEndTag = true;
        return this;
    }
    
    public CommentTagBuilder cleanupLineWithCommentRemoval() {
        cleanupLineWithCommentRemoval = true;
        return this;
    }
    
    public String buildTag() {
        return startTag + (startTag)
    }
}
