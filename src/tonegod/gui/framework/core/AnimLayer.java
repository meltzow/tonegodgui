/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.framework.core;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import java.util.LinkedHashMap;
import java.util.Map;
import tonegod.gui.core.Element;
import tonegod.gui.core.ElementManager;
import tonegod.gui.core.utils.UIDUtil;

/**
 *
 * @author t0neg0d
 */
public class AnimLayer extends Element {
	private Map<String, AnimElement> animElements = new LinkedHashMap<String, AnimElement>();
	private float childZOrder = -1;
	private float zOrderStepMid = 0.001f;
	
	public AnimLayer(ElementManager screen) {
		this(screen, UIDUtil.getUID());
	}
	
	public AnimLayer(ElementManager screen, String UID) {
		super(
			screen,
			UID,
			Vector2f.ZERO,
			new Vector2f(screen.getWidth(),screen.getHeight()),
			Vector4f.ZERO,
			null
		);
		setAsContainerOnly();
		setIgnoreMouse(true);
		setEffectZOrder(false);
	}
	
	public void addAnimElement(String UID, AnimElement el) {
		if (childZOrder == -1)
			childZOrder = getLocalTranslation().z;
		el.setElementKey(UID);
		animElements.put(UID, el);
		el.setPositionZ(childZOrder);
		childZOrder -= zOrderStepMid;
		attachChild(el);
	}
	
	public AnimElement removeAnimElement(String UID) {
		return animElements.remove(UID);
	}
	
	public void removeAnimElement(AnimElement el) {
		animElements.remove(el.getElementKey());
	}
	
	public void bringAnimElementToFront(AnimElement el) {
		
	}
}
