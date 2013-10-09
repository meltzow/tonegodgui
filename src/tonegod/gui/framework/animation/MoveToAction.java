/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.framework.animation;

/**
 *
 * @author t0neg0d
 */
public class MoveToAction extends TemporalAction {

	private float x, y;
	
	@Override
	protected void begin() {
		quad.setPosition(x,y);
		setDuration(0);
	}
	
	@Override
	protected void update(float percent) {  }
	
	@Override
	protected void end() {  }

	public void setPosition (float x, float y) {
		this.x = x;
		this.y = y;
		setDuration(1);
	}
}
