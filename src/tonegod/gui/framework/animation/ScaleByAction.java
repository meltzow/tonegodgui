/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.framework.animation;

/**
 *
 * @author t0neg0d
 */
public class ScaleByAction extends TemporalAction {

	private float initX, initY;
	private float amountX, amountY;
	private float nextPercent = 0;
	private float lastPercent = 0;
	private boolean autoReverse = false;
	private int cycles = 0;
	
	@Override
	protected void begin() {
		lastPercent = 0;
		nextPercent = 0;
		if (autoReverse) {
			initX = quad.scaleX;
			initY = quad.scaleY;
			setDuration(getDuration()*.5f);
		}
	}
	
	@Override
	protected void update(float percent) {
		nextPercent = percent - lastPercent;
		lastPercent = percent;
		
		quad.scaleX += amountX * nextPercent;
		quad.scaleY += amountY * nextPercent;
	}
	
	@Override
	protected void end() {
		if (autoReverse && cycles == 0) {
			autoReverse = false;
			amountX = -amountX;
			amountY = -amountY;
			restart();
			cycles = 1;
		} else if (cycles == 1) {
			quad.scaleX = initX;
			quad.scaleY = initY;
		}
	}

	public void setAmount (float x, float y) {
		amountX = x;
		amountY = y;
	}

	public void setAmount (float scale) {
		amountX = scale;
		amountY = scale;
	}

	public float getAmountX () {
		return amountX;
	}

	public void setAmountX (float x) {
		this.amountX = x;
	}

	public float getAmountY () {
		return amountY;
	}

	public void setAmountY (float y) {
		this.amountY = y;
	}
	
	public void setAutoReverse(boolean autoReverse) {
		this.autoReverse = autoReverse;
	}
}
