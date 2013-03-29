package edu.uco.sdd.t3.gameboard;


import java.util.ArrayList;
import java.util.Vector;

import android.util.Log;

public class Board {


	public Board(Game g, int boardSize) {
		mCurrentGame = g;
		mStrategy = new PlaceMarkerDirectly(this);
		mTotalMarkersPlaced = 0;
		mBoardSize = boardSize;
		setmGameHistory(new ArrayList<MoveAction>());
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
			getmGameHistory().add(coord);
			mTotalMarkersPlaced++;
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
	
	public int getTotalMarkersPlaced() {
		return mTotalMarkersPlaced;
	}
	
	public boolean isFilled() {
		return (mTotalMarkersPlaced >= mBoardSize * mBoardSize);
	}
	
	public ArrayList<MoveAction> getmGameHistory() {
		return mGameHistory;
	}

	public void setmGameHistory(ArrayList<MoveAction> mGameHistory) {
		this.mGameHistory = mGameHistory;
	}

	private Game mCurrentGame;
	private int mBoardSize;
	private int mTotalMarkersPlaced;
	private MarkerPlacementStrategy mStrategy;
	private ArrayList<MoveAction> mGameHistory;
	private Vector<Vector<Integer>> mGameBoard;
}
