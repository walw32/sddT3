package edu.uco.sdd.t3.gameboard;

import edu.uco.sdd.t3.gameboard.Game.Mode;

/**
 * A GameAction that signifies a change of state in the Game.
 * Typically, this GameAction is used to tell the board to
 * switch marker placement strategies in the current game.
 * 
 * @author Joshua Ford
 * @version 1.0
 */
public class ChangeStrategyAction extends GameAction {

	/**
	 * Creates a ChangeStrategyAction coupled with a Game and a
	 * Board object to send and receive messages to.
	 * 
	 * @param game The current game that will invoke this action.
	 * @param board The board that will receive this action.
	 */
	public ChangeStrategyAction(Game game, Board board) {
		super(game, board);
	}

	/**
	 * Executes this action.
	 */
	@Override
	public void execute() {
		Board gameBoard = getBoard();
		Game game = getGame();
		if (game.getGameMode() == Mode.SUDDEN_DEATH) {
			gameBoard.setPlaceMarkerStrategy(new SlideExistingMarker(gameBoard));
		} else {
			gameBoard.setPlaceMarkerStrategy(new PlaceMarkerDirectly(gameBoard));
		}
	}

}
