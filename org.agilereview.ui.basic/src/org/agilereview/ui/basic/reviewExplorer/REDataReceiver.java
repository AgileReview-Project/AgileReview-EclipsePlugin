/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.reviewExplorer;

import org.agilereview.core.external.storage.ReviewSet;
import org.agilereview.ui.basic.external.reviewDataReceiver.AbstractReviewDataReceiver;
import org.agilereview.ui.basic.external.reviewDataReceiver.AbstractReviewDataView;

/**
 * 
 * @author Thilo Rauch (07.07.2012)
 */
public class REDataReceiver extends AbstractReviewDataReceiver {

    private static REDataReceiver instance;

    public REDataReceiver() {
        super();
        instance = this;
    }

    public static AbstractReviewDataReceiver getInstance() {
        return instance;
    }

    @Override
    public Object transformData(ReviewSet rawData) {
        return super.transformData(rawData);
    }

    /* (non-Javadoc)
     * @see org.agilereview.ui.basic.external.reviewDataReceiver.AbstractReviewDataReceiver#getReviewDataViewClass()
     * @author Thilo Rauch (08.07.2012)
     */
    @Override
    public Class<? extends AbstractReviewDataView> getReviewDataViewClass() {
        return ReviewExplorerView.class;
    }

    //    /* (non-Javadoc)
    //     * @see org.agilereview.ui.basic.external.reviewDataReceiver.AbstractReviewDataReceiver#bindView(org.agilereview.ui.basic.external.reviewDataReceiver.AbstractReviewDataView)
    //     * @author Thilo Rauch (08.07.2012)
    //     */
    //    @Override
    //    public void bindView(AbstractReviewDataView view) {
    //
    //    }

}
