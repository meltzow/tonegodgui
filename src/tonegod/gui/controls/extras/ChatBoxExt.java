/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.extras;

import com.jme3.font.BitmapText;
import com.jme3.font.LineWrapMode;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.buttons.CheckBox;
import tonegod.gui.controls.form.Form;
import tonegod.gui.controls.lists.SelectBox;
import tonegod.gui.controls.lists.Spinner;
import tonegod.gui.controls.menuing.MenuItem;
import tonegod.gui.controls.scrolling.ScrollArea;
import tonegod.gui.controls.text.Label;
import tonegod.gui.controls.text.TextField;
import tonegod.gui.controls.windows.AlertBox;
import tonegod.gui.controls.windows.Panel;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;
import tonegod.gui.core.utils.BitmapTextUtil;

/**
 *
 * @author t0neg0d
 */
public abstract class ChatBoxExt extends Panel {
	private ScrollArea saChatArea;
	private TextField tfChatInput;
	private ButtonAdapter btnChatSendMsg;
	private ButtonAdapter btnChatFilter;
	private float btnChatFilterHeight = 20;
	private SelectBox sbDefaultChannel;
	private float saContentPadding;
	private boolean showSendButton = true;
	private boolean showFilterButton = true;
	private Form chatForm;
	
	private Window filters = null;
	private ScrollArea filtersScrollArea = null;
	float filterLineHeight;
	
	float controlSpacing, controlSize, buttonWidth, scrollSize;
	Vector4f indents;
	
	private int sendKey;
	private int chatHistorySize = 30;
	protected List<ChatMessage> chatMessages = new ArrayList();
	
	protected List<ChatChannel> channels = new ArrayList();
	private String defaultCommand;
	
	List<Label> displayMessages = new ArrayList();
	
	/**
	 * Creates a new instance of the ChatBox control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 */
	public ChatBoxExt(Screen screen, String UID, Vector2f position) {
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
	public ChatBoxExt(Screen screen, String UID, Vector2f position, Vector2f dimensions) {
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
	public ChatBoxExt(Screen screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg);
		
		this.setIsMovable(true);
		this.setIsResizable(true);
		this.setScaleNS(false);
		this.setScaleEW(false);
		
		chatForm = new Form(screen);
		saContentPadding = screen.getStyle("ChatBox").getFloat("contentPadding");
		
		indents = screen.getStyle("Window").getVector4f("contentIndents");
		controlSpacing = screen.getStyle("Common").getFloat("defaultControlSpacing");
		controlSize = screen.getStyle("Common").getFloat("defaultControlSize");
		buttonWidth = screen.getStyle("Button").getVector2f("defaultSize").x;
		scrollSize = screen.getStyle("ScrollArea#VScrollBar").getFloat("defaultControlSize");
		
		saChatArea = new ScrollArea(screen, UID + ":ChatArea",
			new Vector2f(
				indents.y,
				indents.x
			),
			new Vector2f(
				getWidth()-indents.y-indents.z,
				getHeight()-controlSize-(controlSpacing*2)-indents.x-indents.w
			),
			false
		) {
			@Override
			public void controlResizeHook() {
				float totalHeight = 0;
				int index = 0;
				for (Label l : displayMessages) {
					l.setHeight(l.getTextElement().getHeight());
					totalHeight += l.getHeight();
					index++;
				}
				if (totalHeight > saChatArea.getHeight()) {
					saChatArea.getScrollableArea().setHeight(totalHeight+(saChatArea.getPadding()*2));
				}
				totalHeight = 0;
				for (Label l : displayMessages) {
					totalHeight += l.getHeight();
					l.setX(saContentPadding);
					l.setWidth(saChatArea.getWidth()-(saContentPadding*2));
					l.setY(saChatArea.getScrollableArea().getHeight()-totalHeight);
				}
				if (getVScrollBar() != null) {
					getVScrollBar().setThumbScale();
				}
				adjustWidthForScroll();
			}
		};
		saChatArea.setIsResizable(false);
		saChatArea.setScaleEW(true);
		saChatArea.setScaleNS(true);
		saChatArea.setClippingLayer(saChatArea);
		saChatArea.getScrollableArea().setIgnoreMouse(true);
		saChatArea.getScrollableArea().setDockS(true);
		saChatArea.setText("");
		addChild(saChatArea);
		
		
		btnChatFilter = new ButtonAdapter(
			screen,
			UID + ":ChatFilter",
			new Vector2f(indents.y,getHeight()-controlSize-indents.w),
			new Vector2f(controlSize, controlSize)
		) {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean isToggled) {
				if (filters == null) {
					filters = new Window(
						screen,
						getElementParent().getUID()+":FilterWindow",
						new Vector2f(screen.getWidth()/2-225,screen.getHeight()/2-175),
						new Vector2f(450,350)
					);
					filters.setWindowTitle("Chat Filters");
					filters.setIsResizable(false);
					
					filtersScrollArea = new ScrollArea(
						screen,
						filters.getUID() + ":ScrollArea",
						new Vector2f(
							indents.y,
							indents.x+filters.getDragBarHeight()+controlSpacing
						),
						new Vector2f(
							filters.getWidth()-indents.y-indents.z,
							filters.getHeight()-indents.x-indents.w-filters.getDragBarHeight()-screen.getStyle("Window").getFloat("buttonAreaHeight")-(controlSpacing*2)
						),
						false
					);
					filtersScrollArea.getScrollableArea().setIgnoreMouse(true);
					filters.addChild(filtersScrollArea);
					
					ButtonAdapter btnFiltersClose = new ButtonAdapter(
						screen,
						filters.getUID() + ":btnClose",
						new Vector2f(filters.getWidth()-buttonWidth-indents.z,filters.getHeight()-controlSize-controlSpacing-indents.w)
					) {
						@Override
						public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean isToggled) {
							filters.hideWindow();
						}
					};
					btnFiltersClose.setText("Close");
					btnFiltersClose.setDockS(true);
					btnFiltersClose.setDockE(true);
					filters.addChild(btnFiltersClose);

					screen.addElement(filters);
				}
				showFiltersWindow();
			}
		};
	//	btnChatFilter.setFontSize(16);
		btnChatFilter.setDockS(true);
		btnChatFilter.setDockW(true);
		btnChatFilter.setScaleEW(false);
		btnChatFilter.setScaleNS(false);
		btnChatFilter.setText("F");
		
		chatForm.addFormElement(btnChatFilter);
		addChild(btnChatFilter);
		
		sbDefaultChannel = new SelectBox(
			screen,
			UID + ":DefaultChannel",
			new Vector2f(indents.y+controlSize, getHeight()-controlSize-indents.w),
			new Vector2f(100-controlSize, controlSize)
		) {
			@Override
			public void onChange(int selectedIndex, Object value) {
			//	throw new UnsupportedOperationException("Not supported yet.");
			}
		};
		sbDefaultChannel.setDockS(true);
		sbDefaultChannel.setDockW(true);
		sbDefaultChannel.setScaleEW(false);
		sbDefaultChannel.setScaleNS(false);
		
		chatForm.addFormElement(sbDefaultChannel);
		addChild(sbDefaultChannel);
		
		tfChatInput = new TextField(
			screen,
			UID + ":ChatInput",
			new Vector2f(indents.y+sbDefaultChannel.getWidth()+(controlSize*2), getHeight()-controlSize-indents.w),
			new Vector2f(getWidth()-sbDefaultChannel.getWidth()-(controlSize*2)-indents.y-indents.z-buttonWidth, controlSize)
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
		
		
		chatForm.addFormElement(btnChatSendMsg);
		addChild(btnChatSendMsg);
		chatForm.addFormElement(tfChatInput);
		addChild(tfChatInput);
		
		populateEffects("Window");
	}
	
	private void sendMsg() {
		if (tfChatInput.getText().length() > 0) {
			if (!tfChatInput.getText().equals("")) {
				String command = (String)sbDefaultChannel.getSelectedListItem().getValue();
				onSendMsg(command, tfChatInput.getText());
				tfChatInput.setTextFieldText("");
			}
		}
	}
	
	public void receiveMsg(String command, String msg) {
	//	System.out.println(command);
		ChatChannel channel = getChannelByCommand(command);
		chatMessages.add(new ChatMessage(channel, msg));
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
		saChatArea.getScrollableArea().removeAllChildren();
		saChatArea.getScrollableArea().setY(0);
		saChatArea.getScrollableArea().setHeight(saChatArea.getHeight());
		displayMessages.clear();
		
		float totalHeight = 0;
		for (ChatMessage cm : chatMessages) {
			if (!cm.getChannel().getIsFiltered()) {
				Label l = createMessageLabel(index, cm);
				displayMessages.add(l);
				saChatArea.addScrollableChild(l);
				l.setHeight(l.getTextElement().getHeight());
				totalHeight += l.getHeight();
				index++;
			}
		}
		saChatArea.getScrollableArea().setHeight(totalHeight+(saChatArea.getPadding()*2));
		totalHeight = 0;
		for (Label l : displayMessages) {
			totalHeight += l.getHeight();
			l.setX(saContentPadding);
			l.setWidth(saChatArea.getWidth()-(saContentPadding*2));
			l.setY(saChatArea.getScrollableArea().getHeight()-totalHeight);
		}
		saChatArea.scrollToBottom();
	}
	
	private Label createMessageLabel(int index, ChatMessage cm) {
		String s = cm.getMsg();
		Label l = new Label(
			screen,
			getUID() + ":Label" + index,
			new Vector2f(0, 0),
			new Vector2f(saChatArea.getWidth(),25)
		);
		l.setTextWrap(LineWrapMode.Word);
		l.setScaleEW(true);
		l.setScaleNS(false);
		l.setDockN(true);
		l.setDockW(true);
		l.setIsResizable(false);
		l.setIsMovable(false);
		l.setIgnoreMouse(true);
		l.setClippingLayer(saChatArea);
		l.setClipPadding(saContentPadding);
		l.setFontColor(cm.getChannel().getColor());
		l.setFontSize(saChatArea.getFontSize());
		l.setText("[" + cm.getChannel().getName() + "] " + s);
		l.setHeight(l.getTextElement().getHeight());
		l.setIgnoreMouse(true);
		
		return l;
	}
	
	public void setSendKey(int sendKey) {
		this.sendKey = sendKey;
	}
	
	public abstract void onSendMsg(String command, String msg);
	
	public final void addChatChannel(String UID, String name, Object command, String filterDisplayText, ColorRGBA color, boolean visibleToUser) {
		channels.add(new ChatChannel(UID, name, command, filterDisplayText, color, visibleToUser));
		if (visibleToUser) {
			this.sbDefaultChannel.addListItem(name, command);
			this.sbDefaultChannel.pack();
		}
	}
	
	private ChatChannel getChannelByCommand(Object command) {
		ChatChannel c = null;
		for (ChatChannel channel : channels) {
			if (channel.getCommand() == command) {
				c = channel;
				break;
			}
		}
		return c;
	}
	
	private ChatChannel getChannelByName(String name) {
		ChatChannel c = null;
		for (ChatChannel channel : channels) {
			if (channel.getName().equals(name)) {
				c = channel;
				break;
			}
		}
		return c;
	}
	
	public void showFilterButton(boolean showFilterButton) {
		if (showFilterButton) {
			if (btnChatFilter.getParent() == null) {
				this.attachChild(btnChatFilter);
				chatForm.addFormElement(btnChatFilter);
				sbDefaultChannel.setX(indents.y+controlSize);
				tfChatInput.setX(indents.y+sbDefaultChannel.getWidth()+(controlSize*2));
				if (showSendButton)
					tfChatInput.setWidth(getWidth()-sbDefaultChannel.getWidth()-(controlSize*2)-indents.y-indents.z-buttonWidth);
				else
					tfChatInput.setWidth(getWidth()-sbDefaultChannel.getWidth()-(controlSize*2)-indents.y-indents.z);
			}
		} else {
			if (btnChatFilter.getParent() != null) {
				btnChatFilter.removeFromParent();
				chatForm.removeFormElement(btnChatFilter);
				sbDefaultChannel.setX(indents.y);
				tfChatInput.setX(indents.y+sbDefaultChannel.getWidth()+controlSize);
				if (showSendButton)
					tfChatInput.setWidth(getWidth()-sbDefaultChannel.getWidth()-controlSize-indents.y-indents.z-buttonWidth);
				else
					tfChatInput.setWidth(getWidth()-sbDefaultChannel.getWidth()-controlSize-indents.y-indents.z);
			}
		}
		this.showFilterButton = showFilterButton;
	}
	
	public void showSendButton(boolean showSendButton) {
		if (showSendButton) {
			if (btnChatSendMsg.getParent() == null) {
				this.attachChild(btnChatSendMsg);
				chatForm.addFormElement(btnChatSendMsg);
				tfChatInput.setWidth(tfChatInput.getWidth()-btnChatSendMsg.getWidth());
			}
		} else {
			if (btnChatSendMsg.getParent() != null) {
				btnChatSendMsg.removeFromParent();
				chatForm.removeFormElement(btnChatSendMsg);
				tfChatInput.setWidth(tfChatInput.getWidth()+btnChatSendMsg.getWidth());
			}
		}
		this.showSendButton = showSendButton;
	}
	
	public class ChatMessage {
		private ChatChannel channel;
		private String msg;
		public ChatMessage(ChatChannel channel, String msg) {
			this.channel = channel;
			this.msg = msg;
		}
		
		public ChatChannel getChannel() {
			return channel;
		}
		public String getMsg() {
			return this.msg;
		}
	}
	
	public class ChatChannel {
		private String UID;
		private String name;
		private String filterDisplayText;
		private Object command;
		private ColorRGBA color;
		private boolean visibleToUser;
		private boolean isFiltered = false;
		
		public ChatChannel(String UID, String name, Object command, String filterDisplayText, ColorRGBA color, boolean visibleToUser) {
			this.UID = UID;
			this.name = name;
			this.command = command;
			this.filterDisplayText = filterDisplayText;
			this.color = color;
			this.visibleToUser = visibleToUser;
		}
		
		public String getUID() { return this.UID; }
		public String getName() {
			return this.name;
		}
		public Object getCommand() {
			return this.command;
		}
		public ColorRGBA getColor() {
			return this.color;
		}
		public boolean getVisibleToUser() { return visibleToUser; }
		public void setIsFiltered(boolean isFiltered) { this.isFiltered = isFiltered; }
		public boolean getIsFiltered() { return this.isFiltered; }
		public String getFilterDisplayText() { return filterDisplayText; }
	}
	
	public void setChannelFiltered(ChatChannel channel, boolean filter) {
		channel.setIsFiltered(filter);
		rebuildChat();
	}
	
	protected void showFiltersWindow() {
		Element scrollableArea = filtersScrollArea.getScrollableArea();
		filtersScrollArea.setClipPadding(10);
		
		scrollableArea.removeAllChildren();
		scrollableArea.setY(0);
		scrollableArea.setHeight(filtersScrollArea.getHeight());
		
		boolean init = true;
		String finalString = "";
		float currentHeight = 0;
		int index = 0;
		
		for (ChatChannel channel : channels) {
			filterLineHeight = BitmapTextUtil.getTextLineHeight(scrollableArea, "      " + channel.getFilterDisplayText() + "  ");
			if (init) {
				finalString = "        " + channel.getFilterDisplayText() + "  ";
				init = false;
			} else {
				finalString += "\n        " + channel.getFilterDisplayText() + "  ";
			}
			currentHeight += filterLineHeight;
		}
		currentHeight -= filterLineHeight;
		scrollableArea.setHeight(currentHeight);
		scrollableArea.setWidth(getWidth());
		scrollableArea.setText(finalString);
		
		index = 0;
		for (ChatChannel channel : channels) {
			this.addCheckBox(index, channel);
			index++;
		}
		
		filtersScrollArea.scrollToBottom();
		filters.showWindow();
	}
	
	private void addCheckBox(int index, ChatChannel channel) {
		CheckBox checkbox = new CheckBox(screen, filtersScrollArea.getUID() + ":CheckBox:" + index,
			new Vector2f(12,10+(index*filterLineHeight))
		) {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean isToggled) {
				((ChatChannel)getElementUserData()).setIsFiltered(!isToggled);
				rebuildChat();
			}
		};
		checkbox.setElementUserData(channel);
		checkbox.setScaleEW(false);
		checkbox.setScaleNS(false);
		checkbox.setDockS(true);
		checkbox.setDockW(true);
		checkbox.setIsResizable(false);
		checkbox.setIsMovable(false);
		checkbox.setIgnoreMouse(false);
		checkbox.setClippingLayer(filtersScrollArea);
		if (!channel.getIsFiltered())
			checkbox.setIsChecked(true);
		filtersScrollArea.addScrollableChild(checkbox);

	//	if (!getIsVisible())
	//		checkbox.hide();
	}
	
	public void setToolTipTextInput(String tip) {
		this.tfChatInput.setToolTipText(tip);
	}
	public void setToolTipSendButton(String tip) {
		this.btnChatSendMsg.setToolTipText(tip);
	}
}
