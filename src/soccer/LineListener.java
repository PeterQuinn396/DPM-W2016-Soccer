/*
 * Title: LineListener
 * Date created: March 24, 2016
 * Author: Peter Quinn
 * 
 * Description: Takes a sample provider of a colour and sensor and 
 * uses the data from it to detect gridlines. Implements a derivative filter
 * to detect lines.
 * 
 * Edit History:
 * 
 */

package soccer;

import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.robotics.SampleProvider;

/**
 * 
 * @author Peter Quinn
 *
 */
public class LineListener extends Thread {

	private static final double BLACK_LINE_DIFF = .07;

	private final int SLEEP_TIME = 10; // ms
	private final double THRESHOLD = .08;

	private SampleProvider colourSensor;
	private float[] colourData;
	private boolean lineDetected;
	private float[] pointData;
	private boolean on = true;
	private Object lock;

	// pass a colour sensor sampleProvider to the constructor
	/**
	 * 
	 * @param colourSensor
	 */
	public LineListener(SampleProvider colourSensor) {

		this.colourSensor = colourSensor;
		colourData = new float[colourSensor.sampleSize()];
		lineDetected = false;
		pointData = new float[3];
		lock = new Object();
	}

	@Override
	// invoke with .start()

	// continually check if there is a line. If there is, set the boolean to
	// true.

	public void run() {
		long correctionStart, correctionEnd;

		colourSensor.fetchSample(colourData, 0);

		while (on) {
			correctionStart = System.currentTimeMillis();

			if (detectLine()) {
				synchronized (lock) {
					lineDetected = true;
				}
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			// this ensure that the line is looked for only once every period
			correctionEnd = System.currentTimeMillis();
			if (correctionEnd - correctionStart < SLEEP_TIME) {
				try {
					Thread.sleep(SLEEP_TIME - (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// don't expect interruption
				}
			}
		}
	}

	// reports if a line has been detected
	/**
	 * 
	 * @return
	 *         <p>
	 *         returns true if a line is detected
	 */
	public boolean lineDetected() {
		synchronized (lock) {
			return lineDetected;
		}
	}

	/**
	 * <p>
	 * end line detection
	 */
	public void end() {
		on = false;

	}

	// reset the line detected boolean, must always be done after a line is
	// detected
	/**
	 * <p>
	 * reset lineDetected to false
	 */
	public void reset() {
		lineDetected = false;
	}

	// moving window/buffer of three points
	/**
	 * <p>
	 * update moving window of three points
	 */
	private void updatePoints() {
		pointData[0] = pointData[1];
		pointData[1] = pointData[2];
		colourSensor.fetchSample(colourData, 0);
		pointData[2] = colourData[0];
	}

	// use a simple derivative filter to check if our values are dropping
	// rapidly ie detecting a line
	/**
	 * 
	 * @return
	 *         <p>
	 *         check for dropping values during line detection
	 */
	private boolean detectLine() {

		updatePoints();

		// // check current point against last point, if diff is significant,
		// its a
		// // line
		double last = pointData[1];
		double now = pointData[2];

		if (last - now > BLACK_LINE_DIFF) {

			Sound.setVolume(85);
			Sound.beep();
			Sound.setVolume(0);
			return true;
		} else {
			return false;
		}
	}

}
