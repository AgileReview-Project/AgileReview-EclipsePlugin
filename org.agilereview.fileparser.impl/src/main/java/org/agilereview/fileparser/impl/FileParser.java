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
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.agilereview.common.properties.ParserProperties;

/**
 * Implementation of a file parser which adds AgileReview comment tags to a file
 * @author Malte Brunnlieb (18.05.2014)
 */
public class FileParser {
    
    /**
     * New line character
     */
    public static String newline = System.getProperty("line.separator");
    
    public static File file;
    
    public static Pattern tagPattern;
    
    /**
     * 
     * @param file
     * @param multiLineCommentTags
     * @author Malte Brunnlieb (18.05.2014)
     */
    public FileParser(File file, String[] multiLineCommentTags) {
        this.file = file;
        tagPattern = Pattern
                .compile(Pattern.quote(multiLineCommentTags[0]) + ParserProperties.RAW_TAG_REGEX + Pattern.quote(multiLineCommentTags[1]));
    }
    
    /**
     * TODO JavaDoc
     * @param file
     * @param startLine
     * @param endLine
     * @param multiLineCommentTags
     * @author Malte Brunnlieb (18.05.2014)
     */
    public void addTags(int startLine, int endLine) {
        
        try (FileReader fileReader = new FileReader(file); BufferedReader reader = new BufferedReader(new FileReader(file));) {
            StringBuilder contents = new StringBuilder();
            boolean isMultiLineComment = (startLine != endLine);
            Matcher matcher;
            
            String line;
            int lineNr = 0;
            while ((line = reader.readLine()) != null) {
                lineNr++;
                matcher = tagPattern.matcher(line);
                if (lineNr == startLine) {
                    if (isMultiLineComment) {
                        
                    } else {
                        
                    }
                }
                if (lineNr != 0) {
                    contents.append(newline);
                }
                contents.append(line);
            }
            
            // write the new String with the replaced line OVER the same file
            FileOutputStream output = new FileOutputStream(file);
            output.write(contents.toString().getBytes());
            output.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    
    /**
     * TODO JavaDoc
     * @param file
     * @param tagId
     * @param multiLineCommentTags
     * @author Malte Brunnlieb (18.05.2014)
     */
    public void removeTags(File file, String tagId, String[] multiLineCommentTags) {
        
    }
    
    /**
     * TODO JavaDoc
     * @param file
     * @param multiLineCommentTags
     * @author Malte Brunnlieb (18.05.2014)
     */
    public void clearAllTags(File file, String[] multiLineCommentTags) {
        
    }
    
}
