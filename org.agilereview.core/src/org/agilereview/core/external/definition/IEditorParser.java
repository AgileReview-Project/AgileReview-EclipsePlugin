/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.external.definition;

import org.eclipse.ui.IEditorPart;

/**
 * An {@link IEditorParser} manages to parse a concrete editor type e.g. ITextEditor, sets comment tags and marks them according to current ui
 * selections.
 * @author Malte Brunnlieb (13.07.2012)
 */
public interface IEditorParser {
    
    /**
     * Adds comment tags to the current selection of the currently opened editor. The tags will capture the tagId as information.
     * @param tagId id for identifying the connected comment
     * @param editor {@link IEditorPart} in which the tags should be added
     * @param multiLineCommentTags a two-dimensional array containing the begin and end tag which should be used for inserting the comment anchor
     * @author Malte Brunnlieb (15.07.2012)
     */
    public void addTagsToEditorSelection(IEditorPart editor, String tagId, String[] multiLineCommentTags);
    
    /**
     * Removes all tags according to the given tagId
     * @param tagId id of the comment to be removed
     * @param editor {@link IEditorPart} in which the tags with the given id should be removed
     * @param multiLineCommentTags a two-dimensional array containing the begin and end tag which should be used for inserting the comment anchor
     * @author Malte Brunnlieb (13.07.2012)
     */
    public void removeTagsInEditor(IEditorPart editor, String tagId, String[] multiLineCommentTags);
    
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
    
    /**
     * Disables the parser plug-in and all parsers created by this instance. In this method you should also remove all annotations and other
     * AgileReview specific features. Will be called if the AgileReview perspective is disabled.
     * @author Malte Brunnlieb (03.12.2012)
     */
    public void removeAllInstances();
    
    /**
     * Removes the parser for the given editor. This method is called iff the respective {@link IEditorPart} has been closed in order to free
     * resources and do not work on invalid references.
     * @param editor {@link IEditorPart} for which the parser should be removed
     * @author Malte Brunnlieb (04.12.2012)
     */
    public void removeParser(IEditorPart editor);
    
    /**
     * Adds a parser to the given editor. All ui supported improvements like annotations should also be instantiated.
     * @param editor {@link IEditorPart} for which the parser should be added
     * @author Malte Brunnlieb (03.12.2012)
     */
    public void addInstance(IEditorPart editor);
}
