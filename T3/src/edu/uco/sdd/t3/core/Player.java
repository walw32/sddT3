package edu.uco.sdd.t3.core;


/**
 * Represents the Player in a game of tic-tac-toe. 
 * 
 * @author Joshua Ford
 * @version 1.0
 */
public class Player {
	
	/**
	 * Creates a new Player object associated with a game, board, and a player ID.
	 * 
	 * @param newGame The game to associate this player with.
	 * @param gameBoard The board on which the player places markers.
	 * @param playerId The ID of the player, typically 1 or 2.
	 */
	public Player(Game newGame, Board gameBoard, int playerId) {
		mCurrentGame = newGame;
		mBoard = gameBoard;
		mPlayerId = playerId;
	}

	/**
	 * Places a marker on the board at the given coordinate.
	 * 
	 * @param x The x-coordinate (column)
	 * @param y The y-coordinate (row)
	 */
	public void placeMarker(int x, int y) {
		MoveAction action = new MoveAction(mCurrentGame, mBoard, x, y, mPlayerId);
		mCurrentGame.doAction(action);
	}

	/**
	 * @return The drawable image associated with the player.
	 */
	public MarkerImage getMarker() {
		return mMarker;
	}

	/**
	 * @param m The drawable image to associate with the player.
	 */
	public void setMarker(MarkerImage m) {
		mMarker = m;		
	}

	/**
	 * @return The ID of the player.
	 */
	public int getId() {
		return mPlayerId;
	}

	/**
	 * @param id The ID you want to set for the player, typically 1 or 2.
	 */
	public void setId(int id) {
		mPlayerId = id;		
	}

	private Game mCurrentGame;
	private Board mBoard;
	private MarkerImage mMarker;
	private int mPlayerId;
}
