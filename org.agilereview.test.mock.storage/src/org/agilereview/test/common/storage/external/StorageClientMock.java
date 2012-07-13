/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.test.common.storage.external;

import org.agilereview.core.external.definition.IStorageClient;
import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Reply;
import org.agilereview.core.external.storage.Review;
import org.agilereview.core.external.storage.ReviewSet;

/**
 * This Mock should is implemented as a dummy {@link IStorageClient} which returns the values set by the provided methods
 * @author Malte Brunnlieb (27.05.2012)
 */
public class StorageClientMock implements IStorageClient {
    
    /**
     * Dummy object storage
     */
    private final ReviewSet reviews = new ReviewSet();
    /**
     * The instance created by the {@link IStorageClient} extension point
     */
    private static StorageClientMock instance;
    
    /**
     * Creates a new instance of the {@link StorageClientMock}
     * @author Malte Brunnlieb (28.05.2012)
     */
    public StorageClientMock() {
        instance = this;
    }
    
    /**
     * Returns the instance created by the {@link IStorageClient} extension point. If the instance is not available at the moment, the constructor
     * will block until it is.
     * @return the instance created by the {@link IStorageClient} extension point
     * @author Malte Brunnlieb (28.05.2012)
     */
    public static StorageClientMock getInstance() {
        while (instance == null) {
        }
        return instance;
    }
    
    /**
     * Sets the storage content which should be worked with
     * @param reviews a list of {@link Review}s
     * @author Malte Brunnlieb (27.05.2012)
     */
    public void setStorageContent(ReviewSet reviews) {
        this.reviews.clear();
        this.reviews.addAll(reviews);
    }
    
    /**
     * {@inheritDoc}
     * @see org.agilereview.core.external.definition.IStorageClient#getAllReviews()
     * @author Malte Brunnlieb (27.05.2012)
     */
    @Override
    public ReviewSet getAllReviews() {
        return reviews;
    }
    
    /**
     * {@inheritDoc}
     * @see org.agilereview.core.external.definition.IStorageClient#addReview(org.agilereview.core.external.storage.Review)
     * @author Malte Brunnlieb (27.05.2012)
     */
    @Override
    public void addReview(Review review) {
        reviews.add(review);
    }
    
    /**
     * Dummy implementation, returns always "newId"
     * @see org.agilereview.core.external.definition.IStorageClient#getNewId(org.agilereview.core.external.storage.Review)
     * @author Malte Brunnlieb (27.05.2012)
     */
    @Override
    public String getNewId(Review review) {
        return "newId";
    }
    
    /**
     * Dummy implementation, returns always "newId"
     * @see org.agilereview.core.external.definition.IStorageClient#getNewId(org.agilereview.core.external.storage.Comment)
     * @author Malte Brunnlieb (27.05.2012)
     */
    @Override
    public String getNewId(Comment comment) {
        return "newId";
    }
    
    /**
     * Dummy implementation, returns always "newId"
     * @see org.agilereview.core.external.definition.IStorageClient#getNewId(org.agilereview.core.external.storage.Reply)
     * @author Malte Brunnlieb (27.05.2012)
     */
    @Override
    public String getNewId(Reply reply) {
        return "newId";
    }
}
