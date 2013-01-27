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

/**
 *
 * @author t0neg0d
 */
public abstract class AlertBox extends Window {
	private ScrollArea dlg;
	private ButtonAdapter btnOk;
	Form form;
	
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
		
		form = new Form(screen);
		
		Vector4f indents = screen.getStyle("Window").getVector4f("contentIndents");
		float controlSpacing = screen.getStyle("Common").getFloat("defaultControlSpacing");
		
		dlg = new ScrollArea(screen, UID + ":dialog",
			new Vector2f(
				indents.y,
				indents.x+getDragBarHeight()+controlSpacing
			),
			new Vector2f(
				getWidth()-indents.y-indents.z-screen.getStyle("ScrollArea#VScrollBar").getFloat("defaultControlSize"),
				getHeight()-indents.x-indents.w-getDragBarHeight()-screen.getStyle("Window").getFloat("buttonAreaHeight")-(controlSpacing*2)
			),
			true
		);
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
	
	public void setMsg(String text) {
		dlg.setText(text);
	}
	
	public void setButtonOkText(String text) {
		btnOk.setText(text);
	}
	
	public abstract void onButtonOkPressed(MouseButtonEvent evt, boolean toggled);
}
