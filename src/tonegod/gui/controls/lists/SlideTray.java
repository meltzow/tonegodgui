/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.lists;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import java.util.ArrayList;
import java.util.List;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.core.Element;
import tonegod.gui.core.ElementManager;
import tonegod.gui.core.utils.UIDUtil;
import tonegod.gui.effects.BatchEffect;
import tonegod.gui.effects.Effect;

/**
 *
 * @author t0neg0d
 */
public class SlideTray extends Element {
	public static enum Orientation {
		VERTICAL,
		HORIZONTAL
	}
	private Orientation orientation;
	
	private ButtonAdapter btnPrevElement, btnNextElement;
	private Element elTray;
	
	private List<Element> trayElements = new ArrayList();
	private int currentElementIndex = 0;
	
	private float trayPadding = 5;
	
	private boolean useSlideEffect = false;
	private Effect slideEffect;
	
	private BatchEffect batch = null;
	
	/**
	 * Creates a new instance of the SlideTray control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param orientation The orientation of the SlideTray
	 */
	public SlideTray(ElementManager screen, Vector2f position, Orientation orientation) {
		this(screen, UIDUtil.getUID(), position,
			screen.getStyle("Menu").getVector2f("defaultSize"),
			screen.getStyle("Menu").getVector4f("resizeBorders"),
			screen.getStyle("Menu").getString("defaultImg"),
			orientation
		);
	}
	
	/**
	 * Creates a new instance of the SlideTray control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 * @param orientation The orientation of the SlideTray
	 */
	public SlideTray(ElementManager screen, Vector2f position, Vector2f dimensions, Orientation orientation) {
		this(screen, UIDUtil.getUID(), position, dimensions,
			screen.getStyle("Menu").getVector4f("resizeBorders"),
			screen.getStyle("Menu").getString("defaultImg"),
			orientation
		);
	}
	
	/**
	 * Creates a new instance of the SlideTray control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 * @param resizeBorders A Vector4f containg the border information used when resizing the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg The default image to use for the SlideTray's track
	 * @param orientation The orientation of the SlideTray
	 */
	public SlideTray(ElementManager screen, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg, Orientation orientation) {
		this(screen, UIDUtil.getUID(), position, dimensions, resizeBorders, defaultImg, orientation);
	}
	
	/**
	 * Creates a new instance of the SlideTray control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param orientation The orientation of the SlideTray
	 */
	public SlideTray(ElementManager screen, String UID, Vector2f position, Orientation orientation) {
		this(screen, UID, position,
			screen.getStyle("Menu").getVector2f("defaultSize"),
			screen.getStyle("Menu").getVector4f("resizeBorders"),
			screen.getStyle("Menu").getString("defaultImg"),
			orientation
		);
	}
	
	/**
	 * Creates a new instance of the SlideTray control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 * @param orientation The orientation of the SlideTray
	 */
	public SlideTray(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Orientation orientation) {
		this(screen, UID, position, dimensions,
			screen.getStyle("Menu").getVector4f("resizeBorders"),
			screen.getStyle("Menu").getString("defaultImg"),
			orientation
		);
	}
	
	/**
	 * Creates a new instance of the SlideTray control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 * @param resizeBorders A Vector4f containg the border information used when resizing the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg The default image to use for the SlideTray's track
	 * @param orientation The orientation of the SlideTray
	 */
	public SlideTray(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg, Orientation orientation) {
		super(screen, UID, position, dimensions, resizeBorders, null);
		this.orientation = orientation;
		
		this.setDocking(Docking.NW);
		this.setScaleEW(true);
		this.setScaleNS(false);
		
		slideEffect = new Effect(Effect.EffectType.SlideTo, Effect.EffectEvent.Show, .25f);
		
		btnPrevElement = new ButtonAdapter(screen, getUID() + ":btnPrevElement",
			new Vector2f(-20, 0),
			new Vector2f(20, getHeight()),
			new Vector4f(0,0,0,0),
			null
		) {
			@Override
			public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean isToggled) {
				if (batch == null)
					prevElement();
				else if (!batch.getIsActive())
					prevElement();
			}
		};
		btnPrevElement.setButtonIcon(18, 18, screen.getStyle("Common").getString("arrowLeft"));
		btnPrevElement.clearAltImages();
		btnPrevElement.setDocking(Docking.SW);
		btnPrevElement.setScaleEW(false);
		btnPrevElement.setScaleNS(false);
		
		btnNextElement = new ButtonAdapter(screen, getUID() + ":btnNextElement",
			new Vector2f(getWidth(), 0),
			new Vector2f(20, getHeight()),
			new Vector4f(0,0,0,0),
			null
		) {
			@Override
			public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean isToggled) {
				if (batch == null)
					nextElement();
				else if (!batch.getIsActive())
					nextElement();
			}
		};
		btnNextElement.setButtonIcon(18, 18, screen.getStyle("Common").getString("arrowRight"));
		btnNextElement.clearAltImages();
		btnNextElement.setDocking(Docking.SE);
		btnNextElement.setScaleEW(false);
		btnNextElement.setScaleNS(false);
		
		elTray = new Element(screen, getUID() + ":elTray",
			new Vector2f(0,0),
			new Vector2f(getWidth(), getHeight()),
			new Vector4f(0,0,0,0),
			null
		);
		elTray.setDocking(Docking.SW);
		elTray.setScaleEW(true);
		elTray.setScaleNS(false);
		
		addChild(elTray);
		addChild(btnPrevElement);
		addChild(btnNextElement);
	}
	
	/**
	 * Enables/disables the use of the SlideTo effect when using next/previous buttons
	 * @param useSlideEffect 
	 */
	public void setUseSlideEffect(boolean useSlideEffect) {
		this.useSlideEffect = useSlideEffect;
	}
	
	/**
	 * Adds the provided Element as a tray item
	 * @param element 
	 */
	public void addTrayElement(Element element) {
		if (orientation == Orientation.HORIZONTAL)
			element.setPosition(getNextPosition(),0);
		else
			element.setPosition(0,getNextPosition());
		element.setClippingLayer(elTray);
		element.setDocking(Docking.SW);
		element.setScaleEW(false);
		element.setScaleNS(false);
		trayElements.add(element);
		elTray.addChild(element);
	}
	
	private void nextElement() {
		if (currentElementIndex+1 < trayElements.size()) {
			if (useSlideEffect) {
				batch = new BatchEffect();
				float diff;
				if (orientation == Orientation.HORIZONTAL)
					diff = (trayElements.get(currentElementIndex).getWidth()+trayPadding);
				else
					diff = (trayElements.get(currentElementIndex).getHeight()+trayPadding);
				for (Element el : trayElements) {
					if (orientation == Orientation.HORIZONTAL) {
						Vector2f destination = new Vector2f(el.getX()-diff,el.getY());
						Effect effect = slideEffect.clone();
						effect.setElement(el);
						effect.setEffectDestination(destination);
						batch.addEffect(effect);
					} else {
						Vector2f destination = new Vector2f(el.getX(),el.getY()+diff);
						Effect effect = slideEffect.clone();
						effect.setElement(el);
						effect.setEffectDestination(destination);
						batch.addEffect(effect);
					}
				}
				screen.getEffectManager().applyBatchEffect(batch);
			} else {
				for (Element el : trayElements) {
					if (orientation == Orientation.HORIZONTAL) {
						el.setX(el.getX()-(trayElements.get(currentElementIndex).getWidth()+trayPadding));
					} else {
						el.setY(el.getY()+(trayElements.get(currentElementIndex).getHeight()+trayPadding));
					}
				}
			}
			currentElementIndex++;
		}
	}
	
	private void prevElement() {
		if (currentElementIndex-1 > -1) {
			if (useSlideEffect) {
				batch = new BatchEffect();
				float diff;
				if (orientation == Orientation.HORIZONTAL)
					diff = (trayElements.get(currentElementIndex-1).getWidth()+trayPadding);
				else
					diff = (trayElements.get(currentElementIndex-1).getHeight()+trayPadding);
				for (Element el : trayElements) {
					if (orientation == Orientation.HORIZONTAL) {
						Vector2f destination = new Vector2f(el.getX()+diff,el.getY());
						Effect effect = slideEffect.clone();
						effect.setElement(el);
						effect.setEffectDestination(destination);
						batch.addEffect(effect);
					} else {
						Vector2f destination = new Vector2f(el.getX(),el.getY()-diff);
						Effect effect = slideEffect.clone();
						effect.setElement(el);
						effect.setEffectDestination(destination);
						batch.addEffect(effect);
					}
				}
				screen.getEffectManager().applyBatchEffect(batch);
			} else {
				for (Element el : trayElements) {
					if (orientation == Orientation.HORIZONTAL) {
						el.setX(el.getX()+(trayElements.get(currentElementIndex-1).getWidth()+trayPadding));
					} else {
						el.setY(el.getY()-(trayElements.get(currentElementIndex-1).getHeight()+trayPadding));
					}
				}
			}
			currentElementIndex--;
		}
	}
	
	private float getNextPosition() {
		float ret = 0;
		for (Element el : trayElements) {
			if (orientation == Orientation.HORIZONTAL) {
				ret += el.getWidth()+trayPadding;
			} else {
				ret += el.getHeight()+trayPadding;
			}
		}
		return ret;
	}
	
	@Override
	public void setControlClippingLayer(Element clippingLayer) {
		setClippingLayer(clippingLayer);
		for (Element el : elementChildren.values()) {
			if (!trayElements.contains(el))
				el.setControlClippingLayer(clippingLayer);
		}
	}
}
