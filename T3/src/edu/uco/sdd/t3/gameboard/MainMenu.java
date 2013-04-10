package edu.uco.sdd.t3.gameboard;

import edu.uco.sdd.t3.R;
import edu.uco.sdd.t3.R.layout;
import edu.uco.sdd.t3.R.menu;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainMenu extends Activity {
	private int gameSize;
	private int threshold = 15;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main_menu, menu);
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		setContentView(R.layout.activity_main_menu);
	}

	// to allow for going back from between content views for game
	// configurations
	@Override
	public void onBackPressed() {
		setContentView(R.layout.activity_main_menu);
		return;
	}

	public boolean onMenuButtonClicked(View v) {
		int buttonId = v.getId();
		switch (buttonId) {

		// 3 BUTTONS ON INITIAL MENU SCREEN //
		// if user chooses a single player game, bring up config for single
		// player
		case R.id.singlePlayerButton:
			setContentView(R.layout.single_player_game_configuration);
			break;
		// if user chooses a multiplayer game, bring up config for multiplayer
		case R.id.multiPlayerButton:
			setContentView(R.layout.multiplayer_game_configuration);
			break;
		// cloud replay game
		case R.id.cloudButton:
			Intent intent3 = new Intent(MainMenu.this, Cloud.class);
			intent3.putExtra("action", "replay");
			startActivity(intent3);
			break;

		// BUTTONS FOR CONFIGURATION SUBMISSION

		// single player game, gameType is 1
		case R.id.playGame:
			// Gets a reference to our radio group
			RadioGroup boardSizes = (RadioGroup) findViewById(R.id.boardSizes);
			// Returns an integer which represents the selected radio button's
			// ID
			int selectedSize = boardSizes.getCheckedRadioButtonId();
			// Gets a reference to our "selected" radio button
			RadioButton boardSize = (RadioButton) findViewById(selectedSize);
			// Now you can get the text or whatever you want from the "selected"
			// radio button

			if (boardSize.getText().equals("3x3")) {
				gameSize = 3;
			} else if (boardSize.getText().equals("4x4")) {
				gameSize = 4;
			} else if (boardSize.getText().equals("5x5")) {
				gameSize = 5;
			}

			RadioGroup timeoutThresholds = (RadioGroup) findViewById(R.id.timeoutThresholds);
			// Returns an integer which represents the selected radio button's
			// ID
			int selectedTimeout = timeoutThresholds.getCheckedRadioButtonId();
			// Gets a reference to our "selected" radio button
			RadioButton timeout = (RadioButton) findViewById(selectedTimeout);
			// Now you can get the text or whatever you want from the "selected"
			// radio button
			timeout.getText();

			if (timeout.getText().equals("15 sec.")) {
				threshold = 15;
			} else if (timeout.getText().equals("30 sec.")) {
				threshold = 30;
			} else if (timeout.getText().equals("45 sec.")) {
				threshold = 45;
			}
			setContentView(R.layout.single_player_game_configuration);
			Intent intent = new Intent(MainMenu.this, GameplayView.class);
			intent.putExtra("gameType", 1);
			intent.putExtra("gameSize", gameSize);
			intent.putExtra("gameTimeout", threshold);
			startActivity(intent);
			break;

		// network buttons

		// host game, gameType is 2
		case R.id.hostGameButton:

			break;

		// join game, gameType is 3
		case R.id.joinGameButton:

			break;

		}
		return true;
	}
}
