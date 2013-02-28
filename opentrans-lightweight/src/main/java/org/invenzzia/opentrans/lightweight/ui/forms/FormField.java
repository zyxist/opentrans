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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates component fields that shall be treated as form fields.
 * 
 * @author Tomasz Jędrzejewski
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FormField {
	/**
	 * The key this field should be registered as. It must be unique
	 * within the form.
	 * 
	 * @return Form field access key.
	 */
	String key();
	/**
	 * If set to true, an additional action listener is installed on the
	 * specified field, that causes the form to be submitted.
	 * 
	 * @return 
	 */
	boolean triggerFormProcessing() default false;
}
