package edu.uco.sdd.t3.core;


/**
 * Represents an action that is being done in the game. Typically instantiated
 * by players in the game as its subclass, MoveAction.
 * 
 * @author Joshua Ford
 * @version 1.0
 */
public abstract class GameAction {

	/**
	 * Creates a MoveAction for the particular game on the given board.
	 * 
	 * @param game The current game that invokes this action.
	 * @param board The board that receives this action.
	 */
	public GameAction(Game game, Board board) {
		mGame = game;
		mBoard = board;
	}
	
	/**
	 * Executes the action.
	 */
	public abstract void execute();
	
	/**
	 * @return The game board associated with this action.
	 */
	public Board getBoard() {
		return mBoard;
	}
	
	/**
	 * @return The current game that invokes this action.
	 */
	public Game getGame() {
		return mGame;
	}

	private Game mGame;
	private Board mBoard;

}
