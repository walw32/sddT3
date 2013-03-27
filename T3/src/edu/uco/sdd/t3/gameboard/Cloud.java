package edu.uco.sdd.t3.gameboard;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;

import edu.uco.sdd.t3.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;

public class Cloud extends Activity {
	private String gameHistory;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cloud_view);

		//get user object
	Bundle bundle = getIntent().getExtras();
	gameHistory = (String) bundle.getSerializable("history");
        Log.d("Game history:",""+gameHistory);
		// this search uses coordinates (nearby search, no multiplier)
		
			//try {
			//	city = URLEncoder.encode(user.getMyCity(),
			//			"utf-8");
			//} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
		//	}
		//	myHttp = "http://mywebsite.com/xml.php?verification=&city="
			//		+ city + "&locale=" + locale;
			// myHttppost = new HttpPost(myHttp);
		

//		new Places().execute();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//getMenuInflater().inflate(R.menu.activity_security_control, menu);
		return true;
	}

	// handles http request in a background thread (have to do it this way)
	/*class Places extends AsyncTask {

		@Override
		protected Object doInBackground(Object... arg0) {

			try {
				response = httpclient.execute(myHttppost);
				// store the xml in user class
				EntityUtils.toString(response.getEntity());
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
			Intent intent = new Intent(Cloud.this, Intermediate2.class);

			intent.putExtra("user", user);
			startActivity(intent);
			finish();

			return null;
		}

	}*/

}
