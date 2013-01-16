/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.text;

import com.jme3.app.Application;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.LineWrapMode;
import com.jme3.font.Rectangle;
import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.List;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;
import tonegod.gui.listeners.KeyboardListener;


/**
 *
 * @author t0neg0d
 */
public class TextField extends Element implements KeyboardListener {
	
	Element caret;
	Material caretMat;
	int caretIndex = 0, head = 0, tail = 0, rangeHead = -1, rangeTail = -1;
	List<String> textFieldText = new ArrayList();
	String finalText = "", visibleText = "", textRangeText = "";
	BitmapText widthTest;
	private boolean hasTabFocus = false;
	private float caretX = 0;
	char searchStr = ' ';
	
	boolean ctrl = false, shift = false, alt = false;
	
	boolean isEnabled = true;
	
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
		
		this.setFontSize(20);
		this.setTextPadding(3);
		this.setTextWrap(LineWrapMode.Clip);
		this.setTextVAlign(BitmapFont.VAlign.Center);
		
		caret = new Element(screen, UID + ":Caret", new Vector2f(0,0), new Vector2f(dimensions.x, dimensions.y), new Vector4f(0,0,0,0), null);
		
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
		
		setTextFieldFontColor(ColorRGBA.Black);
		this.addChild(caret);
		
		this.setText("");
	}

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
			if (caretIndex > 0) {
				if (!ctrl)
					caretIndex--;
				else
					caretIndex = finalText.substring(0,caretIndex-1).lastIndexOf(" ")+1;
				if (caretIndex < 0)
					caretIndex = 0;
				if (!shift) {
					resetTextRange();
					setTextRangeStart(caretIndex);
				} else {
					setTextRangeEnd(caretIndex);
				}
			}
		} else if (evt.getKeyCode() == KeyInput.KEY_RIGHT) {
			if (caretIndex < textFieldText.size()) {
				if (!ctrl)
					caretIndex++;
				else {
					if (finalText.substring(caretIndex+1, finalText.length()).indexOf(" ") != -1)
						
						caretIndex += finalText.substring(caretIndex+1, finalText.length()).indexOf(" ")+2;
					else
						caretIndex = finalText.length();
				}
				if (caretIndex > finalText.length())
					caretIndex = finalText.length();
				if (!shift) {
					resetTextRange();
					setTextRangeStart(caretIndex);
				} else {
					setTextRangeEnd(caretIndex);
				}
			}
		} else if (evt.getKeyCode() == KeyInput.KEY_END || evt.getKeyCode() == KeyInput.KEY_NEXT || evt.getKeyCode() == KeyInput.KEY_DOWN) {
			caretIndex = textFieldText.size();
		} else if (evt.getKeyCode() == KeyInput.KEY_HOME || evt.getKeyCode() == KeyInput.KEY_PRIOR || evt.getKeyCode() == KeyInput.KEY_UP) {
			caretIndex = 0;
		} else {
			if (ctrl) {
				if (evt.getKeyCode() == KeyInput.KEY_C) {
					screen.setClipboardText(textRangeText);
				} else if (evt.getKeyCode() == KeyInput.KEY_V) {
					this.pasteTextInto();
				}
			} else {
				if (isEnabled) {
					textFieldText.add(caretIndex, String.valueOf(evt.getKeyChar()));
					caretIndex++;
				}
			}
		}
		this.setText(getVisibleText());
		controlKeyPressHook(evt, finalText);
	}
	
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
	}
	
	protected void getTextFieldText() {
		String ret = "";
		int index = 0;
		for (String s : textFieldText) {
			ret += s;
			index++;
		}
		finalText = ret;
	}
	
	public void setTextFieldText(String s) {
		caretIndex = 0;
		textFieldText.clear();
		for (int i = 0; i < s.length(); i++) {
			textFieldText.add(caretIndex, String.valueOf(s.charAt(i)));
			caretIndex++;
		}
		this.setText(getVisibleText());
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
		if (index2 != textFieldText.size())
			index2++;
		
		if (head != index1 || tail != index2) {
			head = index1;
			tail = index2;
		}
		if (head != tail && head != -1 && tail != -1) {
			visibleText = finalText.substring(head, tail);
		} else {
			visibleText = "";
		}
		
		widthTest.setText(finalText.substring(head, caretIndex));
		caretX = widthTest.getLineWidth();
		setCaretPosition(getAbsoluteX()+caretX);
		//caret.setLocalTranslation(caret.getLocalTranslation().setX(caretX));
		/*
		if (rangeHead != -1 && rangeTail != -1) {
			float rangeX = getFont().getLineWidth(finalText.substring(head, rangeHead));
			float rangeW = getFont().getLineWidth(finalText.substring(rangeHead, rangeTail));
			System.out.println(rangeX + " : " + rangeW);
			textRange.setX(rangeX);
			textRange.setWidth(rangeW);
		}
		*/
		return visibleText;
	}
	
	public void setTabFocus() {
	//	System.out.println("Setting tab focus for: " + getUID());
		hasTabFocus = true;
		if (isEnabled)
			caret.getMaterial().setBoolean("HasTabFocus", true);
	}
	
	public void resetTabFocus() {
	//	System.out.println("Resetting tab focus for: " + getUID());
		hasTabFocus = false;
		caret.getMaterial().setBoolean("HasTabFocus", false);
	}
	
	
	private void setCaretPosition(float caretX) {
		if (textElement != null) {
			if (hasTabFocus) {
				caret.getMaterial().setFloat("CaretX", caretX+getTextPadding());
				caret.getMaterial().setFloat("LastUpdate", app.getTimer().getTimeInSeconds());
			}
		}
	}
	
	public void setCaretPositionByX(float x) {
		int index1 = visibleText.length();
		if (visibleText.length() > 0) {
			widthTest.setText(visibleText.substring(0, index1));
			while(caret.getAbsoluteX()+widthTest.getLineWidth() > x) {
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
	
	private void setTextRangeStart(int head) {
		if (!visibleText.equals("")) {
			System.out.println("Setting text range start to: " + head);
			rangeHead = head;
			if (head-this.head <= 0)
				widthTest.setText("");
			else if(head-this.head < visibleText.length())
				widthTest.setText(visibleText.substring(0, head-this.head));
			else
				widthTest.setText(visibleText);
			caret.getMaterial().setFloat("TextRangeStart", getAbsoluteX()+widthTest.getLineWidth()+getTextPadding());
		}
	}
	
	private void setTextRangeEnd(int tail) {
		if (!visibleText.equals("") && rangeHead != -1) {
			System.out.println("Setting text range end to: " + tail);
			rangeTail = tail;
			if (tail-this.head <= 0)
				widthTest.setText("");
			else if (tail-this.head < visibleText.length())
				widthTest.setText(visibleText.substring(0, tail-this.head));
			else
				widthTest.setText(visibleText);
			textRangeText = (rangeHead < rangeTail) ? finalText.substring(rangeHead, rangeTail) : finalText.substring(rangeTail, rangeHead);
			caret.getMaterial().setFloat("TextRangeEnd", getAbsoluteX()+widthTest.getLineWidth()+getTextPadding());
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
	
	private void pasteTextInto() {
		String text = screen.getClipboardText();
		int index = caretIndex;
		if (text.length() > 0) {
			for (int i = 0; i < text.length(); i++) {
				textFieldText.add(index, String.valueOf(text.charAt(i)));
				index++;
			}
			caretIndex += index;
			getVisibleText();
		}
	}
	
	public final void setTextFieldFontColor(ColorRGBA fontColor) {
		setFontColor(fontColor);
		caretMat.setColor("Color", fontColor);
	}
	
	public void setIsEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	
	public boolean getIsEnabled() {
		return this.isEnabled;
	}
}
