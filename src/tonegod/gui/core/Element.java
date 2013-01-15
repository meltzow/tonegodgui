/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.core;

import com.jme3.app.Application;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.LineWrapMode;
import com.jme3.font.Rectangle;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.texture.Texture;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import tonegod.gui.effects.Effect;

/**
 *
 * @author t0neg0d
 */
public class Element extends Node {
	public static enum Borders {
		NW, N, NE, W, E, SW, S, SE;
	};
	public static enum Orientation {
		VERTICAL,
		HORIZONTAL
	}
	
	protected Application app;
	protected Screen screen;
	private String UID;
	private Vector2f position;
	private Vector2f dimensions;
	private Vector4f borders = new Vector4f(1,1,1,1);
	
	private boolean ignoreMouse = false;
	private boolean isMovable = false;
	private boolean lockToParentBounds = false;
	private boolean isResizable = false;
	private boolean resizeN = true;
	private boolean resizeS = true;
	private boolean resizeW = true;
	private boolean resizeE = true;
	private boolean dockN = false;
	private boolean dockW = true;
	private boolean dockE = false;
	private boolean dockS = true;
	private boolean scaleNS = true;
	private boolean scaleEW = true;
	
	
	private boolean effectParent = false;
	private boolean effectAbsoluteParent = false;
	
//	private float zOrderStepMajor = 10f;
//	private float zOrderStepMinor = 0.1f;
	
	private Geometry geom;
	private ElementQuadGrid model;
	private Material mat;
	private Texture defaultTex;
	
	protected BitmapText textElement;
	private Vector2f textPosition = new Vector2f(0,0);
	private LineWrapMode textWrap = LineWrapMode.Word;
	private BitmapFont.Align textAlign = BitmapFont.Align.Left;
	private BitmapFont.VAlign textVAlign = BitmapFont.VAlign.Top;
	private String text = "";
	protected BitmapFont font;
	protected float fontSize = 20;
	private float textPadding = 0;
	protected ColorRGBA fontColor = ColorRGBA.White;
	private ColorRGBA defaultColor = new ColorRGBA(1,1,1,0);
	
	private Element elementParent = null;
	protected Map<String, Element> elementChildren = new HashMap();
	
	private boolean isClipped = false;
	private boolean wasClipped = false;
	private Element clippingLayer;
	private Vector4f clippingBounds = new Vector4f();
	private float textClipPadding = 0;
	private boolean isVisible = true;
	private boolean wasVisible = true;
	
	private Map<Effect.EffectEvent, Effect> effects = new HashMap();
	
	public Element(Screen screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String texturePath) {
		this.app = screen.getApplication();
		this.screen = screen;
		this.UID = UID;
		this.position = position;
		this.dimensions = dimensions;
		this.borders = resizeBorders;
		
		BitmapFont tempFont = app.getAssetManager().loadFont(screen.getStyle("Font").getString("defaultFont"));
		
		font = new BitmapFont();
		font.setCharSet(app.getAssetManager().loadFont(screen.getStyle("Font").getString("defaultFont")).getCharSet());
		Material[] pages = new Material[tempFont.getPageSize()];
		for (int i = 0; i < pages.length; i++) {
			pages[i] = tempFont.getPage(i).clone();
		}
		font.setPages(pages);
		
		float imgWidth = 100;
		float pixelSize = 1f/imgWidth;
		
		if (texturePath != null) {
			defaultTex = app.getAssetManager().loadTexture(texturePath);
			defaultTex.setMinFilter(Texture.MinFilter.BilinearNearestMipMap);
			defaultTex.setMagFilter(Texture.MagFilter.Bilinear);
			defaultTex.setWrap(Texture.WrapMode.Repeat);

			imgWidth = defaultTex.getImage().getWidth();
			pixelSize = 1f/defaultTex.getImage().getWidth();
		}
		mat = new Material(app.getAssetManager(), "tonegod/gui/shaders/Unshaded.j3md");
		if (texturePath != null) {
			mat.setTexture("ColorMap", defaultTex);
			mat.setColor("Color", ColorRGBA.White);
		} else {
			mat.setColor("Color", defaultColor);
		}
		mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
		mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Back);
		
		this.model = new ElementQuadGrid(this.dimensions, borders, imgWidth, pixelSize);
		
		this.setName(UID + ":Node");
		geom = new Geometry(UID + ":Geometry");
		geom.setMesh(model);
		geom.setCullHint(CullHint.Never);
        geom.setQueueBucket(Bucket.Gui);
		geom.setMaterial(mat);
		this.attachChild(geom);
		
		this.setQueueBucket(Bucket.Gui);
		
		this.setLocalTranslation(position.x, position.y, 0);
	}
	
	public void addChild(Element child) {
		child.elementParent = this;
		child.setQueueBucket(RenderQueue.Bucket.Gui);
		
		elementChildren.put(child.getUID(), child);
		this.attachChild(child);
	}
	
	// Z-ORDER 
	protected void initZOrder(float zOrder) {
		setLocalTranslation(getLocalTranslation().setZ(
			zOrder
		));
		if (getTextElement() != null)
			getTextElement().setLocalTranslation(getTextElement().getLocalTranslation().setZ(
				screen.getZOrderStepMinor()
			));
		
		Set<String> keys = elementChildren.keySet();
		for (String key : keys) {
			elementChildren.get(key).initZOrder(screen.getZOrderStepMinor());
		}
	}
	
	// Recursive & non-recursive parent/child element searches
	/**
	 * Recursively searches children elements for specified element containing the specified UID
	 * @param UID - Unique Indentifier of element to search for
	 * @return Element containing UID or null if not found
	 */
	public Element getChildElementById(String UID) {
		Element ret = null;
		if (this.UID.equals(UID)) {
			ret = this;
		} else {
			if (elementChildren.containsKey(UID)) {
				ret = elementChildren.get(UID);
			} else {
				Set<String> keys = elementChildren.keySet();
				for (String key : keys) {
					ret = elementChildren.get(key).getChildElementById(UID);
					if (ret != null) {
						break;
					}
				}
			}
		}
		return ret;
	}
	
	/**
	 * Returns the top-most parent in the tree of Elements.  The topmost element will always have a parent of null
	 * @return Element elementParent
	 */
	public Element getAbsoluteParent() {
		if (elementParent == null) {
			return this;
		} else {
			return elementParent.getAbsoluteParent();
		}
	}
	
	/**
	 * Returns the parent element of this node
	 * @return Element elementParent
	 */
	public Element getElementParent() {
		return elementParent;
	}
	
	/**
	 * Sets the element's parent element
	 * @param elementParent Element
	 */
	public void setElementParent(Element elementParent) {
		this.elementParent = elementParent;
	}
	
	// Getters & Setters
	/**
	 * Returns the element's unique string identifier
	 * @return String UID
	 */
	public String getUID() {
		return UID;
	}
	
	/**
	 * Returns the default material for the element
	 * @return Material mat
	 */
	public Material getMaterial() {
		return this.mat;
	}
	
	public void setLocalMaterial(Material mat) {
		this.mat = mat;
		this.setMaterial(mat);
	}
	
	public void setIgnoreMouse(boolean ignoreMouse) {
		this.ignoreMouse = ignoreMouse;
	}
	
	public boolean getIgnoreMouse() {
		return this.ignoreMouse;
	}
	
	/**
	 * Enables draggable behavior for this element
	 * @param isMovable boolean
	 */
	public void setIsMovable(boolean isMovable) {
		this.isMovable = isMovable;
	}
	
	/**
	 * Returns if the element has draggable behavior set
	 * @return boolean isMovable
	 */
	public boolean getIsMovable() {
		return this.isMovable;
	}
	
	/**
	 * Enables resize behavior for this element
	 * @param isResizable boolean
	 */
	public void setIsResizable(boolean isResizable) {
		this.isResizable = isResizable;
	}
	
	/**
	 * Returns if the element has resize behavior set
	 * @return boolean isResizable
	 */
	public boolean getIsResizable() {
		return this.isResizable;
	}
	
	/**
	 * Enables/disables north border for resizing
	 * @param resizeN boolean
	 */
	public void setResizeN(boolean resizeN) {
		this.resizeS = resizeN;
	}
	
	/**
	 * Returns whether the elements north border has enabled/disabled resizing
	 * @return boolean resizeN
	 */
	public boolean getResizeN() {
		return this.resizeS;
	}
	
	/**
	 * Enables/disables south border for resizing
	 * @param resizeS boolean
	 */
	public void setResizeS(boolean resizeS) {
		this.resizeN = resizeS;
	}
	
	/**
	 * Returns whether the elements south border has enabled/disabled resizing
	 * @return boolean resizeS
	 */
	public boolean getResizeS() {
		return this.resizeN;
	}
	
	/**
	 * Enables/disables west border for resizing
	 * @param resizeW boolean
	 */
	public void setResizeW(boolean resizeW) {
		this.resizeW = resizeW;
	}
	
	/**
	 * Returns whether the elements west border has enabled/disabled resizing
	 * @return boolean resizeW
	 */
	public boolean getResizeW() {
		return this.resizeW;
	}
	
	/**
	 * Enables/disables east border for resizing
	 * @param resizeE boolean
	 */
	public void setResizeE(boolean resizeE) {
		this.resizeE = resizeE;
	}
	
	/**
	 * Returns whether the elements east border has enabled/disabled resizing
	 * @return boolean resizeE
	 */
	public boolean getResizeE() {
		return this.resizeE;
	}
	
	public void setDockN(boolean dockN) {
		this.dockS = dockN;
		this.dockN = !dockN;
	}
	
	public boolean getDockN() {
		return this.dockS;
	}
	
	public void setDockW(boolean dockW) {
		this.dockW = dockW;
		this.dockE = !dockW;
	}
	
	public boolean getDockW() {
		return this.dockW;
	}
	
	public void setDockE(boolean dockE) {
		this.dockE = dockE;
		this.dockW = !dockE;
	}
	
	public boolean getDockE() {
		return this.dockE;
	}
	
	public void setDockS(boolean dockS) {
		this.dockN = dockS;
		this.dockS = !dockS;
	}
	
	public boolean getDockS() {
		return this.dockN;
	}
	
	public void setScaleNS(boolean scaleNS) {
		this.scaleNS = scaleNS;
	}
	
	public boolean getScaleNS() {
		return this.scaleNS;
	}
	
	public void setScaleEW(boolean scaleEW) {
		this.scaleEW = scaleEW;
	}
	
	public boolean getScaleEW() {
		return this.scaleEW;
	}
	
	public void setEffectParent(boolean effectParent) {
		this.effectParent = effectParent;
	}
	
	public boolean getEffectParent() {
		return this.effectParent;
	}
	
	public void setEffectAbsoluteParent(boolean effectAbsoluteParent) {
		this.effectAbsoluteParent = effectAbsoluteParent;
	}
	
	public boolean getEffectAbsoluteParent() {
		return this.effectAbsoluteParent;
	}
	
	public void setlockToParentBounds(boolean lockToParentBounds) {
		this.lockToParentBounds = lockToParentBounds;
	}
	
	public boolean getLockToParentBounds() {
		return this.lockToParentBounds;
	}
	
	public void setPosition(Vector2f position) {
		this.position = position;
		updateNodeLocation();
	}
	
	public void setPosition(float x, float y) {
		this.position.setX(x);
		this.position.setY(y);
		updateNodeLocation();
	}
	
	public void setX(float x) {
		this.position.setX(x);
		updateNodeLocation();
	}
	
	public void setY(float y) {
		this.position.setY(y);
		updateNodeLocation();
	}
	
	private void updateNodeLocation() {
		this.setLocalTranslation(position.x, position.y, this.getLocalTranslation().getZ());
		updateClipping();
	}
	
	public Vector2f getPosition() {
		return position;
	}
	
	public float getX() {
		return position.x;
	}
	
	public float getY() {
		return position.y;
	}
	
	public float getAbsoluteX() {
		float x = getX();
		Element el = this;
		while (el.getElementParent() != null) {
			el = el.getElementParent();
			x += el.getX();
		}
		return x;
	}
	
	public float getAbsoluteY() {
		float y = getY();
		Element el = this;
		while (el.getElementParent() != null) {
			el = el.getElementParent();
			y += el.getY();
		}
		return y;
	}
	
	public void setDimensions(float w, float h) {
		this.dimensions.setX(w);
		this.dimensions.setY(h);
		getModel().updateDimensions(dimensions.x, dimensions.y);
		geom.updateModelBound();
		if (textElement != null) {
			updateTextElement();
		}
		updateClipping();
	}
	
	public void setDimensions(Vector2f dimensions) {
		this.dimensions = dimensions;
		getModel().updateDimensions(dimensions.x, dimensions.y);
		geom.updateModelBound();
		if (textElement != null) {
			updateTextElement();
		}
		updateClipping();
	}
	
	public void setWidth(float width) {
		this.dimensions.setX(width);
		getModel().updateWidth(dimensions.x);
		geom.updateModelBound();
		if (textElement != null) {
			updateTextElement();
		}
		updateClipping();
	}
	
	public void setHeight(float height) {
		this.dimensions.setY(height);
		getModel().updateHeight(dimensions.y);
		geom.updateModelBound();
		if (textElement != null) {
			updateTextElement();
		}
		updateClipping();
	}
	
	public Vector2f getDimensions() {
		return dimensions;
	}
	
	public float getWidth() {
		return dimensions.x;
	}
	
	public float getHeight() {
		return dimensions.y;
	}
	
	public float getAbsoluteWidth() {
		return getAbsoluteX() + getWidth();
	}
	
	public float getAbsoluteHeight() {
		return getAbsoluteY() + getHeight();
	}
	
	public void resize(float x, float y, Borders dir) {
		float prevWidth = getWidth();
		float prevHeight = getHeight();
		float oX = x, oY = y;
		if (getElementParent() != null) { x -= getAbsoluteX()-getX(); }
		if (getElementParent() != null) { y -= getAbsoluteY()-getY(); }
		float nextX, nextY;
		if (dir == Borders.NW) {
			if (getLockToParentBounds()) { if (x <= 0) { x = 0; } }
			if (resizeW) {
				setWidth(getX()+getWidth()-x);
				setX(x);
			}
			if (getLockToParentBounds()) { if (y <= 0) { y = 0; } }
			if (resizeN) {
				setHeight(getY()+getHeight()-y);
				setY(y);
			}
		} else if (dir == Borders.N) {
			if (getLockToParentBounds()) { if (y <= 0) { y = 0; } }
			if (resizeN) {
				setHeight(getY()+getHeight()-y);
				setY(y);
			}
		} else if (dir == Borders.NE) {
			nextX = oX-getAbsoluteX();
			if (getLockToParentBounds()) { if (nextX >= getElementParent().getWidth()-getX()) { nextX = getElementParent().getWidth()-getX(); } }
			if (resizeE) {
				setWidth(nextX);
			}
			if (getLockToParentBounds()) { if (y <= 0) { y = 0; } }
			if (resizeN) {
				setHeight(getY()+getHeight()-y);
				setY(y);
			}
		} else if (dir == Borders.W) {
			if (getLockToParentBounds()) { if (x <= 0) { x = 0; } }
			if (resizeW) {
				setWidth(getX()+getWidth()-x);
				setX(x);
			}
		} else if (dir == Borders.E) {
			nextX = oX-getAbsoluteX();
			if (getLockToParentBounds()) { if (nextX >= getElementParent().getWidth()-getX()) { nextX = getElementParent().getWidth()-getX(); } }
			if (resizeE) {
				setWidth(nextX);
			}
		} else if (dir == Borders.SW) {
			if (getLockToParentBounds()) { if (x <= 0) { x = 0; } }
			if (resizeW) {
				setWidth(getX()+getWidth()-x);
				setX(x);
			}
			nextY = oY-getAbsoluteY();
			if (getLockToParentBounds()) { if (nextY >= getElementParent().getHeight()-getY()) { nextY = getElementParent().getHeight()-getY(); } }
			if (resizeS) {
				setHeight(nextY);
			}
		} else if (dir == Borders.S) {
			nextY = oY-getAbsoluteY();
			if (getLockToParentBounds()) { if (nextY >= getElementParent().getHeight()-getY()) { nextY = getElementParent().getHeight()-getY(); } }
			if (resizeS) {
				setHeight(nextY);
			}
		} else if (dir == Borders.SE) {
			nextX = oX-getAbsoluteX();
			if (getLockToParentBounds()) { if (nextX >= getElementParent().getWidth()-getX()) { nextX = getElementParent().getWidth()-getX(); } }
			if (resizeE) {
				setWidth(nextX);
			}
			nextY = oY-getAbsoluteY();
			if (getLockToParentBounds()) { if (nextY >= getElementParent().getHeight()-getY()) { nextY = getElementParent().getHeight()-getY(); } }
			if (resizeS) {
				setHeight(nextY);
			}
		}
		float diffX = prevWidth-getWidth();
		float diffY = prevHeight-getHeight();
		controlResizeHook();
		Set<String> keys = elementChildren.keySet();
		for (String key : keys) {
			elementChildren.get(key).childResize(diffX,diffY,dir);
			elementChildren.get(key).controlResizeHook();
		}
	}
	
	private void childResize(float diffX, float diffY, Borders dir) {
		if (dir == Borders.NW || dir == Borders.N || dir == Borders.NE) {
			if (getScaleNS())	setHeight(getHeight()-diffY);
			if (getDockN() && !getScaleNS())	setY(getY()-diffY);
		} else if (dir == Borders.SW || dir == Borders.S || dir == Borders.SE) {
			if (getScaleNS())	setHeight(getHeight()-diffY);
			if (getDockN() && !getScaleNS())	setY(getY()-diffY);
		}
		if (dir == Borders.NW || dir == Borders.W || dir == Borders.SW) {
			if (getScaleEW())	setWidth(getWidth()-diffX);
			if (getDockE() && !getScaleEW())	setX(getX()-diffX);
		} else if (dir == Borders.NE || dir == Borders.E || dir == Borders.SE) {
			if (getScaleEW())	setWidth(getWidth()-diffX);
			if (getDockE() && !getScaleEW())	setX(getX()-diffX);
		}
		Set<String> keys = elementChildren.keySet();
		for (String key : keys) {
			elementChildren.get(key).childResize(diffX,diffY,dir);
		}
	}
	
	public void controlResizeHook() {
		
	}
	
	public void moveTo(float x, float y) {
		if (getLockToParentBounds()) {
			if (x < 0) { x = 0; }
			if (y < 0) { y = 0; }
			if (x > getElementParent().getWidth()-getWidth()) {
				x = getElementParent().getWidth()-getWidth();
			}
			if (y > getElementParent().getHeight()-getHeight()) {
				y = getElementParent().getHeight()-getHeight();
			}
		}
		setX(x);
		setY(y);
		controlMoveHook();
	}
	
	public void controlMoveHook() {
		
	}
	
	/**
	 * Set the north, west, east and south borders in number of pixels
	 * @param nBorder float
	 * @param wBorder float
	 * @param eBorder float
	 * @param sBorder float
	 */
	public void setResizeBorders(float borderSize) {
		borders.set(borderSize,borderSize,borderSize,borderSize);
	}
	
	/**
	 * Set the north, west, east and south borders in number of pixels
	 * @param nBorder float
	 * @param wBorder float
	 * @param eBorder float
	 * @param sBorder float
	 */
	public void setResizeBorders(float nBorder, float wBorder, float eBorder, float sBorder) {
		borders.setX(nBorder);
		borders.setY(wBorder);
		borders.setZ(eBorder);
		borders.setW(sBorder);
	}
	
	/**
	 * Sets the width of north border in number of pixels
	 * @param nBorder float
	 */
	public void setNorthResizeBorder(float nBorder) {
		borders.setX(nBorder);
	}
	
	/**
	 * Sets the width of west border in number of pixels
	 * @param wBorder float
	 */
	public void setWestResizeBorder(float wBorder) {
		borders.setY(wBorder);
	}
	
	/**
	 * Sets the width of east border in number of pixels
	 * @param eBorder float
	 */
	public void setEastResizeBorder(float eBorder) {
		borders.setZ(eBorder);
	}
	
	/**
	 * Sets the width of south border in number of pixels
	 * @param sBorder float
	 */
	public void setSouthResizeBorder(float sBorder) {
		borders.setW(sBorder);
	}
	
	public float getResizeBorderNorthSize() {
		return this.borders.x;
	}
	
	public float getResizeBorderWestSize() {
		return this.borders.y;
	}
	
	public float getResizeBorderEastSize() {
		return this.borders.z;
	}
	
	public float getResizeBorderSouthSize() {
		return this.borders.w;
	}
	
	public void setElementMaterial(Material mat) {
		this.mat = mat;
	}
	
	public Material getElementMaterial() {
		return this.mat;
	}
	
	public Texture getElementTexture() {
		return this.defaultTex;
	}
	
	public ElementQuadGrid getModel() {
		return this.model;
	}
	
	// Font & text
	/**
	 * Sets the element's text layer font
	 * @param fontPath String The font asset path
	 */
	public void setFont(String fontPath) {
		font = app.getAssetManager().loadFont(fontPath);
	}
	
	/**
	 * Returns the Bitmapfont used by the element's text layer
	 * @return BitmapFont font
	 */
	public BitmapFont getFont() {
		return this.font;
	}
	
	/**
	 * Sets the element's text layer font size
	 * @param fontSize float The size to set the font to
	 */
	public void setFontSize(float fontSize) {
		this.fontSize = fontSize;
	}
	
	/**
	 * Returns the element's text layer font size
	 * @return float fontSize
	 */
	public float getFontSize() {
		return this.fontSize;
	}
	
	/**
	 * Sets the element's text layer font color
	 * @param fontColor ColorRGBA The color to set the font to
	 */
	public void setFontColor(ColorRGBA fontColor) {
		this.fontColor = fontColor;
		if (textElement != null) {
			textElement.setColor(fontColor);
		}
	}
	
	/**
	 * Return the element's text layer font color
	 * @return ColorRGBA fontColor
	 */
	public ColorRGBA getFontColor() {
		return this.fontColor;
	}
	
	/**
	 * Sets the element's text layer horizontal alignment
	 * @param align Align textAlign
	 */
	public void setTextAlign(BitmapFont.Align textAlign) {
		this.textAlign = textAlign;
	}
	
	/**
	 * Returns the element's text layer horizontal alignment
	 * @return Align text Align
	 */
	public BitmapFont.Align getTextAlign() {
		return this.textAlign;
	}
	
	/**
	 * Sets the element's text layer vertical alignment
	 * @param valign VAlign textVAlign
	 */
	public void setTextVAlign(BitmapFont.VAlign textVAlign) {
		this.textVAlign = textVAlign;
	}
	
	/**
	 * Returns the element's text layer vertical alignment
	 * @return VAlign textVAlign
	 */
	public BitmapFont.VAlign getTextVAlign() {
		return this.textVAlign;
	}
	
	/**
	 * Sets the element's text later wrap mode
	 * @param textWrap LineWrapMode textWrap
	 */
	public void setTextWrap(LineWrapMode textWrap) {
		this.textWrap = textWrap;
	}
	
	/**
	 * Returns the element's text layer wrap mode
	 * @return LineWrapMode textWrap
	 */
	public LineWrapMode getTextWrap() {
		return this.textWrap;
	}
	
	/**
	 * Sets the elements text layer position
	 * @param x Position's x coord
	 * @param y Position's y coord
	 */
	public void setTextPosition(float x, float y) {
		this.textPosition = new Vector2f(x,y);
	}
	
	/**
	 * Returns the current x, y coords of the element's text layer
	 * @return Vector2f textPosition
	 */
	public Vector2f getTextPosition() {
		return this.textPosition;
	}
	
	/**
	 * Sets the padding set for the element's text layer
	 * @param textPadding 
	 */
	public void setTextPadding(float textPadding) {
		this.textPadding = textPadding;
	}
	
	/**
	 * Returns the ammount of padding set for the elements text layer
	 * @return float textPadding
	 */
	public float getTextPadding() {
		return this.textPadding;
	}
	
	/**
	 * Updates the element's textlayer position and boundary
	 */
	protected void updateTextElement() {
		if (textElement != null) {
			textElement.setLocalTranslation(textPosition.x+textPadding, getHeight()-(textPosition.y+textPadding), textElement.getLocalTranslation().z);
			textElement.setBox(new Rectangle(0,0,dimensions.x-(textPadding*4),dimensions.y-(textPadding*4)));
		}
	}
	
	/**
	 * Sets the text of the element.
	 * @param text String The text to display.
	 */
	public void setText(String text) {
		this.text = text;
		if (textElement == null) {
			textElement = new BitmapText(font, false);
			textElement.setBox(new Rectangle(0,0,dimensions.x,dimensions.y));
		}
		textElement.setLineWrapMode(textWrap);
		textElement.setAlignment(textAlign);
		textElement.setVerticalAlignment(textVAlign);
		textElement.setSize(fontSize);
		textElement.setColor(fontColor);
		textElement.setText(text);
		updateTextElement();
		if (textElement.getParent() == null) {
			this.attachChild(textElement);
		//	textElement.move(0,0,getNextZOrder());
		}
	}
	
	/**
	 * Retuns the current visible text of the element.
	 * @return String text
	 */
	public String getText() {
		return this.text;
	}
	
	public BitmapText getTextElement() {
		return this.textElement;
	}
	
	// Clipping
	public void setDefaultWasVisible(boolean wasVisible) {
		this.wasVisible = wasVisible;
	}
	
	public void show() {
		if (!isVisible) {
			screen.updateZOrder(getAbsoluteParent());
			this.isVisible = true;
			this.isClipped = wasClipped;
			updateClipping();
			controlShowHook();
			
			Set<String> keys = elementChildren.keySet();
			for (String key : keys) {
				elementChildren.get(key).childShow();
			}
			Effect effect = getEffect(Effect.EffectEvent.Show);
			if (effect != null) {
				screen.getEffectManager().applyEffect(effect);
			}
		}
	}
	
	public void childShow() {
		this.isVisible = wasVisible;
		this.isClipped = wasClipped;
		updateClipping();
		controlShowHook();
		
		Set<String> keys = elementChildren.keySet();
		for (String key : keys) {
			elementChildren.get(key).childShow();
		}
	}
	
	public void controlShowHook() {  }
	
	public void hide() {
		if (isVisible) {
			this.wasVisible = isVisible;
			this.isVisible = false;
			this.isClipped = true;
		}
		updateClipping();
		controlHideHook();
		Set<String> keys = elementChildren.keySet();
		for (String key : keys) {
			elementChildren.get(key).hide();
		}
	}
	
	public void controlHideHook() {  }
	
	public boolean getIsVisible() {
		return this.isVisible;
	}
	
	/**
	 * Sets the elements clipping layer to the provided element.
	 * @param clippingLayer The element that provides the clipping boundaries.
	 */
	public void setClippingLayer(Element clippingLayer) {
		if (clippingLayer != null) {
			this.isClipped = true;
			this.wasClipped = true;
			this.clippingLayer = clippingLayer;
			this.mat.setBoolean("UseClipping", true);
		} else {
			this.isClipped = false;
			this.wasClipped = false;
			this.clippingLayer = null;
			this.mat.setBoolean("UseClipping", false);
		}
	}
	
	public boolean getIsClipped() {
		return isClipped;
	}
	
	/**
	 * Returns the elements clipping layer or null is element doesn't use clipping
	 * @return Element clippingLayer
	 */
	public Element getClippingLayer() {
		return this.clippingLayer;
	}
	
	/**
	 * Returns a Vector4f containing the current boundaries of the element's clipping layer
	 * @return Vector4f clippingBounds
	 */
	public Vector4f getClippingBounds() {
		return this.clippingBounds;
	}
	
	/**
	 * Updates the clipping bounds for any element that has a clipping layer
	 */
	public void updateClipping() {
		updateLocalClipping();
		Set<String> keys = elementChildren.keySet();
		for (String key : keys) {
			elementChildren.get(key).updateClipping();
		}
	}
	
	private void updateLocalClipping() {
		if (isVisible) {
			if (clippingLayer != null) {
				clippingBounds.set(clippingLayer.getAbsoluteX(), clippingLayer.getAbsoluteY(), clippingLayer.getAbsoluteWidth(), clippingLayer.getAbsoluteHeight());
				mat.setVector4("Clipping", clippingBounds);
				mat.setBoolean("UseClipping", true);
			} else {
				mat.setBoolean("UseClipping", false);
			}
		} else {
			clippingBounds.set(0,0,0,0);
			mat.setVector4("Clipping", clippingBounds);
			mat.setBoolean("UseClipping", true);
		}
		setFontPages();
	}
	
	public void setTextClipPadding(float textClipPadding) {
		this.textClipPadding = textClipPadding;
	}
	
	/**
	 * Updates font materials with any changes to clipping layers
	 */
	private void setFontPages() {
		if (textElement != null) {
			if (!isVisible) {
				for (int i = 0; i < font.getPageSize(); i++) {
					this.font.getPage(i).setVector4("Clipping", clippingBounds);
					this.font.getPage(i).setBoolean("UseClipping", true);
				}
			} else {
				if (isClipped) {
					for (int i = 0; i < font.getPageSize(); i++) {
						this.font.getPage(i).setVector4("Clipping", clippingBounds.add(textClipPadding, textClipPadding, -textClipPadding, -textClipPadding));
						this.font.getPage(i).setBoolean("UseClipping", true);
					}
				} else {
					for (int i = 0; i < font.getPageSize(); i++) {
						this.font.getPage(i).setBoolean("UseClipping", false);
					}
				}
			} 
		}
	}
	
	// Effects
	public void addEffect(Effect.EffectEvent effectEvent, Effect effect) {
		if (!effects.containsKey(effectEvent)) {
			effect.setElement(this);
			effects.put(effectEvent, effect);
		}
	}
	
	public void removeEffect(Effect.EffectEvent effectEvent) {
		effects.remove(effectEvent);
	}
	
	public Effect getEffect(Effect.EffectEvent effectEvent) {
		Effect effect = null;
		if (effects.get(effectEvent) != null)
			effect = effects.get(effectEvent).clone();
		return effect;
	}
	
	protected void populateEffects(String styleName) {
		int index = 0;
		Effect effect;
		while ((effect = screen.getStyle(styleName).getEffect("event" + index)) != null) {
			effect = effect.clone();
			effect.setElement(this);
			this.addEffect(effect.getEffectEvent(), effect);
			index++;
		}
	}
}
