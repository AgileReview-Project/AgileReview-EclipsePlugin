/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.external.definition;

import java.util.List;

import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Reply;
import org.agilereview.core.external.storage.Review;
import org.agilereview.core.external.storage.ReviewSet;

/**
 * Interface that has to be implemeted by AgileReview storage clients.
 * @author Peter Reuter (19.02.2012)
 */
public interface IStorageClient {
    
    /**
     * This method returns all {@link Review} objects currently loaded.
     * @return a {@link List} of {@link Review} objects wrapped by an {@link ReviewSet} transfer object
     * @author Malte Brunnlieb (22.03.2012)
     */
    public ReviewSet getAllReviews();
    
    /**
     * @return A unique identifier for this {@link Review} as it is required by the {@link IStorageClient}.
     * @author Peter Reuter (28.04.2012)
     */
    public String getNewReviewId();
    
    /**
     * @return A unique identifier for this {@link Comment} as it is required by the {@link IStorageClient}.
     * @param author author of the new comment (might be interesting for some id generation mechanisms)
     * @param review review the new comment is attached to (might be interesting for some id generation mechanisms)
     * @author Peter Reuter (28.04.2012)
     */
    public String getNewCommentId(String author, Review review);
    
    /**
     * @return A unique identifier for this {@link Reply} as it is required by the {@link IStorageClient}.
     * @param parent parent object the reply is attached to (currently possible is a comment or another reply)
     * @author Peter Reuter (28.04.2012)
     */
    public String getNewReplyId(Object parent);
    
}
