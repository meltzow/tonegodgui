/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.framework.core.util;

import tonegod.gui.framework.animation.Interpolation;

/**
 *
 * @author t0neg0d
 */
public class GameTimer {
	private float time = 0;
	private float targetTime = 1;
	private boolean active = false;
	private boolean complete = false;
	private long runCount = 0;
	private Interpolation interpolation = Interpolation.linear;
	
	/**
	 * Creates a new instance of the GameTimer class with a default duration of 1 second
	 */
	public GameTimer() {
		this(1,false);
	}
	
	/**
	 * Creates a new instance of the GameTimer class
	 * @param targetTime The duration the timer should run in seconds
	 */
	public GameTimer(float targetTime) {
		this(targetTime,false);
	}
	
	/**
	 * Creates a new instance of the GameTimer class
	 * @param targetTime The duration the timer should run in seconds
	 * @param autoStart Sets the GameTimer to active if true
	 */
	public GameTimer(float targetTime, boolean autoStart) {
		this.targetTime = targetTime;
		this.active = autoStart;
	}
	
	/**
	 * Sets the amount of time in seconds that should elapse before the
	 * GameTimer is complete.
	 * @param targetTime The duration of the timer
	 */
	public void setTargetTime(float targetTime) {
		this.targetTime = targetTime;
	}
	
	/**
	 * Return the amount of time in seconds the GameTimer should run for
	 * before completing.
	 * @return targetTime
	 */
	public float getTargetTime() {
		return this.targetTime;
	}
	
	/**
	 * Resets the timer for another use.
	 * @param resetFromLastEndTime GameTimer should subtract the targetTime from the
	 * previously elapsed time of the timer.
	 */
	public void reset(boolean resetFromLastEndTime) {
		if (resetFromLastEndTime)
			time -= targetTime;
		else
			time = 0;
		active = false;
		complete = false;
	}
	
	/**
	 * Sets the GameTimer to active
	 */
	public void startGameTimer() {
		this.active = true;
		runCount++;
	}
	
	/**
	 * Returns if the GameTimer has completed running
	 * @return 
	 */
	public boolean isComplete() {
		return this.complete;
	}
	
	/**
	 * Sets the interpolation to apply to the percent complete returned by getPercentComplete()
	 * @param interpolation 
	 */
	public void setInterpolation(Interpolation interpolation) {
		this.interpolation = interpolation;
	}
	
	/**
	 * Returns the interpolation set to be used by getPercentComplete()
	 * @return 
	 */
	public Interpolation getInterpolation() {
		return this.interpolation;
	}
	
	/**
	 * return a float between 0.0 and 1.0 representing the percent complete
	 * @return 
	 */
	public float getPercentComplete() {
		return interpolation.apply(time/targetTime);
	}
	
	/**
	 * Returns if the GameTimer is running
	 * @return 
	 */
	public boolean isActive() {
		return this.active;
	}
	
	/**
	 * Returns the number of times startGameTimer has been called on this timer
	 * @return 
	 */
	public long getRunCount() {
		return this.runCount;
	}
	
	/**
	 * Should be called each game loop
	 * @param tpf 
	 */
	public void update(float tpf) {
		if (active && !complete) {
			time += tpf;
			if (time >= targetTime) {
				complete = true;
				active = false;
			}
		}
	}
}
