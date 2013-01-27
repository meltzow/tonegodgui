/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.lists;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import java.util.ArrayList;
import java.util.List;
import tonegod.gui.controls.buttons.Button;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;
import tonegod.gui.effects.Effect;

/**
 *
 * @author t0neg0d
 */
public class Dial extends Button {
	Element elCenter, elPosition;
	protected List<String> stepValues = new ArrayList();
	
	int selectedIndex = 0;
	boolean isStepped = false;
	float stepSize = 1;
	float currentAngle = 0;
	float gap = 30;
	
	/**
	 * Creates a new instance of the Dial control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 */
	public Dial(Screen screen, String UID, Vector2f position) {
		this(screen, UID, position,
			screen.getStyle("Button").getVector2f("defaultSize"),
			screen.getStyle("Button").getVector4f("resizeBorders"),
			screen.getStyle("Button").getString("defaultImg")
		);
	}
	
	/**
	 * Creates a new instance of the Dial control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 */
	public Dial(Screen screen, String UID, Vector2f position, Vector2f dimensions) {
		this(screen, UID, position, dimensions,
			screen.getStyle("Button").getVector4f("resizeBorders"),
			screen.getStyle("Button").getString("defaultImg")
		);
	}
	
	/**
	 * Creates a new instance of the Dial control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 * @param resizeBorders A Vector4f containg the border information used when resizing the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg The default image to use for the Slider's track
	 */
	public Dial(Screen screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg);
		
		removeEffect(Effect.EffectEvent.Hover);
		removeEffect(Effect.EffectEvent.Press);
		removeEffect(Effect.EffectEvent.LoseFocus);
		
		elCenter = new Element(
			screen,
			UID + ":Center",
			new Vector2f(getWidth()/2, 0),
			new Vector2f(1, getHeight()/2),
			new Vector4f(0,0,0,0),
			null
		);
		elCenter.setScaleNS(false);
		elCenter.setScaleEW(false);
		elCenter.setDockS(true);
		elCenter.setDockW(true);
		addChild(elCenter);
		
		elPosition = new Element(
			screen,
			UID + ":Position",
			new Vector2f(-6, 0),
			new Vector2f(12, 12),
			new Vector4f(0,0,0,0),
			screen.getStyle("Common").getString("arrowUp")
		);
		elCenter.addChild(elPosition);
		elPosition.setIgnoreMouse(true);
		
		setInterval(100);
	}

	/**
	 * Adds a step value to the Slider.  When 2 or more step values are associated with
	 * a Dial, the rotation becomes stepped and advances to the next/previous slot
	 * position as the mouse is moved.  Each slot added has an associated value that is
	 * returned via the onChange event or getSelectedValue() method.
	 * 
	 * @param value The string value to add for the next step.
	 */
	public void addStepValue(String value) {
		stepValues.add(value);
		if (stepValues.size() >= 2) {
			isStepped = true;
			setStepSize();
		//	this.setInterval(1);
		}
	}
	
	/**
	 * Removes a step value by the value originally added.
	 * 
	 * @param value The string value of the step to be removed.
	 */
	public void removeStepValue(String value) {
		stepValues.remove(value);
		if (stepValues.size() < 2) {
			isStepped = false;
			setStepSize();
		//	this.setInterval(100);
		}
	}
	
	private void setStepSize() {
		stepSize = (360-(gap*2))/(stepValues.size()-1);
		System.out.println(stepSize);
	}
	
	private float getStepAngle(float angle) {
		angle += 180;
		
		int nIndex = (int)Math.floor(angle/stepSize);
		
		if (nIndex >= 0 && nIndex < stepValues.size() && nIndex != this.selectedIndex) {
			System.out.println(nIndex);
			setSelectedIndex(nIndex);
		}
		
		return gap+(nIndex*stepSize)-180;
	}
	
	/**
	 * Returns the text value of the current selected step.
	 * 
	 * @return String stepValue
	 */
	public int getSelectedIndex() {
		return selectedIndex;
	}
	
	/**
	 * Sets the Dial's selected index to the selected step index specified and
	 * rotates the Dial to appropriate angle to reflect this change.
	 * 
	 * @param selectedIndex The index to set the Dial's selectedIndex to.
	 */
	private void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
		if( isStepped)	onChange(selectedIndex, stepValues.get(selectedIndex));
		else			onChange(selectedIndex, String.valueOf(selectedIndex));
	}
	
	/**
	 * Event called when the Slider's selectedIndex changes.
	 * 
	 * @param selectedIndex The Slider's current selectedIndex
	 * @param value The string value associated with this index
	 */
	public void onChange(int selectedIndex, String value) {  }
	
	@Override
	public void onButtonStillPressedInterval() {
		Vector2f pos = new Vector2f(elCenter.getAbsoluteX(), elCenter.getAbsoluteY());
		currentAngle = (float)Math.atan2(screen.getMouseXY().x-elCenter.getAbsoluteX(), screen.getMouseXY().y-elCenter.getAbsoluteY())*FastMath.RAD_TO_DEG;
		if (currentAngle < -180+gap)
			currentAngle = -180+gap;
		else if (currentAngle > 180-gap)
			currentAngle = 180-gap;
		float angle = Float.valueOf(currentAngle);
		
		if (isStepped) {
			angle = getStepAngle(angle);
		}
		
		elCenter.setLocalRotation(elCenter.getLocalRotation().fromAngleAxis(-(angle*FastMath.DEG_TO_RAD), Vector3f.UNIT_Z));
	}
}
