/*
 * Copyright (C) 2013 Tomasz Jędrzejewski
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.invenzzia.opentrans.lightweight.model.lists;

import java.util.Comparator;
import org.invenzzia.opentrans.lightweight.model.EntityListModel;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.data.Line;
import org.invenzzia.opentrans.visitons.data.Line.LineRecord;
import org.invenzzia.opentrans.visitons.data.manager.LineManager;
import org.invenzzia.opentrans.visitons.data.utils.LineRecordComparator;

/**
 * Data model for the item list in the dialog window.
 * 
 * @author Tomasz Jędrzejewski
 */
public class LineModel extends EntityListModel<Line, LineRecord, LineManager> {
	@Override
	protected LineManager getDataManager(Project project) {
		return project.getLineManager();
	}

	@Override
	protected LineRecord createRecord() {
		return new LineRecord();
	}
	
	@Override
	protected Comparator<LineRecord> getComparator() {
		return LineRecordComparator.get();
	}
}
