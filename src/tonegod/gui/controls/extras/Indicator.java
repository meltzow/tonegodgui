/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.extras;

import com.jme3.font.BitmapFont;
import com.jme3.font.LineWrapMode;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import java.util.Set;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;
import tonegod.gui.effects.Effect;
import tonegod.gui.listeners.MouseFocusListener;

/**
 *
 * @author t0neg0d
 */
public class Indicator extends Element {
	public static enum Orientation {
		HORIZONTAL,
		VERTICAL
	}
	private float maxValue = 0, currentValue = 0, percentage = 0;
	private Orientation orientation;
	private ColorRGBA indicatorColor;
	private String alphaMapPath;
	private String overlayImg;
	private Element elIndicator, elOverlay;
	private boolean displayValues = false, displayPercentages = false;
	
	/**
	 * Creates a new instance of the Indicator control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 */
	public Indicator(Screen screen, String UID, Vector2f position, Orientation orientation) {
		this(screen, UID, position,
			screen.getStyle("Indicator").getVector2f("defaultSize"),
			screen.getStyle("Indicator").getVector4f("resizeBorders"),
			screen.getStyle("Indicator").getString("defaultImg"),
			orientation
		);
	}
	
	/**
	 * Creates a new instance of the Indicator control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 */
	public Indicator(Screen screen, String UID, Vector2f position, Vector2f dimensions, Orientation orientation) {
		this(screen, UID, position, dimensions,
			screen.getStyle("Indicator").getVector4f("resizeBorders"),
			screen.getStyle("Indicator").getString("defaultImg"),
			orientation
		);
	}
	
	/**
	 * Creates a new instance of the Indicator control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 * @param resizeBorders A Vector4f containg the border information used when resizing the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg The default image to use for the Slider's track
	 */
	public Indicator(Screen screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg, Orientation orientation) {
		super(screen, UID, position, dimensions, resizeBorders, null);
		
		setScaleEW(true);
		setScaleNS(false);
		
		this.overlayImg = defaultImg;
		this.orientation = orientation;
		
		elIndicator = new Element(
			screen,
			UID + ":Indicator",
			new Vector2f(0,0),
			dimensions.clone(),
			resizeBorders.clone(),
			null
		) {
			@Override
			public void setControlClippingLayer(Element clippingLayer) {
			//	setClippingLayer(clippingLayer);
				Set<String> keys = elementChildren.keySet();
				for (String key : keys) {
					elementChildren.get(key).setControlClippingLayer(clippingLayer);
				}
			}
			@Override
			public void updateLocalClipping() {
				Indicator ind = ((Indicator)this.getElementParent());
				if (getIsVisible()) {
					if (getClippingLayer() != null) {
						float clipX = 0, clipY = 0, clipW = 0, clipH = 0;
						if (getElementParent().getClippingLayer() != null) {
							clipX = (getElementParent().getClippingBounds().x > getClippingLayer().getAbsoluteX()) ? getElementParent().getClippingBounds().x : getClippingLayer().getAbsoluteX();
							clipY = (getElementParent().getClippingBounds().y > getClippingLayer().getAbsoluteY()) ? getElementParent().getClippingBounds().y : getClippingLayer().getAbsoluteY();
							if (ind.getOrientation() == Indicator.Orientation.HORIZONTAL) {
								clipW = (getElementParent().getClippingBounds().z < getClippingLayer().getAbsoluteWidth()-(getClippingLayer().getWidth()-ind.getCurrentPercentage())) ? getElementParent().getClippingBounds().z : getClippingLayer().getAbsoluteWidth()-(getClippingLayer().getWidth()-ind.getCurrentPercentage());
								clipH = (getElementParent().getClippingBounds().w < getClippingLayer().getAbsoluteHeight()) ? getElementParent().getClippingBounds().w : getClippingLayer().getAbsoluteHeight();
							} else {
								clipW = (getElementParent().getClippingBounds().z < getClippingLayer().getAbsoluteWidth()) ? getElementParent().getClippingBounds().z : getClippingLayer().getAbsoluteWidth();
								clipH = (getElementParent().getClippingBounds().w < getClippingLayer().getAbsoluteHeight()-(getClippingLayer().getHeight()-ind.getCurrentPercentage())) ? getElementParent().getClippingBounds().w : getClippingLayer().getAbsoluteHeight()-(getClippingLayer().getHeight()-ind.getCurrentPercentage());
							}
						} else {
							clipX = getClippingLayer().getAbsoluteX();
							clipY = getClippingLayer().getAbsoluteY();
							if (ind.getOrientation() == Indicator.Orientation.HORIZONTAL) {
								clipW = getClippingLayer().getAbsoluteWidth()-(getClippingLayer().getWidth()-ind.getCurrentPercentage());
								clipH = getClippingLayer().getAbsoluteHeight();
							} else {
								clipW = getClippingLayer().getAbsoluteWidth();
								clipH = getClippingLayer().getAbsoluteHeight()-(getClippingLayer().getHeight()-ind.getCurrentPercentage());
							}
						}
						getClippingBounds().set(clipX, clipY, clipW, clipH);
						getElementMaterial().setVector4("Clipping", getClippingBounds());
						getElementMaterial().setBoolean("UseClipping", true);
					} else {
						getElementMaterial().setBoolean("UseClipping", false);
					}
				} else {
					getClippingBounds().set(0,0,0,0);
					getElementMaterial().setVector4("Clipping", getClippingBounds());
					getElementMaterial().setBoolean("UseClipping", true);
				}
				//setFontPages();
			}
		};
		elIndicator.setClippingLayer(elIndicator);
		elIndicator.setIgnoreMouse(true);
		elIndicator.setDockS(true);
		elIndicator.setDockW(true);
		elIndicator.setScaleEW(true);
		elIndicator.setScaleNS(false);
		addChild(elIndicator);
		
		elOverlay = new Element(
			screen,
			UID + ":Overlay",
			new Vector2f(0,0),
			dimensions.clone(),
			resizeBorders.clone(),
			overlayImg
		);
		elOverlay.setIgnoreMouse(true);
		elOverlay.setDockS(true);
		elOverlay.setDockW(true);
		elOverlay.setScaleEW(false);
		elOverlay.setScaleNS(false);
		
	//	elOverlay.setTextAlign(BitmapFont.Align.Center);
	//	elOverlay.setTextVAlign(BitmapFont.VAlign.Center);
		
		// Load default font info
		elOverlay.setFontColor(screen.getStyle("Indicator").getColorRGBA("fontColor"));
		elOverlay.setFontSize(screen.getStyle("Indicator").getFloat("fontSize"));
		elOverlay.setTextAlign(BitmapFont.Align.valueOf(screen.getStyle("Indicator").getString("textAlign")));
		elOverlay.setTextVAlign(BitmapFont.VAlign.valueOf(screen.getStyle("Indicator").getString("textVAlign")));
		elOverlay.setTextWrap(LineWrapMode.valueOf(screen.getStyle("Indicator").getString("textWrap")));
		elOverlay.setTextClipPadding(screen.getStyle("Indicator").getFloat("textPadding"));
		elOverlay.setTextPadding(screen.getStyle("Indicator").getFloat("textPadding"));
		
		addChild(elOverlay);
		
	}
	
	public void setScaling(boolean scaleNS, boolean scaleEW) {
		setScaleNS(scaleNS);
		elIndicator.setScaleNS(scaleNS);
		elOverlay.setScaleNS(scaleNS);
		setScaleEW(scaleEW);
		elIndicator.setScaleEW(scaleEW);
		elOverlay.setScaleEW(scaleEW);
	}
	
	@Override
	public void controlResizeHook() {
		refactorIndicator();
	}
	
	public Orientation getOrientation() {
		return this.orientation;
	}
	
	public void setIndicatorColor(ColorRGBA indicatorColor) {
		this.indicatorColor = indicatorColor;
		elIndicator.getElementMaterial().setColor("Color", this.indicatorColor);
	}
	
	public void setMaxValue(float maxValue) {
		this.maxValue = maxValue;
		refactorIndicator();
	}
	
	public float getMaxValue() {
		return this.maxValue;
	}
	
	public void setCurrentValue(float currentValue) {
		this.currentValue = currentValue;
		refactorIndicator();
	}
	
	public float getCurrentValue() {
		return this.currentValue;
	}
	
	private void refactorIndicator() {
		if (currentValue > maxValue) {
			currentValue = maxValue;
		} else if (currentValue < 0) {
			currentValue = 0;
		}
		percentage = currentValue/maxValue;
		if (alphaMapPath == null) {
			if (orientation == Orientation.HORIZONTAL) {
				percentage *= getWidth();
				elIndicator.setWidth(percentage);
			} else {
				percentage *= getHeight();
				elIndicator.setHeight(percentage);
			}
		} else {
			if (orientation == Orientation.HORIZONTAL) {
				percentage *= getWidth();
			} else {
				percentage *= getHeight();
			}
			elIndicator.updateLocalClipping();
		}
		
		if (this.displayValues) {
			elOverlay.setText(String.valueOf((int)this.currentValue) + "/" + String.valueOf((int)this.maxValue));
		} else if (this.displayPercentages) {
			elOverlay.setText(String.valueOf((int)((this.currentValue/this.maxValue)*100)) + "%");
		} else {
			elOverlay.setText("");
		}
	}
	
	public float getCurrentPercentage() {
		return this.percentage;
	}
	
	public void setIndicatorAlphaMap(String alphaMapPath) {
		this.alphaMapPath = alphaMapPath;
		elIndicator.setAlphaMap(this.alphaMapPath);
	}
	
	public Element getTextDisplayElement() {
		return this.elOverlay;
	}
	
	public void setDisplayValues() {
		this.displayPercentages = false;
		this.displayValues = true;
	}
	
	public void setDisplayPercentage() {
		this.displayPercentages = true;
		this.displayValues = false;
	}
	
	public void setHideText() {
		this.displayPercentages = false;
		this.displayValues = false;
	}
	
	public void setBaseImage(String imgPath) {
		Texture tex = screen.getApplication().getAssetManager().loadTexture(imgPath);
		tex.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
		tex.setMagFilter(Texture.MagFilter.Bilinear);
		tex.setWrap(Texture.WrapMode.Repeat);
		
		this.getElementMaterial().setTexture("ColorMap", tex);
		this.getElementMaterial().setColor("Color", ColorRGBA.White);
	}
}
