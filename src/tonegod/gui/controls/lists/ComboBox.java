/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.lists;

import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import tonegod.gui.controls.buttons.Button;
import tonegod.gui.controls.menuing.Menu;
import tonegod.gui.controls.menuing.MenuItem;
import tonegod.gui.controls.text.TextField;
import tonegod.gui.core.Screen;

/**
 *
 * @author t0neg0d
 */
public class ComboBox extends TextField {
	private Button btnArrowDown;
	private Menu DDList = null;
	float btnHeight = 25;
	String ddUID;
	private int selectedIndex;
	private String selectedValue;
	private String selectedCaption;
	
	private int hlIndex;
	private String hlValue;
	private String hlCaption;
	
	private int ssIndex;
	private String ssValue;
	private String ssCaption;
	
	public ComboBox(Screen screen, String UID, Vector2f position) {
		this(screen, UID, position,
			screen.getStyle("TextField").getVector2f("defaultSize"),
			screen.getStyle("TextField").getVector4f("resizeBorders"),
			screen.getStyle("TextField").getString("defaultImg")
		);
	}
	
	public ComboBox(Screen screen, String UID, Vector2f position, Vector2f dimensions) {
		this(screen, UID, position, dimensions,
			screen.getStyle("TextField").getVector4f("resizeBorders"),
			screen.getStyle("TextField").getString("defaultImg")
		);
	}
	
	public ComboBox(Screen screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg);
		
		ddUID = UID + ":ddMenu";
		
		btnArrowDown = new Button(screen, UID + ":ArrowDown", new Vector2f(getWidth(),0), new Vector2f(btnHeight, btnHeight)) {
			@Override
			public void onMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
				
			}
			@Override
			public void onMouseRightDown(MouseButtonEvent evt, boolean toggled) {
			//	throw new UnsupportedOperationException("Not supported yet.");
			}
			@Override
			public void onMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				Menu m = ((Menu)screen.getElementById(ddUID));
				m.showMenu(null, getElementParent().getX(), getElementParent().getY()-m.getHeight());
				((ComboBox)getElementParent()).setTabFocus();
			}
			@Override
			public void onMouseRightUp(MouseButtonEvent evt, boolean toggled) {
			//	throw new UnsupportedOperationException("Not supported yet.");
			}
			@Override
			public void onButtonFocus(MouseMotionEvent evt) {
			//	throw new UnsupportedOperationException("Not supported yet.");
			}
			@Override
			public void onButtonLostFocus(MouseMotionEvent evt) {
			//	throw new UnsupportedOperationException("Not supported yet.");
			}
			@Override
			public void onStillPressedInterval() {
			//	throw new UnsupportedOperationException("Not supported yet.");
			}
		};
		btnArrowDown.setButtonIcon(18, 18, screen.getStyle("Common").getString("arrowDown"));
		
		this.addChild(btnArrowDown);
	}
	
	public void addListItem(String caption, String value) {
		if (DDList == null) {
			DDList = new Menu(screen, ddUID, new Vector2f(0,0), true) {
				@Override
				public void onMenuItemClicked(int index, String value) {
					((ComboBox)getCallerElement()).setSelected(index, DDList.getMenuItem(index).getCaption(), value);
					hide();
				}
			};
			DDList.setCallerElement(this);
		}
		DDList.addMenuItem(caption, value, null);
	}
	
	public void pack() {
		DDList.pack();
		DDList.setIsResizable(true);
		DDList.setResizeN(false);
		DDList.setResizeW(false);
		DDList.setResizeE(true);
		DDList.setResizeS(true);
		DDList.resize(getWidth()+(getTextPadding()*2), DDList.getMenuItemHeight()*4, Borders.SE);
		DDList.setResizeE(false);
		
		screen.addElement(DDList);
		DDList.hide();
	}
	
	protected void setSelected(int index, String caption, String value) {
		this.selectedIndex = index;
		this.selectedCaption = caption;
		this.selectedValue = value;
		setTextFieldText(selectedCaption);
	}
	
	@Override
	public void controlKeyPressHook(KeyInputEvent evt, String text) {
		if (evt.getKeyCode() != KeyInput.KEY_UP && evt.getKeyCode() != KeyInput.KEY_DOWN && evt.getKeyCode() != KeyInput.KEY_RETURN) {
			int miIndexOf = 0;
			int strIndex = -1;
			for (MenuItem mi : DDList.getMenuItems()) {
				strIndex = mi.getCaption().toLowerCase().indexOf(text.toLowerCase());
				if (strIndex == 0) {
					ssIndex = miIndexOf;
					hlIndex = ssIndex;
					hlCaption = ssCaption = DDList.getMenuItem(miIndexOf).getCaption();
					hlValue = ssValue = DDList.getMenuItem(miIndexOf).getValue();
					
					int rIndex = DDList.getMenuItems().size()-miIndexOf;
					float diff = rIndex * DDList.getMenuItemHeight() + (DDList.getMenuPadding()*2);
					
					DDList.scrollYTo(
						( DDList.getHeight()-diff )
					);
					break;
				}
				miIndexOf++;
			}
			if (miIndexOf > -1 && miIndexOf < DDList.getMenuItems().size()-1)
				handleHightlight(miIndexOf);
			if (!DDList.getIsVisible()) DDList.showMenu(null, getX(), getY()-DDList.getHeight());
		} else {
			if (evt.getKeyCode() == KeyInput.KEY_UP) {
				if (hlIndex > 0) {
					hlIndex--;
					hlCaption = DDList.getMenuItem(hlIndex).getCaption();
					hlValue = DDList.getMenuItem(hlIndex).getValue();
					int rIndex = DDList.getMenuItems().size()-hlIndex;
					float diff = rIndex * DDList.getMenuItemHeight() + (DDList.getMenuPadding()*2);
					
					DDList.scrollYTo(
						( DDList.getHeight()-diff )
					);
					handleHightlight(hlIndex);
					setSelected(hlIndex, hlCaption, hlValue);
				}
			} else if (evt.getKeyCode() == KeyInput.KEY_DOWN) {
				if (hlIndex < DDList.getMenuItems().size()-1) {
					hlIndex++;
					hlCaption = DDList.getMenuItem(hlIndex).getCaption();
					hlValue = DDList.getMenuItem(hlIndex).getValue();
					int rIndex = DDList.getMenuItems().size()-hlIndex;
					float diff = rIndex * DDList.getMenuItemHeight() + (DDList.getMenuPadding()*2);
					
					DDList.scrollYTo(
						( DDList.getHeight()-diff )
					);
					handleHightlight(hlIndex);
					setSelected(hlIndex, hlCaption, hlValue);
				}
			}
			if (evt.getKeyCode() == KeyInput.KEY_RETURN) {
				updateSelected();
			}
		}
	}
	
	private void updateSelected() {
		setSelected(hlIndex, hlCaption, hlValue);
		if (DDList.getIsVisible()) DDList.hide();
	}
	
	private void handleHightlight(int index) {
		if (DDList.getIsVisible()) DDList.setHighlight(index);
	}
}
