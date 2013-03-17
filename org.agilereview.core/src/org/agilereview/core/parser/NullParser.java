/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.parser;

import org.agilereview.core.external.definition.IEditorParser;
import org.agilereview.core.external.storage.Comment;
import org.eclipse.ui.IEditorPart;

/**
 * 
 * @author Malte Brunnlieb (04.12.2012)
 */
public class NullParser implements IEditorParser {
    
    /* (non-Javadoc)
     * @see org.agilereview.core.external.definition.IEditorParser#addTagsToEditorSelection(org.eclipse.ui.IEditorPart, java.lang.String, java.lang.String[])
     * @author Malte Brunnlieb (04.12.2012)
     */
    @Override
    public void addTagsToEditorSelection(IEditorPart editor, Comment comment, String[] multiLineCommentTags) {
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.core.external.definition.IEditorParser#removeTagsInEditor(org.eclipse.ui.IEditorPart, java.lang.String, java.lang.String[])
     * @author Malte Brunnlieb (04.12.2012)
     */
    @Override
    public void removeTagsInEditor(IEditorPart editor, Comment comment, String[] multiLineCommentTags) {
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.core.external.definition.IEditorParser#removeAllInstances()
     * @author Malte Brunnlieb (04.12.2012)
     */
    @Override
    public void removeAllInstances() {
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.core.external.definition.IEditorParser#removeParser(org.eclipse.ui.IEditorPart)
     * @author Malte Brunnlieb (04.12.2012)
     */
    @Override
    public void removeParser(IEditorPart editor) {
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.core.external.definition.IEditorParser#addInstance(org.eclipse.ui.IEditorPart)
     * @author Malte Brunnlieb (04.12.2012)
     */
    @Override
    public void addInstance(IEditorPart editor, String[] multiLineCommentTags) {
    }
    
}
