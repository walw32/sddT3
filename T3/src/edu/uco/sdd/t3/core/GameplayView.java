package edu.uco.sdd.t3.core;

// This is Jack's comment test

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import edu.uco.sdd.t3.Cloud;
import edu.uco.sdd.t3.R;
import edu.uco.sdd.t3.network.NetworkGame;
import edu.uco.sdd.t3.network.NetworkPlayer;

public class GameplayView extends Activity implements GameObserver,
		BoardObserver {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Grab data that was passed to us from the config screen.
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			try {
				gameType = (Integer) bundle.getSerializable("gameType");

				boardSize = (Integer) bundle.getSerializable("gameSize");
				timeoutThreshold = (Integer) bundle
						.getSerializable("gameTimeout") * 1000;
			} catch (NullPointerException ex) {
				gameType = 0;
				boardSize = 3;
				timeoutThreshold = 15 * 1000;
			}
		} else {
			gameType = 0;
			boardSize = 3;
			timeoutThreshold = 15 * 1000;
		}

		switch (boardSize) {
		case 3:
			setContentView(R.layout.activity_gameplay_view_3x3);
			break;
		case 4:
			setContentView(R.layout.activity_gameplay_view_4x4);
			break;
		case 5:
			setContentView(R.layout.activity_gameplay_view_5x5);
			break;
		}
		// not a cloud-replay game, default board size (at least until we
		// implement configuration screen)
		if (gameType == 1) {
			replayBoardSize = String.valueOf(boardSize);
			mCurrentGame = new Game();
			mCurrentGame.attachObserver(this);
			TimeoutClock timer = new TimeoutClock(mHandler, timeoutThreshold);
			mCurrentGame.setTimer(timer);
			timer.attachGame(mCurrentGame);
			mBoard = new Board(boardSize);
			mBoard.attachObserver(this);
			mBoard.attachObserver(mCurrentGame);
			mPlayer1 = new Player(mCurrentGame, mBoard, 1);
			mPlayer2 = new Player(mCurrentGame, mBoard, 2);
			Drawable xImage = getResources().getDrawable(R.drawable.x_graphic);
			Drawable oImage = getResources().getDrawable(R.drawable.o_graphic);
			MarkerImage X = new MarkerImage(xImage);
			MarkerImage O = new MarkerImage(oImage);
			mPlayer1.setMarker(X);
			mPlayer2.setMarker(O);
			View cloudButton = findViewById(R.id.cloudSave);
			View nextMoveButton = findViewById(R.id.nextMove);
			cloudButton.setVisibility(View.GONE);
			nextMoveButton.setVisibility(View.GONE);
		}
		// Network man vs man game - Deprecated, should be using ClientView or
		// ServerView
		else if (gameType == 2) {

		}
		// Network AI vs AI game - Deprecated, should be using ClientView or
		// ServerView
		else if (gameType == 3) {

		}
		// this means it's a cloud-replay game
		else if (gameType == 4) {
			gameHistory = (String) bundle.getSerializable("history");
			replayBoardSize = (String) bundle.getSerializable("boardSize");
			if (replayBoardSize.equals("3")) {
				setContentView(R.layout.activity_gameplay_view_3x3);
				boardSize = 3;
			} else if (replayBoardSize.equals("4")) {
				setContentView(R.layout.activity_gameplay_view_4x4);
				boardSize = 4;
			} else if (replayBoardSize.equals("5")) {
				setContentView(R.layout.activity_gameplay_view_5x5);
				boardSize = 5;
			}
			mCurrentGame = new Game();
			mCurrentGame.attachObserver(this);
			mBoard = new Board(boardSize);
			mBoard.attachObserver(this);
			mBoard.attachObserver(mCurrentGame);
			mPlayer1 = new Player(mCurrentGame, mBoard, 1);
			mPlayer2 = new Player(mCurrentGame, mBoard, 2);
			Drawable xImage = getResources().getDrawable(R.drawable.x_graphic);
			Drawable oImage = getResources().getDrawable(R.drawable.o_graphic);
			MarkerImage X = new MarkerImage(xImage);
			MarkerImage O = new MarkerImage(oImage);
			mPlayer1.setMarker(X);
			mPlayer2.setMarker(O);
			View cloudButton = findViewById(R.id.cloudSave);
			View nextMoveButton = findViewById(R.id.nextMove);
			cloudButton.setVisibility(View.GONE);
			nextMoveButton.setVisibility(View.GONE);
			cloudReplay();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gameplay_view, menu);
		return true;
	}

	// need a separate one for cloud replay buttons due to how marker is placed
	// in onButtonClicked...
	public boolean onReplayButtonClicked(View v) {
		int buttonId = v.getId();
		switch (buttonId) {
		// Cloud Save
		case R.id.cloudSave:
			cloudSave();
			break;
		// Cloud Replay
		case R.id.nextMove:
			cloudReplay();
			break;
		}
		return true;
	}

	public boolean onButtonClicked(View v) {
		int row = -1;
		int col = -1;
		int buttonId = v.getId();
		switch (buttonId) {
		/* ROW 1 **************************** */
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
		case R.id.row1col4:
			row = 0;
			col = 3;
			break;
		case R.id.row1col5:
			row = 0;
			col = 4;
			break;
		/* ROW 2 **************************** */
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
		case R.id.row2col4:
			row = 1;
			col = 3;
			break;
		case R.id.row2col5:
			row = 1;
			col = 4;
			break;
		/* ROW 3 **************************** */
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
		case R.id.row3col4:
			row = 2;
			col = 3;
			break;
		case R.id.row3col5:
			row = 2;
			col = 4;
			break;
		/* ROW 4 **************************** */
		case R.id.row4col1:
			row = 3;
			col = 0;
			break;
		case R.id.row4col2:
			row = 3;
			col = 1;
			break;
		case R.id.row4col3:
			row = 3;
			col = 2;
			break;
		case R.id.row4col4:
			row = 3;
			col = 3;
			break;
		case R.id.row4col5:
			row = 3;
			col = 4;
			break;
		/* ROW 5 **************************** */
		case R.id.row5col1:
			row = 4;
			col = 0;
			break;
		case R.id.row5col2:
			row = 4;
			col = 1;
			break;
		case R.id.row5col3:
			row = 4;
			col = 2;
			break;
		case R.id.row5col4:
			row = 4;
			col = 3;
			break;
		case R.id.row5col5:
			row = 4;
			col = 4;
			break;
		}

		placeMarker(row, col);
		return true;
	}

	@Override
	public void onMarkerPlaced(MoveAction action) {
		MarkerImage markerToPlace;
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
			} else if (column == 3) {
				buttonToReturn = (ImageButton) findViewById(R.id.row1col4);
			} else if (column == 4) {
				buttonToReturn = (ImageButton) findViewById(R.id.row1col5);
			}
		} else if (row == 1) {
			if (column == 0) {
				buttonToReturn = (ImageButton) findViewById(R.id.row2col1);
			} else if (column == 1) {
				buttonToReturn = (ImageButton) findViewById(R.id.row2col2);
			} else if (column == 2) {
				buttonToReturn = (ImageButton) findViewById(R.id.row2col3);
			} else if (column == 3) {
				buttonToReturn = (ImageButton) findViewById(R.id.row2col4);
			} else if (column == 4) {
				buttonToReturn = (ImageButton) findViewById(R.id.row2col5);
			}
		} else if (row == 2) {
			if (column == 0) {
				buttonToReturn = (ImageButton) findViewById(R.id.row3col1);
			} else if (column == 1) {
				buttonToReturn = (ImageButton) findViewById(R.id.row3col2);
			} else if (column == 2) {
				buttonToReturn = (ImageButton) findViewById(R.id.row3col3);
			} else if (column == 3) {
				buttonToReturn = (ImageButton) findViewById(R.id.row3col4);
			} else if (column == 4) {
				buttonToReturn = (ImageButton) findViewById(R.id.row3col5);
			}
		} else if (row == 3) {
			if (column == 0) {
				buttonToReturn = (ImageButton) findViewById(R.id.row4col1);
			} else if (column == 1) {
				buttonToReturn = (ImageButton) findViewById(R.id.row4col2);
			} else if (column == 2) {
				buttonToReturn = (ImageButton) findViewById(R.id.row4col3);
			} else if (column == 3) {
				buttonToReturn = (ImageButton) findViewById(R.id.row4col4);
			} else if (column == 4) {
				buttonToReturn = (ImageButton) findViewById(R.id.row4col5);
			}
		} else if (row == 4) {
			if (column == 0) {
				buttonToReturn = (ImageButton) findViewById(R.id.row5col1);
			} else if (column == 1) {
				buttonToReturn = (ImageButton) findViewById(R.id.row5col2);
			} else if (column == 2) {
				buttonToReturn = (ImageButton) findViewById(R.id.row5col3);
			} else if (column == 3) {
				buttonToReturn = (ImageButton) findViewById(R.id.row5col4);
			} else if (column == 4) {
				buttonToReturn = (ImageButton) findViewById(R.id.row5col5);
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

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.new_game:
			// List items
			final CharSequence[] items = { "3x3", "4x4", "5x5" };
			// Prepare the list dialog box
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			// Set its title
			builder.setTitle("Pick a game mode");
			// Set the list items and assign with the click listener
			builder.setItems(items, new DialogInterface.OnClickListener() {
				// Click listener
				public void onClick(DialogInterface dialog, int item) {
					if (item == 0) {
						newGame("3x3");

					}
					if (item == 1) {
						newGame("4x4");

					}
					if (item == 2) {
						newGame("5x5");

					}
				}
			});
			AlertDialog alert = builder.create();
			// display dialog box
			alert.show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void newGame(String gamemode) { // added parameter
		if (gamemode == "3x3") { // change to gamemode 3x3
			setContentView(R.layout.activity_gameplay_view_3x3);
			newGame(3);
			TimeoutClock timer = new TimeoutClock(mHandler, 15000);
			mCurrentGame.setTimer(timer);
			timer.attachGame(mCurrentGame);
		}
		if (gamemode == "4x4") { // change to gamemode 4x4
			setContentView(R.layout.activity_gameplay_view_4x4);
			newGame(4);
			TimeoutClock timer = new TimeoutClock(mHandler, 30000);
			mCurrentGame.setTimer(timer);
			timer.attachGame(mCurrentGame);
		}
		if (gamemode == "5x5") { // change to gamemode 5x5
			setContentView(R.layout.activity_gameplay_view_5x5);
			newGame(5);
			TimeoutClock timer = new TimeoutClock(mHandler, 45000);
			mCurrentGame.setTimer(timer);
			timer.attachGame(mCurrentGame);
		}
	}

	private void newGame(Integer boardSize) {
		mCurrentGame.detachObserver(this);
		mBoard.detachObserver(mCurrentGame);
		mCurrentGame = new Game();
		mCurrentGame.attachObserver(this);
		mBoard.detachObserver(this);
		mBoard = new Board(boardSize);
		mBoard.attachObserver(this);
		mBoard.attachObserver(mCurrentGame);
		mPlayer1 = new Player(mCurrentGame, mBoard, 1);
		mPlayer2 = new Player(mCurrentGame, mBoard, 2);
		Drawable xImage = getResources().getDrawable(R.drawable.x_graphic);
		Drawable oImage = getResources().getDrawable(R.drawable.o_graphic);
		MarkerImage X = new MarkerImage(xImage);
		MarkerImage O = new MarkerImage(oImage);
		mPlayer1.setMarker(X);
		mPlayer2.setMarker(O);
		View cloudButton = findViewById(R.id.cloudSave);
		View nextMoveButton = findViewById(R.id.nextMove);
		cloudButton.setVisibility(View.GONE);
		nextMoveButton.setVisibility(View.GONE);
		replayBoardSize = boardSize.toString();
	}

	@Override
	public void onGameOver(String message) {
		TextView gameMessage = (TextView) findViewById(R.id.victoryText);
		gameMessage.setText(message);

		// set button for cloud save visible

		View cloudButton = findViewById(R.id.cloudSave);
		View nextMoveButton = findViewById(R.id.nextMove);
		cloudButton.setVisibility(View.VISIBLE);
		nextMoveButton.setVisibility(View.GONE);

	}

	/*
	 * Last modified by Josh on 3/29/2013
	 * 
	 * Tried to add some compatibility to the cloudSave feature during my giant
	 * refactoring.
	 */
	public void cloudSave() {
		// Code for the cloud replay saving system
		mBoard.getGameBoard();
		ArrayList<GameAction> history = mCurrentGame.getActionHistory();
		MoveAction actions;
		gameHistory = "";
		for (int i = 0; i < history.size(); i++) {
			actions = (MoveAction) history.get(i);

			// do it in 3 lines because adding the ints on 1 line just
			// make a big int number.. this way preserves each value as
			// a string concatenation
			gameHistory += actions.getPlayerId();
			gameHistory += actions.getX();
			gameHistory += actions.getY();
		}
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Save Name");
		alert.setMessage("Enter a distinctive name, no spaces allowed.");

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Editable value = input.getText();
				// Do something with value!
				saveName = value.toString();

				Intent intent = new Intent(GameplayView.this, Cloud.class);
				intent.putExtra("action", "save");
				intent.putExtra("saveName", saveName);
				intent.putExtra("history", gameHistory);
				intent.putExtra("boardSize", replayBoardSize);
				startActivity(intent);
				finish();
			}
		});
		alert.show();
	}

	public void cloudReplay() {
		Log.d("REPLAY, GAMEPLAYVIEW", "MOVES = " + gameHistory);
		View nextMoveButton = findViewById(R.id.nextMove);
		nextMoveButton.setVisibility(View.VISIBLE);
		new CloudThread().execute("Replay");

	}

	class CloudThread extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... arg0) {
			// if there are still moves to be replayed
			if (replayCounter < gameHistory.length() - 2) {

				String temp = gameHistory.substring(replayCounter,
						replayCounter + 3);
				Log.d("ROW, COLUMN", "ARE = " + temp);
				final int row = Integer.parseInt(temp.substring(1, 2));
				final int column = Integer.parseInt(temp.substring(2));
				runOnUiThread(new Runnable() {
					public void run() {
						// stuff that updates ui
						placeMarker(row, column);
					}
				});
				replayCounter += 3;
			}
			// replay is finished, hide button
			else {
				runOnUiThread(new Runnable() {
					public void run() {
						View nextMoveButton = findViewById(R.id.nextMove);
						nextMoveButton.setVisibility(View.GONE);
					}
				});
			}
			/*
			 * // old looped way of replaying ... for (int i = 0; i <
			 * gameHistory.length() - 2; i += 3) { String temp =
			 * gameHistory.substring(i, i + 3); final int row =
			 * Integer.parseInt(temp.substring(1, 2)); final int column =
			 * Integer.parseInt(temp.substring(2)); runOnUiThread(new Runnable()
			 * { public void run() {
			 * 
			 * // stuff that updates ui
			 * 
			 * placeMarker(row, column); } });
			 * 
			 * try { Thread.sleep(1000); } catch (InterruptedException e) { //
			 * TODO Auto-generated catch block e.printStackTrace(); }
			 * 
			 * 
			 * }
			 */
			return null;
		}

		@Override
		protected void onProgressUpdate(String... arg0) {
			// TODO Auto-generated method stub

		}

	}

	// gameType will be '1' for single player, '2' for hosted-network game, '3'
	// for joined-network game, and '4' for cloud-replay

	private int replayCounter = 0;
	private String gameHistory;
	private String saveName;
	private String replayBoardSize;

	protected int gameType;
	protected Game mCurrentGame;
	protected Board mBoard;
	protected Player mPlayer1;
	protected Player mPlayer2;
	protected int boardSize;
	protected int timeoutThreshold;
	protected Handler mHandler = new Handler();
}