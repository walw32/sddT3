package edu.uco.sdd.t3.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.xmlpull.v1.XmlPullParserException;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import edu.uco.sdd.t3.R;
import edu.uco.sdd.t3.core.Board;
import edu.uco.sdd.t3.core.Game;
import edu.uco.sdd.t3.core.GameplayView;
import edu.uco.sdd.t3.core.MarkerImage;
import edu.uco.sdd.t3.core.MoveAction;
import edu.uco.sdd.t3.core.Player;
import edu.uco.sdd.t3.core.TimeoutClock;

public class ClientView extends GameplayView {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		serverIp = getIntent().getExtras().getString("IP");
		progressDialog = ProgressDialog.show(this, null,
				"Establishing Connection...", true);
		Thread networkThread = new Thread(new Runnable() {
			public void run() {
				try {
					// Establishing a network connection
					InetAddress serverAddress = InetAddress.getByName(serverIp);
					Log.d("ClientView", "Server IP: " + serverIp);
					Log.d("ClientView", "Server Address: " + serverAddress);
					Log.d("ClientView",
							"Server IP length = " + serverIp.length());
					serverSocket = new Socket(serverAddress, 40000);
					InputStreamReader inputStreamReader = new InputStreamReader(
							serverSocket.getInputStream());
					PrintWriter printWriter = new PrintWriter(serverSocket
							.getOutputStream(), true);
					serverInput = new BufferedReader(inputStreamReader);
					serverOutput = new BufferedWriter(printWriter);

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
									.setMessage("Receiving game information...");
						}
					});

					Log.d("ClientView", "Receiving game metadata...");
					// Receiving game metadata
					String gameMetadataStr = serverInput.readLine();
					NetworkParser parser = new NetworkParser();
					GameMetadata data = null;
					try {
						data = parser.parseGameData(gameMetadataStr);
					} catch (XmlPullParserException e1) {
						Log.d("NetworkPlayer", "Malformed XML!");
						e1.printStackTrace();
					}
					Log.d("ClientView", "Received game metadata.");
					Log.d("ClientView", "Metadata String: " + gameMetadataStr);
					if (data != null) {
						Log.d("ClientView", data.toString());
						gameType = data.getGameType();
						boardSize = data.getBoardSize();
						timeoutThreshold = data.getTimeoutLength();
					} else {
						gameType = 0;
						boardSize = 3;
						timeoutThreshold = 15;
					}
					Log.d("ClientView", "Did we receive data?");

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
					Log.d("ServerView", "Is clientSocket null? "
							+ (serverSocket == null));
					Log.d("ServerView", "Is mCurrentGame null? "
							+ (game == null));
					Log.d("ServerView", "Is mBoard null? " + (board == null));
					Player player1 = new NetworkPlayer(serverSocket, game,
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
					self.setBoard(board);
					self.setPlayer1(player1);
					self.setPlayer2(player2);

					// Cloud replay button that shows at the end of the game
					mMainThreadHandler.post(new Runnable() {
						public void run() {
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
			// Send data to the hosting player.
			sendData(action.toXmlString());
		}
	}
	
	private synchronized void sendData(final String data) {
		Thread networkThread = new Thread(new Runnable() {
			public void run() {
				try {
					Log.d("NetworkGame", "Sending data: " + data);
					serverOutput.write(data);
					serverOutput.newLine();
					serverOutput.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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

	private int boardSize;
	private int timeoutThreshold;
	private int gameType;

	private ClientView self = this;
	private Socket serverSocket;
	private BufferedReader serverInput;
	private BufferedWriter serverOutput;
	private ProgressDialog progressDialog;
	private String serverIp;
	private Handler mMainThreadHandler = new Handler();
}