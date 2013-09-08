/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.extras.android;

import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import java.util.HashMap;
import java.util.Map;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.windows.Panel;
import tonegod.gui.core.Screen;
import tonegod.gui.core.utils.UIDUtil;
import tonegod.gui.effects.Effect;

/**
 *
 * @author t0neg0d
 */
public class Keyboard extends Panel {
	private static enum KeyType {
		NUMERIC,
		ALPHA,
		SYMBOL,
		OTHER
	}
	
	private boolean Shift = false;
	private boolean Symbol = false;
	
	private Map<Integer,KeyboardKey> keys = new HashMap();
	
	/**
	 * Creates a new instance of the Panel control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param position A Vector2f containing the x/y position of the Element
	 */
	public Keyboard(Screen screen) {
		this(screen, UIDUtil.getUID(),
			new Vector2f(0,screen.getHeight()-(screen.getHeight()*0.45f)),
			new Vector2f(screen.getWidth(),(screen.getHeight()*0.45f)),
			screen.getStyle("Window").getVector4f("resizeBorders"),
			screen.getStyle("Window").getString("defaultImg")
		);
	}
	
	/**
	 * Creates a new instance of the Panel control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 * @param resizeBorders A Vector4f containg the border information used when resizing the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg The default image to use for the Panel
	 */
	private Keyboard(Screen screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg);
		
		this.setIsMovable(false);
		this.setIsResizable(false);
		this.setScaleNS(false);
		this.setScaleEW(false);
		this.setClipPadding(screen.getStyle("Window").getFloat("clipPadding"));
		this.setResetKeyboardFocus(false);
		
		populateEffects("Window");
		
		float nWidth = getWidth()*0.1f-(110*0.1f);
		float nX = nWidth+10;;
		float nHeight = getHeight()*0.2f-(60*0.2f);
		float nY = nHeight+10;
		float xGap = 10;
		
		// Row 1 - Numeric
		KeyboardKey key = new KeyboardKey(KeyType.NUMERIC, KeyInput.KEY_1, '1', "1");
		key.setSymbol(KeyType.SYMBOL, KeyInput.KEY_2, '@', "@");
		key.setPosition(xGap,10);
		key.setDimensions(nWidth,nHeight);
		key.createButton();
		keys.put(KeyInput.KEY_1, key);
		addChild(key.getButton());
		
		key = new KeyboardKey(KeyType.NUMERIC, KeyInput.KEY_2, '2', "2");
		key.setSymbol(KeyType.SYMBOL, KeyInput.KEY_3, '#', "#");
		key.setPosition(xGap+(nX),10);
		key.setDimensions(nWidth,nHeight);
		key.createButton();
		keys.put(KeyInput.KEY_2, key);
		addChild(key.getButton());
		
		key = new KeyboardKey(KeyType.NUMERIC, KeyInput.KEY_3, '3', "3");
		key.setSymbol(KeyType.SYMBOL, KeyInput.KEY_7, '&', "&");
		key.setPosition(xGap+(nX*2),10);
		key.setDimensions(nWidth,nHeight);
		key.createButton();
		keys.put(KeyInput.KEY_3, key);
		addChild(key.getButton());
		
		key = new KeyboardKey(KeyType.NUMERIC, KeyInput.KEY_4, '4', "4");
		key.setSymbol(KeyType.SYMBOL, KeyInput.KEY_5, '%', "%");
		key.setPosition(xGap+(nX*3),10);
		key.setDimensions(nWidth,nHeight);
		key.createButton();
		keys.put(KeyInput.KEY_4, key);
		addChild(key.getButton());
		
		key = new KeyboardKey(KeyType.NUMERIC, KeyInput.KEY_5, '5', "5");
		key.setSymbol(KeyType.SYMBOL, KeyInput.KEY_8, '*', "*");
		key.setPosition(xGap+(nX*4),10);
		key.setDimensions(nWidth,nHeight);
		key.createButton();
		keys.put(KeyInput.KEY_5, key);
		addChild(key.getButton());
		
		key = new KeyboardKey(KeyType.NUMERIC, KeyInput.KEY_6, '6', "6");
		key.setSymbol(KeyType.SYMBOL, KeyInput.KEY_EQUALS, '+', "+");
		key.setPosition(xGap+(nX*5),10);
		key.setDimensions(nWidth,nHeight);
		key.createButton();
		keys.put(KeyInput.KEY_6, key);
		addChild(key.getButton());
		
		key = new KeyboardKey(KeyType.NUMERIC, KeyInput.KEY_7, '7', "7");
		key.setSymbol(KeyType.SYMBOL, KeyInput.KEY_MINUS, '-', "-");
		key.setPosition(xGap+(nX*6),10);
		key.setDimensions(nWidth,nHeight);
		key.createButton();
		keys.put(KeyInput.KEY_7, key);
		addChild(key.getButton());
		
		key = new KeyboardKey(KeyType.NUMERIC, KeyInput.KEY_8, '8', "8");
		key.setSymbol(KeyType.SYMBOL, KeyInput.KEY_EQUALS, '=', "=");
		key.setPosition(xGap+(nX*7),10);
		key.setDimensions(nWidth,nHeight);
		key.createButton();
		keys.put(KeyInput.KEY_8, key);
		addChild(key.getButton());
		
		key = new KeyboardKey(KeyType.NUMERIC, KeyInput.KEY_9, '9', "9");
		key.setSymbol(KeyType.SYMBOL, KeyInput.KEY_COMMA, '<', "<");
		key.setPosition(xGap+(nX*8),10);
		key.setDimensions(nWidth,nHeight);
		key.createButton();
		keys.put(KeyInput.KEY_9, key);
		addChild(key.getButton());
		
		key = new KeyboardKey(KeyType.NUMERIC, KeyInput.KEY_0, '0', "0");
		key.setSymbol(KeyType.SYMBOL, KeyInput.KEY_PERIOD, '>', ">");
		key.setPosition(xGap+(nX*9),10);
		key.setDimensions(nWidth,nHeight);
		key.createButton();
		keys.put(KeyInput.KEY_0, key);
		addChild(key.getButton());
		
		// Row 2 - Alpha
		key = new KeyboardKey(KeyType.ALPHA, KeyInput.KEY_Q, 'q', "q");
		key.setShift(KeyType.ALPHA, KeyInput.KEY_Q, 'Q', "Q");
		key.setSymbol(KeyType.SYMBOL, KeyInput.KEY_GRAVE, '~', "~");
		key.setPosition(xGap,10+(nY));
		key.setDimensions(nWidth,nHeight);
		key.createButton();
		keys.put(KeyInput.KEY_Q, key);
		addChild(key.getButton());
		
		key = new KeyboardKey(KeyType.ALPHA, KeyInput.KEY_W, 'w', "w");
		key.setShift(KeyType.ALPHA, KeyInput.KEY_W, 'W', "W");
		key.setSymbol(KeyType.SYMBOL, KeyInput.KEY_6, '^', "^");
		key.setPosition(xGap+(nX),10+(nY));
		key.setDimensions(nWidth,nHeight);
		key.createButton();
		keys.put(KeyInput.KEY_W, key);
		addChild(key.getButton());
		
		key = new KeyboardKey(KeyType.ALPHA, KeyInput.KEY_E, 'e', "e");
		key.setShift(KeyType.ALPHA, KeyInput.KEY_E, 'E', "E");
		key.setSymbol(KeyType.SYMBOL, KeyInput.KEY_9, '(', "(");
		key.setPosition(xGap+(nX*2),10+(nY));
		key.setDimensions(nWidth,nHeight);
		key.createButton();
		keys.put(KeyInput.KEY_E, key);
		addChild(key.getButton());
		
		key = new KeyboardKey(KeyType.ALPHA, KeyInput.KEY_R, 'r', "r");
		key.setShift(KeyType.ALPHA, KeyInput.KEY_R, 'R', "R");
		key.setSymbol(KeyType.SYMBOL, KeyInput.KEY_0, ')', ")");
		key.setPosition(xGap+(nX*3),10+(nY));
		key.setDimensions(nWidth,nHeight);
		key.createButton();
		keys.put(KeyInput.KEY_R, key);
		addChild(key.getButton());
		
		key = new KeyboardKey(KeyType.ALPHA, KeyInput.KEY_T, 't', "t");
		key.setShift(KeyType.ALPHA, KeyInput.KEY_T, 'T', "T");
		key.setSymbol(KeyType.SYMBOL, KeyInput.KEY_LBRACKET, '{', "{");
		key.setPosition(xGap+(nX*4),10+(nY));
		key.setDimensions(nWidth,nHeight);
		key.createButton();
		keys.put(KeyInput.KEY_T, key);
		addChild(key.getButton());
		
		key = new KeyboardKey(KeyType.ALPHA, KeyInput.KEY_Y, 'y', "y");
		key.setShift(KeyType.ALPHA, KeyInput.KEY_Y, 'Y', "Y");
		key.setSymbol(KeyType.SYMBOL, KeyInput.KEY_RBRACKET, '}', "}");
		key.setPosition(xGap+(nX*5),10+(nY));
		key.setDimensions(nWidth,nHeight);
		key.createButton();
		keys.put(KeyInput.KEY_Y, key);
		addChild(key.getButton());
		
		key = new KeyboardKey(KeyType.ALPHA, KeyInput.KEY_U, 'u', "u");
		key.setShift(KeyType.ALPHA, KeyInput.KEY_U, 'U', "U");
		key.setSymbol(KeyType.SYMBOL, KeyInput.KEY_LBRACKET, '[', "[");
		key.setPosition(xGap+(nX*6),10+(nY));
		key.setDimensions(nWidth,nHeight);
		key.createButton();
		keys.put(KeyInput.KEY_U, key);
		addChild(key.getButton());
		
		key = new KeyboardKey(KeyType.ALPHA, KeyInput.KEY_I, 'i', "i");
		key.setShift(KeyType.ALPHA, KeyInput.KEY_I, 'I', "I");
		key.setSymbol(KeyType.SYMBOL, KeyInput.KEY_RBRACKET, ']', "]");
		key.setPosition(xGap+(nX*7),10+(nY));
		key.setDimensions(nWidth,nHeight);
		key.createButton();
		keys.put(KeyInput.KEY_I, key);
		addChild(key.getButton());
		
		key = new KeyboardKey(KeyType.ALPHA, KeyInput.KEY_O, 'o', "o");
		key.setShift(KeyType.ALPHA, KeyInput.KEY_O, 'O', "O");
		key.setSymbol(KeyType.SYMBOL, KeyInput.KEY_GRAVE, '`', "`");
		key.setPosition(xGap+(nX*8),10+(nY));
		key.setDimensions(nWidth,nHeight);
		key.createButton();
		keys.put(KeyInput.KEY_O, key);
		addChild(key.getButton());
		
		key = new KeyboardKey(KeyType.ALPHA, KeyInput.KEY_P, 'p', "p");
		key.setShift(KeyType.ALPHA, KeyInput.KEY_P, 'P', "P");
		key.setSymbol(KeyType.SYMBOL, KeyInput.KEY_4, '$', "$");
		key.setPosition(xGap+(nX*9),10+(nY));
		key.setDimensions(nWidth,nHeight);
		key.createButton();
		keys.put(KeyInput.KEY_P, key);
		addChild(key.getButton());
		
		// Row 3 - Alpha
		xGap = 10+(nWidth*0.5f);
		
		key = new KeyboardKey(KeyType.ALPHA, KeyInput.KEY_A, 'a', "a");
		key.setShift(KeyType.ALPHA, KeyInput.KEY_A, 'A', "A");
		key.setSymbol(KeyType.SYMBOL, KeyInput.KEY_SLASH, '/', "/");
		key.setPosition(xGap,10+(nY*2));
		key.setDimensions(nWidth,nHeight);
		key.createButton();
		keys.put(KeyInput.KEY_A, key);
		addChild(key.getButton());
		
		key = new KeyboardKey(KeyType.ALPHA, KeyInput.KEY_S, 's', "s");
		key.setShift(KeyType.ALPHA, KeyInput.KEY_S, 'S', "S");
		key.setSymbol(KeyType.SYMBOL, KeyInput.KEY_BACKSLASH, '|', "|");
		key.setPosition(xGap+(nX),10+(nY*2));
		key.setDimensions(nWidth,nHeight);
		key.createButton();
		keys.put(KeyInput.KEY_S, key);
		addChild(key.getButton());
		
		key = new KeyboardKey(KeyType.ALPHA, KeyInput.KEY_D, 'd', "d");
		key.setShift(KeyType.ALPHA, KeyInput.KEY_D, 'D', "D");
		key.setSymbol(KeyType.SYMBOL, KeyInput.KEY_BACKSLASH, '\\', "\\");
		key.setPosition(xGap+(nX*2),10+(nY*2));
		key.setDimensions(nWidth,nHeight);
		key.createButton();
		keys.put(KeyInput.KEY_D, key);
		addChild(key.getButton());
		
		key = new KeyboardKey(KeyType.ALPHA, KeyInput.KEY_F, 'f', "f");
		key.setShift(KeyType.ALPHA, KeyInput.KEY_F, 'F', "F");
		key.setSymbol(KeyType.SYMBOL, KeyInput.KEY_UNDERLINE, '_', "_");
		key.setPosition(xGap+(nX*3),10+(nY*2));
		key.setDimensions(nWidth,nHeight);
		key.createButton();
		keys.put(KeyInput.KEY_F, key);
		addChild(key.getButton());
		
		key = new KeyboardKey(KeyType.ALPHA, KeyInput.KEY_G, 'g', "g");
		key.setShift(KeyType.ALPHA, KeyInput.KEY_G, 'G', "G");
		key.setSymbol(KeyType.SYMBOL, KeyInput.KEY_COLON, ':', ":");
		key.setPosition(xGap+(nX*4),10+(nY*2));
		key.setDimensions(nWidth,nHeight);
		key.createButton();
		keys.put(KeyInput.KEY_G, key);
		addChild(key.getButton());
		
		key = new KeyboardKey(KeyType.ALPHA, KeyInput.KEY_H, 'h', "h");
		key.setShift(KeyType.ALPHA, KeyInput.KEY_H, 'H', "H");
		key.setSymbol(KeyType.SYMBOL, KeyInput.KEY_SEMICOLON, ';', ";");
		key.setPosition(xGap+(nX*5),10+(nY*2));
		key.setDimensions(nWidth,nHeight);
		key.createButton();
		keys.put(KeyInput.KEY_H, key);
		addChild(key.getButton());
		
		key = new KeyboardKey(KeyType.ALPHA, KeyInput.KEY_J, 'j', "j");
		key.setShift(KeyType.ALPHA, KeyInput.KEY_J, 'J', "J");
		key.setSymbol(KeyType.SYMBOL, KeyInput.KEY_APOSTROPHE, '"', "\"");
		key.setPosition(xGap+(nX*6),10+(nY*2));
		key.setDimensions(nWidth,nHeight);
		key.createButton();
		keys.put(KeyInput.KEY_J, key);
		addChild(key.getButton());
		
		key = new KeyboardKey(KeyType.ALPHA, KeyInput.KEY_K, 'k', "k");
		key.setShift(KeyType.ALPHA, KeyInput.KEY_K, 'K', "K");
		key.setSymbol(KeyType.SYMBOL, KeyInput.KEY_APOSTROPHE, '\'', "'");
		key.setPosition(xGap+(nX*7),10+(nY*2));
		key.setDimensions(nWidth,nHeight);
		key.createButton();
		keys.put(KeyInput.KEY_K, key);
		addChild(key.getButton());
		
		key = new KeyboardKey(KeyType.ALPHA, KeyInput.KEY_L, 'l', "l");
		key.setShift(KeyType.ALPHA, KeyInput.KEY_L, 'L', "L");
		key.setSymbol(KeyType.SYMBOL, KeyInput.KEY_GRAVE, '!', "!");
		key.setPosition(xGap+(nX*8),10+(nY*2));
		key.setDimensions(nWidth,nHeight);
		key.createButton();
		keys.put(KeyInput.KEY_L, key);
		addChild(key.getButton());
		
		// Row 3 - Alpha
		key = new KeyboardKey(KeyType.OTHER, KeyInput.KEY_LSHIFT, '^', "SHIFT");
		key.setPosition(10,10+(nY*3));
		key.setDimensions(nWidth+(nWidth*0.5f),nHeight);
		key.createShiftButton();
		keys.put(KeyInput.KEY_LSHIFT, key);
		addChild(key.getButton());
		
		key = new KeyboardKey(KeyType.ALPHA, KeyInput.KEY_Z, 'z', "z");
		key.setShift(KeyType.ALPHA, KeyInput.KEY_Z, 'Z', "Z");
		key.setSymbol(KeyType.SYMBOL, KeyInput.KEY_UNLABELED, '?', "?");
		key.setPosition(xGap+(nX),10+(nY*3));
		key.setDimensions(nWidth,nHeight);
		key.createButton();
		keys.put(KeyInput.KEY_Z, key);
		addChild(key.getButton());
		
		key = new KeyboardKey(KeyType.ALPHA, KeyInput.KEY_X, 'x', "x");
		key.setShift(KeyType.ALPHA, KeyInput.KEY_X, 'X', "X");
		key.setSymbol(KeyType.SYMBOL, KeyInput.KEY_UNLABELED, ':', ":-(");
		key.setPosition(xGap+(nX*2),10+(nY*3));
		key.setDimensions(nWidth,nHeight);
		key.createEmoteButton('(');
		keys.put(KeyInput.KEY_X, key);
		addChild(key.getButton());
		
		key = new KeyboardKey(KeyType.ALPHA, KeyInput.KEY_C, 'c', "c");
		key.setShift(KeyType.ALPHA, KeyInput.KEY_C, 'C', "C");
		key.setSymbol(KeyType.SYMBOL, KeyInput.KEY_UNLABELED, ':', ":-)");
		key.setPosition(xGap+(nX*3),10+(nY*3));
		key.setDimensions(nWidth,nHeight);
		key.createEmoteButton(')');
		keys.put(KeyInput.KEY_C, key);
		addChild(key.getButton());
		
		key = new KeyboardKey(KeyType.ALPHA, KeyInput.KEY_V, 'v', "v");
		key.setShift(KeyType.ALPHA, KeyInput.KEY_V, 'V', "V");
		key.setSymbol(KeyType.SYMBOL, KeyInput.KEY_UNLABELED, ':', ":-D");
		key.setPosition(xGap+(nX*4),10+(nY*3));
		key.setDimensions(nWidth,nHeight);
		key.createEmoteButton('D');
		keys.put(KeyInput.KEY_V, key);
		addChild(key.getButton());
		
		key = new KeyboardKey(KeyType.ALPHA, KeyInput.KEY_B, 'b', "b");
		key.setShift(KeyType.ALPHA, KeyInput.KEY_B, 'B', "B");
		key.setSymbol(KeyType.SYMBOL, KeyInput.KEY_UNLABELED, ':', ":-P");
		key.setPosition(xGap+(nX*5),10+(nY*3));
		key.setDimensions(nWidth,nHeight);
		key.createEmoteButton('P');
		keys.put(KeyInput.KEY_B, key);
		addChild(key.getButton());
		
		key = new KeyboardKey(KeyType.ALPHA, KeyInput.KEY_N, 'n', "n");
		key.setShift(KeyType.ALPHA, KeyInput.KEY_N, 'N', "N");
		key.setSymbol(KeyType.SYMBOL, KeyInput.KEY_UNLABELED, ':', ">:|");
		key.setPosition(xGap+(nX*6),10+(nY*3));
		key.setDimensions(nWidth,nHeight);
		key.createEmoteButton('|');
		keys.put(KeyInput.KEY_N, key);
		addChild(key.getButton());
		
		key = new KeyboardKey(KeyType.ALPHA, KeyInput.KEY_M, 'm', "m");
		key.setShift(KeyType.ALPHA, KeyInput.KEY_M, 'M', "M");
		key.setSymbol(KeyType.SYMBOL, KeyInput.KEY_UNLABELED, ':', ";-)");
		key.setPosition(xGap+(nX*7),10+(nY*3));
		key.setDimensions(nWidth,nHeight);
		key.createEmoteButton(';');
		keys.put(KeyInput.KEY_J, key);
		addChild(key.getButton());
		
		key = new KeyboardKey(KeyType.OTHER, KeyInput.KEY_BACK, '^', "BACK");
		key.setPosition(xGap+(nX*8),10+(nY*3));
		key.setDimensions(nWidth+(nWidth*0.5f),nHeight);
		key.createBackButton();
		keys.put(KeyInput.KEY_BACK, key);
		addChild(key.getButton());
		
		
		// Row 5 - Alpha
		key = new KeyboardKey(KeyType.OTHER, KeyInput.KEY_LCONTROL, '^', "@#_");
		key.setPosition(10,10+(nY*4));
		key.setDimensions(nWidth+(nWidth*0.5f),nHeight);
		key.createSymbolButton();
		keys.put(KeyInput.KEY_LCONTROL, key);
		addChild(key.getButton());
		
		key = new KeyboardKey(KeyType.OTHER, KeyInput.KEY_SPACE, ' ', "SPACE");
		key.setPosition(xGap+(nX),10+(nY*4));
		key.setDimensions(nWidth+(nX*4),nHeight);
		key.createSpaceButton();
		keys.put(KeyInput.KEY_SPACE, key);
		addChild(key.getButton());
		
		key = new KeyboardKey(KeyType.OTHER, KeyInput.KEY_COMMA, ',', ",");
		key.setPosition(xGap+(nX*6),10+(nY*4));
		key.setDimensions(nWidth,nHeight);
		key.createButton();
		keys.put(KeyInput.KEY_COMMA, key);
		addChild(key.getButton());
		
		key = new KeyboardKey(KeyType.OTHER, KeyInput.KEY_PERIOD, '.', ".");
		key.setPosition(xGap+(nX*7),10+(nY*4));
		key.setDimensions(nWidth,nHeight);
		key.createEmoteButton(';');
		keys.put(KeyInput.KEY_PERIOD, key);
		addChild(key.getButton());
		
		key = new KeyboardKey(KeyType.OTHER, KeyInput.KEY_RETURN, '^', "ENTER");
		key.setPosition(xGap+(nX*8),10+(nY*4));
		key.setDimensions(nWidth+(nWidth*0.5f),nHeight);
		key.createEnterButton();
		keys.put(KeyInput.KEY_RETURN, key);
		addChild(key.getButton());
		/*
		createSingleCharKey(xGap,10,nWidth,nHeight,KeyInput.KEY_1, '1', KeyType.NUMERIC);
		createSingleCharKey(xGap+(nX),10,nWidth,nHeight,KeyInput.KEY_2, '2', KeyType.NUMERIC);
		createSingleCharKey(xGap+(nX*2),10,nWidth,nHeight,KeyInput.KEY_3, '3', KeyType.NUMERIC);
		createSingleCharKey(xGap+(nX*3),10,nWidth,nHeight,KeyInput.KEY_4, '4', KeyType.NUMERIC);
		createSingleCharKey(xGap+(nX*4),10,nWidth,nHeight,KeyInput.KEY_5, '5', KeyType.NUMERIC);
		createSingleCharKey(xGap+(nX*5),10,nWidth,nHeight,KeyInput.KEY_6, '6', KeyType.NUMERIC);
		createSingleCharKey(xGap+(nX*6),10,nWidth,nHeight,KeyInput.KEY_7, '7', KeyType.NUMERIC);
		createSingleCharKey(xGap+(nX*7),10,nWidth,nHeight,KeyInput.KEY_8, '8', KeyType.NUMERIC);
		createSingleCharKey(xGap+(nX*8),10,nWidth,nHeight,KeyInput.KEY_9, '9', KeyType.NUMERIC);
		createSingleCharKey(xGap+(nX*9),10,nWidth,nHeight,KeyInput.KEY_0, '0', KeyType.NUMERIC);
		
		// Row 2
		createSingleCharKey(xGap,10+(nY),nWidth,nHeight,KeyInput.KEY_Q, 'q', KeyType.ALPHA);
		createSingleCharKey(xGap+(nX),10+(nY),nWidth,nHeight,KeyInput.KEY_W, 'w', KeyType.ALPHA);
		createSingleCharKey(xGap+(nX*2),10+(nY),nWidth,nHeight,KeyInput.KEY_E, 'e', KeyType.ALPHA);
		createSingleCharKey(xGap+(nX*3),10+(nY),nWidth,nHeight,KeyInput.KEY_R, 'r', KeyType.ALPHA);
		createSingleCharKey(xGap+(nX*4),10+(nY),nWidth,nHeight,KeyInput.KEY_T, 't', KeyType.ALPHA);
		createSingleCharKey(xGap+(nX*5),10+(nY),nWidth,nHeight,KeyInput.KEY_Y, 'y', KeyType.ALPHA);
		createSingleCharKey(xGap+(nX*5),10+(nY),nWidth,nHeight,KeyInput.KEY_Y, 'y', KeyType.ALPHA);
		createSingleCharKey(xGap+(nX*6),10+(nY),nWidth,nHeight,KeyInput.KEY_U, 'u', KeyType.ALPHA);
		createSingleCharKey(xGap+(nX*7),10+(nY),nWidth,nHeight,KeyInput.KEY_I, 'i', KeyType.ALPHA);
		createSingleCharKey(xGap+(nX*8),10+(nY),nWidth,nHeight,KeyInput.KEY_O, 'o', KeyType.ALPHA);
		createSingleCharKey(xGap+(nX*9),10+(nY),nWidth,nHeight,KeyInput.KEY_P, 'p', KeyType.ALPHA);
		
		xGap = 15+(nWidth*0.5f);
		// Row 3
		createSingleCharKey(xGap,10+(nY*2),nWidth,nHeight,KeyInput.KEY_A, 'a', KeyType.ALPHA);
		createSingleCharKey(xGap+(nX),10+(nY*2),nWidth,nHeight,KeyInput.KEY_S, 's', KeyType.ALPHA);
		createSingleCharKey(xGap+(nX*2),10+(nY*2),nWidth,nHeight,KeyInput.KEY_D, 'd', KeyType.ALPHA);
		createSingleCharKey(xGap+(nX*3),10+(nY*2),nWidth,nHeight,KeyInput.KEY_F, 'f', KeyType.ALPHA);
		createSingleCharKey(xGap+(nX*4),10+(nY*2),nWidth,nHeight,KeyInput.KEY_G, 'g', KeyType.ALPHA);
		createSingleCharKey(xGap+(nX*5),10+(nY*2),nWidth,nHeight,KeyInput.KEY_H, 'h', KeyType.ALPHA);
		createSingleCharKey(xGap+(nX*6),10+(nY*2),nWidth,nHeight,KeyInput.KEY_J, 'j', KeyType.ALPHA);
		createSingleCharKey(xGap+(nX*7),10+(nY*2),nWidth,nHeight,KeyInput.KEY_K, 'k', KeyType.ALPHA);
		createSingleCharKey(xGap+(nX*8),10+(nY*2),nWidth,nHeight,KeyInput.KEY_L, 'l', KeyType.ALPHA);
		
		// Row 4
		createLabeledCharKey(10,10+(nY*3),nWidth+(nWidth*0.5f),nHeight,KeyInput.KEY_LSHIFT, '^', "SHIFT", KeyType.OTHER);
		createSingleCharKey(xGap+(nX),10+(nY*3),nWidth,nHeight,KeyInput.KEY_Z, 'z', KeyType.ALPHA);
		createSingleCharKey(xGap+(nX*2),10+(nY*3),nWidth,nHeight,KeyInput.KEY_X, 'x', KeyType.ALPHA);
		createSingleCharKey(xGap+(nX*3),10+(nY*3),nWidth,nHeight,KeyInput.KEY_C, 'c', KeyType.ALPHA);
		createSingleCharKey(xGap+(nX*4),10+(nY*3),nWidth,nHeight,KeyInput.KEY_V, 'v', KeyType.ALPHA);
		createSingleCharKey(xGap+(nX*5),10+(nY*3),nWidth,nHeight,KeyInput.KEY_B, 'b', KeyType.ALPHA);
		createSingleCharKey(xGap+(nX*6),10+(nY*3),nWidth,nHeight,KeyInput.KEY_N, 'n', KeyType.ALPHA);
		createSingleCharKey(xGap+(nX*7),10+(nY*3),nWidth,nHeight,KeyInput.KEY_M, 'm', KeyType.ALPHA);
		createLabeledCharKey(xGap+(nX*8),10+(nY*3),nWidth+(nWidth*0.5f),nHeight,KeyInput.KEY_BACK, '^', "BACK", KeyType.OTHER);
		*/
	}
	
	public void setGlobalShift(boolean shift) {
		this.Shift = shift;
		if (!Symbol) {
			for (KeyboardKey key : keys.values()) {
				if (key.getKeyType() == KeyType.ALPHA)
					key.setShift(shift);
			}
		}
	}
	
	public void setGlobalSymbol(boolean symbol) {
		this.Symbol = symbol;
		for (KeyboardKey key : keys.values()) {
			if (key.getKeyType() == KeyType.ALPHA || key.getKeyType() == KeyType.NUMERIC)
				key.setSymbol(symbol);
		}
	}
	
	private class KeyboardKey {
		KeyType type, shiftType, symbolType;
		int keyCode, shiftKeyCode, symbolKeyCode;
		char character, shiftCharacter, symbolCharacter;
		String label, shiftLabel, symbolLabel;
		float x, y, w, h;
		boolean shift = false;
		boolean symbol = false;
		ButtonAdapter button;
		
		public KeyboardKey(KeyType type, int code, char character, String label) {
			this.type = type;
			this.keyCode = code;
			this.character = character;
			this.label = label;
		}
		
		public void setShift(KeyType type, int code, char character, String label) {
			this.shiftType = type;
			this.shiftKeyCode = code;
			this.shiftCharacter = character;
			this.shiftLabel = label;
		}
		
		public void setSymbol(KeyType type, int code, char character, String label) {
			this.symbolType = type;
			this.symbolKeyCode = code;
			this.symbolCharacter = character;
			this.symbolLabel = label;
		}
		
		public void setPosition(float x, float y) {
			this.x = x;
			this.y = y;
		}
		
		public void setDimensions(float w, float h) {
			this.w = w;
			this.h = h;
		}
		
		public KeyType getKeyType() { return type; }
		public int getKeyCode() { return keyCode; }
		public char getCharacter() { return character; }
		public String getLabel() { return label; }
		
		public ButtonAdapter createButton() {
			button = new ButtonAdapter(screen,
				new Vector2f(x,y),
				new Vector2f(w,h)
			) {
				@Override
				public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean isToggled) {
					KeyInputEvent nEvt = null;
					if (symbol) {
						nEvt = new KeyInputEvent(symbolKeyCode, symbolCharacter, true, false);
					} else if (shift) {
						nEvt = new KeyInputEvent(shiftKeyCode, shiftCharacter, true, false);
					} else {
						nEvt = new KeyInputEvent(keyCode, character, true, false);
					}
					nEvt.setTime(System.currentTimeMillis());
					screen.onKeyEvent(nEvt);
				}
				@Override
				public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean isToggled) {
					KeyInputEvent nEvt = null;
					if (symbol) {
						nEvt = new KeyInputEvent(symbolKeyCode, symbolCharacter, false, false);
					} else if (shift) {
						nEvt = new KeyInputEvent(shiftKeyCode, shiftCharacter, false, false);
					} else {
						nEvt = new KeyInputEvent(keyCode, character, false, false);
					}
					nEvt.setTime(System.currentTimeMillis());
					screen.onKeyEvent(nEvt);
				}
				@Override
				public void onButtonStillPressedInterval() {
					KeyInputEvent nEvt = null;
					if (symbol) {
						nEvt = new KeyInputEvent(symbolKeyCode, symbolCharacter, true, false);
					} else if (shift) {
						nEvt = new KeyInputEvent(shiftKeyCode, shiftCharacter, true, false);
					} else {
						nEvt = new KeyInputEvent(keyCode, character, true, false);
					}
					nEvt.setTime(System.currentTimeMillis());
					screen.onKeyEvent(nEvt);
				}
			};
			button.setText(label);
			button.setResetKeyboardFocus(false);
		//	button.setInterval(15);
			button.removeEffect(Effect.EffectEvent.Hover);
			return button;
		}
		
		public ButtonAdapter createEmoteButton(final char emote) {
			button = new ButtonAdapter(screen,
				new Vector2f(x,y),
				new Vector2f(w,h)
			) {
				@Override
				public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean isToggled) {
					KeyInputEvent nEvt = null;
					if (symbol) {
						if (emote == ')') {
							nEvt = new KeyInputEvent(symbolKeyCode, ':', true, false);
							nEvt.setTime(System.currentTimeMillis());
							screen.onKeyEvent(nEvt);
							nEvt = new KeyInputEvent(symbolKeyCode, '-', true, false);
							nEvt.setTime(System.currentTimeMillis());
							screen.onKeyEvent(nEvt);
							nEvt = new KeyInputEvent(symbolKeyCode, ')', true, false);
							nEvt.setTime(System.currentTimeMillis());
							screen.onKeyEvent(nEvt);
						} else if (emote == '(') {
							nEvt = new KeyInputEvent(symbolKeyCode, ':', true, false);
							nEvt.setTime(System.currentTimeMillis());
							screen.onKeyEvent(nEvt);
							nEvt = new KeyInputEvent(symbolKeyCode, '-', true, false);
							nEvt.setTime(System.currentTimeMillis());
							screen.onKeyEvent(nEvt);
							nEvt = new KeyInputEvent(symbolKeyCode, '(', true, false);
							nEvt.setTime(System.currentTimeMillis());
							screen.onKeyEvent(nEvt);
						} else if (emote == 'D') {
							nEvt = new KeyInputEvent(symbolKeyCode, ':', true, false);
							nEvt.setTime(System.currentTimeMillis());
							screen.onKeyEvent(nEvt);
							nEvt = new KeyInputEvent(symbolKeyCode, '-', true, false);
							nEvt.setTime(System.currentTimeMillis());
							screen.onKeyEvent(nEvt);
							nEvt = new KeyInputEvent(symbolKeyCode, 'D', true, false);
							nEvt.setTime(System.currentTimeMillis());
							screen.onKeyEvent(nEvt);
						} else if (emote == 'P') {
							nEvt = new KeyInputEvent(symbolKeyCode, ':', true, false);
							nEvt.setTime(System.currentTimeMillis());
							screen.onKeyEvent(nEvt);
							nEvt = new KeyInputEvent(symbolKeyCode, '-', true, false);
							nEvt.setTime(System.currentTimeMillis());
							screen.onKeyEvent(nEvt);
							nEvt = new KeyInputEvent(symbolKeyCode, 'P', true, false);
							nEvt.setTime(System.currentTimeMillis());
							screen.onKeyEvent(nEvt);
						} else if (emote == ';') {
							nEvt = new KeyInputEvent(symbolKeyCode, ';', true, false);
							nEvt.setTime(System.currentTimeMillis());
							screen.onKeyEvent(nEvt);
							nEvt = new KeyInputEvent(symbolKeyCode, '-', true, false);
							nEvt.setTime(System.currentTimeMillis());
							screen.onKeyEvent(nEvt);
							nEvt = new KeyInputEvent(symbolKeyCode, ')', true, false);
							nEvt.setTime(System.currentTimeMillis());
							screen.onKeyEvent(nEvt);
						} else if (emote == '|') {
							nEvt = new KeyInputEvent(symbolKeyCode, '>', true, false);
							nEvt.setTime(System.currentTimeMillis());
							screen.onKeyEvent(nEvt);
							nEvt = new KeyInputEvent(symbolKeyCode, ':', true, false);
							nEvt.setTime(System.currentTimeMillis());
							screen.onKeyEvent(nEvt);
							nEvt = new KeyInputEvent(symbolKeyCode, '|', true, false);
							nEvt.setTime(System.currentTimeMillis());
							screen.onKeyEvent(nEvt);
						}
					} else if (shift) {
						nEvt = new KeyInputEvent(shiftKeyCode, shiftCharacter, true, false);
						nEvt.setTime(System.currentTimeMillis());
						screen.onKeyEvent(nEvt);
					} else {
						nEvt = new KeyInputEvent(keyCode, character, true, false);
						nEvt.setTime(System.currentTimeMillis());
						screen.onKeyEvent(nEvt);
					}
				}
				@Override
				public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean isToggled) {
					KeyInputEvent nEvt = null;
					if (symbol) {
						nEvt = new KeyInputEvent(symbolKeyCode, symbolCharacter, false, false);
					} else if (shift) {
						nEvt = new KeyInputEvent(shiftKeyCode, shiftCharacter, false, false);
					} else {
						nEvt = new KeyInputEvent(keyCode, character, false, false);
					}
					nEvt.setTime(System.currentTimeMillis());
					screen.onKeyEvent(nEvt);
				}
				@Override
				public void onButtonStillPressedInterval() {
					KeyInputEvent nEvt = null;
					if (symbol) {
						nEvt = new KeyInputEvent(symbolKeyCode, symbolCharacter, true, false);
					} else if (shift) {
						nEvt = new KeyInputEvent(shiftKeyCode, shiftCharacter, true, false);
					} else {
						nEvt = new KeyInputEvent(keyCode, character, true, false);
					}
					nEvt.setTime(System.currentTimeMillis());
					screen.onKeyEvent(nEvt);
				}
			};
			button.setText(label);
			button.setResetKeyboardFocus(false);
		//	button.setInterval(15);
			button.removeEffect(Effect.EffectEvent.Hover);
			return button;
		}
		
		public ButtonAdapter createShiftButton() {
			button = new ButtonAdapter(screen,
				new Vector2f(x,y),
				new Vector2f(w,h)
			) {
				@Override
				public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean isToggled) {
					setGlobalShift(isToggled);
				}
			};
			button.setText(label);
			button.setIsToggleButton(true);
			button.setResetKeyboardFocus(false);
			button.removeEffect(Effect.EffectEvent.Hover);
			return button;
		}
		
		public ButtonAdapter createSymbolButton() {
			button = new ButtonAdapter(screen,
				new Vector2f(x,y),
				new Vector2f(w,h)
			) {
				@Override
				public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean isToggled) {
					setGlobalSymbol(isToggled);
				}
			};
			button.setText(label);
			button.setIsToggleButton(true);
			button.setResetKeyboardFocus(false);
			button.removeEffect(Effect.EffectEvent.Hover);
			return button;
		}
		
		public ButtonAdapter createBackButton() {
			button = new ButtonAdapter(screen,
				new Vector2f(x,y),
				new Vector2f(w,h)
			) {
				@Override
				public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean isToggled) {
					KeyInputEvent nEvt = null;
					nEvt = new KeyInputEvent(KeyInput.KEY_BACK, symbolCharacter, true, false);
					nEvt.setTime(System.currentTimeMillis());
					screen.onKeyEvent(nEvt);
				}
				@Override
				public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean isToggled) {
					KeyInputEvent nEvt = null;
					nEvt = new KeyInputEvent(KeyInput.KEY_BACK, symbolCharacter, false, false);
					nEvt.setTime(System.currentTimeMillis());
					screen.onKeyEvent(nEvt);
				}
				@Override
				public void onButtonStillPressedInterval() {
					KeyInputEvent nEvt = null;
					nEvt = new KeyInputEvent(KeyInput.KEY_BACK, symbolCharacter, true, false);
					nEvt.setTime(System.currentTimeMillis());
					screen.onKeyEvent(nEvt);
				}
			};
			button.setText(label);
			button.setResetKeyboardFocus(false);
			button.setInterval(15);
			button.removeEffect(Effect.EffectEvent.Hover);
			return button;
		}
		
		public ButtonAdapter createSpaceButton() {
			button = new ButtonAdapter(screen,
				new Vector2f(x,y),
				new Vector2f(w,h)
			) {
				@Override
				public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean isToggled) {
					KeyInputEvent nEvt = null;
					nEvt = new KeyInputEvent(KeyInput.KEY_S, ' ', true, false);
					nEvt.setTime(System.currentTimeMillis());
					screen.onKeyEvent(nEvt);
				}
				@Override
				public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean isToggled) {
					KeyInputEvent nEvt = null;
					nEvt = new KeyInputEvent(KeyInput.KEY_S, ' ', false, false);
					nEvt.setTime(System.currentTimeMillis());
					screen.onKeyEvent(nEvt);
				}
				@Override
				public void onButtonStillPressedInterval() {
					KeyInputEvent nEvt = null;
					nEvt = new KeyInputEvent(KeyInput.KEY_S, ' ', true, false);
					nEvt.setTime(System.currentTimeMillis());
					screen.onKeyEvent(nEvt);
				}
			};
			button.setText(label);
			button.setResetKeyboardFocus(false);
		//	button.setInterval(15);
			button.removeEffect(Effect.EffectEvent.Hover);
			return button;
		}
		
		public ButtonAdapter createEnterButton() {
			button = new ButtonAdapter(screen,
				new Vector2f(x,y),
				new Vector2f(w,h)
			) {
				@Override
				public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean isToggled) {
					KeyInputEvent nEvt = null;
					nEvt = new KeyInputEvent(KeyInput.KEY_RETURN, symbolCharacter, true, false);
					nEvt.setTime(System.currentTimeMillis());
					screen.onKeyEvent(nEvt);
				}
				@Override
				public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean isToggled) {
					KeyInputEvent nEvt = null;
					nEvt = new KeyInputEvent(KeyInput.KEY_RETURN, symbolCharacter, false, false);
					nEvt.setTime(System.currentTimeMillis());
					screen.onKeyEvent(nEvt);
				}
				@Override
				public void onButtonStillPressedInterval() {
					KeyInputEvent nEvt = null;
					nEvt = new KeyInputEvent(KeyInput.KEY_RETURN, symbolCharacter, true, false);
					nEvt.setTime(System.currentTimeMillis());
					screen.onKeyEvent(nEvt);
				}
			};
			button.setText(label);
			button.setResetKeyboardFocus(false);
			button.setInterval(15);
			return button;
		}
		
		public ButtonAdapter getButton() { return this.button; }
		
		public void setShift(boolean shift) {
			this.shift = shift;
			if (shift)	button.setText(shiftLabel);
			else		button.setText(label);
		}
		
		public void setSymbol(boolean symbol) {
			this.symbol = symbol;
			if (symbol)	button.setText(symbolLabel);
			else {
				if (shift)	button.setText(shiftLabel);
				else		button.setText(label);
			}
		}
	}
}
