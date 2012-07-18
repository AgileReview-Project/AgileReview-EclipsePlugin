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
import java.util.HashSet;

import org.agilereview.core.external.definition.IReviewDataReceiver;

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
            propertyChangeSupport.firePropertyChange("reviews", oldValue, this);
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
            propertyChangeSupport.firePropertyChange("reviews", oldValue, this);
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
            propertyChangeSupport.firePropertyChange("reviews", oldValue, this);
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
            propertyChangeSupport.firePropertyChange("reviews", oldValue, this);
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
            propertyChangeSupport.firePropertyChange("reviews", oldValue, this);
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
        propertyChangeSupport.firePropertyChange("reviews", oldValue, this);
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
