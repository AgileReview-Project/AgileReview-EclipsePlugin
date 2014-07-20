/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.detail;

import java.awt.Desktop;
import java.net.URI;

import org.agilereview.core.external.storage.Review;
import org.agilereview.ui.basic.Activator;
import org.agilereview.ui.basic.Activator.ISharedImages;
import org.agilereview.ui.basic.tools.CommentReviewProperties;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Thilo Rauch (18.05.2014)
 */
public class ReviewDetail extends AbstractDetail<Review> implements SelectionListener {
    /**
     * TextArea to edit the person in charge of the given Review
     */
    private Text responsibilityText;
    /**
     * Label to show the Review Name
     */
    private Text reviewNameText;
    /**
     * ComboBox to provide a choice for the Review status
     */
    private Combo statusDropDown;
    /**
     * TextBox to represent the Comment description in a modifiable way
     */
    private StyledText txt;
    /**
     * TextArea to edit an external reference to this Review
     */
    private Text reference;
    /**
     * Button for opening the external reference in a browser
     */
    private Button referenceButton;
    
    private static final Logger LOG = LoggerFactory.getLogger(ReviewDetail.class);
    
    //    /**
    //     * The review that is shown. Keep it removing this instance as {@link PropertyChangeListener} when input is changed.
    //     */
    //    private Review review;
    //    
    //    /**
    //     * This set represents all components which should adapt the composite background color TODO: do this a better way?
    //     */
    //    private final HashSet<Control> bgComponents = new HashSet<Control>();
    
    /**
     * Creates a new ReviewDetail Composite onto the given parent with the specified SWT styles
     * @param parent onto the ReviewDetail Composite will be added
     * @param style with which this Composite will be styled
     */
    protected ReviewDetail(Composite parent, int style) {
        super(parent, style);
    }
    
    /**
     * Create the UI *
     * @author Thilo Rauch (18.05.2014)
     */
    protected void initUI() {
        GridLayout gridLayout = new GridLayout();
        int numColumns = 3;
        gridLayout.numColumns = numColumns;
        this.setLayout(gridLayout);
        
        Label review = new Label(this, SWT.PUSH);
        review.setText("Review: ");
        //        this.bgComponents.add(review);
        
        this.reviewNameText = new Text(this, SWT.WRAP);
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.horizontalSpan = numColumns - 1;
        this.reviewNameText.setLayoutData(gridData);
        this.reviewNameText.setEditable(false);
        //        this.bgComponents.add(this.reviewInstance);
        
        Label refId = new Label(this, SWT.PUSH);
        refId.setText("External reference: ");
        //        this.bgComponents.add(refId);
        
        this.reference = new Text(this, SWT.BORDER | SWT.SINGLE);
        gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalSpan = numColumns - 2;
        this.reference.setLayoutData(gridData);
        this.reference.addFocusListener(this);
        
        this.referenceButton = new Button(this, SWT.PUSH);
        this.referenceButton.setImage(Activator.getDefault().getImageRegistry().get(ISharedImages.BROWSE_WORLD));
        gridData = new GridData();
        gridData.horizontalAlignment = GridData.END;
        this.referenceButton.setLayoutData(gridData);
        this.referenceButton.addSelectionListener(this);
        this.referenceButton.setToolTipText("Interpret \"External reference\" as URI and open it");
        //        this.bgComponents.add(this.referenceButton);
        
        Label author = new Label(this, SWT.PUSH);
        author.setText("Responsibility: ");
        //        this.bgComponents.add(author);
        
        this.responsibilityText = new Text(this, SWT.BORDER | SWT.SINGLE | SWT.WRAP);
        gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.horizontalSpan = numColumns - 1;
        this.responsibilityText.setLayoutData(gridData);
        this.responsibilityText.addFocusListener(this);
        
        Label status = new Label(this, SWT.PUSH);
        status.setText("Status: ");
        //        this.bgComponents.add(status);
        
        this.statusDropDown = new Combo(this, SWT.DROP_DOWN | SWT.BORDER | SWT.PUSH);
        gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.horizontalSpan = numColumns - 1;
        this.statusDropDown.setLayoutData(gridData);
        this.statusDropDown.addFocusListener(this);
        //        this.bgComponents.add(this.statusDropDown);
        
        Sash sash = new Sash(this, SWT.PUSH);
        sash.setVisible(false);
        
        Label caption = new Label(this, SWT.PUSH);
        gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.horizontalSpan = numColumns;
        caption.setLayoutData(gridData);
        caption.setText("Description:");
        //        this.bgComponents.add(caption);
        
        this.txt = new StyledText(this, SWT.PUSH | SWT.V_SCROLL | SWT.BORDER);
        this.txt.setVisible(true);
        this.txt.setWordWrap(true);
        this.txt.setEditable(true);
        this.txt.setEnabled(true);
        this.txt.addFocusListener(this);
        
        gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        gridData.verticalSpan = 5;
        gridData.horizontalSpan = numColumns;
        gridData.grabExcessVerticalSpace = true;
        gridData.grabExcessHorizontalSpace = true;
        this.txt.setLayoutData(gridData);
        
        setPropertyConfigurations();
    }
    
    /**
     * Sets the levels for the status and priority configuration of a comment.
     */
    private void setPropertyConfigurations() {
        CommentReviewProperties commentReviewProps = new CommentReviewProperties();
        String[] levels = commentReviewProps.getReviewStatuses();
        statusDropDown.removeAll();
        for (int i = 0; i < levels.length; i++) {
            statusDropDown.add(levels[i]);
        }
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.ui.basic.detail.AbstractDetail#fillContents()
     * @author Thilo Rauch (20.07.2014)
     */
    @Override
    protected void fillContents() {
        Review review = this.getDetailObject();
        if (review != null) {
            updateUiData(review);
        }
    }
    
    // TODO: Only extra method for background changes (later)
    private void updateUiData(Review review) {
        if (isDisposed()) return;
        this.reference.setText(review.getReference());
        this.responsibilityText.setText(review.getResponsibility());
        this.responsibilityText.setToolTipText(review.getResponsibility());
        this.reviewNameText.setText(review.getName());
        this.reviewNameText.setToolTipText(review.getName());
        
        if (review.getDescription() != null) {
            this.txt.setText(review.getDescription());
        } else {
            this.txt.setText("");
        }
        this.statusDropDown.select(review.getStatus());
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.ui.basic.detail.AbstractDetail#saveChanges()
     * @author Thilo Rauch (20.07.2014)
     */
    @Override
    protected void saveChanges() {
        // Save author
        if (isDisposed()) return;
        this.getDetailObject().setResponsibility(this.responsibilityText.getText());
        // Save Reference
        this.getDetailObject().setReference(this.reference.getText());
        // Save status
        this.getDetailObject().setStatus(this.statusDropDown.getSelectionIndex());
        // Save text
        this.getDetailObject().setDescription(this.txt.getText());
    }
    
    /*
     * (non-Javadoc)
     * @see de.tukl.cs.softech.agilereview.view.detail.AbstractDetail#setFocus()
     */
    @Override
    public boolean setFocus() {
        if (!txt.isDisposed())
            return this.txt.setFocus();
        else
            return false;
    }
    
    //    /*
    //     * (non-Javadoc)
    //     * @see de.tukl.cs.softech.agilereview.view.detail.AbstractDetail#fillContents(java.lang.Object)
    //     */
    //    protected void fillContents(Review review) {
    //        if (review != null) {
    //            // TODO: What was that for?
    //            //            this.backupObject = (Review) review.copy();
    //            //            this.editedObject = review;
    //            
    //            if (this.review != null) {
    //                this.review.removePropertyChangeListener(this);
    //            }
    //            this.review = review;
    //            
    //            updateUiData(review);
    //            
    //            review.addPropertyChangeListener(this);
    //        }
    //        
    //    }
    
    @Override
    public void widgetSelected(SelectionEvent e) {
        try {
            URI uri = new URI(this.reference.getText());
            if (Desktop.isDesktopSupported()) {
                if (Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(uri);
                }
            } else {
                LOG.warn("Show Reference in browser requested, but \"java.awt.Desktop\" not supported by OS.");
                MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Not supported",
                        "Your operating system does not support this functionality.");
            }
        } catch (Exception ex) {
            LOG.warn("Can not open reference {}: It may not be a valid URI", this.reference.getText(), ex);
            MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Invalid URI",
                    "External Reference is an unvalid URI:\n" + ex.getLocalizedMessage());
        }
    }
    
    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
        widgetSelected(e);
    }
    
    // TODO: Refresh background changes for Findbugs-Integration
    //    /* (non-Javadoc)
    //     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
    //     * @author Peter Reuter (14.06.2014)
    //     */
    //    @Override
    //    public void propertyChange(PropertyChangeEvent arg0) {
    //        Display.getDefault().asyncExec(new Runnable() {
    //            
    //            @Override
    //            public void run() {
    //                updateUiData(ReviewDetail.this.review);
    //            }
    //            
    //        });
    //    }
    
    // TODO: Implement different colors
    //    @Override
    //    protected Color determineBackgroundColor() {
    //        String prop = PropertiesManager.getInstance().getInternalProperty(PropertiesManager.INTERNAL_KEYS.DEFAULT_REVIEW_COLOR);
    //        String[] rgb = prop.split(",");
    //        return new Color(PlatformUI.getWorkbench().getDisplay(), Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
    //    }
}
