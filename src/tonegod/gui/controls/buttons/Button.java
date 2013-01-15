/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.buttons;

import com.jme3.app.Application;
import com.jme3.font.BitmapFont;
import com.jme3.font.LineWrapMode;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.texture.Texture;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;
import tonegod.gui.effects.Effect;
import tonegod.gui.listeners.MouseButtonListener;
import tonegod.gui.listeners.MouseFocusListener;

/**
 *
 * @author t0neg0d
 */
public abstract class Button extends Element implements Control, MouseButtonListener, MouseFocusListener {
	Element icon;
	Texture hoverImg = null, pressedImg = null;
	private ColorRGBA hoverFontColor = null, pressedFontColor = null;
	private boolean isToggleButton = false;
	private boolean isToggled = false;
	private boolean hasFocus = false;
	private Spatial spatial;
	private boolean isStillPressed = false;
	private boolean useInterval = false;
	private float intervalsPerSecond = 4;
	private float trackInterval = (4/1000), currentTrack = 0;
	
	public Button(Screen screen, String UID, Vector2f position) {
		this(screen, UID, position,
			screen.getStyle("Button").getVector2f("defaultSize"),
			screen.getStyle("Button").getVector4f("resizeBorders"),
			screen.getStyle("Button").getString("defaultImg")
		);
	}
	
	public Button(Screen screen, String UID, Vector2f position, Vector2f dimensions) {
		this(screen, UID, position, dimensions,
			screen.getStyle("Button").getVector4f("resizeBorders"),
			screen.getStyle("Button").getString("defaultImg")
		);
	}
	
	public Button(Screen screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg);
		
		this.setScaleNS(false);
		this.setScaleEW(false);
		this.setFontSize(screen.getStyle("Button").getFloat("fontSize"));
		this.setFontColor(screen.getStyle("Button").getColorRGBA("fontColor"));
		this.setTextVAlign(BitmapFont.VAlign.valueOf(screen.getStyle("Button").getString("textVAlign")));
		this.setTextAlign(BitmapFont.Align.valueOf(screen.getStyle("Button").getString("textAlign")));
		this.setTextWrap(LineWrapMode.valueOf(screen.getStyle("Button").getString("textWrap")));
		
		if (screen.getStyle("Button").getString("hoverImg") != null) {
			setButtonHoverInfo(
				screen.getStyle("Button").getString("hoverImg"),
				screen.getStyle("Button").getColorRGBA("hoverColor")
			);
		}
		if (screen.getStyle("Button").getString("pressedImg") != null) {
			setButtonPressedInfo(
				screen.getStyle("Button").getString("pressedImg"),
				screen.getStyle("Button").getColorRGBA("pressedColor")
			);
		}
		
		populateEffects("Button");
	}
	
	public void clearAltImages() {
		setButtonHoverInfo(null, null);
		setButtonPressedInfo(null, null);
	}
	
	public void setIsToggleButton(boolean isToggleButton) {
		this.isToggleButton = isToggleButton;
	}
	
	public boolean getIsToggleButton() {
		return this.isToggleButton;
	}
	
	public boolean getIsToggled() {
		return this.isToggled;
	}
	
	public final void setButtonHoverInfo(String pathHoverImg, ColorRGBA hoverFontColor) {
		if (pathHoverImg != null) {
			this.hoverImg = app.getAssetManager().loadTexture(pathHoverImg);
			this.hoverImg.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
			this.hoverImg.setMagFilter(Texture.MagFilter.Nearest);
			this.hoverImg.setWrap(Texture.WrapMode.Repeat);
		}
		if (hoverFontColor != null) {
			this.hoverFontColor = hoverFontColor;
		}
	}
	
	public Texture getButtonHoverImg() {
		return this.hoverImg;
	}
	
	public final void setButtonPressedInfo(String pathPressedImg, ColorRGBA pressedFontColor) {
		if (pathPressedImg != null) {
			this.pressedImg = app.getAssetManager().loadTexture(pathPressedImg);
			this.pressedImg.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
			this.pressedImg.setMagFilter(Texture.MagFilter.Nearest);
			this.pressedImg.setWrap(Texture.WrapMode.Repeat);
		}
		if (pressedFontColor != null) {
			this.pressedFontColor = pressedFontColor;
		}
	}
	
	public Texture getButtonPressedImg() {
		return this.pressedImg;
	}
	
	public void setButtonIcon(float width, float height, String texturePath) {
		if (icon != null) {
			if (icon.getParent() != null) {
				elementChildren.remove(icon.getUID());
				icon.removeFromParent();
			}
		}
		
		Texture tex = app.getAssetManager().loadTexture(texturePath);
		float imgWidth = tex.getImage().getWidth();
		tex = null;
		
		icon = new Element(
			screen,
			this.getUID() + ":btnIcon",
			new Vector2f((getWidth()/2)-(imgWidth/2),(getHeight()/2)-(imgWidth/2)),
			new Vector2f(width,height),
			new Vector4f(0,0,0,0),
			texturePath
		);
		icon.setIgnoreMouse(true);
		icon.setDockS(true);
		icon.setDockS(true);
		icon.setScaleEW(false);
		icon.setScaleNS(false);
		this.addChild(icon);
	}
	
	@Override
	public void onMouseLeftPressed(MouseButtonEvent evt) {
		if (isToggleButton) {
			if (isToggled) {
				isToggled = false;
			} else {
				isToggled = true;
			}
		}
		if (pressedImg != null) {
			Effect effect = getEffect(Effect.EffectEvent.Press);
			if (effect != null) {
				effect.setBlendImage(pressedImg);
				screen.getEffectManager().applyEffect(effect);
			}
		}
		if (pressedFontColor != null) {
			setFontColor(pressedFontColor);
		}
		isStillPressed = true;
		onMouseLeftDown(evt, isToggled);
	}

	@Override
	public void onMouseLeftReleased(MouseButtonEvent evt) {
		if (!isToggleButton) {
			if (hasFocus) {
				if (hoverImg != null) {
				//	screen.getEffectManager().removeEffect(this);
					Effect effect = getEffect(Effect.EffectEvent.Hover);
					if (effect != null) {
						effect.setBlendImage(hoverImg);
						screen.getEffectManager().applyEffect(effect);
					}
				}
				if (hoverFontColor != null) {
					setFontColor(hoverFontColor);
				}
			} else {
				Effect effect = getEffect(Effect.EffectEvent.LoseFocus);
				if (effect != null) {
					effect.setBlendImage(getElementTexture());
					screen.getEffectManager().applyEffect(effect);
				}
			}
		} else {
			if (!isToggled) {
				if (hoverImg != null) {
					Effect effect = getEffect(Effect.EffectEvent.Hover);
					if (effect != null) {
						effect.setBlendImage(hoverImg);
						screen.getEffectManager().applyEffect(effect);
					}
				}
				if (hoverFontColor != null) {
					setFontColor(hoverFontColor);
				}
			}
		}
		isStillPressed = false;
		onMouseLeftUp(evt, isToggled);
	}

	@Override
	public void onMouseRightPressed(MouseButtonEvent evt) {
	//	throw new UnsupportedOperationException("Not supported yet.");
		onMouseRightDown(evt, isToggled);
	}

	@Override
	public void onMouseRightReleased(MouseButtonEvent evt) {
	//	throw new UnsupportedOperationException("Not supported yet.");
		onMouseRightUp(evt, isToggled);
	}

	@Override
	public void onGetFocus(MouseMotionEvent evt) {
		if (!hasFocus) {
			if (!isToggled) {
				if (hoverImg != null) {
					Effect effect = getEffect(Effect.EffectEvent.Hover);
					if (effect != null) {
						effect.setBlendImage(hoverImg);
						screen.getEffectManager().applyEffect(effect);
					}
				}
				if (hoverFontColor != null) {
					setFontColor(hoverFontColor);
				}
			}
			onButtonFocus(evt);
		}
		hasFocus = true;
	}

	@Override
	public void onLoseFocus(MouseMotionEvent evt) {
		if (hasFocus) {
			if (!isToggled) {
				Effect effect = getEffect(Effect.EffectEvent.LoseFocus);
				if (effect != null) {
					effect.setBlendImage(getElementTexture());
					screen.getEffectManager().applyEffect(effect);
				}
				setFontColor(getFontColor());
			}
			onButtonLostFocus(evt);
		}
		hasFocus = false;
	}
	
	public abstract void onMouseLeftDown(MouseButtonEvent evt, boolean toggled);
	public abstract void onMouseRightDown(MouseButtonEvent evt, boolean toggled);
	public abstract void onMouseLeftUp(MouseButtonEvent evt, boolean toggled);
	public abstract void onMouseRightUp(MouseButtonEvent evt, boolean toggled);
	public abstract void onButtonFocus(MouseMotionEvent evt);
	public abstract void onButtonLostFocus(MouseMotionEvent evt);
	
	public boolean getIsStillPressed() {
		return this.isStillPressed;
	}
	
	@Override
	public Control cloneForSpatial(Spatial spatial) {
		return this;
	}

	@Override
	public void setSpatial(Spatial spatial) {
		this.spatial = spatial;
	}

	@Override
	public void render(RenderManager rm, ViewPort vp) {
		
	}
	
	@Override
	public void update(float tpf) {
		if (useInterval && isStillPressed) {
			currentTrack += tpf;
			if (currentTrack >= trackInterval) {
				onStillPressedInterval();
				currentTrack = 0;
			}
		}
	}
	
	public abstract void onStillPressedInterval();
	
	public void setInterval(float intervalsPerSecond) {
		if (intervalsPerSecond > 0) {
			this.useInterval = true;
			this.intervalsPerSecond = intervalsPerSecond;
			this.trackInterval = (float)(1/intervalsPerSecond);
			this.addControl(this);
		} else {
			this.useInterval = false;
			this.intervalsPerSecond = intervalsPerSecond;
			this.removeControl(Button.class);
		}
	}
}
