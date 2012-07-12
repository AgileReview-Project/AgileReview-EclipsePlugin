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
 * 
 * @author Thilo Rauch (07.07.2012)
 */
public abstract class AbstractReviewDataReceiver implements IReviewDataReceiver {

    private ReviewSet reviewData;

    /**
     * Current {@link AbstractReviewDataView} instance
     */
    private AbstractReviewDataView view;

    /**
     * Object in order to synchronize model <-> table binding
     */
    //  private static Object syncObj = new Object(); // XXX All instance sync on same object --> think again

    // private static Map<Class<? extends AbstractReviewDataReceiver>, AbstractReviewDataView> viewMap = new HashMap<Class<? extends AbstractReviewDataReceiver>, AbstractReviewDataView>();

    public AbstractReviewDataReceiver() {
        // XXX remove later
        System.out.println("DataReceiver created");
        // synchronized (syncObj) {
        Object instanceObj = getInstance(getReviewDataViewClass());
        if (instanceObj != null) {
            bindViewOn((AbstractReviewDataView) instanceObj, this);
        }
        // }
    }

    public abstract Class<? extends AbstractReviewDataView> getReviewDataViewClass();

    //    /**
    //     * Binds the {@link AbstractReviewDataView} as the content receiver for persistence data
    //     * @param view {@link AbstractReviewDataView} which contains the viewer
    //     * @author Malte Brunnlieb (27.05.2012)
    //     */
    //    public static void bindView(AbstractReviewDataView dataView) {
    //        synchronized (syncObj) {
    //            AbstractReviewDataReceiver currentInstance = instanceMap.get(dataView.getReviewDataReceiverClass());
    //            if (currentInstance != null) {
    //                currentInstance.view = dataView;
    //                if (currentInstance.reviewData != null) {
    //                    currentInstance.view.dataReceiverChanged(true);
    //                } else {
    //                    currentInstance.view.dataReceiverChanged(false);
    //                }
    //            } else {
    //                // save for later
    //                viewMap.put(dataView.getReviewDataReceiverClass(), dataView);
    //            }
    //        }
    //    }

    private static void bindViewOn(AbstractReviewDataView dataView, AbstractReviewDataReceiver receiver) {
        receiver.view = dataView;
        if (receiver.reviewData != null) {
            // receiver.view.dataReceiverChanged(true);
            receiver.view.setInput(receiver.transformData(receiver.reviewData));
        } else {
            receiver.view.setInput(null);
            // receiver.view.dataReceiverChanged(null);
        }
    }

    public static void bindViewOn(AbstractReviewDataView dataView, Class<? extends AbstractReviewDataReceiver> receiverClass) {
        //synchronized (syncObj) {
        Object instanceObj = getInstance(receiverClass);
        if (instanceObj != null) {
            // No check need, has to be of that type
            bindViewOn(dataView, (AbstractReviewDataReceiver) instanceObj);
        }
        // }
    }

    private static Object getInstance(Class<?> c) {
        Method getInstance;
        Object instance = null;
        try {
            getInstance = c.getMethod("getInstance");
            instance = getInstance.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //        } catch (NoSuchMethodException e) {
        //            // TODO Auto-generated catch block
        //            e.printStackTrace();
        //        } catch (SecurityException e) {
        //            // TODO Auto-generated catch block
        //            e.printStackTrace();
        //        } catch (IllegalAccessException e) {
        //            // TODO Auto-generated catch block
        //            e.printStackTrace();
        //        } catch (IllegalArgumentException e) {
        //            // TODO Auto-generated catch block
        //            e.printStackTrace();
        //        } catch (InvocationTargetException e) {
        //            // TODO Auto-generated catch block
        //            e.printStackTrace();
        //        }
        return instance;
    }

    /* (non-Javadoc)
     * @see org.agilereview.core.external.definition.IReviewDataReceiver#setReviewData(org.agilereview.core.external.storage.ReviewSet)
     * @author Thilo Rauch (07.07.2012)
     */
    @Override
    public void setReviewData(ReviewSet reviews) {
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

    protected Object transformData(ReviewSet rawData) {
        return rawData;
    }

    public void disconnect() {
        if (view != null) {
            view.setInput(null);
        }
    }
}
