package org.invenzzia.opentrans.client;

import org.invenzzia.helium.gui.annotation.Card;
import org.invenzzia.helium.gui.ui.welcome.WelcomeController;
import org.invenzzia.helium.gui.ui.welcome.WelcomeView;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
@Card(position = "explorer", title = "Hi")
public class AnotherWelcomeView extends WelcomeView {
	public AnotherWelcomeView(WelcomeController controller) {
		super(controller);
	}
}
