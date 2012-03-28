package org.agilereview.ui.basic.reviewExplorer.model;

import org.agilereview.core.external.storage.Review;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.SharedImages;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * The LabelProvider specifies how the nodes of the tree viewer should be displayed
 */
class RELabelProvider extends ColumnLabelProvider {
	
	/**
	 * Standard grey color for displaying not exisitng resources
	 */
	private Color grey = Display.getDefault().getSystemColor(SWT.COLOR_GRAY);
	
	/**
	 * Empty Constructor
	 */
	public RELabelProvider(){
		super();
	}
	
//	/**
//	 * Checks whether the file represented by the given AbstractMultipleWrapper exists
//	 * @param o AbstractMultipleWrapper to check
//	 * @return <i>true</i> if the resource exits, false otherwise
//	 */
//	private boolean exists (Object o) {
//		boolean result = true;
//		if (o instanceof AbstractMultipleWrapper) {
//			IWorkspaceRoot wr = ResourcesPlugin.getWorkspace().getRoot();
//			if (!wr.exists(new Path(((AbstractMultipleWrapper) o).getPath()))) {
//				result = false;
//			}
//		}
//		return result;
//	}
	
	
	@Override
	public Image getImage(Object element) {
		// XXX hier weiter. Irgendwie automatisch das richtige Bild nehmen --> common navigator framework?
		//		WorkbenchImages.
		
		
		
		Image result = PlatformUI.getWorkbench().getEditorRegistry().getImageDescriptor(getText(element)).createImage();
		if ((element instanceof MultipleReviewWrapper))
		{
			MultipleReviewWrapper reviewElement = (MultipleReviewWrapper)element;
			String elementId = (reviewElement).getReviewId();
			
			// A closed review cannot be active
			if (!reviewElement.isOpen())
			{
				result = PlatformUI.getWorkbench().getSharedImages().getImage(SharedImages.IMG_OBJ_PROJECT_CLOSED);
			}
			else if (elementId.equals(PropertiesManager.getPreferences().getString(PropertiesManager.EXTERNAL_KEYS.ACTIVE_REVIEW)))
			{
				result = AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, PropertiesManager.getInstance().getInternalProperty(PropertiesManager.INTERNAL_KEYS.ICONS.REVIEW_OK)).createImage();
			}
		}
		else if(element instanceof MultipleProjectWrapper) {
			result = PlatformUI.getWorkbench().getSharedImages().getImage(SharedImages.IMG_OBJ_PROJECT);
		} else if(element instanceof MultipleFolderWrapper) {
			result = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
		} 
		
		return result;
	}

	@Override
	public String getText(Object element) {
		String result = "unknown object";
		if (element instanceof Review) {
			result = ((Review) element).getId(); // TODO: consider active review
		} else if (element instanceof IResource) {
			result = ((IResource) element).getName();
		}
		
//		if(element instanceof MultipleReviewWrapper) {
//			MultipleReviewWrapper wrap = (MultipleReviewWrapper)element;
//			result = wrap.getReviewId();
//			if (wrap.getReviewId().equals(PropertiesManager.getPreferences().getString(PropertiesManager.EXTERNAL_KEYS.ACTIVE_REVIEW)))
//			{
//				result = result + " (active)";
//			}
//		} else if(element instanceof AbstractMultipleWrapper) {
//			result = ((AbstractMultipleWrapper)element).getName();
//		}
		return result;
	}

//	@Override
//	public Color getForeground(Object element) {
//		if (!this.exists(element)) {
//			return this.grey;
//		}
//		return null;
//	}
}