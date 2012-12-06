package org.agilereview.editorparser.itexteditor.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.agilereview.common.exception.ExceptionHandler;
import org.agilereview.core.external.storage.Comment;
import org.agilereview.editorparser.itexteditor.Activator;
import org.agilereview.editorparser.itexteditor.data.ComparablePosition;
import org.agilereview.editorparser.itexteditor.exception.NoDocumentFoundException;
import org.agilereview.editorparser.itexteditor.prefs.AuthorReservationPreferences;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * The AnnotationParser analyzes the document of the given editor and provides a mapping of comment tags and their {@link Position}s
 */
public class TagParser {
    
    /**
     * Sign which separates the comment id from all meta information
     */
    private static String keySeparator = "|";
    /**
     * Core Regular Expression to find the core tag structure
     */
    public static String RAW_TAG_REGEX = "-?(\\?)?" + Pattern.quote(keySeparator) + "(.+?)" + Pattern.quote(keySeparator) + "(\\?)?(-)?";
    /**
     * Commenting tags for this instance
     */
    private String[] tags;
    /**
     * Regular Expression used by this instance
     */
    private final String tagRegex;
    /**
     * Pattern used by this instance
     */
    private final Pattern tagPattern;
    /**
     * This map lists every comment tag found in the document with its {@link Position}
     */
    private final TreeMap<String, Position> idPositionMap = new TreeMap<String, Position>();
    /**
     * Position map of all tags
     */
    private final TreeMap<String, Position[]> idTagPositions = new TreeMap<String, Position[]>();
    /**
     * The currently displayed comments
     */
    private final TreeSet<String> displayedComments = new TreeSet<String>();
    /**
     * Document which provides the contents for this instance
     */
    private IDocument document;
    /**
     * The document of this parser
     */
    private final ITextEditor editor;
    /**
     * Annotation model for this parser
     */
    private final AnnotationManager annotationModel;
    
    /**
     * Creates a new instance of AnnotationParser with the given input
     * @param editor the editor which contents should be analyzed
     * @param commentTags a two dimensional array which holds the comment begin tag and end tag for this parser instance
     * @throws NoDocumentFoundException will be thrown, if the file type which this editor represents is not supported
     * @throws CoreException
     */
    TagParser(ITextEditor editor, String[] commentTags) throws NoDocumentFoundException, CoreException {
        
        tagRegex = Pattern.quote(commentTags[0]) + RAW_TAG_REGEX + Pattern.quote(commentTags[1]);
        tagPattern = Pattern.compile(tagRegex);
        tags = commentTags;
        
        this.editor = editor;
        
        if (editor.getDocumentProvider() != null) {
            this.document = editor.getDocumentProvider().getDocument(editor.getEditorInput());
            if (this.document == null) { throw new NoDocumentFoundException(); }
        } else {
            throw new NoDocumentFoundException();
        }
        
        this.annotationModel = new AnnotationManager(editor);
        parseInput();
    }
    
    /**
     * Parses all comment tags and saves them with their {@link Position}
     * @throws CoreException
     */
    private void parseInput() throws CoreException {
        this.document = editor.getDocumentProvider().getDocument(editor.getEditorInput());
        
        editor.getDocumentProvider().saveDocument(null, editor.getEditorInput(), document, true);
        
        idPositionMap.clear();
        idTagPositions.clear();
        HashSet<String> corruptedCommentKeys = new HashSet<String>();
        IRegion r;
        int startOffset = 0;
        try {
            Matcher matcher;
            FindReplaceDocumentAdapter fra = new FindReplaceDocumentAdapter(document);
            while ((r = fra.find(startOffset, tagRegex, true, false, false, true)) != null) {
                boolean tagDeleted = false;
                matcher = tagPattern.matcher(document.get(r.getOffset(), r.getLength()));
                if (matcher.matches()) {
                    tagDeleted = parseStartTag(corruptedCommentKeys, matcher, r);
                    // rewrite location if the line should be removed after comment deletion
                    if (!tagDeleted) {
                        tagDeleted = parseEndTag(corruptedCommentKeys, matcher, r);
                    }
                }
                
                // if a tag was deleted, search from begin of the deleted tag again
                if (tagDeleted) {
                    startOffset = r.getOffset();
                } else {
                    // -1 in oder to ensure that the startOffset will not be greater then the document length
                    startOffset = r.getOffset() + r.getLength() - 1;
                }
            }
            
            // check for begin tags without end tags
            boolean curruptedBeginTagExists = false;
            TreeSet<Position> positionsToDelete = new TreeSet<Position>();
            for (String key : idTagPositions.keySet()) {
                Position[] ps = idTagPositions.get(key);
                if (ps[1] == null) {
                    // corrupt: begin tag without end tag --> deleting
                    corruptedCommentKeys.add(key);
                    positionsToDelete.add(new ComparablePosition(ps[0]));
                    curruptedBeginTagExists = true;
                }
            }
            
            if (curruptedBeginTagExists) {
                // delete all corrupted begin tags in descending order
                Iterator<Position> it = positionsToDelete.descendingIterator();
                while (it.hasNext()) {
                    Position tmp = it.next();
                    document.replace(tmp.getOffset(), tmp.getLength(), "");
                }
                // delete corrupted annotations
                this.annotationModel.deleteAnnotations(corruptedCommentKeys);
                // parse the file another time to get the correct positions for the tags
                parseInput();
            }
            
        } catch (BadLocationException e) {
            if (startOffset != 0) { // otherwise file out of sync or other reasons, so eclipse cannot open file till refresh --> suppress Exception
                ExceptionHandler.logAndNotifyUser("BadLocationException occurs while parsing the editor: " + editor.getTitle()
                        + "\nPlease consider to write a bug report if this problem holds on.", e, Activator.PLUGIN_ID);
            }
        }
        
        // Save the current document to save the tags
        // TODO only save document if there are changes made by the parser
        editor.getDocumentProvider().saveDocument(null, editor.getEditorInput(), document, true);
        
        // update annotations in order to recognize moved tags
        TreeMap<String, Position> annotationsToUpdate = new TreeMap<String, Position>();
        for (String key : displayedComments) {
            
            if (idPositionMap.get(key) != null) {
                annotationsToUpdate.put(key, idPositionMap.get(key));
            }
        }
        annotationModel.updateAnnotations(annotationsToUpdate);
    }
    
    /**
     * Parses the given region matched by the given Matcher against AgileReview end tag behavior. If the tag is corrupted, it will be added to the
     * corruptedCommentKeys set passed. Otherwise it will be added as valid tag to the tagPositionMap.
     * @param corruptedCommentKeys set of corrupted comment keys
     * @param matcher for the tagRegex on the given region
     * @param tagRegion region of the tag occurrence
     * @return true, if the tag was deleted<br>false, otherwise
     * @throws BadLocationException
     * @author Malte Brunnlieb (08.09.2012)
     */
    private boolean parseStartTag(HashSet<String> corruptedCommentKeys, Matcher matcher, IRegion tagRegion) throws BadLocationException {
        boolean tagDeleted = false;
        Position[] tagPositions;
        if (matcher.group(1).equals("?")) {
            String key = matcher.group(2).trim();
            tagPositions = idTagPositions.get(key);
            // begin tag
            if (tagPositions != null) {
                // same begin tag already exists --> deleting
                corruptedCommentKeys.add(key);
                document.replace(tagRegion.getOffset(), tagRegion.getLength(), "");
                tagDeleted = true;
            } else {
                idPositionMap.put(key, new Position(document.getLineOffset(document.getLineOfOffset(tagRegion.getOffset()))));
            }
            rewriteTagLocationForLineAdaption(matcher, tagRegion, true);
        }
        return tagDeleted;
    }
    
    /**
     * Parses the given region matched by the given Matcher against AgileReview end tag behavior. If the tag is corrupted, it will be added to the
     * corruptedCommentKeys set passed. Otherwise it will be added as valid tag to the tagPositionMap.
     * @param corruptedCommentKeys set of corrupted comment keys
     * @param matcher for the tagRegex on the given region
     * @param tagRegion region of the tag occurrence
     * @return true, if the tag was deleted<br>false, otherwise
     * @throws BadLocationException
     * @author Malte Brunnlieb (08.09.2012)
     */
    private boolean parseEndTag(HashSet<String> corruptedCommentKeys, Matcher matcher, IRegion tagRegion) throws BadLocationException {
        boolean tagDeleted = false;
        Position[] tagPositions;
        if (matcher.group(3).equals("?")) {
            String key = matcher.group(2).trim();
            tagPositions = idTagPositions.get(key);
            // end tag
            if (tagPositions != null) {
                if (tagPositions[1] != null) {
                    // same end tag already exists --> deleting
                    corruptedCommentKeys.add(key);
                    document.replace(tagRegion.getOffset(), tagRegion.getLength(), "");
                    tagDeleted = true;
                } else {
                    // end tag not set
                    Position tmp = idPositionMap.get(key);
                    int line = document.getLineOfOffset(tagRegion.getOffset());
                    tmp.setLength(document.getLineOffset(line) - tmp.getOffset() + document.getLineLength(line));
                    idPositionMap.put(key, tmp);
                    
                    Position[] tmp2 = idTagPositions.get(key);
                    tmp2[1] = new Position(tagRegion.getOffset(), tagRegion.getLength());
                    idTagPositions.put(key, tmp2);
                }
            } else {
                // end tag without begin tag --> deleting
                corruptedCommentKeys.add(key);
                document.replace(tagRegion.getOffset(), tagRegion.getLength(), "");
                tagDeleted = true;
            }
            rewriteTagLocationForLineAdaption(matcher, tagRegion, false);
        }
        return tagDeleted;
    }
    
    /**
     * If the line was added by AgileReview, this function will rewrite the location of the current tag such that the line delimiter will also be
     * removed.
     * @param matcher Matcher matching the tagRegex against the tag in the current line
     * @param tagRegion Region the tag occurs in
     * @param startLine states whether the startLine or the endLine will be adapted
     * @throws BadLocationException
     * @author Malte Brunnlieb (08.09.2012)
     */
    private void rewriteTagLocationForLineAdaption(Matcher matcher, IRegion tagRegion, boolean startLine) throws BadLocationException {
        String key = matcher.group(2).trim();
        if ("-".equals(matcher.group(4))) {
            // set the position such that the line break beforehand will be removed too when replacing this position with the empty string
            int currLine = document.getLineOfOffset(tagRegion.getOffset());
            String lineToDelete = document.get(document.getLineOffset(currLine), document.getLineLength(currLine)
                    - document.getLineDelimiter(currLine).length());
            
            // if there is at least one tag which is not alone in this line, do not delete the whole line!
            Matcher lineMatcher = Pattern.compile("(.*)" + tagRegex + "(.*)").matcher(lineToDelete);
            if (lineMatcher.matches() && lineMatcher.group(1).trim().isEmpty() && lineMatcher.group(6).trim().isEmpty()) {
                setTagPosition(startLine, key, new Position(document.getLineOffset(currLine), document.getLineLength(currLine)));
                return;
            }
        }
        setTagPosition(startLine, key, new Position(tagRegion.getOffset(), tagRegion.getLength()));
    }
    
    /**
     * Sets the tag position newPos either for the start tag or the end tag
     * @param startTag determines whether the start tag should be set or the end tag
     * @param key of the comment the tags are for
     * @param newPos new {@link Position} to be set
     * @author Malte Brunnlieb (09.09.2012)
     */
    private void setTagPosition(boolean startTag, String key, Position newPos) {
        Position[] oldPos = idTagPositions.get(key);
        if (oldPos == null) {
            oldPos = new Position[2];
        }
        oldPos[startTag ? 0 : 1] = newPos;
        idTagPositions.put(key, oldPos);
    }
    
    /**
     * Adds comment tags with the given id to the current editor document selection
     * @param tagId for the comment which should be added
     * @throws BadLocationException
     * @throws CoreException
     * @author Malte Brunnlieb (06.12.2012)
     */
    public void addTagsInDocument(String tagId) throws BadLocationException, CoreException {
        ISelection selection = editor.getSelectionProvider().getSelection();
        if (selection instanceof ITextSelection) {
            int selStartLine = ((ITextSelection) selection).getStartLine();
            int selEndLine = ((ITextSelection) selection).getEndLine();
            addTagsInDocument(tagId, selStartLine, selEndLine);
        }
    }
    
    /**
     * Adds the Comment tags for the given comment in the currently opened file at the currently selected place
     * @param tagId Comment id for which the tags should be inserted
     * @param selStartLine of the position where the comment should be inserted
     * @param selEndLine of the position where the comment should be inserted
     * @throws BadLocationException Thrown if the selected location is not in the document (Should theoretically never happen)
     * @throws CoreException
     */
    public void addTagsInDocument(String tagId, int selStartLine, int selEndLine) throws BadLocationException, CoreException {
        boolean startLineInserted = false, endLineInserted = false;;
        int origSelStartLine = selStartLine;
        String commentTag = keySeparator + tagId + keySeparator;
        boolean[] significantlyChanged = new boolean[] { false, false };
        
        // check if selection needs to be adapted
        int[] newLines = computeSelectionAdapations(selStartLine, selEndLine);
        if (newLines[0] != -1 || newLines[1] != -1) {
            // adapt starting line if necessary
            if (newLines[0] != -1) {
                int newStartLineOffset = document.getLineOffset(newLines[0]);
                int newStartLineLength = document.getLineLength(newLines[0]);
                
                // insert new line if code is in front of javadoc / multi line comments
                if (!document.get(newStartLineOffset, newStartLineLength).trim().isEmpty()) {
                    document.replace(newStartLineOffset + newStartLineLength, 0, System.getProperty("line.separator"));
                    selStartLine = newLines[0] + 1;
                    startLineInserted = true;
                } else {
                    selStartLine = newLines[0];
                }
                
                // only inform the user about these adaptations if he did not select the whole javaDoc
                if (origSelStartLine - 1 != selStartLine) {
                    significantlyChanged[0] = true;
                }
            }
            
            // adapt ending line if necessary
            // add a new line if a line was inserted before
            if (newLines[1] != -1) {
                selEndLine = newLines[1] + (startLineInserted ? 1 : 0);
                significantlyChanged[1] = true;
            } else {
                selEndLine += (startLineInserted ? 1 : 0);
            }
        }
        
        // add new line if start line is last line of javaDoc
        int[] adaptionLines = checkForCodeComment(selStartLine - 1, tags);
        if (adaptionLines[1] != -1 && lineContains(adaptionLines[0] + 1, "/**")) {
            int newStartLineOffset = document.getLineOffset(selStartLine + 1);
            int newStartLineLength = document.getLineLength(selStartLine + 1);
            if (!document.get(newStartLineOffset, newStartLineLength).trim().isEmpty()) {
                document.replace(newStartLineOffset, 0, System.getProperty("line.separator"));
                selStartLine++;
                selEndLine++;
                startLineInserted = true;
                significantlyChanged[0] = true;
            }
        }
        
        // add new line if end line is last line of javaDoc
        adaptionLines = checkForCodeComment(selEndLine - 1, tags);
        if (adaptionLines[1] != -1 && lineContains(adaptionLines[0] + 1, "/**")) {
            int newEndLineOffset = document.getLineOffset(selEndLine + 1);
            int newEndLineLength = document.getLineLength(selEndLine + 1);
            if (!document.get(newEndLineOffset, newEndLineLength).trim().isEmpty()) {
                document.replace(newEndLineOffset, 0, System.getProperty("line.separator"));
                selEndLine++;
                endLineInserted = true;
                significantlyChanged[1] = true;
            }
        }
        
        if (significantlyChanged[0] || significantlyChanged[1]) {
            // inform user
            ExceptionHandler
                    .warnUser("Inserting a AgileReview comment at the current selection will destroy one ore more code comments. "
                            + "AgileReview will adapt the current selection to avoid this.\nIf it is necessary a new line will be inserted above the selection which will be removed on comment deletion.");
            
        }
        
        // compute new selection
        int offset = document.getLineOffset(selStartLine);
        int length = document.getLineOffset(selEndLine) - document.getLineOffset(selStartLine) + document.getLineLength(selEndLine);
        // set new selection
        editor.getSelectionProvider().setSelection(new TextSelection(offset, length));
        
        if (selStartLine == selEndLine) {
            // Only one line is selected
            String lineDelimiter = document.getLineDelimiter(selStartLine);
            int lineDelimiterLength = 0;
            if (lineDelimiter != null) {
                lineDelimiterLength = lineDelimiter.length();
            }
            
            int insertOffset = document.getLineOffset(selStartLine) + document.getLineLength(selStartLine) - lineDelimiterLength;
            
            // Write tag -> get start+end-tag for current file-ending, insert into file
            document.replace(insertOffset, 0, tags[0] + "-?" + commentTag + "?" + (startLineInserted || endLineInserted ? "-" : "") + tags[1]);
            
            // document.getLineLength(selStartLine)-lineDelimiterLength);
        } else {
            // Calculate insert position for start line
            String lineDelimiter = document.getLineDelimiter(selStartLine);
            int lineDelimiterLength = 0;
            if (lineDelimiter != null) {
                lineDelimiterLength = lineDelimiter.length();
            }
            int insertStartOffset = document.getLineOffset(selStartLine) + document.getLineLength(selStartLine) - lineDelimiterLength;
            
            // Calculate insert position for end line
            lineDelimiter = document.getLineDelimiter(selEndLine);
            lineDelimiterLength = 0;
            if (lineDelimiter != null) {
                lineDelimiterLength = lineDelimiter.length();
            }
            int insertEndOffset = document.getLineOffset(selEndLine) + document.getLineLength(selEndLine) - lineDelimiterLength;
            
            // Write tags -> get tags for current file-ending, insert second tag, insert first tag
            document.replace(insertEndOffset, 0, tags[0] + "-" + commentTag + "?" + (endLineInserted ? "-" : "") + tags[1]);
            document.replace(insertStartOffset, 0, tags[0] + "-?" + commentTag + (startLineInserted ? "-" : "") + tags[1]);
        }
        
        // Save, so Eclipse save actions can take place before parsing  
        editor.getDocumentProvider().saveDocument(null, editor.getEditorInput(), document, true);
        parseInput();
        
        if (isAgileReviewPerspectiveOpen()) {//TODO only reserve if this comment should also be displayed
            Comment c = DataManager.getInstance().getComment(tagId);
            if (c != null) {
                new AuthorReservationPreferences().addReservation(c.getAuthor());
            }
        }
    }
    
    /**
     * Checks whether the current perspective is the AgileReview perspective
     * @return true, if the current opened perspective is the AgileReview perspective,<br>false, otherwise
     * @author Malte Brunnlieb (22.11.2012)
     */
    private boolean isAgileReviewPerspectiveOpen() {
        String perspectiveID = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getPerspective().getId();
        return "org.agilereview.perspective".equals(perspectiveID); //TODO should be configurable or compared in a more extensible way
    }
    
    /**
     * Checks whether adding an AgileReview comment at the current selection would destroy a code comment and computes adapted line numbers to avoid
     * destruction of code comments.
     * @param startLine the current startLine of the selection
     * @param endLine the current endLine of the selection
     * @return and array containing the new start (position 0) and endline (position 1). If not nothing is to be changed the content is -1 at position
     *         0/1.
     * @throws BadLocationException
     */
    private int[] computeSelectionAdapations(int startLine, int endLine) throws BadLocationException {
        int[] result = { -1, -1 };
        int[] startLineAdaptions = checkForCodeComment(startLine, tags);
        int[] endLineAdaptions = checkForCodeComment(endLine, tags);
        
        // check if inserting a AgileReview comment at selected code region destroys a code comment
        if (startLineAdaptions[0] != -1 && startLineAdaptions[1] != -1 && startLineAdaptions[0] != startLine) {
            result[0] = startLineAdaptions[0];
        }
        if (endLineAdaptions[0] != -1 && endLineAdaptions[1] != -1 && endLineAdaptions[1] != endLine) {
            result[1] = endLineAdaptions[1];
        }
        
        return result;
    }
    
    /**
     * Checks whether the given line is within a code comment. If this holds the code comments start and endline is returned, else {-1, -1}.
     * @param line the line to check
     * @param tags the start and endtag of code comments
     * @return [-1, -1] if line is not within a code comment, else [startline, endline] of the code comment
     * @throws BadLocationException
     */
    private int[] checkForCodeComment(int line, String[] tags) throws BadLocationException {
        // TODO: optimize the search for tags
        
        int openTagLine = -1;
        int closeTagLine = -1;
        
        // check for opening non-AgileReview comment tags before the line
        for (int i = 0; i <= line; i++) {
            if (lineContains(i, tags[0])) {
                openTagLine = i;
            }
        }
        
        // check for according closing non-AgileReview comment tag
        if (openTagLine > -1) {
            for (int i = openTagLine; i < document.getNumberOfLines(); i++) {
                if (lineContains(i, tags[1])) {
                    closeTagLine = i;
                    break;
                }
            }
        }
        
        // finally return the results if a comment was found
        int[] result = { -1, -1 };
        if (openTagLine <= line && line <= closeTagLine) {
            // TODO: not checked if line right before starting line of code comment contains also a code comment...
            result[0] = openTagLine - 1;
            if (!(closeTagLine == line)) {
                result[1] = closeTagLine;
            }
        }
        return result;
    }
    
    /**
     * Checks whether the line identified by the lineNumber contains the given string. This function erases all AgileReview related comment tags
     * before searching for the given string.
     * @param lineNumber line number of the document
     * @param string string to be searched for
     * @return true, if the string is contained in the given line ignoring AgileReview tags,<br> false otherwise
     * @throws BadLocationException if the given line could not be found in the current document
     * @author Malte Brunnlieb (08.09.2012)
     */
    private boolean lineContains(int lineNumber, String string) throws BadLocationException {
        String lineContent = document.get(document.getLineOffset(lineNumber), document.getLineLength(lineNumber)).trim();
        lineContent = lineContent.replaceAll(Pattern.quote(string) + RAW_TAG_REGEX + Pattern.quote(string), "");
        return lineContent.contains(string);
    }
    
    /**
     * Removes all tags in the parsers document related to the given tagId
     * @param tagId comment id for which the tags should be removed
     * @throws BadLocationException will be thrown if the cached tag locations are out of bounds from the current document
     * @throws CoreException will be thrown during reparsing the document after tag deletion
     * @author Malte Brunnlieb (26.11.2012)
     */
    public void removeTagsInDocument(String tagId) throws BadLocationException, CoreException {
        TreeSet<Position> tagPositions = new TreeSet<Position>();
        
        Position[] ps = idTagPositions.get(tagId);
        if (ps != null) {
            ArrayList<ComparablePosition> cp = new ArrayList<ComparablePosition>();
            for (int i = 0; i < ps.length; i++) {
                cp.add(new ComparablePosition(ps[i]));
            }
            tagPositions.addAll(cp);
        }
        
        this.idTagPositions.keySet().remove(tagId);
        this.idPositionMap.keySet().remove(tagId);
        
        Iterator<Position> it = tagPositions.descendingIterator();
        while (it.hasNext()) {
            Position tmp = it.next();
            document.replace(tmp.getOffset(), tmp.getLength(), "");
        }
        
        parseInput();
    }
    
    //    public void reload() throws CoreException {
    //        parseInput();
    //    }
    
    //    public void revealCommentLocation(String commentID) throws BadLocationException {
    //        if (this.idPositionMap.get(commentID) != null) {
    //            editor.selectAndReveal(this.idPositionMap.get(commentID).offset, 0);
    //        } else {
    //            throw new BadLocationException();
    //        }
    //    }
    
    /**
     * Returns all comments which are overlapping with the given {@link Position}
     * @param p position
     * @return all comments which are overlapping with the given {@link Position}
     */
    public String[] getCommentsByPosition(Position p) {
        return this.annotationModel.getCommentsByPosition(p);
    }
    
    /**
     * Returns a map of all observed comments in the attached document to its positions
     * @return a {@link Map} of comment IDs which are observed during parsing the document to the position of the comments
     * @author Malte Brunnlieb (06.12.2012)
     */
    public Map<String, Position> getObservedComments() {
        return new HashMap<String, Position>(idPositionMap);
    }
    
    /**
     * Computes the next position from the given one on where a comment is located.
     * @param current The current position
     * @return The next position or<br> null if there is no such position.
     */
    public Position getNextCommentsPosition(Position current) {
        Position position;
        TreeSet<ComparablePosition> positions = new TreeSet<ComparablePosition>();
        for (String key : displayedComments) {
            position = idPositionMap.get(key);
            positions.add(new ComparablePosition(position));
        }
        return positions.higher(new ComparablePosition(current));
    }
    
    /**
     * Returns the position of the annotation for the given tag
     * @param tagId tag id of the tag whose position should be returned
     * @return a {@link Position} on which any annotation for the tag can be performed
     * @author Malte Brunnlieb (27.11.2012)
     */
    public Position getPosition(String tagId) {
        return idPositionMap.get(tagId);
    }
    
    //    public void relocateComment(Comment comment, boolean display) throws BadLocationException {
    //        ISelection selection = editor.getSelectionProvider().getSelection();
    //        if (selection instanceof ITextSelection) {
    //            int selStartLine = ((ITextSelection) selection).getStartLine();
    //            int selEndLine = ((ITextSelection) selection).getEndLine();
    //            removeCommentTags(comment);
    //            addTagsInDocument(comment, display, selStartLine, selEndLine);
    //        }
    //    }
}