package edu.uco.sdd.t3.network;

public class GameMetadata {

	public GameMetadata() {
		// TODO Auto-generated constructor stub
	}
	
	public int getGameType() {
		return mGameType;
	}
	public void setGameType(int mGameType) {
		this.mGameType = mGameType;
	}
	public int getBoardSize() {
		return mBoardSize;
	}
	public void setBoardSize(int mBoardSize) {
		this.mBoardSize = mBoardSize;
	}
	public int getTimeoutLength() {
		return mTimeoutLength;
	}
	public void setTimeoutLength(int mTimeoutLength) {
		this.mTimeoutLength = mTimeoutLength;
	}

	@Override
	public String toString() {
		return "GameMetadata [mGameType=" + mGameType + ", mBoardSize="
				+ mBoardSize + ", mTimeoutLength=" + mTimeoutLength + "]";
	}



	private int mGameType;
	private int mBoardSize;
	private int mTimeoutLength;

}
