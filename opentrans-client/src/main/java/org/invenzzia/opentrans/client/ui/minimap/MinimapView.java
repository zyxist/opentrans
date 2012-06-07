/*
 * OpenTrans - public transport simulator
 * Copyright (c) 2011-2012 Invenzzia Group
 * 
 * OpenTrans is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenTrans is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenTrans. If not, see <http://www.gnu.org/licenses/>.
 */
package org.invenzzia.opentrans.client.ui.minimap;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import org.invenzzia.helium.gui.annotation.Card;
import org.invenzzia.helium.gui.mvc.IView;
import org.invenzzia.opentrans.client.ui.commons.Minimap;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
@Card(position = "properties", title = "Minimap")
public class MinimapView extends JPanel implements IView<MinimapController> {
	private MinimapController controller;
	private boolean attached = false;
	
	private Minimap minimapComponent;
	
	public MinimapView() {
		this.minimapComponent = new Minimap();
		this.minimapComponent.setData(new boolean[][] {
			{ false, false, true, true, false, false, true, false },
			{ false, false, true, true, false, false, true, false },
			{ false, true, true, true, true, true, true, false },
			{ false, true, true, true, true, true, true, true },
			{ true, true, true, true, true, true, true, true },
			{ false, false, true, true, true, false, true, false },
			{ false, false, false, true, false, false, true, false }
		});
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		this.add(this.minimapComponent, c);
	}
	
	public void setController(MinimapController controller) {
		if(null != this.controller) {
			this.attached = false;
			this.controller.detachView(this);
		}
		this.controller = controller;
		if(null != this.controller) {
			this.attached = true;
			this.controller.attachView(this);
		}
	}

	@Override
	public MinimapController getController() {
		return this.controller;
	}

} // end MinimapView;
