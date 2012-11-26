package org.agilereview.ui.basic.handler;

import org.agilereview.core.external.exception.NullArgumentException;
import org.agilereview.core.external.storage.PojoFactory;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class NewReviewHandler extends AbstractHandler {
    
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        
        try {
            PojoFactory.createReview();
        } catch (NullArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return null;
    }
    
}
