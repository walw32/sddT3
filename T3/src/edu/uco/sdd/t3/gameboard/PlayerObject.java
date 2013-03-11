package edu.uco.sdd.t3.gameboard;

public class PlayerObject implements Player {
	
	public PlayerObject(Game newGame, int playerId) {
		mCurrentGame = newGame;
		mPlayerId = playerId;
	}

	@Override
	public void placeMarker(int x, int y) {
		MoveAction action = new MoveAction(x, y, mPlayerId);
		mCurrentGame.placeMarker(action);
	}

	@Override
	public Marker getMarker() {
		return mMarker;
	}

	@Override
	public void setMarker(Marker m) {
		mMarker = m;		
	}

	@Override
	public int getId() {
		return mPlayerId;
	}

	@Override
	public void setId(int id) {
		mPlayerId = id;		
	}


	private Game mCurrentGame;
	private Marker mMarker;
	private int mPlayerId;
}
