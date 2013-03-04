package edu.uco.sdd.t3.gameboard;

import java.util.Vector;

import android.util.Log;

public class PlaceMarkerDirectly implements MarkerPlacementStrategy {

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

}
