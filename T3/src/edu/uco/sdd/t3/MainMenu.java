package edu.uco.sdd.t3;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import edu.uco.sdd.t3.core.GameplayView;
import edu.uco.sdd.t3.network.ClientView;
import edu.uco.sdd.t3.network.ServerView;

public class MainMenu extends Activity {
	private int gameSize = 3;
	private int gameType = 0;
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

	public boolean onTimeoutButtonClicked(View v) {
		int buttonId = v.getId();
		switch(buttonId) {
		case R.id.fifteen:
			threshold = 15;
			break;
		case R.id.thirty:
			threshold = 30;
			break;
		case R.id.fortyfive:
			threshold = 45;
			break;
		}
		Log.d("MainMenu", "Threshold changed to " + threshold);
		return true;
	}
	
	public boolean onBoardSizeButtonClicked(View v) {
		int buttonId = v.getId();
		switch(buttonId) {
		case R.id.three:
			gameSize = 3;
			break;
		case R.id.four:
			gameSize = 4;
			break;
		case R.id.five:
			gameSize = 5;
			break;
		}
		Log.d("MainMenu", "Board size changed to " + gameSize);
		return true;
	}
	
	public boolean onGameTypeSelected(View v) {
		int buttonId = v.getId();
		switch(buttonId) {
		case R.id.manVsManButton:
			gameType = 2;
			break;
		case R.id.aiVsAiButton:
			gameType = 3;
			break;
		}
		Log.d("MainMenu", "Game type changed to " + gameType);
		return true;
	}
	
	public boolean onMenuButtonClicked(View v) {
		int buttonId = v.getId();
		Intent intent;
		int selectedSize;
		int selectedTimeout;
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
			String ipAddress = getIpAddress();
			TextView textView = (TextView) findViewById(R.id.IPText);
			textView.setText("Host IP Address: " + ipAddress);
			break;
		// cloud replay game
		case R.id.cloudButton:
			intent = new Intent(MainMenu.this, Cloud.class);
			intent.putExtra("action", "replay");
			startActivity(intent);
			break;

		// BUTTONS FOR CONFIGURATION SUBMISSION
		//
		// single player game, gameType is 1
		case R.id.playGame:
			// Gets a reference to our radio group
			RadioGroup boardSizes = (RadioGroup) findViewById(R.id.boardSizes);
			// Returns an integer which represents the selected radio button's
			// ID
			selectedSize = boardSizes.getCheckedRadioButtonId();
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
			selectedTimeout = timeoutThresholds.getCheckedRadioButtonId();
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
			intent = new Intent(MainMenu.this, GameplayView.class);
			intent.putExtra("gameType", 1);
			intent.putExtra("gameSize", gameSize);
			intent.putExtra("gameTimeout", threshold);
			startActivity(intent);
			break;

		// network buttons

		// host game, gameType is 2
		case R.id.hostGameButton:
			Intent hostIntent = new Intent(MainMenu.this, ServerView.class);
			hostIntent.putExtra("gameType", gameType);
			hostIntent.putExtra("gameSize", gameSize);
			hostIntent.putExtra("gameTimeout", threshold);
			startActivity(hostIntent);
			break;

		// join game, gameType is 3
		case R.id.joinGameButton:
			Intent clientIntent = new Intent(MainMenu.this, ClientView.class);
			EditText textbox = (EditText) findViewById(R.id.hostIp);
			String serverIpAddress = textbox.getText().toString();
			clientIntent.putExtra("IP", serverIpAddress);
			startActivity(clientIntent);
			break;

		}
		return true;
	}
	
	private String getIpAddress() {
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ip = wifiInfo.getIpAddress();
		String address = (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." + ((ip >> 24) & 0xFF);
		return address;
	}
}
