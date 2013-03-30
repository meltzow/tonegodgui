/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.windows;

import com.jme3.font.BitmapFont;
import com.jme3.font.LineWrapMode;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.form.Form;
import tonegod.gui.controls.scrolling.ScrollArea;
import tonegod.gui.core.Screen;
import tonegod.gui.core.utils.UIDUtil;

/**
 *
 * @author t0neg0d
 */
public abstract class AlertBox extends Window {
	private ScrollArea dlg;
	private ButtonAdapter btnOk;
	protected Form form;
	
	/**
	 * Creates a new instance of the AlertBox control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param position A Vector2f containing the x/y position of the Element
	 */
	public AlertBox(Screen screen, Vector2f position) {
		this(screen, UIDUtil.getUID(), position,
			screen.getStyle("Window").getVector2f("defaultSize"),
			screen.getStyle("Window").getVector4f("resizeBorders"),
			screen.getStyle("Window").getString("defaultImg")
		);
	}
	
	/**
	 * Creates a new instance of the AlertBox control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 */
	public AlertBox(Screen screen, Vector2f position, Vector2f dimensions) {
		this(screen, UIDUtil.getUID(), position, dimensions,
			screen.getStyle("Window").getVector4f("resizeBorders"),
			screen.getStyle("Window").getString("defaultImg")
		);
	}
	
	/**
	 * Creates a new instance of the AlertBox control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 * @param resizeBorders A Vector4f containg the border information used when resizing the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg The default image to use for the AlertBox window
	 */
	public AlertBox(Screen screen, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		this(screen, UIDUtil.getUID(), position, dimensions, resizeBorders, defaultImg);
	}
	
	/**
	 * Creates a new instance of the AlertBox control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 */
	public AlertBox(Screen screen, String UID, Vector2f position) {
		this(screen, UID, position,
			screen.getStyle("Window").getVector2f("defaultSize"),
			screen.getStyle("Window").getVector4f("resizeBorders"),
			screen.getStyle("Window").getString("defaultImg")
		);
	}
	
	/**
	 * Creates a new instance of the AlertBox control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 */
	public AlertBox(Screen screen, String UID, Vector2f position, Vector2f dimensions) {
		this(screen, UID, position, dimensions,
			screen.getStyle("Window").getVector4f("resizeBorders"),
			screen.getStyle("Window").getString("defaultImg")
		);
	}
	
	/**
	 * Creates a new instance of the AlertBox control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 * @param resizeBorders A Vector4f containg the border information used when resizing the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg The default image to use for the AlertBox window
	 */
	public AlertBox(Screen screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg);
		
		form = new Form(screen);
		
		Vector4f indents = screen.getStyle("Window").getVector4f("contentIndents");
		float controlSpacing = screen.getStyle("Common").getFloat("defaultControlSpacing");
		
		dlg = new ScrollArea(screen, UID + ":dialog",
			new Vector2f(
				indents.y,
				indents.x+getDragBarHeight()+controlSpacing
			),
			new Vector2f(
				getWidth()-indents.y-indents.z,
				getHeight()-indents.x-indents.w-getDragBarHeight()-screen.getStyle("Window").getFloat("buttonAreaHeight")-(controlSpacing*2)
			),
			true
		);
	//	dlg.setFontColor(ColorRGBA.LightGray);
	//	dlg.setTextAlign(BitmapFont.Align.Left);
	//	dlg.setTextPosition(5,5);
	//	dlg.setTextWrap(LineWrapMode.Word);
		dlg.setIsResizable(false);
		dlg.setScaleEW(true);
		dlg.setScaleNS(true);
		dlg.setClippingLayer(dlg);
	//	dlg.setPadding(5);
		addChild(dlg);
		
		btnOk = new ButtonAdapter(screen,  UID + ":btnOk",
			new Vector2f(
				getWidth()-screen.getStyle("Button").getVector2f("defaultSize").x-indents.z,
				getHeight()-screen.getStyle("Button").getVector2f("defaultSize").y-indents.w
			)
		) {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				onButtonOkPressed(evt, toggled);
			}
		};
		btnOk.setText("Ok");
		btnOk.setDockS(true);
		btnOk.setDockE(true);
		addChild(btnOk);
		form.addFormElement(btnOk);
	}
	
	/**
	 * Sets the message to display in the AlertBox
	 * @param text String The message
	 */
	public void setMsg(String text) {
		dlg.setText(text);
	}
	
	/**
	 * Sets the text of the Ok button
	 * @param text String
	 */
	public void setButtonOkText(String text) {
		btnOk.setText(text);
	}
	
	/**
	 * Abstract method for handling Ok button click event
	 * @param evt MouseButtonEvent
	 * @param toggled boolean
	 */
	public abstract void onButtonOkPressed(MouseButtonEvent evt, boolean toggled);
	
	/**
	 * Sets the tooltip text to display when mouse hovers over the Ok button
	 * @param tip String
	 */
	public void setToolTipOkButton(String tip) {
		this.btnOk.setToolTipText(tip);
	}
}
