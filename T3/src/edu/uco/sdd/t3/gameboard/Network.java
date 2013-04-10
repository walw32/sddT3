package edu.uco.sdd.t3.gameboard;

import java.io.BufferedReader;
import edu.uco.sdd.t3.R;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class Network extends Activity {

	private int gameSize;
	private int threshold = 15;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.multiplayer_game_configuration);

		// BoardSize RadioGroup
		RadioGroup boardSizes = (RadioGroup) findViewById(R.id.boardSizes);
		int selectedSize = boardSizes.getCheckedRadioButtonId();
		RadioButton boardSize = (RadioButton) findViewById(selectedSize);
		if (boardSize.getText().equals("3x3")) {
			gameSize = 3;
		} else if (boardSize.getText().equals("4x4")) {
			gameSize = 4;
		} else if (boardSize.getText().equals("5x5")) {
			gameSize = 5;
		}
		// End of BoardSize RadioGroup
		// ---------------------------
		// Timeout RadioGroup
		RadioGroup timeoutThresholds = (RadioGroup) findViewById(R.id.timeoutThresholds);
		int selectedTimeout = timeoutThresholds.getCheckedRadioButtonId();
		RadioButton timeout = (RadioButton) findViewById(selectedTimeout);
		timeout.getText();
		if (timeout.getText().equals("15 sec.")) {
			threshold = 15;
		} else if (timeout.getText().equals("30 sec.")) {
			threshold = 30;
		} else if (timeout.getText().equals("45 sec.")) {
			threshold = 45;
		}
		// End of Timeout RadioGroup
		// ManvsMan RadioButton
		RadioButton manvsman = (RadioButton) findViewById(R.id.manVsManButton);
		boolean ismanvsman = manvsman.isSelected();
		if (ismanvsman == true) {

		}
		// End of ManvsMan RadioButton
		// ---------------------------
		// AIvsAI RadioButton
		RadioButton aivsai = (RadioButton) findViewById(R.id.aiVsAiButton);
		boolean isaivsai = aivsai.isSelected();
		if (isaivsai == true) {

		}
		// End of AIvsAI RadioButton

		TextView text = (TextView) findViewById(R.id.IPText);
		text.setText(getIP());

		View.OnClickListener handler = new View.OnClickListener() {
			public void onClick(View v) {
				switch (v.getId()) {

				case R.id.hostGameButton: // Host & Play button

					Intent intent1 = new Intent(Network.this, Server.class);
					startActivity(intent1);

					break;
				case R.id.joinGameButton: // Join Game
					EditText ipText = (EditText) findViewById(R.id.hostIp);
					String ipString = ipText.getText().toString();

					Intent intent2 = new Intent(Network.this, Client.class);
					Bundle bundle = new Bundle();
					bundle.putString("IP", ipString);
					intent2.putExtras(bundle);
					startActivity(intent2);

					break;
				}
			}
		};
		findViewById(R.id.hostGameButton).setOnClickListener(handler);
		findViewById(R.id.joinGameButton).setOnClickListener(handler);
	}

	public String getIP() { // This is used by the Host to retrieve its own IP
							// Address
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ip = wifiInfo.getIpAddress();
		String address = ((ip >> 24) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "."
				+ ((ip >> 8) & 0xFF) + "." + (ip & 0xFF);
		return address;
	}
}