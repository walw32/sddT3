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
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import edu.uco.sdd.t3.R;
import edu.uco.sdd.t3.core.Board;
import edu.uco.sdd.t3.core.Game;
import edu.uco.sdd.t3.core.GameplayView;
import edu.uco.sdd.t3.core.MarkerImage;
import edu.uco.sdd.t3.core.MoveAction;
import edu.uco.sdd.t3.core.Player;
import edu.uco.sdd.t3.core.TimeoutClock;

public class ServerView extends GameplayView {	

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
				gameType = -1;
				boardSize = 3;
				timeoutThreshold = 15 * 1000;
			}
		} else {
			gameType = -2;
			boardSize = 3;
			timeoutThreshold = 15 * 1000;
		}
		Log.d("ServerView", "Setting content view based on boardSize = "
				+ boardSize);
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
					String boardSizeXml = "<BoardSize>" + boardSize + "</BoardSize>";
					String timeoutXml = "<Timeout>" + timeoutThreshold
							+ "</Timeout>";
					String gameMetadataXml = "<Game>" + gameTypeXml
							+ boardSizeXml + timeoutXml + "</Game>";
					clientOutput.write(gameMetadataXml, 0,
							gameMetadataXml.length());
					clientOutput.newLine();
					clientOutput.flush();

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
					Game game = new Game();
					game.attachObserver(self);
					TimeoutClock timer = new TimeoutClock(mMainThreadHandler,
							timeoutThreshold);
					game.setTimer(timer);
					timer.attachGame(game);
					Board board = new Board(boardSize);
					board.attachObserver(self);
					board.attachObserver(game);
					
					Log.d("ServerView", "Is clientSocket null? " + (clientSocket == null));
					Log.d("ServerView", "Is mCurrentGame null? " + (game == null));
					Log.d("ServerView", "Is mBoard null? " + (board == null));
					// Client goes first to serve as acknowledgement
					Player player1 = new NetworkPlayer(clientSocket, game,
							board, 1);
					Player player2 = new Player(game, board, 2);
					Drawable xImage = getResources().getDrawable(
							R.drawable.x_graphic);
					Drawable oImage = getResources().getDrawable(
							R.drawable.o_graphic);
					MarkerImage X = new MarkerImage(xImage);
					MarkerImage O = new MarkerImage(oImage);
					player1.setMarker(X);
					player2.setMarker(O);
					self.setCurrentGame(game);
					self.setTimer(timer);
					self.setBoard(board);
					self.setPlayer1(player1);
					self.setPlayer2(player2);			

					// Cloud replay button that shows at the end of the
					// game
					mMainThreadHandler.post(new Runnable() {
						public void run() {
							View cloudButton = findViewById(R.id.cloudSave);
							View nextMoveButton = findViewById(R.id.nextMove);
							cloudButton.setVisibility(View.GONE);
							nextMoveButton.setVisibility(View.GONE);
						}
					});
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});
		networkThread.start();
	}
	
	@Override
	public boolean onButtonClicked(View v) {
		if (getCurrentGame().getGameState() == Game.State.PLAYER_2_TURN) {
			// It's our turn.
			return super.onButtonClicked(v);
		} else {
			return false;
		}
		
	}
	
	@Override
	public void onMarkerPlaced(final MoveAction action) {
		super.onMarkerPlaced(action);
		if (getCurrentGame().getGameState() == Game.State.PLAYER_2_TURN) {
			// Send data to our network player.
			sendData(action.toXmlString());
		}
	}
	
	private synchronized void sendData(final String data) {
		Thread networkThread = new Thread(new Runnable() {
			public void run() {
				try {
					Log.d("NetworkGame", "Sending data: " + data);
					clientOutput.write(data);
					clientOutput.newLine();
					clientOutput.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
	
	private int boardSize;
	private int timeoutThreshold;
	private int gameType;
	
	private ServerView self = this;
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private BufferedReader clientInput;
	private BufferedWriter clientOutput;
	private ProgressDialog progressDialog;
	private Handler mMainThreadHandler = new Handler();
}