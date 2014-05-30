/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.extras;

import com.jme3.font.BitmapFont;
import com.jme3.font.LineWrapMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import tonegod.gui.core.Element;
import tonegod.gui.core.ElementManager;
import tonegod.gui.core.utils.UIDUtil;

/**
 *
 * @author t0neg0d
 */
public abstract class Indicator extends Element {
	/*
	public static enum Orientation {
		HORIZONTAL,
		VERTICAL
	}
	*/
	private float maxValue = 0, currentValue = 0, percentage = 0;
	private Orientation orientation;
	private ColorRGBA indicatorColor;
	private String alphaMapPath;
	private String overlayImg;
	private Element elIndicator, elOverlay;
	private boolean displayValues = false, displayPercentages = false;
	private Vector2f indDimensions = new Vector2f();
	private Vector4f indPadding = Vector4f.ZERO.clone();
	private boolean reverseDirection = false;
	
	/**
	 * Creates a new instance of the Indicator control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param position A Vector2f containing the x/y position of the Element
	 */
	public Indicator(ElementManager screen, Vector2f position, Orientation orientation) {
		this(screen, UIDUtil.getUID(), position,
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
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 */
	public Indicator(ElementManager screen, Vector2f position, Vector2f dimensions, Orientation orientation) {
		this(screen, UIDUtil.getUID(), position, dimensions,
			screen.getStyle("Indicator").getVector4f("resizeBorders"),
			screen.getStyle("Indicator").getString("defaultImg"),
			orientation
		);
	}
	
	/**
	 * Creates a new instance of the Indicator control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 * @param resizeBorders A Vector4f containg the border information used when resizing the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg The default image to use for the Element
	 */
	public Indicator(ElementManager screen, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg, Orientation orientation) {
		this(screen, UIDUtil.getUID(), position, dimensions, resizeBorders, defaultImg, orientation);
	}
	
	/**
	 * Creates a new instance of the Indicator control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 */
	public Indicator(ElementManager screen, String UID, Vector2f position, Orientation orientation) {
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
	public Indicator(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Orientation orientation) {
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
	public Indicator(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg, Orientation orientation) {
		super(screen, UID, position, dimensions, resizeBorders, null);
		
		indDimensions.set(dimensions);
		
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
			//	Set<String> keys = elementChildren.keySet();
				for (Element el : elementChildren.values()) {
					el.setControlClippingLayer(clippingLayer);
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
								if (reverseDirection) {
									clipX = getClippingLayer().getAbsoluteX()+(getClippingLayer().getAbsoluteWidth()-ind.getCurrentPercentage());
									clipW = getClippingLayer().getAbsoluteWidth();
								} else {
									clipW = getClippingLayer().getAbsoluteWidth()-(getClippingLayer().getWidth()-ind.getCurrentPercentage());
								}
								clipH = getClippingLayer().getAbsoluteHeight();
							} else {
								if (reverseDirection) {
									clipY = getClippingLayer().getAbsoluteY()+(getClippingLayer().getAbsoluteHeight()-ind.getCurrentPercentage());
									clipH = getClippingLayer().getAbsoluteHeight();
								} else {
									clipH = getClippingLayer().getAbsoluteHeight()-(getClippingLayer().getHeight()-ind.getCurrentPercentage());
								}
								clipW = getClippingLayer().getAbsoluteWidth();
							}
						}
						getClippingBounds().set(clipX, clipY, clipW, clipH);
						getElementMaterial().setVector4("Clipping", getClippingBounds());
						if (!(Boolean)getElementMaterial().getParam("UseClipping").getValue())
							getElementMaterial().setBoolean("UseClipping", true);
					} else {
						if ((Boolean)getElementMaterial().getParam("UseClipping").getValue())
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
	//	elIndicator.setDockS(true);
	//	elIndicator.setDockW(true);
		elIndicator.setDocking(Docking.SW);
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
		elOverlay.setScaleEW(true);
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
	
	/**
	 * Use this method in place of setScaleEW and setScaleNE
	 * @param scaleNS
	 * @param scaleEW 
	 */
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
		setIndicatorPadding(indPadding);
		refactorIndicator();
	}
	
	/**
	 * Returns the Indicator.Orientation of the Indicator instance
	 * @return Indicator.Orientation
	 */
	public Orientation getOrientation() {
		return this.orientation;
	}
	
	/**
	 * Sets the ColorRGBA value of the Indicator
	 * @param indicatorColor 
	 */
	public void setIndicatorColor(ColorRGBA indicatorColor) {
		this.indicatorColor = indicatorColor;
		elIndicator.getElementMaterial().setColor("Color", this.indicatorColor);
	}
	
	/**
	 * Set the maximum value (e.g. float  = 100%)
	 * @param maxValue 
	 */
	public void setMaxValue(float maxValue) {
		this.maxValue = maxValue;
		refactorIndicator();
	}
	
	/**
	 * Returns the maximum value set for the Indicator
	 * @return maxValue
	 */
	public float getMaxValue() {
		return this.maxValue;
	}
	
	/**
	 * Sets the current value of the Indicator
	 * @param currentValue 
	 */
	public void setCurrentValue(float currentValue) {
		this.currentValue = currentValue;
		refactorIndicator();
	}
	
	public void setReverseDirection(boolean reverseDirection) {
		this.reverseDirection = reverseDirection;
	}
	
	/**
	 * Returns the current value of the Indicator
	 * @return currentValue
	 */
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
	//	if (alphaMapPath == null) {
	//		if (orientation == Orientation.HORIZONTAL) {
	//			percentage *= getWidth();
	//			elIndicator.setWidth(percentage);
	//		} else {
	//			percentage *= getHeight();
	//			elIndicator.setHeight(percentage);
	//		}
	//	} else {
			if (orientation == Orientation.HORIZONTAL) {
				percentage *= indDimensions.x;
			} else {
				percentage *= indDimensions.y;
			}
			elIndicator.updateLocalClipping();
	//	}
		
		if (this.displayValues) {
			elOverlay.setText(String.valueOf((int)this.currentValue) + "/" + String.valueOf((int)this.maxValue));
		} else if (this.displayPercentages) {
			elOverlay.setText(String.valueOf((int)((this.currentValue/this.maxValue)*100)) + "%");
		} else {
			elOverlay.setText("");
		}
		
		onChange(currentValue, currentValue/maxValue*100);
	}
	
	/**
	 * Returns current value as a percent of the max value
	 * @return percentage
	 */
	public float getCurrentPercentage() {
		return this.percentage;
	}
	
	/**
	 * Use setAlphaMap instead
	 * @param alphaMapPath 
	 */
	@Deprecated
	public void setIndicatorAlphaMap(String alphaMapPath) {
		this.alphaMapPath = alphaMapPath;
		elIndicator.setAlphaMap(this.alphaMapPath);
	}
	
	/**
	 * Applies an alpha map to the indicator, allowing for curved shapes
	 * @param alphaMapPath 
	 */
	@Override
	public void setAlphaMap(String alphaMapPath) {
		this.alphaMapPath = alphaMapPath;
		elIndicator.setAlphaMap(this.alphaMapPath);
	}
	
	public Element getIndicatorElement() {
		return elIndicator;
	}
	
	public Element getOverlayElement() {
		return elOverlay;
	}
	
	/**
	 * Return the element used for displaying overlay text
	 * @return elOverlay
	 */
	public Element getTextDisplayElement() {
		return this.elOverlay;
	}
	
	/**
	 * Sets the display text to format as currentValue / maxValue
	 */
	public void setDisplayValues() {
		this.displayPercentages = false;
		this.displayValues = true;
	}
	
	/**
	 * Sets the display text to current value as percent %
	 */
	public void setDisplayPercentage() {
		this.displayPercentages = true;
		this.displayValues = false;
	}
	
	/**
	 * Hides the overlay display text
	 */
	public void setHideText() {
		this.displayPercentages = false;
		this.displayValues = false;
	}
	
	/**
	 * Set the image to use behind the indicator
	 * @param imgPath 
	 */
	public void setBaseImage(String imgPath) {
		setColorMap(imgPath);
	}
	
	/**
	 * Set the image to use in front of the indicator
	 * @param imgPath
	 */
	public void setOverlayImage(String imgPath) {
		elOverlay.setColorMap(imgPath);
	}
	
	public void setIndicatorImage(String imgPath) {
		elIndicator.setColorMap(imgPath);
	}
	
	public void setIndicatorPadding(Vector4f padding) {
		indPadding.set(padding);
		indDimensions.set(getWidth()-(padding.x+padding.z),getHeight()-(padding.y+padding.w));
		elIndicator.setPosition(padding.x,padding.y);
		elIndicator.setDimensions(indDimensions);
	}
	
	public abstract void onChange(float currentValue, float currentPercentage);
}
