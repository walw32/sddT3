package edu.uco.sdd.t3.gameboard;

/**
 * Controls how markers are placed on the board.
 * 
 * @author Joshua Ford
 * @version 1.0
 */
public interface MarkerPlacementStrategy {
	/**
	 * Places a marker on the actual game board.
	 * 
	 * @param coord The MoveAction that contains where the marker
	 * 				is to be placed and who is placing it.
	 * @return True if the marker can be successfully placed,
	 * 		   false otherwise.
	 */
	public boolean placeMarker(MoveAction coord);
}
