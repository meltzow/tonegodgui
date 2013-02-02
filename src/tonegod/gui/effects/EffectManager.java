/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.effects;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import tonegod.gui.core.Element;

/**
 *
 * @author t0neg0d
 */
public class EffectManager implements Control {
	Spatial spatial;
	private Map<String, Effect> currentEffects = new HashMap();
	private List<EffectQueue> currentEffectQueues = new ArrayList();
	
	public EffectManager() {  }
	
	public void applyEffect(Effect effect) {
		if (effect != null) {
			currentEffects.remove(effect.getElement().getUID());
			currentEffects.put(effect.getElement().getUID(), effect);
		}
	}
	
	public void removeEffect(Element element) {
		currentEffects.remove(element.getUID());
	}

	public void applyEffectQueue(EffectQueue queue) {
		queue.setEffectManager(this);
		currentEffectQueues.add(queue);
	}
	
	public void removeEffectQueue(EffectQueue queue) {
		currentEffectQueues.remove(queue);
	}
	
	@Override
	public void update(float tpf) {
		Set<String> keys = currentEffects.keySet();
		for (String key : keys) {
			currentEffects.get(key).update(tpf);
		}
		try {
			for (EffectQueue queue : currentEffectQueues) {
				queue.update(tpf);
			}
		} catch (Exception ex) {  }
	}
	
	@Override
	public Control cloneForSpatial(Spatial spatial) {
		return this;
	}

	@Override
	public void setSpatial(Spatial spatial) {
		this.spatial = spatial;
	}

	@Override
	public void render(RenderManager rm, ViewPort vp) {  }
	@Override
	public void write(JmeExporter ex) throws IOException {  }
	@Override
	public void read(JmeImporter im) throws IOException {  }
}
