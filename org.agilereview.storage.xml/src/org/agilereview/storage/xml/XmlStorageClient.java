package org.agilereview.storage.xml;

import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.agilereview.core.external.definition.IStorageClient;
import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Reply;
import org.agilereview.core.external.storage.Review;
import org.agilereview.storage.xml.exception.DataLoadingException;
import org.agilereview.storage.xml.exception.ExceptionHandler;
import org.agilereview.xmlSchema.author.CommentDocument;
import org.agilereview.xmlSchema.author.CommentsDocument;
import org.agilereview.xmlSchema.author.CommentsDocument.Comments;
import org.agilereview.xmlSchema.author.RepliesDocument.Replies;
import org.agilereview.xmlSchema.review.ReviewDocument;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlTokenSource;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

/**
 * {@link IStorageClient} that stores review data in XML files.
 * @author Peter Reuter (04.04.2012)
 */
public class XmlStorageClient extends Plugin implements IStorageClient, IPropertyChangeListener, PropertyChangeListener {
	
	//TODO compatibility to old xml format?
	
	/**
	 * ID of this plugin 
	 */
	private static String ID = "org.agilereview.storage.xml.XmlStorageClient";
	
	/**
	 * Name of the property which stores the name of the current source folder 
	 */
	private static String SOURCEFOLDER_PROPERTYNAME = "source_folder";

	/**
	 * Map of {@link Review} IDs to {@link Review} objects. 
	 */
	private Map<String, Review> idReviewMap = new HashMap<String, Review>();
	/**
	 * Map of {@link Comment} IDs to {@link Comment} objects. 
	 */
	private Map<String, Comment> idCommentMap = new HashMap<String, Comment>();
	/**
	 * Map of {@link Reply} IDs to {@link Reply} objects. 
	 */
	private Map<String, Reply> idReplyMap = new HashMap<String, Reply>();
	
	
	///////////////////////////////////////////////////
	// static methods for managing files and folders //
	///////////////////////////////////////////////////
	
	/**
	 * Creates an {@link IFile} object which represents the file for storing {@link Comment} objects based on the given reviewId/author pair. The file is create if it does not exist.
	 * @param reviewId
	 * @param author
	 * @return {@link IFile} for the given parameter pair
	 */
	private static IFile getCommentFile(String reviewId, String author) {
		IFile file = getReviewFolder(reviewId).getFile("author_" + author + ".xml");
		if (!file.exists()) {
			try {
				// TODO maybe use ProgressMonitor here?
				file.create(new ByteArrayInputStream("".getBytes()), IResource.NONE, null);
				while (!file.exists()) {
				}
			} catch (final CoreException e) {
				ExceptionHandler.notifyUser(e);
			}
		}
		return file;
	}

	/**
	 * Returns an {@link IFolder} object which represents the folder of the {@link Review} given by its ID. The folder is created if it does not exists.
	 * @param reviewId
	 * @return {@link IFolder} for this review
	 */
	private static IFolder getReviewFolder(String reviewId) {
		IFolder folder = SourceFolderManager.getCurrentSourceFolder().getFolder("review." + reviewId);
		if (!folder.exists()) {
			try {
				// TODO maybe use progressmonitor here?
				folder.create(IResource.NONE, true, null);
				while (!folder.exists()) {
				}
			} catch (final CoreException e) {
				ExceptionHandler.notifyUser(e);
			}
		}
		return folder;
	}

	/**
	 * Returns an {@link IFile} object which represents the the review-file of the {@link Review} given by its ID. The file and its review folder are created if they do not exist.
	 * @param reviewId
	 * @return {@link IFile} for this review
	 */
	private static IFile getReviewFile(String reviewId) {
		IFile file = getReviewFolder(reviewId).getFile("review.xml");
		if (!file.exists()) {
			try {
				// TODO maybe use ProgressMonitor here?
				file.create(new ByteArrayInputStream("".getBytes()), IResource.NONE, null);
				while (!file.exists()) {
				}
			} catch (final CoreException e) {
				ExceptionHandler.notifyUser(e);
			}
		}
		return file;
	}
	
	///////////////////////////////////////////////////////
	// additional methods needed by the XmlStorageClient //
	///////////////////////////////////////////////////////
	
	/**
	 * Constructor for the XmlStorageClient which initializes the local review data base  
	 * @author Peter Reuter (04.04.2012)
	 */
	public XmlStorageClient() {
		initialize();
	}

	/**
	 * Initially loads all {@link Review} objects on startup 
	 * @author Peter Reuter (04.04.2012)
	 */
	private void initialize() {
		loadReviews();
		// TODO load comments of open reviews.
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
	
	////////////////////////////////
	// methods of IStorageClients //
	////////////////////////////////

	@Override
	public String getName() {
		return ID;
	}

	@Override
	public List<Review> getAllReviews() {
		return new ArrayList<Review>(this.idReviewMap.values());
	}

	@Override
	public String getNewId(Review review) {
		return getNewId();
	}

	@Override
	public String getNewId(Comment comment) {
		return getNewId();
	}

	@Override
	public String getNewId(Reply reply) {
		return getNewId();
	}

	@Override
	public void addReview(Review review) {
		this.idReviewMap.put(review.getId(), review);
		for (Comment comment : review.getComments()) {
			this.idCommentMap.put(comment.getId(), comment);
			addReplies(comment.getReplies());
		}
	}
	
	//////////////////////////////////////////
	// methods for extracting xmlbeans data //
	//////////////////////////////////////////

	/**
	 * Loads all {@link org.agilereview.xmlSchema.review.ReviewDocument.Review}
	 *  objects from all files available in the current source folder.
	 * @return A {@link List} of XmlBeans objects representing the review files.
	 * @author Peter Reuter (04.04.2012)
	 */
	private List<org.agilereview.xmlSchema.review.ReviewDocument.Review> loadAllXmlBeansReview() {
		List<org.agilereview.xmlSchema.review.ReviewDocument.Review> result = null;
		try {
			result = new ArrayList<org.agilereview.xmlSchema.review.ReviewDocument.Review>();
			IResource[] allFolders = SourceFolderManager.getCurrentSourceFolder().members();
			LinkedList<IResource> errorFiles = new LinkedList<IResource>();
			for (IResource currFolder : allFolders) {
				if (currFolder instanceof IFolder) {
					IFile reviewFile = ((IFolder) currFolder).getFile("review.xml");
					try {
						ReviewDocument doc = ReviewDocument.Factory.parse(reviewFile.getContents());
						result.add(doc.getReview());
					} catch (XmlException e) {
						errorFiles.add(reviewFile);
					} catch (IOException e) {
						errorFiles.add(reviewFile);
					}
				}
			}
			if (!errorFiles.isEmpty()) {
				String message = "AgileReview could not load the following review files:\n\n";
				for (IResource file : errorFiles) {
					message += file.getLocation().toOSString() + "\n";
				}
				message += "\nThese files may be corrupted (e.g. empty). Please check them.\nComments of a review cannot be loaded without working review file.";
				ExceptionHandler.notifyUser(new DataLoadingException(message));
			}
		} catch (final CoreException e) {
			String message = "Error while reading data from current Review Source Folder. The folder does not exists or is closed.";
			ExceptionHandler.notifyUser(new DataLoadingException(message));
		}

		return result;
	}
	
	/**
	 * Loads all {@link org.agilereview.xmlSchema.author.CommentDocument.Comment}
	 *  objects from files available in the current source folder and belonging
	 *  to the given review. 
	 * @param reviewId The ID of the review.
	 * @return A {@link List} of XmlBeans objects representing the comments belonging to the review given.
	 * @author Peter Reuter (04.04.2012)
	 */
	private List<org.agilereview.xmlSchema.author.CommentDocument.Comment> loadAllXmlBeansComment(String reviewId) {
		List<org.agilereview.xmlSchema.author.CommentDocument.Comment> result = null;
		try {
			result = new ArrayList<org.agilereview.xmlSchema.author.CommentDocument.Comment>();
			IFolder reviewFolder = getReviewFolder(reviewId);
			LinkedList<IResource> errorFiles = new LinkedList<IResource>();
			IResource[] resources = reviewFolder.members();
			for (IResource commentFile : resources) {
				if (commentFile instanceof IFile && !commentFile.getName().equals("review.xml")) {
					try {
						CommentsDocument doc = CommentsDocument.Factory.parse(((IFile)commentFile).getContents());
						result.addAll(Arrays.asList(doc.getComments().getCommentArray()));
					} catch (XmlException e) {
						errorFiles.add(commentFile);
					} catch (IOException e) {
						errorFiles.add(commentFile);
					}
				}
				
			}
			if (!errorFiles.isEmpty()) {
				String message = "AgileReview could not load the following review files:\n\n";
				for (IResource file : errorFiles) {
					message += file.getLocation().toOSString() + "\n";
				}
				message += "\nThese files may be corrupted (e.g. empty). Please check them.\nComments of a review cannot be loaded without working review file.";
				ExceptionHandler.notifyUser(new DataLoadingException(message));
			}
		} catch (final CoreException e) {
			String message = "Error while reading data from current Review Source Folder. The folder does not exists or is closed.";
			ExceptionHandler.notifyUser(new DataLoadingException(message));
		}

		return result;
	}
	
	//////////////////////////////////////////////////////
	// methods for converting xmlbeans objects to pojos //
	//////////////////////////////////////////////////////
	
	/**
	 * Creates a {@link Review} object from the given XmlBeans {@link org.agilereview.xmlSchema.review.ReviewDocument.Review} object.
	 * @param xmlBeansReview The {@link org.agilereview.xmlSchema.review.ReviewDocument.Review} object to convert.
	 * @return The {@link Review} object constructed from the {@link Review} object.
	 * @author Peter Reuter (04.04.2012)
	 */
	private Review getReview(org.agilereview.xmlSchema.review.ReviewDocument.Review xmlBeansReview) {
		String id = xmlBeansReview.getId();
		int status = xmlBeansReview.getStatus();
		String reference = xmlBeansReview.getReferenceId();
		String responsibility = xmlBeansReview.getResponsibility();
		String description = xmlBeansReview.getDescription();
		
		Review review = new Review(id, status, reference, responsibility, description);
		
		this.idReviewMap.put(review.getId(), review);

		return review;
	}
	
	/**
	 * Creates a {@link Comment} object from the given XmlBeans {@link org.agilereview.xmlSchema.author.CommentDocument.Comment} object.
	 * @param xmlBeansComment The {@link org.agilereview.xmlSchema.author.CommentDocument.Comment} object to convert.
	 * @return The {@link Comment} object constructed from the {@link org.agilereview.xmlSchema.author.CommentDocument.Comment} object.
	 * @author Peter Reuter (04.04.2012)
	 */
	private Comment getComment(org.agilereview.xmlSchema.author.CommentDocument.Comment xmlBeansComment) {
		String id = xmlBeansComment.getId();
		String authorName = xmlBeansComment.getAuthorName();
		IFile commentedFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(xmlBeansComment.getResourcePath()));
		Review review = this.idReviewMap.get(xmlBeansComment.getReviewID());
		Calendar creationDate = xmlBeansComment.getCreationDate();
		Calendar modificationDate = xmlBeansComment.getLastModified();
		String recipient = xmlBeansComment.getRecipient();
		int status = xmlBeansComment.getStatus();
		int priority = xmlBeansComment.getPriority();
		String text = xmlBeansComment.getText();
		
		Comment comment = new Comment(id, authorName, commentedFile, review, creationDate, modificationDate, recipient, status, priority, text);
		this.idCommentMap.put(comment.getId(), comment);
		
		comment.setReplies(getReplyList(xmlBeansComment.getReplies().getReplyArray()));
		
		return comment;
	}
	
	/**
	 * Converts an array of {@link org.agilereview.xmlSchema.author.ReplyDocument.Reply}
	 *  objects to a {@link ArrayList} of {@link Reply} objects. 
	 * @param xmlBeansReplies The array of {@link org.agilereview.xmlSchema.author.ReplyDocument.Reply} objects to convert.
	 * @return The {@link ArrayList} of {@link Reply} objects constructed from the array of {@link org.agilereview.xmlSchema.author.ReplyDocument.Reply} objects.
	 * @author Peter Reuter (04.04.2012)
	 */
	private ArrayList<Reply> getReplyList(org.agilereview.xmlSchema.author.ReplyDocument.Reply[] xmlBeansReplies) {
		ArrayList<Reply> result = new ArrayList<Reply>();
		for (org.agilereview.xmlSchema.author.ReplyDocument.Reply xmlBeansReply : xmlBeansReplies) {
			Reply reply = getReply(xmlBeansReply);
			result.add(reply);
		}
		return result;
	}
	
	/**
	 * Converts a single {@link org.agilereview.xmlSchema.author.ReplyDocument.Reply} reply to a {@link Reply} object. 
	 * @param xmlBeansReply The {@link org.agilereview.xmlSchema.author.ReplyDocument.Reply} object to convert.
	 * @return The {@link Reply} object constructed from the {@link org.agilereview.xmlSchema.author.ReplyDocument.Reply} object.
	 * @author Peter Reuter (04.04.2012)
	 */
	private Reply getReply(org.agilereview.xmlSchema.author.ReplyDocument.Reply xmlBeansReply) {
		String id = xmlBeansReply.getId();
		String author = xmlBeansReply.getAuthor();
		Calendar creationDate = xmlBeansReply.getCreationDate();
		Calendar modificationDate =  xmlBeansReply.getLastModified();
		String text = xmlBeansReply.getText();
		
		XmlCursor cursor = xmlBeansReply.newCursor();
		cursor.toParent();
		XmlObject xmlBeansParent = cursor.getObject();
		cursor.dispose();
		Object parent = null;
		if (xmlBeansParent instanceof org.agilereview.xmlSchema.author.CommentDocument.Comment) {
			parent = this.idCommentMap.get(((org.agilereview.xmlSchema.author.CommentDocument.Comment) xmlBeansParent).getId());
		} else if (xmlBeansParent instanceof org.agilereview.xmlSchema.author.ReplyDocument.Reply) {
			parent = this.idReplyMap.get(((org.agilereview.xmlSchema.author.ReplyDocument.Reply)xmlBeansParent).getId());
		} else {
			//TODO should we do something here? --> should not happen (normally)...
		}
		
		Reply reply = new Reply(id, author, creationDate, modificationDate, text, parent);
		
		reply.setReplies(getReplyList(xmlBeansReply.getReplies().getReplyArray()));
		
		return reply;
	}
	
	/////////////////////////////////////////////////////////
	// methods for loading reviews and comments from files //
	/////////////////////////////////////////////////////////

	/**
	 * Loads all {@link Comment} object of all {@link Review} objects.
	 * @author Peter Reuter (04.04.2012)
	 */
	private void loadAllComments() {
		for (String reviewId : this.idReviewMap.keySet()) {
			loadComments(reviewId);
		}
	}

	/**
	 * Loads all {@link Comment} objects of the {@link Review} given by its ID.
	 * @param reviewId The ID of the {@link Review}.
	 * @author Peter Reuter (04.04.2012)
	 */
	private void loadComments(String reviewId) {
		List<org.agilereview.xmlSchema.author.CommentDocument.Comment> xmlBeansComments = loadAllXmlBeansComment(reviewId);
		for (org.agilereview.xmlSchema.author.CommentDocument.Comment xmlBeansComment : xmlBeansComments) {
			Comment comment = getComment(xmlBeansComment);
			this.idReviewMap.get(reviewId).addComment(comment);
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

	/**
	 * Loads all {@link Review} objects and adds a {@link PropertyChangeListener} to each of them. 
	 * @author Peter Reuter (04.04.2012)
	 */
	private void loadReviews() {
		List<org.agilereview.xmlSchema.review.ReviewDocument.Review> xmlBeansReviews = loadAllXmlBeansReview();
		for (org.agilereview.xmlSchema.review.ReviewDocument.Review xmlBeansReview : xmlBeansReviews) {
			Review review = getReview(xmlBeansReview);
			review.addPropertyChangeListener(this);
		}
	}
	
	///////////////////////////////////////////////////////
	// methods for storing reviews and comments to files //
	///////////////////////////////////////////////////////

	/**
	 * Converts a list of {@link Comment} objects to a XmlBeans {@link CommentDocument}. 
	 * @param comments An {@link ArrayList} of comments to be converted.
	 * @return A XmlBeans {@link CommentDocument} holding {@link org.agilereview.xmlSchema.author.CommentDocument.Comment}
	 *  and their respective {@link org.agilereview.xmlSchema.author.ReplyDocument.Reply} objects.
	 * @author Peter Reuter (04.04.2012)
	 */
	private CommentsDocument getXmlBeansCommentsDocument(ArrayList<Comment> comments) {
		CommentsDocument doc = CommentsDocument.Factory.newInstance();
		Comments xmlBeansComments = doc.addNewComments();
		for (Comment comment: comments) {
			org.agilereview.xmlSchema.author.CommentDocument.Comment xmlBeansComment = xmlBeansComments.addNewComment();
			xmlBeansComment.setId(comment.getId());
			xmlBeansComment.setAuthorName(comment.getAuthor());
			xmlBeansComment.setResourcePath(comment.getCommentedFile().getFullPath().toPortableString());
			xmlBeansComment.setReviewID(comment.getReview().getId());
			xmlBeansComment.setCreationDate(comment.getCreationDate());
			xmlBeansComment.setLastModified(comment.getModificationDate());
			xmlBeansComment.setRecipient(comment.getRecipient());
			xmlBeansComment.setStatus(comment.getStatus());
			xmlBeansComment.setPriority(comment.getPriority());
			xmlBeansComment.setText(comment.getText());
			if (!comment.getReplies().isEmpty()) {
				getXmlBeansReplies(comment.getReplies(), xmlBeansComment.addNewReplies());
			}
		}
		return doc;
	}

	/**
	 * Converts a {@link List} of {@link Reply} objects to {@link org.agilereview.xmlSchema.author.ReplyDocument.Reply}
	 *  objects and adds them to the {@link Replies} object.
	 * @param replies The {@link Reply} objects to be converted.
	 * @param xmlBeansReplies The {@link Replies} object to which the 
	 * {@link org.agilereview.xmlSchema.author.ReplyDocument.Reply} objects are to be added to.
	 * @author Peter Reuter (04.04.2012)
	 */
	private void getXmlBeansReplies(List<Reply> replies, Replies xmlBeansReplies) {
		for (Reply reply : replies) {
			org.agilereview.xmlSchema.author.ReplyDocument.Reply xmlBeansReply = xmlBeansReplies.addNewReply();
			xmlBeansReply.setId(reply.getId());
			xmlBeansReply.setAuthor(reply.getAuthor());
			xmlBeansReply.setCreationDate(reply.getCreationDate());
			xmlBeansReply.setLastModified(reply.getModificationDate());
			xmlBeansReply.setText(reply.getText());
			if (!reply.getReplies().isEmpty()) {
				getXmlBeansReplies(reply.getReplies(), xmlBeansReply.addNewReplies());
			}
		}
	}

	/**
	 * @param review
	 * @return
	 */
	private ReviewDocument getXmlBeansReviewDocument(Review review) {
		ReviewDocument doc = ReviewDocument.Factory.newInstance();
		org.agilereview.xmlSchema.review.ReviewDocument.Review xmlBeansReview = doc.addNewReview();
		xmlBeansReview.setId(review.getId());
		xmlBeansReview.setStatus(review.getStatus());
		xmlBeansReview.setReferenceId(review.getReference());
		xmlBeansReview.setReferenceId(review.getResponsibility());
		xmlBeansReview.setDescription(review.getDescription());
		return doc;
	}

	/**
	 * @param file the {@link IFile} which will representing the saved {@link XmlTokenSource}
	 * @param doc the {@link XmlTokenSource} to save
	 */
	private void saveXmlDocument(IFile file, XmlTokenSource doc) {
		try {
			doc.save(file.getLocation().toFile(), new XmlOptions().setSavePrettyPrint());
		} catch (IOException e) {
			ExceptionHandler.notifyUser(e);
		}
	}
	
	///////////////////////////////////////
	// method of IPropertyChangeListener //
	///////////////////////////////////////

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(SOURCEFOLDER_PROPERTYNAME)) {
			this.idReviewMap.clear();
			initialize();
		}
	}
	
	//////////////////////////////////////
	// method of PropertyChangeListener //
	//////////////////////////////////////

	@Override
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		if (evt.getSource() instanceof Review) {
			Review review = (Review) evt.getSource();
			IFile reviewFile = getReviewFile(review.getId());
			ReviewDocument doc = getXmlBeansReviewDocument(review);
			saveXmlDocument(reviewFile, doc);
		} else if (evt.getSource() instanceof Comment || evt.getSource() instanceof Reply) {
			Comment comment;
			if (evt.getSource() instanceof Comment) {
				comment = (Comment) evt.getSource();
			} else {
				Object source = evt.getSource();
				while (!(source instanceof Comment)) {
					source = ((Reply)source).getParent();
				}
				comment = (Comment) source;
			}
			IFile commentFile = getCommentFile(comment.getReview().getId(), comment.getAuthor());
			CommentsDocument doc = getXmlBeansCommentsDocument(getComments(comment.getReview(), comment.getAuthor()));
			saveXmlDocument(commentFile, doc);
		}
	}
	
}
