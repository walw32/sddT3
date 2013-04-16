package edu.uco.sdd.t3.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import edu.uco.sdd.t3.R;
import edu.uco.sdd.t3.core.Board;
import edu.uco.sdd.t3.core.GameplayView;
import edu.uco.sdd.t3.core.MarkerImage;
import edu.uco.sdd.t3.core.Player;
import edu.uco.sdd.t3.core.TimeoutClock;

public class ServerView extends GameplayView {

	private ServerView self = this;
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private BufferedReader clientInput;
	private BufferedWriter clientOutput;
	private ProgressDialog progressDialog;
	private Handler mMainThreadHandler = new Handler();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			serverSocket = new ServerSocket(40000); // ServerView socket
		} catch (IOException e) {
			System.out.println("Could not listen on port: 40000");
		}

		progressDialog = ProgressDialog.show(this, null,
				"Awaiting Connection...", true);
		Thread networkThread = new Thread(new Runnable() {
			public void run() {
				try {
					// Establishing a network connection
					clientSocket = serverSocket.accept();
					InputStreamReader inputStreamReader = new InputStreamReader(
							clientSocket.getInputStream());
					PrintWriter printWriter = new PrintWriter(
							clientSocket.getOutputStream(), true);
					clientInput = new BufferedReader(inputStreamReader);
					clientOutput = new BufferedWriter(printWriter);

					// Updating progress dialog
					mMainThreadHandler.post(new Runnable() {
						public void run() {
							progressDialog
									.setMessage("Connection established!");
							try {
								Thread.sleep(250);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							progressDialog
									.setMessage("Sending game information...");
						}
					});

					// Sending game metadata
					String gameTypeXml = "<Type>" + 0 + "</Type>";
					String boardSizeXml = "<Size>" + boardSize + "</Size>";
					String timeoutXml = "<Timeout>" + timeoutThreshold
							+ "</Timeout>";
					String gameMetadataXml = "<Game>" + gameTypeXml
							+ boardSizeXml + timeoutXml + "</Game>";
					clientOutput.write(gameMetadataXml, 0, gameMetadataXml.length());

					// Dismiss the progress dialog stylishly
					mMainThreadHandler.post(new Runnable() {
						public void run() {
							progressDialog.setMessage("Starting game...");
							try {
								Thread.sleep(250);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							progressDialog.dismiss();
						}
					});

					// Set up the game
					mCurrentGame = new NetworkGame(clientSocket);
					mCurrentGame.attachObserver(self);
					TimeoutClock timer = new TimeoutClock(mHandler,
							timeoutThreshold);
					mCurrentGame.setTimer(timer);
					timer.attachGame(mCurrentGame);
					mBoard = new Board(boardSize);
					mBoard.attachObserver(self);
					mBoard.attachObserver(mCurrentGame);
					mPlayer1 = new Player(mCurrentGame, mBoard, 1);
					mPlayer2 = new NetworkPlayer(clientSocket, mCurrentGame,
							mBoard, 2);
					Drawable xImage = getResources().getDrawable(
							R.drawable.x_graphic);
					Drawable oImage = getResources().getDrawable(
							R.drawable.o_graphic);
					MarkerImage X = new MarkerImage(xImage);
					MarkerImage O = new MarkerImage(oImage);
					mPlayer1.setMarker(X);
					mPlayer2.setMarker(O);

					// Cloud replay button that shows at the end of the
					// game
					mMainThreadHandler.post(new Runnable() {
						public void run() {
							View cloudButton = findViewById(R.id.cloudSave);
							View nextMoveButton = findViewById(R.id.nextMove);
							//cloudButton.setVisibility(View.GONE);
							//nextMoveButton.setVisibility(View.GONE);
						}
					});
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});
		networkThread.start();
	}

	public void onStop() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
		try {
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onStop();
	}
}