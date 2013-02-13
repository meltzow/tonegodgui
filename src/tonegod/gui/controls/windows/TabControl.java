/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.windows;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import tonegod.gui.controls.buttons.Button;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.buttons.RadioButtonGroup;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 *
 * @author t0neg0d
 */
public class TabControl extends Panel {
	List<Button> tabs = new ArrayList();
	List<TabElement> tabElements = new ArrayList();
	int tabButtonIndex = 0;
	float tabWidth, tabHeight;
	RadioButtonGroup tabButtonGroup;
	
	/**
	 * Creates a new instance of the Panel control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 */
	public TabControl(Screen screen, String UID, Vector2f position) {
		this(screen, UID, position,
			screen.getStyle("Window").getVector2f("defaultSize"),
			screen.getStyle("Window").getVector4f("resizeBorders"),
			screen.getStyle("Window").getString("defaultImg")
		);
	}
	
	/**
	 * Creates a new instance of the Panel control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 */
	public TabControl(Screen screen, String UID, Vector2f position, Vector2f dimensions) {
		this(screen, UID, position, dimensions,
			screen.getStyle("Window").getVector4f("resizeBorders"),
			screen.getStyle("Window").getString("defaultImg")
		);
	}
	
	/**
	 * Creates a new instance of the Panel control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 * @param resizeBorders A Vector4f containg the border information used when resizing the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg The default image to use for the Slider's track
	 */
	public TabControl(Screen screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg);
		
		this.setIsMovable(false);
		this.setIsResizable(false);
		this.setScaleNS(false);
		this.setScaleEW(false);
		this.setClipPadding(screen.getStyle("Window").getFloat("clipPadding"));
		
		tabWidth = screen.getStyle("Button").getVector2f("defaultSize").x;
		tabHeight = screen.getStyle("Button").getVector2f("defaultSize").y;
		
		tabButtonGroup = new RadioButtonGroup(screen, getUID() + ":TabButtonGroup") {
			@Override
			public void onSelect(int index, Button value) {
				for (TabElement el : tabElements) {
					if (el.getIndex() == index) {
						el.getElement().getElementParent().attachChild(el.getElement());
					} else {
						el.getElement().removeFromParent();
					}
				}
			}
		};
	//	populateEffects("Window");
	}
	
	public int addTab(String title) {
		ButtonAdapter tab = new ButtonAdapter(
			screen,
			getUID() + ":Tab" + tabButtonIndex,
			new Vector2f(tabWidth*tabButtonIndex,-(tabHeight-5))//,
		//	screen.getStyle("Button").getVector2f("defaultSize"),
		//	screen.getStyle("Window").getVector4f("resizeBorders"),
		//	screen.getStyle("Window").getString("defaultImg")
		);
		tab.setText(title);
		tab.setElementUserData(tabButtonIndex);
		addChild(tab);
		tabButtonGroup.addButton(tab);
		tabs.add(tab);
		tabButtonIndex++;
		return tabButtonIndex-1;
	}
	
	public void addTabChild(int index, Element element) {
		if (index > -1 && index < tabs.size()) {
			addChild(element);
			TabElement tabElement = new TabElement(index, element);
			tabElements.add(tabElement);
			if (index != 0)
				element.removeFromParent();
		}
	}
	
	public class TabElement {
		private int index;
		private Element element;
		
		public TabElement(int index, Element element) {
			this.index = index;
			this.element = element;
		}
		
		public int getIndex() {
			return this.index;
		}
		
		public Element getElement() {
			return this.element;
		}
	}
}
