package edu.uco.sdd.t3.gameboard;

import edu.uco.sdd.t3.R;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Client extends Activity {

	private Socket client;
	private PrintWriter printwriter;
	private EditText textField;
	private Button button;
	private String message;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.multiplayer_game_configuration);

		String ipString = getIntent().getExtras().getString("IP");
		textField = (EditText) findViewById(R.id.IPText); // reference to the
															// text field

		try {
			message = "Did you get this message?";
			client = new Socket(ipString, 40000); // connect to server
			printwriter = new PrintWriter(client.getOutputStream(), true);
			printwriter.write(message); // write the message to output stream
			textField.setText("Sent message...");

			printwriter.flush();
			printwriter.close();
			client.close(); // closing the connection

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}