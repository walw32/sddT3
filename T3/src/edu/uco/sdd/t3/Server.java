package edu.uco.sdd.t3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
 
public class Server extends Activity {
 
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static InputStreamReader inputStreamReader;
    private static BufferedReader bufferedReader;
    private static String message;
    private TextView textField;
    private Handler mMainThreadHandler = new Handler();
 
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.multiplayer_game_configuration);
		
		textField = (TextView) findViewById(R.id.IPText);
 
        try {
            serverSocket = new ServerSocket(40000);  //Server socket
 
        } catch (IOException e) {
            System.out.println("Could not listen on port: 40000");
        }
 
        textField.setText("opened Socket, listening on " + serverSocket.getInetAddress());
 
        Thread networkThread = new Thread(new Runnable() {
        	public void run() {
        		try {
        			clientSocket = serverSocket.accept();   //accept the client connection, blocks until it receives one.
        			Log.d("ServerSocket", "I have just accepted a connection.");
        			Log.d("ServerSocket", "serverSocket.isClosed() = " + serverSocket.isClosed());
        			Log.d("ServerSocket", "clientSocket.isClosed() = " + serverSocket.isClosed());
        			inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
	                bufferedReader = new BufferedReader(inputStreamReader); //get the client message
        			while (!serverSocket.isClosed()) { 	     
        				if (bufferedReader.ready()) {
        	                message = bufferedReader.readLine();
        	                Log.d("ServerSocket", "message: " + message);
        	                mMainThreadHandler.post(new Runnable() {
        	                	public void run() {
        	                		textField.setText(message);
        	                	}
        	                });
        				}
        	                Thread.sleep(1);
        			}
        			Log.d("Serversocket", "Socket closed.");
        			bufferedReader.close();
        			inputStreamReader.close();      			
        		} catch (InterruptedException ex) {
        			
        		}
        		 catch (IOException ex) {
        			 ex.printStackTrace();
        			 mMainThreadHandler.post(new Runnable() {
        				 public void run() {
        					 textField.setText("Problem in message reading");
        				 }
        			 }); 	
	            }
        		finally {
        			try {
        				if (clientSocket != null) {
        					clientSocket.close();
        				}
        			} catch (IOException ex) {
        				ex.printStackTrace();
        			}
        		}
        	}
        });
        networkThread.start();
    }
    
    public void onStop() {
    	try {
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	super.onStop();
    }
}