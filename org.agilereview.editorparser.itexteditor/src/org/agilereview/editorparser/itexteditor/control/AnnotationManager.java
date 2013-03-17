package org.agilereview.editorparser.itexteditor.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.agilereview.core.external.preferences.AgileReviewPreferences;
import org.agilereview.core.external.storage.Comment;
import org.agilereview.editorparser.itexteditor.prefs.AuthorReservationPreferences;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * This class is used to draw and manage annotations for a given text editor
 */
public class AnnotationManager {
    
    /**
     * The color manager for author color management
     */
    private final ColorManager colorManager = new ColorManager();
    /**
     * The texteditor's annotation model
     */
    private final IAnnotationModelExtension annotationModel;
    /**
     * The annotations added by AgileReview to the editor's annotation model
     */
    private final HashMap<Comment, Annotation> annotationMap = new HashMap<Comment, Annotation>();
    
    /**
     * Creates a new AgileAnnotationModel
     * @param editor The text editor in which the annotations will be displayed
     */
    AnnotationManager(IEditorPart editor) {
        IEditorInput input = editor.getEditorInput();
        this.annotationModel = (IAnnotationModelExtension) ((ITextEditor) editor).getDocumentProvider().getAnnotationModel(input);
    }
    
    /**
     * Clears all annotations for the attached editor
     * @author Malte Brunnlieb (05.12.2012)
     */
    void clearAnnotations() {
        displayAnnotations(null);
    }
    
    /**
     * Displays the given positions as annotations in the provided editor. Therefore annotations which should not be displayed any more will be
     * removed and not yet drawn annotations will be added to the annotation model.
     * @param commentPositionMap a map of Positions which should be annotated and the comment keys correlated to the positions
     */
    void displayAnnotations(Map<Comment, Position> commentPositionMap) {
        if (commentPositionMap == null) commentPositionMap = new HashMap<Comment, Position>();
        
        //add annotations that are not already displayed
        Map<Annotation, Position> annotationsToAdd = new HashMap<Annotation, Position>();
        for (Comment comment : commentPositionMap.keySet()) {
            if (!annotationMap.containsKey(comment) && comment.isFilteredToBeShown()) {
                Annotation annotation = createNewAnnotation(comment);
                if (annotation != null) {
                    annotationsToAdd.put(annotation, commentPositionMap.get(comment));
                }
            }
        }
        //remove annotations that should not be displayed
        ArrayList<Annotation> annotationsToRemove = new ArrayList<Annotation>();
        ArrayList<Comment> commentsToDelete = new ArrayList<Comment>();
        for (Comment comment : annotationMap.keySet()) {
            if (!commentPositionMap.containsKey(comment) || !comment.isFilteredToBeShown()) {
                annotationsToRemove.add(annotationMap.get(comment));
                commentsToDelete.add(comment);
            }
        }
        annotationModel.replaceAnnotations(annotationsToRemove.toArray(new Annotation[0]), annotationsToAdd);
        annotationMap.keySet().removeAll(commentsToDelete);
    }
    
    /**
     * Updates the given comment annotations in the provided editor to the given positions
     * @param commentPositionMap a map of updated positions and the comment keys correlated to the positions
     */
    void updateAnnotations(Map<Comment, Position> commentPositionMap) {
        for (Comment comment : commentPositionMap.keySet()) {
            if (annotationMap.get(comment) != null) {
                annotationModel.modifyAnnotationPosition(annotationMap.get(comment), commentPositionMap.get(comment));
            }
        }
    }
    
    /**
     * Adds a new annotation at a given position p.
     * @param comment {@link Comment} for which this annotation holds
     * @param p The position to add the annotation on.
     */
    void addAnnotation(Comment comment, Position p) {
        if (!comment.isFilteredToBeShown()) return;
        Annotation annotation = createNewAnnotation(comment);
        if (annotation != null) {
            ((IAnnotationModel) this.annotationModel).addAnnotation(annotation, p);
        }
    }
    
    /**
     * Deletes all annotations correlating to the given comment keys
     * @param comment {@link Comment} which annotation should be deleted
     */
    void deleteAnnotation(Comment comment) {
        Annotation a = annotationMap.remove(comment);
        if (a != null) {
            a.markDeleted(true);
        }
        annotationModel.replaceAnnotations(new Annotation[] { a }, null);
    }
    
    /**
     * Deletes all annotations correlating to the given comment keys
     * @param commentKeys unique tag keys of the comment annotations which should be deleted
     */
    void deleteAnnotations(Set<String> commentKeys) {
        HashSet<Annotation> annotationsToRemove = new HashSet<Annotation>();
        Annotation a;
        for (String key : commentKeys) {
            a = annotationMap.remove(key);
            if (a != null) {
                a.markDeleted(true);
                annotationsToRemove.add(a);
            }
        }
        annotationModel.replaceAnnotations(annotationsToRemove.toArray(new Annotation[0]), null);
    }
    
    /**
     * Returns all comments which are overlapping with the given {@link Position}
     * @param p position
     * @return all comments which are overlapping with the given {@link Position}
     */
    Comment[] getCommentsByPosition(Position p) {
        HashSet<Comment> commentKeys = new HashSet<Comment>();
        Position tmp;
        for (Comment comment : annotationMap.keySet()) {
            tmp = ((IAnnotationModel) this.annotationModel).getPosition(annotationMap.get(comment));
            if (tmp.overlapsWith(p.getOffset(), p.getLength())) {
                commentKeys.add(comment);
            }
        }
        return commentKeys.toArray(new Comment[0]);
    }
    
    /**
     * Creates a new annotation for a given comment key
     * @param comment {@link Comment} for which an annotation will be created
     * @return created annotation or<br>null, if the comment is not known
     */
    private Annotation createNewAnnotation(Comment comment) {
        String annotationType;
        if (comment == null) return null;
        if (colorManager.isMultiColorEnabled() && colorManager.hasCustomizedColor(comment.getAuthor())) {
            annotationType = AgileReviewPreferences.AUTHOR_COLOR_DEFAULT + "_" + new AuthorReservationPreferences().getAuthorTag(comment.getAuthor());
        } else {
            annotationType = AgileReviewPreferences.AUTHOR_COLOR_DEFAULT;
        }
        Annotation annotation = new Annotation(annotationType, true, "Review: " + comment.getReview().getId() + ", Author: " + comment.getAuthor()
                + ", Comment-ID: " + comment.getId());
        this.annotationMap.put(comment, annotation);
        return annotation;
    }
    
    /**
     * Returns the {@link Set} of {@link Comment}s which are currently displayed (annotated)
     * @return the {@link Set} of {@link Comment}s which are currently displayed (annotated)
     * @author Malte Brunnlieb (17.03.2013)
     */
    public HashSet<Comment> getCurrentlyDisplayedComments() {
        return new HashSet<Comment>(annotationMap.keySet());
    }
}