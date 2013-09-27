/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.framework.core;

import com.jme3.math.Vector2f;
import tonegod.gui.framework.animation.TemporalAction;

/**
 *
 * @author t0neg0d
 */
public interface Transformable {
	public void setPositionX(float x);
	public void setPositionY(float y);
	public void setPosition(float x, float y);
	public void setPosition(Vector2f pos);
	public void setRotation(float rotation);
	public void setScaleX(float scaleX);
	public void setScaleY(float scaleY);
	
	public float getPositionX();
	public float getPositionY();
	public float getRotation();
	public float getScaleX();
	public float getScaleY();
	
	public void addAction(TemporalAction action);
}
