package edu.uco.sdd.t3.gameboard;


import java.util.ArrayList;
import java.util.Vector;

import android.util.Log;

public class Game implements BoardObserver{

	public enum State {
		PLAYER_1_TURN, PLAYER_2_TURN, GAME_OVER
	}

	public Game() {
		mGameObservers = new ArrayList<GameObserver>();
		mActionHistory = new ArrayList<GameAction>();
		mGameState = State.PLAYER_1_TURN;
	}

	@Override
	public void onMarkerPlaced(MoveAction action) {
		int markerCount = action.getBoard().getMarkerCount();
		int boardSize = action.getBoard().getBoardSize();
		int maxMarkers = boardSize * boardSize;
		Log.v("Game", "Marker Count: " + markerCount + " | Max Markers: " + maxMarkers);
		if (doVictoryEvaluation(action)) {
			if (mGameState == State.PLAYER_1_TURN) {
				notify("Game over! Player 1 wins!");
			} else if (mGameState == State.PLAYER_2_TURN) {
				notify("Game over! Player 2 wins!");
			}
			mGameState = State.GAME_OVER;
		} else if(markerCount == maxMarkers) {
			notify("Curses! Stalemate!");
			mGameState = State.GAME_OVER;
		} else {
			if (mGameState == State.PLAYER_1_TURN) {
				mGameState = State.PLAYER_2_TURN;
			} else if (mGameState == State.PLAYER_2_TURN) {
				mGameState = State.PLAYER_1_TURN;
			}
		}
	}
	
	public void notify(String message) {
		for (GameObserver observer : mGameObservers) {
			observer.onGameOver(message);
		}
	}

	public void attachObserver(GameObserver observer) {
		mGameObservers.add(observer);
	}
	
	public void detachObserver(GameObserver observer) {
		mGameObservers.remove(observer);
	}
	
	public void doAction(GameAction action) {
		mActionHistory.add(action);
		action.execute();
	}
	
	public State getGameState() {
		return mGameState;
	}

	private boolean doVictoryEvaluation(MoveAction action) {
		Vector<Vector<Integer>> gameBoardData = action.getBoard().getGameBoard();
		int gameBoardSize = action.getBoard().getBoardSize();
		Log.d("VictoryCheck", "gameBoardSize = " + gameBoardSize);
		int markersNeededToWin = gameBoardSize;
		int playerKey = action.getPlayerId();
		int totalConsecutiveMarkers = 0;
		int startRow = action.getX();
		int startColumn = action.getY();
		// Check for a victory in the row we're in.
		Vector<Integer> currentRow = gameBoardData.get(startRow);
		for (int i = 0; i < gameBoardSize; i++) {
			int currentColumn = (startColumn + i) % gameBoardSize;
			int markerAtThisLocation = currentRow.get(currentColumn);
			if (markerAtThisLocation == playerKey) {
				totalConsecutiveMarkers++;
			} else {
				totalConsecutiveMarkers = 0;
				break;
			}
			if (totalConsecutiveMarkers == markersNeededToWin) {
				Log.d("VictoryCheck", "Row Victory");
				return true;
			}
		}
		// Check for a victory in the column we're in.

		for (int i = 0; i < gameBoardSize; i++) {
			currentRow = gameBoardData.get((startRow + i) % gameBoardSize);
			int markerAtThisLocation = currentRow.get(startColumn);
			if (markerAtThisLocation == playerKey) {
				totalConsecutiveMarkers++;
			} else {
				totalConsecutiveMarkers = 0;
				break;
			}
			if (totalConsecutiveMarkers == markersNeededToWin) {
				Log.d("VictoryCheck", "Column Victory");
				return true;
			}
		}
		// Check for a victory along the diagonal.
		// A diagonal victory from the top left to the bottom right can only
		// occur if
		// the player has a marker wherever a row is equal to a column, i.e.:
		// [x _ _ _ _]
		// [_ x _ _ _]
		// [_ _ x _ _]
		// [_ _ _ x _]
		// [_ _ _ _ x]
		if (startRow == startColumn) {
			for (int i = 0; i < gameBoardSize; i++) {
				currentRow = gameBoardData.get((startRow + i) % gameBoardSize);
				int currentColumn = (startColumn + i) % gameBoardSize;
				int markerAtThisLocation = currentRow.get(currentColumn);
				if (markerAtThisLocation == playerKey) {
					totalConsecutiveMarkers++;
				} else {
					totalConsecutiveMarkers = 0;
					break;
				}
				if (totalConsecutiveMarkers == markersNeededToWin) {
					Log.d("VictoryCheck", "Diagonal Victory TL to BR");
					return true;
				}
			}
		}
		// A diagonal victory from the bottom left to the top right can only
		// occur if
		// the sum of the row and column positions is one less than the board
		// size.
		if (startRow + startColumn == gameBoardSize - 1) {
			for (int i = 0; i < gameBoardSize; i++) {
				// Java doesn't do equivalence classes / modulus correctly.
				// This is a workaround.
				int currentRowCalculator = (startRow - i) % gameBoardSize;
				if (currentRowCalculator < 0) {
					currentRowCalculator += gameBoardSize;
				}
				Log.d("VictoryCheckBRTL", "currentRow = " + currentRowCalculator);
				currentRow = gameBoardData.get(currentRowCalculator);
				int currentColumn = (startColumn + i) % gameBoardSize;
				Log.d("VictoryCheckBRTL", "currentColumn = " + currentColumn);
				int markerAtThisLocation = currentRow.get(currentColumn);
				if (markerAtThisLocation == playerKey) {
					totalConsecutiveMarkers++;
				} else {
					totalConsecutiveMarkers = 0;
					break;
				}
				if (totalConsecutiveMarkers == markersNeededToWin) {
					Log.d("VictoryCheck", "Diagonal Victory BL to TR");
					return true;
				}
			}
		}
		return false;
	}
	
	public ArrayList<GameAction> getActionHistory() {
		return mActionHistory;
	}


	private ArrayList<GameObserver> mGameObservers;
	private ArrayList<GameAction> mActionHistory;
	private State mGameState;
}
