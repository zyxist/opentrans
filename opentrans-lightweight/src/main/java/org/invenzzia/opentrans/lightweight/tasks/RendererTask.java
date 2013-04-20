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
import org.invenzzia.opentrans.visitons.render.stream.EditableTrackStream;
import org.invenzzia.opentrans.visitons.render.stream.GridStream;
import org.invenzzia.opentrans.visitons.render.stream.SegmentBitmapStream;

/**
 * Description here.
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
	private EditableTrackStream editableTrackStream;

	@Override
	public void startup() throws TaskException {
		this.renderer.addRenderingStream(this.gridStream);
		this.renderer.addRenderingStream(this.bitmapStream);
		this.renderer.addRenderingStream(this.editableTrackStream);
	}

	@Override
	public void shutdown() throws TaskException {
	}
}
