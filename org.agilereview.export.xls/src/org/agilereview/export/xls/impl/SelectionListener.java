/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.export.xls.impl;


/**
 * 
 * @author Malte Brunnlieb (10.06.2014)
 */
public class SelectionListener {
    
    /**
     * Will be called by the {@link ViewControl} when the selection was changed and changes
     * @param event
     * @see de.tukl.cs.softech.agilereview.views.ViewControl#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
     */
    //    public void selectionChanged(SelectionChangedEvent event) {
    //        if (event.getSelection() instanceof IStructuredSelection) {
    //            IStructuredSelection sel = (IStructuredSelection) event.getSelection();
    //            Iterator<?> it = sel.iterator();
    //            boolean containsClosedReview = false, firstReviewIsActive = false, firstIteration = true;
    //            while (it.hasNext() && !containsClosedReview) {
    //                Object o = it.next();
    //                if (o instanceof MultipleReviewWrapper) {
    //                    if (!((MultipleReviewWrapper) o).isOpen()) {
    //                        containsClosedReview = true;
    //                    }
    //                    if (firstIteration) {
    //                        if (PropertiesManager.getPreferences().getString(PropertiesManager.EXTERNAL_KEYS.ACTIVE_REVIEW).equals(
    //                                ((MultipleReviewWrapper) o).getReviewId())) {
    //                            firstReviewIsActive = true;
    //                        }
    //                    }
    //                }
    //                firstIteration = false;
    //            }
    //            ISourceProviderService isps = (ISourceProviderService) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getService(
    //                    ISourceProviderService.class);
    //            SourceProvider sp1 = (SourceProvider) isps.getSourceProvider(SourceProvider.CONTAINS_CLOSED_REVIEW);
    //            sp1.setVariable(SourceProvider.CONTAINS_CLOSED_REVIEW, containsClosedReview);
    //            SourceProvider sp2 = (SourceProvider) isps.getSourceProvider(SourceProvider.IS_ACTIVE_REVIEW);
    //            sp2.setVariable(SourceProvider.IS_ACTIVE_REVIEW, firstReviewIsActive);
    //        }
    //    }
}
