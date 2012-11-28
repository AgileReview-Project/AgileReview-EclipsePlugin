package org.agilereview.storage.xml.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.agilereview.storage.xml.Activator;
import org.agilereview.storage.xml.SourceFolderManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.junit.AfterClass;
import org.junit.Test;

public class SourceFolderManagerTest {

	private static IProject sourcefolder = null;
	
	private static final String reviewText = "Test review";
	private static final String reviewReference = "http://www.google.de/";
	private static final String reviewId = "r1";
	private static final String rtext = "Reply text";
	private static final String ctext = "Foobar Blubber....";
	private static final int status = 1;
	private static final int prio = 1;
	private static final String recipient = "Malte";
	private static final Calendar date = Calendar.getInstance();
	private static final String author = "Piet";
	private static final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path("/org.agilereview.storage.xml/src/org/agilereview/storage/xml/conversion/PojoConversion.java"));
	private static final String[] commentIds = { "c0", "c1" };
	private static final String[] replyIds = { "r0", reviewId};

	private IFolder f;

	private IFile rf;

	private IFile af;
	
	@Test
	public void testCreateAndOpenSourceFolder() {
		createSourceFolder();
		
		assertTrue(sourcefolder.exists());
	}
	
	@Test
	public void testSetCurrentSourceFolderProject() throws CoreException {
		initCurrentSourceFolder();
		
		assertEquals(SourceFolderManager.getCurrentSourceFolderName(), sourcefolder.getName());
		assertArrayEquals(sourcefolder.getDescription().getNatureIds(), new String[] { SourceFolderManager.AGILEREVIEW_NATURE, SourceFolderManager.AGILEREVIEW_ACTIVE_NATURE });
	}
	
	@Test
	public void testGetReviewFolder() {
		initCurrentSourceFolder();
		
		f = SourceFolderManager.getReviewFolder(reviewId);
		
		assertTrue(f.exists());
	}
	
	@Test
	public void testGetReviewFile() {
		initCurrentSourceFolder();
				
		rf = SourceFolderManager.getReviewFile(reviewId);
		
		assertTrue(rf.exists());
	}
	
	@Test
	public void testGetCommentFile() {
		initCurrentSourceFolder();
				
		af = SourceFolderManager.getCommentFile(reviewId, author);
		
		assertTrue(af.exists());
	}
	
	@Test
	public void testDeleteCommentFile() {
		initCurrentSourceFolder();
		af = SourceFolderManager.getCommentFile(reviewId, author);
		
		SourceFolderManager.deleteCommentFile(reviewId, author);
		
		assertFalse(af.exists());
	}
	
	@Test
	public void testDeleteReviewContent() {
		initCurrentSourceFolder();
		f = SourceFolderManager.getReviewFolder(reviewId);
		rf = SourceFolderManager.getReviewFile(reviewId);
		af = SourceFolderManager.getCommentFile(reviewId, author);
		
		SourceFolderManager.deleteReviewContents(reviewId);
		
		assertFalse(af.exists());
		assertFalse(rf.exists());
		assertFalse(f.exists());
	}
	
	@AfterClass
	public static void tearDown() throws CoreException {
		if (!(sourcefolder == null) && sourcefolder.exists()) {
			sourcefolder.delete(true, null);
		}
	}

	/**
	 * 
	 * @author Peter Reuter (26.11.2012)
	 */
	private void initCurrentSourceFolder() {
		createSourceFolder();
		IEclipsePreferences preferencesNode = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
		preferencesNode.put("source_folder", "AgileReviews");
		SourceFolderManager.setCurrentSourceFolderProject();
	}

	/**
	 * 
	 * @author Peter Reuter (26.11.2012)
	 */
	private void createSourceFolder() {
		SourceFolderManager.createAndOpenSourceFolder("AgileReviews");
		sourcefolder = ResourcesPlugin.getWorkspace().getRoot().getProject("AgileReviews");
	}
	
}
