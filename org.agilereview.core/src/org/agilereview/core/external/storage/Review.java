/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.external.storage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.agilereview.core.Activator;
import org.agilereview.core.external.preferences.AgileReviewPreferences;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;

/**
 * A class that stores review data and a list of comments belonging to the review.
 * @author Peter Reuter (19.02.2012)
 */
public class Review implements PropertyChangeListener {
    
    /**
     * The unique name of the review entered by the user
     */
    private final String id;
    /**
     * The status of the {@link Review}
     */
    private int status = 0;
    /**
     * A reference to e.g. a bug tracker
     */
    private String reference = "";
    /**
     * The person that is in charge for the {@link Review}
     */
    private String responsibility = "";
    /**
     * A description of the {@link Review}
     */
    private String description = "";
    /**
     * A {@link List} of {@link Comment}s that belong to this {@link Review}
     */
    private List<Comment> comments = new ArrayList<Comment>(0);
    /**
     * A flag indicating whether the {@link Review} is closed or open
     */
    private boolean isOpen = true;
    /**
     * {@link PropertyChangeSupport} of this POJO, used for firing {@link PropertyChangeEvent}s on changes of fields.
     */
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    
    /**
     * Constructor that should be used if a new {@link Review}w is created.
     * @param id a unique name for the {@link Review} entered by the user
     */
    public Review(String id) {
        this.id = id;
        setOpenReviewsPreference();
    }
    
    /**
     * Constructor that should be used if a {@link Review} is reconstructed from storage
     * @param id the id of the {@link Review}
     * @param status the status of the {@link Review}
     * @param reference e.g. a reference to a bug tracker
     * @param responsibility the person that is in charge for this {@link Review}
     * @param description a text describing the e.g. the content or scope of this {@link Review}
     * @param isOpen indicates whether comments for this review are loaded or not
     */
    public Review(String id, int status, String reference, String responsibility, String description, boolean isOpen) {
        this(id);
        this.status = status;
        this.reference = reference;
        this.responsibility = responsibility;
        this.description = description;
        this.isOpen = isOpen;
    }
    
    /**
     * @return the ID of the {@link Review}
     */
    public String getId() {
        return id;
    }
    
    /**
     * @return the current status of the {@link Review}
     */
    public int getStatus() {
        return status;
    }
    
    /**
     * @param status the new status of the {@link Review}
     */
    public void setStatus(int status) {
        int oldValue = this.status;
        this.status = status;
        propertyChangeSupport.firePropertyChange("status", oldValue, this.status);
    }
    
    /**
     * @return the current reference of the {@link Review}
     */
    public String getReference() {
        return reference;
    }
    
    /**
     * @param reference the new reference of the {@link Review}
     */
    public void setReference(String reference) {
        String oldValue = this.reference;
        this.reference = reference;
        propertyChangeSupport.firePropertyChange("reference", oldValue, this.reference);
    }
    
    /**
     * @return the person that currently is in charge for this {@link Review}
     */
    public String getResponsibility() {
        return responsibility;
    }
    
    /**
     * @param responsibility the person that now is in charge for this {@link Review}
     */
    public void setResponsibility(String responsibility) {
        String oldValue = this.responsibility;
        this.responsibility = responsibility;
        propertyChangeSupport.firePropertyChange("responsibility", oldValue, this.responsibility);
    }
    
    /**
     * @return a list of comments belonging to this {@link Review}
     */
    public List<Comment> getComments() {
        return comments;
    }
    
    /**
     * Sets the {@link Comment}s of this {@link Review}.<br><strong>Note: </strong>This method is intended to be used only while deriving
     * {@link Comment}s from external storage!
     * @param comments
     * @author Peter Reuter (28.04.2012)
     */
    public void setComments(List<Comment> comments) {
        this.comments = comments;
        for (Comment comment : this.comments) {
            comment.addPropertyChangeListener(this);
        }
    }
    
    /**
     * Adds a {@link Comment} to this {@link Review} if and only if it was not added before.
     * @param comment the {@link Comment} that is to be added to the {@link List} of {@link Comment}s
     */
    public void addComment(Comment comment) {
        if (!this.comments.contains(comment)) {
            ArrayList<Comment> oldValue = new ArrayList<Comment>(this.comments);
            this.comments.add(comment);
            comment.addPropertyChangeListener(this);
            propertyChangeSupport.firePropertyChange("comments", oldValue, this.comments);
        }
    }
    
    /**
     * Deletes the given {@link Comment} this {@link Review}.
     * @param comment the {@link Comment} that is to be removed from the {@link List} of {@link Comment}s
     */
    public void deleteComment(Comment comment) {
        ArrayList<Comment> oldValue = new ArrayList<Comment>(this.comments);
        this.comments.remove(comment);
        comment.removePropertyChangeListener(this);
        propertyChangeSupport.firePropertyChange("comments", oldValue, this.comments);
    }
    
    /**
     * Deletes the {@link Comment} with the given index from this {@link Review};
     * @param index the index of the {@link Comment} that is to be deleted
     */
    public void deleteComment(int index) {
        deleteComment(this.comments.get(index));
    }
    
    /**
     * Removes all {@link Comment}s from this {@link Review}. This method is intended to be used when closing reviews or using the cleanup function.
     * @author Peter Reuter (26.06.2012)
     */
    public void clearComments() {
        ArrayList<Comment> oldValue = new ArrayList<Comment>(this.comments);
        for (Comment c : this.comments) {
            c.clearReplies();
        }
        this.comments.clear();
        propertyChangeSupport.firePropertyChange("comments", oldValue, this.comments);
    }
    
    /**
     * @return <code>true</code> if the {@link Review} is open, <code>false</code> otherwise
     */
    public boolean getIsOpen() {
        return this.isOpen;
    }
    
    /**
     * @param isOpen the new state of the {@link Review}
     */
    public void setIsOpen(boolean isOpen) {
        boolean oldValue = this.isOpen;
        this.isOpen = isOpen;
        setOpenReviewsPreference();
        propertyChangeSupport.firePropertyChange("isOpen", oldValue, this.isOpen);
    }
    
    /**
     * @return the current description of the {@link Review}
     */
    public String getDescription() {
        return this.description;
    }
    
    /**
     * @param description the new description of the {@link Review}
     */
    public void setDescription(String description) {
        String oldValue = this.description;
        this.description = description;
        propertyChangeSupport.firePropertyChange("description", oldValue, this.description);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return id;
    }
    
    /**
     * Adds a {@link PropertyChangeListener} to the list of listeners that are notified on {@link PropertyChangeEvent}s
     * @param listener
     * @author Peter Reuter (28.04.2012)
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
    
    /**
     * Removes a {@link PropertyChangeListener} from the list of listeners that are notified on {@link PropertyChangeEvent}s
     * @param listener
     * @author Peter Reuter (28.04.2012)
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        propertyChangeSupport.firePropertyChange(evt);
    }
    
    /**
     * Adds/removes this review to/from the preference holding a comma separated list of open reviews depending on its "open/closed" state.
     * @author Peter Reuter (26.06.2012)
     */
    void setOpenReviewsPreference() {
        List<String> reviewIds = new ArrayList<String>(Arrays.asList(Platform.getPreferencesService().getString(Activator.PLUGIN_ID, AgileReviewPreferences.OPEN_REVIEWS,
                "", null).split(",")));
        if (this.isOpen) {
            if (!reviewIds.contains(this.id)) {
                reviewIds.add(this.id);
            }
        } else {
            reviewIds.remove(this.id);
        }
        String reviewIdsPref = "";
        Iterator<String> i = reviewIds.iterator();
        for (;;) {
            reviewIdsPref += i.next();
            if (!i.hasNext()) break;
            reviewIdsPref += (",");
        }
        InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID).put(AgileReviewPreferences.OPEN_REVIEWS, reviewIdsPref);
    }
    
}
