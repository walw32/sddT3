package edu.uco.sdd.t3.gameboard;

import edu.uco.sdd.t3.R;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class Server extends Activity implements DataDisplay {
TextView serverMessage;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multiplayer_game_configuration);
        serverMessage=(TextView)findViewById(R.id.IPText);
    }

   public void connect(View view)
   {
	    MyServer server= new MyServer();
	   	 server.setEventListener(this);
	   	 server.startListening();

   }
   public void Display(String message)
   {
	   serverMessage.setText(""+message);
   }
}