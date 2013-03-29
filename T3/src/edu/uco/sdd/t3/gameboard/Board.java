package edu.uco.sdd.t3.gameboard;


import java.util.ArrayList;
import java.util.Vector;

public class Board {


	public Board(int boardSize) {
		mStrategy = new PlaceMarkerDirectly(this);
		mMarkerCount = 0;
		mBoardSize = boardSize;
		mBoardObservers = new ArrayList<BoardObserver>();
		mGameBoard = new Vector<Vector<Integer>>();
		mGameBoard.setSize(mBoardSize);
		for (int i = 0; i < mBoardSize; i++) {
			Vector<Integer> row = new Vector<Integer>();
			row.setSize(mBoardSize);
			for (int j = 0; j < row.size(); j++) {
				row.set(j, 0);
			}
			mGameBoard.set(i, row);
		}
	}
	
	public boolean placeMarker(MoveAction coord) {
		if (mStrategy.placeMarker(coord)) {
			mMarkerCount++;
			for (BoardObserver observer : mBoardObservers) {
				observer.onMarkerPlaced(coord);
			}
			return true;
		} else {
			return false;
		}
	}
	
	public void setPlaceMarkerStrategy(MarkerPlacementStrategy s) {
		mStrategy = s;
	}
	
	public Vector<Vector<Integer>> getGameBoard() {
		return mGameBoard;
	}
	
	public int getBoardSize() {
		return mBoardSize;
	}
	
	public int getMarkerCount() {
		return mMarkerCount;
	}
	
	public boolean isFilled() {
		return (mMarkerCount >= mBoardSize * mBoardSize);
	}
	
	public void attachObserver(BoardObserver observer) {
		mBoardObservers.add(observer);
	}
	
	public void detachObserver(BoardObserver observer) {
		mBoardObservers.remove(observer);
	}

	private int mBoardSize;
	private int mMarkerCount;
	private MarkerPlacementStrategy mStrategy;
	private ArrayList<BoardObserver> mBoardObservers;
	private Vector<Vector<Integer>> mGameBoard;
}
