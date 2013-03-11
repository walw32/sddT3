package edu.uco.sdd.t3.gameboard;

public interface GameStateListener {
	public void onMarkerPlaced(MoveAction action);
	public void onGameOver(String message);
}
