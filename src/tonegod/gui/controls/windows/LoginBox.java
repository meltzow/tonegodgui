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
import tonegod.gui.controls.text.Password;
import tonegod.gui.controls.text.TextField;
import tonegod.gui.controls.scrolling.ScrollArea;
import tonegod.gui.controls.scrolling.VScrollBar;
import tonegod.gui.controls.text.Label;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 *
 * @author t0neg0d
 */
public abstract class LoginBox extends Window {
	Button btnLogin, btnCancel;
	Element responseMsg, lblUserName, lblPassword;
	TextField userName;
	Password password;
	
	public LoginBox(Screen screen, String UID, Vector2f position) {
		this(screen, UID, position,
			screen.getStyle("Window").getVector2f("defaultSize"),
			screen.getStyle("Window").getVector4f("resizeBorders"),
			screen.getStyle("Window").getString("defaultImg")
		);
	}
	
	public LoginBox(Screen screen, String UID, Vector2f position, Vector2f dimensions) {
		this(screen, UID, position, dimensions,
			screen.getStyle("Window").getVector4f("resizeBorders"),
			screen.getStyle("Window").getString("defaultImg")
		);
	}
	
	public LoginBox(Screen screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg);
		
		lblUserName = new Label(screen, UID + ":Lbl:UserName", new Vector2f(15, getHeight()-70), new Vector2f((getWidth()/3)-30, 25));
		lblUserName.setTextAlign(BitmapFont.Align.Right);
		lblUserName.setText("User ID:");
		this.addChild(lblUserName);
		
		userName = new TextField(screen, UID + ":userName", new Vector2f(getWidth()/3, getHeight()-70), new Vector2f(getWidth()-(getWidth()/3)-15, 25));
		this.addChild(userName);
		
		lblPassword = new Label(screen, UID + ":Lbl:Password", new Vector2f(15, getHeight()-100), new Vector2f((getWidth()/3)-30, 25));
		lblPassword.setTextAlign(BitmapFont.Align.Right);
		lblPassword.setText("Password:");
		this.addChild(lblPassword);
		
		password = new Password(screen, UID + "password", new Vector2f(getWidth()/3, getHeight()-100), new Vector2f(getWidth()-(getWidth()/3)-15, 25));
		this.addChild(password);
		
		btnLogin = new Button(screen,  UID + ":btnOk", new Vector2f(getWidth()-100-17, 19)) {
			@Override
			public void onMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
				onBtnLoginMouseLeftDown(evt, toggled);
			}
			@Override
			public void onMouseRightDown(MouseButtonEvent evt, boolean toggled) {
				onBtnLoginMouseRightDown(evt, toggled);
			}
			@Override
			public void onMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				onBtnLoginMouseLeftUp(evt, toggled);
			}
			@Override
			public void onMouseRightUp(MouseButtonEvent evt, boolean toggled) {
				onBtnLoginMouseRightUp(evt, toggled);
			}
			@Override
			public void onStillPressedInterval() { onBtnLoginInterval(); }
			@Override
			public void onButtonFocus(MouseMotionEvent evt) {  }
			@Override
			public void onButtonLostFocus(MouseMotionEvent evt) {  }
		};
	//	btnLogin.setButtonHoverInfo("Textures/button_x_h.png", ColorRGBA.White);
	//	btnLogin.setButtonPressedInfo("Textures/button_x_d.png", ColorRGBA.DarkGray);
		btnLogin.setFontColor(ColorRGBA.LightGray);
		btnLogin.setFontSize(16);
		btnLogin.setText("Ok");
		btnLogin.setDockS(true);
		btnLogin.setDockE(true);
	//	btnLogin.setClippingLayer(this);
		addChild(btnLogin);
		
		btnCancel = new Button(screen, UID + ":btnCancel", new Vector2f(17, 19)) {
			@Override
			public void onMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
				onBtnCancelMouseLeftDown(evt, toggled);
			}
			@Override
			public void onMouseRightDown(MouseButtonEvent evt, boolean toggled) {
				onBtnCancelMouseRightDown(evt, toggled);
			}
			@Override
			public void onMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				onBtnCancelMouseLeftUp(evt, toggled);
			}
			@Override
			public void onMouseRightUp(MouseButtonEvent evt, boolean toggled) {
				onBtnCancelMouseRightUp(evt, toggled);
			}
			@Override
			public void onStillPressedInterval() { onBtnCancelInterval(); }
			@Override
			public void onButtonFocus(MouseMotionEvent evt) {  }
			@Override
			public void onButtonLostFocus(MouseMotionEvent evt) {  }
		};
	//	btnCancel.setButtonHoverInfo("Textures/button_x_h.png", ColorRGBA.White);
	//	btnCancel.setButtonPressedInfo("Textures/button_x_d.png", ColorRGBA.DarkGray);
		btnCancel.setFontColor(ColorRGBA.LightGray);
		btnCancel.setFontSize(16);
		btnCancel.setText("Cancel");
		btnCancel.setDockS(true);
		btnCancel.setDockW(true);
	//	btnCancel.setClippingLayer(this);
		addChild(btnCancel);
		
		this.setWindowTitle("Login");
	}
	
	public void setMsg(String text) {
		responseMsg.setText(text);
	}
	
	public void setButtonLoginText(String text) {
		btnLogin.setText(text);
	}
	
	public abstract void onBtnLoginMouseLeftDown(MouseButtonEvent evt, boolean toggled);
	public abstract void onBtnLoginMouseLeftUp(MouseButtonEvent evt, boolean toggled);
	public abstract void onBtnLoginMouseRightDown(MouseButtonEvent evt, boolean toggled);
	public abstract void onBtnLoginMouseRightUp(MouseButtonEvent evt, boolean toggled);
	public abstract void onBtnLoginInterval();
	
	public void setButtonCancelText(String text) {
		btnCancel.setText(text);
	}
	
	public abstract void onBtnCancelMouseLeftDown(MouseButtonEvent evt, boolean toggled);
	public abstract void onBtnCancelMouseLeftUp(MouseButtonEvent evt, boolean toggled);
	public abstract void onBtnCancelMouseRightDown(MouseButtonEvent evt, boolean toggled);
	public abstract void onBtnCancelMouseRightUp(MouseButtonEvent evt, boolean toggled);
	public abstract void onBtnCancelInterval();
}
