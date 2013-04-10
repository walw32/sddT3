package edu.uco.sdd.t3.gameboard;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import edu.uco.sdd.t3.R;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class Client extends Activity {
TextView serverMessage;
Thread m_objThreadClient;
Socket clientSocket;
Bundle bundle = getIntent().getExtras();
String ipString = bundle.getString("IP");
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multiplayer_game_configuration);
        serverMessage=(TextView)findViewById(R.id.IPText);
    }
public void Start(View view)
{
	m_objThreadClient=new Thread(new Runnable() {
		  public void run()
	       {
	          try 
	           {
				 clientSocket= new Socket(ipString, 40000);
				 ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
			     oos.writeObject("Hello there");
			     Message serverMessage= Message.obtain();
			     ObjectInputStream ois =new ObjectInputStream(clientSocket.getInputStream());
			     String strMessage = (String)ois.readObject();
			    serverMessage.obj=strMessage;
                mHandler.sendMessage(serverMessage); 
			    oos.close();
			    ois.close();
			   } 
	           catch (Exception e) 
	           {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 
	         }
			});
	 
	 m_objThreadClient.start();

}
Handler mHandler = new Handler() {
	@Override
	public void handleMessage(Message msg) {
		messageDisplay(msg.obj.toString());
	}
};
public void messageDisplay(String servermessage)
{
	serverMessage.setText(""+servermessage);
}
   
}
