package edu.uco.sdd.t3.gameboard;

public class SlideExistingMarker implements MarkerPlacementStrategy {

	private Board mGameBoard;
	
	public SlideExistingMarker(Board board) {
		mGameBoard = board;
	}

	@Override
	public boolean placeMarker(MoveAction coord) {
		return false;
	}

}
