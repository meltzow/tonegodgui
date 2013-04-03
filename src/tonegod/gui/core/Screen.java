package tonegod.gui.core;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppState;
import com.jme3.audio.AudioNode;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.cursors.plugins.JmeCursor;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.shapes.EmitterShape;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.font.BitmapText;
import com.jme3.font.LineWrapMode;
import com.jme3.font.Rectangle;
import com.jme3.font.plugins.BitmapFontLoader;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.lwjgl.input.Mouse;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import tonegod.gui.controls.extras.OSRViewPort;
import tonegod.gui.controls.form.Form;
import tonegod.gui.controls.lists.ComboBox;
import tonegod.gui.controls.menuing.Menu;
import tonegod.gui.controls.text.TextField;
import tonegod.gui.controls.util.ToolTip;
import tonegod.gui.core.Element.Borders;
import tonegod.gui.core.utils.BitmapTextUtil;
import tonegod.gui.core.utils.XMLHelper;
import tonegod.gui.effects.Effect;
import tonegod.gui.effects.EffectManager;
import tonegod.gui.fonts.BitmapFontLoaderX;
import tonegod.gui.listeners.*;

/**
 *
 * @author t0neg0d
 */
public class Screen implements Control, RawInputListener, ClipboardOwner {

	public static enum CursorType {
		POINTER,
		HAND,
		MOVE,
		TEXT,
		WAIT,
		RESIZE_CNW,
		RESIZE_CNE,
		RESIZE_NS,
		RESIZE_EW,
		CUSTOM_0,
		CUSTOM_1,
		CUSTOM_2,
		CUSTOM_3,
		CUSTOM_4,
		CUSTOM_5,
		CUSTOM_6,
		CUSTOM_7,
		CUSTOM_8,
		CUSTOM_9
	}
	private Application app;
	protected Spatial spatial;
	private Map<String, Element> elements = new HashMap();
	private Ray elementZOrderRay = new Ray();
	private Vector3f guiRayOrigin = new Vector3f();
	
	private Element eventElement = null;
	private Element targetElement = null;
	private Element keyboardElement = null;
	private Element tabFocusElement = null;
	private Form focusForm = null;
	private Vector2f eventElementOriginXY = new Vector2f();
	private float eventElementOffsetX = 0;
	private float eventElementOffsetY = 0;
	private float targetElementOffsetX = 0;
	private float targetElementOffsetY = 0;
	private Borders eventElementResizeDirection = null;
	private Element mouseFocusElement = null;
	private Element previousMouseFocusElement = null;
	private boolean focusElementIsMovable = false;
	private boolean mousePressed = false;
	private boolean mouseLeftPressed = false;
	private boolean mouseRightPressed = false;
	private boolean mouseWheelPressed = false;
	
	private float zOrderCurrent = .5f;
	private float zOrderStepMajor = .01f;
	private float zOrderStepMinor = 0.0001f;
	
	private String clipboardText = "";
	
	private String styleMap;
	private Map<String, Style> styles = new HashMap();
	
	protected EffectManager effectManager;
	protected Node t0neg0dGUI = new Node("t0neg0dGUI");
	
	private Vector2f mouseXY = new Vector2f(0,0);
	private boolean SHIFT = false;
	private boolean CTRL = false;
	private boolean ALT = false;
	
	private boolean useCustomCursors = false;
	private boolean forceCursor = false;
	private Map<CursorType, JmeCursor> cursors = new HashMap();
	
	private boolean useToolTips = false;
	private ToolTip toolTip = null;
	private float toolTipMaxWidth = 250;
	
	private float globalAlpha = 1.0f;
	
	private Map<String, AudioNode> audioNodes = new HashMap();
	private boolean useUIAudio = false;
	private float uiAudioVolume;
	
	private ParticleEmitter cursorEmitter;
	private OSRViewPort cursorEmitterVP = null;
	private Node cursorEmitterNode = new Node("cursorEmitterNode");
	private Node cursorEmitterPlaneNode = new Node("cursorEmitterPlaneNode");
	private boolean useCursorEffects = false;
	
	private Clipboard clipboard;
	private boolean clipboardActive = false;
	
	private boolean useTextureAtlas = false;
	private Texture atlasTexture;
	
	private LayoutParser layoutParser;
	
	/**
	 * Creates a new instance of the Screen control using the default style information
	 * provided with the library.
	 * 
	 * @param app A JME Application
	 */
	public Screen(Application app) {
		this(app, "tonegod/gui/style/def/style_map.xml");
	}
	
	/**
	 * Creates an instance of the Screen control.
	 * 
	 * @param app A JME Application
	 * @param styleMap A path to the style_map.xml file containing the custom theme information
	 */
	public Screen(Application app, String styleMap) {
		this.app = app;
		this.elementZOrderRay.setDirection(Vector3f.UNIT_Z);
		app.getAssetManager().unregisterLoader(BitmapFontLoader.class);
		app.getAssetManager().registerLoader(BitmapFontLoaderX.class, "fnt");
		
		this.styleMap = styleMap;
		parseStyles(styleMap);
		
		effectManager = new EffectManager(this);
		app.getInputManager().addRawInputListener(this);
		layoutParser = new LayoutParser(this);
	}
	
	/**
	 * Returns the JME application associated with the Screen
	 * @return Application app
	 */
	public Application getApplication() {
		return this.app;
	}
	
	/**
	 * Return the width of the current Viewport
	 * 
	 * @return float width
	 */
	public float getWidth() {
		return app.getViewPort().getCamera().getWidth();
	}
	
	/**
	 * Returns the height of the current Viewport
	 * 
	 * @return  float height
	 */
	public float getHeight() {
		return app.getViewPort().getCamera().getHeight();
	}
	
	/**
	 * Initializes the Screen control
	 */
	@Deprecated
	public void initialize() {
	//	app.getInputManager().addRawInputListener(this);
		
	//	if (getUseCustomCursors())
	//		setCursor(CursorType.POINTER);
	}
	
	@Override
	public void update(float tpf) {  }

	@Override
	public void render(RenderManager rm, ViewPort vp) {
	//	throw new UnsupportedOperationException("Not supported yet.");
	}
	
	/**
	 *  Adds an Element to the Screen and scene graph
	 * @param element The Element to add
	 */
	public void addElement(Element element) {
		if (element instanceof Menu)
			element.hide();
		
		if (getElementById(element.getUID()) != null) {
			try {
				throw new ConflictingIDException();
			} catch (ConflictingIDException ex) {
				Logger.getLogger(Element.class.getName()).log(Level.SEVERE, "The child element '" + element.getUID() + "' (" + element.getClass() + ") conflicts with a previously added child element in parent Screen.", ex);
				System.exit(0);
			}
		} else {
			elements.put(element.getUID(), element);
			element.setY(getHeight()-element.getHeight()-element.getY());
			t0neg0dGUI.attachChild(element);

			// Set initla z-order
			getNextZOrder(true);
			element.initZOrder(zOrderCurrent);
			element.resize(element.getX()+element.getWidth(), element.getY()+element.getHeight(), Borders.SE);
		}
	}
	
	/**
	 * Removes an Element from the Screen and scene graph
	 * @param element The Element to remove
	 */
	public void removeElement(Element element) {
		elements.remove(element.getUID());
		float shiftZ = element.getLocalTranslation().getZ();
		for (Element el : elements.values()) {
			if (!(el instanceof ToolTip)) {
				if (el.getLocalTranslation().getZ() > shiftZ) {
					el.move(0,0,-zOrderStepMajor);
				}
			}
		}
		zOrderCurrent -= zOrderStepMajor;
		element.removeFromParent();
		element.cleanup();
	}
	
	/**
	 * Returns the Element with the associated ID.  If not found, returns null
	 * @param UID The String ID of Element to find
	 * @return Element element
	 */
	public Element getElementById(String UID) {
		Element ret = null;
		if (elements.containsKey(UID)) {
			ret = elements.get(UID);
		} else {
			for (Element el : elements.values()) {
				ret = el.getChildElementById(UID);
				if (ret != null) {
					break;
				}
			}
		}
		return ret;
	}
	
	/**
	 * Returns the guiNode used by the Screen
	 * @return Node
	 */
	public Node getGUINode() {
		return t0neg0dGUI;
	}
	
	public void setUseTextureAtlas(boolean useTextureAtlas, String texturePath) {
		this.useTextureAtlas = useTextureAtlas;
		
		if (texturePath != null) {
			atlasTexture = app.getAssetManager().loadTexture(texturePath);
			atlasTexture.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
			atlasTexture.setMagFilter(Texture.MagFilter.Bilinear);
			atlasTexture.setWrap(Texture.WrapMode.Repeat);
		} else {
			atlasTexture = null;
		}
	}
	
	public boolean getUseTextureAtlas() { return this.useTextureAtlas; }
	
	public Texture getAtlasTexture() { return atlasTexture; }
	
	public float[] parseAtlasCoords(String texturePath) {
		float[] coords = new float[4];
		
		if (texturePath != null) {
			StringTokenizer st = new StringTokenizer(texturePath, "|");
			if (st.countTokens() == 4) {
				try {
					String token = st.nextToken();
					coords[0] = Float.parseFloat(token.substring(token.indexOf('=')+1));
					token = st.nextToken();
					coords[1] = Float.parseFloat(token.substring(token.indexOf('=')+1));
					token = st.nextToken();
					coords[2] = Float.parseFloat(token.substring(token.indexOf('=')+1));
					token = st.nextToken();
					coords[3] = Float.parseFloat(token.substring(token.indexOf('=')+1));
				} catch (Exception ex) { throwParserException(); }
			} else throwParserException();
		}
		return coords;
	}
	
	private void throwParserException() {
		try {
			throw new java.text.ParseException("The provided texture information does not conform to the expected standard of ?x=(int)&y=(int)&w=(int)&h=(int)", 0);
		} catch (ParseException ex) {
			Logger.getLogger(Screen.class.getName()).log(Level.SEVERE, "The provided texture information does not conform to the expected standard of ?x=(int)&y=(int)&w=(int)&h=(int)", ex);
		}
	}
	
	// Z-ORDER
	/**
	 * Returns the next available z-order
	 * @param stepMajor Return the z-order incremented by a major step if true, a minor step if false
	 * @return float zOrder
	 */
	public float getNextZOrder(boolean stepMajor) {
		if (stepMajor)
			zOrderCurrent += zOrderStepMajor;
		else
			zOrderCurrent += zOrderStepMinor;
		return zOrderCurrent;
	}
	
	/**
	 * Brings the element specified to the front of the zOrder list shifting other below to keep all
	 * Elements within the current z-order range.
	 * 
	 * @param topMost The Element to bring to the front
	 */
	public void updateZOrder(Element topMost) {
	//	zOrderCurrent = zOrderInit;
		String topMostUID = topMost.getUID();
		float shiftZ = topMost.getLocalTranslation().getZ();
		
		for (Element el : elements.values()) {
			if (topMost.getIsGlobalModal()) {
				
			} else if (topMost.getIsModal()) {
			
			} else {
				if (!el.getIsGlobalModal() && !el.getIsModal()) {
					if (el.getLocalTranslation().getZ() > shiftZ) {
						el.move(0,0,-zOrderStepMajor);
					}
				}
			}
		}
		topMost.setLocalTranslation(topMost.getLocalTranslation().setZ(Float.valueOf(zOrderCurrent)));
	}
	
	/**
	 * Returns the zOrder major step value
	 * @return float
	 */
	public float getZOrderStepMajor() {
		return this.zOrderStepMajor;
	}
	
	/**
	 * Returns the zOrder minor step value
	 * @return float
	 */
	public float getZOrderStepMinor() {
		return this.zOrderStepMinor;
	}
	
	/**
	 * Stored the current mouse position as a Vector2f
	 * @param x The mouse's current X coord
	 * @param y The mouse's current Y coord
	 */
	private void setMouseXY(float x, float y) {
		mouseXY.set(x, y);
	}
	
	/**
	 * Returns a Vector2f containing the last stored mouse X/Y coords
	 * @return Vector2f mouseXY
	 */
	public Vector2f getMouseXY() {
		return this.mouseXY;
	}
	
	// Raw Input handlers
	@Override
	public void beginInput() {
	//	throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void endInput() {
	//	throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void onJoyAxisEvent(JoyAxisEvent evt) {
	//	throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void onJoyButtonEvent(JoyButtonEvent evt) {
	//	throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void onMouseMotionEvent(MouseMotionEvent evt) {
		setMouseXY(evt.getX(),evt.getY());
		if (this.useCursorEffects) {
			if (!app.getInputManager().isCursorVisible()) {
			//	cursorEmitter.emitAllParticles();
				cursorEmitter.setParticlesPerSec(0);
			} else {
				if (cursorEmitter.getParticlesPerSec() == 0)
					this.configEmitterDefault();
				updateCursorEmitter();
			}
		}
		if (useToolTips) updateToolTipLocation();
		if (!mousePressed) {
			mouseFocusElement = getEventElement(evt.getX(), evt.getY());
			if (mouseFocusElement != previousMouseFocusElement) {
				if (previousMouseFocusElement instanceof MouseFocusListener) {
					((MouseFocusListener)previousMouseFocusElement).onLoseFocus(evt);
				}
				if (mouseFocusElement instanceof MouseFocusListener) {
					((MouseFocusListener)mouseFocusElement).onGetFocus(evt);
				}
				previousMouseFocusElement = mouseFocusElement;
			}
			if (mouseFocusElement != null) {
				focusElementIsMovable = mouseFocusElement.getIsMovable();

				if (mouseFocusElement instanceof MouseWheelListener) {
					if (evt.getDeltaWheel() > 0) {
						((MouseWheelListener)mouseFocusElement).onMouseWheelDown(evt);
					} else if (evt.getDeltaWheel() < 0) {
						((MouseWheelListener)mouseFocusElement).onMouseWheelUp(evt);
					}
				}
			}
			if (mouseFocusElement instanceof MouseMovementListener) {
				((MouseMovementListener)mouseFocusElement).onMouseMove(evt);
			}
		} else {
			if (eventElement != null) {
				if (mouseLeftPressed) {
					if (eventElementResizeDirection != null) {
						eventElement.resize(evt.getX(), evt.getY(), eventElementResizeDirection);
					} else if (focusElementIsMovable) {
						eventElement.moveTo(evt.getX()-eventElementOffsetX, evt.getY()-eventElementOffsetY);
					}
				}

				if (eventElement instanceof MouseMovementListener) {
					((MouseMovementListener)eventElement).onMouseMove(evt);
				}
			}
		}
	}

	@Override
	public void onMouseButtonEvent(MouseButtonEvent evt) {
		if (evt.isPressed()) {
			mousePressed = true;
			resetTabFocusElement();
			if (this.useCursorEffects) {
				this.configEmiterClick(evt.getButtonIndex());
			}
			switch (evt.getButtonIndex()) {
				case 0:
					mouseLeftPressed = true;
					eventElement = getEventElement(evt.getX(), evt.getY());
					if (eventElement != null) {
						updateZOrder(eventElement.getAbsoluteParent());
						this.setTabFocusElement(eventElement);
						if (eventElement.getIsDragDropDragElement())
							targetElement = null;
						if (eventElement.getIsResizable()) {
							float offsetX = evt.getX();
							float offsetY = evt.getY();
							Element el = eventElement;
							if (offsetX > el.getAbsoluteX() && offsetX < el.getAbsoluteX()+el.getResizeBorderWestSize()) {
								if (offsetY > el.getAbsoluteY() && offsetY < el.getAbsoluteY()+el.getResizeBorderNorthSize()) {
									eventElementResizeDirection = Borders.NW;
								} else if (offsetY > (el.getAbsoluteHeight()-el.getResizeBorderSouthSize()) && offsetY < el.getAbsoluteHeight()) {
									eventElementResizeDirection = Borders.SW;
								} else {
									eventElementResizeDirection = Borders.W;
								}
							} else if (offsetX > (el.getAbsoluteWidth()-el.getResizeBorderEastSize()) && offsetX < el.getAbsoluteWidth()) {
								if (offsetY > el.getAbsoluteY() && offsetY < el.getAbsoluteY()+el.getResizeBorderNorthSize()) {
									eventElementResizeDirection = Borders.NE;
								} else if (offsetY > (el.getAbsoluteHeight()-el.getResizeBorderSouthSize()) && offsetY < el.getAbsoluteHeight()) {
									eventElementResizeDirection = Borders.SE;
								} else {
									eventElementResizeDirection = Borders.E;
								}
							} else {
								if (offsetY > el.getAbsoluteY() && offsetY < el.getAbsoluteY()+el.getResizeBorderNorthSize()) {
									eventElementResizeDirection = Borders.N;
								} else if (offsetY > (el.getAbsoluteHeight()-el.getResizeBorderSouthSize()) && offsetY < el.getAbsoluteHeight()) {
									eventElementResizeDirection = Borders.S;
								}
							}
							if (keyboardElement != null) {
								if (keyboardElement instanceof TextField) ((TextField)keyboardElement).resetTabFocus();
							}
							keyboardElement = null;
						} else if (eventElement.getIsMovable() && eventElementResizeDirection == null) {
							eventElementResizeDirection = null;
							if (keyboardElement != null) {
								if (keyboardElement instanceof TextField) ((TextField)keyboardElement).resetTabFocus();
							}
							keyboardElement = null;
							eventElementOriginXY.set(eventElement.getPosition());
						} else if (eventElement instanceof KeyboardListener) {
							if (keyboardElement != null) {
								if (keyboardElement instanceof TextField) ((TextField)keyboardElement).resetTabFocus();
							}
							keyboardElement = eventElement;
							if (keyboardElement instanceof TextField) {
								((TextField)keyboardElement).setTabFocus();
							//	((TextField)keyboardElement).setCaretPositionByX(evt.getX());
							}
							// TODO: Update target element's font shader
						} else {
							eventElementResizeDirection = null;
							if (keyboardElement != null) {
								if (keyboardElement instanceof TextField) ((TextField)keyboardElement).resetTabFocus();
							}
							keyboardElement = null;
						}
						if (eventElement instanceof MouseButtonListener) {
							((MouseButtonListener)eventElement).onMouseLeftPressed(evt);
						}
						evt.setConsumed();
					}
					break;
				case 1:
					mouseRightPressed = true;
					eventElement = getEventElement(evt.getX(), evt.getY());
					if (eventElement != null) {
						updateZOrder(eventElement.getAbsoluteParent());
						if (eventElement instanceof MouseButtonListener) {
							((MouseButtonListener)eventElement).onMouseRightPressed(evt);
						}
						evt.setConsumed();
					}
					break;
				case 2:
					mouseWheelPressed = true;
					eventElement = getEventElement(evt.getX(), evt.getY());
					if (eventElement != null) {
						if (eventElement instanceof MouseWheelListener) {
							((MouseWheelListener)eventElement).onMouseWheelPressed(evt);
						}
						evt.setConsumed();
					}
					break;
			}
		} else if (evt.isReleased()) {
			handleMenuState();
			switch (evt.getButtonIndex()) {
				case 0:
					mouseLeftPressed = false;
					eventElementResizeDirection = null;
				//	if (eventElement.getIsDragDropDragElement())
					targetElement = getTargetElement(evt.getX(), evt.getY());
					if (eventElement instanceof MouseButtonListener) {
						((MouseButtonListener)eventElement).onMouseLeftReleased(evt);
					}
					if (eventElement != null)
						evt.setConsumed();
					break;
				case 1:
					mouseRightPressed = false;
					if (eventElement instanceof MouseButtonListener) {
						((MouseButtonListener)eventElement).onMouseRightReleased(evt);
					}
					if (eventElement != null)
						evt.setConsumed();
					break;
				case 2:
					mouseWheelPressed = false;
					if (eventElement instanceof MouseWheelListener) {
						((MouseWheelListener)eventElement).onMouseWheelReleased(evt);
					}
					if (eventElement != null)
						evt.setConsumed();
					break;
			}
			mousePressed = false;
			eventElement = null;
		}
	}
	
	@Override
	public void onKeyEvent(KeyInputEvent evt) {
		if (evt.getKeyCode() == KeyInput.KEY_LSHIFT || evt.getKeyCode() == KeyInput.KEY_RSHIFT) {
			if (evt.isPressed()) SHIFT = true;
			else SHIFT = false;
		}
		if (evt.getKeyCode() == KeyInput.KEY_LCONTROL || evt.getKeyCode() == KeyInput.KEY_RCONTROL) {
			if (evt.isPressed()) CTRL = true;
			else CTRL = false;
		}
		if (evt.getKeyCode() == KeyInput.KEY_LMENU || evt.getKeyCode() == KeyInput.KEY_RMENU) {
			if (evt.isPressed()) ALT = true;
			else ALT = false;
		}
		if (evt.getKeyCode() == KeyInput.KEY_TAB && evt.isPressed()) {
			if (focusForm != null) {
				if (!SHIFT)	focusForm.tabNext();
				else		focusForm.tabPrev();
			}
		} else {
			if (keyboardElement != null) {
				if (keyboardElement.getParent() != null && keyboardElement.getIsVisible()) {
					if (evt.isPressed()) {
						((KeyboardListener)keyboardElement).onKeyPress(evt);
					} else if (evt.isReleased()) {
						((KeyboardListener)keyboardElement).onKeyRelease(evt);
					}
				}
			}
		}
	}

	@Override
	public void onTouchEvent(TouchEvent evt) {
	//	throw new UnsupportedOperationException("Not supported yet.");
	}
	
	/**
	 * Determines and returns the current mouse focus Element
	 * @param x The current mouse X coord
	 * @param y The current mouse Y coord
	 * @return Element eventElement
	 */
	private Element getEventElement(float x, float y) {
		guiRayOrigin.set(x, y, 0f);
		
		elementZOrderRay.setOrigin(guiRayOrigin);
		CollisionResults results = new CollisionResults();

		t0neg0dGUI.collideWith(elementZOrderRay, results);

		float z = 0;
		Element testEl = null, el = null;
		for (CollisionResult result : results) {
			boolean discard = false;
			if (result.getGeometry().getParent() instanceof Element) {
				testEl = ((Element)(result.getGeometry().getParent()));
				if (testEl.getIgnoreMouse()) {
					discard = true;
				} else if (testEl.getIsClipped()) {
					if (result.getContactPoint().getX() < testEl.getClippingBounds().getX() ||
						result.getContactPoint().getX() > testEl.getClippingBounds().getZ() ||
						result.getContactPoint().getY() < testEl.getClippingBounds().getY() ||
						result.getContactPoint().getY() > testEl.getClippingBounds().getW()) {
						discard = true;
					}
				}
			}
		//	System.out.println(testEl.getUID() + ": " + discard + ": " + testEl.getLocalTranslation().getZ() + ": " + z + ": " + result.getContactPoint().getZ());
			if (!discard) {
				
				if (result.getContactPoint().getZ() > z) {
					z = result.getContactPoint().getZ();
					if (result.getGeometry().getParent() instanceof Element) {
						el = testEl;//((Element)(result.getGeometry().getParent()));
					}
				}
			}
		}
		if (el != null) {
			Element parent = null;
			if (el.getEffectParent() && mousePressed) {
				parent = el.getElementParent();
			} else if (el.getEffectAbsoluteParent() && mousePressed) {
				parent = el.getAbsoluteParent();
			}
			if (parent != null) {
				el = parent;
			}
			eventElementOffsetX = x-el.getX();
			eventElementOffsetY = y-el.getY();
			return el;
		} else {
			return null;
		}
	}
	
	/**
	 * Determines and returns the current mouse focus Element
	 * @param x The current mouse X coord
	 * @param y The current mouse Y coord
	 * @return Element eventElement
	 */
	private Element getTargetElement(float x, float y) {
		guiRayOrigin.set(x, y, 0f);
		
		elementZOrderRay.setOrigin(guiRayOrigin);
		CollisionResults results = new CollisionResults();

		t0neg0dGUI.collideWith(elementZOrderRay, results);

		float z = 0;
		Element testEl = null, el = null;
		for (CollisionResult result : results) {
			boolean discard = false;
			if (result.getGeometry().getParent() instanceof Element) {
				testEl = ((Element)(result.getGeometry().getParent()));
				if (testEl.getIgnoreMouse() || !testEl.getIsDragDropDropElement()) {
					discard = true;
				} else if (testEl.getIsClipped()) {
					if (result.getContactPoint().getX() < testEl.getClippingBounds().getX() ||
						result.getContactPoint().getX() > testEl.getClippingBounds().getZ() ||
						result.getContactPoint().getY() < testEl.getClippingBounds().getY() ||
						result.getContactPoint().getY() > testEl.getClippingBounds().getW()) {
						discard = true;
					}
				}
			}
		//	System.out.println(testEl.getUID() + ": " + discard + ": " + testEl.getLocalTranslation().getZ() + ": " + z + ": " + result.getContactPoint().getZ());
			if (!discard) {
				
				if (result.getContactPoint().getZ() > z) {
					z = result.getContactPoint().getZ();
					if (result.getGeometry().getParent() instanceof Element) {
						el = testEl;//((Element)(result.getGeometry().getParent()));
					}
				}
			}
		}
		if (el != null) {
			Element parent = null;
			if (el.getEffectParent() && mousePressed) {
				parent = el.getElementParent();
			} else if (el.getEffectAbsoluteParent() && mousePressed) {
				parent = el.getAbsoluteParent();
			}
			if (parent != null) {
				el = parent;
			}
			targetElementOffsetX = x-el.getX();
			targetElementOffsetY = y-el.getY();
			return el;
		} else {
			return null;
		}
	}
	
	/**
	 * Returns the current Drag enabled Element
	 * @return Element
	 */
	public Element getDragElement() {
		return this.eventElement;
	}
	
	/**
	 * Returns the current Drop enabled Element
	 * @return Element
	 */
	public Element getDropElement() {
		return this.targetElement;
	}
	
	/**
	 * Sets the current stored text to the internal clipboard.  This is probably going
	 * to vanish quickly.
	 * @param text The text to store
	 */
	public void setClipboardText(String text) {
		try {
			clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			StringSelection stringSelection = new StringSelection( text );
			clipboard.setContents(stringSelection, this);
		} catch (Exception ex) {
			this.clipboardText = text;
		}
	}
	
	/**
	 * Returns the internal clipboard's current stored text
	 * @return String text
	 */
	public String getClipboardText() {
		String ret = "";
		clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable text = clipboard.getContents(null);
		boolean isText = (text != null && text.isDataFlavorSupported(DataFlavor.stringFlavor));
		if (isText) {
			try {
				ret = (String)text.getTransferData(DataFlavor.stringFlavor);
			} catch (Exception ex) {
				ret = this.clipboardText;
				if (ret == null)
					ret = "";
			}
		}
		return ret;
	}
	
	/**
	 * Returns a pointer to the EffectManager
	 * @return EffectManager effectManager
	 */
	public EffectManager getEffectManager() {
		return this.effectManager;
	}
	
	public void setGlobalUIScale(float widthPercent, float heightPercent) {
		for (Element el : elements.values()) {
			el.setPosition(el.getPosition().x*widthPercent, el.getPosition().y*heightPercent);
			el.setDimensions(el.getDimensions().x*widthPercent, el.getDimensions().y*heightPercent);
			el.setFontSize(el.getFontSize()*heightPercent);
			el.setGlobalUIScale(widthPercent, heightPercent);
		}
	}
	
	@Override
	public Control cloneForSpatial(Spatial spatial) {
		Screen screen = new Screen(this.app, this.styleMap);
		screen.elements.putAll(this.elements);
		return screen;
	}

	@Override
	public void setSpatial(Spatial spatial) {
		this.spatial = spatial;
		if (spatial != null) {
			((Node)spatial).attachChild(t0neg0dGUI);
			t0neg0dGUI.addControl(effectManager);
		}
	}
	
	@Override
	public void write(JmeExporter ex) throws IOException {  }
	
	@Override
	public void read(JmeImporter im) throws IOException {  }
	
	// Styles
	private void parseStyles(String path) {
		List<String> docPaths = new ArrayList();
		try {
			// Get Cursors
			InputStream file = Screen.class.getClassLoader().getResourceAsStream(
				path
			);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();
			NodeList nodeLst = doc.getElementsByTagName("cursors");
			
			for (int s = 0; s < nodeLst.getLength(); s++) {
				org.w3c.dom.Node fstNode = nodeLst.item(0);
				if (fstNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
					org.w3c.dom.Element fstElmnt = (org.w3c.dom.Element) fstNode;
				//	String styleName = XMLHelper.getNodeAttributeValue(fstNode, "control");
					String cursorDocPath = XMLHelper.getNodeAttributeValue(fstNode, "path");
					docPaths.add(cursorDocPath);
				}
			}
			
			if (file != null)
				file.close();
			
			for (String docPath : docPaths) {
				try {
					file = Screen.class.getClassLoader().getResourceAsStream(
						docPath
					);
					
					dbf = DocumentBuilderFactory.newInstance();
					db = dbf.newDocumentBuilder();
					doc = db.parse(file);
					doc.getDocumentElement().normalize();
					NodeList nLst = doc.getElementsByTagName("cursor");
					
					for (int s = 0; s < nLst.getLength(); s++) {
						org.w3c.dom.Node fstNode = nLst.item(s);
						if (fstNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
							org.w3c.dom.Element fstElmnt = (org.w3c.dom.Element) fstNode;
							String key = XMLHelper.getNodeAttributeValue(fstNode, "type");
							String curPath = XMLHelper.getNodeAttributeValue(fstNode, "path");
							
							JmeCursor jmeCursor = new JmeCursor();
							
							cursors.put(
								CursorType.valueOf(key), 
								(JmeCursor)app.getAssetManager().loadAsset(curPath)
							);
						}
					}
					if (file != null)
						file.close();
				} catch (Exception ex) {
					System.err.println("Problem loading control definition: " + ex);
				}
			}
			
			// Get Audio
			docPaths.clear();
			
			file = Screen.class.getClassLoader().getResourceAsStream(
				path
			);
			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			doc = db.parse(file);
			doc.getDocumentElement().normalize();
			nodeLst = doc.getElementsByTagName("audio");
			
			for (int s = 0; s < nodeLst.getLength(); s++) {
				org.w3c.dom.Node fstNode = nodeLst.item(0);
				if (fstNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
					org.w3c.dom.Element fstElmnt = (org.w3c.dom.Element) fstNode;
				//	String styleName = XMLHelper.getNodeAttributeValue(fstNode, "control");
					String cursorDocPath = XMLHelper.getNodeAttributeValue(fstNode, "path");
					docPaths.add(cursorDocPath);
				}
			}
			
			if (file != null)
				file.close();
			
			for (String docPath : docPaths) {
				try {
					file = Screen.class.getClassLoader().getResourceAsStream(
						docPath
					);
					
					dbf = DocumentBuilderFactory.newInstance();
					db = dbf.newDocumentBuilder();
					doc = db.parse(file);
					doc.getDocumentElement().normalize();
					NodeList nLst = doc.getElementsByTagName("audiofile");
					
					for (int s = 0; s < nLst.getLength(); s++) {
						org.w3c.dom.Node fstNode = nLst.item(s);
						if (fstNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
							org.w3c.dom.Element fstElmnt = (org.w3c.dom.Element) fstNode;
							String key = XMLHelper.getNodeAttributeValue(fstNode, "key");
							String audioPath = XMLHelper.getNodeAttributeValue(fstNode, "path");
							
							AudioNode audioNode = new AudioNode(app.getAssetManager(), audioPath, false);
							audioNode.setPositional(false);
							audioNode.setReverbEnabled(false);
							audioNodes.put(key, audioNode);
							t0neg0dGUI.attachChild(audioNode);
						}
					}
					if (file != null)
						file.close();
				} catch (Exception ex) {
					System.err.println("Problem loading audio file: " + ex);
				}
			}
			
			// Get Styles
			docPaths.clear();
			
			file = Screen.class.getClassLoader().getResourceAsStream(
				path
			);
			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			doc = db.parse(file);
			doc.getDocumentElement().normalize();
			nodeLst = doc.getElementsByTagName("style");
			
			for (int s = 0; s < nodeLst.getLength(); s++) {
				org.w3c.dom.Node fstNode = nodeLst.item(s);
				if (fstNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
					org.w3c.dom.Element fstElmnt = (org.w3c.dom.Element) fstNode;
					String styleName = XMLHelper.getNodeAttributeValue(fstNode, "control");
					String styleDocPath = XMLHelper.getNodeAttributeValue(fstNode, "path");
					docPaths.add(styleDocPath);
				}
			}

			if (file != null)
				file.close();
			
			for (String docPath : docPaths) {
				try {
					file = Screen.class.getClassLoader().getResourceAsStream(
						docPath
					);
					
					dbf = DocumentBuilderFactory.newInstance();
					db = dbf.newDocumentBuilder();
					doc = db.parse(file);
					doc.getDocumentElement().normalize();
					NodeList nLst = doc.getElementsByTagName("element");
					
					for (int s = 0; s < nLst.getLength(); s++) {
						org.w3c.dom.Node fstNode = nLst.item(s);
						if (fstNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
							org.w3c.dom.Element fstElmnt = (org.w3c.dom.Element) fstNode;
							String key = XMLHelper.getNodeAttributeValue(fstNode, "name");
							
							Style style = new Style();
							
							try {
								org.w3c.dom.Node nds = fstElmnt.getElementsByTagName("attributes").item(0);
								org.w3c.dom.Element el = (org.w3c.dom.Element) nds;
								NodeList nodes = el.getElementsByTagName("property");
								
								for (int n = 0; n < nodes.getLength(); n++) {
									org.w3c.dom.Node nNode = nodes.item(n);
									if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
										org.w3c.dom.Element nElmnt = (org.w3c.dom.Element) nNode;
										addStyleTag(style, nNode, nElmnt);
									}
								}
								
								nds = fstElmnt.getElementsByTagName("images").item(0);
								el = (org.w3c.dom.Element) nds;
								nodes = el.getElementsByTagName("property");
								
								for (int n = 0; n < nodes.getLength(); n++) {
									org.w3c.dom.Node nNode = nodes.item(n);
									if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
										org.w3c.dom.Element nElmnt = (org.w3c.dom.Element) nNode;
										addStyleTag(style, nNode, nElmnt);
									}
								}
								
								nds = fstElmnt.getElementsByTagName("font").item(0);
								el = (org.w3c.dom.Element) nds;
								nodes = el.getElementsByTagName("property");
								
								for (int n = 0; n < nodes.getLength(); n++) {
									org.w3c.dom.Node nNode = nodes.item(n);
									if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
										org.w3c.dom.Element nElmnt = (org.w3c.dom.Element) nNode;
										addStyleTag(style, nNode, nElmnt);
									}
								}
								
								nds = fstElmnt.getElementsByTagName("effects").item(0);
								el = (org.w3c.dom.Element) nds;
								nodes = el.getElementsByTagName("property");
								
								for (int n = 0; n < nodes.getLength(); n++) {
									org.w3c.dom.Node nNode = nodes.item(n);
									if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
										org.w3c.dom.Element nElmnt = (org.w3c.dom.Element) nNode;
										addStyleTag(style, nNode, nElmnt);
									}
								}
							} catch (Exception ex) {
								System.err.println("Problem parsing attributes: " + ex);
							}
							styles.put(key, style);
						}
					}
					if (file != null)
						file.close();
				} catch (Exception ex) {
					System.err.println("Problem loading control definition: " + ex);
				}
			}
			
			if (file != null)
				file.close();
			
			doc = null;
			db = null;
			dbf = null;
		} catch (Exception e) {
			System.err.println("Problem loading style map: " + e);
		}
	}
	
	private void addStyleTag(Style style, org.w3c.dom.Node nNode, org.w3c.dom.Element nElmnt) {
		String name = XMLHelper.getNodeAttributeValue(nNode, "name");
		String type = XMLHelper.getNodeAttributeValue(nNode, "type");
	//	System.out.println(name + " : " + type);
		if (type.equals("Vector2f")) {
			style.putTag(
				name,
				new Vector2f(
					Float.parseFloat(XMLHelper.getNodeAttributeValue(nElmnt.getElementsByTagName("x").item(0), "value")),
					Float.parseFloat(XMLHelper.getNodeAttributeValue(nElmnt.getElementsByTagName("y").item(0), "value"))
				)
			);
		//	System.out.println(style.getVector2f(name));
		} else if (type.equals("Vector3f")) {
			style.putTag(
				name,
				new Vector3f(
					Float.parseFloat(XMLHelper.getNodeAttributeValue(nElmnt.getElementsByTagName("x").item(0), "value")),
					Float.parseFloat(XMLHelper.getNodeAttributeValue(nElmnt.getElementsByTagName("y").item(0), "value")),
					Float.parseFloat(XMLHelper.getNodeAttributeValue(nElmnt.getElementsByTagName("z").item(0), "value"))
				)
			);
		} else if (type.equals("Vector4f")) {
			style.putTag(
				name,
				new Vector4f(
					Float.parseFloat(XMLHelper.getNodeAttributeValue(nElmnt.getElementsByTagName("x").item(0), "value")),
					Float.parseFloat(XMLHelper.getNodeAttributeValue(nElmnt.getElementsByTagName("y").item(0), "value")),
					Float.parseFloat(XMLHelper.getNodeAttributeValue(nElmnt.getElementsByTagName("z").item(0), "value")),
					Float.parseFloat(XMLHelper.getNodeAttributeValue(nElmnt.getElementsByTagName("w").item(0), "value"))
				)
			);
		} else if (type.equals("ColorRGBA")) {
			style.putTag(
				name,
				new ColorRGBA(
					Float.parseFloat(XMLHelper.getNodeAttributeValue(nElmnt.getElementsByTagName("r").item(0), "value")),
					Float.parseFloat(XMLHelper.getNodeAttributeValue(nElmnt.getElementsByTagName("g").item(0), "value")),
					Float.parseFloat(XMLHelper.getNodeAttributeValue(nElmnt.getElementsByTagName("b").item(0), "value")),
					Float.parseFloat(XMLHelper.getNodeAttributeValue(nElmnt.getElementsByTagName("a").item(0), "value"))
				)
			);
		} else if (type.equals("float")) {
			style.putTag(
				name,
				Float.parseFloat(XMLHelper.getNodeAttributeValue(nNode, "value"))
			);
		} else if (type.equals("int")) {
			style.putTag(
				name,
				Integer.parseInt(XMLHelper.getNodeAttributeValue(nNode, "value"))
			);
		} else if (type.equals("boolean")) {
			style.putTag(
				name,
				Boolean.parseBoolean(XMLHelper.getNodeAttributeValue(nNode, "value"))
			);
		} else if (type.equals("String")) {
			style.putTag(
				name,
				XMLHelper.getNodeAttributeValue(nNode, "value")
			);
		} else if (type.equals("Effect")) {
			style.putTag(
				name,
				new Effect(
					Effect.EffectType.valueOf(XMLHelper.getNodeAttributeValue(nElmnt.getElementsByTagName("effect").item(0), "value")),
					Effect.EffectEvent.valueOf(XMLHelper.getNodeAttributeValue(nElmnt.getElementsByTagName("event").item(0), "value")),
					Float.parseFloat(XMLHelper.getNodeAttributeValue(nElmnt.getElementsByTagName("speed").item(0), "value"))
				)
			);
		}
	}
	
	/**
	 * Returns the Style object associated to the provided key
	 * @param key The String key of the Style
	 * @return Style style
	 */
	public Style getStyle(String key) {
		return styles.get(key);
	}
	
	/**
	 * Enables the use of Style defined custom cursors.  Initally set prior to initializing screen
	 * @param useCustomCursors boolean
	 */
	public void setUseCustomCursors(boolean useCustomCursors) {
		this.useCustomCursors = useCustomCursors;
		if (!useCustomCursors) {
			getApplication().getInputManager().setMouseCursor(null);
		} else {
			setCursor(CursorType.POINTER);
		}
	}
	
	/**
	 * Returns true if custom cursors are currently enabled
	 * @return boolean
	 */
	public boolean getUseCustomCursors() {
		return this.useCustomCursors;
	}
	
	/**
	 * For internal use - Use setForcedCursor instead
	 * @param cur 
	 */
	public void setCursor(CursorType cur) {
		if (getUseCustomCursors()) {
			if (!forceCursor) {
				JmeCursor jmeCur = cursors.get(cur);
				if (jmeCur != null)
					getApplication().getInputManager().setMouseCursor(jmeCur);
			}
		}
	}
	
	/**
	 * Sets the cursor and locks the cursor until releaseForcedCursor is called.
	 * @param cur CursorType
	 */
	public void setForcedCursor(CursorType cur) {
		if (getUseCustomCursors()) {
			JmeCursor jmeCur = cursors.get(cur);
			if (jmeCur != null) {
				getApplication().getInputManager().setMouseCursor(jmeCur);
				forceCursor = true;
			}
		}
	}
	
	/**
	 * Release cursor control back to the Element level
	 */
	public void releaseForcedCursor() {
		if (getUseCustomCursors()) {
			JmeCursor jmeCur = cursors.get(CursorType.POINTER);
			if (jmeCur != null) {
				getApplication().getInputManager().setMouseCursor(jmeCur);
			}
			forceCursor = false;
		}
	}
	
	/**
	 * Sets the overall opacity of all elements that have not been flagged as ignoreGlobalAlpha(true)
	 * @param globalAlpha float
	 */
	public void setGlobalAlpha(float globalAlpha) {
		this.globalAlpha = globalAlpha;
		for (Element el : elements.values()) {
			el.setGlobalAlpha(globalAlpha);
		}
	}
	
	/**
	 * Returns the current value of global alpha
	 * @return float
	 */
	public float getGlobalAlpha() {
		return this.globalAlpha;
	}
	
	// Menu handling
	private void handleMenuState() {
		if (eventElement == null) {
			for (Element el : elements.values()) {
				if (el instanceof Menu) {
					el.hide();
				}
			}
		} else {
			if (!(eventElement.getAbsoluteParent() instanceof Menu) && !(eventElement.getParent() instanceof ComboBox)) {
				for (Element el : elements.values()) {
					if (el instanceof Menu) {
						el.hide();
					}
				}
			} else if (eventElement.getAbsoluteParent() instanceof Menu) {
				for (Element el : elements.values()) {
					if (el instanceof Menu && el != eventElement.getAbsoluteParent()) {
						el.hide();
					}
				}
			} else if (eventElement.getParent() instanceof ComboBox) {
				for (Element el : elements.values()) {
					if (el instanceof Menu && el != ((ComboBox)eventElement.getParent()).getMenu()) {
						el.hide();
					}
				}
			}
		}
	}
	
	// ToolTips
	/**
	 * Enables/disables the use of ToolTips
	 * @param useToolTips boolean
	 */
	public void setUseToolTips(boolean useToolTips) {
		this.useToolTips = useToolTips;
		if (useToolTips) {
			if (toolTip == null) {
				toolTip = new ToolTip(
					this,
					"GlobalToolTip",
					new Vector2f(0,0),
					new Vector2f(200,50)
				);
				toolTip.setIgnoreGlobalAlpha(true);
				toolTip.setIsGlobalModal(true);
				toolTip.setTextPadding(2);
				toolTip.setTextPosition(0, 0);
				toolTip.hide();
				addElement(toolTip);
				toolTip.move(0,0,20);
			} else {
				t0neg0dGUI.attachChild(toolTip);
			}
		} else {
			if (toolTip != null)
				toolTip.removeFromParent();
		}
	}
	
	/**
	 * Returns if ToolTips are enabled/disabled
	 * @return boolean
	 */
	public boolean getUseToolTips() {
		return useToolTips;
	}
	
	/**
	 * For internal use only - DO NOT CALL THIS METHOD
	 */
	public void updateToolTipLocation() {
		if (useToolTips) {
			if (this.mouseFocusElement != null && getApplication().getInputManager().isCursorVisible()) {
				String toolTipText = this.mouseFocusElement.getToolTipText();
				if (toolTipText != null) {
					if (!toolTip.getText().equals(this.mouseFocusElement.getToolTipText())) {
						toolTip.setText("");
						toolTip.setHeight(25);
						float finalWidth = BitmapTextUtil.getTextWidth(toolTip, toolTipText, toolTipMaxWidth);
						toolTip.setText(toolTipText);
						toolTip.setWidth(finalWidth+(toolTip.getTextPadding()*12));
						toolTip.setHeight(toolTip.getTextElement().getHeight()+(toolTip.getTextPadding()*12));
						toolTip.getTextElement().setBox(new Rectangle(0,0,toolTip.getWidth()-(toolTip.getTextPadding()*2),toolTip.getHeight()-(toolTip.getTextPadding()*2)));
					}
					float nextX = mouseXY.x-(toolTip.getWidth()/2);
					if (nextX < 0) nextX = 0;
					else if (nextX+toolTip.getWidth() > getWidth()) nextX = getWidth()-toolTip.getWidth();
					float nextY = mouseXY.y-toolTip.getHeight()-40;
					if (nextY < 0) nextY = mouseXY.y+5;
					toolTip.moveTo(nextX, nextY);
					if (!toolTip.getIsVisible())
						toolTip.show();
				} else {
					toolTip.setText("");
					toolTip.hide();
				}
			} else {
				toolTip.setText("");
				toolTip.hide();
			}
		}
	}
	
	// Audio support
	/**
	 * Enables/disables UI Audio
	 * @param useUIAudio boolean
	 */
	public void setUseUIAudio(boolean useUIAudio) {
		this.useUIAudio = useUIAudio;
	}
	
	/**
	 * Returns if the UI Audio option is enabled/disabled
	 * @return boolean
	 */
	public boolean getUseUIAudio() {
		return this.useUIAudio;
	}
	
	/**
	 * Sets the global UI Audio volume
	 * @param uiAudioVolume float
	 */
	public void setUIAudioVolume(float uiAudioVolume) {
		this.uiAudioVolume = uiAudioVolume;
	}
	
	/**
	 * Gets the current global UI Audio volume
	 * @return float
	 */
	public float getUIAudioVolume() {
		return this.uiAudioVolume;
	}
	
	/**
	 * Plays an instance of an audio node
	 * @param key String The key associated with the audio node
	 * @param volume float the volume to play the instance at (effected by global volume)
	 */
	public void playAudioNode(String key, float volume) {
		AudioNode audioNode = audioNodes.get(key);
		if (audioNode != null) {
			audioNode.setVolume(volume*getUIAudioVolume());
			audioNode.playInstance();
		}
	}
	
	// Cursor Effects
	/**
	 * Enables/disables the use of Cursor effects
	 * @param useCursorEffects boolean
	 */
	public void setUseCursorEffects(boolean useCursorEffects) {
		if (useCursorEffects) {
			if (cursorEmitterVP == null) {
				initializeCursorEmitter();
			}
			t0neg0dGUI.attachChild(cursorEmitterVP);
			cursorEmitterVP.move(0,0,19);
		} else {
			if (cursorEmitterVP != null)
				cursorEmitterVP.removeFromParent();
		}
		this.useCursorEffects = useCursorEffects;
	}
	
	private void initializeCursorEmitter() {
		cursorEmitter = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 25);
		Material mat_red = new Material(app.getAssetManager(), "tonegod/gui/shaders/Particle.j3md");
		mat_red.setTexture("Texture", app.getAssetManager().loadTexture("tonegod/gui/style/def/Common/Particles/spark.png"));
		cursorEmitter.setMaterial(mat_red);
		cursorEmitter.setQueueBucket(RenderQueue.Bucket.Transparent);
		cursorEmitter.setShape(new EmitterSphereShape(Vector3f.ZERO,.08f));
		cursorEmitter.setImagesX(2); 
		cursorEmitter.setImagesY(2);
		configEmitterDefault();
		cursorEmitter.setEnabled(true);
		cursorEmitterNode.attachChild(cursorEmitter);
		
		Material mat = new Material(app.getAssetManager(), "tonegod/gui/shaders/Unshaded.j3md");
		mat.setColor("Color",new ColorRGBA(0,0,0,0));
		
		Quad q = new Quad(8000,8000);
		Geometry quadGeom = new Geometry();
		quadGeom.setMesh(q);
		cursorEmitterPlaneNode = new Node("PlaneNode");
		cursorEmitterPlaneNode.attachChild(quadGeom);
		cursorEmitterPlaneNode.setMaterial(mat);
		cursorEmitterPlaneNode.setLocalTranslation(new Vector3f(-4000,-4000,0));
		cursorEmitterNode.attachChild(cursorEmitterPlaneNode);
		
		cursorEmitterVP = new OSRViewPort(this, "subView", new Vector2f(0,0), new Vector2f(getWidth(),getHeight()), new Vector4f(0,0,0,0), null);
		cursorEmitterVP.setOSRBridge(cursorEmitterNode, (int)getWidth(), (int)getHeight());
		cursorEmitterVP.setBackgroundColor(new ColorRGBA(0,0,0,0));
		cursorEmitterVP.setUseCameraControlZoom(false);
		cursorEmitterVP.setUseCameraControlRotate(false);
		cursorEmitterVP.setCameraDistance(5f);
		cursorEmitterVP.setCameraMinDistance(0.15f);
		cursorEmitterVP.setCameraMaxDistance(5f);
		cursorEmitterVP.setCameraHorizonalRotation(-90*FastMath.DEG_TO_RAD);
		cursorEmitterVP.setCameraVerticalRotation(0);
		cursorEmitterVP.setIgnoreMouse(true);
		cursorEmitterVP.setIgnoreGlobalAlpha(true);
		cursorEmitterVP.setIsGlobalModal(true);
	}
	
	/**
	 * For internal use - DO NOT CALL THIS METHOD
	 */
	public void updateCursorEmitter() {
		Camera cam = cursorEmitterVP.getOSRBridge().getCamera();
		CollisionResults results = new CollisionResults();
		Vector3f click3d = cam.getWorldCoordinates(
			new Vector2f(mouseXY.x, mouseXY.y), 0f).clone();
		Vector3f dir = cam.getWorldCoordinates(
			new Vector2f(mouseXY.x, mouseXY.y), 1f).subtractLocal(click3d).normalizeLocal();
		Ray ray = new Ray(click3d, dir);
		cursorEmitterPlaneNode.collideWith(ray, results);
		
		if(results.size() > 0) {
			CollisionResult result = results.getClosestCollision();
			cursorEmitter.setLocalTranslation(result.getContactPoint().getX(), result.getContactPoint().getY(), 0);
		}
	}
	
	private void configEmitterDefault() {
		cursorEmitter.setEndColor(  new ColorRGBA(0f, 0f, 1f, 1f));
		cursorEmitter.setStartColor(new ColorRGBA(.8f, .8f, 1f, 0.5f));
		cursorEmitter.getParticleInfluencer().setInitialVelocity(new Vector3f(0, .25f, 0));
		cursorEmitter.setStartSize(.065f);
		cursorEmitter.setEndSize(.02f);
		cursorEmitter.setGravity(0, 1.5f, 0);
		cursorEmitter.setLowLife(.8f);
		cursorEmitter.setHighLife(1.5f);
		cursorEmitter.setParticlesPerSec(12);
	}
	
	private void configEmiterClick(int button) {
		cursorEmitter.setEndColor(  new ColorRGBA(.5f, .5f, 1f, 1f));
		cursorEmitter.setStartColor(new ColorRGBA(.8f, .8f, 1f, 0.5f));
		if (button == 0) {
			cursorEmitter.getParticleInfluencer().setInitialVelocity(new Vector3f(.75f, 1f, 0));
		} else if (button == 1) {
			cursorEmitter.getParticleInfluencer().setInitialVelocity(new Vector3f(-.75f, 1f, 0));
		} else if (button == 2) {
			cursorEmitter.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 1f, 0));
		}
		cursorEmitter.setStartSize(.065f);
		cursorEmitter.setEndSize(.02f);
		cursorEmitter.setGravity(0, .75f, 0);
		cursorEmitter.setLowLife(.8f);
		cursorEmitter.setHighLife(1.5f);
		cursorEmitter.emitAllParticles();
		configEmitterDefault();
	}
	
	// Forms and tab focus
	/**
	 * Method for setting the tab focus element
	 * @param element The Element to set tab focus to
	 */
	public void setTabFocusElement(Element element) {
		resetFocusElement();
		focusForm = element.getForm();
		if (focusForm != null) {
			tabFocusElement = element;
			focusForm.setSelectedTabIndex(element);
			if (tabFocusElement instanceof TabFocusListener) {
				((TabFocusListener)element).setTabFocus();
			}
		}
	}
	
	/**
	 * Resets the tab focus element to null after calling the TabFocusListener's
	 * resetTabFocus method.
	 */
	public void resetTabFocusElement() {
		resetFocusElement();
		this.tabFocusElement = null;
		this.focusForm = null;
	}
	
	/**
	 * Send reset to the current Tab Focus Element
	 */
	private void resetFocusElement() {
		if (tabFocusElement != null) {
			if (tabFocusElement instanceof TabFocusListener) {
				((TabFocusListener)tabFocusElement).resetTabFocus();
			}
		}
	}
	
	/**
	 * Sets the current Keyboard focus Element
	 * @param element The Element to set keyboard focus to
	 */
	public void setKeyboardElemeent(Element element) {
		keyboardElement = element;
	}
	
	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
	//	System.out.println("Clipboard failed, switching to internal clipboard.");
	//	this.clipboardActive = false;
	}
	
	// Layout Parser
	public void parseLayout(String path, AbstractAppState state) {
		layoutParser.parseLayout(path, state);
	}
	
	// Key states
	public boolean getCtrl() { return this.CTRL; }
	public boolean getShift() { return this.SHIFT; }
	public boolean getAlt() { return this.ALT; }
	
	// Determining OS
	public static boolean isWindows() {
		String OS = System.getProperty("os.name").toLowerCase();
		return (OS.indexOf("win") >= 0);
	}
	public static boolean isMac() {
 		String OS = System.getProperty("os.name").toLowerCase();
		return (OS.indexOf("mac") >= 0);
	}
	public static boolean isUnix() {
		String OS = System.getProperty("os.name").toLowerCase();
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
	}
	public static boolean isSolaris() {
		String OS = System.getProperty("os.name").toLowerCase();
		return (OS.indexOf("sunos") >= 0);
	}
}
