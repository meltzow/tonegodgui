/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.lists;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
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
public abstract class Slider extends Button {
	public static enum Orientation {
		VERTICAL,
		HORIZONTAL
	}
	
	protected List<String> stepValues = new ArrayList();
	Element elThumbLock;
	Button elThumb;
	
	protected Orientation orientation;
	int selectedIndex = 0;
	boolean isStepped = false;
	float stepSize = 1;
	private boolean trackSurroundsThumb;
	private Vector2f thumbLockSize = new Vector2f(),
					thumbLockPosition = new Vector2f(),
					thumbSize = new Vector2f(),
					thumbPosition = new Vector2f();
	private float controlSize, controlLength;
	private Vector2f evalDimensions = new Vector2f();
	private MouseButtonEvent trackEvt;
	
	/**
	 * Creates a new instance of the Slider control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param orientation Slider.Orientation used to establish Horizontal/Vertical layout during control configuration
	 * @param trackSurroundsThumb Boolean used to determine thumb placement when control is configured
	 */
	public Slider(Screen screen, String UID, Vector2f position, Orientation orientation, boolean trackSurroundsThumb) {
		this(screen, UID, position,
			screen.getStyle("Slider").getVector2f("defaultSize"),
			screen.getStyle("Slider").getVector4f("resizeBorders"),
			screen.getStyle("Slider").getString("defaultImg"),
			orientation,
			trackSurroundsThumb
		);
	}
	
	/**
	 * Creates a new instance of the Slider control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 * @param orientation Slider.Orientation used to establish Horizontal/Vertical layout during control configuration
	 * @param trackSurroundsThumb  Boolean used to determine thumb placement when control is configured
	 */
	public Slider(Screen screen, String UID, Vector2f position, Vector2f dimensions, Orientation orientation, boolean trackSurroundsThumb) {
		this(screen, UID, position, dimensions,
			screen.getStyle("Slider").getVector4f("resizeBorders"),
			screen.getStyle("Slider").getString("defaultImg"),
			orientation,
			trackSurroundsThumb
		);
	}
	
	/**
	 * Creates a new instance of the Slider control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 * @param resizeBorders A Vector4f containg the border information used when resizing the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg The default image to use for the Slider's track
	 * @param orientation Slider.Orientation used to establish Horizontal/Vertical layout during control configuration
	 * @param trackSurroundsThumb  Boolean used to determine thumb placement when control is configured
	 */
	public Slider(Screen screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg, Orientation orientation, boolean trackSurroundsThumb) {
		super(screen, UID, position, dimensions, resizeBorders, 
			screen.getStyle("ScrollArea#VScrollBar").getString("trackImg")
		);
		
		this.orientation = orientation;
		this.trackSurroundsThumb = trackSurroundsThumb;;
		
		if (orientation == Orientation.VERTICAL) {
			controlSize = dimensions.x;
			controlLength = dimensions.y;
		} else {
			controlSize = dimensions.y;
			controlLength = dimensions.x;
		}
		
		this.configureControl();
		
		elThumbLock = new Element(
			screen,
			UID + ":Thumb",
			thumbLockPosition,
			thumbLockSize,
			new Vector4f(5,5,5,5),
			null
		) { 
			@Override
			public void moveTo(float x, float y) {
				Slider slider = ((Slider)getElementParent());
				if (isStepped) {
					int index = 0;
					if (slider.orientation == Slider.Orientation.HORIZONTAL) {
						index = (int)Math.round(x/stepSize);
						x = index*stepSize;
						if (slider.trackSurroundsThumb) x += controlSize/2;
					} else {
						index = (int)Math.round(y/stepSize);
						y = index*stepSize;
						if (slider.trackSurroundsThumb) y += controlSize/2;
					}
					if (slider.getSelectedIndex() != index && index > -1 && index < slider.stepValues.size()) {
						slider.setSelectedIndex(index);
					}
				} else {
					int percent = 0;
					if (slider.orientation == Slider.Orientation.HORIZONTAL) {
						percent = (int)((x/slider.getWidth())*100);
					} else {
						percent = (int)((y/slider.getHeight())*100);
					}
					if (slider.getSelectedIndex() != percent && percent >= 0 && percent <= 100) {
						slider.setSelectedIndex(percent);
					}
				}
				if (getLockToParentBounds()) {
					if (slider.trackSurroundsThumb) {
						if (slider.orientation == Slider.Orientation.HORIZONTAL) {
							if (x < controlSize/2) { x = controlSize/2; }
							if (y < 0) { y = 0; }
							if (x > getElementParent().getWidth()-controlSize/2) {
								x = getElementParent().getWidth()-controlSize/2;
							}
							if (y > getElementParent().getHeight()-getHeight()) {
								y = getElementParent().getHeight()-getHeight();
							}
						} else {
							if (x < 0) { x = 0; }
							if (y < controlSize/2) { y = controlSize/2; }
							if (x > getElementParent().getWidth()-getWidth()) {
								x = getElementParent().getWidth()-getWidth();
							}
							if (y > getElementParent().getHeight()-controlSize/2) {
								y = getElementParent().getHeight()-controlSize/2;
							}
						}
					} else {
						if (x < 0) { x = 0; }
						if (y < 0) { y = 0; }
						if (x > getElementParent().getWidth()-getWidth()) {
							x = getElementParent().getWidth()-getWidth();
						}
						if (y > getElementParent().getHeight()-getHeight()) {
							y = getElementParent().getHeight()-getHeight();
						}
					}
				}
				this.setX(x);
				this.setY(y);
				controlMoveHook();
			}
		};
		elThumbLock.setDockN(true);
		elThumbLock.setDockW(true);
		elThumbLock.setlockToParentBounds(true);
		addChild(elThumbLock);
		
		elThumb = new Button(
			screen,
			UID + ":Thumb",
			thumbPosition,
			thumbSize,
			new Vector4f(5,5,5,5),
			screen.getStyle("Button").getString("defaultImg")
		) {
			@Override
			public void onMouseLeftDown(MouseButtonEvent evt, boolean toggled) {  }
			@Override
			public void onMouseRightDown(MouseButtonEvent evt, boolean toggled) {  }
			@Override
			public void onMouseLeftUp(MouseButtonEvent evt, boolean toggled) {  }
			@Override
			public void onMouseRightUp(MouseButtonEvent evt, boolean toggled) {  }
			@Override
			public void onButtonFocus(MouseMotionEvent evt) {  }
			@Override
			public void onButtonLostFocus(MouseMotionEvent evt) {  }
			@Override
			public void onStillPressedInterval() {  }
			
		};
		elThumbLock.addChild(elThumb);
		
		if (orientation == Orientation.VERTICAL) {
			this.setScaleNS(true);
			elThumb.setScaleNS(false);
			this.setScaleEW(false);
			elThumb.setScaleEW(false);
		} else {
			this.setScaleNS(false);
			elThumb.setScaleNS(false);
			this.setScaleEW(true);
			elThumb.setScaleEW(false);
		}
		
		elThumb.setDockN(true);
		elThumb.setDockW(true);
		elThumb.setIsMovable(true);
		elThumb.setEffectParent(true);
		
		removeEffect(Effect.EffectEvent.Hover);
		removeEffect(Effect.EffectEvent.Press);
		removeEffect(Effect.EffectEvent.LoseFocus);
		
		this.setInterval(100);
	}
	
	private void configureControl() {
		if (orientation == Orientation.HORIZONTAL) {
			if (trackSurroundsThumb) {
				thumbLockSize.set(1, controlSize);
				thumbLockPosition.set(controlSize/2,0);
				thumbSize.set(controlSize, controlSize);
				thumbPosition.set(-(controlSize/2),0);
			} else {
				setY(getY()+(controlSize/3));
				setDimensions(controlLength, controlSize/3);
				thumbLockSize.set(1, controlSize/3);
				thumbLockPosition.set(0, 0);
				thumbSize.set(controlSize/3*2, controlSize);
				thumbPosition.set(-(controlSize/4), -(controlSize/3));
			}
		} else {
			if (trackSurroundsThumb) {
				thumbLockSize.set(controlSize, 1);
				thumbLockPosition.set(0,controlSize/2);
				thumbSize.set(controlSize, controlSize);
				thumbPosition.set(0,-(controlSize/2));
			} else {
				setX(getX()+(controlSize/3));
				setDimensions(controlSize/3, controlLength);
				thumbLockSize.set(controlSize/3, 1);
				thumbLockPosition.set(0,0);
				thumbSize.set(controlSize, controlSize/3*2);
				thumbPosition.set(-(controlSize/3), -(controlSize/4));
			}
		}
	}
	
	/**
	 * Adds a step value to the Slider.  When 2 or more step values are associated with
	 * a slider, the thumb movement becomes stepped and advances to the next/previous slot
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
			this.setInterval(1);
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
			this.setInterval(100);
		}
	}
	
	private void setStepSize() {
		if (orientation == Orientation.HORIZONTAL) {
			if (trackSurroundsThumb) {
				stepSize = (getWidth()-controlSize)/(stepValues.size()-1);
			} else {
				stepSize = getWidth()/(stepValues.size()-1);
			}
		} else {
			if (trackSurroundsThumb) {
				stepSize = (getHeight()-controlSize)/(stepValues.size()-1);
			} else {
				stepSize = getHeight()/(stepValues.size()-1);
			}
		}
		System.out.println("Slider: " + (getHeight()-controlSize));
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
	 * Sets the Slider's selected index to the selected step index specified and
	 * moves the Slider's thumb to appropriate x/y coordinates to reflect this change.
	 * 
	 * @param selectedIndex The index to set the Slider's selectedIndex to.
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
	public abstract void onChange(int selectedIndex, String value);
	
	@Override
	public void onMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
		trackEvt = evt;
		updateThumbByTrackClick();
	}
	
	@Override
	public void onMouseRightDown(MouseButtonEvent evt, boolean toggled) {
		
	}
	
	@Override
	public void onMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
		
	}
	
	@Override
	public void onMouseRightUp(MouseButtonEvent evt, boolean toggled) {
		
	}
	
	@Override
	public void onButtonFocus(MouseMotionEvent evt) {
		
	}
	
	@Override
	public void onButtonLostFocus(MouseMotionEvent evt) {
		
	}
	
	@Override
	public void onStillPressedInterval() {
		updateThumbByTrackClick();
	}
	
	private void updateThumbByTrackClick() {
		float step = (isStepped) ? stepSize : 1;
		if (orientation == Orientation.HORIZONTAL) {
			if (trackEvt.getX()-getAbsoluteX() < elThumbLock.getX()) {
				if (elThumbLock.getX()-step > trackEvt.getX()-getAbsoluteX()) {
					elThumbLock.setX(elThumbLock.getX()-step);
				}
				int index = (int)Math.round(elThumbLock.getX()/step);
				int percent = (int)((elThumbLock.getX()/getWidth())*100);
				if (isStepped)	setSelectedIndex(index);
				else			setSelectedIndex(percent);
			} else if (trackEvt.getX()-getAbsoluteX() > elThumbLock.getX()) {
				if (elThumbLock.getX()+step < trackEvt.getX()-getAbsoluteX()) {
					elThumbLock.setX(elThumbLock.getX()+step);
				}
				int index = (int)Math.round(elThumbLock.getX()/step);
				int percent = (int)((elThumbLock.getX()/getWidth())*100);
				if (isStepped)	setSelectedIndex(index);
				else			setSelectedIndex(percent);
			}
		} else {
			if (trackEvt.getY()-getAbsoluteY() < elThumbLock.getY()) {
				if (elThumbLock.getY()-step > trackEvt.getY()-getAbsoluteY()) {
					elThumbLock.setY(elThumbLock.getY()-step);
				}
				int index = (int)Math.round(elThumbLock.getY()/step);
				int percent = (int)((elThumbLock.getY()/getHeight())*100);
				if (isStepped) {
					if (index >= 0 && index < stepValues.size())
						setSelectedIndex(index);
				} else			setSelectedIndex(percent);
			} else if (trackEvt.getY()-getAbsoluteY() > elThumbLock.getY()) {
				if (elThumbLock.getY()+step < trackEvt.getY()-getAbsoluteY()) {
					elThumbLock.setY(elThumbLock.getY()+step);
				}
				int index = (int)Math.round(elThumbLock.getY()/step);
				int percent = (int)((elThumbLock.getY()/getHeight())*100);
				if (isStepped) {
					if (index >= 0 && index < stepValues.size())
						setSelectedIndex(index);
				} else			setSelectedIndex(percent);
			}
		}
	}
}
