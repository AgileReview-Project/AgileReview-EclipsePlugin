/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.external.definition;

import java.io.File;

/**
 * 
 * @author Malte Brunnlieb (18.05.2014)
 */
public interface IFileParser {
    
    public void addTags(File file, int startLine, int endLine, String[] multiLineCommentTags);
    
    /**
     * 
     * @param file
     * @param tagId
     * @param multiLineCommentTags
     * @author Malte Brunnlieb (18.05.2014)
     */
    public void removeTags(File file, String tagId, String[] multiLineCommentTags);
    
    /**
     * Clears all tags in the given File
     * @author Malte Brunnlieb (18.05.2014)
     */
    public void clearAllTags(File file, String[] multiLineCommentTags);
}
