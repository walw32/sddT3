package edu.uco.sdd.t3.gameboard;

import android.graphics.drawable.Drawable;

public class Marker {
	
	public Marker() {
		mDrawable = null;
		mPlayer = null;
	}
	
	public Marker(Marker copyThis) {
		mDrawable = copyThis.mDrawable;
		mPlayer = copyThis.mPlayer;
	}
	
	public Marker(Marker copyDrawable, Player p) {
		mDrawable = copyDrawable.mDrawable;
		mPlayer = p;
	}
	
	public Marker(Drawable d, Player p) {
		mDrawable = d;
		mPlayer = p;
	}
	
	public Drawable getDrawable() {
		return mDrawable;
	}
	
	public void setDrawable(Drawable d) {
		mDrawable = d;
	}
	
	public Player getPlayer() {
		return mPlayer;
	}
	
	private Drawable mDrawable;
	private Player mPlayer;
}
