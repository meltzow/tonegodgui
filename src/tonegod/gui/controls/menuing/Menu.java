/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.menuing;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.LineWrapMode;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import java.util.ArrayList;
import java.util.List;
import tonegod.gui.controls.scrolling.ScrollArea;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;
import tonegod.gui.listeners.MouseButtonListener;
import tonegod.gui.listeners.MouseMovementListener;
import tonegod.gui.listeners.MouseWheelListener;

/**
 *
 * @author t0neg0d
 */
public abstract class Menu extends ScrollArea implements MouseMovementListener, MouseWheelListener, MouseButtonListener {
	
	private List<MenuItem> menuItems = new ArrayList();
	private Element highlight;
	private int miIndex = 0;
	private float initWidth;
	private float menuItemHeight;
	private float menuPadding = 4;
	private Menu caller;
	private Element callerElement;
	float menuOverhang;
	boolean isScrollable;
	
	private int currentMenuItemIndex = -1;
	
	public Menu(Screen screen, String UID, Vector2f position, boolean isScrollable) {
		this(screen, UID, position,
			screen.getStyle("Menu").getVector2f("defaultSize"),
			screen.getStyle("Menu").getVector4f("resizeBorders"),
			screen.getStyle("Menu").getString("defaultImg"),
			isScrollable
		);
	}
	
	public Menu(Screen screen, String UID, Vector2f position, Vector2f dimensions, boolean isScrollable) {
		this(screen, UID, position, dimensions,
			screen.getStyle("Menu").getVector4f("resizeBorders"),
			screen.getStyle("Menu").getString("defaultImg"),
			isScrollable
		);
	}
	
	public Menu(Screen screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg, boolean isScrollable) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg, false);
		
		setFontSize(20);
		menuOverhang = screen.getStyle("Menu").getFloat("menuOverhang");
		menuItemHeight = screen.getStyle("Menu").getFloat("menuItemHeight");
		menuPadding = screen.getStyle("Menu").getFloat("menuPadding");
		initWidth = menuItemHeight*3;
		this.isScrollable = isScrollable;
		
		if (!isScrollable) {
			getVScrollBar().removeFromParent();
		}
		setWidth(initWidth+(menuPadding*2));
		
		highlight = new Element(
			screen,
			UID + ":Highlight",
			new Vector2f(0,0),
			new Vector2f(10,10),
			new Vector4f(1,1,1,1),
			null
		);
		highlight.setScaleEW(true);
		highlight.setScaleNS(false);
		highlight.setDockN(true);
		highlight.setDockW(true);
		highlight.setIgnoreMouse(true);
		
		populateEffects("Menu");
	}
	
	public void addMenuItem(String caption, String value, Menu subMenu) {
		this.getVScrollBar().hide();
		MenuItem menuItem = new MenuItem(
			this,
			caption,
			value,
			subMenu
		);
	//	menuItem.setMenuItemIndex(miIndex);
		this.menuItems.add(menuItem);
	//	this.addScrollableChild(menuItem);
		miIndex++;
	}
	
	public void removeMenuItem() {
		
	}
	
	public void setMenuOverhang(float menuOverhang) {
		this.menuOverhang = menuOverhang;
	}
	
	public float getMenuOverhang() {
		return this.menuOverhang;
	}
	
	public float getMenuItemHeight() {
		return this.menuItemHeight;
	}
	
	public float getInitialWidth() {
		return this.initWidth;
	}
	
	public float getMenuPadding() {
		return this.menuPadding;
	}
	
	public void pack() {
		String finalString = " ";
		
		scrollableArea.setFontSize(fontSize);
		scrollableArea.setTextWrap(LineWrapMode.Clip);
		scrollableArea.setTextVAlign(BitmapFont.VAlign.Center);
		scrollableArea.setTextPadding(menuPadding);
		scrollableArea.setText(finalString);
		scrollableArea.setIgnoreMouse(true);
		setPadding(0);
		menuItemHeight = scrollableArea.getTextElement().getLineHeight();
	
		int index = 0;
		float totalHeight = menuItems.size()*menuItemHeight;
		float currentHeight = 0;
		float width = menuItemHeight*3;
		boolean init = true;
		
		BitmapText temp = new BitmapText(font);
		temp.setSize(fontSize);
		temp.setLineWrapMode(LineWrapMode.NoWrap);
		
		for (MenuItem mi : menuItems) {
			temp.setBox(null);
			temp.setText("      " + mi.getCaption() + "  ");
			float tWidth = (menuItemHeight*2)+temp.getLineWidth();
			width = (tWidth > width) ? tWidth : width;
			if (init) {
				finalString = "      " + mi.getCaption() + "  ";
				init = false;
			} else {
				finalString += "\n      " + mi.getCaption() + "  ";
			}
			currentHeight += menuItemHeight;
			
			if (mi.getSubMenu() != null) {
				this.addSubmenuArrow(index);
			}
			index++;
		}
		scrollableArea.setText(finalString);
		
	//	getTextElement().setLocalTranslation(getTextElement().getLocalTranslation().setX(menuItemHeight));
		if (!isScrollable)
			this.resize(getX()+width+(menuPadding*2), getY()+currentHeight+(menuPadding*2), Borders.SE);
		else
			this.resize(getX()+width+(menuPadding*2), getY()+(menuItemHeight*5)+(menuPadding*2), Borders.SE);
		scrollableArea.setX(0);
		scrollableArea.setWidth(width);
		scrollableArea.setY(0);
		scrollableArea.setHeight(currentHeight+(menuPadding*2));
		
		if (highlight.getParent() == null) {
			highlight.setWidth(width);
			highlight.setHeight(menuItemHeight);
			highlight.getElementMaterial().setColor("Color", new ColorRGBA(0.7f,0.7f,0.7f,0.5f));
			highlight.setClippingLayer(this);
			scrollableArea.addChild(highlight);
		}
		
		if (getVScrollBar() != null)
			getVScrollBar().setX(width);
	}
	
	private void addSubmenuArrow(int index) {
		Element elArrow = new Element(screen, getUID() + ":Arrow:" + index,
				new Vector2f(getWidth()-menuItemHeight-(menuPadding*2), (menuItems.size()*menuItemHeight)-menuItemHeight+menuPadding-(index*menuItemHeight)),
				new Vector2f(menuItemHeight, menuItemHeight),
				new Vector4f(0,0,0,0),
				screen.getStyle("Common").getString("arrowRight")
			);
			elArrow.setScaleEW(false);
			elArrow.setScaleNS(false);
			elArrow.setDockS(true);
			elArrow.setDockE(true);
			elArrow.setIsResizable(false);
			elArrow.setIsMovable(false);
			elArrow.setIgnoreMouse(true);
			elArrow.setClippingLayer(this);
			elArrow.setTextClipPadding(this.getMenuPadding());

			addScrollableChild(elArrow);
	}
	
	public final void setCallerElement(Element el) {
		this.callerElement = el;
	}
	
	public Element getCallerElement() {
		return this.callerElement;
	}
	
	public List<MenuItem> getMenuItems() {
		return this.menuItems;
	}
	
	public MenuItem getMenuItem(int index) {
		return this.menuItems.get(index);
	}
	
	public void showMenu(Menu caller, float x, float y) {
		this.caller = caller;
		if (caller != null) {
			if (x < 0) x = 0;
			else if (x+getWidth() > screen.getWidth())
				x = caller.getAbsoluteX()-getWidth()+menuOverhang;
			if (y < 0) y = 0;
			else if (getAbsoluteHeight() > screen.getHeight())
				y -= getAbsoluteHeight()-screen.getHeight();
		} else {
			if (x < 0) x = 0;
			else if (x+getWidth() > screen.getWidth())
				x = screen.getWidth()-getWidth();
			if (y < 0) y = 0;
			else if (getAbsoluteHeight() > screen.getHeight())
				y = screen.getHeight()-getHeight();
		}
		this.moveTo(x, y);
		this.show();
	}
	
	public void hideMenu() {
		hide();
	}
	
	public void hideAllSubmenus(boolean upChain) {
		for (MenuItem mi : menuItems) {
			if (mi.getSubMenu() != null)
				mi.getSubMenu().hideMenu();
		}
		if (caller != null && upChain) {
			caller.hideAllSubmenus(upChain);
		}
	}
	
	public void handleMenuItemClick(MenuItem menuItem, int menuItemIndex, String value) {
		onMenuItemClicked(menuItemIndex, value);
		hide();
	}
	
	public abstract void onMenuItemClicked(int index, String value);
	
	@Override
	public void onMouseMove(MouseMotionEvent evt) {
		float x = evt.getX()-getX();
		float y = scrollableArea.getAbsoluteHeight()-menuPadding-evt.getY();
		
		if (currentMenuItemIndex != (int)Math.floor(y/menuItemHeight)) {
			currentMenuItemIndex = (int)Math.floor(y/menuItemHeight);
			
			System.out.println(currentMenuItemIndex);
			if (currentMenuItemIndex > -1 && currentMenuItemIndex < menuItems.size()) {
				setHighlight(currentMenuItemIndex);
				this.hideAllSubmenus(false);
				Menu subMenu = menuItems.get(currentMenuItemIndex).getSubMenu();
				if (subMenu != null) {
					subMenu.showMenu(this, getAbsoluteWidth()-this.menuOverhang, scrollableArea.getAbsoluteHeight()-(menuItemHeight+(currentMenuItemIndex*menuItemHeight))-(subMenu.getHeight()-menuItemHeight));
				}
			}
		}
	//	System.out.println(miIndex);
		
	}
	
	public void setHighlight(int index) {
		if (highlight.getParent() == null)
			this.attachChild(highlight);
		highlight.setY(scrollableArea.getHeight()+scrollableArea.getY()-(index*menuItemHeight)-menuItemHeight-(menuPadding));
	}
	
	@Override
	public void onMouseLeftPressed(MouseButtonEvent evt) {
		
	}
	@Override
	public void onMouseLeftReleased(MouseButtonEvent evt) {
		if (currentMenuItemIndex > -1 && currentMenuItemIndex < menuItems.size())
			this.handleMenuItemClick(menuItems.get(currentMenuItemIndex), currentMenuItemIndex, menuItems.get(currentMenuItemIndex).getValue());
	}
	@Override
	public void onMouseRightPressed(MouseButtonEvent evt) {
		
	}
	@Override
	public void onMouseRightReleased(MouseButtonEvent evt) {
		
	}
	@Override
	public void controlHideHook() {
		highlight.removeFromParent();
		currentMenuItemIndex = -1;
	}
}
