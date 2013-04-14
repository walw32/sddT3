package edu.uco.sdd.t3.core;

import java.util.Vector;


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
	
	public SlideExistingMarker(Board board) {
		mGameBoard = board;
		locateOpenSpot();
	}

	@Override
	public boolean placeMarker(MoveAction action) {
		int playerId = action.getPlayerId();
		int row = action.getX();
		int column = action.getY();
		if (!isAdjacent(row, column)) {
			return false;
		}
		Vector<Vector<Integer>> board = mGameBoard.getGameBoard();
		int valueAtLocation = board.get(row).get(column);
		if(valueAtLocation > 0) {
			// Put the player's ID into the open spot
			board.get(openSpotRow).set(openSpotCol, playerId);
			// Make the spot of the move action the open spot
			board.get(row).set(column, -1);
			openSpotRow = row;
			openSpotCol = column;
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
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 * @return
	 */
	private boolean isAdjacent(int x, int y) {
		int xDifference = Math.abs(x - openSpotCol);
		int yDifference = Math.abs(y - openSpotRow);
		if (xDifference == 0 && yDifference ==  1) {
			return true;
		} else if (xDifference == 1 && yDifference == 0) {
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
		Vector<Vector<Integer>> board = mGameBoard.getGameBoard();
		int boardSize = mGameBoard.getBoardSize();
		for (int row = 0; row < boardSize; row++) {
			for (int col = 0; col < boardSize; col++) {
				if (board.get(row).get(col) <= 0) {
					openSpotCol = col;
					openSpotRow = row;
					return;
				}
			}
		}
	}

}
