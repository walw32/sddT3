package edu.uco.sdd.t3.core;

import java.util.Vector;


import android.util.Log;

/**
 * Strategy for placing a marker directly on the game board.
 * 
 * @author Joshua Ford
 * @version 1.0
 */
public class PlaceMarkerDirectly implements MarkerPlacementStrategy {

	private static final String TAG = "PlaceMarkerDirectly";
	private Board mGameBoard;
	
	public PlaceMarkerDirectly(Board gameBoard) {
		mGameBoard = gameBoard;
	}

	@Override
	public boolean placeMarker(MoveAction action) {
		int playerId = action.getPlayerId();
		int row = action.getX();
		int column = action.getY();
		Vector<Vector<Integer>> board = mGameBoard.getGameBoard();
		Vector<Integer> rowVector = board.get(row);
		int valueAtLocation = rowVector.get(column);
		if(valueAtLocation > 0) {
			return false;
		} else {
			rowVector.set(column, playerId); // board[row][column] = playerId;
			return true;
		}
	}

	@Override
	public String getTag() {
		return TAG;
	}

}
