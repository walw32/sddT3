package edu.uco.sdd.t3;

import edu.uco.sdd.t3.R;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
 
public class Server extends Activity {
 
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static InputStreamReader inputStreamReader;
    private static BufferedReader bufferedReader;
    private static String message;
    private EditText textField;
 
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.multiplayer_game_configuration);
		
		textField = (EditText) findViewById(R.id.IPText);
 
        try {
            serverSocket = new ServerSocket(40000);  //Server socket
 
        } catch (IOException e) {
            System.out.println("Could not listen on port: 40000");
        }
 
        textField.setText("opened Socket");
 
        while (true) {
            try {
 
                clientSocket = serverSocket.accept();   //accept the client connection
                inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
                bufferedReader = new BufferedReader(inputStreamReader); //get the client message
                message = bufferedReader.readLine();
                textField.setText(message);
 
                inputStreamReader.close();
                clientSocket.close();
 
            } catch (IOException ex) {
            	textField.setText("Problem in message reading");
            }
        }
 
    }
}