/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.external.reviewDataReceiver;

import java.lang.reflect.Method;

import org.agilereview.core.external.definition.IReviewDataReceiver;
import org.agilereview.core.external.storage.ReviewSet;

/**
 * This class is part of the framework for providing views with review information. Extend this abstract class and implement the abstract methods to
 * get a receiver for the review data. If you use this class and {@link AbstractReviewDataView} then both classes will connect to each other so the
 * review data will be pushed into the view. Both, View and DataReceiver, are created by eclipse but these abstract classes handle nearly everything
 * regarding instance control for you.
 * 
 * The only thing the extending class has to do is capture the instance created by the framework and provide it trough a static getInstance() method.
 * Example
 * 
 * <p><blockquote><pre> <code> class MyViewDataReceiver extends AbstractReviewDataReceiver { <br> &nbsp; private static MyViewDataReceiver instance;
 * 
 * &nbsp; public MyViewDataReceiver() { super(); instance = this; }
 * 
 * &nbsp; public static MyViewDataReceiver getInstance() { return instance; } }
 * 
 * </code> </blockquote></pre></p>
 * 
 * @author Thilo Rauch (07.07.2012)
 */
public abstract class AbstractReviewDataReceiver implements IReviewDataReceiver {

    /**
     * Last review set provided by the extension point
     */
    private ReviewSet reviewData;
    /**
     * Current {@link AbstractReviewDataView} instance
     */
    private AbstractReviewDataView view;

    /**
     * Constructor which has be be invoked by extending classes
     * @author Thilo Rauch (13.07.2012)
     */
    public AbstractReviewDataReceiver() {
        // XXX remove later
        System.out.println("DataReceiver created");
        Object instanceObj = getInstance(getReviewDataViewClass());
        if (instanceObj != null) {
            bindViewOn((AbstractReviewDataView) instanceObj, this);
        }
    }

    /* (non-Javadoc)
     * @see org.agilereview.core.external.definition.IReviewDataReceiver#setReviewData(org.agilereview.core.external.storage.ReviewSet)
     * @author Thilo Rauch (07.07.2012)
     */
    @Override
    public final void setReviewData(ReviewSet reviews) {
        // XXX remove later
        System.out.println("setReviewData called with " + reviews);
        // First set reviews
        reviewData = reviews;

        // Now think about binding to View
        if (reviewData == null) {
            if (view != null) {
                view.setInput(null);
            }
        } else {
            if (view != null) {
                view.setInput(transformData(reviewData));
            }
        }
    }

    /**
     * Binds the given view to an instance of the given class, if one can be retrieved by a static getInstance() method of that type
     * @param dataView View to bind to
     * @param receiverClass Class of which an instance should be bound to this receiver
     * @author Thilo Rauch (13.07.2012)
     */
    static final void bindViewOn(AbstractReviewDataView dataView, Class<? extends AbstractReviewDataReceiver> receiverClass) {
        Object instanceObj = getInstance(receiverClass);
        if (instanceObj != null) {
            // No check need, has to be of that type
            bindViewOn(dataView, (AbstractReviewDataReceiver) instanceObj);
        }
    }

/**
     * Method for the binding framework to find the view corresponding to this data receiver.
     * Should look like (with regard to sample):
     * <p><blockquote><pre>
     * {@code 
     *     @Override
     *     public Class<? extends AbstractReviewDataView> getReviewDataViewClass() {
     *          return MyView.class;
     *     }
     * }
     * </pre></blockquote></p>
     * @return Class of the corresponding view
     * @author Thilo Rauch (13.07.2012)
     */
    protected abstract Class<? extends AbstractReviewDataView> getReviewDataViewClass();

    /**
     * Override this method if you don't want a {@link ReviewSet} passed to you view. You can transform the data however you like before it s passed
     * to the view.
     * @param rawData The {@link ReviewSet} passed by the extension point
     * @return The transformed data which should be input to the view
     * @author Thilo Rauch (13.07.2012)
     */
    protected Object transformData(ReviewSet rawData) {
        return rawData;
    }

    /**
     * Gets an instance of the specified class if the class has an getInstance() method which actually returns a instance of that class.
     * @param c Class a instance is wanted for
     * @return A instance of the passed in class or {@code null} if no method getInstance() exists that returns an instance of the passed type.
     * @author Thilo Rauch (13.07.2012)
     */
    private static Object getInstance(Class<?> c) {
        Method getInstance;
        try {
            getInstance = c.getMethod("getInstance");
            Object instance = getInstance.invoke(null);
            if (c.isInstance(instance)) {
                return instance;
            }
        } catch (Exception e) {
            // XXX Do some logging here
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Binds the given view to the given data receiver
     * @param dataView view to bind
     * @param receiver data receiver to bind
     * @author Thilo Rauch (13.07.2012)
     */
    private static void bindViewOn(AbstractReviewDataView dataView, AbstractReviewDataReceiver receiver) {
        receiver.view = dataView;
        if (receiver.reviewData != null) {
            receiver.view.setInput(receiver.transformData(receiver.reviewData));
        } else {
            receiver.view.setInput(null);
        }
    }

    // XXX wofür?
    public void disconnect() {
        if (view != null) {
            view.setInput(null);
        }
    }
}
