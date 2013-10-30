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
public class RotationInfluencer extends InfluencerBase {
	private boolean isEnabled = true;
	private float maxRotationSpeed;
	private boolean useRandomStartAngle = true;
	private boolean useFixedRotationSpeed = false;
	
	public RotationInfluencer(ElementEmitter emitter) {
		super(emitter);
	}
	
	@Override
	public void update(ElementParticle particle, float tpf) {
		if (isEnabled) {
			if (particle.rotateDir)
				particle.angle += particle.rotateSpeed * tpf * FastMath.RAD_TO_DEG;
			else
				particle.angle -= particle.rotateSpeed * tpf * FastMath.RAD_TO_DEG;
		}
	}

	@Override
	public void initialize(ElementParticle particle) {
		if (useRandomStartAngle)
			particle.angle = FastMath.rand.nextFloat()*360;
		else
			particle.angle = 0;
		if (!useFixedRotationSpeed)
			particle.rotateSpeed = FastMath.rand.nextFloat()*maxRotationSpeed;
		else
			particle.rotateSpeed = maxRotationSpeed;
	}

	@Override
	public void setIsEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	@Override
	public boolean getIsEnabled() {
		return this.isEnabled;
	}
	
	public void setUseFixedRotationSpeed(boolean useFixedRotationSpeed) {
		this.useFixedRotationSpeed = useFixedRotationSpeed;
	}
	
	public boolean getUseFixedRotationSpped() {
		return this.useFixedRotationSpeed;
	}
	
	public void setMaxRotationSpeed(float maxRotationSpeed) {
		this.maxRotationSpeed = maxRotationSpeed;
	}
	
	public float getMaxRotationSpeed() { return maxRotationSpeed; }
	
	public void setUseRandomStartAngle(boolean useRandomStartAngle) {
		this.useRandomStartAngle = useRandomStartAngle;
	}
	
	public boolean getUseRandomStartAngle() { return useRandomStartAngle; }
	
	@Override
	public RotationInfluencer clone() {
		RotationInfluencer clone = new RotationInfluencer(emitter);
		clone.setMaxRotationSpeed(maxRotationSpeed);
		return clone;
	}
}
