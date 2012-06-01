/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.invenzzia.opentrans.client;

import org.invenzzia.helium.gui.annotation.Card;
import org.invenzzia.helium.gui.ui.welcome.WelcomeController;
import org.invenzzia.helium.gui.ui.welcome.WelcomeView;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
@Card(position = "editor", title = "Welcome")
public class MyWelcomeView extends WelcomeView {
	public MyWelcomeView(WelcomeController controller) {
		super(controller);
	}
}
