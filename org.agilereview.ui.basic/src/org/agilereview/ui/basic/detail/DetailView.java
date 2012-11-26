/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.detail;

import org.agilereview.core.external.storage.Comment;
import org.agilereview.ui.basic.external.reviewDataReceiver.AbstractReviewDataReceiver;
import org.agilereview.ui.basic.external.reviewDataReceiver.AbstractReviewDataView;
import org.eclipse.swt.widgets.Composite;

/**
 * 
 * @author Thilo Rauch (15.10.2012)
 */
public class DetailView extends AbstractReviewDataView {
    
    private static DetailView instance;
    
    private CommentDetail commentDetail;
    
    public DetailView() {
        super();
        instance = this;
    }
    
    public static DetailView getInstance() {
        return instance;
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.ui.basic.external.reviewDataReceiver.AbstractReviewDataView#getReviewDataReceiverClass()
     * @author Thilo Rauch (15.10.2012)
     */
    @Override
    protected Class<? extends AbstractReviewDataReceiver> getReviewDataReceiverClass() {
        return DetailDataReceiver.class;
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.ui.basic.external.reviewDataReceiver.AbstractReviewDataView#buildUI(org.eclipse.swt.widgets.Composite, java.lang.Object)
     * @author Thilo Rauch (15.10.2012)
     */
    @Override
    protected void buildUI(Composite parent, Object initalInput) {
        if (initalInput instanceof Comment) {
            commentDetail = new CommentDetail(parent, parent.getStyle());
            commentDetail.fillContents((Comment) initalInput);
        } else {
            clearParent();
        }
        
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.ui.basic.external.reviewDataReceiver.AbstractReviewDataView#refreshInput(java.lang.Object)
     * @author Thilo Rauch (15.10.2012)
     */
    @Override
    protected void refreshInput(Object reviewData) {
        if (commentDetail != null) {
            commentDetail.fillContents((Comment) reviewData);
        }
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     * @author Thilo Rauch (15.10.2012)
     */
    @Override
    public void setFocus() {
        if (commentDetail != null) {
            commentDetail.setFocus();
        }
    }
}
