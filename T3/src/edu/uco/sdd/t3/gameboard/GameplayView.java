package edu.uco.sdd.t3.gameboard;

import edu.uco.sdd.t3.R;
import edu.uco.sdd.t3.R.layout;
import edu.uco.sdd.t3.R.menu;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class GameplayView extends Activity implements OnMarkerPlacedListener, OnGameOverListener {

	private Game mCurrentGame;
	private Player mPlayer1;
	private Player mPlayer2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gameplay_view);
		int boardSize = 3;
		mCurrentGame = new Game(this, boardSize);
		mCurrentGame.setMarkerPlacedListener(this);
		mCurrentGame.setGameVictoryListener(this);
		mPlayer1 = mCurrentGame.getPlayer1();
		mPlayer2 = mCurrentGame.getPlayer2();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gameplay_view, menu);
		return true;
	}

	public boolean onButtonClicked(View v) {
		int row = -1;
		int col = -1;
		int buttonId = v.getId();
		switch (buttonId) {
			case R.id.row1col1:
				row = 0;
				col = 0;
				break;
			case R.id.row1col2:
				row = 0;
				col = 1;
				break;
			case R.id.row1col3:
				row = 0;
				col = 2;
				break;
			case R.id.row2col1:
				row = 1;
				col = 0;
				break;
			case R.id.row2col2:
				row = 1;
				col = 1;
				break;
			case R.id.row2col3:
				row = 1;
				col = 2;
				break;
			case R.id.row3col1:
				row = 2;
				col = 0;
				break;
			case R.id.row3col2:
				row = 2;
				col = 1;
				break;
			case R.id.row3col3:
				row = 2;
				col = 2;
				break;
		}
		placeMarker(row, col);
		return true;
	}

	@Override
	public void onMarkerPlaced(MoveAction action) {
		Marker markerToPlace;
		int playerId = action.getPlayerId();
		if (playerId == mPlayer1.getId()) {
			markerToPlace = mPlayer1.getMarker();
		} else {
			markerToPlace = mPlayer2.getMarker();
		} 
		Drawable markerImage = markerToPlace.getDrawable();
		int row = action.getX();
		int column = action.getY();
		ImageButton imageToUpdate = getImageButtonAtLocation(row, column);
		imageToUpdate.setImageDrawable(markerImage);
	}

	private ImageButton getImageButtonAtLocation(int row, int column) {
		ImageButton buttonToReturn = null;
		if (row == 0) {
			if (column == 0) {
				buttonToReturn = (ImageButton) findViewById(R.id.row1col1);
			} else if (column == 1) {
				buttonToReturn = (ImageButton) findViewById(R.id.row1col2);
			} else if (column == 2) {
				buttonToReturn = (ImageButton) findViewById(R.id.row1col3);
			}
		} else if (row == 1) {
			if (column == 0) {
				buttonToReturn = (ImageButton) findViewById(R.id.row2col1);
			} else if (column == 1) {
				buttonToReturn = (ImageButton) findViewById(R.id.row2col2);
			} else if (column == 2) {
				buttonToReturn = (ImageButton) findViewById(R.id.row2col3);
			}
		} else if (row == 2) {
			if (column == 0) {
				buttonToReturn = (ImageButton) findViewById(R.id.row3col1);
			} else if (column == 1) {
				buttonToReturn = (ImageButton) findViewById(R.id.row3col2);
			} else if (column == 2) {
				buttonToReturn = (ImageButton) findViewById(R.id.row3col3);
			}
		}
		return buttonToReturn;
	}

	private void placeMarker(int row, int column) {
		if (mCurrentGame.getGameState() == Game.State.PLAYER_1_TURN) {
			Log.d("Player 1", "row = " + row + " col = " + column);
			mPlayer1.placeMarker(row, column);
		} else if (mCurrentGame.getGameState() == Game.State.PLAYER_2_TURN) {
			Log.d("Player 2", "row = " + row + " col = " + column);
			mPlayer2.placeMarker(row, column);
		}
	}

	@Override
	public void onGameOver(String message) {
		TextView gameMessage = (TextView) findViewById(R.id.victoryText);
		gameMessage.setText(message);
	}

}
