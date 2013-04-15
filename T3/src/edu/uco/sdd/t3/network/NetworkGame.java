package edu.uco.sdd.t3.network;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import edu.uco.sdd.t3.core.Game;
import edu.uco.sdd.t3.core.GameAction;
import edu.uco.sdd.t3.core.MoveAction;

public class NetworkGame extends Game {

	public NetworkGame() {
		
	}
	
	public NetworkGame(Socket socket) {
		mSocket = socket;
		try {
			mOut = new BufferedWriter(new PrintWriter(socket.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public NetworkGame(Mode gameMode) {
		super(gameMode);
	}
	
	public NetworkGame(Socket socket, Mode gameMode) {
		super(gameMode);
		mSocket = socket;
		try {
			mOut = new BufferedWriter(new PrintWriter(socket.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onMarkerPlaced(MoveAction action) {
		super.onMarkerPlaced(action);
	}
	
	@Override
	public void doAction(GameAction action) {
		super.doAction(action);
		String actionStr = action.toXmlString();
		if (actionStr != null) {
			sendData(actionStr);
		}
	}
	
	public synchronized void sendData(final String data) {
		Thread networkThread = new Thread(new Runnable() {
			public void run() {
				try {
					mOut.write(data);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		networkThread.start();
	}
	
	public void setSocket(Socket socket) {
		mSocket = socket;
		try {
			mOut = new BufferedWriter(new PrintWriter(socket.getOutputStream(), true));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close() throws IOException {
		if (mOut != null) {
			mOut.flush();
			mOut.close();
		}
		if (mSocket != null) {
			mSocket.close();
		}
	}
	
	private Socket mSocket;
	private BufferedWriter mOut;

}
