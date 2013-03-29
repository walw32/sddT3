package edu.uco.sdd.t3.gameboard;

public class Player {
	
	public Player(Game newGame, Board gameBoard, int playerId) {
		mCurrentGame = newGame;
		mBoard = gameBoard;
		mPlayerId = playerId;
	}

	public void placeMarker(int x, int y) {
		MoveAction action = new MoveAction(mCurrentGame, mBoard, x, y, mPlayerId);
		mCurrentGame.doAction(action);
	}

	public MarkerImage getMarker() {
		return mMarker;
	}

	public void setMarker(MarkerImage m) {
		mMarker = m;		
	}

	public int getId() {
		return mPlayerId;
	}

	public void setId(int id) {
		mPlayerId = id;		
	}

	private Game mCurrentGame;
	private Board mBoard;
	private MarkerImage mMarker;
	private int mPlayerId;
}
