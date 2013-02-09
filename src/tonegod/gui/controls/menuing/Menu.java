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
import tonegod.gui.effects.Effect;
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
	private float initWidth;
	private float menuItemHeight;
	private float menuPadding = 4;
	private Menu caller;
	private Element callerElement;
	float menuOverhang;
	boolean isScrollable;
	BitmapText sizeEval;
	ColorRGBA highlightColor;
	private int currentMenuItemIndex = -1;
	private int currentHighlightIndex = 0;
	private Vector2f preferredSize = Vector2f.ZERO;
	
	/**
	 * Creates a new instance of the Menu control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param isScrollable Boolean defining if the menu is a scrollable list
	 */
	public Menu(Screen screen, String UID, Vector2f position, boolean isScrollable) {
		this(screen, UID, position,
			screen.getStyle("Menu").getVector2f("defaultSize"),
			screen.getStyle("Menu").getVector4f("resizeBorders"),
			screen.getStyle("Menu").getString("defaultImg"),
			isScrollable
		);
	}
	
	/**
	 * Creates a new instance of the Menu control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 * @param isScrollable Boolean defining if the menu is a scrollable list
	 */
	public Menu(Screen screen, String UID, Vector2f position, Vector2f dimensions, boolean isScrollable) {
		this(screen, UID, position, dimensions,
			screen.getStyle("Menu").getVector4f("resizeBorders"),
			screen.getStyle("Menu").getString("defaultImg"),
			isScrollable
		);
	}
	
	/**
	 * Creates a new instance of the Menu control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 * @param resizeBorders A Vector4f containg the border information used when resizing the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg The default image to use for the Slider's track
	 * @param isScrollable Boolean defining if the menu is a scrollable list
	 */
	public Menu(Screen screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg, boolean isScrollable) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg, false);
		
	//	setFontSize(20);
		
		menuOverhang = screen.getStyle("Menu").getFloat("menuOverhang");
		menuPadding = screen.getStyle("Menu").getFloat("menuPadding");
		highlightColor = screen.getStyle("Menu").getColorRGBA("highlightColor");
		
		// Load default font info
		setFontColor(screen.getStyle("Menu").getColorRGBA("fontColor"));
		setFontSize(screen.getStyle("Menu").getFloat("fontSize"));
		setTextAlign(BitmapFont.Align.valueOf(screen.getStyle("Menu").getString("textAlign")));
		setTextVAlign(BitmapFont.VAlign.valueOf(screen.getStyle("Menu").getString("textVAlign")));
		setTextWrap(LineWrapMode.valueOf(screen.getStyle("Menu").getString("textWrap")));
		setTextPadding(screen.getStyle("Menu").getFloat("textPadding"));
		setTextClipPadding(screen.getStyle("Menu").getFloat("textPadding"));
		scrollableArea.setFontColor(screen.getStyle("Menu").getColorRGBA("fontColor"));
		scrollableArea.setFontSize(screen.getStyle("Menu").getFloat("fontSize"));
		scrollableArea.setTextAlign(BitmapFont.Align.valueOf(screen.getStyle("Menu").getString("textAlign")));
		scrollableArea.setTextVAlign(BitmapFont.VAlign.valueOf(screen.getStyle("Menu").getString("textVAlign")));
		scrollableArea.setTextWrap(LineWrapMode.valueOf(screen.getStyle("Menu").getString("textWrap")));
		scrollableArea.setTextPadding(screen.getStyle("Menu").getFloat("textPadding"));
		scrollableArea.setTextClipPadding(menuPadding+screen.getStyle("Menu").getFloat("textPadding"));
		
		sizeEval = new BitmapText(font);
		sizeEval.setSize(fontSize);
		sizeEval.setLineWrapMode(LineWrapMode.NoWrap);
		sizeEval.setText(" ");
		menuItemHeight = sizeEval.getLineHeight();
		
		scrollableArea.setText(" ");
		scrollableArea.setIgnoreMouse(true);
		scrollableArea.setHeight(menuItemHeight);
		
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
	
	public void addMenuItem(String caption, Object value, Menu subMenu) {
		this.getVScrollBar().hide();
		MenuItem menuItem = new MenuItem(
			this,
			caption,
			value,
			subMenu
		);
		this.menuItems.add(menuItem);
	//	miIndex++;
		
		pack();
	}
	
	public void insertMenuItem(int index, String caption, Object value, Menu subMenu) {
		if (!menuItems.isEmpty()) {
			if (index >= 0 && index < menuItems.size()) {
				this.getVScrollBar().hide();
				MenuItem menuItem = new MenuItem(
					this,
					caption,
					value,
					subMenu
				);
				this.menuItems.add(index, menuItem);
		//		miIndex++;

				pack();
			}
		}
	}
	
	public void removeMenuItem(int index) {
		this.getVScrollBar().hide();
		if (!menuItems.isEmpty()) {
			if (index >= 0 && index < menuItems.size()) {
				menuItems.remove(index);
		//		miIndex--;
				pack();
			}
		}
	}
	
	public void removeMenuItem(Object value) {
		if (!menuItems.isEmpty()) {
			int index = -1;
			int count = 0;
			for (MenuItem mi : menuItems) {
				if (mi.getValue() == value) {
					index = count;
					break;
				}
				count++;
			}
			removeMenuItem(index);
		}
	}
	
	public void removeMenuItem(String caption) {
		if (!menuItems.isEmpty()) {
			int index = -1;
			int count = 0;
			for (MenuItem mi : menuItems) {
				if (mi.getCaption().equals(caption)) {
					index = count;
					break;
				}
				count++;
			}
			removeMenuItem(index);
		}
	}
	
	public void removeFirstMenuItem() {
		removeMenuItem(0);
	}
	
	public void removeLastMenuItem() {
		if (!menuItems.isEmpty()) {
			removeMenuItem(menuItems.size()-1);
		}
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
	
	public void setPreferredSize(Vector2f preferredSize) {
		this.preferredSize = preferredSize;
	}
	
	public void pack() {
		String finalString = "";
		
		scrollableArea.removeAllChildren();
		scrollableArea.setHeight(menuItemHeight);
		
		int index = 0;
		float currentHeight = 0;
		float width = menuItemHeight*3;
		boolean init = true;
		
		for (MenuItem mi : menuItems) {
			sizeEval.setBox(null);
			sizeEval.setText("      " + mi.getCaption() + "  ");
			float tWidth = (menuItemHeight*2)+sizeEval.getLineWidth();
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
		
		if (preferredSize == Vector2f.ZERO) {
			this.resize(getX()+width+(menuPadding*2), getY()+currentHeight+(menuPadding*2), Borders.SE);
		} else {
			float nextWidth = (preferredSize.x > width+(menuPadding*2)) ? preferredSize.x : width+(menuPadding*2);
			float nextHeight = (currentHeight > preferredSize.y+(menuPadding*2)) ? preferredSize.y : currentHeight+(menuPadding*2);
			this.resize(getX()+nextWidth, getY()+nextHeight, Borders.SE);
		}
		
		scrollableArea.setX(menuPadding);
		scrollableArea.setWidth( ((getWidth() > width) ? getWidth() : width)-(menuPadding*2) );
		scrollableArea.setY(menuPadding);
		scrollableArea.setHeight(currentHeight);
		
		
		if (highlight.getParent() == null) {
			highlight.setX(menuPadding);
			highlight.setWidth( ((getWidth() > width) ? getWidth() : width)-(menuPadding*2) );
			highlight.setHeight(menuItemHeight);
			highlight.getElementMaterial().setColor("Color", highlightColor);
			highlight.setClippingLayer(this);
			highlight.setClipPadding(menuPadding);
			scrollableArea.addChild(highlight);
		} else {
			highlight.setWidth( ((getWidth() > width) ? getWidth() : width)-(menuPadding*2) );
		}
		
		if(getScrollableHeight() > getHeight()-(menuPadding*2)) {
			scrollToBottom();
		//	if (getVScrollBar() != null)
		//		getVScrollBar().setX(getWidth());
			setIsResizable(true);
			setResizeN(false);
			setResizeW(false);
			setResizeE(true);
			setResizeS(true);
		}
	}
	
	private void addSubmenuArrow(int index) {
		Element elArrow = new Element(screen, getUID() + ":Arrow:" + index,
				new Vector2f(getWidth()-menuItemHeight-(menuPadding*2), -(menuItems.size()*menuItemHeight)+(menuItemHeight+(index*menuItemHeight))),//(menuItems.size()*menuItemHeight)-menuItemHeight+menuPadding-(index*menuItemHeight)),
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
			
			if (!getIsVisible())
				elArrow.hide();
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
			else if (y+getHeight() > screen.getHeight())
				y -= getAbsoluteHeight()-screen.getHeight();
		} else {
			if (x < 0) x = 0;
			else if (x+getWidth() > screen.getWidth())
				x = screen.getWidth()-getWidth();
			if (y < 0) y = 0;
			else if (y+getHeight() > screen.getHeight())
				y = screen.getHeight()-getHeight();
		}
		this.moveTo(x, y);
		Effect effect = getEffect(Effect.EffectEvent.Show);
		if (effect != null)
			if (effect.getEffectType() == Effect.EffectType.FadeIn)
				this.propagateEffect(effect);
			else
				screen.getEffectManager().applyEffect(effect);
		else
			this.show();
	}
	
	/**
	 * Hides the menu
	 */
	public void hideMenu() {
		Effect effect = getEffect(Effect.EffectEvent.Hide);
		if (effect != null)
			if (effect.getEffectType() == Effect.EffectType.FadeOut)
				this.propagateEffect(effect);
			else
				screen.getEffectManager().applyEffect(effect);
		else
			this.hide();
	}
	
	protected void hideAllSubmenus(boolean upChain) {
		for (MenuItem mi : menuItems) {
			if (mi.getSubMenu() != null)
				mi.getSubMenu().hideMenu();
		}
		if (caller != null && upChain) {
			caller.hideAllSubmenus(upChain);
		}
	}
	
	private void handleMenuItemClick(MenuItem menuItem, int menuItemIndex, Object value) {
		onMenuItemClicked(menuItemIndex, value);
		hide();
	}
	
	/**
	 * Abstract method for handling menu item selection
	 * 
	 * @param index Index of MenuItem clicked
	 * @param value String value of MenuItem clicked
	 */
	public abstract void onMenuItemClicked(int index, Object value);
	
	@Override
	public void onMouseMove(MouseMotionEvent evt) {
		float x = evt.getX()-getX();
		float y = scrollableArea.getAbsoluteHeight()-menuPadding-evt.getY();
		
		if (currentMenuItemIndex != (int)Math.floor(y/menuItemHeight)) {
			currentMenuItemIndex = (int)Math.floor(y/menuItemHeight);
			
			if (currentMenuItemIndex > -1 && currentMenuItemIndex < menuItems.size()) {
				setHighlight(currentMenuItemIndex);
				this.hideAllSubmenus(false);
				Menu subMenu = menuItems.get(currentMenuItemIndex).getSubMenu();
				if (subMenu != null) {
					subMenu.showMenu(this, getAbsoluteWidth()-this.menuOverhang, scrollableArea.getAbsoluteHeight()-(menuItemHeight+(currentMenuItemIndex*menuItemHeight))-(subMenu.getHeight()-menuItemHeight));
				}
			}
		}
		
	}
	
	/**
	 * Sets the highlight Element's current position to the Y position of the supplied
	 * MenuItem index
	 * 
	 * @param index int
	 */
	public void setHighlight(int index) {
		currentHighlightIndex = index;
		if (highlight.getParent() == null)
			this.attachChild(highlight);
		highlight.setY(scrollableArea.getHeight()+scrollableArea.getY()-(index*menuItemHeight)-menuItemHeight);
	}
	
	public Element getHighlight() {
		return highlight;
	}
	
	@Override
	public void onMouseLeftPressed(MouseButtonEvent evt) {
		
		evt.setConsumed();
	}
	@Override
	public void onMouseLeftReleased(MouseButtonEvent evt) {
		if (currentMenuItemIndex > -1 && currentMenuItemIndex < menuItems.size())
			this.handleMenuItemClick(menuItems.get(currentMenuItemIndex), currentMenuItemIndex, menuItems.get(currentMenuItemIndex).getValue());
		evt.setConsumed();
	}
	@Override
	public void onMouseRightPressed(MouseButtonEvent evt) {
		
		evt.setConsumed();
	}
	@Override
	public void onMouseRightReleased(MouseButtonEvent evt) {
		
		evt.setConsumed();
	}
	@Override
	public void controlHideHook() {
		highlight.removeFromParent();
		currentMenuItemIndex = -1;
	}
	@Override
	public void controlScrollHook() {
		highlight.setY(scrollableArea.getHeight()+scrollableArea.getY()-(currentHighlightIndex*menuItemHeight)-menuItemHeight);
		if (getCallerElement() != null)
			screen.setTabFocusElement(getCallerElement());
	}
}
