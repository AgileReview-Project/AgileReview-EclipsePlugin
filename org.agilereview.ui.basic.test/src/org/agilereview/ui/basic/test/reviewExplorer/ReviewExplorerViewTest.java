/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.test.reviewExplorer;

import java.io.ByteArrayInputStream;
import java.util.Calendar;

import junit.framework.Assert;

import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Review;
import org.agilereview.core.external.storage.ReviewSet;
import org.agilereview.test.common.storage.external.StorageClientMock;
import org.agilereview.test.common.utils.TmpJavaProject;
import org.agilereview.ui.basic.commentSummary.CommentSummaryView;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotPerspective;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test class for class {@link CommentSummaryView}
 * @author Malte Brunnlieb (26.05.2012)
 */
public class ReviewExplorerViewTest {
    
    /**
     * SWTBot instance for UI-Tests
     */
    private final SWTWorkbenchBot bot = new SWTWorkbenchBot();
    
    @Rule
    public TmpJavaProject tmpJavaProject1 = new TmpJavaProject("TestProject1");
    @Rule
    public TmpJavaProject tmpJavaProject2 = new TmpJavaProject("TestProject2");
    
    /**
     * Setup method which will be executed before this test class will be executed at all
     * @author Malte Brunnlieb (27.05.2012)
     */
    @BeforeClass
    public static void setupTestClass() {
        PlatformUI.getPreferenceStore().setValue("org.agilereview.testrunner.active", true);
        PlatformUI.getPreferenceStore().setValue("org.agilereview.testrunner.mock.storage", true); //TODO if client can be set via preferences -> use this
    }
    
    /**
     * Teardown method which will be executed after all tests were executed
     * @author Malte Brunnlieb (27.05.2012)
     */
    @AfterClass
    public static void tearDownTestClass() {
        PlatformUI.getPreferenceStore().setValue("org.agilereview.testrunner.active", false);
        PlatformUI.getPreferenceStore().setValue("org.agilereview.testrunner.mock.storage", false); //TODO if client can be set via preferences -> use this
    }
    
    /**
     * Setup method which resets the workbench and activates the AgileReview perspective. Also the expected exception type will be reset to none.
     * @author Malte Brunnlieb (26.05.2012)
     * @throws CoreException
     */
    @Before
    public void setupTest() throws CoreException {
        bot.resetWorkbench();
        SWTBotPerspective perspective = bot.perspectiveById("org.agilereview.ui.basic.perspective");
        if (!perspective.isActive()) {
            perspective.activate();
        }
        // Create project
        tmpJavaProject1.createProject();
        tmpJavaProject2.createProject();
        // Wait for a second so everything is loaded
        //        try {
        //            Thread.sleep(2000);
        //        } catch (InterruptedException e) {
        //            // TODO Auto-generated catch block
        //            e.printStackTrace();
        //        }
    }
    
    @After
    public void tearDownTest() {
        //        try {
        //            ResourcesPlugin.getWorkspace().getRoot().delete(true, true, new NullProgressMonitor());
        //        } catch (CoreException e) {
        //            // TODO Auto-generated catch block
        //            e.printStackTrace();
        //        }
    }
    
    private SWTBotTree getTreeViewer() {
        SWTBotTree tree = null;
        int retryCount = 3;
        while (tree == null && retryCount > 0) {
            try {
                tree = bot.treeWithId("reTree");
            } catch (WidgetNotFoundException e) {
                // Wait a second, try again
                try {
                    Thread.sleep(1 * 1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                retryCount--;
            }
        }
        return tree;
    }
    
    private IFile createFileStructure(IJavaProject project, String[] folderArr, String fileName) {
        
        IContainer lastContainer = project.getProject();
        if (folderArr.length > 0) {
            for (int i = 0; i < folderArr.length; i++) {
                IFolder folder = lastContainer.getFolder(new Path(folderArr[i]));
                if (!folder.exists()) {
                    try {
                        folder.create(true, true, new NullProgressMonitor());
                    } catch (CoreException e) {
                        e.printStackTrace();
                    }
                }
                lastContainer = folder;
            }
        }
        IFile file = lastContainer.getFile(new Path(fileName));
        if (!file.exists()) {
            try {
                file.create(new ByteArrayInputStream("test 123".getBytes()), true, new NullProgressMonitor());
            } catch (CoreException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
    
    /**
     * Inserts a review and checks whether the review and corresponding files are shown correctly
     * @throws CoreException
     * @author Thilo Rauch (15.07.2012)
     */
    @Test
    public void testReviewShownWithCorrectLabels() throws CoreException {
        // setup test data
        IFile file = createFileStructure(tmpJavaProject1.getProject(), new String[] { "src" }, "test.java");
        
        Review r1 = new Review("r1");
        Comment c1 = new Comment("c1", "Alice", file, r1, Calendar.getInstance(), Calendar.getInstance(), "", 0, 0, "");
        r1.addComment(c1);
        
        ReviewSet reviews = new ReviewSet();
        reviews.add(r1);
        
        StorageClientMock.getInstance().setStorageContent(reviews);
        
        try {
            Thread.sleep(2 * 1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        
        SWTBotTree tree = getTreeViewer();
        
        // Now check structure
        String[] expectation = { "r1", "TestProject1", "src", "test.java" };
        SWTBotTreeItem[] items = tree.getAllItems();
        for (int i = 0; i < expectation.length; i++) {
            Assert.assertEquals(1, items.length);
            Assert.assertEquals(expectation[i], items[0].getText());
            items[0].expand();
            items = items[0].getItems();
        }
        Assert.assertEquals(0, items.length);
    }
    
    /**
     * Simulate Refactoring and see whether everything is shown correctly
     * @throws CoreException
     * @author Thilo Rauch (15.07.2012)
     */
    @Test
    public void simulateRefactoring() throws CoreException {
        // setup test data
        IFile file = createFileStructure(tmpJavaProject1.getProject(), new String[] { "src" }, "test.java");
        
        Review r1 = new Review("r1");
        Comment c1 = new Comment("c1", "Alice", file, r1, Calendar.getInstance(), Calendar.getInstance(), "", 0, 0, "");
        r1.addComment(c1);
        
        ReviewSet reviews = new ReviewSet();
        reviews.add(r1);
        
        StorageClientMock.getInstance().setStorageContent(reviews);
        
        try {
            Thread.sleep(2 * 1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        
        // Expand to file
        SWTBotTree tree = getTreeViewer();
        
        SWTBotTreeItem node = tree.expandNode("r1", "TestProject1", "src");
        Assert.assertEquals("test.java", node.getNode(0).getText());
        
        // Rename file
        IPath newpath = file.getFullPath().removeLastSegments(1).append("file123.java");
        file.move(newpath, true, new NullProgressMonitor());
        
        IFile newFile = createFileStructure(tmpJavaProject1.getProject(), new String[] { "src" }, "file123.java");
        
        Assert.assertEquals("file123.java", newFile.getName());
        c1.setCommentedFile(newFile);
        
        // Check change on comment
        Assert.assertEquals("file123.java", c1.getCommentedFile().getName());
        
        // Check that tree was refreshed
        Assert.assertEquals("file123.java", node.getNode(0).getText());
    }
    
    /**
     * Simulate closing a review and see whether everything is shown correctly
     * @throws CoreException
     * @author Thilo Rauch (15.07.2012)
     */
    @Test
    public void simulateClosingReview() throws CoreException {
        // setup test data
        IFile file = createFileStructure(tmpJavaProject1.getProject(), new String[] { "src" }, "test.java");
        
        final Review r1 = new Review("r1");
        Comment c1 = new Comment("c1", "Alice", file, r1, Calendar.getInstance(), Calendar.getInstance(), "", 0, 0, "");
        r1.addComment(c1);
        
        ReviewSet reviews = new ReviewSet();
        reviews.add(r1);
        
        StorageClientMock.getInstance().setStorageContent(reviews);
        
        try {
            Thread.sleep(2 * 1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        
        SWTBotTree tree = getTreeViewer();
        final SWTBotTreeItem item = tree.getTreeItem("r1");
        item.display.syncExec(new Runnable() {
            @Override
            public void run() {
                Image old = item.widget.getImage();
                
                r1.clearComments();
                r1.setIsOpen(false);
                
                item.expand();
                Assert.assertEquals(0, item.getItems().length);
                Assert.assertFalse(item.widget.getImage().equals(old));
            }
        });
    }
    
    /**
     * Checks for one review with multiple projects
     * @throws CoreException
     * @author Thilo Rauch (15.07.2012)
     */
    @Test
    public void testOneReviewWithDifferentProjects() throws CoreException {
        // setup test data
        Review r1 = new Review("r1");
        IFile file1 = createFileStructure(tmpJavaProject1.getProject(), new String[] { "folder1" }, "file1.java");
        Comment c1 = new Comment("c1", "Alice", file1, r1, Calendar.getInstance(), Calendar.getInstance(), "", 0, 0, "");
        r1.addComment(c1);
        
        IFile file2 = createFileStructure(tmpJavaProject2.getProject(), new String[] { "folder1" }, "file1.java");
        Comment c2 = new Comment("c2", "Alice", file2, r1, Calendar.getInstance(), Calendar.getInstance(), "", 0, 0, "");
        r1.addComment(c2);
        
        ReviewSet reviews = new ReviewSet();
        reviews.add(r1);
        
        StorageClientMock.getInstance().setStorageContent(reviews);
        
        try {
            Thread.sleep(2 * 1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        
        SWTBotTree tree = getTreeViewer();
        
        // Now check structure
        String[] expectationBefore = { "r1" };
        // Now check structure
        SWTBotTreeItem[] items = tree.getAllItems();
        for (int i = 0; i < expectationBefore.length; i++) {
            Assert.assertEquals(1, items.length);
            Assert.assertEquals(expectationBefore[i], items[0].getText());
            items[0].expand();
            items = items[0].getItems();
        }
        // Now check the two projects
        Assert.assertEquals(2, items.length);
        
        SWTBotTreeItem[] itemsBefore = items;
        String[] expectationAfter = { "folder1", "file1.java" };
        Assert.assertEquals("TestProject1", itemsBefore[0].getText());
        Assert.assertEquals("TestProject2", itemsBefore[1].getText());
        itemsBefore[0].expand();
        itemsBefore[1].expand();
        items = itemsBefore[0].getItems();
        for (int i = 0; i < expectationAfter.length; i++) {
            Assert.assertEquals(1, items.length);
            Assert.assertEquals(expectationAfter[i], items[0].getText());
            items[0].expand();
            items = items[0].getItems();
        }
        items = itemsBefore[0].getItems();
        for (int i = 0; i < expectationAfter.length; i++) {
            Assert.assertEquals(1, items.length);
            Assert.assertEquals(expectationAfter[i], items[0].getText());
            items[0].expand();
            items = items[0].getItems();
        }
    }
    
    /**
     * Checks for one review with multiple folder
     * @throws CoreException
     * @author Thilo Rauch (15.07.2012)
     */
    @Test
    public void testOneReviewWithDifferentFolder() throws CoreException {
        // setup test data
        Review r1 = new Review("r1");
        IFile file1 = createFileStructure(tmpJavaProject1.getProject(), new String[] { "folder1" }, "file1.java");
        Comment c1 = new Comment("c1", "Alice", file1, r1, Calendar.getInstance(), Calendar.getInstance(), "", 0, 0, "");
        r1.addComment(c1);
        
        IFile file2 = createFileStructure(tmpJavaProject1.getProject(), new String[] { "folder2" }, "file2.java");
        Comment c2 = new Comment("c2", "Alice", file2, r1, Calendar.getInstance(), Calendar.getInstance(), "", 0, 0, "");
        r1.addComment(c2);
        
        ReviewSet reviews = new ReviewSet();
        reviews.add(r1);
        
        StorageClientMock.getInstance().setStorageContent(reviews);
        
        try {
            Thread.sleep(2 * 1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        
        SWTBotTree tree = getTreeViewer();
        
        // Now check structure
        String[] expectation = { "r1", "TestProject1" };
        // Now check structure
        SWTBotTreeItem[] items = tree.getAllItems();
        for (int i = 0; i < expectation.length; i++) {
            Assert.assertEquals(1, items.length);
            Assert.assertEquals(expectation[i], items[0].getText());
            items[0].expand();
            items = items[0].getItems();
        }
        // Now check the two folder
        Assert.assertEquals(2, items.length);
        
        Assert.assertEquals("folder1", items[0].getText());
        items[0].expand();
        Assert.assertEquals(1, items[0].getItems().length);
        items[0].getItems()[0].expand();
        Assert.assertEquals("file1.java", items[0].getItems()[0].getText());
        
        Assert.assertEquals("folder2", items[1].getText());
        items[1].expand();
        Assert.assertEquals(1, items[1].getItems().length);
        items[1].getItems()[0].expand();
        Assert.assertEquals("file2.java", items[1].getItems()[0].getText());
    }
    
    /**
     * Checks for one review with multiple files
     * @throws CoreException
     * @author Thilo Rauch (15.07.2012)
     */
    @Test
    public void testOneReviewWithDifferentFiles() throws CoreException {
        // setup test data
        Review r1 = new Review("r1");
        IFile file1 = createFileStructure(tmpJavaProject1.getProject(), new String[] { "folder1" }, "file1.java");
        Comment c1 = new Comment("c1", "Alice", file1, r1, Calendar.getInstance(), Calendar.getInstance(), "", 0, 0, "");
        r1.addComment(c1);
        
        IFile file2 = createFileStructure(tmpJavaProject1.getProject(), new String[] { "folder1" }, "file2.java");
        Comment c2 = new Comment("c2", "Alice", file2, r1, Calendar.getInstance(), Calendar.getInstance(), "", 0, 0, "");
        r1.addComment(c2);
        
        ReviewSet reviews = new ReviewSet();
        reviews.add(r1);
        
        StorageClientMock.getInstance().setStorageContent(reviews);
        
        try {
            Thread.sleep(2 * 1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        
        SWTBotTree tree = getTreeViewer();
        
        // Now check structure
        String[] expectation = { "r1", "TestProject1", "folder1" };
        // Now check structure
        SWTBotTreeItem[] items = tree.getAllItems();
        for (int i = 0; i < expectation.length; i++) {
            Assert.assertEquals(1, items.length);
            Assert.assertEquals(expectation[i], items[0].getText());
            items[0].expand();
            items = items[0].getItems();
        }
        // Now check the to files
        Assert.assertEquals(2, items.length);
        Assert.assertEquals("file1.java", items[0].getText());
        Assert.assertEquals(0, items[0].getItems().length);
        Assert.assertEquals("file2.java", items[1].getText());
        Assert.assertEquals(0, items[1].getItems().length);
        
    }
    
    /**
     * Two different reviews with multiple files
     * @throws CoreException
     * @author Thilo Rauch (15.07.2012)
     */
    @Test
    public void testMultipleReviewsWithDifferentFiles() throws CoreException {
        // setup test data
        Review r1 = new Review("r1");
        IFile file1 = createFileStructure(tmpJavaProject1.getProject(), new String[] { "folder1" }, "file1.java");
        Comment c1 = new Comment("c1", "Alice", file1, r1, Calendar.getInstance(), Calendar.getInstance(), "", 0, 0, "");
        r1.addComment(c1);
        
        Review r2 = new Review("r2");
        IFile file2 = createFileStructure(tmpJavaProject1.getProject(), new String[] { "folder1" }, "file2.java");
        Comment c2 = new Comment("c2", "Alice", file2, r2, Calendar.getInstance(), Calendar.getInstance(), "", 0, 0, "");
        r2.addComment(c2);
        
        ReviewSet reviews = new ReviewSet();
        reviews.add(r1);
        reviews.add(r2);
        
        StorageClientMock.getInstance().setStorageContent(reviews);
        
        try {
            Thread.sleep(2 * 1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        
        SWTBotTree tree = getTreeViewer();
        
        // Now check structure
        String[] expectationR1 = { "TestProject1", "folder1", "file1.java" };
        String[] expectationR2 = { "TestProject1", "folder1", "file2.java" };
        
        // Check reviews
        Assert.assertEquals(2, tree.getAllItems().length);
        
        SWTBotTreeItem item = tree.getTreeItem("r1");
        item.expand();
        SWTBotTreeItem[] items = item.getItems();
        for (int i = 0; i < expectationR1.length; i++) {
            Assert.assertEquals(1, items.length);
            Assert.assertEquals(expectationR1[i], items[0].getText());
            items[0].expand();
            items = items[0].getItems();
        }
        Assert.assertEquals(0, items.length);
        
        item = tree.getTreeItem("r2");
        item.expand();
        items = item.getItems();
        for (int i = 0; i < expectationR2.length; i++) {
            Assert.assertEquals(1, items.length);
            Assert.assertEquals(expectationR2[i], items[0].getText());
            items[0].expand();
            items = items[0].getItems();
        }
        Assert.assertEquals(0, items.length);
    }
    
    /**
     * Tests that changes on the reviewset are reflected
     * @throws CoreException
     * @author Thilo Rauch (15.07.2012)
     */
    @Test
    public void testReviewInputChanged() {
        // setup test data
        IFile file = createFileStructure(tmpJavaProject1.getProject(), new String[] { "src" }, "test.java");
        
        Review r1 = new Review("r1");
        Comment c1 = new Comment("c1", "Alice", file, r1, Calendar.getInstance(), Calendar.getInstance(), "", 0, 0, "");
        r1.addComment(c1);
        
        ReviewSet reviews1 = new ReviewSet();
        reviews1.add(r1);
        
        StorageClientMock.getInstance().setStorageContent(reviews1);
        
        try {
            Thread.sleep(2 * 1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        
        SWTBotTree tree = getTreeViewer();
        
        // Check before changing
        Assert.assertEquals(1, tree.getAllItems().length);
        Assert.assertEquals("r1", tree.getAllItems()[0].getText());
        // Change input
        Review r2 = new Review("r2");
        Comment c2 = new Comment("c2", "Alice", file, r2, Calendar.getInstance(), Calendar.getInstance(), "", 0, 0, "");
        r2.addComment(c2);
        ReviewSet reviews2 = new ReviewSet();
        reviews2.add(r2);
        StorageClientMock.getInstance().setStorageContent(reviews2);
        try {
            Thread.sleep(2 * 1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        
        // Check for change
        Assert.assertEquals(1, tree.getAllItems().length);
        Assert.assertEquals("r2", tree.getAllItems()[0].getText());
    }
    
    /**
     * Tests that changes on the reviewset are reflected
     * @throws CoreException
     * @author Thilo Rauch (15.07.2012)
     */
    @Test
    public void testReviewSetChanged() {
        // setup test data
        IFile file = createFileStructure(tmpJavaProject1.getProject(), new String[] { "src" }, "test.java");
        
        Review r1 = new Review("r1");
        Comment c1 = new Comment("c1", "Alice", file, r1, Calendar.getInstance(), Calendar.getInstance(), "", 0, 0, "");
        r1.addComment(c1);
        
        ReviewSet reviews = new ReviewSet();
        reviews.add(r1);
        
        StorageClientMock.getInstance().setStorageContent(reviews);
        
        try {
            Thread.sleep(2 * 1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        
        SWTBotTree tree = getTreeViewer();
        
        // Check before changing
        Assert.assertEquals(1, tree.getAllItems().length);
        
        // Change review set
        Review r2 = new Review("r2");
        Comment c2 = new Comment("c2", "Alice", file, r2, Calendar.getInstance(), Calendar.getInstance(), "", 0, 0, "");
        r2.addComment(c2);
        reviews.add(r2);
        StorageClientMock.getInstance().setStorageContent(reviews);
        // StorageClientMock.getInstance().addReview(r2);
        
        // Check for change
        Assert.assertEquals(2, tree.getAllItems().length);
        
        // Change back
        StorageClientMock.getInstance().removeReview(r1);
        Assert.assertEquals(1, tree.getAllItems().length);
    }
    
    /**
     * Tests that changes on a review are reflected
     * @throws CoreException
     * @author Thilo Rauch (15.07.2012)
     */
    @Test
    public void testReviewChanged() throws CoreException {
        // setup test data
        IFile file = createFileStructure(tmpJavaProject1.getProject(), new String[] { "before" }, "test.java");
        
        Review r1 = new Review("r1");
        Comment c1 = new Comment("c1", "Alice", file, r1, Calendar.getInstance(), Calendar.getInstance(), "", 0, 0, "");
        r1.addComment(c1);
        
        ReviewSet reviews = new ReviewSet();
        reviews.add(r1);
        
        StorageClientMock.getInstance().setStorageContent(reviews);
        
        try {
            Thread.sleep(2 * 1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        
        SWTBotTree tree = getTreeViewer();
        
        // Review
        SWTBotTreeItem item = tree.getAllItems()[0];
        item.expand();
        
        // Project
        item = item.getItems()[0];
        item.expand();
        // Check before
        Assert.assertEquals(1, item.getItems().length);
        Assert.assertEquals("before", item.getItems()[0].getText());
        
        // Add new comment
        file = createFileStructure(tmpJavaProject1.getProject(), new String[] { "after" }, "test.java");
        Comment c2 = new Comment("c2", "Bob", file, r1, Calendar.getInstance(), Calendar.getInstance(), "", 0, 0, "");
        r1.addComment(c2);
        
        // Check for change
        Assert.assertEquals(2, item.getItems().length);
        
        // Change back
        r1.deleteComment(c1);
        Assert.assertEquals(1, tree.getAllItems().length);
        Assert.assertEquals("after", item.getItems()[0].getText());
        
    }
}
