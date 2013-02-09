/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.extras;

import com.jme3.font.BitmapFont;
import com.jme3.font.LineWrapMode;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import java.util.ArrayList;
import java.util.List;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.form.Form;
import tonegod.gui.controls.lists.Spinner;
import tonegod.gui.controls.scrolling.ScrollArea;
import tonegod.gui.controls.text.TextField;
import tonegod.gui.controls.windows.Panel;
import tonegod.gui.core.Screen;

/**
 *
 * @author t0neg0d
 */
public abstract class ChatBox extends Panel {
	private ScrollArea saChatArea;
	private TextField tfChatInput;
	private ButtonAdapter btnChatSendMsg;
	private Spinner spnChannels;
	
	private Form chatForm;
	
	private int sendKey;
	private int chatHistorySize = 30;
	protected List<String> chatMessages = new ArrayList();
	
	/**
	 * Creates a new instance of the ChatBox control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 */
	public ChatBox(Screen screen, String UID, Vector2f position) {
		this(screen, UID, position,
			screen.getStyle("Window").getVector2f("defaultSize"),
			screen.getStyle("Window").getVector4f("resizeBorders"),
			screen.getStyle("Window").getString("defaultImg")
		);
	}
	
	/**
	 * Creates a new instance of the ChatBox control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 */
	public ChatBox(Screen screen, String UID, Vector2f position, Vector2f dimensions) {
		this(screen, UID, position, dimensions,
			screen.getStyle("Window").getVector4f("resizeBorders"),
			screen.getStyle("Window").getString("defaultImg")
		);
	}
	
	/**
	 * Creates a new instance of the ChatBox control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 * @param resizeBorders A Vector4f containg the border information used when resizing the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg The default image to use for the Slider's track
	 */
	public ChatBox(Screen screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg);
		
		this.setIsMovable(true);
		this.setIsResizable(true);
		this.setScaleNS(false);
		this.setScaleEW(false);
		
		chatForm = new Form(screen);
		
		Vector4f indents = screen.getStyle("Window").getVector4f("contentIndents");
		float controlSpacing = screen.getStyle("Common").getFloat("defaultControlSpacing");
		float controlSize = screen.getStyle("Common").getFloat("defaultControlSize");
		float buttonWidth = screen.getStyle("Button").getVector2f("defaultSize").x;
		float scrollSize = screen.getStyle("ScrollArea#VScrollBar").getFloat("defaultControlSize");
		
		saChatArea = new ScrollArea(screen, UID + ":ChatArea",
			new Vector2f(
				indents.y,
				indents.x+controlSpacing
			),
			new Vector2f(
				getWidth()-scrollSize-indents.y-indents.z,
				getHeight()-(controlSpacing*2)-indents.x-indents.w
			),
			true
		);
		saChatArea.setIsResizable(false);
		saChatArea.setScaleEW(true);
		saChatArea.setScaleNS(true);
		saChatArea.setClippingLayer(saChatArea);
		saChatArea.setText("");
		addChild(saChatArea);
		
		tfChatInput = new TextField(
			screen,
			UID + ":ChatInput",
			new Vector2f(indents.y, getHeight()-controlSize-indents.w),
			new Vector2f(getWidth()-indents.y-indents.z-buttonWidth, controlSize)
		) {
			@Override
			public void controlKeyPressHook(KeyInputEvent evt, String text) {
				if (evt.getKeyCode() == sendKey) {
					if (tfChatInput.getText().length() > 0) {
						tfChatInput.setText(tfChatInput.getText().substring(0,tfChatInput.getText().length()-1));
						sendMsg();
					}
				}
			}
		};
		tfChatInput.setScaleEW(true);
		tfChatInput.setScaleNS(false);
		tfChatInput.setDockS(true);
		tfChatInput.setDockW(true);
		
		btnChatSendMsg = new ButtonAdapter(
			screen,
			UID + ":ChatSendMsg",
			new Vector2f(getWidth()-indents.z-buttonWidth, getHeight()-controlSize-indents.w),
			new Vector2f(buttonWidth,controlSize)
		) {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				sendMsg();
			}
		};
		btnChatSendMsg.setScaleEW(false);
		btnChatSendMsg.setScaleNS(false);
		btnChatSendMsg.setDockS(true);
		btnChatSendMsg.setDockE(true);
		btnChatSendMsg.setText("Send");
		
		addChild(btnChatSendMsg);
		addChild(tfChatInput);
		
		populateEffects("Window");
	}
	
	private void sendMsg() {
		if (tfChatInput.getText().length() > 0) {
			if (!tfChatInput.getText().equals("")) {
				onSendMsg(tfChatInput.getText());
				tfChatInput.setTextFieldText("");
			}
		}
	}
	
	public void receiveMsg(String msg) {
		chatMessages.add(msg);
		updateChatHistory();
	}
	
	private void updateChatHistory() {
		if (chatMessages.size() > chatHistorySize) {
			chatMessages.remove(0);
		}
		rebuildChat();
	}
	
	private void rebuildChat() {
		String displayText = "";
		int index = 0;
		for (String s : chatMessages) {
			if (index > 0)
				displayText += "\n" + s;
			else
				displayText += s;
			index++;
		}
		saChatArea.setText(displayText);
		saChatArea.scrollToBottom();
	}
	
	public void setSendKey(int sendKey) {
		this.sendKey = sendKey;
	}
	
	public abstract void onSendMsg(String msg);
	
	public void setToolTipTextInput(String tip) {
		this.tfChatInput.setToolTipText(tip);
	}
	public void setToolTipSendButton(String tip) {
		this.btnChatSendMsg.setToolTipText(tip);
	}
}
