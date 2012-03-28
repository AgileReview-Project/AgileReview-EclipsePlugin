package org.agilereview.ui.basic.reviewExplorer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.agilereview.core.external.definition.IReviewDataReceiver;
import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Review;
import org.agilereview.ui.basic.reviewExplorer.model.REContentProvider;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.services.ISourceProviderService;

/**
 * The Review Explorer is the view which shows all reviews as well as 
 * the files and folder which are commented in the corresponding reviews.
 * 
 * @author Thilo Rauch (28.03.2012)
 */
public class ReviewExplorerView extends ViewPart implements IReviewDataReceiver, IDoubleClickListener {

	/**
	 * All review data
	 */
	private List<Review> globalData = new ArrayList<Review>();
	
	/**
	 * The tree for showing the reviews
	 */
	private TreeViewer treeViewer;	
	/**
	 * Action for opening the files displayed in the tree viewer on double-click
	 */
	private REOpenAction openFileAction;
	/**
	 * Properties manager for repeated access 
	 */
	private PropertiesManager props = PropertiesManager.getInstance();	
	/**
	 * Current Instance used by the ViewPart
	 */
	private static ReviewExplorerView instance;
	
	/**
	 * Returns the current instance of the ReviewExplorer
	 * @return current instance
	 */
	public static ReviewExplorerView getInstance() {
		return instance;
	}

	
	/* (non-Javadoc)
	 * @see org.agilereview.core.external.definition.IReviewDataReceiver#setReviewData(java.util.List)
	 * @author Thilo Rauch (28.03.2012)
	 */
	@Override
	public void setReviewData(List<Review> reviews) {
		this.globalData = reviews;
		// delete old resource markers
		ResourcesPlugin.getWorkspace().getRoot().deleteMarkers(REContentProvider.AGILE_REVIEW_MARKER_ID, false, IResource.DEPTH_INFINITE);
		// now prepare the new markers
		for (Review r : globalData) {
			for (Comment c: r.getComments()) {
				c.getCommentedFile().createMarker(REContentProvider.AGILE_REVIEW_MARKER_ID);
			}
		}
	}
	
	
	@Override
	public void createPartControl(Composite parent) {
		instance = this;
		
		// Create the treeview MULTI, H_SCROLL, V_SCROLL, and BORDER
		treeViewer = new TreeViewer(parent);
		treeViewer.setContentProvider(new REContentProvider());
		treeViewer.setLabelProvider(new RELabelProvider());
		treeViewer.setComparator(new REViewerComparator());
		treeViewer.setInput(globalData);
		treeViewer.addSelectionChangedListener(ViewControl.getInstance());
		refreshInput();
				
		openFileAction = new REOpenAction(this.getSite().getPage(), treeViewer);
				
		treeViewer.addDoubleClickListener(this);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, Activator.PLUGIN_ID+".ReviewExplorer");
		
		// Create a popup menu
		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(treeViewer.getControl());
		// Set the MenuManager
		treeViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuManager, treeViewer);
		getSite().setSelectionProvider(treeViewer);
	
		// register view
		ViewControl.registerView(this.getClass());
	}
	
	/**
	 * Refreshes the tree viewer. Also expands all previously expanded nodes afterwards.
	 */
	public void refresh()
	{
		PluginLogger.log(this.getClass().toString(), "refresh", "Refreshing the ReviewExplorer viewer (without reloading the input)");
		this.treeViewer.getControl().setRedraw(false);
		Object[] expandedElements = this.treeViewer.getExpandedElements();
		this.treeViewer.refresh();
		
		for (Object o : expandedElements)
		{
			this.treeViewer.expandToLevel(o, 1);
		}
		this.treeViewer.getControl().setRedraw(true);
		this.treeViewer.getControl().redraw();
	}
	
	
	/**
	 * Sets the input of the ReviewExplorer completely new
	 */
	public void refreshInput()
	{
		PluginLogger.log(this.getClass().toString(), "refreshInput", "Refreshing the ReviewExplorer viewer (with reloading the input)");
		// Save previous selection
		ISelection selection = this.treeViewer.getSelection();
		// Save expansion state
		this.treeViewer.getControl().setRedraw(false);
		TreePath[] expandedElements = this.treeViewer.getExpandedTreePaths();
		
		// Refresh the input
		this.root.clear();
		for (Review r : RA.getAllReviews())
		{
			MultipleReviewWrapper currWrap = new MultipleReviewWrapper(r, r.getId());
			// Check if review is "open"
			currWrap.setOpen(props.isReviewOpen(r.getId()));
			root.addReview(currWrap);
		}
		
		// Expand nodes again
		this.treeViewer.refresh();
		for (Object o : expandedElements)
		{
			this.treeViewer.expandToLevel(o, 1);
		}
		this.treeViewer.getControl().setRedraw(true);
		this.treeViewer.getControl().redraw();

		//Reset selection
		this.treeViewer.setSelection(selection, true);
	}
	
	/**
	 * Adds the given review to the viewer
	 * @param r the new Review
	 */
	public void addReview(Review r) {
		MultipleReviewWrapper rWrap = new MultipleReviewWrapper(r, r.getId());
		// When a new review is created this should be open an activated
		rWrap.setOpen(true);
		this.props.addToOpenReviews(r.getId());
		PropertiesManager.getPreferences().setValue(PropertiesManager.EXTERNAL_KEYS.ACTIVE_REVIEW, r.getId());
		
		root.addReview(rWrap);
		this.refresh();
		this.treeViewer.setSelection(new StructuredSelection(rWrap), true);
	}
	
	/**
	 * Deletes the given review from the viewer
	 * @param r
	 */
	public void deleteReview(MultipleReviewWrapper r) {
		treeViewer.getTree().setRedraw(false);
		root.deleteReview(r);
		this.refresh();
		treeViewer.getTree().setRedraw(true);
	}
	
	/**
	 * Expands all sub nodes of the passed node 
	 * @param selection node which should be expanded
	 */
	public void expandAllSubNodes(AbstractMultipleWrapper selection) {
		treeViewer.expandToLevel(selection, TreeViewer.ALL_LEVELS);
	}
	
	/**
	 * Collapses all sub nodes of the passed node 
	 * @param selection node which should be expanded
	 */
	public void collapseAllSubNodes(AbstractMultipleWrapper selection) {
		treeViewer.collapseToLevel(selection, TreeViewer.ALL_LEVELS);
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
	 */
	@Override
	public void doubleClick(DoubleClickEvent event) {
		PluginLogger.log(this.getClass().toString(), "doubleClick", "Doubleclick in ReviewExplorer detected");
		if(openFileAction.isEnabled()){
			openFileAction.run();
		}
		ISelection sel = event.getSelection();
		if (sel instanceof IStructuredSelection)
		{
			Object o = ((IStructuredSelection)sel).getFirstElement();
			
			if (o instanceof MultipleReviewWrapper)
			{
				String command = "";
				try {
					// If the review is closed -> open it
					if (props.isReviewOpen(((MultipleReviewWrapper)o).getReviewId()))
					{
						// Check if already active, then expand, else activate
						String activeReview = PropertiesManager.getPreferences().getString(PropertiesManager.EXTERNAL_KEYS.ACTIVE_REVIEW);
						if (activeReview.equals(((MultipleReviewWrapper)o).getReviewId())) {
							if (treeViewer.getExpandedState(o)) {
								treeViewer.collapseToLevel(o, 1);
							} else {
								treeViewer.expandToLevel(o, 1);
							}
						} else {
							// Review is open -> activate it
							command = "de.tukl.cs.softech.agilereview.views.reviewexplorer.activate";
							// Execute activation command
							IHandlerService handlerService = (IHandlerService)getSite().getService(IHandlerService.class);
							handlerService.executeCommand(command, null);	
						}
					} else {
						command = "de.tukl.cs.softech.agilereview.views.reviewexplorer.openClose";
						// Execute open/close command
						IHandlerService handlerService = (IHandlerService)getSite().getService(IHandlerService.class);
						handlerService.executeCommand(command, null);
					}
				} catch (ExecutionException e) {
					PluginLogger.logError(this.getClass().toString(), "doubleClick", "Problems occured executing command \""+command+"\"", e);
				} catch (NotDefinedException e) {
					PluginLogger.logError(this.getClass().toString(), "doubleClick", "Command \""+command+"\" is not defined", e);
				} catch (NotEnabledException e) {
					PluginLogger.logError(this.getClass().toString(), "doubleClick", ""+command+"\" is not enabled", e);
				} catch (NotHandledException e) {
					PluginLogger.logError(this.getClass().toString(), "doubleClick", "Command \""+command+"\" is not handled", e);
				}
			} else {
				// On Double-Click there can only be one item selected
				if (treeViewer.getExpandedState(o)) {
					treeViewer.collapseToLevel(o, 1);
				} else {
					treeViewer.expandToLevel(o, 1);
				}
			}		
		}
	}
	
	/**
	 * Validates the ReviewExplorer selection in order to update the CONTAINS_CLOSED_REVIEW variable of the {@link SourceProvider}
	 */
	public void validateExplorerSelection() {
		selectionChanged(new SelectionChangedEvent(this.treeViewer, this.treeViewer.getSelection()));
	}
	
	/**
	 * Will be called by the {@link ViewControl} when the selection was changed and changes
	 * @param event
	 * @see de.tukl.cs.softech.agilereview.views.ViewControl#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		if(event.getSelection() instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) event.getSelection();
			Iterator<?> it = sel.iterator();
			boolean containsClosedReview = false, firstReviewIsActive = false, firstIteration = true;
			while(it.hasNext() && !containsClosedReview) {
				Object o = it.next();
				if(o instanceof MultipleReviewWrapper) {
					if(!((MultipleReviewWrapper)o).isOpen()) {
						containsClosedReview = true;
					}
					if(firstIteration) {
						if(PropertiesManager.getPreferences().getString(PropertiesManager.EXTERNAL_KEYS.ACTIVE_REVIEW).equals(((MultipleReviewWrapper)o).getReviewId())) {
							firstReviewIsActive = true;
						}
					}
				}
				firstIteration = false;
			}
			ISourceProviderService isps = (ISourceProviderService) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getService(ISourceProviderService.class);
			SourceProvider sp1 = (SourceProvider) isps.getSourceProvider(SourceProvider.CONTAINS_CLOSED_REVIEW);
			sp1.setVariable(SourceProvider.CONTAINS_CLOSED_REVIEW, containsClosedReview);
			SourceProvider sp2 = (SourceProvider) isps.getSourceProvider(SourceProvider.IS_ACTIVE_REVIEW);
			sp2.setVariable(SourceProvider.IS_ACTIVE_REVIEW, firstReviewIsActive);
		}
	}

	@Override
	public void setFocus() {}

	
}