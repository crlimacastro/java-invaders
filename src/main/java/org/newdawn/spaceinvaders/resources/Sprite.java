package org.newdawn.spaceinvaders.resources;

import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

import org.newdawn.spaceinvaders.resources.Sprite;

/**
 * A sprite to be displayed on the screen. Note that a sprite
 * contains no state information, i.e. its just the image and
 * not the location. This allows us to use a single sprite in
 * lots of different places without having to store multiple
 * copies of the image.
 * 
 * @author Kevin Glass
 */
public class Sprite implements IResource {
	/** The image to be drawn for this sprite */
	private Image image;

	public Sprite() {
	}

	@Override
	public void load(String path) {
		try {
			URL url = this.getClass().getClassLoader().getResource(path);
			BufferedImage sourceImage = ImageIO.read(url);
			Image image = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice()
				.getDefaultConfiguration()
				.createCompatibleImage(sourceImage.getWidth(), sourceImage.getHeight(), Transparency.BITMASK);
			// draw our source image into the accelerated image
			image.getGraphics().drawImage(sourceImage, 0, 0, null);
			// create a sprite, add it the cache then return it
			this.image = image;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get the width of the drawn sprite
	 * 
	 * @return The width in pixels of this sprite
	 */
	public int getWidth() {
		return image.getWidth(null);
	}

	/**
	 * Get the height of the drawn sprite
	 * 
	 * @return The height in pixels of this sprite
	 */
	public int getHeight() {
		return image.getHeight(null);
	}

	/**
	 * Draw the sprite onto the graphics context provided
	 * 
	 * @param g The graphics context on which to draw the sprite
	 * @param x The x location at which to draw the sprite
	 * @param y The y location at which to draw the sprite
	 */
	public void draw(Graphics g, int x, int y) {
		g.drawImage(image, x, y, null);
	}
}