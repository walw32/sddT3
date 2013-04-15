package edu.uco.sdd.t3.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import edu.uco.sdd.t3.R;
import edu.uco.sdd.t3.R.id;
import edu.uco.sdd.t3.R.layout;
import edu.uco.sdd.t3.core.Board;
import edu.uco.sdd.t3.core.GameplayView;
import edu.uco.sdd.t3.core.MarkerImage;
import edu.uco.sdd.t3.core.Player;
import edu.uco.sdd.t3.core.TimeoutClock;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ClientView extends GameplayView {

	private ClientView self = this;
	private Socket serverSocket;
	private BufferedReader serverInput;
	private BufferedWriter serverOutput;
	private ProgressDialog progressDialog;
	private String serverIp;
	private Handler mMainThreadHandler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.multiplayer_game_configuration);

		serverIp = getIntent().getExtras().getString("IP");		
		progressDialog = ProgressDialog.show(this, null, "Establishing Connection...", true);
		Thread networkThread = new Thread(new Runnable() {
			public void run() {
				try {
					// Establishing a network connection
					InetAddress serverAddress = InetAddress.getByName(serverIp);
					Log.d("ClientView", "Server Address: " + serverAddress);
					serverSocket = new Socket(serverAddress, 40000);
					InputStreamReader inputStreamReader = new InputStreamReader(
							serverSocket.getInputStream());
					PrintWriter printWriter = new PrintWriter(serverSocket.getOutputStream(), true);
					serverInput = new BufferedReader(inputStreamReader);
					serverOutput = new BufferedWriter(printWriter);
					
					// Updating progress dialog
					mMainThreadHandler.post(new Runnable() {
						public void run() {
							progressDialog.setMessage("Connection established!");
							try {
								wait(250);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							progressDialog.setMessage("Receiving game information...");
						}
					});
					
					// Sending game metadata
					String gameMetadata = serverInput.readLine();
					
					// Dismiss the progress dialog stylishly
					mMainThreadHandler.post(new Runnable() {
						public void run() {
							progressDialog.setMessage("Starting game...");
							try {
								wait(250);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							progressDialog.dismiss();
						}
					});
					
					// Set up the game
					mCurrentGame = new NetworkGame(serverSocket);
					mCurrentGame.attachObserver(self);
					TimeoutClock timer = new TimeoutClock(mHandler,
							timeoutThreshold);
					mCurrentGame.setTimer(timer);
					timer.attachGame(mCurrentGame);
					mBoard = new Board(boardSize);
					mBoard.attachObserver(self);
					mBoard.attachObserver(mCurrentGame);
					mPlayer1 = new NetworkPlayer(serverSocket, mCurrentGame, mBoard, 2);
					mPlayer2 = new Player(mCurrentGame, mBoard, 1);
					Drawable xImage = getResources().getDrawable(R.drawable.x_graphic);
					Drawable oImage = getResources().getDrawable(R.drawable.o_graphic);
					MarkerImage X = new MarkerImage(xImage);
					MarkerImage O = new MarkerImage(oImage);
					mPlayer1.setMarker(X);
					mPlayer2.setMarker(O);
					
					// Cloud replay button that shows at the end of the game
					View cloudButton = findViewById(R.id.cloudSave);
					View nextMoveButton = findViewById(R.id.nextMove);
					cloudButton.setVisibility(View.GONE);
					nextMoveButton.setVisibility(View.GONE);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});
		networkThread.start();
		
	}
	
	@Override
	public void onStop() {
		// Close the ClientView Socket connection here.
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		super.onStop();
	}
}