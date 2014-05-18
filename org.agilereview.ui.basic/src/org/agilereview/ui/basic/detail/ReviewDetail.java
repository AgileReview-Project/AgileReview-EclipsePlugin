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
import java.util.HashSet;

import org.agilereview.core.external.storage.Review;
import org.agilereview.ui.basic.Activator;
import org.agilereview.ui.basic.Activator.ISharedImages;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/**
 * 
 * @author Thilo Rauch (18.05.2014)
 */
public class ReviewDetail extends Composite implements SelectionListener {
    /**
     * TextArea to edit the person in charge of the given Review
     */
    private Text authorInstance;
    /**
     * Label to show the Review Name
     */
    private Text reviewInstance;
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
    
    /**
     * This set represents all components which should adapt the composite background color TODO: do this a better way?
     */
    private HashSet<Control> bgComponents = new HashSet<Control>();
    
    /**
     * Creates a new ReviewDetail Composite onto the given parent with the specified SWT styles
     * @param parent onto the ReviewDetail Composite will be added
     * @param style with which this Composite will be styled
     */
    protected ReviewDetail(Composite parent, int style) {
        super(parent, style);
        initUI();
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
        bgComponents.add(review);
        
        reviewInstance = new Text(this, SWT.WRAP);
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.horizontalSpan = numColumns - 1;
        reviewInstance.setLayoutData(gridData);
        reviewInstance.setEditable(false);
        bgComponents.add(reviewInstance);
        
        Label refId = new Label(this, SWT.PUSH);
        refId.setText("External reference: ");
        bgComponents.add(refId);
        
        reference = new Text(this, SWT.BORDER | SWT.SINGLE);
        gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalSpan = numColumns - 2;
        reference.setLayoutData(gridData);
        // TODO: Source Provider
        //        reference.addFocusListener(this);
        
        referenceButton = new Button(this, SWT.PUSH);
        referenceButton.setImage(Activator.getDefault().getImageRegistry().get(ISharedImages.BROWSE_WORLD));
        gridData = new GridData();
        gridData.horizontalAlignment = GridData.END;
        referenceButton.setLayoutData(gridData);
        referenceButton.addSelectionListener(this);
        referenceButton.setToolTipText("Interpret \"External reference\" as URI and open it");
        bgComponents.add(referenceButton);
        
        Label author = new Label(this, SWT.PUSH);
        author.setText("Responsibility: ");
        bgComponents.add(author);
        
        authorInstance = new Text(this, SWT.BORDER | SWT.SINGLE | SWT.WRAP);
        gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.horizontalSpan = numColumns - 1;
        authorInstance.setLayoutData(gridData);
        // TODO: Source Provider
        //        authorInstance.addFocusListener(this);
        //        authorInstance.addModifyListener(this);
        
        Label status = new Label(this, SWT.PUSH);
        status.setText("Status: ");
        bgComponents.add(status);
        
        statusDropDown = new Combo(this, SWT.DROP_DOWN | SWT.BORDER | SWT.PUSH);
        gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.horizontalSpan = numColumns - 1;
        statusDropDown.setLayoutData(gridData);
        // TODO: Source Provider
        //        statusDropDown.addFocusListener(this);
        //        statusDropDown.addModifyListener(this);
        bgComponents.add(statusDropDown);
        
        Sash sash = new Sash(this, SWT.PUSH);
        sash.setVisible(false);
        
        Label caption = new Label(this, SWT.PUSH);
        gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.horizontalSpan = numColumns;
        caption.setLayoutData(gridData);
        caption.setText("Description:");
        bgComponents.add(caption);
        
        txt = new StyledText(this, SWT.PUSH | SWT.V_SCROLL | SWT.BORDER);
        txt.setVisible(true);
        txt.setWordWrap(true);
        txt.setEditable(true);
        txt.setEnabled(true);
        // TODO: Source Provider
        //        txt.addFocusListener(this);
        //        txt.addModifyListener(this);
        gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        gridData.verticalSpan = 5;
        gridData.horizontalSpan = numColumns;
        gridData.grabExcessVerticalSpace = true;
        gridData.grabExcessHorizontalSpace = true;
        txt.setLayoutData(gridData);
        
        // TODO: What was that for?
        //        setPropertyConfigurations();
    }
    
    //    /*
    //     * (non-Javadoc)
    //     * @see de.tukl.cs.softech.agilereview.view.detail.AbstractDetail#saveChanges()
    //     */
    //    @Override
    //    protected boolean saveChanges() {
    //        boolean result = false;
    //        String newStr;
    //        if (!(newStr = this.authorInstance.getText().trim()).equals(this.editedObject.getPersonInCharge().getName())) {
    //            this.editedObject.getPersonInCharge().setName(newStr);
    //            result = true;
    //        }
    //        if (!(newStr = this.reference.getText().trim()).equals(this.editedObject.getReferenceId())) {
    //            this.editedObject.setReferenceId(newStr);
    //            result = true;
    //        }
    //        if (this.statusDropDown.getSelectionIndex() != this.editedObject.getStatus()) {
    //            this.editedObject.setStatus(this.statusDropDown.getSelectionIndex());
    //            result = true;
    //        }
    //        if (!(newStr = this.txt.getText().trim()).equals(this.editedObject.getDescription())) {
    //            this.editedObject.setDescription(super.convertLineBreaks(newStr));
    //            result = true;
    //        }
    //        return result;
    //    }
    
    /*
     * (non-Javadoc)
     * @see de.tukl.cs.softech.agilereview.view.detail.AbstractDetail#setFocus()
     */
    @Override
    public boolean setFocus() {
        return this.txt.setFocus();
    }
    
    //    /**
    //     * Sets the levels for the status and priority configuration of a comment.
    //     */
    //    private void setPropertyConfigurations() {
    //        PropertiesManager pm = PropertiesManager.getInstance();
    //        String value = pm.getInternalProperty(PropertiesManager.INTERNAL_KEYS.REVIEW_STATUS);
    //        String[] levels = value.split(",");
    //        statusDropDown.removeAll();
    //        for (int i = 0; i < levels.length; i++) {
    //            statusDropDown.add(levels[i]);
    //        }
    //    }
    
    /*
     * (non-Javadoc)
     * @see de.tukl.cs.softech.agilereview.view.detail.AbstractDetail#fillContents(java.lang.Object)
     */
    protected void fillContents(Review review) {
        if (review != null) {
            // TODO: What was that for?
            //            this.backupObject = (Review) review.copy();
            //            this.editedObject = review;
            
            this.reference.setText(review.getReference());
            this.authorInstance.setText(review.getResponsibility());
            this.authorInstance.setToolTipText(review.getResponsibility());
            this.reviewInstance.setText(review.getName());
            this.reviewInstance.setToolTipText(review.getName());
            
            if (review.getDescription() != null) {
                this.txt.setText(review.getDescription());
            } else {
                this.txt.setText("");
            }
            statusDropDown.select(review.getStatus());
        }
        // TODO: What was that for?
        //        //set revertable to false because it was set from the ModificationListener while inserting inital content
        //        ISourceProviderService isps = (ISourceProviderService) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getService(
        //                ISourceProviderService.class);
        //        SourceProvider sp = (SourceProvider) isps.getSourceProvider(SourceProvider.REVERTABLE);
        //        sp.setVariable(SourceProvider.REVERTABLE, false);
    }
    
    @Override
    public void widgetSelected(SelectionEvent e) {
        try {
            URI uri = new URI(this.reference.getText());
            if (Desktop.isDesktopSupported()) {
                if (Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(uri);
                }
            } else {
                // TODO Do some Logging
                // PluginLogger.logWarning(this.getClass().toString(), "widgetSelected", "\"java.awt.Desktop\" not supported by OS");
                MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Not supported",
                        "Your operating system does not support this functionality.");
            }
        } catch (Exception ex) {
            // TODO: DO some logging?
            //            PluginLogger.logError(this.getClass().toString(), "widgetSelected", "Can not open \"" + this.reference.getText()
            //                    + "\": It may not be a valid URI", ex);
            MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Invalid URI",
                    "External Reference is an unvalid URI:\n" + ex.getLocalizedMessage());
        }
    }
    
    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
        widgetSelected(e);
    }
    
    // TODO: Implement different colors
    //    @Override
    //    protected Color determineBackgroundColor() {
    //        String prop = PropertiesManager.getInstance().getInternalProperty(PropertiesManager.INTERNAL_KEYS.DEFAULT_REVIEW_COLOR);
    //        String[] rgb = prop.split(",");
    //        return new Color(PlatformUI.getWorkbench().getDisplay(), Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
    //    }
}
