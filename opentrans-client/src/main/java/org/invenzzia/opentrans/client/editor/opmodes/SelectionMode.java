/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.invenzzia.opentrans.client.editor.opmodes;

import org.invenzzia.opentrans.client.ui.netview.IOperationMode;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
public class SelectionMode implements IOperationMode {

	@Override
	public void modeActivated() {
	}

	@Override
	public void modeDeactivated() {
	}

	@Override
	public String getName() {
		return "Select";
	}

	@Override
	public String getIcon() {
		return "ui-cursor";
	}
	
	@Override
	public String getHelpText() {
		return "Click on the network infrastructure elements to select them.";
	}
}
