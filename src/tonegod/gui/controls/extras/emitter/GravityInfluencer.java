/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.extras.emitter;

import com.jme3.math.Vector2f;
import tonegod.gui.controls.extras.emitter.ElementEmitter.ElementParticle;

/**
 *
 * @author t0neg0d
 */
public class GravityInfluencer implements Influencer {
	
	private boolean isEnabled = true;
	private Vector2f gravity = new Vector2f(0,1);
	
	@Override
	public void update(ElementParticle p, float tpf) {
		if (isEnabled) {
			// applying gravity
			p.velocity.x -= gravity.x * tpf;
			p.velocity.y -= gravity.y * tpf;
			p.position.addLocal(new Vector2f(p.velocity.x,p.velocity.y));
		}
	}

	@Override
	public void initialize(ElementParticle particle) {
		
	}

	@Override
	public void setIsEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	@Override
	public boolean getIsEnabled() {
		return this.isEnabled;
	}
	
	public void setGravity(Vector2f gravity) {
		this.gravity.set(gravity);
	}
	
	public Vector2f getGravity() {
		return this.gravity;
	}
	
	@Override
	public GravityInfluencer clone() {
		GravityInfluencer clone = new GravityInfluencer();
		clone.setGravity(gravity);
		return clone;
	}
}
