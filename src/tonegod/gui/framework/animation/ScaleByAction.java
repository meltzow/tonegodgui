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

	private float initX = -1, initY = -1;
	private float amountX, amountY;
	private float initDuration = -1;
	private float nextPercent = 0;
	private float lastPercent = 0;
	private boolean autoReverse = false;
	private boolean initAutoReverse = false;
	private int cycles = 0;
	
	@Override
	protected void begin() {
		lastPercent = 0;
		nextPercent = 0;
		if (autoReverse) {
			if (initX == -1) initX = quad.getScaleX();
			if (initY == -1) initY = quad.getScaleY();
			if (initDuration == -1)	{
				initDuration = getDuration();
				setDuration(initDuration*.5f);
			}
		}
	}
	
	@Override
	protected void update(float percent) {
		nextPercent = percent - lastPercent;
		lastPercent = percent;
		
		quad.setScaleX(quad.getScaleX()+(amountX * nextPercent));
		quad.setScaleY(quad.getScaleY()+(amountY * nextPercent));
	}
	
	@Override
	protected void end() {
		if (autoReverse && cycles == 0) {
			restart();
			autoReverse = false;
			amountX = -amountX;
			amountY = -amountY;
			cycles = 1;
		} else if (cycles == 1) {
			quad.setScaleX(initX);
			quad.setScaleY(initY);
			amountX = -amountX;
			amountY = -amountY;
			autoReverse = initAutoReverse;
			cycles = 0;
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
		initAutoReverse = autoReverse;
	}
}
