package org.agilereview.storage.xml;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.agilereview.core.external.definition.IStorageClient;
import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Reply;
import org.agilereview.core.external.storage.Review;
import org.agilereview.storage.xml.conversion.PojoConversion;
import org.agilereview.storage.xml.conversion.XmlBeansConversion;
import org.agilereview.storage.xml.exception.DataLoadingException;
import org.agilereview.storage.xml.exception.ExceptionHandler;
import org.agilereview.xmlSchema.author.CommentsDocument;
import org.agilereview.xmlSchema.review.ReviewDocument;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlTokenSource;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
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
	private static final String ID = "org.agilereview.storage.xml.XmlStorageClient";
	
	/**
	 * Name of the property which stores the name of the current source folder
	 */
	private static final String SOURCEFOLDER_PROPERTYNAME = "source_folder";
	/**
	 * Name of the property which stores the names of the open reviews as a comma separated list.
	 */
	private static final String OPENREVIEWS_PROPERTYNAME = "openReviews";

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
	@SuppressWarnings("deprecation")
	private void initialize() {
		loadReviews();
		// TODO load comments of open reviews.
		List<String> reviewIds = Arrays.asList(Activator.getDefault().getPluginPreferences().getString(OPENREVIEWS_PROPERTYNAME).split(","));
		loadComments(reviewIds);
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
			IFolder reviewFolder = SourceFolderManager.getReviewFolder(reviewId);
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
	
	////////////////////////////////////////////////////////////////////
	// methods for loading/storing reviews and comments from/to files //
	////////////////////////////////////////////////////////////////////

	/**
	 * Loads all {@link Comment} objects of the {@link Review} given by its ID.
	 * @param reviewId The ID of the {@link Review}.
	 * @author Peter Reuter (04.04.2012)
	 */
	private void loadComments(String reviewId) {
		List<org.agilereview.xmlSchema.author.CommentDocument.Comment> xmlBeansComments = loadAllXmlBeansComment(reviewId);
		for (org.agilereview.xmlSchema.author.CommentDocument.Comment xmlBeansComment : xmlBeansComments) {
			Review review = this.idReviewMap.get(xmlBeansComment.getReviewID());
			Comment comment = XmlBeansConversion.getComment(review, xmlBeansComment);
			this.idReviewMap.get(reviewId).addComment(comment);
			this.idCommentMap.put(comment.getId(), comment);
			comment.setReplies(XmlBeansConversion.getReplyList(comment, xmlBeansComment.getReplies().getReplyArray()));
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
			Review review = XmlBeansConversion.getReview(xmlBeansReview);
			this.idReviewMap.put(review.getId(), review);
			review.addPropertyChangeListener(this);
		}
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
			IFile reviewFile = SourceFolderManager.getReviewFile(review.getId());
			ReviewDocument doc = PojoConversion.getXmlBeansReviewDocument(review);
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
			IFile commentFile = SourceFolderManager.getCommentFile(comment.getReview().getId(), comment.getAuthor());
			CommentsDocument doc = PojoConversion.getXmlBeansCommentsDocument(getComments(comment.getReview(), comment.getAuthor()));
			saveXmlDocument(commentFile, doc);
		}
	}
	
}
