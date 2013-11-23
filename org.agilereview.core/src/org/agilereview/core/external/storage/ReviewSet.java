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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.agilereview.core.external.definition.IReviewDataReceiver;
import org.agilereview.core.external.storage.constants.PropertyChangeEventKeys;
import org.agilereview.core.external.storage.listeners.ICommentFilterListener;

/**
 * List of {@link Review}s provides property change support for the list itself and the reviews contained
 * @author Malte Brunnlieb (03.06.2012)
 */
public final class ReviewSet extends HashSet<Review> implements PropertyChangeListener {
    
    /**
     * Generated serial version UID
     */
    private static final long serialVersionUID = 1180422400668603041L;
    /**
     * {@link PropertyChangeSupport} of this POJO, used for firing {@link PropertyChangeEvent}s on changes of fields.
     */
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    /**
     * The {@link List} of currently registered {@link ICommentFilterListener}s
     */
    private final List<ICommentFilterListener> commentFilterListeners = new LinkedList<ICommentFilterListener>();
    /**
     * Current registered filter
     */
    private final Map<Object, List<ICommentFilter>> currentFilter = new HashMap<Object, List<ICommentFilter>>();
    /**
     * Map for storing generic values
     */
    private final Map<String, Object> genericMap = new HashMap<String, Object>();
    
    /**
     * {@inheritDoc} <br> Added {@link PropertyChangeSupport} for tracking changes by {@link IReviewDataReceiver}
     * @see java.util.ArrayList#add(java.lang.Object)
     * @author Malte Brunnlieb (03.06.2012)
     */
    @Override
    public boolean add(Review e) {
        HashSet<Review> oldValue = new HashSet<Review>(this);
        boolean changed = super.add(e);
        if (changed) {
            e.addPropertyChangeListener(this);
            propertyChangeSupport.firePropertyChange(PropertyChangeEventKeys.REVIEWSET_REVIEWS, oldValue, this);
        }
        return changed;
    }
    
    /**
     * {@inheritDoc} <br> Added {@link PropertyChangeSupport} for tracking changes by {@link IReviewDataReceiver}
     * @see java.util.ArrayList#addAll(java.util.Collection)
     * @author Malte Brunnlieb (03.06.2012)
     */
    @Override
    public boolean addAll(Collection<? extends Review> c) {
        HashSet<Review> oldValue = new HashSet<Review>(this);
        boolean changed = super.addAll(c);
        if (changed) {
            for (Review r : c) {
                r.addPropertyChangeListener(this);
            }
            propertyChangeSupport.firePropertyChange(PropertyChangeEventKeys.REVIEWSET_REVIEWS, oldValue, this);
        }
        return changed;
    }
    
    /**
     * {@inheritDoc} <br> Added {@link PropertyChangeSupport} for tracking changes by {@link IReviewDataReceiver}
     * @see java.util.ArrayList#remove(java.lang.Object)
     * @author Malte Brunnlieb (03.06.2012)
     */
    @Override
    public boolean remove(Object o) {
        HashSet<Review> oldValue = new HashSet<Review>(this);
        boolean success = super.remove(o);
        if (success) {
            ((Review) o).setOpenReviewsPreference();
            ((Review) o).removePropertyChangeListener(this);
            propertyChangeSupport.firePropertyChange(PropertyChangeEventKeys.REVIEWSET_REVIEWS, oldValue, this);
        }
        return success;
    }
    
    /**
     * {@inheritDoc} <br> Added {@link PropertyChangeSupport} for tracking changes by {@link IReviewDataReceiver}
     * @see java.util.AbstractCollection#removeAll(java.util.Collection)
     * @author Malte Brunnlieb (03.06.2012)
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        HashSet<Review> oldValue = new HashSet<Review>(this);
        boolean success = super.removeAll(c);
        if (success) {
            for (Object r : c) {
                ((Review) r).setOpenReviewsPreference();
                ((Review) r).removePropertyChangeListener(this);
            }
            propertyChangeSupport.firePropertyChange(PropertyChangeEventKeys.REVIEWSET_REVIEWS, oldValue, this);
        }
        return success;
    }
    
    /**
     * {@inheritDoc} <br> Added {@link PropertyChangeSupport} for tracking changes by {@link IReviewDataReceiver}
     * @see java.util.AbstractCollection#retainAll(java.util.Collection)
     * @author Malte Brunnlieb (03.06.2012)
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        HashSet<Review> oldValue = new HashSet<Review>(this);
        boolean success = super.retainAll(c);
        if (success) {
            ArrayList<Review> removedOnes = new ArrayList<Review>(oldValue);
            removedOnes.removeAll(c);
            for (Review r : removedOnes) {
                r.setOpenReviewsPreference();
                r.removePropertyChangeListener(this);
            }
            propertyChangeSupport.firePropertyChange(PropertyChangeEventKeys.REVIEWSET_REVIEWS, oldValue, this);
        }
        return success;
    }
    
    /**
     * {@inheritDoc} <br> Added {@link PropertyChangeSupport} for tracking changes by {@link IReviewDataReceiver}
     * @see java.util.ArrayList#clear()
     * @author Malte Brunnlieb (03.06.2012)
     */
    @Override
    public void clear() {
        HashSet<Review> oldValue = new HashSet<Review>(this);
        for (Review r : this) {
            r.clearComments();
            r.setOpenReviewsPreference();
            r.removePropertyChangeListener(this);
        }
        super.clear();
        propertyChangeSupport.firePropertyChange(PropertyChangeEventKeys.REVIEWSET_REVIEWS, oldValue, this);
    }
    
    /**
     * Sets the given comments as the current filter for the given source. In order to provide different parallel filter mechanisms, a filter depends
     * on its managing source object
     * @param source object which manages the filter mechanism
     * @param filter the filtered set of comments to be shown
     * @author Malte Brunnlieb (10.03.2013)
     */
    public void setCommentFilter(Object source, List<ICommentFilter> filter) {
        synchronized (currentFilter) {
            currentFilter.put(source, filter);
            filterComments();
        }
    }
    
    /**
     * Removes the filter for the given source
     * @param source object on which the filter is registered
     * @author Malte Brunnlieb (28.05.2013)
     */
    public void removeCommentFilter(Object source) {
        synchronized (currentFilter) {
            currentFilter.remove(source);
            filterComments();
        }
    }
    
    /**
     * Filters the currently visible set of comments
     * @author Malte Brunnlieb (01.06.2013)
     */
    private void filterComments() {
        List<Comment> allComments = new LinkedList<Comment>();
        for (Review r : this) {
            allComments.addAll(r.getComments());
        }
        
        if (currentFilter.isEmpty()) {
            notifyCommentFilterListeners(new HashSet<Comment>(allComments));
        } else {
            Set<Comment> filteredComments = new HashSet<Comment>();
            for (List<ICommentFilter> matcher : currentFilter.values()) {
                for (ICommentFilter cf : matcher) {
                    Iterator<Comment> it = allComments.iterator();
                    while (it.hasNext()) {
                        Comment c = it.next();
                        if (cf.accept(c)) {
                            filteredComments.add(c);
                            it.remove();
                        }
                    }
                }
            }
            notifyCommentFilterListeners(filteredComments);
        }
    }
    
    /**
     * 
     * @param filteredComments
     * @author Malte Brunnlieb (01.06.2013)
     */
    private void notifyCommentFilterListeners(Set<Comment> filteredComments) {
        for (ICommentFilterListener listener : commentFilterListeners) {
            listener.setFilteredComments(new HashSet<Comment>(filteredComments));
        }
    }
    
    /**
     * Adds the given {@link ICommentFilterListener}
     * @param listener {@link ICommentFilterListener} to be added
     * @author Malte Brunnlieb (01.06.2013)
     */
    public void addCommentFilterListener(ICommentFilterListener listener) {
        commentFilterListeners.add(listener);
    }
    
    /**
     * Removes the given {@link ICommentFilterListener}
     * @param listener {@link ICommentFilterListener} to be removed
     * @author Malte Brunnlieb (01.06.2013)
     */
    public void removeCommentFilterListener(ICommentFilterListener listener) {
        commentFilterListeners.remove(listener);
    }
    
    /**
     * Possibility to store generic values in the review set (including {@link PropertyChangeSupport})
     * @param key key under which the value will be stored. The key is also the <i>propertyName</i> which will be used for propertyChange events
     * @param value value to store
     * @return old value stored under this key. Can be <code>null</code>.
     * @author Thilo Rauch (26.11.2012)
     */
    public Object storeValue(String key, Object value) {
        Object oldObject = genericMap.put(key, value);
        propertyChangeSupport.firePropertyChange(PropertyChangeEventKeys.REVIEWSET_METADATA, oldObject, value);
        return oldObject;
    }
    
    /**
     * Retrieve the value stored under this key.
     * @param key key to retrieve
     * @return <code>null</code> if the key is null or nothing (or null) is stored under this key
     * @author Thilo Rauch (26.11.2012)
     */
    public Object getValue(String key) {
        if (key == null) {
            return null;
        }
        return genericMap.get(key);
    }
    
    /**
     * Adds a {@link PropertyChangeListener} to the list of listeners that are notified on {@link PropertyChangeEvent}s
     * @param listener
     * @author Malte Brunnlieb (03.06.2012)
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
    
    /**
     * Removes a {@link PropertyChangeListener} from the list of listeners that are notified on {@link PropertyChangeEvent}s
     * @param listener
     * @author Malte Brunnlieb (03.06.2012)
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
    
    /*
     * (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     * @author Malte Brunnlieb (03.06.2012)
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        propertyChangeSupport.firePropertyChange(evt);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     * @author Malte Brunnlieb (17.01.2013)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof ReviewSet) {
            return this.hashCode() == obj.hashCode();
        } else {
            return false;
        }
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     * @author Malte Brunnlieb (17.01.2013)
     */
    @Override
    public int hashCode() {
        return (int) (super.hashCode() ^ serialVersionUID);
    }
}
