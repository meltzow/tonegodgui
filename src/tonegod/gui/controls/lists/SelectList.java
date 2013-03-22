/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.lists;

import com.jme3.font.BitmapFont;
import com.jme3.font.LineWrapMode;
import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import java.util.ArrayList;
import java.util.List;
import tonegod.gui.controls.menuing.Menu;
import tonegod.gui.controls.menuing.MenuItem;
import tonegod.gui.controls.scrolling.ScrollArea;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;
import tonegod.gui.core.utils.BitmapTextUtil;
import tonegod.gui.listeners.KeyboardListener;
import tonegod.gui.listeners.MouseButtonListener;
import tonegod.gui.listeners.MouseMovementListener;
import tonegod.gui.listeners.MouseWheelListener;
import tonegod.gui.listeners.TabFocusListener;

/**
 *
 * @author t0neg0d
 */
public abstract class SelectList extends ScrollArea implements MouseMovementListener, MouseWheelListener, MouseButtonListener, TabFocusListener, KeyboardListener {
	List<ListItem> listItems = new ArrayList();
	List<Integer> selectedIndexes = new ArrayList();
	List<Element> highlights = new ArrayList();
	
	boolean isMultiselect = false;
	float initWidth;
	float listItemHeight;
	float listPadding = 1;
	ColorRGBA highlightColor;
	int currentListItemIndex = -1;
	boolean shift = false, ctrl = false;
	
	/**
	 * Creates a new instance of the Menu control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param isScrollable Boolean defining if the menu is a scrollable list
	 */
	public SelectList(Screen screen, String UID, Vector2f position) {
		this(screen, UID, position,
			screen.getStyle("Menu").getVector2f("defaultSize"),
			screen.getStyle("Menu").getVector4f("resizeBorders"),
			screen.getStyle("Menu").getString("defaultImg")
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
	public SelectList(Screen screen, String UID, Vector2f position, Vector2f dimensions) {
		this(screen, UID, position, dimensions,
			screen.getStyle("Menu").getVector4f("resizeBorders"),
			screen.getStyle("Menu").getString("defaultImg")
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
	public SelectList(Screen screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg, false);
		
		listPadding = screen.getStyle("Menu").getFloat("menuPadding");
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
		scrollableArea.setTextClipPadding(listPadding+screen.getStyle("Menu").getFloat("textPadding"));
		scrollableArea.setScaleEW(false);
		
		listItemHeight = BitmapTextUtil.getTextLineHeight(this, "Xg");
		
		scrollableArea.setText(" ");
		scrollableArea.setIgnoreMouse(true);
		scrollableArea.setHeight(listItemHeight);
		
		initWidth = listItemHeight*3;
	}
	
	public void setIsMultiselect(boolean isMultiselect) {
		this.isMultiselect = isMultiselect;
	}
	
	public boolean getIsMultiselect() {
		return this.isMultiselect;
	}
	/**
	 * Adds a ListItem to the Menu
	 * @param caption The display caption of the MenuItem
	 * @param value The value to associate with the MenuItem
	 */
	public void addListItem(String caption, Object value) {
		this.getVScrollBar().hide();
		ListItem listItem = new ListItem(
			this,
			caption,
			value
		);
		
		this.listItems.add(listItem);
		pack();
	}
	
	/**
	 * Inserts a new ListItem at the provided index
	 * @param index The index to insert into
	 * @param caption The display caption of the MenuItem
	 * @param value The value to associate with the MenuItem
	 */
	public void insertListItem(int index, String caption, Object value) {
		if (!listItems.isEmpty()) {
			if (index >= 0 && index < listItems.size()) {
				this.getVScrollBar().hide();
				ListItem listItem = new ListItem(
					this,
					caption,
					value
				);
				this.listItems.add(index, listItem);
				pack();
			}
		}
	}
	
	/**
	 * Remove the ListItem at the provided index
	 * @param index int
	 */
	public void removeListItem(int index) {
		this.getVScrollBar().hide();
		if (!listItems.isEmpty()) {
			if (index >= 0 && index < listItems.size()) {
				listItems.remove(index);
				pack();
			}
		}
	}
	
	/**
	 * Remove the first ListItem that contains the provided value
	 * @param value Object
	 */
	public void removeListItem(Object value) {
		if (!listItems.isEmpty()) {
			int index = -1;
			int count = 0;
			for (ListItem mi : listItems) {
				if (mi.getValue() == value) {
					index = count;
					break;
				}
				count++;
			}
			removeListItem(index);
		}
	}
	
	/**
	 * Remove the first ListItem that contains the provided caption
	 * @param value Object
	 */
	public void removeListItem(String caption) {
		if (!listItems.isEmpty()) {
			int index = -1;
			int count = 0;
			for (ListItem mi : listItems) {
				if (mi.getCaption().equals(caption)) {
					index = count;
					break;
				}
				count++;
			}
			removeListItem(index);
		}
	}
	
	/**
	 * Removes the first ListItem in the SelectList
	 */
	public void removeFirstListItem() {
		removeListItem(0);
	}
	
	/**
	 * Removes the last ListItem in the SelectList
	 */
	public void removeLastListItem() {
		if (!listItems.isEmpty()) {
			removeListItem(listItems.size()-1);
		}
	}
	
	/**
	 * Sets the current selected index for single select SelectLists
	 * @param index int
	 */
	public void setSelectedIndex(Integer index) {
		selectedIndexes = new ArrayList();
		selectedIndexes.add(index);
		displayHighlights();
		onChange();
	}
	
	/**
	 * Sets the current list of selected indexes to the specified indexes
	 * @param indexes 
	 */
	public void setSelectedIndexes(Integer... indexes) {
		for (int i = 0; i < indexes.length; i++) {
			if (!selectedIndexes.contains(indexes[i]))
				selectedIndexes.add(indexes[i]);
		}
		displayHighlights();
		onChange();
	}
	
	/**
	 * Adds the specified index to the list of selected indexes
	 * @param index int
	 */
	public void addSelectedIndex(Integer index) {
		if (!selectedIndexes.contains(index))
			selectedIndexes.add(index);
		displayHighlights();
		onChange();
	}
	
	/**
	 * Removes the specified index from the list of selected indexes
	 * @param index int
	 */
	public void removeSelectedIndex(Integer index) {
		selectedIndexes.remove(index);
		displayHighlights();
		onChange();
	}
	
	/**
	 * Returns the first (or only) index in the list of selected indexes
	 * @return int
	 */
	public int getSelectedIndex() {
		if (selectedIndexes.isEmpty())
			return -1;
		else
			return selectedIndexes.get(0);
	}
	
	/**
	 * Returns the entire list of selected indexes
	 * @return List<Integer>
	 */
	public List<Integer> getSelectedIndexes() {
		return this.selectedIndexes;
	}
	
	/**
	 * Returns the ListItem at the specified index
	 * @param index int
	 * @return ListItem
	 */
	public ListItem getListItem(int index) {
		if (!listItems.isEmpty()) {
			if (index >= 0 && index < listItems.size())
				return listItems.get(index);
			else return null;
		} else
			return null;
	}
	
	/**
	 * Returns a List containing all ListItems corresponding to the list of selectedIndexes
	 * @return List<ListItem>
	 */
	public List<ListItem> getSelectedListItems() {
		List<ListItem> ret = new ArrayList();
		for (Integer i : selectedIndexes) {
			ret.add(getListItem(i));
		}
		return ret;
	}
	
	/**
	 * Forces the SelectList to rebuild all ListItems.  This does not need to be called, however it will not effect anything negatively if it is.
	 */
	public void pack() {
		String finalString = "";
		
		listItemHeight = BitmapTextUtil.getTextLineHeight(this, "Xg");
		
		scrollableArea.removeAllChildren();
		scrollableArea.setHeight(listItemHeight);
		
		int index = 0;
		float currentHeight = 0;
		float width = listItemHeight*3;
		boolean init = true;
		
		String leftSpacer = "  ";
		String rightSpacer = "  ";
		
		for (ListItem mi : listItems) {
			float tWidth = (listItemHeight*2)+BitmapTextUtil.getTextWidth(this, leftSpacer + mi.getCaption() + rightSpacer);
			width = (tWidth > width) ? tWidth : width;
			if (init) {
				finalString = leftSpacer + mi.getCaption() + rightSpacer;
				init = false;
			} else {
				finalString += "\n" + leftSpacer + mi.getCaption() + rightSpacer;
			}
			if (selectedIndexes.contains(index)) {
				Element highlight = createHighlight(index);
				highlight.setX(0);
			//	highlight.setY(listPadding);
				highlight.setWidth( getWidth()-(listPadding*2) );
				highlight.setHeight(listItemHeight);
				highlight.getElementMaterial().setColor("Color", highlightColor);
				highlight.setClippingLayer(this);
				highlight.setClipPadding(listPadding);
				highlight.setY(scrollableArea.getHeight()-((listItems.size()-index)*listItemHeight)+listPadding);
				scrollableArea.addChild(highlight);
			}
			currentHeight += listItemHeight;
			index++;
		}
		scrollableArea.setText(finalString);
		
		scrollableArea.setX(listPadding);
		scrollableArea.setWidth( ((getWidth() > width) ? getWidth() : width)-(listPadding*2) );
		scrollableArea.setY(listPadding);
		scrollableArea.setHeight(currentHeight);
		
		
		if(getScrollableHeight() > getHeight()-(listPadding*2)) {
			scrollToTop();
			setWidth(getWidth());
			getVScrollBar().setX(getWidth());
		}
		scrollToTop();
	}
	
	private void displayHighlights() {
		scrollableArea.removeAllChildren();
		int index = 0;
		float currentHeight = 0;
		for (ListItem mi : listItems) {
			if (selectedIndexes.contains(index)) {
				Element highlight = createHighlight(index);
				highlight.setX(0);
			//	highlight.setY(listPadding);
				highlight.setWidth( getWidth()-(listPadding*2) );
				highlight.setHeight(listItemHeight);
				highlight.getElementMaterial().setColor("Color", highlightColor);
				highlight.setClippingLayer(this);
				highlight.setClipPadding(listPadding);
				highlight.setY(scrollableArea.getHeight()-((listItems.size()-index)*listItemHeight));
				scrollableArea.addChild(highlight);
			}
			currentHeight += listItemHeight;
			index++;
		}
	}
	
	private Element createHighlight(int index) {
		Element highlight = new Element(
			screen,
			getUID() + ":Highlight" + index,
			new Vector2f(0,0),
			new Vector2f(listPadding,listPadding),
			new Vector4f(1,1,1,1),
			null
		);
		highlight.setScaleEW(true);
		highlight.setScaleNS(false);
		highlight.setDockN(true);
		highlight.setDockS(true);
		highlight.setIgnoreMouse(true);
		
		return highlight;
	}
	@Override
	public void onMouseMove(MouseMotionEvent evt) {
		float x = evt.getX()-getX();
		float y = scrollableArea.getAbsoluteHeight()-listPadding-evt.getY();
		
		if (currentListItemIndex != (int)Math.floor(y/listItemHeight)) {
			currentListItemIndex = (int)Math.floor(y/listItemHeight);
		}
	}

	@Override
	public void onMouseLeftPressed(MouseButtonEvent evt) {
		evt.setConsumed();
	}

	@Override
	public void onMouseLeftReleased(MouseButtonEvent evt) {
		if (isMultiselect) {
			if (shift || ctrl) {
				if (!selectedIndexes.contains(currentListItemIndex)) {
					addSelectedIndex(currentListItemIndex);
				} else {
					removeSelectedIndex(currentListItemIndex);
				}
			} else {
				setSelectedIndex(currentListItemIndex);
			}
		} else {
			if (currentListItemIndex >= 0 && currentListItemIndex < listItems.size())
				setSelectedIndex(currentListItemIndex);
			else
				selectedIndexes = new ArrayList();
		}
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
	public void onKeyPress(KeyInputEvent evt) {
		if (evt.getKeyCode() == KeyInput.KEY_LCONTROL || evt.getKeyCode() == KeyInput.KEY_RCONTROL) {
			ctrl = true;
		} else if (evt.getKeyCode() == KeyInput.KEY_LSHIFT || evt.getKeyCode() == KeyInput.KEY_RSHIFT) {
			shift = true;
		}
	}

	@Override
	public void onKeyRelease(KeyInputEvent evt) {
		if (evt.getKeyCode() == KeyInput.KEY_LCONTROL || evt.getKeyCode() == KeyInput.KEY_RCONTROL) {
			ctrl = false;
		} else if (evt.getKeyCode() == KeyInput.KEY_LSHIFT || evt.getKeyCode() == KeyInput.KEY_RSHIFT) {
			shift = false;
		}
	}

	@Override
	public void setTabFocus() {
		screen.setKeyboardElemeent(this);
	}

	@Override
	public void resetTabFocus() {
		screen.setKeyboardElemeent(null);
	}
	
	public abstract void onChange();
	
	public class ListItem {
		SelectList owner;
		String caption;
		Object value;
		
		public ListItem(SelectList owner, String caption, Object value) {
			this.owner = owner;
			this.caption = caption;
			this.value = value;
		}
		
		public String getCaption() {
			return this.caption;
		}
		
		public Object getValue() {
			return this.value;
		}
	}
}
