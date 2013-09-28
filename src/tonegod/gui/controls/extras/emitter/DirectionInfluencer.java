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
public class DirectionInfluencer implements Influencer {
	
	private boolean isEnabled = true;
	private Vector2f direction = Vector2f.ZERO.clone();
	private Vector2f temp = new Vector2f();
	private float strength = 1;
	
	@Override
	public void update(ElementParticle p, float tpf) {
		
	}

	@Override
	public void initialize(ElementParticle particle) {
		if (isEnabled) {
			if (direction != Vector2f.ZERO) {
				temp.set(direction.normalize()).multLocal(particle.randforce*strength);
				particle.velocity.interpolate(particle.velocity, temp, 0.5f);
			}
		}
	}

	@Override
	public void setIsEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	@Override
	public boolean getIsEnabled() {
		return this.isEnabled;
	}
	
	public void setDirection(Vector2f direction) {
		this.direction.set(direction);
	}
	
	public Vector2f getGravity() {
		return this.direction;
	}
	
	public void setStrength(float strength) {
		this.strength = strength;
	}
	
	public float getStrength() {
		return this.strength;
	}
	
	@Override
	public DirectionInfluencer clone() {
		DirectionInfluencer clone = new DirectionInfluencer();
		clone.setDirection(direction);
		return clone;
	}
}
