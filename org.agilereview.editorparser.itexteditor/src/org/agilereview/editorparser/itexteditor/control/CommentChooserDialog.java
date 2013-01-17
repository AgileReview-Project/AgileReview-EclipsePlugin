package org.agilereview.editorparser.itexteditor.control;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * The CommentChooserDialog is a small Dialog to choose between multiple comments
 */
public class CommentChooserDialog extends Composite implements Listener {
    
    /**
     * Drop down box to choose the right comment
     */
    private Combo comboChooseComment;
    /**
     * "ok" Button
     */
    private Button okButton;
    
    /**
     * boolean which indicates whether this wizard was canceled or not
     */
    private boolean boolSaved = false;
    /**
     * inserted reply text
     */
    private String strReplyText = "";
    /**
     * Tags from which the user can choose
     */
    private final String[] argsArr;
    
    /**
     * Creates a new dialog for entering replies
     * @param parent
     * @param style
     * @param args Comment tags from which to choose
     */
    public CommentChooserDialog(Shell parent, int style, String[] args) {
        super(parent, style);
        this.argsArr = args;
        initUI();
    }
    
    /**
     * Creates the UI
     */
    private void initUI() {
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        this.setLayout(gridLayout);
        
        comboChooseComment = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
        comboChooseComment.setItems(this.argsArr);
        comboChooseComment.select(0);
        comboChooseComment.setFocus();
        
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        gridData.horizontalSpan = 2;
        gridData.grabExcessVerticalSpace = true;
        gridData.grabExcessHorizontalSpace = true;
        comboChooseComment.setLayoutData(gridData);
        
        okButton = new Button(this, SWT.PUSH);
        okButton.setText("Ok");
        okButton.addListener(SWT.Selection, this);
        this.getShell().setDefaultButton(okButton);
        
        Button cancelButton = new Button(this, SWT.PUSH);
        cancelButton.setText("Cancel");
        cancelButton.addListener(SWT.Selection, this);
    }
    
    /**
     * Returns whether the Save-Button was pressed or not
     * @return true for save button, false for cancel button
     */
    public boolean getSaved() {
        return boolSaved;
    }
    
    /**
     * Return the reply's text
     * @return reply text
     */
    public String getReplyText() {
        return strReplyText;
    }
    
    /*
     * (non-Javadoc)
     * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
     */
    @Override
    public void handleEvent(Event event) {
        if (event.widget == okButton) {
            strReplyText = comboChooseComment.getText().trim();
            if (strReplyText.equals("")) {
                MessageDialog.openInformation(this.getShell(), "Information", "You have to fill out the comment text!");
            } else {
                boolSaved = true;
                getParent().dispose();
            }
        } else {
            boolSaved = false;
            getParent().dispose();
        }
    }
}
