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
import java.util.Collection;
import java.util.LinkedList;

import org.agilereview.core.external.definition.IReviewDataReceiver;

/**
 * List of {@link Review}s provides property change support for the list itself and the reviews contained
 * @author Malte Brunnlieb (03.06.2012)
 */
public class ReviewList extends LinkedList<Review> implements PropertyChangeListener {
    
    /**
     * Generated serial version UID
     */
    private static final long serialVersionUID = 1180422400668603041L;
    /**
     * {@link PropertyChangeSupport} of this POJO, used for firing {@link PropertyChangeEvent}s on changes of fields.
     */
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    
    /**
     * {@inheritDoc} <br> Added {@link PropertyChangeSupport} for tracking changes by {@link IReviewDataReceiver}
     * @see java.util.LinkedList#add(int, java.lang.Object)
     * @author Malte Brunnlieb (03.06.2012)
     */
    @Override
    public void add(int index, Review review) {
        LinkedList<Review> oldValue = new LinkedList<Review>(this);
        super.add(index, review);
        review.addPropertyChangeListener(this);
        propertyChangeSupport.firePropertyChange("ReviewList", oldValue, this);
    }
    
    /**
     * {@inheritDoc} <br> Added {@link PropertyChangeSupport} for tracking changes by {@link IReviewDataReceiver}
     * @see java.util.LinkedList#add(java.lang.Object)
     * @author Malte Brunnlieb (03.06.2012)
     */
    @Override
    public boolean add(Review e) {
        LinkedList<Review> oldValue = new LinkedList<Review>(this);
        boolean changed = super.add(e);
        if (changed) {
            e.addPropertyChangeListener(this);
            propertyChangeSupport.firePropertyChange("ReviewList", oldValue, this);
        }
        return changed;
    }
    
    /**
     * {@inheritDoc} <br> Added {@link PropertyChangeSupport} for tracking changes by {@link IReviewDataReceiver}
     * @see java.util.LinkedList#addAll(java.util.Collection)
     * @author Malte Brunnlieb (03.06.2012)
     */
    @Override
    public boolean addAll(Collection<? extends Review> c) {
        LinkedList<Review> oldValue = new LinkedList<Review>(this);
        boolean changed = super.addAll(c);
        if (changed) {
            for (Review r : c) {
                r.addPropertyChangeListener(this);
            }
            propertyChangeSupport.firePropertyChange("ReviewList", oldValue, this);
        }
        return changed;
    }
    
    /**
     * {@inheritDoc} <br> Added {@link PropertyChangeSupport} for tracking changes by {@link IReviewDataReceiver}
     * @see java.util.LinkedList#addAll(int, java.util.Collection)
     * @author Malte Brunnlieb (03.06.2012)
     */
    @Override
    public boolean addAll(int index, Collection<? extends Review> c) {
        LinkedList<Review> oldValue = new LinkedList<Review>(this);
        boolean changed = super.addAll(index, c);
        if (changed) {
            for (Review r : c) {
                r.addPropertyChangeListener(this);
            }
            propertyChangeSupport.firePropertyChange("ReviewList", oldValue, this);
        }
        return changed;
    }
    
    /**
     * {@inheritDoc} <br> Added {@link PropertyChangeSupport} for tracking changes by {@link IReviewDataReceiver}
     * @see java.util.LinkedList#addFirst(java.lang.Object)
     * @author Malte Brunnlieb (03.06.2012)
     */
    @Override
    public void addFirst(Review e) {
        LinkedList<Review> oldValue = new LinkedList<Review>(this);
        super.addFirst(e);
        e.addPropertyChangeListener(this);
        propertyChangeSupport.firePropertyChange("ReviewList", oldValue, this);
    }
    
    /**
     * {@inheritDoc} <br> Added {@link PropertyChangeSupport} for tracking changes by {@link IReviewDataReceiver}
     * @see java.util.LinkedList#addLast(java.lang.Object)
     * @author Malte Brunnlieb (03.06.2012)
     */
    @Override
    public void addLast(Review e) {
        LinkedList<Review> oldValue = new LinkedList<Review>(this);
        super.addLast(e);
        e.addPropertyChangeListener(this);
        propertyChangeSupport.firePropertyChange("ReviewList", oldValue, this);
    }
    
    /**
     * {@inheritDoc} <br> Added {@link PropertyChangeSupport} for tracking changes by {@link IReviewDataReceiver}
     * @see java.util.LinkedList#remove()
     * @author Malte Brunnlieb (03.06.2012)
     */
    @Override
    public Review remove() {
        LinkedList<Review> oldValue = new LinkedList<Review>(this);
        Review head = super.remove();
        head.removePropertyChangeListener(this);
        propertyChangeSupport.firePropertyChange("ReviewList", oldValue, this);
        return head;
    }
    
    /**
     * {@inheritDoc} <br> Added {@link PropertyChangeSupport} for tracking changes by {@link IReviewDataReceiver}
     * @see java.util.LinkedList#remove(int)
     * @author Malte Brunnlieb (03.06.2012)
     */
    @Override
    public Review remove(int index) {
        LinkedList<Review> oldValue = new LinkedList<Review>(this);
        Review removedObj = super.remove(index);
        removedObj.removePropertyChangeListener(this);
        propertyChangeSupport.firePropertyChange("ReviewList", oldValue, this);
        return removedObj;
    }
    
    /**
     * {@inheritDoc} <br> Added {@link PropertyChangeSupport} for tracking changes by {@link IReviewDataReceiver}
     * @see java.util.LinkedList#remove(java.lang.Object)
     * @author Malte Brunnlieb (03.06.2012)
     */
    @Override
    public boolean remove(Object o) {
        LinkedList<Review> oldValue = new LinkedList<Review>(this);
        boolean success = super.remove(o);
        if (success) {
            ((Review) o).removePropertyChangeListener(this);
            propertyChangeSupport.firePropertyChange("ReviewList", oldValue, this);
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
        LinkedList<Review> oldValue = new LinkedList<Review>(this);
        boolean success = super.removeAll(c);
        if (success) {
            for (Object r : c) {
                ((Review) r).addPropertyChangeListener(this);
            }
            propertyChangeSupport.firePropertyChange("ReviewList", oldValue, this);
        }
        return success;
    }
    
    /**
     * {@inheritDoc} <br> Added {@link PropertyChangeSupport} for tracking changes by {@link IReviewDataReceiver}
     * @see java.util.LinkedList#removeFirst()
     * @author Malte Brunnlieb (03.06.2012)
     */
    @Override
    public Review removeFirst() {
        LinkedList<Review> oldValue = new LinkedList<Review>(this);
        Review head = super.removeFirst();
        head.removePropertyChangeListener(this);
        propertyChangeSupport.firePropertyChange("ReviewList", oldValue, this);
        return head;
    }
    
    /**
     * {@inheritDoc} <br> Added {@link PropertyChangeSupport} for tracking changes by {@link IReviewDataReceiver}
     * @see java.util.LinkedList#removeFirstOccurrence(java.lang.Object)
     * @author Malte Brunnlieb (03.06.2012)
     */
    @Override
    public boolean removeFirstOccurrence(Object o) {
        LinkedList<Review> oldValue = new LinkedList<Review>(this);
        boolean success = super.removeFirstOccurrence(o);
        if (success) {
            ((Review) o).addPropertyChangeListener(this);
            propertyChangeSupport.firePropertyChange("ReviewList", oldValue, this);
        }
        return success;
    }
    
    /**
     * {@inheritDoc} <br> Added {@link PropertyChangeSupport} for tracking changes by {@link IReviewDataReceiver}
     * @see java.util.LinkedList#removeLast()
     * @author Malte Brunnlieb (03.06.2012)
     */
    @Override
    public Review removeLast() {
        LinkedList<Review> oldValue = new LinkedList<Review>(this);
        Review last = super.removeLast();
        last.removePropertyChangeListener(this);
        propertyChangeSupport.firePropertyChange("ReviewList", oldValue, this);
        return last;
    }
    
    /**
     * {@inheritDoc} <br> Added {@link PropertyChangeSupport} for tracking changes by {@link IReviewDataReceiver}
     * @see java.util.LinkedList#removeLastOccurrence(java.lang.Object)
     * @author Malte Brunnlieb (03.06.2012)
     */
    @Override
    public boolean removeLastOccurrence(Object o) {
        LinkedList<Review> oldValue = new LinkedList<Review>(this);
        boolean success = super.removeLastOccurrence(o);
        if (success) {
            ((Review) o).removePropertyChangeListener(this);
            propertyChangeSupport.firePropertyChange("ReviewList", oldValue, this);
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
        LinkedList<Review> oldValue = new LinkedList<Review>(this);
        boolean success = super.retainAll(c);
        if (success) {
            LinkedList<Review> removedOnes = new LinkedList<Review>(oldValue);
            removedOnes.removeAll(c);
            for (Review r : removedOnes) {
                r.removePropertyChangeListener(this);
            }
            propertyChangeSupport.firePropertyChange("ReviewList", oldValue, this);
        }
        return success;
    }
    
    /**
     * {@inheritDoc} <br> Added {@link PropertyChangeSupport} for tracking changes by {@link IReviewDataReceiver}
     * @see java.util.LinkedList#clear()
     * @author Malte Brunnlieb (03.06.2012)
     */
    @Override
    public void clear() {
        LinkedList<Review> oldValue = new LinkedList<Review>(this);
        super.clear();
        for (Review r : oldValue) {
            r.removePropertyChangeListener(this);
        }
        propertyChangeSupport.firePropertyChange("ReviewList", oldValue, this);
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
}
