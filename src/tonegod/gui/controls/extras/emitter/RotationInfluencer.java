/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.extras.emitter;

import com.jme3.math.FastMath;
import tonegod.gui.controls.extras.emitter.ElementEmitter.ElementParticle;

/**
 *
 * @author t0neg0d
 */
public class RotationInfluencer implements Influencer {
	private boolean isEnabled = true;
	private float maxRotationSpeed;
	
	@Override
	public void update(ElementParticle particle, float tpf) {
		if (isEnabled) {
			if (particle.rotateDir)
				particle.angle += particle.rotateSpeed * tpf;
			else
				particle.angle -= particle.rotateSpeed * tpf;
		}
	}

	@Override
	public void initialize(ElementParticle particle) {
		particle.angle = 0;
		particle.rotateSpeed = FastMath.rand.nextFloat()*maxRotationSpeed;
	}

	@Override
	public void setIsEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	@Override
	public boolean getIsEnabled() {
		return this.isEnabled;
	}
	
	public void setMaxRotationSpeed(float maxRotationSpeed) {
		this.maxRotationSpeed = maxRotationSpeed;
	}
	
	@Override
	public RotationInfluencer clone() {
		RotationInfluencer clone = new RotationInfluencer();
		return clone;
	}
}
