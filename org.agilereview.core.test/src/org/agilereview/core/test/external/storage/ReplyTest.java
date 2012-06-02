/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.test.external.storage;

import java.util.Calendar;

import junit.framework.AssertionFailedError;

import org.agilereview.core.external.storage.Reply;
import org.eclipse.core.runtime.Assert;
import org.junit.Test;

/**
 * TestClass for {@link org.agilereview.core.external.storage.Reply}
 * @author Malte Brunnlieb (18.02.2012)
 * @author Peter Reuter (29.05.2012)
 */
public class ReplyTest {

	/**
	 * Test method for {@link Reply#Reply(String, String, Calendar, Calendar, String, Object)}.
	 * @author Malte Brunnlieb (19.02.2012)
	 * @author Peter Reuter (29.05.2012)
	 */
	@Test
	public final void testReplyStringStringCalendarCalendarStringObject() {
		Calendar d = Calendar.getInstance();
		Object o = new Object();
		Reply r = new Reply("id", "TestAuthor", d, d, "TestDesc", o);

		Assert.isNotNull(r);

		// check consistency
		if (!r.getId().equals("id") || !r.getAuthor().equals("TestAuthor") || !r.getCreationDate().equals(d) || !r.getModificationDate().equals(d)
				|| !r.getText().equals("TestDesc") || !r.getParent().equals(o)) {
			throw new AssertionFailedError("Reply data not consistent:\n" + "TestAuthor==" + r.getAuthor() + "\n" + d.toString() + "=="
					+ r.getCreationDate().toString() + "\n" + d.toString() + "==" + r.getModificationDate().toString() + "\nTestDesc==" + r.getText());
		}
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Reply#Reply(String, Object)}.
	 * @author Malte Brunnlieb (19.02.2012)
	 * @author Peter Reuter (29.05.2012)
	 */
	@Test
	public final void testReplyStringObject() {
		Object o = new Object();
		Reply r = new Reply("id", o);

		Assert.isNotNull(r);

		// check consistency
		if (!r.getId().equals("id") || !r.getParent().equals(o)) {
			throw new AssertionFailedError("Reply data not consistent!");
		}
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
	 * @author Peter Reuter (29.05.2012)
	 */
	@Test
	public final void testSetText() {
		Object o = new Object();
		Reply r = new Reply("id", o);
		r.setText("TestText =29`´1°äüö");

		if (!r.getText().equals("TestText =29`´1°äüö")) {
			throw new AssertionFailedError("Text could not be saved successfully:\nTestText =29`´1°äüö!=" + r.getText());
		}
	}
}
