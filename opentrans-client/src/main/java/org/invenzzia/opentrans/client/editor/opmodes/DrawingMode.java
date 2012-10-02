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
package org.invenzzia.opentrans.client.editor.opmodes;

import org.invenzzia.helium.gui.ui.menu.MenuModel;
import org.invenzzia.opentrans.client.ui.netview.ClickedElement;
import org.invenzzia.opentrans.client.ui.netview.IOperationMode;
import org.invenzzia.opentrans.visitons.infrastructure.IVertex;
import org.invenzzia.opentrans.visitons.infrastructure.StraightTrack;
import org.invenzzia.opentrans.visitons.infrastructure.Vertex;
import org.invenzzia.opentrans.visitons.infrastructure.graph.EditableGraph;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
public class DrawingMode implements IOperationMode {
	/**
	 * What we are editing?
	 */
	private EditableGraph editableGraph;
	/**
	 * The movable vertex used for drawing tracks. This vertex lies under the current
	 * cursor position until we click somewhere.
	 */
	private IVertex handledVertex;
	
	@Override
	public void modeActivated() {
		
	}

	@Override
	public void modeDeactivated() {
	}

	@Override
	public String getName() {
		return "Draw";
	}

	@Override
	public String getIcon() {
		return "pencil";
	}
	
	@Override
	public String getHelpText() {
		return "Click on the map to start drawing a track.";
	}
	
	@Override
	public MenuModel getContextMenuModel() {
		return null;
	}
	
	@Override
	public Class<?> getMenuActions() {
		return null;
	}

	@Override
	public void mouseClicked(ClickedElement element, short button) {
		if(null == this.handledVertex) {
			// New drawing starts
			Vertex v1 = new Vertex(-1, 1, element.getSegment(), element.getX(), element.getY());
			Vertex v2 = new Vertex(-1, 1, element.getSegment(), element.getX(), element.getY());
			
			StraightTrack track = new StraightTrack(-1);
			track.setVertex(0, v1);
			track.setVertex(1, v2);
			v1.setTrack(0, track);
			v2.setTrack(1, track);
			
			this.editableGraph.addVertex(v1);
			this.editableGraph.addVertex(v2);
			this.editableGraph.addTrack(track);
			this.handledVertex = v2;
		} else {
			// We finish the existing drawing.
			this.handledVertex = null;
		}
	}

	@Override
	public void mouseMoved(ClickedElement element) {
		if(null != this.handledVertex) {
			this.handledVertex.registerUpdate(element.getSegment(), element.getX(), element.getY());
			if(this.handledVertex.isUpdatePossible()) {
				this.handledVertex.applyUpdate();
			} else {
				this.handledVertex.rollbackUpdate();
			}
		}
	}

	@Override
	public void mouseDragged(ClickedElement element, short button) {
		
	}
}
