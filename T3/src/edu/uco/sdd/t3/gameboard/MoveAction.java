package edu.uco.sdd.t3.gameboard;

/**
 * Represents the moves that player's make on the game board.
 * 
 * @author Joshua Ford
 * @version 1.0
 */
public class MoveAction extends GameAction {
	
	/**
	 * Creates a MoveAction object with the given coordinates and
	 * associated player ID.
	 * 
	 * @param game The current game that invokes this action.
	 * @param board The board that will receive this action.
	 * @param row The row coordinate of the move.
	 * @param column The column coordinate of the move.
	 * @param playerId The player ID of the player who is placing the marker.
	 */
	public MoveAction(Game game, Board board, int row, int column, int playerId) {
		super(game, board);
		mPosX = row;
		mPosY = column;
		mPlayerId = playerId;
	}
	
	@Override
	public void execute() {
		getBoard().placeMarker(this);
	}
	
	/**
	 * @return The column coordinate of the move.
	 */
	public int getX() {
		return mPosX;
	}
	
	/**
	 * @return The row coordinate of the move.
	 */
	public int getY() {
		return mPosY;
	}
	
	/**
	 * @return The player ID of the player that made this move.
	 */
	public int getPlayerId() {
		return mPlayerId;
	}
	
	private int mPosX;
	private int mPosY;
	private int mPlayerId;
}
