package edu.uco.sdd.t3.core;

import java.util.Vector;

import android.util.Log;


/**
 * Strategy for sliding a marker into an open spot on the game board.
 * 
 * @author Joshua Ford
 * @version 1.0
 */
public class SlideExistingMarker implements MarkerPlacementStrategy {

	private Board mGameBoard;
	private int openSpotCol;
	private int openSpotRow;
	private int previousOpenSpotCol;
	private int previousOpenSpotRow;
	private static final String TAG = "SlideExistingMarker";
	
	public SlideExistingMarker(Board board) {
		mGameBoard = board;
		previousOpenSpotRow = -1;
		previousOpenSpotCol = -1;
		locateOpenSpot();
	}

	@Override
	public boolean placeMarker(MoveAction action) {
		int playerId = action.getPlayerId();
		int row = action.getX();
		int column = action.getY();
		if (!isAdjacent(column, row)) {
			return false;
		}
		Vector<Vector<Integer>> board = mGameBoard.getGameBoard();
		int valueAtLocation = board.get(row).get(column);
		Log.d(TAG, "valueAtLocation = " + valueAtLocation);
		Log.d(TAG, "playerId = " + playerId);
		if(valueAtLocation > 0 && valueAtLocation == playerId) {
			// Put the player's ID into the open spot
			board.get(openSpotRow).set(openSpotCol, playerId);
			// Make the spot of the move action the open spot
			board.get(row).set(column, -1);
			previousOpenSpotRow = openSpotRow;
			previousOpenSpotCol = openSpotCol;
			openSpotRow = row;
			openSpotCol = column;
			Log.d("SlideExistingMarker", "openSpotRow = " + openSpotRow);
			Log.d("SlideExistingMarker", "openSpotColumn = " + openSpotCol);
			return true;
		} else {
			// If the spot they touched was empty, we can't do anything.
			return false;
		}
	}
	
	/**
	 * Given an X and a Y coordinate of the matrix, this method
	 * will calculate if that coordinate is adjacent to the
	 * open spot on the board. 
	 * 
	 * @param col The x-coordinate (The column)
	 * @param row The y-coordinate (The row)
	 * @return
	 */
	private boolean isAdjacent(int col, int row) {
		Log.d(TAG, "in isAdjacent()");
		Log.d(TAG, "(x,y) = ("+(col+1)+","+(row+1)+")");
		Log.d(TAG, "(openSpotCol,openSpotRow) = ("+(openSpotCol+1)+","+(openSpotRow+1)+")");
		// Add +1 to adjust for the zero-indexing
		int colDifference = Math.abs((col+1) - (openSpotCol+1)); // Column adjacency
		int rowDifference = Math.abs((row+1) - (openSpotRow+1)); // Row adjacency
		Log.d(TAG, "rowDifference = " + rowDifference);
		Log.d(TAG, "colDifference = " + colDifference);
		if (rowDifference == 0 && colDifference ==  1) {
			Log.d(TAG, "Condition 1 applies, returning true!");
			return true;
		} else if (rowDifference == 1 && colDifference == 0) {
			Log.d(TAG, "Condition 2 applies, returning true!");
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Locates the open spot on the game board. Will return
	 * when it finds the first open and available spot on the
	 * board.
	 */
	private void locateOpenSpot() {
		Log.d("SlideExistingMarker", "in locateOpenSpot()");
		Vector<Vector<Integer>> board = mGameBoard.getGameBoard();
		int boardSize = mGameBoard.getBoardSize();
		for (int row = 0; row < boardSize; row++) {
			for (int col = 0; col < boardSize; col++) {
				if (board.get(row).get(col) <= 0) {
					openSpotCol = col;
					openSpotRow = row;
					Log.d("SlideExistingMarker", "openSpotRow = " + openSpotRow);
					Log.d("SlideExistingMarker", "openSpotColumn = " + openSpotCol);
					return;
				}
			}
		}
	}
	
	public int getOpenSpotCol() {
		return openSpotCol;
	}

	public int getOpenSpotRow() {
		return openSpotRow;
	}
	
	public int getPreviousOpenSpotCol() {
		return previousOpenSpotCol;
	}

	public int getPreviousOpenSpotRow() {
		return previousOpenSpotRow;
	}

	@Override
	public String getTag() {
		return TAG;
	}

}
