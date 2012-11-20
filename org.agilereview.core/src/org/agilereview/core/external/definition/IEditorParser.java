/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.external.definition;

import org.eclipse.core.resources.IFile;

/**
 * An {@link IEditorParser} manages to parse a concrete editor type e.g. ITextEditor, sets comment tags and marks them according to current ui
 * selections.
 * @author Malte Brunnlieb (13.07.2012)
 */
public interface IEditorParser {
    
    /**
     * Adds comment tags to the selection defined by the given offset and length. The tags will capture the tagId as information.
     * @param file {@link IFile} which should be annotated
     * @param startLine start of the annotation
     * @param endLine end of the annotation
     * @param tagId id for identifying the connected comment
     * @author Malte Brunnlieb (13.07.2012)
     */
    public void addTags(IFile file, int startLine, int endLine, String tagId);
    
    /**
     * Adds comment tags to the current selection of the currently opened editor. The tags will capture the tagId as information.
     * @param tagId id for identifying the connected comment
     * @author Malte Brunnlieb (15.07.2012)
     */
    public void addTagsToCurrentEditorSelection(String tagId);
    
    /**
     * Removes all tags according to the given tagId
     * @param file {@link IFile} in which the tags should be removed
     * @param tagId id of the comment to be removed
     * @author Malte Brunnlieb (13.07.2012)
     */
    public void removeTags(IFile file, String tagId);
    
    /**
     * Removes all tags according to the given tagId
     * @param tagId id of the comment to be removed
     * @author Malte Brunnlieb (13.07.2012)
     */
    public void removeTagsInCurrentEditor(String tagId);
    
    /**
     * Deletes all Tags in the parsed editor
     * @author Malte Brunnlieb (16.11.2012)
     */
    public void clearAllTags();
    
    /**
     * Forces the editor parser to reparse its contents<br>This will be necessary after every kind of refactoring
     * @author Malte Brunnlieb (16.11.2012)
     */
    public void reparse();
}
