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
import tonegod.gui.controls.lists.SlideTray;
import tonegod.gui.core.Element;
import tonegod.gui.core.ElementManager;
import tonegod.gui.core.utils.BitmapTextUtil;
import tonegod.gui.core.utils.UIDUtil;

/**
 *
 * @author t0neg0d
 */
public class TabControl extends Element {
	protected List<Button> tabs = new ArrayList();
	protected Map<Integer,TabPanel> tabPanels = new HashMap();
	protected int tabButtonIndex = 0;
	protected float tabWidth, tabHeight, tabXInc;
	protected RadioButtonGroup tabButtonGroup;
	protected Vector4f tabResizeBorders;
	protected SlideTray tabSlider;
	protected boolean isFixedTabWidth = false;
	protected float fixedTabWidth = 0;
	
	/**
	 * Creates a new instance of the TabControl control
	 * 
	 * @param screen The screen control the Element is to be added to
	 */
	public TabControl(ElementManager screen) {
		this(screen, UIDUtil.getUID(), Vector2f.ZERO,
			screen.getStyle("Window").getVector2f("defaultSize"),
			screen.getStyle("Window").getVector4f("resizeBorders"),
			null
		);
	}
	
	/**
	 * Creates a new instance of the TabControl control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param position A Vector2f containing the x/y position of the Element
	 */
	public TabControl(ElementManager screen, Vector2f position) {
		this(screen, UIDUtil.getUID(), position,
			screen.getStyle("Window").getVector2f("defaultSize"),
			screen.getStyle("Window").getVector4f("resizeBorders"),
			null
		);
	}
	
	/**
	 * Creates a new instance of the TabControl control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 */
	public TabControl(ElementManager screen, Vector2f position, Vector2f dimensions) {
		this(screen, UIDUtil.getUID(), position, dimensions,
			screen.getStyle("Window").getVector4f("resizeBorders"),
			null
		);
	}
	
	/**
	 * Creates a new instance of the TabControl control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 * @param resizeBorders A Vector4f containg the border information used when resizing the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg The default image to use for the TabControl background
	 */
	public TabControl(ElementManager screen, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		this(screen, UIDUtil.getUID(), position, dimensions, resizeBorders, defaultImg);
	}
	
	/**
	 * Creates a new instance of the TabControl control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 */
	public TabControl(ElementManager screen, String UID, Vector2f position) {
		this(screen, UID, position,
			screen.getStyle("Window").getVector2f("defaultSize"),
			screen.getStyle("Window").getVector4f("resizeBorders"),
			null
		);
	}
	
	/**
	 * Creates a new instance of the TabControl control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 */
	public TabControl(ElementManager screen, String UID, Vector2f position, Vector2f dimensions) {
		this(screen, UID, position, dimensions,
			screen.getStyle("Window").getVector4f("resizeBorders"),
			null
		);
	}
	
	/**
	 * Creates a new instance of the TabControl control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 * @param resizeBorders A Vector4f containg the border information used when resizing the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg The default image to use for the TabControl background
	 */
	public TabControl(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg);
		
		this.setIsMovable(false);
		this.setIsResizable(false);
		this.setDocking(Docking.NW);
		this.setScaleNS(true);
		this.setScaleEW(true);
		this.setClipPadding(screen.getStyle("Window").getFloat("clipPadding"));
		
		tabWidth = screen.getStyle("Tab").getVector2f("defaultSize").x;
		tabHeight = screen.getStyle("Tab").getVector2f("defaultSize").y;
		tabResizeBorders = screen.getStyle("Tab").getVector4f("resizeBorders");
		tabXInc = screen.getStyle("Tab").getVector2f("defaultSize").x-tabResizeBorders.y;
		
		tabButtonGroup = new RadioButtonGroup(screen, getUID() + ":TabButtonGroup") {
			@Override
			public void onSelect(int index, Button value) {
				Set<Integer> keys = tabPanels.keySet();
				TabPanel selectedPanel = tabPanels.get(index);
				Button selectedTab = tabs.get(index);
				for (Integer key : keys) {
					if (key != index) {
						tabPanels.get(key).hide();
					}
				}
				selectedPanel.show();
				selectedTab.removeFromParent();
				selectedTab.getElementParent().attachChild(selectedTab);
				selectedPanel.hide();
				selectedPanel.show();
			}
		};
		
		tabSlider = new SlideTray(screen, getUID() + ":tabSlider",
			new Vector2f(20,0),
			new Vector2f(getWidth()-40,40),
			SlideTray.Orientation.HORIZONTAL
		);
		addChild(tabSlider);
	}
	
	/**
	 * Sets the resize borders for use with ElementQuadGrid per tab
	 * @param tabResizeBorders 
	 */
	public void setTabResizeBorders(Vector4f tabResizeBorders) {
		this.tabResizeBorders.set(tabResizeBorders);
	}
	
	
	/**
	 * Sets the width to always use for Tabs
	 * @param fixedTabWidth float Forced width of all Tabs
	 */
	public void setFixedTabWidth(float fixedTabWidth) {
		if (fixedTabWidth > 0) {
			isFixedTabWidth = true;
			this.fixedTabWidth = fixedTabWidth;
		} else {
			isFixedTabWidth = false;
			this.fixedTabWidth = 0;
		}
	}
	
	/**
	 * Sets the default tab height
	 * @param tabHeight 
	 */
	public void setTabHeight(float tabHeight) {
		this.tabHeight = tabHeight;
	}
	
	/**
	 * Enables the SliderToEffect of the SlideTray containing the tabs
	 * @param useSlideEffect boolean
	 */
	public void setUseSlideEffect(boolean useSlideEffect) {
		tabSlider.setUseSlideEffect(useSlideEffect);
	}
	
	/**
	 * Adds a new Tab and TabPanel to the TabControl
	 * @param title String The Title to set for the Tab
	 */
	public void addTab(String title) {
		ButtonAdapter tab = new ButtonAdapter(
			screen,
			getUID() + ":Tab" + tabButtonIndex,
			new Vector2f(tabXInc*tabButtonIndex,0),
			screen.getStyle("Tab").getVector2f("defaultSize"),
			screen.getStyle("Tab").getVector4f("resizeBorders"),
			screen.getStyle("Tab").getString("defaultImg")
		);
		if (isFixedTabWidth) {
			tab.setWidth(fixedTabWidth);
		} else {
			float width = BitmapTextUtil.getTextWidth(tab, title);
			tab.setWidth(width+(8*2));
		}
		tab.clearAltImages();
		tab.setButtonHoverInfo(
			screen.getStyle("Tab").getString("hoverImg"),
			screen.getStyle("Tab").getColorRGBA("hoverColor")
		);
		tab.setButtonPressedInfo(
			screen.getStyle("Tab").getString("pressedImg"),
			screen.getStyle("Tab").getColorRGBA("pressedColor")
		);
		tab.setText(title);
		tab.setDocking(Docking.NW);
		tab.setScaleEW(false);
		tab.setScaleNS(false);
		tab.setElementUserData(tabButtonIndex);
		tab.setControlClippingLayer(tab);
		tabButtonGroup.addButton(tab);
		tabs.add(tab);
		
		TabPanel panel = new TabPanel(
			screen,
			getUID() + ":TabPanel" + tabButtonIndex,
			new Vector2f(0,tabHeight-screen.getStyle("Tab").getVector4f("resizeBorders").w),
			getDimensions().subtract(new Vector2f(0,tabHeight-screen.getStyle("Tab").getVector4f("resizeBorders").w))
		);
		addChild(panel);
		tabPanels.put(tabButtonIndex,panel);
		
		tabSlider.addTrayElement(tab);
		
		if (tabButtonIndex != 0)
			panel.hide();
		else
			tab.setIsToggled(true);
		
		tabButtonIndex++;
	}
	
	/**
	 * Adds the provided Element to the panel associated with the tab index
	 * @param index int Tab index
	 * @param element Element
	 */
	public void addTabChild(int index, Element element) {
		if (index > -1 && index < tabs.size()) {
			tabPanels.get(index).addChild(element);
			element.setClippingLayer(tabPanels.get(index));
		}
	}
	
	public class TabPanel extends Panel {
		
		/**
	 * Creates a new instance of the Panel control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 */
		public TabPanel(ElementManager screen, String UID, Vector2f position) {
			this(screen, UID, position,
				screen.getStyle("Window").getVector2f("defaultSize"),
				screen.getStyle("Window").getVector4f("resizeBorders"),
				screen.getStyle("Tab").getString("panelImg")
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
		public TabPanel(ElementManager screen, String UID, Vector2f position, Vector2f dimensions) {
			this(screen, UID, position, dimensions,
				screen.getStyle("Window").getVector4f("resizeBorders"),
				screen.getStyle("Tab").getString("panelImg")
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
		public TabPanel(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
			super(screen, UID, position, dimensions, resizeBorders, defaultImg);
			
			this.setIsMovable(false);
			this.setIsResizable(false);
			this.setScaleNS(true);
			this.setScaleEW(true);
			this.setClipPadding(screen.getStyle("Window").getFloat("clipPadding"));
		}
	}
	
	private void slideLeft() {
		
	}
	
	private void slideRight() {
		
	}
	
	private void slideToTab(int index) {
		
	}
}
