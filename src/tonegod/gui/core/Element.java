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
import tonegod.gui.controls.extras.OSRViewPort;
import tonegod.gui.controls.form.Form;
import tonegod.gui.effects.Effect;

/**
 *
 * @author t0neg0d
 */
public class Element extends Node {
	/**
	 *
	 */
	public static enum Borders {
		/**
		 *
		 */
		NW,
		/**
		 *
		 */
		N,
		/**
		 *
		 */
		NE,
		/**
		 *
		 */
		W,
		/**
		 *
		 */
		E,
		/**
		 *
		 */
		SW,
		/**
		 *
		 */
		S,
		/**
		 *
		 */
		SE;
	};
	/**
	 *
	 */
	public static enum Orientation {
		/**
		 *
		 */
		VERTICAL,
		/**
		 *
		 */
		HORIZONTAL
	}
	
	protected Application app;
	protected Screen screen;
	private String UID;
	private Vector2f position;
	private Vector2f orgPosition;
	private Vector2f dimensions;
	public Vector4f borders = new Vector4f(1,1,1,1);
	public Vector4f borderHandles = new Vector4f(12,12,12,12);
	private Vector2f minDimensions = null; //new Vector2f(100, 50);
	
	private boolean ignoreMouse = false;
	protected boolean isMovable = false;
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
	
	private Geometry geom;
	private ElementQuadGrid model;
	private Material mat;
	private Texture defaultTex;
	
	protected BitmapText textElement;
	protected Vector2f textPosition = new Vector2f(0,0);
	protected LineWrapMode textWrap = LineWrapMode.Word;
	protected BitmapFont.Align textAlign = BitmapFont.Align.Left;
	protected BitmapFont.VAlign textVAlign = BitmapFont.VAlign.Top;
	private String text = "";
	private String toolTipText = null;
	
	protected BitmapFont font;
	protected float fontSize = 20;
	protected float textPadding = 0;
	protected ColorRGBA fontColor = ColorRGBA.White;
	private ColorRGBA defaultColor = new ColorRGBA(1,1,1,0);
	
	private Element elementParent = null;
	protected Map<String, Element> elementChildren = new HashMap();
	
	protected boolean isClipped = false;
	protected boolean wasClipped = false;
	private Element clippingLayer;
	private Vector4f clippingBounds = new Vector4f();
	private float clipPadding = 0;
	private float textClipPadding = 0;
	protected boolean isVisible = true;
	protected boolean wasVisible = true;
	private boolean hasFocus = false;
	
	private Form form;
	private int tabIndex = 0;
	
	private float zOrder;
	private Map<Effect.EffectEvent, Effect> effects = new HashMap();
	
	private OSRBridge bridge;
	
	private boolean ignoreGlobalAlpha = false;
	private boolean isModal = false;
	private boolean isGlobalModal = false;
	
	private Object elementUserData;
	
	/**
	 * The Element class is the single primitive for all controls in the gui library.
	 * Each element consists of an ElementQuadMesh for rendering resizable textures,
	 * as well as a BitmapText element if setText(String text) is called.
	 * 
	 * Behaviors, such as movement and resizing, are common to all elements and can
	 * be enabled/disabled to ensure the element reacts to user input as needed.
	 * 
	 * @param screen The Screen control the element or it's absolute parent element is being added to
	 * @param UID A unique String identifier used when looking up elements by screen.getElementByID()
	 * @param position A Vector2f containing the x/y coordinates (relative to it's parent elements x/y) for positioning
	 * @param dimensions A Vector2f containing the dimensions of the element, x being width, y being height
	 * @param resizeBorders A Vector4f containing the size of each border used for scaling images without distorting them (x = N, y = W, x = E, w = S)
	 * @param texturePath A String path to the default image to be rendered on the element's mesh
	 */
	public Element(Screen screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String texturePath) {
		this.app = screen.getApplication();
		this.screen = screen;
		this.UID = UID;
		this.position = position;
		this.dimensions = dimensions;
	//	this.minDimensions = dimensions.clone();
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
		float imgHeight = 100;
		float pixelWidth = 1f/imgWidth;
		float pixelHeight = 1f/imgHeight;
		
		if (texturePath != null) {
			defaultTex = app.getAssetManager().loadTexture(texturePath);
			defaultTex.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
			defaultTex.setMagFilter(Texture.MagFilter.Bilinear);
			defaultTex.setWrap(Texture.WrapMode.Repeat);

			imgWidth = defaultTex.getImage().getWidth();
			imgHeight = defaultTex.getImage().getHeight();
			pixelWidth = 1f/imgWidth;
			pixelHeight = 1f/imgHeight;
		}
		mat = new Material(app.getAssetManager(), "tonegod/gui/shaders/Unshaded.j3md");
		if (texturePath != null) {
			mat.setTexture("ColorMap", defaultTex);
			mat.setColor("Color", ColorRGBA.White);
		} else {
			mat.setColor("Color", defaultColor);
		}
		mat.setFloat("GlobalAlpha", screen.getGlobalAlpha());
		
		mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
		mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Back);
		
		this.model = new ElementQuadGrid(this.dimensions, borders, imgWidth, imgHeight, pixelWidth, pixelHeight);
		
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
	
	public final Vector2f getV2fPercentToPixels(Vector2f in) {
		if (getElementParent() == null) {
			if (in.x < 1) in.setX(screen.getWidth()*in.x);
			if (in.y < 1) in.setY(screen.getHeight()*in.y);
		} else {
			if (in.x < 1) in.setX(getElementParent().getWidth()*in.x);
			if (in.y < 1) in.setY(getElementParent().getHeight()*in.y);
		}
		return in;
	}
	
	/**
	 * Adds the specified Element as a child to this Element.
	 * @param child The Element to add as a child
	 */
	public void addChild(Element child) {
		child.elementParent = this;
		
		child.setY(this.getHeight()-child.getHeight()-child.getY());
		child.orgPosition = position.clone();
		child.orgPosition.setY(child.getY());
		child.setQueueBucket(RenderQueue.Bucket.Gui);
		
		elementChildren.put(child.getUID(), child);
		this.attachChild(child);
	}
	
	/**
	 * Removes the specified Element
	 * @param child Element to remove
	 */
	public void removeChild(Element child) {
		Element e = elementChildren.remove(child.getUID());
		if (e != null)
			e.removeFromParent();
	}
	
	/**
	 * Remove all child Elements from this Element
	 */
	public void removeAllChildren() {
		Set<String> keys = elementChildren.keySet();
		Element e;
		for (String key : keys) {
			e = elementChildren.get(key);
			e.removeFromParent();
		}
		elementChildren.clear();
	}
	
	public Screen getScreen() {
		return this.screen;
	}
	
	// Z-ORDER 
	/**
	 * Recursive call made by the screen control to properly initialize z-order (depth) placement
	 * @param zOrder The depth to place the Element at. (Relative to the parent's z-order)
	 */
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
	
	/**
	 * Returns the Element's zOrder
	 * @return float zOrder
	 */
	public float getZOrder() {
		return this.zOrder;
	}
	
	/**
	 * Sets the Elements zOrder (I would suggest NOT using this method)
	 * @param zOrder 
	 */
	public void setZOrder(float zOrder) {
		this.zOrder = zOrder;
		initZOrder(zOrder);
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
	
	/**
	 * A way to override the default material of the element.
	 * 
	 * NOTE: It is important that the shader used with the new material is either:
	 * A: The provided Unshaded material contained with this library, or
	 * B: The custom shader contains the caret, text range, clipping and effect 
	 *    handling provided in the default shader.
	 * 
	 * @param mat The Material to use for rendering this Element.
	 */
	public void setLocalMaterial(Material mat) {
		this.mat = mat;
		this.setMaterial(mat);
	}
	
	/**
	 * Informs the screen control that this Element should be ignored by mouse events.
	 * 
	 * @param ignoreMouse boolean
	 */
	public void setIgnoreMouse(boolean ignoreMouse) {
		this.ignoreMouse = ignoreMouse;
	}
	
	/**
	 * Returns if the element is set to ingnore mouse events
	 * 
	 * @return boolean ignoreMouse
	 */
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
	
	/**
	 * Enables north docking of element (disables south docking of Element).  This
	 * determines how the Element should retain positioning on parent resize events.
	 * 
	 * @param dockN boolean
	 */
	public void setDockN(boolean dockN) {
		this.dockS = dockN;
		this.dockN = !dockN;
	}
	
	/**
	 * Returns if the Element is docked to the north quadrant of it's parent element.
	 * @return boolean dockN
	 */
	public boolean getDockN() {
		return this.dockS;
	}
	
	/**
	 * Enables west docking of Element (disables east docking of Element).  This
	 * determines how the Element should retain positioning on parent resize events.
	 * 
	 * @param dockW boolean
	 */
	public void setDockW(boolean dockW) {
		this.dockW = dockW;
		this.dockE = !dockW;
	}
	
	/**
	 * Returns if the Element is docked to the west quadrant of it's parent element.
	 * @return boolean dockW
	 */
	public boolean getDockW() {
		return this.dockW;
	}
	
	/**
	 * Enables east docking of Element (disables west docking of Element).  This
	 * determines how the Element should retain positioning on parent resize events.
	 * 
	 * @param dockE boolean
	 */
	public void setDockE(boolean dockE) {
		this.dockE = dockE;
		this.dockW = !dockE;
	}
	
	/**
	 * Returns if the Element is docked to the east quadrant of it's parent element.
	 * @return boolean dockE
	 */
	public boolean getDockE() {
		return this.dockE;
	}
	
	/**
	 * Enables south docking of Element (disables north docking of Element).  This
	 * determines how the Element should retain positioning on parent resize events.
	 * 
	 * @param dockS boolean
	 */
	public void setDockS(boolean dockS) {
		this.dockN = dockS;
		this.dockS = !dockS;
	}
	
	/**
	 * Returns if the Element is docked to the south quadrant of it's parent element.
	 * @return boolean dockS
	 */
	public boolean getDockS() {
		return this.dockN;
	}
	
	/**
	 * Determines if the element should scale with parent when resized vertically.
	 * @param scaleNS boolean
	 */
	public void setScaleNS(boolean scaleNS) {
		this.scaleNS = scaleNS;
	}
	
	/**
	 * Returns if the Element is set to scale vertically when it's parent Element is
	 * resized.
	 * 
	 * @return boolean scaleNS
	 */
	public boolean getScaleNS() {
		return this.scaleNS;
	}
	
	/**
	 * Determines if the element should scale with parent when resized horizontally.
	 * @param scaleEW boolean
	 */
	public void setScaleEW(boolean scaleEW) {
		this.scaleEW = scaleEW;
	}
	
	/**
	 * Returns if the Element is set to scale horizontally when it's parent Element is
	 * resized.
	 * 
	 * @return boolean scaleEW
	 */
	public boolean getScaleEW() {
		return this.scaleEW;
	}
	
	/**
	 * Sets the element to pass certain events (movement, resizing) to it direct parent instead
	 * of effecting itself.
	 * 
	 * @param effectParent boolean
	 */
	public void setEffectParent(boolean effectParent) {
		this.effectParent = effectParent;
	}
	
	/**
	 * Returns if the Element is set to pass events to it's direct parent
	 * @return boolean effectParent
	 */
	public boolean getEffectParent() {
		return this.effectParent;
	}
	
	/**
	 * Sets the element to pass certain events (movement, resizing) to it absolute
	 * parent instead of effecting itself.
	 * 
	 * The Elements absolute parent is the element farthest up in it's nesting order, 
	 * or simply put, was added to the screen.
	 * 
	 * @param effectAbsoluteParent boolean
	 */
	public void setEffectAbsoluteParent(boolean effectAbsoluteParent) {
		this.effectAbsoluteParent = effectAbsoluteParent;
	}
	
	/**
	 * Returns if the Element is set to pass events to it's absolute parent
	 * @return boolean effectParent
	 */
	public boolean getEffectAbsoluteParent() {
		return this.effectAbsoluteParent;
	}
	
	/**
	 * Forces the object to stay within the constrainst of it's parent Elements
	 * dimensions.
	 * 
	 * @param lockToParentBounds boolean
	 */
	public void setlockToParentBounds(boolean lockToParentBounds) {
		this.lockToParentBounds = lockToParentBounds;
	}
	
	/**
	 * Returns if the Element has been constrained to it's parent Element's dimensions.
	 * 
	 * @return boolean lockToParentBounds
	 */
	public boolean getLockToParentBounds() {
		return this.lockToParentBounds;
	}
	
	/**
	 * Set the x,y coordinates of the Element.  X and y are relative to the parent
	 * Element.
	 * 
	 * @param position Vector2f screen poisition of Element
	 */
	public void setPosition(Vector2f position) {
		this.position = position;
		updateNodeLocation();
	}
	
	/**
	 * Set the x,y coordinates of the Element.  X and y are relative to the parent
	 * Element.
	 * 
	 * @param x The x coordinate screen poisition of Element
	 * @param y The y coordinate screen poisition of Element
	 */
	public void setPosition(float x, float y) {
		this.position.setX(x);
		this.position.setY(y);
		updateNodeLocation();
	}
	
	/**
	 * Set the x coordinates of the Element.  X is relative to the parent Element.
	 * 
	 * @param x The x coordinate screen poisition of Element
	 */
	public void setX(float x) {
		this.position.setX(x);
		updateNodeLocation();
	}
	
	/**
	 * Set the y coordinates of the Element.  Y is relative to the parent Element.
	 * 
	 * @param y The y coordinate screen poisition of Element
	 */
	public void setY(float y) {
		this.position.setY(y);
		updateNodeLocation();
	}
	
	private void updateNodeLocation() {
		this.setLocalTranslation(position.x, position.y, this.getLocalTranslation().getZ());
		updateClipping();
	}
	
	/**
	 * Returns the current screen location of the Element
	 * @return Vector2f position
	 */
	public Vector2f getPosition() {
		return position;
	}
	
	/**
	 * Gets the relative x coordinate of the Element from it's parent Element's x
	 * @return  float
	 */
	public float getX() {
		return position.x;
	}
	
	/**
	 * Gets the relative y coordinate of the Element from it's parent Element's y
	 * @return  float
	 */
	public float getY() {
		return position.y;
	}
	
	/**
	 * Returns the x coord of an element from screen x 0, ignoring the nesting order.
	 * @return  float x
	 */
	public float getAbsoluteX() {
		float x = getX();
		Element el = this;
		while (el.getElementParent() != null) {
			el = el.getElementParent();
			x += el.getX();
		}
		return x;
	}
	
	/**
	 * Returns the y coord of an element from screen y 0, ignoring the nesting order.
	 * @return float
	 */
	public float getAbsoluteY() {
		float y = getY();
		Element el = this;
		while (el.getElementParent() != null) {
			el = el.getElementParent();
			y += el.getY();
		}
		return y;
	}
	
	/**
	 * Sets the width and height of the element
	 * @param w float
	 * @param h float
	 */
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
	
	/**
	 * Sets the width and height of the element
	 * @param dimensions Vector2f
	 */
	public void setDimensions(Vector2f dimensions) {
		this.dimensions = dimensions;
		getModel().updateDimensions(dimensions.x, dimensions.y);
		geom.updateModelBound();
		if (textElement != null) {
			updateTextElement();
		}
		updateClipping();
	}
	
	public void setMinDimensions(Vector2f minDimensions) {
		this.minDimensions = minDimensions;
	}
	
	/**
	 * Sets the width of the element
	 * @param width float
	 */
	public void setWidth(float width) {
		this.dimensions.setX(width);
		getModel().updateWidth(dimensions.x);
		geom.updateModelBound();
		if (textElement != null) {
			updateTextElement();
		}
		updateClipping();
	}
	
	/**
	 * Sets the height of the element
	 * @param height float
	 */
	public void setHeight(float height) {
		this.dimensions.setY(height);
		getModel().updateHeight(dimensions.y);
		geom.updateModelBound();
		if (textElement != null) {
			updateTextElement();
		}
		updateClipping();
	}
	
	/**
	 * Returns a Vector2f containing the actual width and height of an Element
	 * @return float
	 */
	public Vector2f getDimensions() {
		return dimensions;
	}
	
	/**
	 * Returns the actual width of an Element
	 * @return float
	 */
	public float getWidth() {
		return dimensions.x;
	}
	
	/**
	 * Returns the actual height of an Element
	 * @return float
	 */
	public float getHeight() {
		return dimensions.y;
	}
	
	/**
	 * Returns the width of an Element from screen x 0
	 * @return float
	 */
	public float getAbsoluteWidth() {
		return getAbsoluteX() + getWidth();
	}
	
	/**
	 * Returns the height of an Element from screen y 0
	 * @return float
	 */
	public float getAbsoluteHeight() {
		return getAbsoluteY() + getHeight();
	}
	
	public void validateLayout() {
		if (getDimensions().x < 1 || getDimensions().y < 1) {
			Vector2f dim = getV2fPercentToPixels(getDimensions());
			resize(getAbsoluteX()+dim.x, getAbsoluteY()+dim.y, Element.Borders.SE);
		}
		if (getPosition().x < 1 || getPosition().y < 1) {
			Vector2f pos = getV2fPercentToPixels(getPosition());
			setPosition(pos.x,pos.y);
		}
		if (getElementParent() != null)
			setY(getElementParent().getHeight()-getHeight()-getY());
		else
			setY(screen.getHeight()-getHeight()-getY());
		
		Set<String> keys = elementChildren.keySet();
		for (String key : keys) {
			elementChildren.get(key).validateLayout();
		}
	}
	
	/**
	 * The preferred method for resizing Elements if the resize must effect nested
	 * Elements as well.
	 * 
	 * @param x the absolute x coordinate from screen x 0
	 * @param y the absolute y coordinate from screen y 0
	 * @param dir The Element.Borders used to determine the direction of the resize event
	 */
	public void resize(float x, float y, Borders dir) {
		float prevWidth = getWidth();
		float prevHeight = getHeight();
		float oX = x, oY = y;
		if (getElementParent() != null) { x -= getAbsoluteX()-getX(); }
		if (getElementParent() != null) { y -= getAbsoluteY()-getY(); }
		float nextX, nextY;
		if (dir == Borders.NW) {
			if (getLockToParentBounds()) { if (x <= 0) { x = 0; } }
			if (minDimensions != null) {
				if (getX()+getWidth()-x <= minDimensions.x) { x = getX()+getWidth()-minDimensions.x; }
			}
			if (resizeW) {
				setWidth(getX()+getWidth()-x);
				setX(x);
			}
			if (getLockToParentBounds()) { if (y <= 0) { y = 0; } }
			if (minDimensions != null) {
				if (getY()+getHeight()-y <= minDimensions.y) { y = getY()+getHeight()-minDimensions.y; }
			}
			if (resizeN) {
				setHeight(getY()+getHeight()-y);
				setY(y);
			}
		} else if (dir == Borders.N) {
			if (getLockToParentBounds()) { if (y <= 0) { y = 0; } }
			if (minDimensions != null) {
				if (getY()+getHeight()-y <= minDimensions.y) { y = getY()+getHeight()-minDimensions.y; }
			}
			if (resizeN) {
				setHeight(getY()+getHeight()-y);
				setY(y);
			}
		} else if (dir == Borders.NE) {
			nextX = oX-getAbsoluteX();
			if (getLockToParentBounds()) { if (nextX >= getElementParent().getWidth()-getX()) { nextX = getElementParent().getWidth()-getX(); } }
			if (minDimensions != null) {
				if (nextX <= minDimensions.x) { nextX = minDimensions.x; }
			}
			if (resizeE) {
				setWidth(nextX);
			}
			if (getLockToParentBounds()) { if (y <= 0) { y = 0; } }
			if (minDimensions != null) {
				if (getY()+getHeight()-y <= minDimensions.y) { y = getY()+getHeight()-minDimensions.y; }
			}
			if (resizeN) {
				setHeight(getY()+getHeight()-y);
				setY(y);
			}
		} else if (dir == Borders.W) {
			if (getLockToParentBounds()) { if (x <= 0) { x = 0; } }
			if (minDimensions != null) {
				if (getX()+getWidth()-x <= minDimensions.x) { x = getX()+getWidth()-minDimensions.x; }
			}
			if (resizeW) {
				setWidth(getX()+getWidth()-x);
				setX(x);
			}
		} else if (dir == Borders.E) {
			nextX = oX-getAbsoluteX();
			if (getLockToParentBounds()) { if (nextX >= getElementParent().getWidth()-getX()) { nextX = getElementParent().getWidth()-getX(); } }
			if (minDimensions != null) {
				if (nextX <= minDimensions.x) { nextX = minDimensions.x; }
			}
			if (resizeE) {
				setWidth(nextX);
			}
		} else if (dir == Borders.SW) {
			if (getLockToParentBounds()) { if (x <= 0) { x = 0; } }
			if (minDimensions != null) {
				if (getX()+getWidth()-x <= minDimensions.x) { x = getX()+getWidth()-minDimensions.x; }
			}
			if (resizeW) {
				setWidth(getX()+getWidth()-x);
				setX(x);
			}
			nextY = oY-getAbsoluteY();
			if (getLockToParentBounds()) { if (nextY >= getElementParent().getHeight()-getY()) { nextY = getElementParent().getHeight()-getY(); } }
			if (minDimensions != null) {
				if (nextY <= minDimensions.y) { nextY = minDimensions.y; }
			}
			if (resizeS) {
				setHeight(nextY);
			}
		} else if (dir == Borders.S) {
			nextY = oY-getAbsoluteY();
			if (getLockToParentBounds()) { if (nextY >= getElementParent().getHeight()-getY()) { nextY = getElementParent().getHeight()-getY(); } }
			if (minDimensions != null) {
				if (nextY <= minDimensions.y) { nextY = minDimensions.y; }
			}
			if (resizeS) {
				setHeight(nextY);
			}
		} else if (dir == Borders.SE) {
			nextX = oX-getAbsoluteX();
			if (getLockToParentBounds()) { if (nextX >= getElementParent().getWidth()-getX()) { nextX = getElementParent().getWidth()-getX(); } }
			if (minDimensions != null) {
				if (nextX <= minDimensions.x) { nextX = minDimensions.x; }
			}
			if (resizeE) {
				setWidth(nextX);
			}
			nextY = oY-getAbsoluteY();
			if (getLockToParentBounds()) { if (nextY >= getElementParent().getHeight()-getY()) { nextY = getElementParent().getHeight()-getY(); } }
			if (minDimensions != null) {
				if (nextY <= minDimensions.y) { nextY = minDimensions.y; }
			}
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
	/*
	private void childResize(float diffX, float diffY, Borders dir) {
		if (dir == Borders.N) {
		//	if (dockS && scaleNS) {
		//		if (minDimensions == null) {
		//			setHeight(getHeight()-diffY);
		//		} else if (getHeight()-diffY > minDimensions.y) {
		//			setHeight(getHeight()-diffY);
		//		} else {
		//			setHeight(minDimensions.y);
		//		}
		//	}
		} else if (dir == Borders.S) {
			if (dockS && scaleNS) {
				if (minDimensions == null) {
					setHeight(getHeight()-diffY);
				} else {
					float cY = getElementParent().getHeight()-(getElementParent().getHeight()-orgPosition.y);
					if (getY() < cY) setHeight(minDimensions.y);
					else setHeight(getHeight()-diffY);
					if (getHeight() > minDimensions.y) setY(cY);
					else setY(getY()-diffY);
				}
			} else
				setY(getY()-diffY);
		}
		if (dir == Borders.W) {
			if (dockE) {
				
			}
		} else if (dir == Borders.E) {
			if (dockW) {
				
			}
		}
		
		Set<String> keys = elementChildren.keySet();
		for (String key : keys) {
			elementChildren.get(key).childResize(diffX,diffY,dir);
		}
	}
	*/
	
	private void childResize(float diffX, float diffY, Borders dir) {
		if (dir == Borders.NW || dir == Borders.N || dir == Borders.NE) {
			if (getScaleNS()) setHeight(getHeight()-diffY);
			if (getDockN() && !getScaleNS()) setY(getY()-diffY);
		} else if (dir == Borders.SW || dir == Borders.S || dir == Borders.SE) {
			if (getScaleNS()) setHeight(getHeight()-diffY);
			if (getDockN() && !getScaleNS()) setY(getY()-diffY);
		}
		if (dir == Borders.NW || dir == Borders.W || dir == Borders.SW) {
			if (getScaleEW()) setWidth(getWidth()-diffX);
			if (getDockE() && !getScaleEW()) setX(getX()-diffX);
		} else if (dir == Borders.NE || dir == Borders.E || dir == Borders.SE) {
			if (getScaleEW()) setWidth(getWidth()-diffX);
			if (getDockE() && !getScaleEW()) setX(getX()-diffX);
		}
		Set<String> keys = elementChildren.keySet();
		for (String key : keys) {
			elementChildren.get(key).childResize(diffX,diffY,dir);
			elementChildren.get(key).controlResizeHook();
		}
	}
	
	/**
	 * Overridable method for extending the resize event
	 */
	public void controlResizeHook() {
		
	}
	
	/**
	 * Moves the Element to the specified coordinates
	 * @param x The new x screen coordinate of the Element
	 * @param y The new y screen coordinate of the Element
	 */
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
	
	/**
	 * Overridable method for extending the move event
	 */
	public void controlMoveHook() {
		
	}
	
	/**
	 * Set the north, west, east and south borders in number of pixels
	 * @param borderSize 
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
	
	/**
	 * Returns the height of the north resize border
	 * 
	 * @return float
	 */
	public float getResizeBorderNorthSize() {
		return this.borderHandles.x;
	}
	
	/**
	 * Returns the width of the west resize border
	 * 
	 * @return float
	 */
	public float getResizeBorderWestSize() {
		return this.borderHandles.y;
	}
	
	/**
	 * Returns the width of the east resize border
	 * 
	 * @return float
	 */
	public float getResizeBorderEastSize() {
		return this.borderHandles.z;
	}
	
	/**
	 * Returns the height of the south resize border
	 * 
	 * @return float
	 */
	public float getResizeBorderSouthSize() {
		return this.borderHandles.w;
	}
	
	/**
	 * Returns the default material for the element
	 * @param mat 
	 */
	public void setElementMaterial(Material mat) {
		this.mat = mat;
	}
	
	/**
	 * Returns a pointer to the Material used for rendering this Element.
	 * 
	 * @return Material mat
	 */
	public Material getElementMaterial() {
		return this.mat;
	}
	
	/**
	 * Returns the default Texture for the Element
	 * 
	 * @return Texture defaultTexture
	 */
	public Texture getElementTexture() {
		return this.defaultTex;
	}
	
	/**
	 * Returns a pointer to the custom mesh used to render the Element.
	 * 
	 * @return ElementGridQuad model
	 */
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
		if (textElement != null) {
			String text = this.getText();
			textElement.removeFromParent();
			textElement = null;
			setText(text);
		}
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
		if (textElement != null) {
			textElement.setSize(fontSize);
		}
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
	 * 
	 * @param textAlign 
	 */
	public void setTextAlign(BitmapFont.Align textAlign) {
		this.textAlign = textAlign;
		if (textElement != null) {
			textElement.setAlignment(textAlign);
		}
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
	 * 
	 * @param textVAlign 
	 */
	public void setTextVAlign(BitmapFont.VAlign textVAlign) {
		this.textVAlign = textVAlign;
		if (textElement != null) {
			textElement.setVerticalAlignment(textVAlign);
		}
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
		if (textElement != null) {
			textElement.setLineWrapMode(textWrap);
		}
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
			textElement.setBox(new Rectangle(0,0,dimensions.x-(textPadding*2),dimensions.y-(textPadding*2)));
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
	
	/**
	 * Returns a pointer to the BitmapText element of this Element.  Returns null
	 * if setText() has not been called.
	 * 
	 * @return BitmapText textElement
	 */
	public BitmapText getTextElement() {
		return this.textElement;
	}
	
	// Clipping
	/**
	 * Adds an alpha map to the Elements material
	 * @param alphaMap A String path to the alpha map
	 */
	public void setAlphaMap(String alphaMap) {
		Texture alpha = app.getAssetManager().loadTexture(alphaMap);
		alpha.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
		alpha.setMagFilter(Texture.MagFilter.Bilinear);
		alpha.setWrap(Texture.WrapMode.Repeat);
		
		mat.setTexture("AlphaMap", alpha);
	}
	
	/**
	 * This may be remove soon and probably should not be used as the method of handling
	 * hide show was updated making this unnecissary.
	 * 
	 * @param wasVisible boolean
	 */
	public void setDefaultWasVisible(boolean wasVisible) {
		this.wasVisible = wasVisible;
	}
	
	/**
	 * Sets this Element and any Element contained within it's nesting order to visible.
	 * 
	 * NOTE: Hide and Show relies on shader-based clipping
	 */
	public void show() {
		if (!isVisible) {
			screen.updateZOrder(getAbsoluteParent());
			this.isVisible = true;
			this.isClipped = wasClipped;
			updateClipping();
			controlShowHook();
			
			if (getParent() == null) {
				if (getElementParent() != null) {
						getElementParent().attachChild(this);
				} else {
					screen.getGUINode().attachChild(this);
				}
			}
			
			Set<String> keys = elementChildren.keySet();
			for (String key : keys) {
				elementChildren.get(key).childShow();
			}
		}
	}
	
	/**
	 * Recursive call for properly showing children of the Element.  I'm thinking this
	 * this needs to be a private method, however I need to verify this before I
	 * update it.
	 */
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
	
	/**
	 * An overridable method for extending the show event.
	 */
	public void controlShowHook() {  }
	
	/**
	 * Recursive call that sets this Element and any Element contained within it's 
	 * nesting order to hidden.
	 * 
	 * NOTE: Hide and Show relies on shader-based clipping
	 */
	public void hide() {
		if (isVisible) {
			this.wasVisible = isVisible;
			this.isVisible = false;
			this.isClipped = true;
		}
		updateClipping();
		controlHideHook();
		if (!(this instanceof OSRViewPort))
			removeFromParent();
		Set<String> keys = elementChildren.keySet();
		for (String key : keys) {
			elementChildren.get(key).childHide();
		}
	}
	
	public void childHide() {
		if (isVisible) {
			this.wasVisible = isVisible;
			this.isVisible = false;
			this.isClipped = true;
		}
		updateClipping();
		controlHideHook();
		Set<String> keys = elementChildren.keySet();
		for (String key : keys) {
			elementChildren.get(key).childHide();
		}
	}
	
	/**
	 * An overridable method for extending the hide event.
	 */
	public void controlHideHook() {  }
	
	public void propagateEffect(Effect effect, boolean callHide) {
		Effect nEffect = effect.clone();
		nEffect.setCallHide(callHide);
		nEffect.setElement(this);
		screen.getEffectManager().applyEffect(nEffect);
		Set<String> keys = elementChildren.keySet();
		for (String key : keys) {
			elementChildren.get(key).propagateEffect(effect, false);
		}
	}
	
	/**
	 * Return if the Element is visible
	 * 
	 * @return boolean isVisible
	 */
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
	
	/**
	 * Recursive update of all child Elements clipping layer
	 * @param clippingLayer The clipping layer to apply
	 */
	public void setControlClippingLayer(Element clippingLayer) {
		setClippingLayer(clippingLayer);
		Set<String> keys = elementChildren.keySet();
		for (String key : keys) {
			elementChildren.get(key).setControlClippingLayer(clippingLayer);
		}
	}
	
	/**
	 * Returns if the Element's clipping layer has been set
	 * 
	 * @return boolean isClipped
	 */
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
	 * Adds a padding to the clippinglayer, in effect this contracts the size of the clipping
	 * bounds by the specified number of pixels
	 * @param clipPadding The number of pixels to pad the clipping area
	 */
	public void setClipPadding(float clipPadding) {
		this.clipPadding = clipPadding;
	}
	
	/**
	 * Returns the current clipPadding
	 * @return float clipPadding
	 */
	public float getClipPadding() {
		return clipPadding;
	}
	
	/**
	 * Updates the clipping bounds for any element that has a clipping layer
	 * 
	 * See updateLocalClipping
	 */
	public void updateClipping() {
		updateLocalClipping();
		Set<String> keys = elementChildren.keySet();
		for (String key : keys) {
			elementChildren.get(key).updateClipping();
		}
	}
	
	/**
	 * Updates the clipping bounds for any element that has a clipping layer
	 */
	public void updateLocalClipping() {
		if (isVisible) {
			if (clippingLayer != null) {
				float cPadding = 0;
				if (clippingLayer != this)
					cPadding = clippingLayer.getClipPadding();
				clippingBounds.set(
					clippingLayer.getAbsoluteX()+cPadding,
					clippingLayer.getAbsoluteY()+cPadding,
					clippingLayer.getAbsoluteWidth()-cPadding,
					clippingLayer.getAbsoluteHeight()-cPadding
				);
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
	
	/**
	 * Shrinks the clipping area by set number of pixels
	 * 
	 * @param textClipPadding The number of pixels to pad the clipping area with on each side
	 */
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
	/**
	 * Associates an Effect with this Element.  Effects are not automatically associated
	 * with the specified event, but instead, the event type is used to retrieve the Effect
	 * at a later point
	 * 
	 * @param effectEvent The Effect.EffectEvent the Effect is to be registered with
	 * @param effect The Effect to store
	 */
	public void addEffect(Effect.EffectEvent effectEvent, Effect effect) {
		addEffect(effect);
	}
	
	/**
	 * Associates an Effect with this Element.  Effects are not automatically associated
	 * with the specified event, but instead, the event type is used to retrieve the Effect
	 * at a later point
	 * 
	 * @param effect The Effect to store
	 */
	public void addEffect(Effect effect) {
		effects.remove(effect.getEffectEvent());
		if (!effects.containsKey(effect.getEffectEvent())) {
			effect.setElement(this);
			effects.put(effect.getEffectEvent(), effect);
		}
	}
	
	/**
	 * Removes the Effect associated with the Effect.EffectEvent specified
	 * @param effectEvent 
	 */
	public void removeEffect(Effect.EffectEvent effectEvent) {
		effects.remove(effectEvent);
	}
	
	/**
	 * Retrieves the Effect associated with the specified Effect.EffectEvent
	 * 
	 * @param effectEvent
	 * @return 
	 */
	public Effect getEffect(Effect.EffectEvent effectEvent) {
		Effect effect = null;
		if (effects.get(effectEvent) != null)
			effect = effects.get(effectEvent).clone();
		return effect;
	}
	
	/**
	 * Called by controls during construction to prepopulate effects based on Styles.
	 * 
	 * @param styleName The String identifier of the Style
	 */
	protected void populateEffects(String styleName) {
		int index = 0;
		Effect effect;
		while ((effect = screen.getStyle(styleName).getEffect("event" + index)) != null) {
			effect = effect.clone();
			effect.setElement(this);
			this.addEffect(effect);
			index++;
		}
	}
	
	public void setGlobalAlpha(float globalAlpha) {
		if (!ignoreGlobalAlpha) {
			getElementMaterial().setFloat("GlobalAlpha", globalAlpha);
			Set<String> keys = elementChildren.keySet();
			for (String key : keys) {
				elementChildren.get(key).setGlobalAlpha(globalAlpha);
			}
		} else {
			getElementMaterial().setFloat("GlobalAlpha", 1);
		}
	}
	
	public void setIgnoreGlobalAlpha(boolean ignoreGlobalAlpha) {
		this.ignoreGlobalAlpha = ignoreGlobalAlpha;
	}
	
	// Tab focus
	/**
	 * For use by the Form control (Do not call this method directly)
	 * @param form The form the Element has been added to
	 */
	public void setForm(Form form) {
		this.form = form;
	}
	
	/**
	 * Returns the form the Element is controlled by
	 * @return Form form
	 */
	public Form getForm() {
		return this.form;
	}
	
	/**
	 * Sets the tab index (This is assigned by the Form control. Do not call this method directly)
	 * @param tabIndex The tab index assigned to the Element
	 */
	public void setTabIndex(int tabIndex) {
		this.tabIndex = tabIndex;
	}
	
	/**
	 * Returns the tab index assigned by the Form control
	 * @return 
	 */
	public int getTabIndex() {
		return tabIndex;
	}
	
	// Off Screen Rendering Bridge
	public void addOSRBridge(OSRBridge bridge) {
		this.bridge = bridge;
		addControl(bridge);
		getElementMaterial().setTexture("ColorMap", bridge.getTexture());
		getElementMaterial().setColor("Color", ColorRGBA.White);
	}
	
	// Tool Tip
	public void setToolTipText(String toolTip) {
		this.toolTipText = toolTip;
	}
	
	public String getToolTipText() {
		return toolTipText;
	}
	
	public void setHasFocus(boolean hasFocus) {
		this.hasFocus = hasFocus;
	}
	
	public boolean getHasFocus() {
		return this.hasFocus;
	}
	
	// Modal
	public void setIsModal(boolean isModal) {
		this.isModal = isModal;
	}
	
	public boolean getIsModal() {
		return this.isModal;
	}
	
	public void setIsGlobalModal(boolean isGlobalModal) {
		this.isGlobalModal = isGlobalModal;
	}
	
	public boolean getIsGlobalModal() {
		return this.isGlobalModal;
	}
	
	// User data
	public void setElementUserData(Object elementUserData) {
		this.elementUserData = elementUserData;
	}
	
	public Object getElementUserData() {
		return this.elementUserData;
	}
}
