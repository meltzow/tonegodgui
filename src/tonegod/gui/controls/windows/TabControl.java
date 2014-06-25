/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.windows;

import com.jme3.font.BitmapFont.Align;
import com.jme3.font.BitmapFont.VAlign;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import tonegod.gui.controls.buttons.Button;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.buttons.RadioButtonGroup;
import tonegod.gui.controls.lists.SlideTray;
import tonegod.gui.controls.lists.SlideTray.ZOrderSort;
import tonegod.gui.controls.scrolling.ScrollPanel;
import tonegod.gui.controls.text.LabelElement;
import tonegod.gui.core.Element;
import tonegod.gui.core.ElementManager;
import tonegod.gui.core.layouts.Layout;
import tonegod.gui.core.utils.BitmapTextUtil;
import tonegod.gui.core.utils.UIDUtil;
import tonegod.gui.framework.core.AnimText;

/**
 *
 * @author t0neg0d
 */
public class TabControl extends Element {
	private Orientation orientation = Orientation.HORIZONTAL;
	protected List<Button> tabs = new ArrayList();
	protected Map<Integer,TabPanel> tabPanels = new HashMap();
	protected int tabButtonIndex = 0;
	protected float tabWidth, tabHeight, tabInc;
	protected RadioButtonGroup tabButtonGroup;
	protected Vector4f tabResizeBorders;
	protected SlideTray tabSlider;
	protected boolean isFixedTabSize = false;
	protected float fixedTabSize = 0;
	protected float tabTraySize, tabTrayOverlap = 3;
	protected float labelPadding = 24;
	
	/**
	 * Creates a new instance of the TabControl control
	 * 
	 * @param screen The screen control the Element is to be added to
	 */
	public TabControl(ElementManager screen) {
		this(screen, UIDUtil.getUID(), Vector2f.ZERO,
			screen.getStyle("Window").getVector2f("defaultSize"),
			screen.getStyle("Window").getVector4f("resizeBorders"),
			null,
			Orientation.HORIZONTAL
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
			null,
			Orientation.HORIZONTAL
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
			null,
			Orientation.HORIZONTAL
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
		this(screen, UIDUtil.getUID(), position, dimensions, resizeBorders, defaultImg,
			Orientation.HORIZONTAL);
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
			null,
			Orientation.HORIZONTAL
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
			null,
			Orientation.HORIZONTAL
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
	public TabControl(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg, Orientation orientation) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg);
		
		this.orientation = orientation;
		
		this.setIsMovable(false);
		this.setIsResizable(false);
		this.setDocking(Docking.NW);
		this.setScaleNS(true);
		this.setScaleEW(true);
		this.setClipPadding(screen.getStyle("Window").getFloat("clipPadding"));
		
		configTabControl();
	}
	
	private void configTabControl() {
		if (orientation == Orientation.HORIZONTAL) {
			tabWidth = screen.getStyle("Tab").getVector2f("defaultSize").x;
			tabHeight = screen.getStyle("Tab").getVector2f("defaultSize").y;
			tabResizeBorders = screen.getStyle("Tab").getVector4f("resizeBorders");
			tabInc = screen.getStyle("Tab").getVector2f("defaultSize").x-screen.getStyle("Tab").getFloat("tabOverhang");
		} else {
			tabWidth = screen.getStyle("Tab").getVector2f("defaultSize").y;
			tabHeight = screen.getStyle("Tab").getVector2f("defaultSize").x;
			tabResizeBorders = screen.getStyle("Tab").getVector4f("resizeBorders");
			tabInc = screen.getStyle("Tab").getVector2f("defaultSize").y-screen.getStyle("Tab").getFloat("tabOverhang");
		}
		
		tabButtonGroup = new RadioButtonGroup(screen, getUID() + ":TabButtonGroup") {
			@Override
			public void onSelect(int index, Button value) {
				Set<Integer> keys = tabPanels.keySet();
				TabPanel selectedPanel = tabPanels.get(index);
				Button selectedTab = tabs.get(index);
				tabSlider.resort(selectedTab);
				if (tabTrayOverlap != 0) {
					for (Button b : tabs) {
						if (b != selectedTab) {
							if (orientation == Orientation.HORIZONTAL) {
								b.setHeight(tabHeight-tabTrayOverlap);
								b.setY(tabSlider.getHeight()-b.getHeight());
							} else {
								b.setWidth(tabWidth-tabTrayOverlap);
							}
						} else {
							if (orientation == Orientation.HORIZONTAL) {
								b.setHeight(tabHeight);
								b.setY(tabSlider.getHeight()-b.getHeight());
							} else {
								b.setWidth(tabWidth);
							}
						}
					}
				}
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
		
		float btnInset = 20;
		if (orientation == Orientation.HORIZONTAL) {
			tabSlider = new SlideTray(screen, getUID() + ":tabSlider",
				new Vector2f(btnInset,0),
				new Vector2f(getWidth()-(btnInset*2),(btnInset*2)),
				Orientation.HORIZONTAL
			) {
				@Override
				public void controlResizeHook() {
					this.updateClippingLayers();
				}
			};
			tabSlider.setTrayPadding(-(tabResizeBorders.z/5*2));
			tabSlider.setZOrderSorting(ZOrderSort.LAST_TO_FIRST);
			tabSlider.setButtonSize(tabHeight);
			tabSlider.alignButtonsV(VAlign.Top);
		} else {
			tabSlider = new SlideTray(screen, getUID() + ":tabSlider",
				new Vector2f(0,btnInset),
				new Vector2f((btnInset*2),getHeight()-(btnInset*2)),
				Orientation.VERTICAL
			) {
				@Override
				public void controlResizeHook() {
					this.updateClippingLayers();
				}
			};
			tabSlider.setTrayPadding(-(tabResizeBorders.z/5*2));
			tabSlider.setZOrderSorting(ZOrderSort.FIRST_TO_LAST);
			tabSlider.setButtonSize(tabWidth);
			tabSlider.alignButtonsH(Align.Left);
		}
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
	public void setFixedTabSize(float fixedTabWidth) {
		if (fixedTabWidth > 0) {
			isFixedTabSize = true;
			this.fixedTabSize = fixedTabWidth;
		} else {
			isFixedTabSize = false;
			this.fixedTabSize = 0;
		}
	}
	
	/**
	 * Sets the default tab height
	 * @param tabHeight 
	 */
	public void setTabSize(float size) {
		if (orientation == Orientation.HORIZONTAL)
			this.tabHeight = size;
		else
			this.tabWidth = size;
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
		Vector2f pos = new Vector2f();
		Vector2f dim = new Vector2f();
		
		LabelElement label = new LabelElement(screen);
		
		if (orientation == Orientation.VERTICAL) {
			label.setText(title);
			label.setSizeToText(true);
			label.setUseTextClipping(false);
			AnimText txt = label.getAnimText();

			txt.setPosition(-(txt.getLineWidth()+(label.getHeight()/2)),(txt.getLineHeight())+(label.getHeight()/2));
			txt.setOrigin(txt.getLineWidth()/2,txt.getLineHeight()/2);
			txt.setRotation(90);
			label.setDimensions(label.getHeight(),label.getWidth());
			label.updateClippingLayers();
			txt.update(0);
		}
		
		if (orientation == Orientation.HORIZONTAL) {
			pos.set(tabInc*tabButtonIndex,0);
			dim.set(screen.getStyle("Tab").getVector2f("defaultSize"));
		} else {
			pos.set(0,tabInc*tabButtonIndex);
			dim.set(screen.getStyle("Tab").getVector2f("defaultSize").y,screen.getStyle("Tab").getVector2f("defaultSize").x);
		}
		
		ButtonAdapter tab = new ButtonAdapter(
			screen,
			getUID() + ":Tab" + tabButtonIndex,
			pos,
			dim,
			(orientation == Orientation.HORIZONTAL) ? screen.getStyle("Tab").getVector4f("resizeBorders") : screen.getStyle("Tab").getVector4f("resizeBordersV"),
			(orientation == Orientation.HORIZONTAL) ? screen.getStyle("Tab").getString("defaultImg") : screen.getStyle("Tab").getString("defaultImgV")
		);
		if (isFixedTabSize) {
			tab.setWidth(fixedTabSize);
		} else {
			float width = BitmapTextUtil.getTextWidth(tab, title);
			tab.setWidth(width+(labelPadding*2)+(tabResizeBorders.x+tabResizeBorders.z));
		}
		tab.clearAltImages();
		
		String hImg = (orientation == Orientation.HORIZONTAL) ? screen.getStyle("Tab").getString("hoverImg") : screen.getStyle("Tab").getString("hoverImgV");
		String pImg = (orientation == Orientation.HORIZONTAL) ? screen.getStyle("Tab").getString("pressedImg") : screen.getStyle("Tab").getString("pressedImgV");
		
		tab.setButtonHoverInfo(
			hImg,
			screen.getStyle("Tab").getColorRGBA("hoverColor")
		);
		tab.setButtonPressedInfo(
			pImg,
			screen.getStyle("Tab").getColorRGBA("pressedColor")
		);
		
		tab.setDocking(Docking.NW);
		tab.setScaleEW(false);
		tab.setScaleNS(false);
		tab.setElementUserData(tabButtonIndex);
		
		if (orientation == Orientation.VERTICAL) {
			tab.addChild(label);
			label.centerToParent();
		} else {
			tab.setText(title);
		}
		
		tabButtonGroup.addButton(tab);
		tabs.add(tab);
		
		if (orientation == Orientation.HORIZONTAL) {
			pos.set(
				0,
				tabHeight-screen.getStyle("Tab").getVector4f("resizeBorders").w
			);
			dim.set(
				getDimensions().subtract(
					new Vector2f(
						0,
						tabHeight-screen.getStyle("Tab").getVector4f("resizeBorders").w
					)
				)
			);
		} else {
			pos.set(
				tabWidth-screen.getStyle("Tab").getVector4f("resizeBorders").x,
				0
			);
			dim.set(
				getDimensions().subtract(
					new Vector2f(
						tabWidth-screen.getStyle("Tab").getVector4f("resizeBorders").x,
						0
					)
				)
			);
		}
		
		TabPanel panel = new TabPanel(
			screen,
			getUID() + ":TabPanel" + tabButtonIndex,
			pos,
			dim
		);
		addChild(panel);
		tabPanels.put(tabButtonIndex,panel);
		
		tabSlider.addTrayElement(tab);
		
		if (tabButtonIndex != 0)
			panel.hide();
		else
			tab.setIsToggled(true);
		
		tab.addClippingLayer(tab);
		label.addClippingLayer(label);
		
		tabButtonIndex++;
	}
	
	public void setSelectedTab(int index) {
	//	if (tabButtonGroup.)
		tabButtonGroup.onSelect(index, tabs.get(index));
		tabSlider.resort(tabs.get(index));
	}
	
	/**
	 * Adds the provided Element to the panel associated with the tab index
	 * @param index int Tab index
	 * @param element Element
	 */
	public void addTabChild(int index, Element element) {
		if (index > -1 && index < tabs.size()) {
			tabPanels.get(index).addChild(element);
		//	element.setClippingLayer(tabPanels.get(index));
			element.addClippingLayer(tabPanels.get(index));
		}
	}
	
	public void setTabPanelLayout(int index, Layout layout) {
		tabPanels.get(index).getScrollableArea().setLayout(layout);
	}
	
	public void layoutTabPanelChildren(int index) {
		tabPanels.get(index).getScrollableArea().layoutChildren();
	}
	
	public class TabPanel extends ScrollPanel {
		
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
		//	this.setClipPadding(screen.getStyle("Window").getFloat("clipPadding"));
		}
	}
	
	private void slideLeft() {
		
	}
	
	private void slideRight() {
		
	}
	
	private void slideToTab(int index) {
		
	}
}
