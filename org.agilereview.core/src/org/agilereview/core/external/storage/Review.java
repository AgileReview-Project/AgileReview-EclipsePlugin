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

import org.agilereview.common.exception.ExceptionHandler;
import org.agilereview.core.Activator;
import org.agilereview.core.external.preferences.AgileReviewPreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

/**
 * A class that stores review data and a list of comments belonging to the review.
 * @author Peter Reuter (19.02.2012)
 */
public class Review implements PropertyChangeListener, IPreferenceChangeListener {
    
    /**
     * The unique name of the review entered by the user
     */
    private final String id;
    /**
     * The name of the review, more understandable for humans than the generated ID.
     */
    private String name = "";
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
     * Indicates whether new comments will be added to this review {@link Review}.
     */
    private boolean isActive = false;
    /**
     * {@link PropertyChangeSupport} of this POJO, used for firing {@link PropertyChangeEvent}s on changes of fields.
     */
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    
    /**
     * Constructor that should be used if a new {@link Review}w is created. The {@link Review} will be added to the list of open reviews.
     * @param id a unique identifier for the {@link Review} computed by the current IStorageClient.
     */
    public Review(String id) {
        this.id = id;
        setOpenReviewsPreference();
        InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID).addPreferenceChangeListener(this);
    }
    
    /**
     * Constructor that should be used if a {@link Review} is reconstructed from storage
     * @param id the id of the {@link Review}
     * @param name the name of the {@link Review}
     * @param status the status of the {@link Review}
     * @param reference e.g. a reference to a bug tracker
     * @param responsibility the person that is in charge for this {@link Review}
     * @param description a text describing the e.g. the content or scope of this {@link Review}
     */
    public Review(String id, String name, int status, String reference, String responsibility, String description) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.reference = reference;
        this.responsibility = responsibility;
        this.description = description;
        IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
        this.isOpen = preferences.get(AgileReviewPreferences.OPEN_REVIEWS, "").contains(this.id);
        this.isActive = this.id.equals(preferences.get(AgileReviewPreferences.ACTIVE_REVIEW_ID, ""));
        preferences.addPreferenceChangeListener(this);
    }
    
    @Override
    protected void finalize() throws Throwable {
        InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID).removePreferenceChangeListener(this);
        super.finalize();
    }
    
    /**
     * @return the ID of the {@link Review}
     */
    public String getId() {
        return id;
    }
    
    /**
     * @return the name of the {@link Review}
     */
    public String getName() {
        return name;
    }
    
    /**
     * @param name the new name of the {@link Review}
     */
    public void setName(String name) {
        String oldValue = this.name;
        this.name = name;
        propertyChangeSupport.firePropertyChange("name", oldValue, this.name);
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
        setIsActive(false);
        propertyChangeSupport.firePropertyChange("isOpen", oldValue, this.isOpen);
    }
    
    /**
     * @return <code>true</code> if the {@link Review} is the current active one, <code>false</code> otherwise.
     * @author Peter Reuter (06.12.2012)
     */
    public boolean getIsActive() {
        return this.isActive;
    }
    
    /**
     * Sets this {@link Review} as the current active one.
     * @author Peter Reuter (06.12.2012)
     */
    public void setToActive() {
        setIsActive(true);
    }
    
    /**
     * Sets the review state to active or resets the {@link AgileReviewPreferences#ACTIVE_REVIEW_ID} preference.
     * @param isActive value indicating whether the review will be the active one or no review at all.
     * @author Peter Reuter (06.12.2012)
     */
    private void setIsActive(boolean isActive) {
        String id = null;
        if (isActive) {
            // current review will be the active one
            id = this.id;
        } else {
            // current review was active, now should no longer be the active one --> no active review available
            if (this.isActive) {
                id = "";
            }
        }
        
        if (id != null) {
            IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
            preferences.put(AgileReviewPreferences.ACTIVE_REVIEW_ID, this.id);
            try {
                preferences.flush();
            } catch (BackingStoreException e) {
                String message = "AgileReview could not persistently save the ID of the active review.";
                ExceptionHandler.logAndNotifyUser(e, message);
            }
        }
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
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        propertyChangeSupport.firePropertyChange(evt);
    }
    
    /**
     * Adds/removes this review to/from the preference holding a comma separated list of open reviews depending on its "open/closed" state.
     * @author Peter Reuter (26.06.2012)
     */
    void setOpenReviewsPreference() {
        IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
        String reviewIdsPref = preferences.get(AgileReviewPreferences.OPEN_REVIEWS, "");
        ArrayList<String> reviewIds;
        
        if ("".equals(reviewIdsPref)) {
            reviewIds = new ArrayList<String>();
        } else {
            reviewIds = new ArrayList<String>(Arrays.asList(reviewIdsPref.split(",")));
        }
        
        if (this.isOpen) {
            if (!reviewIds.contains(this.id)) {
                reviewIds.add(this.id);
            }
        } else {
            reviewIds.remove(this.id);
        }
        
        StringBuffer buf = new StringBuffer();
        if (reviewIds.size() > 0) {
            Iterator<String> i = reviewIds.iterator();
            for (;;) {
                buf.append(i.next());
                if (!i.hasNext()) break;
                buf.append(",");
            }
        }
        preferences.put(AgileReviewPreferences.OPEN_REVIEWS, buf.toString());
        
        try {
            preferences.flush();
        } catch (BackingStoreException e) {
            String message = "AgileReview could not persistently save list of open Reviews.";
            ExceptionHandler.logAndNotifyUser(e, message);
        }
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener#preferenceChange(org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent)
     * @author Peter Reuter (17.01.2013)
     */
    @Override
    public void preferenceChange(PreferenceChangeEvent event) {
        if (AgileReviewPreferences.ACTIVE_REVIEW_ID.equals(event.getKey())) {
            boolean oldValue = this.isActive;
            this.isActive = this.id.equals(event.getNewValue());
            if (this.isActive != oldValue) {
                propertyChangeSupport.firePropertyChange("isActive", oldValue, this.isActive);
            }
        }
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     * @author Peter Reuter (17.01.2013)
     */
    @Override
    public int hashCode() {
        return (this.getId()).hashCode();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     * @author Peter Reuter (17.01.2013)
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o.getClass() != getClass()) {
            return false;
        }
        Review reviewToCompare = (Review) o;
        if (this.getId().equals(reviewToCompare.getId())) {
            return true;
        }
        return false;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Review '" + this.getName() + "'";
    }
    
}
