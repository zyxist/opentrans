/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.invenzzia.opentrans.client;

import java.awt.Dimension;
import org.invenzzia.helium.gui.ui.welcome.WelcomeController;
import org.invenzzia.helium.gui.ui.welcome.WelcomeView;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
public class MyWelcomeView extends WelcomeView {
	public MyWelcomeView(WelcomeController controller) {
		super();
		this.setPreferredSize(new Dimension(600, 600));
	}
}
