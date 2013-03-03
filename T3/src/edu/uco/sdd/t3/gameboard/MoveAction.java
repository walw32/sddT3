package edu.uco.sdd.t3.gameboard;

public class MoveAction {
	private int mPosX;
	private int mPosY;
	private int mPlayerId;
	
	public MoveAction(int row, int column, int playerId) {
		mPosX = row;
		mPosY = column;
		mPlayerId = playerId;
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
}
