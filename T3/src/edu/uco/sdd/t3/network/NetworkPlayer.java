package edu.uco.sdd.t3.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import android.util.Log;

import edu.uco.sdd.t3.core.Board;
import edu.uco.sdd.t3.core.Game;
import edu.uco.sdd.t3.core.Player;

public class NetworkPlayer extends Player {

	public NetworkPlayer(Game newGame, Board gameBoard, int playerId) {
		super(newGame, gameBoard, playerId);
	}
	
	public NetworkPlayer(Socket socket, Game newGame, Board gameBoard, int playerId) {
		super(newGame, gameBoard, playerId);
		mSocket = socket;
		try {
			mIn = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		mInputListener.start();
	}
	
	public void setSocket(Socket socket) {
		mSocket = socket;
		try {
			mIn = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		mInputListener.start();
	}
	
	public void close() throws IOException {
		mInputListener.interrupt();
		if (mIn != null) {
			mIn.close();
		}
		if (mSocket != null) {
			mSocket.close();
		}
	}
	
	private Socket mSocket;
	private BufferedReader mIn;
	private Thread mInputListener = new Thread(new Runnable() {
		public void run() {
			try {
			while (!mSocket.isClosed()) { 	     
				if (mIn.ready()) {
	                String message = mIn.readLine();
	                Log.d("NetworkPlayer", "Received message: " + message);
	                // TODO: Parse out the data
				}
	                Thread.sleep(1);
			}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				
			}
		}
	});

}
