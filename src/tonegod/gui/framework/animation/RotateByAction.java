/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.framework.animation;

/**
 *
 * @author t0neg0d
 */
public class RotateByAction extends TemporalAction {
	private float initRot;
	private float amount;
	private float nextPercent = 0;
	private float lastPercent = 0;
	private boolean autoReverse = false;
	private int cycles = 0;
	
	@Override
	protected void begin() {
		lastPercent = 0;
		nextPercent = 0;
		if (autoReverse) {
			initRot = quad.rotation;
			setDuration(getDuration()*.5f);
		}
	}
	
	@Override
	protected void update(float percent) {
		nextPercent = percent - lastPercent;
		lastPercent = percent;
		
		quad.rotation += amount*nextPercent;
	//	if (!isReverse())	quad.rotation += amount*nextPercent;
	//	else				quad.rotation -= amount*nextPercent;
	}
	
	@Override
	protected void end() {
		if (autoReverse && cycles == 0) {
			autoReverse = false;
			amount = -amount;
			restart();
			cycles = 1;
		} else if (cycles == 1) {
			quad.rotation = initRot;
		}
	}
	
	public void setAmount(float amount) {
		this.amount = amount;
	}
	
	public void setAutoReverse(boolean autoReverse) {
		this.autoReverse = autoReverse;
	}
}