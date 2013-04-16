package edu.uco.sdd.t3.network;

import java.io.IOException;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

/*
 * The following methods were inspired from an Android tutorial on parsing XML.
 * Many of my functions are similar.
 * 
 * Link: http://developer.android.com/training/basics/network-ops/xml.html
 */

public class NetworkParser {
	// Computer Science Announcement Feed XML Parser

	private static final String TAG = "NetworkParser";
	private static final String namespace = null; 	// We don't care about namespaces.

	public NetworkParser() {

	}

	public GameMetadata parseGameData(String gameMetadataXML)
			throws XmlPullParserException, IOException {
		Log.v(TAG, "in parseGameData()");
		StringReader feed = new StringReader(gameMetadataXML);
		try {
			XmlPullParser xmlparser = Xml.newPullParser();
			xmlparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,
					false);
			xmlparser.setInput(feed);
			xmlparser.nextTag();
			return readGame(xmlparser);
		} finally {
			feed.close();
			Log.v(TAG, "finished parseGameData()");
		}
	}
	
	public MoveData parseMoveData(String moveDataXML)
			throws XmlPullParserException, IOException {
		Log.v(TAG, "in parseMoveData()");
		StringReader feed = new StringReader(moveDataXML);
		try {
			XmlPullParser xmlparser = Xml.newPullParser();
			xmlparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,
					false);
			xmlparser.setInput(feed);
			xmlparser.nextTag();
			return readMove(xmlparser);
		} finally {
			feed.close();
			Log.v(TAG, "finished parseMoveData()");
		}
	}
	
	private GameMetadata readGameMetadata(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		Log.v(TAG, "in readGameMetadata()");
		GameMetadata data = null;
		//parser.require(XmlPullParser.START_TAG, namespace, "rss");

		// While we haven't reached the end of the document.
		while (parser.next() != XmlPullParser.END_TAG) {
			// Look for the starting points of the tags only
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			// Start by looking for a game tag.
			String tagName = parser.getName();
			Log.v(TAG, "tagName = " + tagName);
			if (tagName.equals("Game")) {
				Log.v(TAG, "tagName == \"game\"");
				data = readGame(parser);
			} 
			else {
				skip(parser);
			}
		}
		Log.v(TAG, "finished readGameMetadata()");
		return data;
	}
	
	private MoveData readMoveData(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		
		MoveData data = null;
		//parser.require(XmlPullParser.START_TAG, namespace, "rss");

		// While we haven't reached the end of the document.
		while (parser.next() != XmlPullParser.END_TAG) {
			// Look for the starting points of the tags only
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			// Start by looking for a game tag.
			String tagName = parser.getName();
			Log.v(TAG, "tagName = " + tagName);
			if (tagName.equals("Move")) {
				data = readMove(parser);
			} 
			else {
				skip(parser);
			}
		}
		return data;
	}
	
	private GameMetadata readGame(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		
		GameMetadata data = new GameMetadata();
		
		// Require <game> tag
		parser.require(XmlPullParser.START_TAG, namespace, "Game");
		String gameType = "null";
		String boardSize = "null";
		String timeout = "null";

		// While we haven't hit </game>...
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			// Read through the game block, looking for the game type, board size, and
			// timeout length tags.
			String tagName = parser.getName();
			Log.v(TAG, "tagName = " + tagName);
			if (tagName.equals("Type")) {
				gameType = readGameType(parser);
			} else if (tagName.equals("BoardSize")) {
				boardSize = readBoardSize(parser);
			} else if (tagName.equals("Timeout")) {
				timeout = readTimeout(parser);
			} else {
				// tagName does not match any of the tags we're looking for.
				// Skip it.
				skip(parser);
			}
		}
		data.setBoardSize(Integer.parseInt(boardSize));
		data.setGameType(Integer.parseInt(gameType));
		data.setTimeoutLength(Integer.parseInt(timeout));
		return data;
	}
	
	private MoveData readMove(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		
		MoveData data = new MoveData();
		
		// Require <move> tag
		parser.require(XmlPullParser.START_TAG, namespace, "Move");
		String xPos = "null";
		String yPos = "null";

		// While we haven't hit </move>...
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			// Read through the game block, looking for the x and y position data
			String tagName = parser.getName();
			if (tagName.equals("x")) {
				xPos = readXPos(parser);
			} else if (tagName.equals("y")) {
				yPos = readYPos(parser);
			} else {
				// tagName does not match any of the tags we're looking for.
				// Skip it.
				skip(parser);
			}
		}
		data.setPosX(Integer.parseInt(xPos));
		data.setPosY(Integer.parseInt(yPos));
		return data;
	}

	// Read everything between two GameType tags, i.e. <Type>This stuff</Type>
	private String readGameType(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		
		parser.require(XmlPullParser.START_TAG, namespace, "Type"); // <Type>
		String type = readText(parser);
		parser.require(XmlPullParser.END_TAG, namespace, "Type"); // </Type>
		return type;
	}

	// Read everything between two BoardSize tags.
	private String readBoardSize(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		
		parser.require(XmlPullParser.START_TAG, namespace, "BoardSize"); // <BoardSize>
		String boardSize = readText(parser);
		parser.require(XmlPullParser.END_TAG, namespace, "BoardSize"); // </BoardSize>
		return boardSize;
	}

	// Read everything between two Timeout tags.
	private String readTimeout(XmlPullParser parser) throws XmlPullParserException, IOException {
		
		parser.require(XmlPullParser.START_TAG, namespace, "Timeout"); // <Timeout>
		String timeout = readText(parser);
		parser.require(XmlPullParser.END_TAG, namespace, "Timeout"); // </Timeout>
		return timeout;
	}
	
	private String readXPos(XmlPullParser parser) throws XmlPullParserException, IOException {
		
		parser.require(XmlPullParser.START_TAG, namespace, "x"); // <x>
		String xPos = readText(parser);
		parser.require(XmlPullParser.END_TAG, namespace, "x"); // </x>
		return xPos;
	}
	
	private String readYPos(XmlPullParser parser) throws XmlPullParserException, IOException {
		
		parser.require(XmlPullParser.START_TAG, namespace, "y"); // <y>
		String yPos = readText(parser);
		parser.require(XmlPullParser.END_TAG, namespace, "y"); // </y>
		return yPos;
	}
	
	// Read the actual text between tags and return it as a string.
	private String readText(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		String result = "";
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}

	// Skips the stuff we don't care about.
	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
	    if (parser.getEventType() != XmlPullParser.START_TAG) {
	        throw new IllegalStateException();
	    }
	    int depth = 1;
	    while (depth != 0) {
	        switch (parser.next()) {
	        case XmlPullParser.END_TAG:
	            depth--;
	            break;
	        case XmlPullParser.START_TAG:
	            depth++;
	            break;
	        }
	    }
	 }
}
