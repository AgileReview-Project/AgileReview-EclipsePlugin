package org.agilereview.storage.xml.persistence;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.agilereview.common.exception.ExceptionHandler;
import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Review;
import org.agilereview.storage.xml.Activator;
import org.agilereview.storage.xml.conversion.Jaxb2Pojo;
import org.agilereview.storage.xml.exception.ConversionException;
import org.agilereview.storage.xml.exception.DataLoadingException;
import org.agilereview.xmlschema.author.Comments;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

/**
 * Methods for loading {@link Review}s and {@link Comment}s from filesystem
 * @author Peter Reuter (03.11.2013)
 */
public class XmlLoader {

	/**
	 * Load {@link org.agilereview.xmlschema.review.Review} from file
	 * @param reviewFile
	 * @return
	 * @throws ConversionException
	 * @throws DataLoadingException
	 * @author Peter Reuter (03.11.2013)
	 */
	private static org.agilereview.xmlschema.review.Review loadReview(IFile reviewFile) throws ConversionException, DataLoadingException {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(org.agilereview.xmlschema.review.Review.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			org.agilereview.xmlschema.review.Review jaxbReview = (org.agilereview.xmlschema.review.Review) jaxbUnmarshaller.unmarshal(reviewFile.getContents());
			
			return jaxbReview;
			
		} catch (JAXBException e) {
			throw new ConversionException(e);
		} catch (CoreException e) {
			throw new DataLoadingException(e);
		}
	}

	/**
	 * Load {@link Comments} from file
	 * @param authorFile
	 * @return
	 * @throws ConversionException
	 * @throws DataLoadingException
	 * @author Peter Reuter (03.11.2013)
	 */
	private static Comments loadComments(IFile authorFile) throws ConversionException, DataLoadingException {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Comments.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			Comments jaxbComments = (Comments) jaxbUnmarshaller.unmarshal(authorFile.getContents());
			
			return jaxbComments;
			
		} catch (JAXBException e) {
			throw new ConversionException(e);
		} catch (CoreException e) {
			throw new DataLoadingException(e);
		}
	}
	
	/**
	 * Loads all {@link org.agilereview.xmlSchema.review.ReviewDocument.Review} objects from all files available in the current source folder.
	 * @return A {@link List} of XmlBeans objects representing the review files.
	 * @author Peter Reuter (04.04.2012)
	 */
	private static List<org.agilereview.xmlschema.review.Review> loadAllJaxbReview() {
		List<org.agilereview.xmlschema.review.Review> result = new ArrayList<org.agilereview.xmlschema.review.Review>();
		try {
			IResource[] allFolders = SourceFolderManager.getCurrentReviewSourceProject().members();
			LinkedList<String> errors = new LinkedList<String>();
			for (IResource currFolder : allFolders) {
				if (currFolder instanceof IFolder) {
					String reviewId = SourceFolderManager.getReviewId(currFolder);
					IFile reviewFile = SourceFolderManager.getReviewFile(reviewId);
					try {
						org.agilereview.xmlschema.review.Review jaxbReview = XmlLoader.loadReview(reviewFile);
						result.add(jaxbReview);
					} catch (ConversionException e) {
						errors.add(reviewFile.getLocation().toOSString() + " ("+e.getLocalizedMessage()+")");
					} catch (DataLoadingException e) {
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
				ExceptionHandler.logAndNotifyUser(new DataLoadingException(message), Activator.PLUGIN_ID);
			}
		} catch (final CoreException e) {
			String message = "Error while reading data from current Review Source Project. The Review Source Project '"+SourceFolderManager.getCurrentReviewSourceProjectName()+"' does not exists or is closed.";
			ExceptionHandler.logAndNotifyUser(new DataLoadingException(message), Activator.PLUGIN_ID);
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
	private static org.agilereview.xmlschema.author.Comments loadAllJaxbComment(String reviewId) {
		org.agilereview.xmlschema.author.Comments result = new Comments();
		try {
			IFolder reviewFolder = SourceFolderManager.getReviewFolder(reviewId);
			if (reviewFolder != null) {
				LinkedList<String> errors = new LinkedList<String>();
				IResource[] resources = reviewFolder.members();
				for (IResource commentFile : resources) {
					if (SourceFolderManager.isAuthorFile(commentFile)) {
						try {
							org.agilereview.xmlschema.author.Comments doc = XmlLoader.loadComments((IFile) commentFile);
							result = doc;
						} catch (ConversionException e) {
							errors.add(commentFile.getLocation().toOSString() + " ("+e.getLocalizedMessage()+")");
						} catch (DataLoadingException e) {
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
					ExceptionHandler.logAndNotifyUser(new DataLoadingException(message), Activator.PLUGIN_ID);
				}
			}
		} catch (final CoreException e) {
			String message = "Error while reading data from current Review Source Project. The Review Source Project '"+SourceFolderManager.getCurrentReviewSourceProjectName()+"' does not exists or is closed.";
			ExceptionHandler.logAndNotifyUser(new DataLoadingException(message), Activator.PLUGIN_ID);
		}

		return result;
	}

	/**
	 * Loads all {@link Review} objects and adds a {@link PropertyChangeListener} to each of them.
	 * @author Peter Reuter (04.04.2012)
	 * @return the list of all {@link Review}s of the current Review Source Folder
	 */
	public static List<Review> loadReviews() {
		List<Review> reviews = new ArrayList<Review>();
		List<org.agilereview.xmlschema.review.Review> jaxbReviews = XmlLoader.loadAllJaxbReview();
		for (org.agilereview.xmlschema.review.Review xmlBeansReview : jaxbReviews) {
			Review review = Jaxb2Pojo.getReview(xmlBeansReview);
			reviews.add(review);
		}
		return reviews;
	}

	/**
	 * Loads all {@link Comment} objects of the {@link Review} given by its ID.
	 * @param review 
	 * @author Peter Reuter (04.04.2012)
	 */
	public static void loadComments(Review review) {
		if (review != null) {
			ArrayList<Comment> comments = new ArrayList<Comment>();
			org.agilereview.xmlschema.author.Comments jaxbComments = loadAllJaxbComment(review.getId());
			for (org.agilereview.xmlschema.author.Comment jaxbComment : jaxbComments.getComment()) {
				Comment comment = Jaxb2Pojo.getComment(review, jaxbComment);
				comments.add(comment);
			}
		}		
	}

}
