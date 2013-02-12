/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.scrolling;

import com.jme3.font.BitmapFont;
import com.jme3.font.LineWrapMode;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import java.util.Set;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;
import tonegod.gui.listeners.MouseFocusListener;
import tonegod.gui.listeners.MouseWheelListener;

/**
 *
 * @author t0neg0d
 */
public class ScrollArea extends Element implements MouseWheelListener {
	protected Element scrollableArea;
	private boolean isTextOnly = true;
	private boolean isScrollable = true;
	private VScrollBar vScrollBar;
	private float scrollSize;
	private boolean scrollHidden = false;
	
	/**
	 * Creates a new instance of the ScrollArea control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param isTextOnly Boolean defining if the scroll area will contain other Elements or use formatted text
	 */
	public ScrollArea(Screen screen, String UID, Vector2f position, boolean isTextOnly) {
		this(screen, UID, position,
			screen.getStyle("ScrollArea").getVector2f("defaultSize"),
			screen.getStyle("ScrollArea").getVector4f("resizeBorders"),
			screen.getStyle("ScrollArea").getString("defaultImg"),
			isTextOnly
		);
	}
	
	/**
	 * Creates a new instance of the ScrollArea control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 * @param isTextOnly Boolean defining if the scroll area will contain other Elements or use formatted text
	 */
	public ScrollArea(Screen screen, String UID, Vector2f position, Vector2f dimensions, boolean isTextOnly) {
		this(screen, UID, position, dimensions,
			screen.getStyle("ScrollArea").getVector4f("resizeBorders"),
			screen.getStyle("ScrollArea").getString("defaultImg"),
			isTextOnly
		);
	}
	
	/**
	 * Creates a new instance of the ScrollArea control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 * @param resizeBorders A Vector4f containg the border information used when resizing the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg The default image to use for the Slider's track
	 * @param isTextOnly Boolean defining if the scroll area will contain other Elements or use formatted text
	 */
	public ScrollArea (Screen screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg, boolean isTextOnly) {
		super (screen, UID, position, dimensions, resizeBorders, defaultImg);
		
		this.isTextOnly = isTextOnly;
		
		// Load default font info
		setFontColor(screen.getStyle("ScrollArea").getColorRGBA("fontColor"));
		setFontSize(screen.getStyle("ScrollArea").getFloat("fontSize"));
		setTextAlign(BitmapFont.Align.valueOf(screen.getStyle("ScrollArea").getString("textAlign")));
		setTextVAlign(BitmapFont.VAlign.valueOf(screen.getStyle("ScrollArea").getString("textVAlign")));
		setTextWrap(LineWrapMode.valueOf(screen.getStyle("ScrollArea").getString("textWrap")));
		setTextClipPadding(screen.getStyle("ScrollArea").getFloat("textPadding"));
		
		scrollSize = screen.getStyle("ScrollArea#VScrollBar").getFloat("defaultControlSize");
		
		setWidth(getWidth()-scrollSize);
		
		if (!isTextOnly) {
			createScrollableArea();
		} else {
			setTextPadding(screen.getStyle("ScrollArea").getFloat("textPadding"));
			setText("");
		}
		
		vScrollBar = new VScrollBar(screen, UID + ":vScroll",
			new Vector2f(getWidth(), 0),
			new Vector2f(scrollSize, getHeight())
		);
		
		addChild(vScrollBar);
		
		setVScrollBar(vScrollBar);
	}
	
	private void createScrollableArea() {
		scrollableArea = new Element(screen, getUID() + ":scrollable", new Vector2f(0, 0), new Vector2f(getWidth(), 25), new Vector4f(14,14,14,14), null);
	//	scrollableArea.setTextPadding(0);
		
		scrollableArea.setIsResizable(false);
		scrollableArea.setIsMovable(false);
		scrollableArea.setScaleEW(true);
		scrollableArea.setScaleNS(false);
		scrollableArea.setDockN(true);
		scrollableArea.setDockW(true);
		
		// Load default font info
		scrollableArea.setFontColor(screen.getStyle("ScrollArea").getColorRGBA("fontColor"));
		scrollableArea.setFontSize(screen.getStyle("ScrollArea").getFloat("fontSize"));
		scrollableArea.setTextAlign(BitmapFont.Align.valueOf(screen.getStyle("ScrollArea").getString("textAlign")));
		scrollableArea.setTextVAlign(BitmapFont.VAlign.valueOf(screen.getStyle("ScrollArea").getString("textVAlign")));
		scrollableArea.setTextWrap(LineWrapMode.valueOf(screen.getStyle("ScrollArea").getString("textWrap")));
		scrollableArea.setTextPadding(screen.getStyle("ScrollArea").getFloat("textPadding"));
		scrollableArea.setTextClipPadding(screen.getStyle("ScrollArea").getFloat("textPadding"));
		
		scrollableArea.setClippingLayer(this);
		scrollableArea.setTextClipPadding(screen.getStyle("ScrollArea").getFloat("scrollAreaPadding"));
		
		this.addChild(scrollableArea);
	}
	
	public Element getScrollableArea() {
		return this.scrollableArea;
	}
	
	public boolean getIsTextOnly() {
		return isTextOnly;
	}
	
	private void setVScrollBar(VScrollBar vScrollBar) {
		this.vScrollBar = vScrollBar;
		vScrollBar.setScrollableArea(this);
	}
	
	public VScrollBar getVScrollBar() {
		return this.vScrollBar;
	}
	
	public void addScrollableChild(Element child) {
		scrollableArea.addChild(child);
	}
	
	public void setPadding(float padding) {
		if (isTextOnly) {
			setTextPadding(padding);
			setTextClipPadding(padding);
		} else {
			scrollableArea.setTextPadding(padding);
			scrollableArea.setTextClipPadding(padding);
		}
	}
	
	public float getPadding() {
		if (isTextOnly) {
			return getTextPadding();
		} else {
			return scrollableArea.getTextPadding();
		}
	}
	
	public float getScrollableHeight() {
		if (isTextOnly) {
			return textElement.getHeight()+(getTextPadding()*2)+(getClipPadding()*2);
		} else {
			return scrollableArea.getHeight()+(scrollableArea.getTextPadding()*2)+(scrollableArea.getClipPadding()*2);
		}
	}
	
	@Override
	public void controlResizeHook() {
		if (vScrollBar != null) {
			vScrollBar.setThumbScale();
		}
		adjustWidthForScroll();
		if (scrollableArea != null)
			if (scrollableArea.getY() > 0 && getScrollableHeight() > getHeight())
				scrollToBottom();
	}
	
	@Override
	public void setControlClippingLayer(Element clippingLayer) {
	//	setClippingLayer(clippingLayer);
		Set<String> keys = elementChildren.keySet();
		for (String key : keys) {
			elementChildren.get(key).setControlClippingLayer(clippingLayer);
		}
	}
	
	public final void adjustWidthForScroll() {
		if (vScrollBar.getParent() == null && !scrollHidden) {
			setWidth(getWidth()+vScrollBar.getWidth());
			scrollHidden = true;
		} else if (vScrollBar.getParent() != null && scrollHidden) {
			setWidth(getWidth()-vScrollBar.getWidth());
			getVScrollBar().setX(getWidth());
			scrollHidden = false;
		}
	}
	
	public void scrollThumbYTo(float y) {
		adjustWidthForScroll();
		vScrollBar.scrollYTo(y);
	}
	
	public void scrollYTo(float y) {
		adjustWidthForScroll();
		if (scrollableArea == null) {
			textElement.setLocalTranslation(textElement.getLocalTranslation().setY(y));
		} else {
			scrollableArea.setY(0);
			scrollableArea.setY(y);
		}
		controlScrollHook();
	}
	
	public void controlScrollHook() {  }
	
	public void scrollYBy(float yInc) {
		adjustWidthForScroll();
		if (scrollableArea == null) {
			float nextY = textElement.getLocalTranslation().getY() + yInc;
			textElement.setLocalTranslation(textElement.getLocalTranslation().setY(yInc));
		} else {
			scrollableArea.setY(scrollableArea.getY()+yInc);
		}
	}
	
	public void scrollToBottom() {
		adjustWidthForScroll();
		vScrollBar.scrollToBottom();
	}
	
	public void scrollToTop() {
		adjustWidthForScroll();
		vScrollBar.scrollToTop();
	}

	@Override
	public void onMouseWheelPressed(MouseButtonEvent evt) {
		evt.setConsumed();}
	@Override
	public void onMouseWheelReleased(MouseButtonEvent evt) {
		evt.setConsumed();}
	@Override
	public void onMouseWheelUp(MouseMotionEvent evt) {
		if (vScrollBar != null) {
			vScrollBar.scrollByYInc(-vScrollBar.getTrackInc());
		}
		evt.setConsumed();
	}
	@Override
	public void onMouseWheelDown(MouseMotionEvent evt) {
		if (vScrollBar != null) {
			vScrollBar.scrollByYInc(vScrollBar.getTrackInc());
		}
		evt.setConsumed();
	}
}
