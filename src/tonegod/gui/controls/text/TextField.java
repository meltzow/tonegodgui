/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.text;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.LineWrapMode;
import com.jme3.font.Rectangle;
import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import java.util.ArrayList;
import java.util.List;
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
public class TextField extends Element implements KeyboardListener, TabFocusListener, MouseFocusListener, MouseButtonListener {

	public static enum Type {
		DEFAULT,
		ALPHA,
		ALPHA_NOSPACE,
		NUMERIC,
		ALPHANUMERIC,
		ALPHANUMERIC_NOSPACE,
		EXCLUDE_SPECIAL,
		EXCLUDE_CUSTOM,
		INCLUDE_CUSTOM
	};
	private String validateAlpha = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ ";
	private String validateAlphaNoSpace = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private String validateNumeric = "0123456789.";
	private String validateSpecChar = "`~!@#$%^&*()-_=+[]{}\\|;:'\",<.>/?";
	private String validateCustom = "";
	private String testString = "Gg|/X";
	private Element caret;
	private Material caretMat;
	protected int caretIndex = 0, head = 0, tail = 0;
	protected int rangeHead = -1, rangeTail = -1;
	protected int visibleHead = -1, visibleTail = -1;
	protected List<String> textFieldText = new ArrayList();
	protected String finalText = "", visibleText = "", textRangeText = "";
	protected BitmapText widthTest;
	private boolean hasTabFocus = false;
	protected float caretX = 0;
	private char searchStr = ' ';
	private Type type = Type.DEFAULT;
	private boolean ctrl = false, shift = false, alt = false;
	private boolean isEnabled = true;
	private boolean forceUpperCase = false, forceLowerCase = false;
	private int maxLength = 0;
	private String nextChar;
	private boolean valid;
	private boolean copy = true, paste = true;
	float lastClick = 0;
	
	/**
	 * Creates a new instance of the TextField control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 */
	public TextField(Screen screen, String UID, Vector2f position) {
		this(screen, UID, position,
			screen.getStyle("TextField").getVector2f("defaultSize"),
			screen.getStyle("TextField").getVector4f("resizeBorders"),
			screen.getStyle("TextField").getString("defaultImg")
		);
	}
	
	/**
	 * Creates a new instance of the TextField control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 */
	public TextField(Screen screen, String UID, Vector2f position, Vector2f dimensions) {
		this(screen, UID, position, dimensions,
			screen.getStyle("TextField").getVector4f("resizeBorders"),
			screen.getStyle("TextField").getString("defaultImg")
		);
	}
	
	/**
	 * Creates a new instance of the TextField control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 * @param resizeBorders A Vector4f containg the border information used when resizing the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg The default image to use for the Slider's track
	 */
	public TextField(Screen screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg);
		
		this.setScaleEW(true);
		this.setScaleNS(false);
		this.setDockN(true);
		this.setDockW(true);
		
		float padding = screen.getStyle("TextField").getFloat("textPadding");
		
		this.setFontSize(screen.getStyle("TextField").getFloat("fontSize"));
		this.setTextPadding(padding);
		this.setTextWrap(LineWrapMode.valueOf(screen.getStyle("TextField").getString("textWrap")));
		this.setTextAlign(BitmapFont.Align.valueOf(screen.getStyle("TextField").getString("textAlign")));
		this.setTextVAlign(BitmapFont.VAlign.valueOf(screen.getStyle("TextField").getString("textVAlign")));
		
		this.setMinDimensions(dimensions.clone());
		
		caret = new Element(screen, UID + ":Caret", new Vector2f(padding,padding), new Vector2f(dimensions.x-(padding*2), dimensions.y-(padding*2)), new Vector4f(0,0,0,0), null);
		
		caretMat = caret.getMaterial().clone();
		caretMat.setBoolean("IsTextField", true);
		caretMat.setTexture("ColorMap", null);
		caretMat.setColor("Color", getFontColor());
		
		caret.setLocalMaterial(caretMat);
		caret.setIgnoreMouse(true);
	//	caret.setlockToParentBounds(true);
		caret.setScaleEW(true);
		caret.setScaleNS(false);
		caret.setDockS(true);
		caret.setDockW(true);
		
		setTextFieldFontColor(screen.getStyle("TextField").getColorRGBA("fontColor"));
		this.addChild(caret);
		
		this.setText("");
		
		populateEffects("TextField");
	}

	// Validation
	/**
	 * Sets the TextField.Type of the text field.  This can be used to enfoce rules on the inputted text
	 * @param type Type
	 */
	public void setType(Type type) {
		this.type = type;
	}
	
	/**
	 * Returns the current Type of the TextField
	 * @return Type
	 */
	public Type getType() {
		return this.type;
	}
	
	/**
	 * Sets a custom validation rule for the TextField.
	 * @param grabBag String A list of character to either allow or diallow as input
	 */
	public void setCustomValidation(String grabBag) {
		validateCustom = grabBag;
	}
	
	/**
	 * Attempts to parse an int from the inputted text of the TextField
	 * @return int
	 * @throws NumberFormatException 
	 */
	public int parseInt() throws NumberFormatException {
		return Integer.parseInt(getText());
	}
	
	/**
	 * Attempts to parse a float from the inputted text of the TextField
	 * @return float
	 * @throws NumberFormatException 
	 */
	public float parseFloat() throws NumberFormatException {
		return Float.parseFloat(getText());
	}
	
	/**
	 * Attempts to parse a short from the inputted text of the TextField
	 * @return short
	 * @throws NumberFormatException 
	 */
	public short parseShort() throws NumberFormatException {
		return Short.parseShort(getText());
	}
	
	/**
	 * Attempts to parse a double from the inputted text of the TextField
	 * @return double
	 * @throws NumberFormatException 
	 */
	public double parseDouble() throws NumberFormatException {
		return Double.parseDouble(getText());
	}
	
	/**
	 * Attempts to parse a long from the inputted text of the TextField
	 * @return long
	 * @throws NumberFormatException 
	 */
	public long parseLong() throws NumberFormatException {
		return Long.parseLong(getText());
	}
	
	// Interaction
	@Override
	public void onKeyPress(KeyInputEvent evt) {
		if (evt.getKeyCode() == KeyInput.KEY_LMETA || evt.getKeyCode() == KeyInput.KEY_RMETA ||
			evt.getKeyCode() == KeyInput.KEY_F1 || evt.getKeyCode() == KeyInput.KEY_F2 ||
			evt.getKeyCode() == KeyInput.KEY_F3 || evt.getKeyCode() == KeyInput.KEY_F4 ||
			evt.getKeyCode() == KeyInput.KEY_F5 || evt.getKeyCode() == KeyInput.KEY_F6 ||
			evt.getKeyCode() == KeyInput.KEY_F7 || evt.getKeyCode() == KeyInput.KEY_F8 ||
			evt.getKeyCode() == KeyInput.KEY_F9 || evt.getKeyCode() == KeyInput.KEY_CAPITAL ||
			evt.getKeyCode() == KeyInput.KEY_ESCAPE || evt.getKeyCode() == KeyInput.KEY_TAB) {
		} else if (evt.getKeyCode() == KeyInput.KEY_LCONTROL || evt.getKeyCode() == KeyInput.KEY_RCONTROL) {
			ctrl = true;
		} else if (evt.getKeyCode() == KeyInput.KEY_LSHIFT || evt.getKeyCode() == KeyInput.KEY_RSHIFT) {
			shift = true;
		} else if (evt.getKeyCode() == KeyInput.KEY_LMENU || evt.getKeyCode() == KeyInput.KEY_RMENU) {
			alt = true;
		} else if (evt.getKeyCode() == KeyInput.KEY_BACK) {
			if (caretIndex > 0) {
				textFieldText.remove(caretIndex-1);
				caretIndex--;
			}
		//	updateTextElement();
		} else if (evt.getKeyCode() == KeyInput.KEY_LEFT) {
			if (!shift) resetTextRange();
			if (caretIndex > -1) {
				if (!ctrl)
					caretIndex--;
				else {
					int cIndex = caretIndex;
					if (cIndex > 0)
						if (finalText.charAt(cIndex-1) == ' ')
							cIndex--;
					int index = 0;
					if (cIndex > 0) index = finalText.substring(0,cIndex).lastIndexOf(' ');;
					if (index < 0)	index = 0;
					caretIndex = index;
				}
				if (caretIndex < 0)
					caretIndex = 0;
				
				if (!shift) setTextRangeStart(caretIndex);
			}
		} else if (evt.getKeyCode() == KeyInput.KEY_RIGHT) {
			if (!shift) resetTextRange();
			if (caretIndex <= textFieldText.size()) {
				if (!ctrl)
					caretIndex++;
				else {
					int cIndex = caretIndex;
					if (cIndex < finalText.length())
						if (finalText.charAt(cIndex) == ' ')
							cIndex++;
					int index = finalText.length();
					if (cIndex < finalText.length()) {
						index = finalText.substring(cIndex, finalText.length()).indexOf(' ');
						if (index == -1)	index = finalText.length();
						else				index += cIndex;
					} else {
						index = finalText.length();
					}
					caretIndex = index;
				}
				if (caretIndex > finalText.length())
					caretIndex = finalText.length();
				
				if (!shift) {
					if (caretIndex < textFieldText.size())	setTextRangeStart(caretIndex);
					else									setTextRangeStart(textFieldText.size());
				}
			}
		} else if (evt.getKeyCode() == KeyInput.KEY_END || evt.getKeyCode() == KeyInput.KEY_NEXT || evt.getKeyCode() == KeyInput.KEY_DOWN) {
			caretIndex = textFieldText.size();
		} else if (evt.getKeyCode() == KeyInput.KEY_HOME || evt.getKeyCode() == KeyInput.KEY_PRIOR || evt.getKeyCode() == KeyInput.KEY_UP) {
			caretIndex = 0;
		} else {
			if (ctrl) {
				if (evt.getKeyCode() == KeyInput.KEY_C) {
					if (copy)
						screen.setClipboardText(textRangeText);
				} else if (evt.getKeyCode() == KeyInput.KEY_V) {
					if (paste)
						this.pasteTextInto();
				}
			} else {
				if (isEnabled) {
					if (rangeHead != -1 && rangeTail != -1) {
						editTextRangeText("");
					}
					nextChar = String.valueOf(evt.getKeyChar());
					if (forceUpperCase)			nextChar = nextChar.toUpperCase();
					else if (forceLowerCase)	nextChar = nextChar.toLowerCase();
					valid = true;
					if (maxLength > 0) {
						if (getText().length() >= maxLength) valid =false;
					}
					if (valid) {
						if (type == Type.DEFAULT) {
							textFieldText.add(caretIndex, nextChar);
							caretIndex++;
						} else if (type == Type.ALPHA) {
							if (validateAlpha.indexOf(nextChar) != -1) {
								textFieldText.add(caretIndex, nextChar);
								caretIndex++;
							}
						} else if (type == Type.ALPHA_NOSPACE) {
							if (validateAlpha.indexOf(nextChar) != -1) {
								textFieldText.add(caretIndex, nextChar);
								caretIndex++;
							}
						} else if (type == Type.NUMERIC) {
							if (validateNumeric.indexOf(nextChar) != -1) {
								textFieldText.add(caretIndex, nextChar);
								caretIndex++;
							}
						} else if (type == Type.ALPHANUMERIC) {
							if (validateAlpha.indexOf(nextChar) != -1 || validateNumeric.indexOf(nextChar) != -1) {
								textFieldText.add(caretIndex, nextChar);
								caretIndex++;
							}
						} else if (type == Type.ALPHANUMERIC_NOSPACE) {
							if (validateAlphaNoSpace.indexOf(nextChar) != -1 || validateNumeric.indexOf(nextChar) != -1) {
								textFieldText.add(caretIndex, nextChar);
								caretIndex++;
							}
						} else if (type == Type.EXCLUDE_SPECIAL) {
							if (validateSpecChar.indexOf(nextChar) == -1) {
								textFieldText.add(caretIndex, nextChar);
								caretIndex++;
							}
						} else if (type == Type.EXCLUDE_CUSTOM) {
							if (validateCustom.indexOf(nextChar) == -1) {
								textFieldText.add(caretIndex, nextChar);
								caretIndex++;
							}
						} else if (type == Type.INCLUDE_CUSTOM) {
							if (validateCustom.indexOf(nextChar) != -1) {
								textFieldText.add(caretIndex, nextChar);
								caretIndex++;
							}
						}
					}
				}
			}
		}
		this.setText(getVisibleText());
		
		if (shift && (evt.getKeyCode() == KeyInput.KEY_LEFT || evt.getKeyCode() == KeyInput.KEY_RIGHT)) setTextRangeEnd(caretIndex);
		
		centerTextVertically();
		
		controlKeyPressHook(evt, getText());
		evt.setConsumed();
	}
	
	/**
	 * An overridable hook for the onKeyPress event of the TextField
	 * @param evt KeyInputEvent
	 * @param text String
	 */
	public void controlKeyPressHook(KeyInputEvent evt, String text) {  }
	
	@Override
	public void onKeyRelease(KeyInputEvent evt) {
		if (evt.getKeyCode() == KeyInput.KEY_LCONTROL || evt.getKeyCode() == KeyInput.KEY_RCONTROL) {
			ctrl = false;
		} else if (evt.getKeyCode() == KeyInput.KEY_LSHIFT || evt.getKeyCode() == KeyInput.KEY_RSHIFT) {
			shift = false;
		} else if (evt.getKeyCode() == KeyInput.KEY_LMENU || evt.getKeyCode() == KeyInput.KEY_RMENU) {
			alt = false;
		}
		evt.setConsumed();
	}
	
	/**
	 * Internal use - NEVER USE THIS!!
	 */
	protected void getTextFieldText() {
		String ret = "";
		int index = 0;
		for (String s : textFieldText) {
			ret += s;
			index++;
		}
		finalText = ret;
	}
	
	/**
	 * This method should be used in place of setText.
	 * @param s String The text to set for the TextField
	 */
	public void setTextFieldText(String s) {
		caretIndex = 0;
		textFieldText.clear();
		for (int i = 0; i < s.length(); i++) {
			textFieldText.add(caretIndex, String.valueOf(s.charAt(i)));
			caretIndex++;
		}
		this.setText(getVisibleText());
		
		setCaretPositionToEnd();
		
		centerTextVertically();
	}
	
	@Override
	public final void setFontSize(float fontSize) {
		this.fontSize = fontSize;
		
	//	widthTest = new BitmapText(font, false);
	//	widthTest.setBox(null);
	//	widthTest.setSize(getFontSize());
	//	widthTest.setText(testString);
		
	
		
		if (textElement != null) {
			textElement.setSize(fontSize);
		}
	}
	
	@Override
	public String getText() {
		String ret = "";
		int index = 0;
		for (String s : textFieldText) {
			ret += s;
			index++;
		}
		return ret;
	}
	
	/**
	 * Returns the visible portion of the TextField's text
	 * @return String
	 */
	protected String getVisibleText() {
		getTextFieldText();
		
		widthTest = new BitmapText(font, false);
		widthTest.setBox(null);
		widthTest.setSize(getFontSize());

		int index1 = 0, index2;
		widthTest.setText(finalText.substring(index1));
		while(widthTest.getLineWidth() > getWidth()) {
			if (index1 == caretIndex)
				break;
			index1++;
			widthTest.setText(finalText.substring(index1));
		}

		index2 = finalText.length()-1;
		if (index2 == caretIndex && caretIndex != textFieldText.size()) {
			index2 = caretIndex+1;
			widthTest.setText(finalText.substring(index1, index2));
			while(widthTest.getLineWidth() < getWidth()) {
				if (index2 == textFieldText.size())
					break;
				index2++;
				widthTest.setText(finalText.substring(index1, index2));
			}
		}
		if (index2 != textFieldText.size()) index2++;
		
		if (head != index1 || tail != index2) {
			head = index1;
			tail = index2;
		}
		if (head != tail && head != -1 && tail != -1)
			visibleText = finalText.substring(head, tail);
		else
			visibleText = "";

		widthTest.setText(finalText.substring(head, caretIndex));
		caretX = widthTest.getLineWidth();
		setCaretPosition(getAbsoluteX()+caretX);
		
		return visibleText;
	}
	
	/**
	 * For internal use - do not call this method
	 * @param caretX float
	 */
	protected void setCaretPosition(float caretX) {
		if (textElement != null) {
			if (hasTabFocus) {
				caret.getMaterial().setFloat("CaretX", caretX+getTextPadding());
				caret.getMaterial().setFloat("LastUpdate", app.getTimer().getTimeInSeconds());
			}
		}
	}
	
	/**
	 * For internal use - do not call this method
	 * @param x float
	 */
	public void setCaretPositionByX(float x) {
		int index1 = visibleText.length();
		if (visibleText.length() > 0) {
			widthTest.setSize(getFontSize());
			widthTest.setText(visibleText.substring(0, index1));
			while(caret.getAbsoluteX()+widthTest.getLineWidth() > (x+getTextPadding())) {
				index1--;
				widthTest.setText(visibleText.substring(0, index1));
			}
			caretX = widthTest.getLineWidth();
		}
		caretIndex = head+index1;
		setCaretPosition(getAbsoluteX()+caretX);
		if (!shift) {
			resetTextRange();
			setTextRangeStart(caretIndex);
		} else {
			setTextRangeEnd(caretIndex);
		}
	}
	
	/**
	 * Sets the caret position to the end of the TextField's text
	 */
	public void setCaretPositionToEnd() {
		int index1 = visibleText.length();
		if (visibleText.length() > 0) {
			widthTest.setText(visibleText.substring(0, index1));
			caretX = widthTest.getLineWidth();
		}
		caretIndex = head+index1;
		setCaretPosition(getAbsoluteX()+caretX);
		resetTextRange();
	}
	
	private void pasteTextInto() {
		String text = screen.getClipboardText();
		editTextRangeText(text);
	}
	
	/**
	 * Sets the ColorRGBA value used for text & caret
	 * @param fontColor ColorRGBA
	 */
	public final void setTextFieldFontColor(ColorRGBA fontColor) {
		setFontColor(fontColor);
		caretMat.setColor("Color", fontColor);
	}
	
	/**
	 * Enables/disables the TextField
	 * @param isEnabled boolean
	 */
	public void setIsEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	
	/**
	 * Returns if the TextField is currently enabled/disabled
	 * @return boolean
	 */
	public boolean getIsEnabled() {
		return this.isEnabled;
	}
	
	/**
	 * Enables/disables the use of the Copy text feature
	 * @param copy boolean
	 */
	public void setAllowCopy(boolean copy) {
		this.copy = copy;
	}
	
	/**
	 * Returns if the Copy feature is enabled/disabled
	 * @return copy
	 */
	public boolean getAllowCopy() {
		return this.copy;
	}
	
	/**
	 * Eanbles/disables use of the Paste text feature
	 * @param paste boolean
	 */
	public void setAllowPaste(boolean paste) {
		this.paste = paste;
	}
	
	/**
	 * Returns if the Paste feature is enabled/disabled
	 * @return paste
	 */
	public boolean getAllowPaste() {
		return this.paste;
	}
	
	/**
	 * Enables/disables both the Copy and Paste feature
	 * @param copyAndPaste boolean
	 */
	public void setAllowCopyAndPaste(boolean copyAndPaste) {
		this.copy = copyAndPaste;
		this.paste = copyAndPaste;
	}
	
	/**
	 * Forces all text input to uppercase
	 * @param forceUpperCase boolean
	 */
	public void setForceUpperCase(boolean forceUpperCase) {
		this.forceUpperCase = forceUpperCase;
		this.forceLowerCase = false;
	}
	
	/**
	 * Returns if the TextField is set to force uppercase
	 * @return boolean
	 */
	public boolean getForceUpperCase() {
		return this.forceUpperCase;
	}
	
	/**
	 * Forces all text input to lowercase
	 * @return boolean
	 */
	public void setForceLowerCase(boolean forceLowerCase) {
		this.forceLowerCase = forceLowerCase;
		this.forceUpperCase = false;
	}
	
	/**
	 * Returns if the TextField is set to force lowercase
	 * @return boolean
	 */
	public boolean getForceLowerCase() {
		return this.forceLowerCase;
	}
	
	/**
	 * Set the maximum character limit for the TextField.  0 = unlimited
	 * @param maxLength int
	 */
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}
	
	/**
	 * Returns the maximum limit of character allowed for this TextField
	 * @return int
	 */
	public int getMaxLength() {
		return this.maxLength;
	}
	
	@Override
	public void setTabFocus() {
		hasTabFocus = true;
		setTextRangeStart(caretIndex);
		if (isEnabled)
			caret.getMaterial().setBoolean("HasTabFocus", true);
		screen.setKeyboardElemeent(this);
		controlTextFieldSetTabFocusHook();
		Effect effect = getEffect(Effect.EffectEvent.TabFocus);
		if (effect != null) {
			effect.setColor(ColorRGBA.DarkGray);
			screen.getEffectManager().applyEffect(effect);
		}
	}
	
	@Override
	public void resetTabFocus() {
		hasTabFocus = false;
		shift = false;
		ctrl = false;
		alt = false;
		caret.getMaterial().setBoolean("HasTabFocus", false);
		screen.setKeyboardElemeent(null);
		controlTextFieldResetTabFocusHook();
		Effect effect = getEffect(Effect.EffectEvent.LoseTabFocus);
		if (effect != null) {
			effect.setColor(ColorRGBA.White);
			screen.getEffectManager().applyEffect(effect);
		}
	}
	
	/**
	 * Overridable hook for receive tab focus event
	 */
	public void controlTextFieldSetTabFocusHook() {  }
	
	/**
	 * Overridable hook for lose tab focus event
	 */
	public void controlTextFieldResetTabFocusHook() {  }

	@Override
	public void onGetFocus(MouseMotionEvent evt) {
		if (getIsEnabled()) screen.setCursor(Screen.CursorType.TEXT);
		setHasFocus(true);
	}

	@Override
	public void onLoseFocus(MouseMotionEvent evt) {
		if (getIsEnabled()) screen.setCursor(Screen.CursorType.POINTER);
		setHasFocus(false);
	}
	
	@Override
	public final void setText(String text) {
		this.text = text;
		if (textElement == null) {
			textElement = new BitmapText(font, false);
			textElement.setBox(new Rectangle(0,0,getDimensions().x,getDimensions().y));
			centerTextVertically();
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
		centerTextVertically();
	}
	
	private void centerTextVertically() {
		
		float height = BitmapTextUtil.getTextLineHeight(this, testString);
		float nextY = height-FastMath.floor(getHeight());
		nextY /= 2;
		nextY = (float)FastMath.ceil(nextY)+1;
		
		setTextPosition(getTextPosition().x, -nextY);
	}
	
	@Override
	public void onMouseLeftPressed(MouseButtonEvent evt) {
		float time = screen.getApplication().getTimer().getTimeInSeconds();
		float diff;
		if (lastClick != 0) {
			diff = time-lastClick;
			if (diff > 0.5f) {
				lastClick = 0;
			}
		}
		
		if (lastClick == 0)
			lastClick = time;
		else {
			diff = time-lastClick;
			if (diff < 0.2f) {
				// Double Click Madness!
				if (this.isEnabled)
					selectTextRangeDoubleClick();
			}
			lastClick = 0;
		}
	}

	@Override
	public void onMouseLeftReleased(MouseButtonEvent evt) {  }

	@Override
	public void onMouseRightPressed(MouseButtonEvent evt) {  }

	@Override
	public void onMouseRightReleased(MouseButtonEvent evt) {  }
	
	// Text Range methods
	/**
	 * Sets the current text range to all text within the TextField
	 */
	public void selectTextRangeAll() {
		setTextRangeStart(0);
		setTextRangeEnd(finalText.length());
		caretIndex = finalText.length();
		getVisibleText();
	}
	
	/**
	 * Resets the current text range
	 */
	public void selectTextRangeNone() {
		this.resetTextRange();
	}
	
	/**
	 * Sets the current text range to the first instance of the provided string, if found
	 * @param s The String to search for
	 */
	public void selectTextRangeBySubstring(String s) {
		int head = finalText.indexOf(s);
		if (head != -1) {
			setTextRangeStart(head);
			int tail = head+s.length();
			setTextRangeEnd(tail);
			caretIndex = tail;
			getVisibleText();
		}
	}
	
	/**
	 * Sets the selected text range to head-tail or tail-head depending on the provided indexes.
	 * Selects nothing if either of the provided indexes are out of range
	 * @param head The start or end index of the desired text range
	 * @param tail The end or start index of the desired text range
	 */
	public void selectTextRangeByIndex(int head, int tail) {
		int nHead = head;
		int nTail = tail;
		if (head > tail) {
			nHead = tail;
			nTail = head;
		}
		if (nHead < 0) nHead = 0;
		if (nTail > finalText.length()) nTail = finalText.length();
		
		this.setTextRangeStart(nHead);
		this.setTextRangeEnd(nTail);
		caretIndex = nTail;
		getVisibleText();
	}
	
	private void selectTextRangeDoubleClick() {
		int end = caretIndex+finalText.substring(caretIndex, finalText.length()).indexOf(' ');
		int start = finalText.substring(0,caretIndex).lastIndexOf(' ')+1;
		if (start == -1) start = 0;
		setTextRangeStart(start);
		setTextRangeEnd(end);
		caretIndex = end;
		getVisibleText();
	}
	
	private void setTextRangeStart(int head) {
		if (!visibleText.equals("")) {
			rangeHead = head;
		}
	}
	
	private void setTextRangeEnd(int tail) {
		if (!visibleText.equals("") && rangeHead != -1) {
			widthTest.setSize(getFontSize());
			
			if (rangeHead-this.head <= 0)
				widthTest.setText("");
			else if(rangeHead-this.head < visibleText.length())
				widthTest.setText(visibleText.substring(0, rangeHead-this.head));
			else
				widthTest.setText(visibleText);
			
			float rangeX = getTextPadding();
			if (rangeHead >= this.head)
				rangeX = getAbsoluteX()+widthTest.getLineWidth()+getTextPadding();
			
			rangeTail = tail;
			if (tail-this.head <= 0)
				widthTest.setText("");
			else if (tail-this.head < visibleText.length())
				widthTest.setText(visibleText.substring(0, tail-this.head));
			else
				widthTest.setText(visibleText);
			
			textRangeText = (rangeHead < rangeTail) ? finalText.substring(rangeHead, rangeTail) : finalText.substring(rangeTail, rangeHead);
			
		//	System.out.println(textRangeText);
			
			float rangeW = getTextPadding();
			if (rangeTail <= this.tail)
				rangeW = getAbsoluteX()+widthTest.getLineWidth()+getTextPadding();
			
			if (rangeHead > rangeTail) {
				caret.getMaterial().setFloat("TextRangeStart", rangeW);
				caret.getMaterial().setFloat("TextRangeEnd", rangeX);
			} else {
				caret.getMaterial().setFloat("TextRangeStart", rangeX);
				caret.getMaterial().setFloat("TextRangeEnd", rangeW);
			}
			
			caret.getMaterial().setBoolean("ShowTextRange", true);
		}
	}
	
	private void resetTextRange() {
		textRangeText = "";
		rangeHead = -1;
		rangeTail = -1;
		caret.getMaterial().setFloat("TextRangeStart", 0);
		caret.getMaterial().setFloat("TextRangeEnd", 0);
		caret.getMaterial().setBoolean("ShowTextRange", false);
	}
	
	private void editTextRangeText(String insertText) {
		int head = 0, tail = 0;
		if (rangeHead != -1 && rangeTail != -1) {
			head = rangeHead;
			tail = rangeTail;
		} else {
			head = caretIndex-1;
			if (head == -1)
				head = 0;
			tail = caretIndex;
		}
		String newText;
		if (tail > head)	{
			newText = finalText.substring(0,head) + insertText + finalText.substring(tail, finalText.length());
			int tempIndex = head+insertText.length();
			setTextFieldText(newText);
			caretIndex = tempIndex;
		} else {
			newText = finalText.substring(0,tail) + insertText + finalText.substring(head, finalText.length());
			int tempIndex = tail+insertText.length();
			setTextFieldText(newText);
			caretIndex = tempIndex;
		}
	}
}
