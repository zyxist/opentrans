/*
 * Visitons - public transport simulation engine
 * Copyright (c) 2011-2012 Invenzzia Group
 * 
 * Visitons is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Visitons is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Visitons. If not, see <http://www.gnu.org/licenses/>.
 */
package org.invenzzia.opentrans.visitons.render.stream;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.Map;
import org.invenzzia.opentrans.visitons.render.CameraModelSnapshot;
import org.invenzzia.opentrans.visitons.render.RenderingStreamAdapter;
import org.invenzzia.opentrans.visitons.render.scene.TrackSnapshot;
import org.invenzzia.opentrans.visitons.render.scene.TrackSnapshot.ITrackRecord;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
public class TrackStream extends RenderingStreamAdapter {
	private String recognizedTrackSnapshotKey;
	
	public TrackStream() {
		this.recognizedTrackSnapshotKey = "infrastructure";
	}
	
	public void setRecognizedTrackSnapshotKey(String key) {
		this.recognizedTrackSnapshotKey = key;
	}
	
	public String getRecognizedTrackSnapshotKey() {
		return this.recognizedTrackSnapshotKey;
	}

	@Override
	public void render(Graphics2D graphics, Map<Object, Object> snapshot, long prevTimeFrame) {
		CameraModelSnapshot camera = this.extract(snapshot, CameraModelSnapshot.class);
		Object obj = snapshot.get(this.recognizedTrackSnapshotKey);		
		if(null != obj) {
			TrackSnapshot ts = (TrackSnapshot) obj;
			
			graphics.setColor(Color.BLUE);
			graphics.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			for(ITrackRecord dt: ts.getDrawableTracks()) {
				dt.draw(camera, graphics);
			}
		}
	}
}
