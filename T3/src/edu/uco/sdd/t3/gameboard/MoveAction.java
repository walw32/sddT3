package edu.uco.sdd.t3.gameboard;

public class MoveAction extends GameAction {
	
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
	
	public int getX() {
		return mPosX;
	}
	
	public int getY() {
		return mPosY;
	}
	
	public int getPlayerId() {
		return mPlayerId;
	}
	
	private int mPosX;
	private int mPosY;
	private int mPlayerId;
}
