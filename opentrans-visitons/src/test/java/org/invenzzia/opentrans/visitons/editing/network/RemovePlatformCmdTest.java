/*
 * Copyright (C) 2013 Invenzzia Group <http://www.invenzzia.org/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.invenzzia.opentrans.visitons.editing.network;

import com.google.common.eventbus.EventBus;
import org.invenzzia.helium.exception.ModelException;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.data.Platform;
import org.invenzzia.opentrans.visitons.data.Platform.PlatformRecord;
import org.invenzzia.opentrans.visitons.data.Stop;
import org.invenzzia.opentrans.visitons.data.Stop.StopRecord;
import org.invenzzia.opentrans.visitons.geometry.Geometry;
import org.invenzzia.opentrans.visitons.network.Track;
import org.invenzzia.opentrans.visitons.network.objects.TrackObject;
import org.junit.Assert;
import org.junit.Test;
import static org.mockito.Mockito.*;


public class RemovePlatformCmdTest {

	@Test
	public void testExecuteUndoRedoCommand() throws ModelException, Exception {
		// Data preparation
		Project project = new Project();
		Stop stop = new Stop();
		stop.setName("Foo");
		project.getStopManager().addItem(stop);
		Track track = new Track();
		track.setId(15);
		TrackObject trackObject = new TrackObject();
		trackObject.setPosition(0.5);
		trackObject.setOrientation((byte)1);
		track.addTrackObject(trackObject);
		Platform platform = new Platform(stop, trackObject);
		project.getWorld().addTrack(track);
		EventBus eventBus = mock(EventBus.class);
		
		Assert.assertSame(track, trackObject.getTrack());
		Assert.assertTrue(stop.hasPlatforms());
		Assert.assertSame(platform, stop.getPlatform(platform.getNumber()));
		Assert.assertTrue(track.hasTrackObjects());
		
		StopRecord stopRecord = new StopRecord();
		stopRecord.importData(stop, project);
		PlatformRecord record = stopRecord.getPlatform(platform.getNumber());
		
		record.importData(platform);
		
		RemovePlatformCmd cmd = new RemovePlatformCmd(record);
		cmd.execute(project, eventBus);
		
		Assert.assertFalse(stop.hasPlatforms());
		Assert.assertFalse(track.hasTrackObjects());
		
		cmd.undo(project, eventBus);
		
		Assert.assertTrue(stop.hasPlatforms());
		Assert.assertTrue(track.hasTrackObjects());
		
		Platform restoredPlatform = stop.getPlatform(platform.getNumber());
		Assert.assertNotNull(restoredPlatform);
		Assert.assertEquals(platform.getNumber(), restoredPlatform.getNumber());
		Assert.assertEquals(platform.getName(), restoredPlatform.getName());
		Assert.assertSame(stop, restoredPlatform.getStop());
		TrackObject restoredObject = restoredPlatform.getTrackObject();
		Assert.assertEquals(trackObject.getOrientation(), restoredObject.getOrientation());
		Assert.assertEquals(trackObject.getPosition(), restoredObject.getPosition(), Geometry.EPSILON);
		Assert.assertSame(track, restoredObject.getTrack());
		Assert.assertSame(restoredPlatform, restoredObject.getObject());

		cmd.redo(project, eventBus);

		Assert.assertFalse(stop.hasPlatforms());
		Assert.assertFalse(track.hasTrackObjects());
	}

}
