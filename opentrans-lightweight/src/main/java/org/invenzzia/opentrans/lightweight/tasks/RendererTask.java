/*
 * Copyright (C) 2013 Invenzzia Group <http://www.invenzzia.org/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.invenzzia.opentrans.lightweight.tasks;

import com.google.inject.Inject;
import org.invenzzia.opentrans.lightweight.exception.TaskException;
import org.invenzzia.opentrans.visitons.render.Renderer;
import org.invenzzia.opentrans.visitons.render.stream.DebugPointStream;
import org.invenzzia.opentrans.visitons.render.stream.GridStream;
import org.invenzzia.opentrans.visitons.render.stream.SegmentBitmapStream;
import org.invenzzia.opentrans.visitons.render.stream.SelectionStream;
import org.invenzzia.opentrans.visitons.render.stream.TrackStream;

/**
 * This must be refactored some day to use multibindings. It's a bit shame
 * to do it in this way.
 * 
 * @author Tomasz Jędrzejewski
 */
public class RendererTask implements ITask {
	@Inject
	private Renderer renderer;
	@Inject
	private GridStream gridStream;
	@Inject
	private SegmentBitmapStream bitmapStream;
	@Inject
	private TrackStream trackStream;
	@Inject
	private DebugPointStream debugStream;
	@Inject
	private SelectionStream selectionStream;

	@Override
	public void startup() throws TaskException {
		this.renderer.addRenderingStream(this.bitmapStream);
		this.renderer.addRenderingStream(this.gridStream);
		this.renderer.addRenderingStream(this.trackStream);
		this.renderer.addRenderingStream(this.selectionStream);
		this.renderer.addRenderingStream(this.debugStream);
	}

	@Override
	public void shutdown() throws TaskException {
	}
}
