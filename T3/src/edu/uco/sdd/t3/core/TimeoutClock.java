package edu.uco.sdd.t3.core;

import edu.uco.sdd.t3.R;
import android.os.Handler;
import android.util.Log;

/**
 * Used to keep track of time for the game.
 * 
 * @author Joshua Ford
 * @version 1.0
 */
public class TimeoutClock {

	/**
	 * Creates a TimeoutClock with a specified countdown length.
	 * 
	 * @param timeInMillis The countdown time.
	 */
	public TimeoutClock(Handler handler, long timeInMillis) {
		mMainHandler = handler;
		mTimerLength = timeInMillis;
		mCurrentTime = mTimerLength;
		mClock = new Thread(new ClockThread(timeInMillis));
	}
	
	/**
	 * Starts the TimeoutClock.
	 */
	public void start() {
		mClock.start();
		Log.v("TimeoutClock", "Clock started!");
	}
	
	/**
	 * Stops the TimeoutClock.
	 */
	public void stop() {
		mClock.interrupt();
		Log.v("TimeoutClock", "Clock stopped!");
	}
	
	/**
	 * Sets the TimeoutClock to a new time.
	 * 
	 * @param timeInMillis The countdown time.
	 */
	public void set(long timeInMillis) {
		mTimerLength = timeInMillis;
		mClock = new Thread(new ClockThread(timeInMillis));
		Log.v("TimeoutClock", "Clock set to " + timeInMillis + "!");
	}
	
	/**
	 * Resets the TimeoutClock to the countdown time.
	 */
	public void reset() {
		mClock.interrupt();
		mClock = new Thread(new ClockThread(mTimerLength));
		Log.v("TimeoutClock", "Clock reset!");
	}
	
	public long getCurrentTime() {
		return mCurrentTime;
	}
	
	/**
	 * Attaches a game to the TimeoutClock, who will then
	 * notify all of the game's observers when the time is up.
	 * 
	 * @param game The game to attach to the TimeoutClock.
	 */
	public void attachGame(Game game) {
		mGame = game;
		mGame.setTimer(this);
	}
	
	private class ClockThread implements Runnable {

		public ClockThread(long timeInMillis) {
			mLength = timeInMillis;
		}
		
		@Override
		public void run() {
			try {
				Log.v("ClockThread", "ClockThread started.");
				mStartTime = System.currentTimeMillis();
				mStopTime = mStartTime + mLength;
				long currentTime = mStartTime;
				long currentLength = mStopTime - currentTime;
				Log.v("ClockThread", "currentLength = " + currentLength);
				while (currentLength > 0) {
					Thread.sleep(100);	
					currentTime = System.currentTimeMillis();
					currentLength = mStopTime - currentTime;
					mCurrentTime = currentLength;
				}
				if (mGame != null) {
					mMainHandler.post(new Runnable() {
						public void run() {
							mGame.timeExhausted();
						}
					});
				}
			} catch (InterruptedException e) {
				
			}		
		}
		private long mStartTime;
		private long mStopTime;
		private long mLength;		
	}
	
	private Thread mClock;
	private long mTimerLength;
	private long mCurrentTime;
	private Game mGame;
	private Handler mMainHandler;
	

}
