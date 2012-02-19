/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.test.external.storage;

import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Date;

import junit.framework.AssertionFailedError;

import org.agilereview.core.external.storage.Reply;
import org.eclipse.core.runtime.Assert;
import org.junit.Test;

/**
 * TestClass for {@link org.agilereview.core.external.storage.Reply}
 * @author Malte Brunnlieb (18.02.2012)
 */
public class ReplyTest {

    /**
     * Test method for {@link org.agilereview.core.external.storage.Reply#Reply(java.lang.String, java.util.Date, java.util.Date, java.lang.String)}.
     * @author Malte Brunnlieb (19.02.2012)
     */
    @Test
    public final void testReplyStringDateDateString() {
	Date d = Calendar.getInstance().getTime();
	Reply r = new Reply("TestAuthor", d, d, "TestDesc");

	Assert.isNotNull(r);

	// check consistency
	if (!r.getAuthor().equals("TestAuthor") || !r.getCreationDate().equals(d) || !r.getModificationDate().equals(d)
		|| !r.getText().equals("TestDesc")) {
	    throw new AssertionFailedError("Reply data not consistent:\n" + "TestAuthor==" + r.getAuthor() + "\n" + d.toString() + "=="
		    + r.getCreationDate().toString() + "\n" + d.toString() + "==" + r.getModificationDate().toString() + "\nTestDesc==" + r.getText());
	}
    }

    /**
     * Test method for {@link org.agilereview.core.external.storage.Reply#Reply()}.
     * @author Malte Brunnlieb (19.02.2012)
     */
    @Test
    public final void testReply() {
	Reply r = new Reply();

	Assert.isNotNull(r);

	// check consistency
	fail("Not yet implemented"); // TODO check for auto set of author
    }

    /**
     * Test method for {@link org.agilereview.core.external.storage.Reply#getAuthor()}.
     * @author Malte Brunnlieb (19.02.2012)
     */
    @Test
    public final void testGetAuthor() {
	// tested by constructor tests
    }

    /**
     * Test method for {@link org.agilereview.core.external.storage.Reply#getCreationDate()}.
     * @author Malte Brunnlieb (19.02.2012)
     */
    @Test
    public final void testGetCreationDate() {
	// tested by constructor tests
    }

    /**
     * Test method for {@link org.agilereview.core.external.storage.Reply#getModificationDate()}.
     * @author Malte Brunnlieb (19.02.2012)
     */
    @Test
    public final void testGetModificationDate() {
	// tested by constructor tests
    }

    /**
     * Test method for {@link org.agilereview.core.external.storage.Reply#getText()}.
     * @author Malte Brunnlieb (19.02.2012)
     */
    @Test
    public final void testGetText() {
	// tested by constructor tests
    }

    /**
     * Test method for {@link org.agilereview.core.external.storage.Reply#setText(java.lang.String)}.
     * @author Malte Brunnlieb (19.02.2012)
     */
    @Test
    public final void testSetText() {
	Reply r = new Reply();
	r.setText("TestText =29`´1°äüö");

	if (!r.getText().equals("TestText =29`´1°äüö")) {
	    throw new AssertionFailedError("Text could not be saved successfully:\nTestText =29`´1°äüö!=" + r.getText());
	}
    }
}
