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
public class SpriteInfluencer implements Influencer {
	private boolean isEnabled = true;
	private boolean autoUpdate = false;
	private boolean randomStartImage = true;
	
	@Override
	public void update(ElementParticle particle, float tpf) {
		
	}

	@Override
	public void initialize(ElementParticle particle) {
	//	if (!autoUpdate) particle.particle.setIsEnabled(false);
	//	if (randomStartImage) {
	//		int nextRow = FastMath.rand.nextInt(particle.particle.getSpriteRowCount());
	//		int nextCol = FastMath.rand.nextInt(particle.particle.getSpriteColCount());
	//		particle.particle.updateTextureAtlasImage("x=" + (nextRow*particle.particle.getSpriteWidth()) + "|y=" + (nextCol*particle.particle.getSpriteHeight()) + "|w=" + particle.particle.getSpriteWidth() + "|h=" + particle.particle.getSpriteHeight());
	//	}
	}

	@Override
	public void setIsEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	@Override
	public boolean getIsEnabled() {
		return this.isEnabled;
	}
	
}
