/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.buttons;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.LineWrapMode;
import com.jme3.font.Rectangle;
import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
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
import tonegod.gui.core.utils.BitmapTextUtil;
import tonegod.gui.effects.Effect;
import tonegod.gui.listeners.KeyboardListener;
import tonegod.gui.listeners.MouseButtonListener;
import tonegod.gui.listeners.MouseFocusListener;
import tonegod.gui.listeners.TabFocusListener;

/**
 *
 * @author t0neg0d
 */
public abstract class Button extends Element implements Control, MouseButtonListener, MouseFocusListener, KeyboardListener, TabFocusListener {
	String hoverSound, pressedSound;
	boolean useHoverSound, usePressedSound;
	float hoverSoundVolume, pressedSoundVolume;
	protected Element icon;
	protected Texture hoverImg = null, pressedImg = null;
	protected ColorRGBA hoverFontColor = null, pressedFontColor = null;
	protected boolean isToggleButton = false;
	protected boolean isToggled = false;
	private Spatial spatial;
	protected boolean isStillPressed = false;
	private boolean useInterval = false;
	private float intervalsPerSecond = 4;
	protected float trackInterval = (4/1000), currentTrack = 0;
	protected boolean initClickPause = false;
	protected float initClickInterval = 0.25f, currentInitClickTrack = 0;
	protected RadioButtonGroup radioButtonGroup = null;
	protected boolean isRadioButton = false;
	
	/**
	 * Creates a new instance of the Button control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 */
	public Button(Screen screen, String UID, Vector2f position) {
		this(screen, UID, position,
			screen.getStyle("Button").getVector2f("defaultSize"),
			screen.getStyle("Button").getVector4f("resizeBorders"),
			screen.getStyle("Button").getString("defaultImg")
		);
	}
	
	/**
	 * Creates a new instance of the Button control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 */
	public Button(Screen screen, String UID, Vector2f position, Vector2f dimensions) {
		this(screen, UID, position, dimensions,
			screen.getStyle("Button").getVector4f("resizeBorders"),
			screen.getStyle("Button").getString("defaultImg")
		);
	}
	
	/**
	 * Creates a new instance of the Button control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 * @param resizeBorders A Vector4f containg the border information used when resizing the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg The default image to use for the Slider's track
	 */
	public Button(Screen screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg);
		
		this.setScaleNS(false);
		this.setScaleEW(false);
		this.setFontSize(screen.getStyle("Button").getFloat("fontSize"));
		this.setFontColor(screen.getStyle("Button").getColorRGBA("fontColor"));
		this.setTextVAlign(BitmapFont.VAlign.valueOf(screen.getStyle("Button").getString("textVAlign")));
		this.setTextAlign(BitmapFont.Align.valueOf(screen.getStyle("Button").getString("textAlign")));
		this.setTextWrap(LineWrapMode.valueOf(screen.getStyle("Button").getString("textWrap")));
		
		this.setMinDimensions(dimensions.clone());
		
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
	
		hoverSound = screen.getStyle("Button").getString("hoverSound");
		useHoverSound = screen.getStyle("Button").getBoolean("useHoverSound");
		hoverSoundVolume = screen.getStyle("Button").getFloat("hoverSoundVolume");
		pressedSound = screen.getStyle("Button").getString("pressedSound");
		usePressedSound = screen.getStyle("Button").getBoolean("usePressedSound");
		pressedSoundVolume = screen.getStyle("Button").getFloat("pressedSoundVolume");
		
		populateEffects("Button");
	}
	
	/**
	 * Clears current hover and pressed images set by Style defines
	 */
	public void clearAltImages() {
		setButtonHoverInfo(null, null);
		setButtonPressedInfo(null, null);
	}
	
	/**
	 * Sets if the button is to interact as a Toggle Button
	 * Click once to activate / Click once to deactivate
	 * 
	 * @param isToggleButton boolean
	 */
	public void setIsToggleButton(boolean isToggleButton) {
		this.isToggleButton = isToggleButton;
	}
	
	/**
	 * Returns if the Button is flagged as a Toggle Button
	 * 
	 * @return boolean isToggleButton
	 */
	public boolean getIsToggleButton() {
		return this.isToggleButton;
	}
	
	/**
	 * Sets if the button is to interact as a Radio Button
	 * Click once to activate - stays active
	 * 
	 * @param isRadioButton boolean
	 */
	public void setIsRadioButton(boolean isRadioButton) {
		this.isRadioButton = isRadioButton;
	}
	
	/**
	 * Returns if the Button is flagged as a Toggle Button
	 * 
	 * @return boolean isRadioButton
	 */
	public boolean getIsRadioButton() {
		return this.isRadioButton;
	}
	
	/**
	 * Set a toggle button state to toggled/untoggled
	 * @param isToggled boolean
	 */
	public void setIsToggled(boolean isToggled) {
		this.isToggled = isToggled;
		
		if (pressedImg != null && isToggled) {
			Effect effect = getEffect(Effect.EffectEvent.Press);
			if (effect != null) {
				effect.setBlendImage(pressedImg);
				screen.getEffectManager().applyEffect(effect);
			}
			if (pressedFontColor != null) {
				setFontColor(pressedFontColor);
			}
		} else {
			Effect effect = getEffect(Effect.EffectEvent.Press);
			if (effect != null) {
				effect.setBlendImage(getElementTexture());
				screen.getEffectManager().applyEffect(effect);
			}
			if (pressedFontColor != null) {
				setFontColor(pressedFontColor);
			}
		}
		
		MouseButtonEvent evtd = new MouseButtonEvent(0,true,0,0);
		MouseButtonEvent evtu = new MouseButtonEvent(0,false,0,0);
		onButtonMouseLeftDown(evtd, isToggled);
		onButtonMouseLeftUp(evtu, isToggled);
		if (radioButtonGroup != null) {
			if (isToggled)
				radioButtonGroup.setSelected(this);
		}
		evtu.setConsumed();
		evtd.setConsumed();
	}
	
	/**
	 * Returns the current toggle state of the Button if the Button has been flagged as 
	 * isToggle
	 * @return boolean isToggle
	 */
	public boolean getIsToggled() {
		return this.isToggled;
	}
	
	/**
	 * Sets the texture image path and color to use when the button has mouse focus
	 * @param pathHoverImg String path to image for mouse focus event
	 * @param hoverFontColor ColorRGBA to use for mouse focus event
	 */
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
	
	/**
	 * Returns the Texture used when button has mouse focus
	 * 
	 * @return Texture
	 */
	public Texture getButtonHoverImg() {
		return this.hoverImg;
	}
	
	/**
	 * Sets the image and font color to use when the button is depressed
	 * @param pathPressedImg Path to the image for pressed state
	 * @param pressedFontColor ColorRGBA to use for pressed state
	 */
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
	
	/**
	 * Returns the texture to be used when the button is depressed
	 * 
	 * @return Texture
	 */
	public Texture getButtonPressedImg() {
		return this.pressedImg;
	}
	
	/**
	 * If called, an overlay icon is added to the button.  This icon is centered by default
	 * 
	 * @param width width to display icon
	 * @param height to display icon
	 * @param texturePath The path of the image to use as the icon overlay
	 */
	public void setButtonIcon(float width, float height, String texturePath) {
		if (icon != null) {
			if (icon.getParent() != null) {
				elementChildren.remove(icon.getUID());
				icon.removeFromParent();
			}
		}
		
	//	Texture tex = app.getAssetManager().loadTexture(texturePath);
	//	float imgWidth = tex.getImage().getWidth();
	//	tex = null;
		
		icon = new Element(
			screen,
			this.getUID() + ":btnIcon",
			new Vector2f((getWidth()/2)-(width/2),(getHeight()/2)-(height/2)),
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
				if (!isRadioButton) isToggled = false;
			} else {
				isToggled = true;
			}
		}
		if (pressedImg != null) {
			Effect effect = getEffect(Effect.EffectEvent.Press);
			if (effect != null) {
				if (usePressedSound && screen.getUseUIAudio()) {
					effect.setAudioFile(pressedSound);
					effect.setAudioVolume(pressedSoundVolume);
				}
				effect.setBlendImage(pressedImg);
				screen.getEffectManager().applyEffect(effect);
			}
		}
		if (pressedFontColor != null) {
			setFontColor(pressedFontColor);
		}
		isStillPressed = true;
		initClickPause = true;
		currentInitClickTrack = 0;
		onButtonMouseLeftDown(evt, isToggled);
		evt.setConsumed();
	}

	@Override
	public void onMouseLeftReleased(MouseButtonEvent evt) {
		if (!isToggleButton) {
			if (getHasFocus()) {
				Effect effect = getEffect(Effect.EffectEvent.LoseFocus);
				if (effect != null) {
					effect.setBlendImage(getElementTexture());
					screen.getEffectManager().applyEffect(effect);
				}
				if (hoverImg != null) {
				//	screen.getEffectManager().removeEffect(this);
					Effect effect2 = getEffect(Effect.EffectEvent.Hover);
					if (effect2 != null) {
						effect2.setBlendImage(hoverImg);
						screen.getEffectManager().applyEffect(effect2);
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
					Effect effect = getEffect(Effect.EffectEvent.LoseFocus);
					if (effect != null) {
						effect.setBlendImage(getElementTexture());
						screen.getEffectManager().applyEffect(effect);
					}
					Effect effect2 = getEffect(Effect.EffectEvent.Hover);
					if (effect2 != null) {
						effect2.setBlendImage(hoverImg);
						screen.getEffectManager().applyEffect(effect2);
					}
				}
				if (hoverFontColor != null) {
					setFontColor(hoverFontColor);
				}
			}
		}
		isStillPressed = false;
		initClickPause = false;
		currentInitClickTrack = 0;
		onButtonMouseLeftUp(evt, isToggled);
		if (radioButtonGroup != null)
			radioButtonGroup.setSelected(this);
		evt.setConsumed();
	}
	
	public void setRadioButtonGroup(RadioButtonGroup radioButtonGroup) {
		this.radioButtonGroup = radioButtonGroup;
		this.isToggleButton = true;
		this.isRadioButton = true;
	}
	
	@Override
	public void onMouseRightPressed(MouseButtonEvent evt) {
	//	throw new UnsupportedOperationException("Not supported yet.");
		onButtonMouseRightDown(evt, isToggled);
		if (screen.getUseToolTips()) {
			if (getToolTipText() !=  null) {
			//	screen.setToolTip(null);
			}
		}
		evt.setConsumed();
	}

	@Override
	public void onMouseRightReleased(MouseButtonEvent evt) {
	//	throw new UnsupportedOperationException("Not supported yet.");
		onButtonMouseRightUp(evt, isToggled);
		evt.setConsumed();
	}

	@Override
	public void onGetFocus(MouseMotionEvent evt) {
		if (!getHasFocus()) {
			if (!isToggled) {
				if (hoverImg != null) {
					Effect effect = getEffect(Effect.EffectEvent.Hover);
					if (effect != null) {
						if (useHoverSound && screen.getUseUIAudio()) {
							effect.setAudioFile(hoverSound);
							effect.setAudioVolume(hoverSoundVolume);
						}
						effect.setBlendImage(hoverImg);
						screen.getEffectManager().applyEffect(effect);
					}
				}
				if (hoverFontColor != null) {
					setFontColor(hoverFontColor);
				}
			}
			screen.setCursor(Screen.CursorType.HAND);
			onButtonFocus(evt);
			if (screen.getUseToolTips()) {
				if (getToolTipText() !=  null) {
				//	screen.setToolTip(getToolTipText());
				}
			}
		}
		setHasFocus(true);
	}

	@Override
	public void onLoseFocus(MouseMotionEvent evt) {
		if (getHasFocus()) {
			if (!isToggled) {
				Effect effect = getEffect(Effect.EffectEvent.LoseFocus);
				if (effect != null) {
					effect.setBlendImage(getElementTexture());
					screen.getEffectManager().applyEffect(effect);
				}
				setFontColor(getFontColor());
			}
			screen.setCursor(Screen.CursorType.POINTER);
			onButtonLostFocus(evt);
			/*
			if (screen.getUseToolTips()) {
				if (getToolTipText() !=  null) {
					screen.setToolTip(null);
				}
			}
			*/
		}
		setHasFocus(false);
	}
	
	/**
	 * Enables/disbale hover effect sound
	 * @param useHoverSound 
	 */
	public void setUseButtonHoverSound(boolean useHoverSound) {
		this.useHoverSound = useHoverSound;
	}
	
	/**
	 * Enable/disable pressed effect sound
	 * @param usePressedSound 
	 */
	public void setUseButtonPressedSound(boolean usePressedSound) {
		this.usePressedSound = usePressedSound;
	}
	
	public abstract void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled);
	public abstract void onButtonMouseRightDown(MouseButtonEvent evt, boolean toggled);
	public abstract void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled);
	public abstract void onButtonMouseRightUp(MouseButtonEvent evt, boolean toggled);
	public abstract void onButtonFocus(MouseMotionEvent evt);
	public abstract void onButtonLostFocus(MouseMotionEvent evt);
	/**
	 * Abstract method for handling interval updates while the button is still pressed
	 * 
	 * NOTE: This is only called if the button's setInterval method has been previously called
	 */
	public void onButtonStillPressedInterval() {  }
	
	/**
	 * Returns if the button is still pressed
	 * @return boolean
	 */
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
			if (initClickPause) {
				currentInitClickTrack += tpf;
				if (currentInitClickTrack >= initClickInterval) {
					initClickPause = false;
					currentInitClickTrack = 0;
				}
			} else {
				currentTrack += tpf;
				if (currentTrack >= trackInterval) {
					onButtonStillPressedInterval();
					currentTrack = 0;
				}
			}
		}
	}
	
	/**
	 * This method registers the button as a JME Control creating an interval event to be
	 * processed every time the interval limit has been reached.
	 * 
	 * See onButtonStillPressedInterval()
	 * 
	 * @param intervalsPerSecond The number of calls to onButtonStillPressedInterval per second.
	 */
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
	
	// Tab focus
	@Override
	public void setTabFocus() {
		screen.setKeyboardElemeent(this);
		Effect effect = getEffect(Effect.EffectEvent.TabFocus);
		if (effect != null) {
		//	System.out.println(getUID() + ": Effect Found");
			effect.setColor(ColorRGBA.DarkGray);
			screen.getEffectManager().applyEffect(effect);
		}
	}
	
	@Override
	public void resetTabFocus() {
		screen.setKeyboardElemeent(null);
		Effect effect = getEffect(Effect.EffectEvent.LoseTabFocus);
		if (effect != null) {
			effect.setColor(ColorRGBA.White);
			screen.getEffectManager().applyEffect(effect);
		}
	}
	
	// Default keyboard interaction
	@Override
	public void onKeyPress(KeyInputEvent evt) {
		if (evt.getKeyCode() == KeyInput.KEY_SPACE) {
			onMouseLeftPressed(new MouseButtonEvent(0,true,0,0));
		}
	}
	
	@Override
	public void onKeyRelease(KeyInputEvent evt) {
		if (evt.getKeyCode() == KeyInput.KEY_SPACE) {
			onMouseLeftReleased(new MouseButtonEvent(0,false,0,0));
		}
	}
	
	@Override
	public void setText(String text) {
		this.text = text;
		if (textElement == null) {
			textElement = new BitmapText(font, false);
			textElement.setBox(new Rectangle(0,0,getDimensions().x,getDimensions().y));
		}
		textElement.setLineWrapMode(textWrap);
		textElement.setAlignment(textAlign);
		textElement.setVerticalAlignment(textVAlign);
		textElement.setSize(fontSize);
		textElement.setColor(fontColor);
		
		if (textVAlign == BitmapFont.VAlign.Center) {
			textElement.setVerticalAlignment(BitmapFont.VAlign.Top);
			centerTextVertically(text);
		}
		
		textElement.setText(text);
		updateTextElement();
		if (textElement.getParent() == null) {
			this.attachChild(textElement);
		//	textElement.move(0,0,getNextZOrder());
		}
	}
	/**
	 * Fix for BitmapFont.VAlign
	 * @param text 
	 */
	private void centerTextVertically(String text) {
		float height = BitmapTextUtil.getTextLineHeight(this, text);
		setTextPosition(getTextPosition().x, getHeight()/2-((height-(height*.1f))/2));
	}
}
