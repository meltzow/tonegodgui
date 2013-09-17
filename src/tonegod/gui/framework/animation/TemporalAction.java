/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.framework.animation;

import tonegod.gui.framework.core.AnimElement;
import tonegod.gui.framework.core.QuadData;
import tonegod.gui.framework.core.Transformable;

/**
 * Based on LibGdx TemporalAction.  This has been altered to work with QuadData representing
 * a single quad from a mesh of x number of quads.
 * @author t0neg0d
 */
public abstract class TemporalAction {
	private float duration, time;
	private Interpolation interpolation;
	private boolean reverse, complete;
	protected AnimElement batch;
	protected Transformable quad;
	
	public TemporalAction () {
	}

	public TemporalAction (float duration) {
		this.duration = duration;
	}

	public TemporalAction (float duration, Interpolation interpolation) {
		this.duration = duration;
		this.interpolation = interpolation;
	}
	
	public void setTransformable(Transformable quad) { this.quad = quad; }
	public Transformable getTransformable() { return this.quad; }
	
	public boolean act (float delta) {
		if (complete) return true;
		if (time == 0) begin();
		time += delta;
		complete = time >= duration;
		float percent;
		if (complete)
			percent = 1;
		else {
			percent = time / duration;
			if (interpolation != null) percent = interpolation.apply(percent);
		}
		update(reverse ? 1 - percent : percent);
		if (complete) end();
		return complete;
	}

	protected void begin () {  }
	protected void end () {  }
	abstract protected void update (float percent);
	
	public void finish () { time = duration; }
	
	public void restart () {
		time = 0;
		complete = false;
	}
	
	public void reset () {
		reverse = false;
		interpolation = null;
	}
	
	/** Gets the transition time so far. */
	public float getTime () { return time; }

	/** Sets the transition time so far. */
	public void setTime (float time) {
		this.time = time;
	}

	public float getDuration () {
		return duration;
	}

	/** Sets the length of the transition in seconds. */
	public void setDuration (float duration) {
		this.duration = duration;
	}

	public Interpolation getInterpolation () {
		return interpolation;
	}

	public void setInterpolation (Interpolation interpolation) {
		this.interpolation = interpolation;
	}

	public boolean isReverse () {
		return reverse;
	}

	/** When true, the action's progress will go from 100% to 0%. */
	public void setReverse (boolean reverse) {
		this.reverse = reverse;
	}
}
