package org.agilereview.storage.xml.test;

import static org.junit.Assert.fail;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotPerspective;
import org.junit.BeforeClass;
import org.junit.Test;

public class NewReviewSourceProjectWizardTest {

	private final static SWTWorkbenchBot bot = new SWTWorkbenchBot();
	
	@BeforeClass
	public static void setUp() {
		SWTBotPerspective perspective = bot.perspectiveById("org.agilereview.ui.basic.perspective");
        if (!perspective.isActive()) {
            perspective.activate();
        }
	}
	
	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
