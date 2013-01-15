/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.windows;

import com.jme3.app.Application;
import com.jme3.font.BitmapFont;
import com.jme3.font.LineWrapMode;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import tonegod.gui.controls.buttons.Button;
import tonegod.gui.controls.scrolling.ScrollArea;
import tonegod.gui.controls.scrolling.VScrollBar;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 *
 * @author t0neg0d
 */
public abstract class AlertBox extends Window {
	private ScrollArea dlg;
	private Button btnOk;
	
	public AlertBox(Screen screen, String UID, Vector2f position) {
		this(screen, UID, position,
			screen.getStyle("Window").getVector2f("defaultSize"),
			screen.getStyle("Window").getVector4f("resizeBorders"),
			screen.getStyle("Window").getString("defaultImg")
		);
	}
	
	public AlertBox(Screen screen, String UID, Vector2f position, Vector2f dimensions) {
		this(screen, UID, position, dimensions,
			screen.getStyle("Window").getVector4f("resizeBorders"),
			screen.getStyle("Window").getString("defaultImg")
		);
	}
	
	public AlertBox(Screen screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg);
		
		dlg = new ScrollArea(screen, UID + ":dialog", new Vector2f(10, 60), new Vector2f(getWidth()-45, getHeight()-60-35), true);
		dlg.setFontColor(ColorRGBA.LightGray);
		dlg.setTextAlign(BitmapFont.Align.Left);
		dlg.setTextPosition(5,5);
		dlg.setTextWrap(LineWrapMode.Word);
		dlg.setIsResizable(false);
		dlg.setScaleEW(true);
		dlg.setScaleNS(true);
		dlg.setClippingLayer(dlg);
		dlg.setPadding(5);
		addChild(dlg);
		
		btnOk = new Button(screen,  UID + ":btnOk", new Vector2f(getWidth()-100-17, 19)) {
			@Override
			public void onMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
				onBtnOkMouseLeftDown(evt, toggled);
			}
			@Override
			public void onMouseRightDown(MouseButtonEvent evt, boolean toggled) {
				onBtnOkMouseRightDown(evt, toggled);
			}
			@Override
			public void onMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				onBtnOkMouseLeftUp(evt, toggled);
			}
			@Override
			public void onMouseRightUp(MouseButtonEvent evt, boolean toggled) {
				onBtnOkMouseRightUp(evt, toggled);
			}
			@Override
			public void onStillPressedInterval() { onBtnOkInterval(); }
			@Override
			public void onButtonFocus(MouseMotionEvent evt) {  }
			@Override
			public void onButtonLostFocus(MouseMotionEvent evt) {  }
		};
	//	btnOk.setButtonHoverInfo("Textures/button_x_h.png", ColorRGBA.White);
	//	btnOk.setButtonPressedInfo("Textures/button_x_d.png", ColorRGBA.DarkGray);
	//	btnOk.setFontColor(ColorRGBA.LightGray);
	//	btnOk.setFontSize(16);
		btnOk.setText("Ok");
		btnOk.setDockS(true);
		btnOk.setDockE(true);
	//	btnOk.setClippingLayer(this);
		addChild(btnOk);
	}
	
	public void setMsg(String text) {
		dlg.setText(text);
	}
	
	public void setButtonOkText(String text) {
		btnOk.setText(text);
	}
	
	public abstract void onBtnOkMouseLeftDown(MouseButtonEvent evt, boolean toggled);
	public abstract void onBtnOkMouseLeftUp(MouseButtonEvent evt, boolean toggled);
	public abstract void onBtnOkMouseRightDown(MouseButtonEvent evt, boolean toggled);
	public abstract void onBtnOkMouseRightUp(MouseButtonEvent evt, boolean toggled);
	public abstract void onBtnOkInterval();
}
