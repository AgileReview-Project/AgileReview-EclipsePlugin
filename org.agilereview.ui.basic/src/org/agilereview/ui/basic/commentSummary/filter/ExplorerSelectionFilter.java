/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.commentSummary.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;

import org.agilereview.core.external.storage.Comment;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * Filters the viewer's model by the selections of the review explorer
 */
public class ExplorerSelectionFilter extends ViewerFilter {
    
    /**
     * ID's of the selected reviews
     */
    private final ArrayList<String> reviewIDs;
    
    /**
     * paths of the selected projects/folders/files
     */
    private final HashMap<String, HashSet<String>> paths;
    
    /**
     * @param reviewIDs the reviews
     * @param paths the paths
     */
    public ExplorerSelectionFilter(ArrayList<String> reviewIDs, HashMap<String, HashSet<String>> paths) {
        this.reviewIDs = reviewIDs;
        this.paths = paths;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        boolean matches = false;
        if (reviewIDs == null || paths == null || (reviewIDs.isEmpty() && paths.isEmpty())) {
            matches = true;
        } else {
            Comment comment = (Comment) element;
            
            if (reviewIDs.contains(comment.getReview())) {
                matches = true;
            }
            
            HashSet<String> containedPaths = paths.get(comment.getReview().getId());
            if (!(containedPaths == null)) {
                for (String path : containedPaths) {
                    String pathMatcher = ".*" + Pattern.quote(path) + ".*";
                    if (comment.getCommentedFile().getFullPath().toOSString().matches(pathMatcher)) {
                        matches = true;
                    }
                }
            }
        }
        return matches;
    }
    
}