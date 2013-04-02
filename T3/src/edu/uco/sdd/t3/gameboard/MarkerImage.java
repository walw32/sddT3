package edu.uco.sdd.t3.gameboard;

import android.graphics.drawable.Drawable;

/**
 * The actual drawable that represents a Player's marker.
 * 
 * @author Joshua Ford
 * @version 1.0
 */
public class MarkerImage {
	
	public MarkerImage() {
		mDrawable = null;
	}
	
	/**
	 * Copy constructor.
	 * 
	 * @param image The MarkerImage to copy.
	 */
	public MarkerImage(MarkerImage image) {
		mDrawable = image.mDrawable;
	}
	
	/**
	 * Creates a MarkerImage from a given drawable.
	 * 
	 * @param d The drawable that will be associated with this MarkerImage.
	 */
	public MarkerImage(Drawable d) {
		mDrawable = d;
	}
	
	public Drawable getDrawable() {
		return mDrawable;
	}
	
	public void setDrawable(Drawable d) {
		mDrawable = d;
	}
	
	private Drawable mDrawable;
}
