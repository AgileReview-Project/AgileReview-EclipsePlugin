/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.fileparser.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.StringWriter;

import org.agilereview.core.external.definition.IFileParser;

/**
 * 
 * @author Malte Brunnlieb (18.05.2014)
 */
public class FileParser implements IFileParser {
    
    public static String newline = System.getProperty("line.separator");
    
    /* (non-Javadoc)
     * @see org.agilereview.core.external.definition.IFileParser#addTags(java.io.File, int, int, java.lang.String[])
     * @author Malte Brunnlieb (18.05.2014)
     */
    public void addTags(File file, int startLine, int endLine, String[] multiLineCommentTags) {
        try {
            // input the file content to the String "input"
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            StringWriter contents = new StringWriter();
            boolean isMultiLineComment = (startLine != endLine);
            
            int lineNr = 0;
            while ((line = reader.readLine()) != null) {
                lineNr++;
                if (lineNr == startLine) {
                    if (isMultiLineComment) {
                        if (lineNr != 0) {
                            contents.append(newline);
                        }
                        contents.append(line);
                    }
                }
            }
            
            // write the new String with the replaced line OVER the same file
            FileOutputStream output = new FileOutputStream(file);
            output.write(contents.toString().getBytes());
            
        } catch (Exception e) {
            System.out.println("Problem reading file.");
        }
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.core.external.definition.IFileParser#removeTags(java.io.File, java.lang.String, java.lang.String[])
     * @author Malte Brunnlieb (18.05.2014)
     */
    public void removeTags(File file, String tagId, String[] multiLineCommentTags) {
        
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.core.external.definition.IFileParser#clearAllTags(java.io.File, java.lang.String[])
     * @author Malte Brunnlieb (18.05.2014)
     */
    public void clearAllTags(File file, String[] multiLineCommentTags) {
        
    }
    
}
