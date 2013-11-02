/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.editorparser.itexteditor.control;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashSet;
import java.util.Set;

import org.agilereview.core.external.definition.IReviewDataReceiver;
import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Review;
import org.agilereview.core.external.storage.ReviewSet;
import org.agilereview.core.external.storage.listeners.ICommentFilterListener;

/**
 * This class manages all data received via the {@link IReviewDataReceiver} interface and triggers painting a filtered set of comments as annotations
 * @author Malte Brunnlieb (22.11.2012)
 */
public class DataManager implements IReviewDataReceiver, ICommentFilterListener {
    
    /**
     * Current set of reviews provided by the core plug-in
     */
    private ReviewSet reviews;
    /**
     * Currently visible comments
     */
    private Set<Comment> visibleComments;
    /**
     * Instance created by the AgileReview core plug-in
     */
    private static volatile DataManager instance;
    
    /**
     * Property change support for the filter mechanism
     */
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    
    /**
     * Constructor which saves the current created instance for further usage
     * @author Malte Brunnlieb (23.11.2012)
     */
    public DataManager() {
        instance = this;
    }
    
    /**
     * Returns the single instance of the {@link IReviewDataReceiver} created by the AgileReview core plug-in
     * @return the last instance created by the framework
     * @author Malte Brunnlieb (23.11.2012)
     */
    public static DataManager getInstance() {
        while (instance == null) {
        }
        return instance;
    }
    
    /*
     * (non-Javadoc)
     * @see org.agilereview.core.external.definition.IReviewDataReceiver#setReviewData(org.agilereview.core.external.storage.ReviewSet)
     * @author Malte Brunnlieb (22.11.2012)
     */
    @Override
    public void setReviewData(ReviewSet reviews) {
        this.reviews = reviews;
        reviews.addCommentFilterListener(this);
    }
    
    /**
     * Searches for a comment with the given Id and returns it.
     * @param id the Id of the comment
     * @return the {@link Comment}, if there is one with the given Id<br>null, otherwise
     * @author Malte Brunnlieb (22.11.2012)
     */
    public Comment getComment(String id) {
        Set<Review> reviews = new HashSet<Review>(this.reviews);
        for (Review r : reviews) {
            for (Comment c : r.getComments()) {
                if (c.getId().equals(id)) {
                    return c;
                }
            }
        }
        return null;
    }
    
    /**
     * Determines whether the given comment should be highlighted
     * @param comment {@link Comment}
     * @return <code>true</code> if the comment should be highlighted, <br><code>false</code> otherwise
     * @author Malte Brunnlieb (01.06.2013)
     */
    public boolean isVisible(Comment comment) {
        return visibleComments != null && visibleComments.contains(comment);
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.core.external.storage.listeners.ICommentFilterListener#setFilteredComments(java.util.Set)
     * @author Malte Brunnlieb (01.06.2013)
     */
    @Override
    public void setFilteredComments(Set<Comment> filteredComments) {
        visibleComments = filteredComments;
        propertyChangeSupport.firePropertyChange("visibleComments", null, visibleComments);
        System.out.println("new comment!");
    }
    
    /**
     * Added a {@link PropertyChangeListener} for the set of visible comments
     * @param listener {@link PropertyChangeListener} for the set of visible comments
     * @author Malte Brunnlieb (02.11.2013)
     */
    public void addVisibleCommentsListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
}
