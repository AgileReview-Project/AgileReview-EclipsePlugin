package org.agilereview.ui.basic;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {
    
    public static class ISharedImages {
        // TODO: Maybe AR icon is not needed?
        /**
         * AgileReview icon
         */
        public static final String AGILEREVIEW_ICON = "agilereview_icon";
        /**
         * Active review icon
         */
        public static final String ACTIVE_REVIEW_ICON = "active_review_icon";
        /**
         * World Icon (Browse)
         */
        public static final String BROWSE_WORLD = "browse_world";
    }
    
    /**
     * The plug-in ID
     */
    public static final String PLUGIN_ID = "org.agilereview.ui.basic"; //$NON-NLS-1$
    
    /**
     * The shared instance
     */
    private static Activator plugin;
    
    /**
     * Properties for internal access only (e.g. file paths)
     */
    private Properties internalProps = new Properties();
    /**
     * Filename/path of the property file
     */
    private String internalPropFile = "internalUI.properties";
    
    /**
     * The constructor
     */
    public Activator() {
        try {
            InputStream inStream = Activator.class.getClassLoader().getResourceAsStream(internalPropFile);
            internalProps.load(inStream);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }
    
    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }
    
    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }
    
    public String getInternalProperty(String key) {
        return internalProps.getProperty(key);
    }
    
    @Override
    protected void initializeImageRegistry(ImageRegistry registry) {
        super.initializeImageRegistry(registry);
        String coreId = "org.agilereview.core";
        registry.put(ISharedImages.AGILEREVIEW_ICON, AbstractUIPlugin.imageDescriptorFromPlugin(coreId, "icons/agilereview_icon.png"));
        registry.put(ISharedImages.ACTIVE_REVIEW_ICON, AbstractUIPlugin.imageDescriptorFromPlugin(coreId, "icons/review_activate.png"));
        registry.put(ISharedImages.BROWSE_WORLD, AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, "icons/discovery.gif"));
    }
    
}
