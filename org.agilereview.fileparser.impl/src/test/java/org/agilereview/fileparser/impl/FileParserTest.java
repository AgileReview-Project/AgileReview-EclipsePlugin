/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.fileparser.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test-Klasse für den {@link FileParser}
 * @author Malte Brunnlieb (18.05.2014)
 */
public class FileParserTest {
    
    @Test
    public void testeAddTagsSingleLineComments() throws URISyntaxException, IOException {
        File testResource = new File(getClass().getResource("/org/agilereview/fileparser/impl/TestClass").toURI());
        File tmpFile = File.createTempFile("TestClass", "java");
        FileUtils.copyFile(testResource, tmpFile);
        
        //precondition
        Assert.assertEquals("        System.out.println(\"am\");", getLine(tmpFile, 23));
        
        FileParser parser = new FileParser(tmpFile, new String[] { "/*", "*/" });
        
        parser.addTags(24, 24);
        
    }
    
    private String getLine(File file, int lineNr) {
        try (FileReader fileReader = new FileReader(file); BufferedReader reader = new BufferedReader(new FileReader(file));) {
            String line = null;
            int currLineNr = 0;
            while ((line = reader.readLine()) != null) {
                currLineNr++;
                if (lineNr == currLineNr) return line;
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }
}
