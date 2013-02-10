/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.windows;

import com.jme3.font.BitmapFont;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.form.Form;
import tonegod.gui.controls.text.Label;
import tonegod.gui.controls.text.Password;
import tonegod.gui.controls.text.TextField;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 *
 * @author t0neg0d
 */
public abstract class LoginBox extends Window {
	ButtonAdapter btnLogin, btnCancel;
	Element responseMsg, lblUserName, lblPassword;
	TextField userName;
	Password password;
	Form form;
	
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
		
		form = new Form(screen);
		
		Vector4f indents = screen.getStyle("Window").getVector4f("contentIndents");
		float controlSize = screen.getStyle("Common").getFloat("defaultControlSize");
		float controlSpacing = screen.getStyle("Common").getFloat("defaultControlSpacing");
		
		lblUserName = new Label(screen, UID + ":Lbl:UserName",
			new Vector2f(
				indents.y,
				getDragBarHeight()+indents.x+controlSpacing
			),
			new Vector2f(
				(getWidth()/3)-indents.y-indents.z,
				controlSize
			)
		);
		lblUserName.setTextAlign(BitmapFont.Align.Right);
		lblUserName.setText("User ID:");
		this.addChild(lblUserName);
		
		userName = new TextField(screen, UID + ":userName",
			new Vector2f(
				getWidth()/3,
				getDragBarHeight()+indents.x+controlSpacing
			),
			new Vector2f(
				getWidth()-(getWidth()/3)-indents.z,
				controlSize
			)
		);
		this.addChild(userName);
		form.addFormElement(userName);
		
		lblPassword = new Label(screen, UID + ":Lbl:Password",
			new Vector2f(
				indents.y,
				getDragBarHeight()+indents.x+controlSize+(controlSpacing*2)
			),
			new Vector2f(
				(getWidth()/3)-indents.y-indents.z,
				controlSize
			)
		);
		lblPassword.setTextAlign(BitmapFont.Align.Right);
		lblPassword.setText("Password:");
		this.addChild(lblPassword);
		
		password = new Password(screen, UID + "password",
			new Vector2f(
				getWidth()/3,
				getDragBarHeight()+indents.x+controlSize+(controlSpacing*2)
			),
			new Vector2f(
				getWidth()-(getWidth()/3)-indents.z,
				controlSize
			)
		);
		this.addChild(password);
		form.addFormElement(password);
		
		btnLogin = new ButtonAdapter(screen,  UID + ":btnOk",
			new Vector2f(
				getWidth()-screen.getStyle("Button").getVector2f("defaultSize").x-indents.z,
				getHeight()-screen.getStyle("Button").getVector2f("defaultSize").y-indents.w
			)
		) {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				onButtonLoginPressed(evt, toggled);
			}
		};
		btnLogin.setText("Login");
		btnLogin.setDockS(true);
		btnLogin.setDockE(true);
		addChild(btnLogin);
		form.addFormElement(btnLogin);
		
		btnCancel = new ButtonAdapter(screen, UID + ":btnCancel",
			new Vector2f(
				indents.y,
				getHeight()-screen.getStyle("Button").getVector2f("defaultSize").y-indents.w
			)
		) {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				onButtonCancelPressed(evt, toggled);
			}
		};
		btnCancel.setText("Cancel");
		btnCancel.setDockS(true);
		btnCancel.setDockW(true);
		addChild(btnCancel);
		form.addFormElement(btnCancel);
		
		this.setWindowTitle("Login");
	}
	
	public void setMsg(String text) {
		responseMsg.setText(text);
	}
	
	public String getTextUserName() {
		return this.userName.getText();
	}
	
	public String getTextPassword() {
		return this.password.getText();
	}
	
	public void setButtonLoginText(String text) {
		btnLogin.setText(text);
	}
	
	public abstract void onButtonLoginPressed(MouseButtonEvent evt, boolean toggled);
	
	public void setButtonCancelText(String text) {
		btnCancel.setText(text);
	}
	
	public abstract void onButtonCancelPressed(MouseButtonEvent evt, boolean toggled);
	
	public void setToolTipLoginInput(String tip) {
		userName.setToolTipText(tip);
	}
	public void setToolTipPasswordInput(String tip) {
		password.setToolTipText(tip);
	}
	public void setToolTipLoginButton(String tip) {
		this.btnLogin.setToolTipText(tip);
	}
	public void setToolTipCancelButton(String tip) {
		this.btnCancel.setToolTipText(tip);
	}
}
