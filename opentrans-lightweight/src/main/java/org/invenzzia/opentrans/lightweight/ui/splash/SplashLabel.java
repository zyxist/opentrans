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
package org.invenzzia.opentrans.lightweight.ui.splash;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JLabel;

/**
 * A component for displaying the splash image with the progress bar and progress information provided.
 *
 * @author Tomasz Jedrzejewski
 */
class SplashLabel extends JLabel {

	private static final int PROGRESS_HEIGHT = 10;
	private SplashScreen model;
	private int textPosX = 30;
	private int textPosY = 30;
	private int progressPosY = 30;
	private Color progressColor = Color.BLACK;

	public SplashLabel(SplashScreen screen) {
		this.model = screen;
	}

	public void setTextPositionX(int x) {
		this.textPosX = x;
	}

	public void setTextPositionY(int y) {
		this.textPosY = y;
	}

	public void setTextPosition(int x, int y) {
		this.textPosX = x;
		this.textPosY = y;
	}

	public void setProgressPosition(int y) {
		this.progressPosY = y;
	}

	public void setProgressColor(Color color) {
		this.progressColor = color;
	}

	public int getTextPositionX() {
		return this.textPosX;
	}

	public int getTextPositionY() {
		return this.textPosY;
	}

	public int getProgressPosition() {
		return this.progressPosY;
	}

	public Color getProgressColor() {
		return this.progressColor;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		String pt = this.model.getMessage();
		
		((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if(null != pt) {
			g.setColor(this.getForeground());
			g.drawString(pt, this.textPosX, this.textPosY);
		}
		double scaleFactor = this.getSize().getWidth() / this.model.getWeight();
		int width = (int) (this.model.getCurrentWeight() * scaleFactor);

		g.setColor(this.progressColor);
		g.fillRect(0, this.progressPosY, width, SplashLabel.PROGRESS_HEIGHT);
	}
}
