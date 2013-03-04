package edu.uco.sdd.t3.gameboard;

public interface Player {
	public void placeMarker(int x, int y);
	public Marker getMarker();
	public void setMarker(Marker m);
	public int getId();
	public void setId(int id);
}
