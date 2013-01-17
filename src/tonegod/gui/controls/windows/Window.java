/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.windows;

import com.jme3.font.BitmapFont;
import com.jme3.font.LineWrapMode;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 *
 * @author t0neg0d
 */
public class Window extends Element {
	Element dragBar;
	
	/**
	 * Creates a new instance of the Window control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 */
	public Window(Screen screen, String UID, Vector2f position) {
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
	public Window(Screen screen, String UID, Vector2f position, Vector2f dimensions) {
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
	public Window(Screen screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg);
		
		this.setIsResizable(true);
		this.setScaleNS(false);
		this.setScaleEW(false);
	//	this.setClippingLayer(this);
		
		dragBar = new Element(screen, UID + ":DragBar", new Vector2f(5, getHeight()-25-5), new Vector2f(getWidth()-10, 25),
			screen.getStyle("Window#Dragbar").getVector4f("resizeBorders"),
			screen.getStyle("Window#Dragbar").getString("defaultImg")
		);
		dragBar.setFontSize(screen.getStyle("Window#Dragbar").getFloat("fontSize"));
		dragBar.setFontColor(screen.getStyle("Window#Dragbar").getColorRGBA("fontColor"));
		dragBar.setTextAlign(BitmapFont.Align.valueOf(screen.getStyle("Window#Dragbar").getString("textAlign")));
		dragBar.setTextPosition(0,0);
		dragBar.setTextPadding(screen.getStyle("Window#Dragbar").getFloat("textPadding"));
		dragBar.setTextWrap(LineWrapMode.valueOf(screen.getStyle("Window#Dragbar").getString("textWrap")));
		dragBar.setIsResizable(false);
		dragBar.setScaleEW(true);
		dragBar.setScaleNS(false);
		dragBar.setIsMovable(true);
		dragBar.setEffectParent(true);
		dragBar.setClippingLayer(this);
		
		addChild(dragBar);
	//	this.setTextVAlign(BitmapFont.VAlign.Center);
	//	this.setTextAlign(BitmapFont.Align.Center);
	//	this.setTextWrap(LineWrapMode.Clip);
		
		populateEffects("Window");
	}
	
	/**
	 * Returns a pointer to the Element used as a window dragbar
	 * @return Element
	 */
	public Element getDragBar() {
		return this.dragBar;
	}
	
	/**
	 * Sets the Window title text
	 * @param title String
	 */
	public void setWindowTitle(String title) {
		dragBar.setText(title);
	}
}
