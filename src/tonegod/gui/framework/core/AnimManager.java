/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.framework.core;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tonegod.gui.core.Screen;
import tonegod.gui.framework.animation.TemporalAction;

/**
 *
 * @author t0neg0d
 */
public class AnimManager extends AbstractControl {
	Screen screen;
	List<ActionItem> queue = new ArrayList();
	List<ActionItem> remove = new ArrayList();
	List<ActionItem> active = new ArrayList();
	
	float time;
	
	public AnimManager(Screen screen) {
		this.screen = screen;
		time = this.screen.getApplication().getTimer().getTimeInSeconds();
	}
	
	public void addQueuedAction(TemporalAction action, Transformable item, float startTime) {
		ActionItem act = new ActionItem(action,item,time+startTime);
		queue.add(act);
	}
	
	@Override
	protected void controlUpdate(float tpf) {
		time += tpf;
		for (ActionItem item : queue) {
			if (time >= item.startTime) {
				if (item.item != null) {
					item.item.addAction(item.action);
					if (item.item instanceof QuadData)
						((QuadData)item.item).show();
				}
				active.add(item);
				remove.add(item);
			}
		}
		if (!remove.isEmpty()) {
			queue.removeAll(remove);
			remove.clear();
		}
		if (!active.isEmpty()) {
			for (ActionItem item : active) {
				if (!item.item.getContainsAction(item.action)) {
					remove.add(item);
				}
			}
		}
		if (!remove.isEmpty()) {
			active.removeAll(remove);
			remove.clear();
		}
	}

	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) {  }
	
	public int getQueueCount() {
		return this.queue.size();
	}
	
	public void getIsQueueIdle() {
		
	}
	
	public class ActionItem {
		TemporalAction action;
		Transformable item;
		float startTime;
		
		private ActionItem(TemporalAction action, Transformable item, float startTime) {
			this.action = action;
			this.item = item;
			this.startTime = startTime;
		}
	}
}
