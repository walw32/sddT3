package edu.uco.sdd.t3.core;


import java.util.ArrayList;
import java.util.Vector;


import android.util.Log;

/**
 * Controls the rules of the game and serves as an interface for Player
 * objects to communicate with the board. Also observes the board and
 * is notified whenever markers are successfully placed so it can
 * check for player victory.
 * 
 * @author Joshua Ford
 * @version 1.0
 */
public class Game implements BoardObserver{

	/**
	 * Represents the two different game modes: Normal and Sudden Death.
	 * The Normal game mode is used for regular tic-tac-toe.
	 * The Sudden Death game mode limits the number of markers that
	 * are able to be placed on the board, after which the markers
	 * are slid around the surface of the board.
	 */
	public enum Mode {
		NORMAL, SUDDEN_DEATH
	}
	
	/**
	 * Represents the state of the current game. Players take turns
	 * placing markers, and a victory check is done whenever markers
	 * are successfully placed. The previous turn is taken into effect
	 * to determine the winner of the game.
	 */
	public enum State {
		PLAYER_1_TURN, PLAYER_2_TURN, GAME_OVER
	}

	/**
	 * Creates a default game with the normal game mode.
	 */
	public Game() {
		mGameObservers = new ArrayList<GameObserver>();
		mActionHistory = new ArrayList<GameAction>();
		mGameState = State.PLAYER_1_TURN;
		mGameMode = Mode.NORMAL;
	}
	
	/**
	 * Creates a game with a specific game mode.
	 * 
	 * @param gameMode The game mode to select. Typically "Normal" or "Sudden Death".
	 */
	public Game(Mode gameMode) {
		mGameObservers = new ArrayList<GameObserver>();
		mActionHistory = new ArrayList<GameAction>();
		mGameState = State.PLAYER_1_TURN;
		mGameMode = gameMode;
	}

	/**
	 * This method is called whenever a Board object
	 * successfully places a marker. A victory evaluation
	 * is done, and if there is a victory, then any observers
	 * of this game object are notified that the game is over.
	 * 
	 * If there is not a victory and all markers are placed on
	 * the board, then the game is in stalemate, and all observers
	 * are notified again that a stalemate has occurred.
	 * 
	 * If none of the other conditions are true, then it is a
	 * regular move, and the game changes state to reflect
	 * whose turn it is.
	 * 
	 * @param The MoveAction used to place a marker on the board.
	 */
	@Override
	public void onMarkerPlaced(MoveAction action) {
		if (mTimer != null) {
			mTimer.stop();
		}
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
		} else if(action.getBoard().isFilled()) {
			notify("Curses! Stalemate!");
		} else {
			if (mGameState == State.PLAYER_1_TURN) {
				mGameState = State.PLAYER_2_TURN;
			} else if (mGameState == State.PLAYER_2_TURN) {
				mGameState = State.PLAYER_1_TURN;
			}
		}
		if (mTimer != null) {
			mTimer.reset();
			mTimer.start();
		}
	}
	
	/**
	 * This method should be called from the Game's TimeoutClock
	 * to tell the Game that it should end because the time is up.
	 */
	public void timeExhausted() {
		if (mGameState == State.PLAYER_1_TURN) {
			notify("Game over! Player 1 failed to move in time!");
		} else if (mGameState == State.PLAYER_2_TURN) {
			notify("Game over! Player 2 failed to move in time!");
		}
	}
	
	/**
	 * Notifies all observers of this game that the game is over.
	 * 
	 * @param message The message to send to those observers.
	 */
	public void notify(String message) {
		mGameState = State.GAME_OVER;
		for (GameObserver observer : mGameObservers) {
			observer.onGameOver(message);
		}
	}

	/**
	 * Attaches an observer to this game object.
	 * 
	 * @param observer The GameObserver to attach.
	 */
	public void attachObserver(GameObserver observer) {
		mGameObservers.add(observer);
	}
	
	/**
	 * Removes an existing observer from this game object.
	 * 
	 * @param observer The GameObserver to remove.
	 */
	public void detachObserver(GameObserver observer) {
		mGameObservers.remove(observer);
	}
	
	/**
	 * Adds a given GameAction to its history and then executes it.
	 * 
	 * @param action The GameAction to execute.
	 */
	public void doAction(GameAction action) {
		mActionHistory.add(action);
		action.execute();
	}
	
	/**
	 * @return The current state of the game.
	 */
	public State getGameState() {
		return mGameState;
	}
	
	/**
	 * @return The current game mode being played.
	 */
	public Mode getGameMode() {
		return mGameMode;
	}

	/**
	 * Checks for a victory along all possible paths by 
	 * extracting relevant data from a MoveAction.
	 * 
	 * @param action The MoveAction that encapsulates a player's move.
	 * @return True if that player is victorious, false otherwise.
	 */
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
	
	/**
	 * @return An array list of all actions this game has executed.
	 */
	public ArrayList<GameAction> getActionHistory() {
		return mActionHistory;
	}
	
	/**
	 * @return The TimeoutClock that this game uses to keep track of time.
	 */
	public TimeoutClock getTimer() {
		return mTimer;
	}
	
	/**
	 * Sets the TimeoutClock object that the game uses to keep track of time.
	 * @param timer The TimeoutClock object to use to keep track of time.
	 */
	public void setTimer(TimeoutClock timer) {
		mTimer = timer;
	}


	private ArrayList<GameObserver> mGameObservers;
	private ArrayList<GameAction> mActionHistory;
	private State mGameState;
	private Mode mGameMode;
	private TimeoutClock mTimer;
}
