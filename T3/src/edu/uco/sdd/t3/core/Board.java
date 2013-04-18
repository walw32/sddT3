package edu.uco.sdd.t3.core;

import java.util.ArrayList;
import java.util.Vector;

import edu.uco.sdd.t3.core.Game.Mode;

/**
 * Data structure used to keep track of markers being placed. It is also uses a
 * strategy to determine how markers are placed on the board, but otherwise is
 * not directly responsible for placing markers on the board, despite its member
 * function.
 * 
 * @author Joshua Ford
 * @version 1.0
 */
public class Board {

	/**
	 * Creates a Board object of a certain size.
	 * 
	 * @param boardSize
	 *            The size of the board as a single number, e.g. "3" = 3 x 3
	 *            game board.
	 */
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

	/**
	 * Places a marker somewhere on the board.
	 * 
	 * @param action
	 *            The action that contains the coordinates and PlayerId.
	 * @return True if the marker is successfully placed, false otherwise.
	 */
	public boolean placeMarker(MoveAction action) {
		if (mStrategy.placeMarker(action)) {
			if (mStrategy.getTag().equals("PlaceMarkerDirectly")) {
				mMarkerCount++;
			}
			for (BoardObserver observer : mBoardObservers) {
				observer.onMarkerPlaced(action);
			}
			if (mMarkerCount == mBoardSize * mBoardSize - 1) {
				// If it was the last marker placed, and we're in Sudden Death,
				// change strategies.
				if (action.getGame().getGameMode() == Game.Mode.SUDDEN_DEATH) {
					if (!mLastMarkerPlaced) {
						mLastMarkerPlaced = true;
						ChangeStrategyAction changeMarkerStrategy = new ChangeStrategyAction(
								action.getGame(), action.getBoard());
						changeMarkerStrategy.execute();
					}
				}
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Sets the marker placement strategy. Common strategies include
	 * PlaceMarkerDirectly and SlideExistingMarker.
	 * 
	 * @param s
	 *            The MarkerPlacementStrategy that determines how markers are
	 *            placed.
	 */
	public void setPlaceMarkerStrategy(MarkerPlacementStrategy s) {
		mStrategy = s;
	}

	/**
	 * Returns the marker placement strategy of this board.
	 * 
	 * @return The board's marker placement strategy.
	 */
	public MarkerPlacementStrategy getPlaceMarkerStrategy() {
		return mStrategy;
	}

	/**
	 * 
	 * @return The current game board as a matrix.
	 */
	public Vector<Vector<Integer>> getGameBoard() {
		return mGameBoard;
	}

	/**
	 * 
	 * @return The size of the board.
	 */
	public int getBoardSize() {
		return mBoardSize;
	}

	/**
	 * 
	 * @return The total number of markers placed on the board.
	 */
	public int getMarkerCount() {
		return mMarkerCount;
	}

	/**
	 * 
	 * @return True if the total number of markers placed equals the amount of
	 *         available spaces available on the board. False otherwise.
	 */
	public boolean isFilled() {
		return (mMarkerCount >= mBoardSize * mBoardSize);
	}

	/**
	 * Attaches a BoardObserver to this Board. Such an observer will be notified
	 * when a marker is successfully placed on the board.
	 * 
	 * @param observer
	 *            The BoardObserver that will observe this board.
	 */
	public void attachObserver(BoardObserver observer) {
		mBoardObservers.add(observer);
	}

	/**
	 * Removes an observer from this Board. That observer will no longer be
	 * notified of any markers that are placed.
	 * 
	 * @param observer
	 *            The BoardObserver that will no longer observe this board.
	 */
	public void detachObserver(BoardObserver observer) {
		mBoardObservers.remove(observer);
	}

	private int mBoardSize;
	private int mMarkerCount;
	private MarkerPlacementStrategy mStrategy;
	private ArrayList<BoardObserver> mBoardObservers;
	private Vector<Vector<Integer>> mGameBoard;
	private boolean mLastMarkerPlaced = false;
}
