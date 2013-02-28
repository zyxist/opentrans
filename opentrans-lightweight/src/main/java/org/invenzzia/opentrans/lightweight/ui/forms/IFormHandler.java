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

package org.invenzzia.opentrans.lightweight.ui.forms;

/**
 * Form handler is used by the form engine to handle user-specific code.
 * Each form in the application must have its own handler, which defines
 * what fields are available, how to populate them, how to validate them,
 * and what to do after approving the form.
 * 
 * @author Tomasz Jędrzejewski
 */
public interface IFormHandler {
	public String getSuccessMessage();
	public String getFailureMessage();
	
	public void loadModel(IFormAccessor form);
	public boolean validateModel(IFormAccessor form);
	public void saveModel(IFormAccessor form);
}
