package org.agilereview.storage.xml;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.agilereview.core.external.definition.IStorageClient;
import org.agilereview.core.external.preferences.AgileReviewPreferences;
import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Reply;
import org.agilereview.core.external.storage.Review;
import org.agilereview.core.external.storage.ReviewSet;
import org.agilereview.storage.xml.conversion.PojoConversion;
import org.agilereview.storage.xml.conversion.XmlBeansConversion;
import org.agilereview.storage.xml.exception.DataLoadingException;
import org.agilereview.storage.xml.exception.ExceptionHandler;
import org.agilereview.storage.xml.wizards.noreviewsource.NoReviewSourceProjectWizard;
import org.agilereview.xmlSchema.author.CommentsDocument;
import org.agilereview.xmlSchema.review.ReviewDocument;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlTokenSource;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;

/**
 * {@link IStorageClient} that stores review data in XML files.
 * @author Peter Reuter (04.04.2012)
 */
public class XmlStorageClient implements IStorageClient, IPreferenceChangeListener, PropertyChangeListener {

	//TODO compatibility to old xml format?

	/**
	 * Map of {@link Review} IDs to {@link Review} objects.
	 */
	private Map<String, Review> idReviewMap = new HashMap<String, Review>();
	/**
	 * The set of all loaded {@link Review}s.
	 */
	private ReviewSet reviewSet = new ReviewSet();
	/**
	 * Map of {@link Comment} IDs to {@link Comment} objects.
	 */
	private Map<String, Comment> idCommentMap = new HashMap<String, Comment>();
	/**
	 * Map of {@link Reply} IDs to {@link Reply} objects.
	 */
	private Map<String, Reply> idReplyMap = new HashMap<String, Reply>();
	/**
	 * Preferences for the XMLStorage plugin 
	 */
	private IEclipsePreferences preferencesNode = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
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
		preferencesNode.addPreferenceChangeListener(this);
		initialize();
	}
	
	@Override
	protected void finalize() throws Throwable {
		preferencesNode.removePreferenceChangeListener(this);
		super.finalize();
	}

	/**
	 * Initially loads all {@link Review} objects on startup
	 * @author Peter Reuter (04.04.2012)
	 */
	private void initialize() {
		// disable propertychangelistener
		reviewSet.removePropertyChangeListener(this);

		// clean up internal data structure (maps etc.)
		unloadReviews(this.reviewSet);
		// clear pojos
		this.reviewSet.clear();
		// update sourcefolder, load new data
		if (SourceFolderManager.getCurrentReviewSourceProject() != null) {
			loadReviews();
			String openReviewIdsList = InstanceScope.INSTANCE.getNode(AgileReviewPreferences.CORE_PLUGIN_ID).get(AgileReviewPreferences.OPEN_REVIEWS, "");
			if (!openReviewIdsList.equals("")) {
				List<String> openReviewIds = Arrays.asList(openReviewIdsList.split(","));
				loadComments(openReviewIds);
			}			
		} else {
			// no source folder available
			userNotified = false;
		}
		// reenable propertychangelistener
		reviewSet.addPropertyChangeListener(this);
	}

	/**
	 * Generates a unique ID for {@link Review}, {@link Comment} and {@link Reply} objects.
	 * @return a random and unique ID.
	 * @author Peter Reuter (04.04.2012)
	 */
	private String getNewId() {
		// TODO change this method... more sophisticated/short IDs are required!
		return UUID.randomUUID().toString();
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
		return getNewId();
	}

	@Override
	public String getNewCommentId(String author, Review review) {
		return getNewId();
	}

	@Override
	public String getNewReplyId(Object parent) {
		return getNewId();
	}

	//////////////////////////////////////////////////////
	// methods for loading POJO data from xmlbeans data //
	//////////////////////////////////////////////////////

	/**
	 * Loads all {@link Review} objects and adds a {@link PropertyChangeListener} to each of them.
	 * @author Peter Reuter (04.04.2012)
	 */
	private void loadReviews() {
		List<org.agilereview.xmlSchema.review.ReviewDocument.Review> xmlBeansReviews = loadAllXmlBeansReview();
		for (org.agilereview.xmlSchema.review.ReviewDocument.Review xmlBeansReview : xmlBeansReviews) {
			Review review = XmlBeansConversion.getReview(xmlBeansReview);
			this.idReviewMap.put(review.getId(), review);
		}
		this.reviewSet.addAll(idReviewMap.values());
	}

	/**
	 * Loads all {@link Comment} objects of the {@link Review} given by its ID.
	 * @param reviewId The ID of the {@link Review}.
	 * @author Peter Reuter (04.04.2012)
	 */
	private void loadComments(String reviewId) {
		Review review = this.idReviewMap.get(reviewId);
		if (review != null) {
			List<org.agilereview.xmlSchema.author.CommentDocument.Comment> xmlBeansComments = loadAllXmlBeansComment(reviewId);
			ArrayList<Comment> comments = new ArrayList<Comment>();
			for (org.agilereview.xmlSchema.author.CommentDocument.Comment xmlBeansComment : xmlBeansComments) {
				Comment comment = XmlBeansConversion.getComment(review, xmlBeansComment);
				// check if comment in map (just for unit tests --> XmlStorage is create twice, so it loads twice...
				comments.add(comment);
				this.idCommentMap.put(comment.getId(), comment);
				if (xmlBeansComment.getReplies() != null && xmlBeansComment.getReplies().getReplyArray() != null) {
					List<Reply> replies = XmlBeansConversion.getReplyList(comment, xmlBeansComment.getReplies().getReplyArray());
					addReplies(replies);
					comment.setReplies(replies);
				}
			}
			review.setComments(comments);	
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
	 * TODO add javadoc
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
	 * TODO add javadoc
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
	 * TODO add javadoc
	 * @param replies
	 * @author Peter Reuter (18.07.2012)
	 */
	private void unloadReplies(List<Reply> replies) {
		for (Reply r : replies) {
			unloadReplies(r.getReplies());
			this.idReplyMap.remove(r);
		}
	}

	/////////////////////////////////////////////////
	// methods for loading xmlbeans data from disc //
	/////////////////////////////////////////////////

	/**
	 * Loads all {@link org.agilereview.xmlSchema.review.ReviewDocument.Review} objects from all files available in the current source folder.
	 * @return A {@link List} of XmlBeans objects representing the review files.
	 * @author Peter Reuter (04.04.2012)
	 */
	private List<org.agilereview.xmlSchema.review.ReviewDocument.Review> loadAllXmlBeansReview() {
		List<org.agilereview.xmlSchema.review.ReviewDocument.Review> result = new ArrayList<org.agilereview.xmlSchema.review.ReviewDocument.Review>();
		try {
			IResource[] allFolders = SourceFolderManager.getCurrentReviewSourceProject().members();
			LinkedList<String> errors = new LinkedList<String>();
			for (IResource currFolder : allFolders) {
				if (currFolder instanceof IFolder) {
					String reviewId = ((IFolder) currFolder).getName().replace("review.","");
					IFile reviewFile = SourceFolderManager.getReviewFile(reviewId);
					try {
						ReviewDocument doc = ReviewDocument.Factory.parse(reviewFile.getContents());
						result.add(doc.getReview());
					} catch (XmlException e) {
						errors.add(reviewFile.getLocation().toOSString() + " ("+e.getLocalizedMessage()+")");
					} catch (IOException e) {
						errors.add(reviewFile.getLocation().toOSString() + " ("+e.getLocalizedMessage()+")");
					}
				}
			}
			if (!errors.isEmpty()) {
				String message = "AgileReview could not load the following review files:\n\n";
				for (String error : errors) {
					message += error + "\n";
				}
				message += "\nThese files may be corrupted (e.g. empty). Please check them.\nComments of a review cannot be loaded without working review file.";
				ExceptionHandler.notifyUser(new DataLoadingException(message));
			}
		} catch (final CoreException e) {
			String message = "Error while reading data from current Review Source Project. The Review Source Project '"+SourceFolderManager.getCurrentReviewSourceProjectName()+"' does not exists or is closed.";
			ExceptionHandler.notifyUser(new DataLoadingException(message));
		}

		return result;
	}

	/**
	 * Loads all {@link org.agilereview.xmlSchema.author.CommentDocument.Comment} objects from files available in the current source folder and
	 * belonging to the given review.
	 * @param reviewId The ID of the review.
	 * @return A {@link List} of XmlBeans objects representing the comments belonging to the review given.
	 * @author Peter Reuter (04.04.2012)
	 */
	private List<org.agilereview.xmlSchema.author.CommentDocument.Comment> loadAllXmlBeansComment(String reviewId) {
		List<org.agilereview.xmlSchema.author.CommentDocument.Comment> result = new ArrayList<org.agilereview.xmlSchema.author.CommentDocument.Comment>();
		try {
			IFolder reviewFolder = SourceFolderManager.getReviewFolder(reviewId);
			if (reviewFolder != null) {
				LinkedList<String> errors = new LinkedList<String>();
				IResource[] resources = reviewFolder.members();
				for (IResource commentFile : resources) {
					if (commentFile instanceof IFile && !commentFile.getName().equals("review.xml")) {
						try {
							String authorName = ((IFile) commentFile).getName().replace("author_", "").replace(".xml", "");
							CommentsDocument doc = CommentsDocument.Factory.parse(SourceFolderManager.getCommentFile(reviewId, authorName).getContents());
							result.addAll(Arrays.asList(doc.getComments().getCommentArray()));
						} catch (XmlException e) {
							errors.add(commentFile.getLocation().toOSString() + " ("+e.getLocalizedMessage()+")");
						} catch (IOException e) {
							errors.add(commentFile.getLocation().toOSString() + " ("+e.getLocalizedMessage()+")");
						}
					}

				}
				if (!errors.isEmpty()) {
					String message = "AgileReview could not load the following review files:\n\n";
					for (String error : errors) {
						message += error + "\n";
					}
					message += "\nThese files may be corrupted (e.g. empty). Please check them.\nComments of a review cannot be loaded without working review file.";
					ExceptionHandler.notifyUser(new DataLoadingException(message));
				}
			}
		} catch (final CoreException e) {
			String message = "Error while reading data from current Review Source Project. The Review Source Project '"+SourceFolderManager.getCurrentReviewSourceProjectName()+"' does not exists or is closed.";
			ExceptionHandler.notifyUser(new DataLoadingException(message));
		}

		return result;
	}

	//////////////////////////////////////////////
	// methods for storing xmldocuments on disc //
	//////////////////////////////////////////////

	/**
	 * @param file the {@link IFile} which will representing the saved {@link XmlTokenSource}
	 * @param doc the {@link XmlTokenSource} to save
	 */
	private void saveXmlDocument(IFile file, XmlTokenSource doc) {
		try {
			doc.save(file.getLocation().toFile(), new XmlOptions().setSavePrettyPrint());
			try {
				file.refreshLocal(IFile.DEPTH_INFINITE, null);
			} catch (CoreException e) {
				String message = "Error while refreshing file "+file.getFullPath().toOSString()+"!";
				ExceptionHandler.notifyUser(new DataLoadingException(message));
			}
		} catch (IOException e) {
			ExceptionHandler.notifyUser(e);
		}
	}

	/**
	 * Writes back new comments/replies or changes of a comment/reply to the filesystem.
	 * @param comment The comment that was changed or is the parent comment of the reply that changed.
	 * @author Peter Reuter (24.06.2012)
	 */
	private void store(Comment comment) {
		IFile commentFile = SourceFolderManager.getCommentFile(comment.getReview().getId(), comment.getAuthor());
		if (commentFile != null) {
			CommentsDocument doc = PojoConversion.getXmlBeansCommentsDocument(getComments(comment.getReview(), comment.getAuthor()));
			saveXmlDocument(commentFile, doc);	
		}		
	}

	/**
	 * Writes back new reviews or changes of a review to the filesystem.
	 * @param review The {@link Review} that was added or changed.
	 * @author Peter Reuter (24.06.2012)
	 */
	private void store(Review review) {
		IFile reviewFile = SourceFolderManager.getReviewFile(review.getId());
		if (reviewFile != null) {
			ReviewDocument doc = PojoConversion.getXmlBeansReviewDocument(review);
			saveXmlDocument(reviewFile, doc);
		}
	}

	///////////////////////////////////////////////
	// helper methods for PropertyChangeListener //
	///////////////////////////////////////////////

	/**
	 * TODO add javadoc
	 * @param evt
	 * @author Peter Reuter (18.07.2012)
	 */
	private void propertyChangeOfCommentOrReply(java.beans.PropertyChangeEvent evt) {
		Object source = evt.getSource();
		while (!(source instanceof Comment)) {
			source = ((Reply) source).getParent();
		}
		store((Comment) source);
	}

	/**
	 * TODO add javadoc
	 * @param evt
	 * @author Peter Reuter (18.07.2012)
	 */
	private void propertyChangeOfReview(java.beans.PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("isOpen")) {
			if ((Boolean) evt.getNewValue()) {
				loadComments(((Review) evt.getSource()).getId());
			} else {
				Review review = ((Review) evt.getSource());
				unloadComments(review.getComments());
			}
		} else if ("comments".equals(evt.getPropertyName())) {
			@SuppressWarnings("unchecked")
			ArrayList<Comment> oldValue = new ArrayList<Comment>((ArrayList<Comment>)evt.getOldValue());
			@SuppressWarnings("unchecked")
			ArrayList<Comment> newValue = new ArrayList<Comment>((ArrayList<Comment>)evt.getNewValue());
			if (oldValue.size() > newValue.size() && ((Review)evt.getSource()).getIsOpen()) {
				// comments were remove from review
				oldValue.removeAll(newValue);
				for (Comment c: oldValue) {
					unloadReplies(c.getReplies());
					ArrayList<Comment> authorCommentsForReview = getComments(c.getReview(), c.getAuthor());
					if (authorCommentsForReview.size() > 0) {
						store(c);
					} else {
						IFile commentFile = SourceFolderManager.getCommentFile(c.getReview().getId(), c.getAuthor());
						if (commentFile != null) {
							try {
								commentFile.delete(true, null);
							} catch (CoreException e) {
								String message = "Error while deleting file '"+commentFile.getFullPath().toOSString()+"'.";
								ExceptionHandler.notifyUser(new DataLoadingException(message));
							}	
						}
					}
				}
			} else {
				// comments were added to review
				newValue.removeAll(oldValue);
				for (Comment c: newValue) {
					store(c);
				}
			}
		} else {
			store((Review) evt.getSource());
		}
	}

	/**
	 * TODO add javadoc
	 * @param evt
	 * @author Peter Reuter (18.07.2012)
	 */
	private void propertyChangeOfReviewSet(java.beans.PropertyChangeEvent evt) {
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
				store(review);
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
	}

	//////////////////////////////////////
	// method of PropertyChangeListener //
	//////////////////////////////////////

	@Override
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		if (SourceFolderManager.getCurrentReviewSourceProject() == null && !userNotified ) {
			userNotified = true;
			String message = "No Review Source Project available. All changes will not be stored persistently. Please create and/or activate a Review Source Folder to persistently store Review data.";
			ExceptionHandler.notifyUser(message);
			Display.getCurrent().syncExec(new Runnable() {
				@Override
				public void run() {
					NoReviewSourceProjectWizard dialog = new NoReviewSourceProjectWizard();
					WizardDialog wDialog = new WizardDialog(Display.getCurrent().getActiveShell(), dialog);
					wDialog.setBlockOnOpen(true);
					wDialog.open();
				}
			});
		} else {
			if (evt.getSource() instanceof ReviewSet) {
				propertyChangeOfReviewSet(evt);
			} else if (evt.getSource() instanceof Review) {
				propertyChangeOfReview(evt);
			} else if ((evt.getSource() instanceof Comment || evt.getSource() instanceof Reply) && !"modificationDate".equals(evt.getPropertyName())) {
				propertyChangeOfCommentOrReply(evt);
			}
		}				
	}

	///////////////////////////////////////
	// method of IPropertyChangeListener //
	///////////////////////////////////////

	@Override
	public void preferenceChange(PreferenceChangeEvent event) {
		if (event.getKey().equals(SourceFolderManager.SOURCEFOLDER_PROPERTYNAME)) {
			SourceFolderManager.setCurrentSourceFolderProject();
			initialize();
		}
	}

	//////////////////////////
	// other helper methods //
	//////////////////////////

	/**
	 * Returns all {@link Comment} objects belonging to the given {@link Review} and author.
	 * @param review
	 * @param author
	 * @return An {@link ArrayList} of {@link Comment} objects.
	 * @author Peter Reuter (04.04.2012)
	 */
	private ArrayList<Comment> getComments(Review review, String author) {
		ArrayList<Comment> result = new ArrayList<Comment>();
		for (Comment comment : review.getComments()) {
			if (comment.getAuthor().equals(author)) {
				result.add(comment);
			}
		}
		return result;
	}

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
