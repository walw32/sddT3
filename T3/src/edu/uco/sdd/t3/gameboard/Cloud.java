package edu.uco.sdd.t3.gameboard;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import edu.uco.sdd.t3.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class Cloud extends Activity implements OnItemClickListener {
	private String gameHistory;
	private String cloudHttp;
	private String gameID;
	private String action;
	private String xml;
	private ArrayList<String> nameList = new ArrayList<String>();
	private ArrayList<String> moveList = new ArrayList<String>();
	private ListView lv;
	private Context myContext = this;
	private AlertDialog alert;
	HttpPost cloudHttppost;
	HttpResponse response;
	HttpClient httpclient = new DefaultHttpClient();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cloud_view);

		// get user object
		Bundle bundle = getIntent().getExtras();
		action = (String) bundle.getSerializable("action");
		Log.d("ACTION:", "" + action);
		if (action.equals("save")) {
			gameHistory = (String) bundle.getSerializable("history");
			try {
				gameID = URLEncoder.encode("androidtest", "utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d("Game history:", "" + gameHistory);
			cloudSave(gameID);

		} else
			cloudReplay();
	}

	public void cloudSave(String gameID) {
		Log.d("Game history:", "" + gameHistory);
		// this search uses coordinates (nearby search, no multiplier)

		cloudHttp = "http://herura.com/cloud.php?code=toongo&name=" + gameID
				+ "&moves=" + gameHistory + "&action=save";

		new CloudHelper().execute(cloudHttp);
	}

	public void cloudReplay() {
		cloudHttp = "http://herura.com/cloud.php?code=toongo&action=replay";
		new CloudHelper().execute(cloudHttp);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.activity_security_control, menu);
		return true;
	}

	// handles http request in a background thread (have to do it this way)
	class CloudHelper extends AsyncTask<String, Void, String> {
		private ArrayList<String> gameNames;
		private ArrayList<String> id;

		@Override
		protected String doInBackground(String... url) {
			if (action.equals("save")) {
				cloudHttppost = new HttpPost(cloudHttp);
				try {
					// store the game moves in database
					response = httpclient.execute(cloudHttppost);
					// retrieve the game moves from database
					// EntityUtils.toString(response.getEntity());
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				cloudHttppost = new HttpPost(cloudHttp);
				try {
					// store the game moves in database
					response = httpclient.execute(cloudHttppost);
					// retrieve the game moves from database
					xml = EntityUtils.toString(response.getEntity());
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return "Executed";

		}

		@Override
		protected void onPostExecute(String unused) {
			if (action.equals("save")) {
				setContentView(R.layout.main_menu);
			} else {
				try {
					DocumentBuilderFactory dbf = DocumentBuilderFactory
							.newInstance();
					DocumentBuilder db = dbf.newDocumentBuilder();
					InputSource is = new InputSource();
					is.setCharacterStream(new StringReader(xml));

					Document doc = db.parse(is);
					NodeList nodes = doc.getElementsByTagName("result");

					// iterate the results
					for (int i = 0; i < nodes.getLength(); i++) {
						Element element = (Element) nodes.item(i);

						NodeList name = element.getElementsByTagName("name");
						Element line = (Element) name.item(0);
						nameList.add(getCharacterDataFromElement(line));

						NodeList move = element.getElementsByTagName("move");
						line = (Element) move.item(0);
						moveList.add(getCharacterDataFromElement(line));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				// List items

				AlertDialog.Builder builder = new AlertDialog.Builder(myContext);
				builder.setTitle("Select a game to replay:");

				final ArrayAdapter<String> names = new ArrayAdapter<String>(
						myContext,
						android.R.layout.simple_list_item_single_choice,
						nameList);
				builder.setSingleChoiceItems(names, 0,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								Log.d("NAME:", "" + nameList.get(item));
								Intent intent = new Intent(Cloud.this,
										GameplayView.class);
								intent.putExtra("history", moveList.get(item));
								startActivity(intent);
								stopDialog();
								finish();
							}

						});
				alert = builder.create();
				alert.show();
			}
		}

	}

	// converts elements of xml/doc to strings
	public static String getCharacterDataFromElement(Element e) {
		Node child = ((Node) e).getFirstChild();
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return "?";

	}

	public boolean onCloudButtonClicked(View v) {

		int buttonId = v.getId();
		switch (buttonId) {

		case R.id.cloudButton:
			Intent intent = new Intent(Cloud.this, Cloud.class);
			intent.putExtra("action", "replay");
			startActivity(intent);
			finish();
			break;
		}

		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

	}

	public void stopDialog() {
		alert.dismiss();
	}
}
