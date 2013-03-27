package edu.uco.sdd.t3.gameboard;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;

public class Cloud extends Activity {
	private String xml;
	private HttpEntity resEntity;
	private HttpPost googleHttppost;
	private HttpPost uwangoHttppost;
	private HttpClient httpclient;
	private HttpResponse response;
	private DocumentBuilder db;
	private Document doc;
	private String url;
	private String area;
	private String reference;
	private String locale;
	private Float lat;
	private Float lng;
	private String latString;
	private String lngString;
	private String googleHttp;
	private String uwangoHttp;
	private String city;
	User user;
//this page gets the result from our database first (if any, then proceeds to intermediate2 to handle google responses)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_security_control);

		// get user object

		Bundle bundle = getIntent().getExtras();
		user = (User) bundle.getSerializable("user");
		this.locale = user.getLocale();
		httpclient = new DefaultHttpClient();

		this.locale = user.getLocale();
		// this search uses coordinates (nearby search, no multiplier)
		if (user.isUsingPhysicalLocation()) {
			try {
				city = URLEncoder.encode(user.getUwangoCity(),
						"utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			uwangoHttp = "http://uwango.com/xml.php?verification=tooblydoo&city="
					+ city + "&locale=" + locale;
			uwangoHttppost = new HttpPost(uwangoHttp);
		}
		// this search uses city (text search, 10x multiplier)
		else {
			try {
				city = URLEncoder.encode(user.getCity().substring(1), "utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// for uwango database search
			uwangoHttp = "http://uwango.com/xml.php?verification=tooblydoo&city="
					+ city + "&locale=" + locale;
			uwangoHttppost = new HttpPost(uwangoHttp);
		}

		new Places().execute();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_security_control, menu);
		return true;
	}

	// handles http request in a background thread (have to do it this way)
	class Places extends AsyncTask {

		@Override
		protected Object doInBackground(Object... arg0) {

			try {
				response = httpclient.execute(uwangoHttppost);
				// store the uwango xml in user class
				user.setUwangoXml(EntityUtils.toString(response.getEntity()));
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// redirects to intermediate2 for places result
			Intent intent = new Intent(Cloud.this, Intermediate2.class);

			intent.putExtra("user", user);
			startActivity(intent);
			finish();

			return null;
		}

	}

}
