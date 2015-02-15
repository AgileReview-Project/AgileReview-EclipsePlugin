package org.agilereview.storage.xml;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.agilereview.common.exception.ExceptionHandler;
import org.agilereview.core.external.definition.IStorageClient;
import org.agilereview.core.external.preferences.AgileReviewPreferences;
import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Reply;
import org.agilereview.core.external.storage.Review;
import org.agilereview.core.external.storage.ReviewSet;
import org.agilereview.core.external.storage.constants.PropertyChangeEventKeys;
import org.agilereview.storage.xml.exception.DataLoadingException;
import org.agilereview.storage.xml.persistence.SourceFolderManager;
import org.agilereview.storage.xml.persistence.XmlLoader;
import org.agilereview.storage.xml.persistence.XmlPersister;
import org.agilereview.storage.xml.wizards.noreviewsource.NoReviewSourceProjectWizard;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.prefs.BackingStoreException;

/**
 * {@link IStorageClient} that stores review data in XML files.
 * @author Peter Reuter (04.04.2012)
 */
public class XmlStorageClient implements IStorageClient, IPreferenceChangeListener, PropertyChangeListener {
    
    private static final String SEPARATOR = "@";
    
    //TODO compatibility to old xml format?
    
    /**
     * Map of {@link Review} IDs to {@link Review} objects.
     */
    private final Map<String, Review> idReviewMap = new HashMap<String, Review>();
    /**
     * The set of all loaded {@link Review}s.
     */
    private final ReviewSet reviewSet = new ReviewSet();
    /**
     * Map of {@link Comment} IDs to {@link Comment} objects.
     */
    private final Map<String, Comment> idCommentMap = new HashMap<String, Comment>();
    /**
     * Map of {@link Reply} IDs to {@link Reply} objects.
     */
    private final Map<String, Reply> idReplyMap = new HashMap<String, Reply>();
    /**
     * Preferences for the XMLStorage plugin
     */
    private final IEclipsePreferences preferencesNode = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
    /**
     * Indicates whether the user was notified that no Review Source Project is available.
     */
    private boolean userNotified = false;
    
    ///////////////////////////////////////////////////////
    // additional methods needed by the XmlStorageClient //
    ///////////////////////////////////////////////////////
    
    /**
     * Constructor for the XmlStorageClient which initializes the local review data base
     * @author Peter Reuter (04.04.2012)
     */
    public XmlStorageClient() {
        this.preferencesNode.addPreferenceChangeListener(this);
        initialize();
    }
    
    @Override
    protected void finalize() throws Throwable {
        this.preferencesNode.removePreferenceChangeListener(this);
        super.finalize();
    }
    
    /**
     * Initially loads all {@link Review} objects on startup
     * @author Peter Reuter (04.04.2012)
     */
    private void initialize() {
        
        // disable propertychangelistener
        this.reviewSet.removePropertyChangeListener(this);
        
        // clean up internal data structure (maps etc.)
        unloadReviews(this.reviewSet);
        // clear pojos
        this.reviewSet.clear();
        // update sourcefolder, load new data
        if (SourceFolderManager.getCurrentReviewSourceProject() != null) {
            loadReviews();
            String openReviewIdsList = InstanceScope.INSTANCE.getNode(AgileReviewPreferences.CORE_PLUGIN_ID).get(AgileReviewPreferences.OPEN_REVIEWS,
                    "");
            if (!openReviewIdsList.equals("")) {
                List<String> openReviewIds = Arrays.asList(openReviewIdsList.split(","));
                loadComments(openReviewIds);
            }
        } else {
            // no source folder available
            this.userNotified = false;
        }
        // reenable propertychangelistener
        this.reviewSet.addPropertyChangeListener(this);
    }
    
    ////////////////////////////////
    // methods of IStorageClients //
    ////////////////////////////////
    
    @Override
    public ReviewSet getAllReviews() {
        return this.reviewSet;
    }
    
    @Override
    public String getNewReviewId() {
        int id = 0;
        List<Review> reviews = new ArrayList<>(this.reviewSet);
        Collections.sort(reviews, new Comparator<Review>() {
            
            @Override
            public int compare(Review o1, Review o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
        for (Review r : reviews) {
            int currId = Integer.parseInt(r.getId().substring(1, r.getId().length()));
            if (id < currId) {
                break;
            } else {
                id = currId + 1;
            }
        }
        return "r" + id;
    }
    
    @Override
    public String getNewCommentId(String author, Review review) {
        int id = 0;
        List<Comment> comments = review.getComments();
        Collections.sort(comments, new Comparator<Comment>() {
            
            @Override
            public int compare(Comment o1, Comment o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
        for (Comment c : review.getComments()) {
            if (c.getAuthor().equals(author)) {
                int currId = Integer.parseInt(c.getId().substring(1, c.getId().indexOf(SEPARATOR)));
                if (id < currId) {
                    break;
                } else {
                    id = currId + 1;
                }
            }
        }
        return "c" + id + SEPARATOR + author + SEPARATOR + review.getId();
    }
    
    @Override
    public String getNewReplyId(Object parent) {
        String newId = "";
        if (parent instanceof Comment) {
            newId = getNewReplyId((Comment) parent);
        } else { // parent is reply
            newId = getNewReplyId((Reply) parent);
        }
        return newId;
    }
    
    /**
     * Get new reply ID if parent of new reply is a comment
     * @param parent the comment
     * @return the new ID
     * @author Peter Reuter (15.02.2015)
     */
    private String getNewReplyId(Comment parent) {
        List<Reply> replies = parent.getReplies();
        Collections.sort(replies, new Comparator<Reply>() {
            
            @Override
            public int compare(Reply o1, Reply o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
        int id = 0;
        for (Reply r : replies) {
            int currId = Integer.parseInt(r.getId().substring(1, r.getId().length()));
            if (id < currId) {
                break;
            } else {
                id = currId + 1;
            }
        }
        return "r" + id;
    }
    
    /**
     * Get new reply ID if parent of new reply is a reply as well.
     * @param parent the parent reply
     * @return the new ID
     * @author Peter Reuter (15.02.2015)
     */
    private String getNewReplyId(Reply parent) {
        List<Reply> replies = parent.getReplies();
        Collections.sort(replies, new Comparator<Reply>() {
            
            @Override
            public int compare(Reply o1, Reply o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
        int id = 0;
        for (Reply r : replies) {
            int currId = Integer.parseInt(r.getId().substring(1, r.getId().length()));
            if (id < currId) {
                break;
            } else {
                id = currId + 1;
            }
        }
        String result = parent.getId() + "r" + id;
        return result;
    }
    
    //////////////////////////////////////////////////////
    // methods for loading POJO data from xmlbeans data //
    //////////////////////////////////////////////////////
    
    /**
     * Loads all {@link Review} objects and adds a {@link PropertyChangeListener} to each of them.
     * @author Peter Reuter (04.04.2012)
     */
    private void loadReviews() {
        List<Review> reviews = XmlLoader.loadReviews();
        for (Review review : reviews) {
            this.idReviewMap.put(review.getId(), review);
        }
        this.reviewSet.addAll(this.idReviewMap.values());
    }
    
    /**
     * Loads all {@link Comment} objects of the {@link Review} given by its ID.
     * @param reviewId The ID of the {@link Review}.
     * @author Peter Reuter (04.04.2012)
     */
    private void loadComments(String reviewId) {
        Review review = this.idReviewMap.get(reviewId);
        if (review != null) {
            XmlLoader.loadComments(review);
            for (Comment comment : review.getComments()) {
                this.idCommentMap.put(comment.getId(), comment);
                addReplies(comment.getReplies());
            }
        }
    }
    
    /**
     * Loads all {@link Comment} objects of the {@link Review}s given by their ID.
     * @param reviewIds
     * @author Peter Reuter (04.04.2012)
     */
    private void loadComments(List<String> reviewIds) {
        for (String reviewId : reviewIds) {
            loadComments(reviewId);
        }
    }
    
    /////////////////////////////////////
    // methods for unloading POJO data //
    /////////////////////////////////////
    
    /**
     * Unload all reviews
     * @param reviews
     * @author Peter Reuter (18.07.2012)
     */
    private void unloadReviews(HashSet<Review> reviews) {
        for (Review r : reviews) {
            unloadComments(r.getComments());
        }
        this.reviewSet.clear();
        this.idReviewMap.clear();
    }
    
    /**
     * Unload all comments
     * @param comments
     * @author Peter Reuter (18.07.2012)
     */
    private void unloadComments(List<Comment> comments) {
        for (Comment c : comments) {
            unloadReplies(c.getReplies());
            this.idCommentMap.remove(c);
        }
        comments.clear();
    }
    
    /**
     * Unload all replies
     * @param replies
     * @author Peter Reuter (18.07.2012)
     */
    private void unloadReplies(List<Reply> replies) {
        for (Reply r : replies) {
            unloadReplies(r.getReplies());
            this.idReplyMap.remove(r);
        }
    }
    
    ///////////////////////////////////////////////
    // helper methods for PropertyChangeListener //
    ///////////////////////////////////////////////
    
    /**
     * A {@link Comment} or {@link Reply} was changed
     * @param evt
     * @author Peter Reuter (18.07.2012)
     */
    private void propertyChangeOfCommentOrReply(java.beans.PropertyChangeEvent evt) {
        Object source = evt.getSource();
        while (!(source instanceof Comment)) {
            source = ((Reply) source).getParent();
        }
        XmlPersister.store((Comment) source);
    }
    
    /**
     * A {@link Review} changed
     * @param evt
     * @author Peter Reuter (18.07.2012)
     */
    private void propertyChangeOfReview(java.beans.PropertyChangeEvent evt) {
        if (PropertyChangeEventKeys.REVIEW_ISOPEN_STATUS.equals(evt.getPropertyName())) {
            if ((Boolean) evt.getNewValue()) {
                loadComments(((Review) evt.getSource()).getId());
            } else {
                Review review = ((Review) evt.getSource());
                unloadComments(review.getComments());
            }
        } else if (PropertyChangeEventKeys.REVIEW_COMMENTS.equals(evt.getPropertyName())) {
            @SuppressWarnings("unchecked")
            ArrayList<Comment> oldValue = new ArrayList<Comment>((ArrayList<Comment>) evt.getOldValue());
            @SuppressWarnings("unchecked")
            ArrayList<Comment> newValue = new ArrayList<Comment>((ArrayList<Comment>) evt.getNewValue());
            if (oldValue.size() > newValue.size() && ((Review) evt.getSource()).getIsOpen()) {
                // comments were remove from review
                oldValue.removeAll(newValue);
                for (Comment c : oldValue) {
                    unloadReplies(c.getReplies());
                    ArrayList<Comment> authorCommentsForReview = Helper.getComments(c.getReview(), c.getAuthor());
                    if (authorCommentsForReview.size() > 0) {
                        XmlPersister.store(c);
                    } else {
                        IFile commentFile = SourceFolderManager.getCommentFile(c.getReview().getId(), c.getAuthor());
                        if (commentFile != null) {
                            try {
                                commentFile.delete(true, null);
                            } catch (CoreException e) {
                                String message = "Error while deleting file '" + commentFile.getFullPath().toOSString() + "'.";
                                ExceptionHandler.logAndNotifyUser(new DataLoadingException(message), Activator.PLUGIN_ID);
                            }
                        }
                    }
                }
            } else {
                // comments were added to review
                newValue.removeAll(oldValue);
                for (Comment c : newValue) {
                    XmlPersister.store(c);
                }
            }
        } else {
            XmlPersister.store((Review) evt.getSource());
        }
    }
    
    /**
     * A {@link ReviewSet} changed.
     * @param evt
     * @author Peter Reuter (18.07.2012)
     */
    private void propertyChangeOfReviewSet(java.beans.PropertyChangeEvent evt) {
        if (PropertyChangeEventKeys.REVIEWSET_REVIEWS.equals(evt.getPropertyName())) {
            @SuppressWarnings("unchecked")
            HashSet<Review> oldValue = (HashSet<Review>) evt.getOldValue();
            @SuppressWarnings("unchecked")
            HashSet<Review> newValue = (HashSet<Review>) evt.getNewValue();
            if (newValue.size() >= oldValue.size()) {
                // reviews added
                HashSet<Review> diff = new HashSet<Review>(newValue);
                diff.removeAll(oldValue);
                for (Review review : diff) {
                    this.idReviewMap.put(review.getId(), review);
                    this.reviewSet.add(review);
                    for (Comment comment : review.getComments()) {
                        this.idCommentMap.put(comment.getId(), comment);
                        addReplies(comment.getReplies());
                    }
                    XmlPersister.store(review);
                }
            } else {
                // reviews removed
                HashSet<Review> diff = new HashSet<Review>(oldValue);
                diff.removeAll(newValue);
                for (Review review : diff) {
                    this.idReviewMap.remove(review.getId());
                    this.reviewSet.remove(review);
                    unloadComments(review.getComments());
                    SourceFolderManager.deleteReviewContents(review.getId());
                }
            }
        } else {
            // nothing to do here
        }
    }
    
    /**
     * @param evt
     * @author Peter Reuter (04.12.2012)
     */
    private void processPropertyChange(java.beans.PropertyChangeEvent evt) {
        if (evt.getSource() instanceof ReviewSet) {
            propertyChangeOfReviewSet(evt);
        } else if (evt.getSource() instanceof Review) {
            propertyChangeOfReview(evt);
        } else if (evt.getSource() instanceof Comment && !PropertyChangeEventKeys.COMMENT_MODIFICATION_DATE.equals(evt.getPropertyName())
                || evt.getSource() instanceof Reply && !PropertyChangeEventKeys.REPLY_MODIFICATION_DATE.equals(evt.getPropertyName())) {
            propertyChangeOfCommentOrReply(evt);
        }
    }
    
    //////////////////////////////////////
    // method of PropertyChangeListener //
    //////////////////////////////////////
    
    @Override
    public void propertyChange(java.beans.PropertyChangeEvent evt) {
        if (SourceFolderManager.getCurrentReviewSourceProject() == null && !this.userNotified) {
            
            this.userNotified = true;
            
            String message = "No Review Source Project available. All changes will be discarded when Eclipse shuts down or a Review Source Project is created and/or activated. Please create and/or activate a Review Source Folder to persistently store Review data.";
            ExceptionHandler.logAndNotifyUser(new Exception(message), Activator.PLUGIN_ID);
            
            // backup data that was inserted until now
            ReviewSet backup = new ReviewSet();
            backup.addAll(this.reviewSet);
            
            // allow user to create and/or activate Review Source Project
            Display.getCurrent().syncExec(new Runnable() {
                @Override
                public void run() {
                    NoReviewSourceProjectWizard dialog = new NoReviewSourceProjectWizard();
                    WizardDialog wDialog = new WizardDialog(Display.getCurrent().getActiveShell(), dialog);
                    wDialog.setBlockOnOpen(true);
                    wDialog.open();
                }
            });
            
            // add backup data to ReviewSet and thereby to new Review Source Project
            if (SourceFolderManager.getCurrentReviewSourceProject() != null) {
                this.reviewSet.addAll(backup);
            }
            
        } else {
            processPropertyChange(evt);
        }
    }
    
    ///////////////////////////////////////
    // method of IPropertyChangeListener //
    ///////////////////////////////////////
    
    @Override
    public void preferenceChange(PreferenceChangeEvent event) {
        if (event.getKey().equals(SourceFolderManager.SOURCEFOLDER_PROPERTYNAME)) {
            SourceFolderManager.setCurrentSourceFolderProject();
            // remove the preference for the currently active review as this might cause inconsistences
            IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode("org.agilereview.core");
            preferences.remove(AgileReviewPreferences.ACTIVE_REVIEW_ID);
            try {
                preferences.flush();
            } catch (BackingStoreException e) {
                String message = "AgileReview could not discard the ID of the active review.";
                ExceptionHandler.logAndNotifyUser(e, message);
            }
            initialize();
        }
    }
    
    //////////////////////////
    // other helper methods //
    //////////////////////////
    
    /**
     * Adds the {@link List} of {@link Reply} objects recursively to the idReplyMap.
     * @param replies
     * @author Peter Reuter (04.04.2012)
     */
    private void addReplies(List<Reply> replies) {
        for (Reply reply : replies) {
            this.idReplyMap.put(reply.getId(), reply);
            addReplies(reply.getReplies());
        }
    }
    
}
