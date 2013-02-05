/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.extras;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.scene.Node;
import tonegod.gui.core.Element;
import tonegod.gui.core.OSRBridge;
import tonegod.gui.core.Screen;
import tonegod.gui.listeners.MouseButtonListener;
import tonegod.gui.listeners.MouseFocusListener;
import tonegod.gui.listeners.MouseMovementListener;
import tonegod.gui.listeners.MouseWheelListener;

/**
 *
 * @author t0neg0d
 */
public class OSRViewPort extends Element implements MouseButtonListener, MouseMovementListener, MouseWheelListener, MouseFocusListener {
	private OSRBridge bridge;
	private boolean rotateEnabled = true;
	private boolean useLeftMouseRotate = false;
	private boolean zoomEnabled = true;
	private boolean enabled = false;
	private boolean mouseLook = false;
	private int lastX = 0, lastY = 0;
	private Element elOverlay;
	
	/**
	 * Creates a new instance of the Window control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 */
	public OSRViewPort(Screen screen, String UID, Vector2f position) {
		this(screen, UID, position,
			screen.getStyle("Window").getVector2f("defaultSize"),
			screen.getStyle("Window").getVector4f("resizeBorders"),
			screen.getStyle("Window").getString("defaultImg")
		);
	}
	
	/**
	 * Creates a new instance of the Window control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 */
	public OSRViewPort(Screen screen, String UID, Vector2f position, Vector2f dimensions) {
		this(screen, UID, position, dimensions,
			screen.getStyle("Window").getVector4f("resizeBorders"),
			screen.getStyle("Window").getString("defaultImg")
		);
	}
	
	/**
	 * Creates a new instance of the Window control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 * @param resizeBorders A Vector4f containg the border information used when resizing the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg The default image to use for the Slider's track
	 */
	public OSRViewPort(Screen screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		super(screen, UID, position, dimensions, resizeBorders, null);
		
		if (defaultImg != null) {
			elOverlay = new Element(
				screen,
				UID + ":Overlay",
				new Vector2f(0,0),
				dimensions.clone(),
				new Vector4f(0,0,0,0),
				defaultImg
			);
			elOverlay.setScaleNS(true);
			elOverlay.setScaleEW(true);
			elOverlay.setDockN(true);
			elOverlay.setDockW(true);
			elOverlay.setIsResizable(true);
			elOverlay.setIsMovable(false);
			elOverlay.setIgnoreMouse(true);

			addChild(elOverlay);
		}
	}
	
	public void setOSRBridge(Node root, int width, int height) {
		bridge = new OSRBridge(screen.getApplication().getRenderManager(), width, height, root);
		addOSRBridge(bridge);
		bridge.getChaseCamera().setDragToRotate(true);
		bridge.getChaseCamera().setHideCursorOnRotate(false);
	}

	public void setLeftMouseButtonRotation(boolean useLeftMouseRotate) {
		this.useLeftMouseRotate = true;
	}
	
	public void setBackgroundColor(ColorRGBA color) {
		bridge.getViewPort().setBackgroundColor(color);
	}
	
	public void setCameraDistance(float distance) {
		bridge.getChaseCamera().setDefaultDistance(distance);
	}

	public void setCameraHorizonalRotation(float angleInRads) {
		bridge.getChaseCamera().setDefaultHorizontalRotation(angleInRads);
	}

	public void setCameraVerticalRotation(float angleInRads) {
		bridge.getChaseCamera().setDefaultVerticalRotation(angleInRads);
	}

	public void setCameraMinDistance(float distance) {
		bridge.getChaseCamera().setMinDistance(distance);
	}

	public void setCameraMaxDistance(float distance) {
		bridge.getChaseCamera().setMaxDistance(distance);
	}

	public void setCameraMinVerticalRotation(float angleInRads) {
		bridge.getChaseCamera().setMinVerticalRotation(angleInRads);
	}

	public void setCameraMaxVerticalRotation(float angleInRads) {
		bridge.getChaseCamera().setMaxVerticalRotation(angleInRads);
	}
	
	public void setUseCameraControlRotate(boolean rotateEnabled) {
		this.rotateEnabled = rotateEnabled;
	}
	
	public void setUseCameraControlZoom(boolean zoomEnabled) {
		this.zoomEnabled = zoomEnabled;
	}
	
	public OSRBridge getOSRBridge() {
		return this.bridge;
	}
	
	@Override
	public void onMouseLeftPressed(MouseButtonEvent evt) {
		if (rotateEnabled && useLeftMouseRotate) {
			mouseLook = true;
			screen.getApplication().getInputManager().setCursorVisible(false);
			bridge.getChaseCamera().onAction("ChaseCamToggleRotate", evt.isPressed(), bridge.getCurrentTPF());
		}
		if (screen.getUseToolTips()) {
			if (getToolTipText() !=  null) {
				screen.setToolTip(null);
			}
		}
		evt.setConsumed();
	}
	@Override
	public void onMouseLeftReleased(MouseButtonEvent evt) {
		if (rotateEnabled && useLeftMouseRotate) {
			mouseLook = false;
			screen.getApplication().getInputManager().setCursorVisible(true);
			bridge.getChaseCamera().onAction("ChaseCamToggleRotate", evt.isPressed(), bridge.getCurrentTPF());
		}
		evt.setConsumed();
	}
	@Override
	public void onMouseRightPressed(MouseButtonEvent evt) {
		if (rotateEnabled && !useLeftMouseRotate) {
			mouseLook = true;
			screen.getApplication().getInputManager().setCursorVisible(false);
			bridge.getChaseCamera().onAction("ChaseCamToggleRotate", evt.isPressed(), bridge.getCurrentTPF());
		}
		if (screen.getUseToolTips()) {
			if (getToolTipText() !=  null) {
				screen.setToolTip(null);
			}
		}
		evt.setConsumed();
	}
	@Override
	public void onMouseRightReleased(MouseButtonEvent evt) {
		if (rotateEnabled && !useLeftMouseRotate) {
			mouseLook = false;
			screen.getApplication().getInputManager().setCursorVisible(true);
			bridge.getChaseCamera().onAction("ChaseCamToggleRotate", evt.isPressed(), bridge.getCurrentTPF());
		}
		evt.setConsumed();
	}
	@Override
	public void onMouseMove(MouseMotionEvent evt) {
		if (mouseLook) {
			if (enabled) {
				if (evt.getY() > lastY)
					bridge.getChaseCamera().onAnalog("ChaseCamUp", -evt.getDY()*(bridge.getCurrentTPF()/2), bridge.getCurrentTPF());
				else
					bridge.getChaseCamera().onAnalog("ChaseCamDown", evt.getDY()*(bridge.getCurrentTPF()/2), bridge.getCurrentTPF());
				if (evt.getX() > lastX)
					bridge.getChaseCamera().onAnalog("ChaseCamMoveRight", evt.getDX()*(bridge.getCurrentTPF()/2), bridge.getCurrentTPF());
				else
					bridge.getChaseCamera().onAnalog("ChaseCamMoveLeft", -evt.getDX()*(bridge.getCurrentTPF()/2), bridge.getCurrentTPF());
			}
			lastX = evt.getX();
			lastY = evt.getY();
			evt.setConsumed();
		}
	}
	@Override
	public void onMouseWheelPressed(MouseButtonEvent evt) {  }
	@Override
	public void onMouseWheelReleased(MouseButtonEvent evt) {  }
	@Override
	public void onMouseWheelUp(MouseMotionEvent evt) {
		if (zoomEnabled) {
			if (enabled) {
				bridge.getChaseCamera().onAnalog("ChaseCamZoomIn", evt.getDeltaWheel()*(bridge.getCurrentTPF()/4), bridge.getCurrentTPF());
			}
		}
		evt.setConsumed();
	}
	@Override
	public void onMouseWheelDown(MouseMotionEvent evt) {
		if (zoomEnabled) {
			if (enabled) {
				bridge.getChaseCamera().onAnalog("ChaseCamZoomIn", evt.getDeltaWheel()*(bridge.getCurrentTPF()/4), bridge.getCurrentTPF());
			}
		}
		evt.setConsumed();
	}
	@Override
	public void onGetFocus(MouseMotionEvent evt) {
		this.enabled = true;
		if (!getHasFocus()) {
			if (screen.getUseToolTips()) {
				if (getToolTipText() !=  null) {
					screen.setToolTip(getToolTipText());
				}
			}
		}
		setHasFocus(true);
	}
	@Override
	public void onLoseFocus(MouseMotionEvent evt) {
		if (!mouseLook)
			this.enabled = false;
		if (getHasFocus()) {
			if (screen.getUseToolTips()) {
				if (getToolTipText() !=  null) {
					screen.setToolTip(null);
				}
			}
		}
		setHasFocus(false);
	}
	
}
