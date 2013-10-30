/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.framework.animation;

import tonegod.gui.framework.core.QuadData;

/**
 *
 * @author t0neg0d
 */
public class MoveTexToAction extends TemporalAction {

	private float x, y;
	
	@Override
	protected void begin() {
		((QuadData)quad).tcOffsetX = x;
		((QuadData)quad).tcOffsetY = y;
		setDuration(0);
	}
	
	@Override
	protected void update(float percent) {  }
	
	@Override
	protected void end() {  }

	@Override
	public void restart() {
		setTime(0);
		setComplete(false);
		setDuration(1);
		reset();
	}
	
	public void setPosition (float x, float y) {
		this.x = x;
		this.y = y;
		setDuration(1);
	}
}
