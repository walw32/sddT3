package edu.uco.sdd.t3.gameboard;

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
	public TimeoutClock(long timeInMillis) {
		mTimerLength = timeInMillis;
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
		mClock = new Thread(new ClockThread(mTimerLength));
		Log.v("TimeoutClock", "Clock reset!");
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
				mStartTime = System.currentTimeMillis();
				mStopTime = mStartTime + mLength;
				long currentTime = mStartTime;
				long currentLength = mStopTime - currentTime;
				while (currentLength < mLength) {
					Thread.sleep(100);	
					currentTime = System.currentTimeMillis();
					currentLength = mStopTime - currentTime;
				}
				if (mGame != null) {
					mGame.timeExhausted();
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
	private Game mGame;
	

}
