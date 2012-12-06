/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.editorparser.itexteditor.control;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;

import org.agilereview.core.external.definition.IReviewDataReceiver;
import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Review;
import org.agilereview.core.external.storage.ReviewSet;

/**
 * This class manages all data received via the {@link IReviewDataReceiver} interface and triggers painting a filtered set of comments as annotations
 * @author Malte Brunnlieb (22.11.2012)
 */
public class DataManager implements IReviewDataReceiver, PropertyChangeListener {
    
    /**
     * Current set of reviews provided by the core plug-in
     */
    private ReviewSet reviews;
    /**
     * Instance created by the AgileReview core plug-in
     */
    private static volatile DataManager instance;
    
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
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
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
        reviews.addPropertyChangeListener(this);
    }
    
    /* (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     * @author Malte Brunnlieb (22.11.2012)
     */
    @Override
    public void propertyChange(PropertyChangeEvent arg0) {
        // TODO Implement Filtermechanism
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
                if (c.getId().equals(id)) { return c; }
            }
        }
        return null;
    }
}
