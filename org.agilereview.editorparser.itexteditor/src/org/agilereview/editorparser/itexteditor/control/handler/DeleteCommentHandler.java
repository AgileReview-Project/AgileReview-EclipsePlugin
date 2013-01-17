package org.agilereview.editorparser.itexteditor.control.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * Delete the comment the editor is currently "in"
 */
public class DeleteCommentHandler extends AbstractHandler {
    
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        
        //        IEditorPart editorPart = HandlerUtil.getActiveEditor(event);
        //        if (editorPart instanceof ITextEditor) {
        //            ISelection sel = ((ITextEditor) editorPart).getSelectionProvider().getSelection();
        //            if (sel instanceof ITextSelection) {
        //                ITextSelection textSel = (ITextSelection) sel;
        //                Position p = new Position(textSel.getOffset(), textSel.getLength());
        //                
        //                String[] tagTupel = new String[0];
        //                if (ViewControl.isOpen(CommentTableView.class)) {
        //                    tagTupel = CommentTableView.getInstance().getCommentsByPositionOfActiveEditor(p);
        //                }
        //                
        //                if (tagTupel.length > 0) {
        //                    String tag = "";
        //                    
        //                    if (tagTupel.length == 1) {
        //                        tag = tagTupel[0];
        //                    } else {
        //                        // More than one possible comment -> let the user choose
        //                        Shell shell = new Shell(HandlerUtil.getActiveShell(event));
        //                        shell.setText("Select a Comment");
        //                        CommentChooserDialog dialog = new CommentChooserDialog(shell, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM | SWT.SHELL_TRIM,
        //                                tagTupel);
        //                        dialog.setSize(200, 100);
        //                        shell.pack();
        //                        shell.open();
        //                        while (!shell.isDisposed()) {
        //                            if (!Display.getDefault().readAndDispatch()) Display.getDefault().sleep();
        //                        }
        //                        
        //                        if (dialog.getSaved()) {
        //                            tag = dialog.getReplyText();
        //                        } else {
        //                            return null;
        //                        }
        //                    }
        //                    
        //                    DataManager.getInstance().getComment(tag);
        //                    // Get the right comment
        //                    Comment c = ra.getComment(reviewId, author, commentId);
        //                    
        //                    // ask
        //                    if (!MessageDialog.openConfirm(HandlerUtil.getActiveShell(event), "Editor - Delete", "Are you sure you want to delete comment \""
        //                            + tag + "\"?")) { return null; }
        //                    // delete it
        //                    if (ViewControl.isOpen(CommentTableView.class)) {
        //                        CommentTableView.getInstance().deleteComment(c);
        //                    }
        //                    try {
        //                        ra.deleteComment(c);
        //                    } catch (IOException e) {
        //                        PluginLogger.logError(this.getClass().toString(), "execute", "IOException occured while deleting a comment in ReviewAccess: "
        //                                + c, e);
        //                    } catch (NoReviewSourceFolderException e) {
        //                        ExceptionHandler.handleNoReviewSourceFolderException();
        //                    }
        //                    // Refresh the Review Explorer
        //                    ViewControl.refreshViews(ViewControl.REVIEW_EXPLORER);
        //                    
        //                    // XXX Detail view?
        //                }
        //            }
        //        }
        
        return null;
    }
    
}