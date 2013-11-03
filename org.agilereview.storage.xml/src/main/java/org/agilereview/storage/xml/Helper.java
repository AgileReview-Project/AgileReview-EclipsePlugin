package org.agilereview.storage.xml;

import java.util.ArrayList;

import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Review;

/**
 * Helper methods for the storage client
 * @author Peter Reuter (03.11.2013)
 */
public class Helper {
	
	/**
	 * Returns all {@link Comment} objects belonging to the given {@link Review} and author.
	 * @param review
	 * @param author
	 * @return An {@link ArrayList} of {@link Comment} objects.
	 * @author Peter Reuter (04.04.2012)
	 */
	public static ArrayList<Comment> getComments(Review review, String author) {
		ArrayList<Comment> result = new ArrayList<Comment>();
		for (Comment comment : review.getComments()) {
			if (comment.getAuthor().equals(author)) {
				result.add(comment);
			}
		}
		return result;
	}

}
