package edu.uco.sdd.t3.gameboard;

/**
 * An interface that observes a board object and is notified
 * whenever a marker is successfully placed on the board.
 * 
 * @author Joshua
 * @version 1.0
 */
public interface BoardObserver {
	/**
	 * When a marker is placed on the Board, this
	 * observer is notified via this method. The MoveAction
	 * used to place the marker is also sent via this
	 * method, so any pertinent information can be obtained.
	 * 
	 * @param action The MoveAction used to place the marker.
	 */
	public void onMarkerPlaced(MoveAction action);
}
