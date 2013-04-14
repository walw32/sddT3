package edu.uco.sdd.t3.core;

/**
 * Used to observe a game's state, and receives a message upon
 * the termination of gameplay.
 * 
 * @author Joshua Ford
 * @version 1.0
 */
public interface GameObserver {
	/**
	 * The method in which observers are notified of changes to
	 * the game state.
	 * 
	 * @param message The message sent to the observer from the game.
	 */
	public void onGameOver(String message);
}
