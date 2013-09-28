/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.framework.core;

import com.jme3.app.Application;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import java.util.ArrayList;
import java.util.List;
import tonegod.gui.controls.extras.emitter.ElementEmitter;
import tonegod.gui.controls.extras.emitter.ElementEmitter.EmitterAction;
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
	float time;
	
	public AnimManager(Screen screen) {
		this.screen = screen;
		time = this.screen.getApplication().getTimer().getTimeInSeconds();
	}
	
	public void addQueuedAction(TemporalAction action, Transformable item, float startTime) {
		ActionItem act = new ActionItem(action,item,time+startTime);
		queue.add(act);
	}
	
	public void addQueuedAction(EmitterAction action, ElementEmitter emitter, float startTime) {
		ActionItem act = new ActionItem(action,emitter,time+startTime);
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
				} else {
					switch (item.emitterAction) {
						case EmitAllParticles:
							item.emitter.emitAllParticles();
							break;
						case AttachEmitter:
							item.emitter.startEmitter();
							break;
						case DetachEmitter:
							item.emitter.stopEmitter();
					}
				}
				remove.add(item);
			}
		}
		if (!remove.isEmpty()) {
			queue.removeAll(remove);
			remove.clear();
		}
	}

	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) {
		
	}
	
	public class ActionItem {
		TemporalAction action;
		Transformable item;
		ElementEmitter emitter;
		EmitterAction emitterAction;
		float startTime;
		
		public ActionItem(EmitterAction action, ElementEmitter emitter, float startTime) {
			this(null, null, action, emitter, startTime);
		}
		
		public ActionItem(TemporalAction action, Transformable item, float startTime) {
			this(action, item, null, null, startTime);
		}
		
		private ActionItem(TemporalAction action, Transformable item, EmitterAction emitterAction, ElementEmitter emitter, float startTime) {
			this.action = action;
			this.item = item;
			this.emitterAction = emitterAction;
			this.emitter = emitter;
			this.startTime = startTime;
		}
	}
}
