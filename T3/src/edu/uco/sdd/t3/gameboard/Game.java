package edu.uco.sdd.t3.gameboard;

import java.util.Vector;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import edu.uco.sdd.t3.R;

public class Game {
	public enum State {
		PLAYER_1_TURN, PLAYER_2_TURN, GAME_OVER
	}

	private Board mGameBoard;
	private Player mPlayer1;
	private Player mPlayer2;
	private OnMarkerPlacedListener mMarkerListener;
	private OnGameOverListener mGameEndListener;
	private Context mContext;
	private State mGameState;

	public Game(Context appContext, int boardSize) {
		mContext = appContext;
		mGameBoard = new Board(this, boardSize);
		mPlayer1 = new PlayerObject(this, 1);
		mPlayer2 = new PlayerObject(this, 2);
		Drawable xImage = mContext.getResources().getDrawable(
				R.drawable.x_graphic);
		Drawable oImage = mContext.getResources().getDrawable(
				R.drawable.o_graphic);
		Marker X = new Marker(xImage, mPlayer1);
		Marker O = new Marker(oImage, mPlayer2);
		mGameState = State.PLAYER_1_TURN;
	}

	public void placeMarker(MoveAction action) {
		if (mGameState == State.PLAYER_1_TURN) {
			if (action.getPlayerId() != mPlayer1.getId()) {
				return;
			}
			// If we can place the marker at that location, do stuff.
			if (mGameBoard.placeMarker(action)) {
				if (mMarkerListener != null) {
					mMarkerListener.onMarkerPlaced(action);
				}
				mGameState = State.PLAYER_2_TURN;
				if (doVictoryEvaluation(action)) {
					mGameState = State.GAME_OVER;
					Log.d("Game", "Game over! Player 1 wins!");
					if (mGameEndListener != null) {
						mGameEndListener.onGameOver("Game over! Player 1 wins!");
					}
				} else if (mGameBoard.isFilled()) {
					mGameState = State.GAME_OVER;
					stalemate();
				}
			} else {
				return;
			}
		} else if (mGameState == State.PLAYER_2_TURN) {
			if (action.getPlayerId() != mPlayer2.getId()) {
				return;
			}
			if (mGameBoard.placeMarker(action)) {
				if (mMarkerListener != null) {
					mMarkerListener.onMarkerPlaced(action);
				}
				mGameState = State.PLAYER_1_TURN;
				if (doVictoryEvaluation(action)) {
					mGameState = State.GAME_OVER;
					Log.d("Game", "Game over! Player 2 wins!");
					if (mGameEndListener != null) {
						mGameEndListener.onGameOver("Game over! Player 2 wins!");
					} 
				} else if (mGameBoard.isFilled()) {
					mGameState = State.GAME_OVER;
					stalemate();
				}
			} else {
				return;
			}
		} else if (mGameState == State.GAME_OVER) {
			return;
		}
	}

	public Player getPlayer1() {
		return mPlayer1;
	}

	public Player getPlayer2() {
		return mPlayer2;
	}

	public void setMarkerPlacedListener(OnMarkerPlacedListener l) {
		mMarkerListener = l;
	}
	
	public void setGameVictoryListener(OnGameOverListener l) {
		mGameEndListener = l;
	}

	public State getGameState() {
		return mGameState;
	}

	private boolean doVictoryEvaluation(MoveAction action) {
		Vector<Vector<Integer>> gameBoardData = mGameBoard.getGameBoard();
		int gameBoardSize = mGameBoard.getBoardSize();
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
	
	public void stalemate() {
		mGameEndListener.onGameOver("Curses! Stalemate!");
	}
}
