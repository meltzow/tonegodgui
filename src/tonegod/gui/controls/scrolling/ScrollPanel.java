/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.scrolling;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import tonegod.gui.core.Element;
import tonegod.gui.core.ElementManager;
import tonegod.gui.framework.animation.Interpolation;
import tonegod.gui.framework.core.util.GameTimer;
import tonegod.gui.listeners.FlingListener;
import tonegod.gui.listeners.MouseWheelListener;
import tonegod.gui.listeners.TouchListener;

/**
 *
 * @author t0neg0d
 */
public class ScrollPanel extends Element {
	protected ScrollPanelBounds innerBounds;
	protected Element scrollableArea;
	protected ScrollPanelBarV vScrollBar;
	protected ScrollPanelBarH hScrollBar;
	private float scrollSize = 25;
	private int buttonInc = 1;
	private int trackInc = 10;
	private boolean vScrollEnabled = true;
	private boolean hScrollEnabled = true;
	private boolean scrollChild = false;
	private boolean flingEnabled = true;
	private GameTimer flingTimer;
	private float touchStartY = 0;
	private float touchEndY = 0;
	private float touchOffsetY = 0;
	private boolean flingDir = true;
	private float flingSpeed = 1;
	
	public ScrollPanel(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		super(screen, UID, position, dimensions, Vector4f.ZERO, null);
		setAsContainerOnly();
		
		innerBounds = new ScrollPanelBounds(screen, UID + "innerBounds", Vector2f.ZERO, dimensions, resizeBorders, defaultImg);	
		innerBounds.setScaleEW(true);
		innerBounds.setScaleNS(true);
		innerBounds.setDocking(Docking.SW);
		
		scrollableArea = new Element(screen, UID + "scrollableArea", Vector2f.ZERO, dimensions, Vector4f.ZERO, null);	
		scrollableArea.setScaleEW(false);
		scrollableArea.setScaleNS(false);
		scrollableArea.setDocking(Docking.NW);
		scrollableArea.setAsContainerOnly();
		
		innerBounds.addChild(scrollableArea);
		scrollableArea.setClippingLayer(innerBounds);
		addChild(innerBounds);
		
		vScrollBar = new ScrollPanelBarV(this);
		addChild(vScrollBar, true);
		hScrollBar = new ScrollPanelBarH(this);
		addChild(hScrollBar, true);
		
		initFlingTimer();
	}
	
	private void initFlingTimer() {
		flingTimer = new GameTimer() {
			@Override
			public void timerUpdateHook(float tpf) {
				float currentY = getScrollableAreaVerticalPosition();
				float nextInc = 15*flingSpeed*(1f-this.getPercentComplete());
				
				if (flingDir) {
					float nextY = currentY+nextInc;
					if (nextY <= scrollableArea.getHeight() && nextY >= innerBounds.getHeight()) {
						scrollYTo(nextY);
						setVThumbPositionToScrollArea();
					}
				} else {
					float nextY = currentY-nextInc;
					if (nextY <= scrollableArea.getHeight() && nextY >= innerBounds.getHeight()) {
						scrollYTo(nextY);
						setVThumbPositionToScrollArea();
					}
				}
			}
			@Override
			public void onComplete(float time) {
				
			}
		};
		flingTimer.setInterpolation(Interpolation.exp5Out);
	}
	
	public void addScrollableContent(Element el) {
		scrollableArea.addChild(el);
		el.setClippingLayer(innerBounds);
		el.setClipPadding(innerBounds.getClipPadding());
		el.setDocking(Docking.SW);
		reshape();
	}
	
	public void removeScrollableContent(Element el) {
		scrollableArea.removeChild(el);
		reshape();
	}
	
	private void reshape() {
		scrollableArea.sizeToContent();
		scrollableArea.setY(innerBounds.getHeight()-scrollableArea.getHeight());
		setVThumbSize();
		setHThumbSize();
		innerBounds.setClipPadding(5);
		scrollableArea.setControlClippingLayer(innerBounds);
	}
	
	public void setScrollSize(float scrollSize) {
		this.scrollSize = scrollSize;
	}
	
	public float getScrollSize() {
		return this.scrollSize;
	}
	
	@Override
	public void controlResizeHook() {
		boolean vHide = false,
				vShow = false,
				hHide = false,
				hShow = false;
		boolean vResize = false,
				hResize = false;
		boolean vDir, hDir;
		
		if (getHeight() < scrollableArea.getHeight()) {
			if (innerBounds.getWidth() == getWidth())
				vResize = true;
			if (!vScrollBar.getIsVisible())
				vShow = true;
			vDir = false;
		} else {
			if (innerBounds.getWidth() == getWidth()-scrollSize)
				vResize = true;
			if (vScrollBar.getIsVisible())
				vHide = true;
			vDir = true;
		}
		
		if (getWidth() < scrollableArea.getWidth()) {
			if (innerBounds.getHeight() == getHeight())
				hResize = true;
			if (!hScrollBar.getIsVisible())
				hShow = true;
			hDir = false;
		} else {
			if (innerBounds.getHeight() == getHeight()-scrollSize)
				hResize = true;
			if (hScrollBar.getIsVisible())
				hHide = true;
			hDir = true;
		}
		
		if (vResize) {
			if (!vDir) {
				innerBounds.setWidth(getWidth()-scrollSize);
			} else {
				innerBounds.setWidth(getWidth());
			}
		}
		if (hResize) {
			if (!hDir) {
				innerBounds.setHeight(getHeight()-scrollSize);
				innerBounds.setY(scrollSize);
			} else {
				innerBounds.setHeight(getHeight());
				innerBounds.setY(0);
			}
		}
		if (vShow)		vScrollBar.show();
		else if (vHide)	vScrollBar.hide();
		if (hShow)		{
			hScrollBar.show();
			scrollableArea.setY(scrollableArea.getY()-scrollSize);
		}
		else if (hHide)	{
			hScrollBar.hide();
			scrollableArea.setY(scrollableArea.getY()+scrollSize);
		}
		
		setVThumbSize();
		setHThumbSize();
		if (scrollableArea.getWidth() > innerBounds.getWidth() && scrollableArea.getX() < 0) {
			scrollToRight();
		} else if (scrollableArea.getWidth() < innerBounds.getWidth()) {
			scrollToLeft();
		}
		setHThumbPositionToScrollArea();
		if (scrollableArea.getHeight() > innerBounds.getHeight() && scrollableArea.getY() > 0) {
			scrollToBottom();
		} else if (scrollableArea.getHeight() < innerBounds.getHeight()) {
			scrollToTop();
		}
		setVThumbPositionToScrollArea();
	}
	
	public void setScrollAreaPadding(float padding) {
		innerBounds.setClipPadding(padding);
	}
	
	//<editor-fold desc="Vertical Scrolling">
	public float getScrollableAreaVerticalPosition() {
		return innerBounds.getHeight()-(scrollableArea.getY()+scrollableArea.getHeight());
	}
	
	public float getScrollBoundsHeight() {
		return this.innerBounds.getHeight();
	}
	
	public float getScrollableAreaHeight() {
		return scrollableArea.getHeight();
	}
	
	/**
	 * Returns the height difference between the scrollable area's total height and the
	 * scroll panel's bounds.
	 * 
	 * Note: This returns a negative float value if the scrollable area is smaller than it's bounds.
	 * 
	 * @return 
	 */
	public float getVerticalScrollDistance() {
		float diff =  scrollableArea.getHeight()-innerBounds.getHeight();
		return diff;
	}
	
	public void scrollToTop() {
		scrollableArea.setY(-getVerticalScrollDistance());
		setVThumbPositionToScrollArea();
	}
	
	public void scrollToBottom() {
		scrollableArea.setY(0);
		setVThumbPositionToScrollArea();
	}
	
	public void scrollYTo(float y) {
		scrollableArea.setY(y);
	}
	
	public void scrollYBy(float incY) {
		if (incY < 0) {
			if (vScrollBar.getScrollThumb().getY() > 0) {
				if (vScrollBar.getScrollThumb().getY()+incY < 0)
					incY -= vScrollBar.getScrollThumb().getY()+incY;
				vScrollBar.getScrollThumb().setY(vScrollBar.getScrollThumb().getY()+incY);
			}
		} else {
			float scrollHeight = vScrollBar.getScrollTrack().getHeight()-vScrollBar.getScrollThumb().getHeight();
			if (vScrollBar.getScrollThumb().getY() < scrollHeight) {
				if (vScrollBar.getScrollThumb().getY()+incY > scrollHeight)
					incY -= vScrollBar.getScrollThumb().getY()+incY-scrollHeight;
				vScrollBar.getScrollThumb().setY(vScrollBar.getScrollThumb().getY()+incY);
			}
		}
		setScrollAreaPositionToVThumb();
	}
	
	private float getVThumbRatio() {
		float ratio = innerBounds.getHeight()/scrollableArea.getHeight();
		if (ratio > 1f)
			ratio = 1f;
		return ratio;
	}
	
	public void setVThumbSize() {
		float ratio = getVThumbRatio();
		vScrollBar.getScrollThumb().setWidth(25);
		vScrollBar.getScrollThumb().setHeight(vScrollBar.getScrollTrack().getHeight()*ratio);
	}
	
	public void setVThumbPositionToScrollArea() {
		float relY = (FastMath.abs(scrollableArea.getY())/getVerticalScrollDistance());
		vScrollBar.getScrollThumb().setY((vScrollBar.getScrollTrack().getHeight()-vScrollBar.getScrollThumb().getHeight())*relY);
	}
	
	public void setScrollAreaPositionToVThumb() {
		float relY = (vScrollBar.getScrollThumb().getY()/(vScrollBar.getScrollTrack().getHeight()-vScrollBar.getScrollThumb().getHeight()));
		scrollableArea.setY(-(getVerticalScrollDistance()*relY));
	}
	//</editor-fold>
	
	//<editor-fold desc="Horizontal Scrolling">
	public float getScrollableAreaHorizontalPosition() {
		return innerBounds.getWidth()-(scrollableArea.getX()+scrollableArea.getWidth());
	}
	
	public float getScrollBoundsWidth() {
		return this.innerBounds.getWidth();
	}
	
	public float getScrollableAreaWidth() {
		return scrollableArea.getWidth();
	}
	
	/**
	 * Returns the width difference between the scrollable area's total width and the
	 * scroll panel's bounds.
	 * 
	 * Note: This returns a negative float value if the scrollable area is smaller than it's bounds.
	 * 
	 * @return 
	 */
	public float getHorizontalScrollDistance() {
		float diff =  scrollableArea.getWidth()-innerBounds.getWidth();
		return diff;
	}
	
	public void scrollToLeft() {
		scrollableArea.setX(0);
		setHThumbPositionToScrollArea();
	}
	
	public void scrollToRight() {
		scrollableArea.setX(-getHorizontalScrollDistance());
		setHThumbPositionToScrollArea();
	}
	
	public void scrollXTo(float x) {
		scrollableArea.setX(0);
	}
	
	public void scrollXBy(float incX) {
		
	}
	
	private float getHThumbRatio() {
		float ratio = innerBounds.getWidth()/scrollableArea.getWidth();
		if (ratio > 1f)
			ratio = 1f;
		return ratio;
	}
	
	public void setHThumbSize() {
		float ratio = getHThumbRatio();
		hScrollBar.getScrollThumb().setHeight(25);
		hScrollBar.getScrollThumb().setWidth(hScrollBar.getScrollTrack().getWidth()*ratio);
	}
	
	public void setHThumbPositionToScrollArea() {
		float relX = (FastMath.abs(scrollableArea.getX())/getHorizontalScrollDistance());
		hScrollBar.getScrollThumb().setX((hScrollBar.getScrollTrack().getWidth()-hScrollBar.getScrollThumb().getWidth())*relX);
	}
	
	public void setScrollAreaPositionToHThumb() {
		float relX = (hScrollBar.getScrollThumb().getX()/(hScrollBar.getScrollTrack().getWidth()-hScrollBar.getScrollThumb().getWidth()));
		scrollableArea.setX(-(getHorizontalScrollDistance()*relX));
	}
	//</editor-fold>
	
	public void setButtonInc(int buttonInc) { this.buttonInc = buttonInc; }
	
	public int getButtonInc() { return this.buttonInc; }
	
	public void setTrackInc(int trackInc) { this.trackInc = trackInc; }
	
	public int getTrackInc() { return this.trackInc; }

	public ScrollPanelBarV getVerticalScrollBar() { return this.vScrollBar; }
	
	public ScrollPanelBarH getHorizontalScrollBar() { return this.hScrollBar; }
	
	public Element getScrollBounds() { return this.innerBounds; }
	
	public Element getScrollableArea() { return this.scrollableArea; }
	
	public void configureAsChildOfScrollPanel() {
		scrollChild = true;
		setScaleEW(false);
		innerBounds.setScaleEW(false);
		scrollableArea.setScaleEW(false);
		setScaleNS(false);
		innerBounds.setScaleNS(false);
		scrollableArea.setScaleNS(false);
		scrollableArea.setControlClippingLayer(innerBounds, getElementParent().getElementParent());
		vScrollBar.setScalingEnabled(false);
		hScrollBar.setScalingEnabled(false);
		setDocking(Element.Docking.SW);
	}
	
	public void setFlingEnabled(boolean flingEnabled) { this.flingEnabled = flingEnabled; }
	
	public boolean getFlingEnabled() { return this.flingEnabled; }
	
	public class ScrollPanelBounds extends Element implements MouseWheelListener, TouchListener, FlingListener {
		public ScrollPanelBounds(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
			super(screen, UID, position, dimensions, resizeBorders, defaultImg);
		}
		
		@Override
		public void onMouseWheelPressed(MouseButtonEvent evt) { evt.setConsumed(); }
		@Override
		public void onMouseWheelReleased(MouseButtonEvent evt) { evt.setConsumed(); }
		@Override
		public void onMouseWheelUp(MouseMotionEvent evt) {
			scrollYBy(-getTrackInc());
			evt.setConsumed();
		}
		@Override
		public void onMouseWheelDown(MouseMotionEvent evt) {
			scrollYBy(getTrackInc());
			evt.setConsumed();
		}
		//<editor-fold desc="Android Events">
		@Override
		public void onFling(TouchEvent evt) {
			if (flingEnabled && (evt.getDeltaY() > 0.2f || evt.getDeltaY() < -0.2f)) {
				if (!screen.getAnimManager().hasGameTimer(flingTimer)) {
					flingTimer.reset(false);
					flingDir  = (evt.getDeltaY() < 0) ? true : false;
					flingSpeed = FastMath.abs(evt.getDeltaY());
					screen.getAnimManager().addGameTimer(flingTimer);
				}
			}
		}

		@Override
		public void onTouchDown(TouchEvent evt) {
			if (screen.getAnimManager().hasGameTimer(flingTimer)) {
				flingTimer.endGameTimer();
				screen.getAnimManager().removeGameTimer(flingTimer);
			}
			if (flingEnabled) {
				touchStartY = getScrollableAreaVerticalPosition();
				touchOffsetY = evt.getY()-touchStartY;
			}
		}

		@Override
		public void onTouchMove(TouchEvent evt) {
			if (flingEnabled) {
				float nextY = evt.getY()-touchOffsetY;
				if (nextY <= getScrollableAreaHeight() && nextY >= innerBounds.getHeight()) {
					scrollYTo(nextY);
					setVThumbPositionToScrollArea();
					touchEndY = getScrollableAreaVerticalPosition();
				}
			}
		}

		@Override
		public void onTouchUp(TouchEvent evt) {

		}
		//</editor-fold>
	}
}
