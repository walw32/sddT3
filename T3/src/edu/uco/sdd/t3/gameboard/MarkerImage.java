package edu.uco.sdd.t3.gameboard;

import android.graphics.drawable.Drawable;

public class MarkerImage {
	
	public MarkerImage() {
		mDrawable = null;
	}
	
	public MarkerImage(MarkerImage image) {
		mDrawable = image.mDrawable;
	}
	
	public MarkerImage(Drawable d, Player p) {
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
