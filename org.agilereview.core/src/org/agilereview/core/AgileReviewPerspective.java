package org.agilereview.core;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 *  This class is meant to serve as an example for how various contributions 
 *  are made to a perspective. Note that some of the extension point id's are
 *  referred to as API constants while others are hardcoded and may be subject 
 *  to change. 
 */
public class AgileReviewPerspective implements IPerspectiveFactory {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	public void createInitialLayout(IPageLayout factory) {}
}