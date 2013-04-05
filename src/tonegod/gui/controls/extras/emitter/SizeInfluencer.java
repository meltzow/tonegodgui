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
public class SizeInfluencer implements Influencer {
	private boolean isEnabled = true;
	private float startSize = 1f;
	private float endSize = 0.01f;
	
	@Override
	public void update(ElementParticle particle, float tpf) {
		if (isEnabled) {
			particle.size = FastMath.interpolateLinear(particle.blend, startSize, endSize);
		}
	}

	@Override
	public void initialize(ElementParticle particle) {
		particle.size = startSize;
	}

	@Override
	public void setIsEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	@Override
	public boolean getIsEnabled() {
		return this.isEnabled;
	}
	
	public void setStartSize(float startSize) {
		this.startSize = startSize;
	}
	
	public void setEndSize(float endSize) {
		this.endSize = endSize;
	}
	
	@Override
	public SizeInfluencer clone() {
		SizeInfluencer clone = new SizeInfluencer();
		clone.setStartSize(startSize);
		clone.setEndSize(endSize);
		return clone;
	}
}
