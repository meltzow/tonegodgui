/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.framework.core;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import java.util.ArrayList;
import java.util.List;
import tonegod.gui.framework.animation.TemporalAction;

/**
 *
 * @author t0neg0d
 */
public class QuadData {
	public List<TemporalAction> actions = new ArrayList();
	public QuadData parent;
	public String key;
	public TextureRegion region;
	public int index;
	public float x = 0f;
	public float y = 0f;
	public float z = 1;
	public float width = 0f;
	public float height = 0f;
	public float initWidth = 0f;
	public float initHeight = 0f;
	public float scaleX = 1f;
	public float scaleY = 1f;
	public float rotation = 0f;
	public ColorRGBA color = new ColorRGBA(1,1,1,1);
	public Vector2f origin = new Vector2f(0,0);
	private boolean visible = true;
	
	public QuadData(String quadKey, TextureRegion region, float x, float y, float width, float height, Vector2f origin) {
		this.key = quadKey;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.origin = origin;
		this.region = region;
	}
	
	public void addAction(TemporalAction action) {
		action.setQuad(this);
		actions.add(action);
	}
	
	public void update(float tpf) {
		for (TemporalAction a : actions) {
			a.act(tpf);
		}
		
		for (TemporalAction a : actions) {
			if (a.getTime() >= a.getDuration()) {
				actions.remove(a);
				break;
			}
		}
	}
	
	public void hide() {
		if (visible) {
			initWidth = width;
			initHeight = height;
			width = 0;
			height = 0;
			visible = false;
		}
	}
	
	public void show() {
		if (!visible) {
			width = initWidth;
			height = initHeight;
			visible = true;
		}
	}
}
