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

package org.invenzzia.opentrans.lightweight.ui.component;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JButton;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
public class IconButton extends JButton {
	private static final Font font = new Font("Monospaced", Font.PLAIN, 6);
	
	/**
	 * The text displayed in the left bottom corner.
	 */
	private String subtext;
	
	/**
	 * Sets the text displayed in the left bottom corner of the button.
	 * @param subtext 
	 */
	public void setSubtext(String subtext) {
		this.subtext = subtext;
	}
	
	/**
	 * Returns the text displayed in the left bottom corner of the button.
	 * @return 
	 */
	public String getSubtext() {
		return this.subtext;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(null != this.subtext) {
			Graphics2D graphics = (Graphics2D) g;

			graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			FontMetrics metrics = graphics.getFontMetrics(font);
			int startPos = this.getHeight() - metrics.getHeight();

			graphics.setColor(Color.BLACK);
			graphics.setFont(font);
			graphics.drawString(this.getSubtext().toUpperCase(), 7, startPos);
		}
	}
}
