package edu.uco.sdd.t3;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

public class Client extends Activity {

	private Socket client;
	private PrintWriter printwriter;
	private TextView textField;
	private Button button;
	private String message;
	private InetAddress serverIp;
	private Handler mMainThreadHandler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.multiplayer_game_configuration);

		String ipString = getIntent().getExtras().getString("IP");
		try {
			serverIp = InetAddress.getByName(ipString);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		textField = (TextView) findViewById(R.id.IPText); // reference to the
															// text field

		Thread networkThread = new Thread(new Runnable() {
			public void run() {
				try {
					message = "Did you get this message?";
					client = new Socket(serverIp, 40000); // connect to server
					Log.d("ClientSocket", "Connection established.");
					printwriter = new PrintWriter(client.getOutputStream(), true);
					BufferedWriter bufferedWriter = new BufferedWriter(printwriter);
					Log.d("ClientSocket", "Writing message: \"" + message + "\"");
					bufferedWriter.write(message, 0, message.length()); // write the message to output stream
					Log.d("ClientSocket", "Message written. Updating textField.");
					mMainThreadHandler.post(new Runnable() {
						public void run() {
							textField.setText("Sent message...");
						}
					});
					bufferedWriter.flush();
					bufferedWriter.close();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}
		});
		networkThread.start();
		
	}
	
	@Override
	public void onStop() {
		// Close the Client Socket connection here.
		if (client != null) {
			try {
				client.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		super.onStop();
	}
}