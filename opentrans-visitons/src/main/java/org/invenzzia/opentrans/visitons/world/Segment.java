/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.invenzzia.opentrans.visitons.world;

import java.awt.Image;
import org.invenzzia.opentrans.visitons.infrastructure.Vertex;

/**
 * Represents a single world segment, a mechanism of partitioning the space in order to speed up rendering and finding objects. A segment
 * represents a square area of 1 km^2. It contains the references to all the vertices located inside it.
 *
 * @author Tomasz Jędrzejewski
 */
public class Segment {

	/**
	 * Segment size in meters
	 */
	public static final int SIZE = 1000;
	/**
	 * Segment position on the world map.
	 */
	private int positionX;
	/**
	 * Segment position on the world map.
	 */
	private int positionY;
	/**
	 * Stores a background bitmap, but only if the image is already displayed.
	 */
	private Image image;
	/**
	 * The path to the image.
	 */
	private String imagePath;
	/**
	 * Is the segment in the visible area of the camera?
	 */
	private boolean isDisplayed;

	/**
	 * Sets the position coordinates of the segment.
	 *
	 * @param x
	 * @param y
	 * @return Fluent interface.
	 */
	public Segment setPosition(int x, int y) {
		this.positionX = x;
		this.positionY = y;
		return this;
	} // end setPosition();

	/**
	 * @return Returns the X-location of the segment.
	 */
	public int getX() {
		return this.positionX;
	} // end getX();

	/**
	 * @return Returns the Y-location of the segment.
	 */
	public int getY() {
		return this.positionY;
	} // end getY();

	/**
	 * Adds a new vertex to the segment. Avoid using this method in favour of {@link World#addVertex}.
	 *
	 * @param vertex
	 * @return Fluent interface.
	 */
	Segment addVertex(Vertex vertex) {
		return this;
	} // end addVertex();
} // end Segment;