/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.test.controller.extension;

import org.agilereview.core.controller.extension.EditorParserController;
import org.agilereview.core.controller.extension.ExtensionControllerFactory;
import org.agilereview.core.controller.extension.ExtensionControllerFactory.ExtensionPoint;
import org.agilereview.test.mock.editorparser.external.EditorParserMock;
import org.eclipse.ui.editors.text.TextEditor;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link EditorParserController}
 * @author Malte Brunnlieb (15.07.2012)
 */
public class EditorParserControllerTest {
    
    /**
     * Test for Method {@link EditorParserController#getParser(Class)}
     * @author Malte Brunnlieb (15.07.2012)
     */
    @Test
    public void getParserTest() {
        EditorParserController controller = (EditorParserController) ExtensionControllerFactory.getExtensionController(ExtensionPoint.EditorParser);
        EditorParserMock.waitUnitCreation();
        Assert.assertNotNull(controller.getParser(TextEditor.class));
        Assert.assertNull(controller.getParser(getClass()));
    }
}
