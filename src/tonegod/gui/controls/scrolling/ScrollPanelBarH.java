/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.scrolling;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.core.Element;
import tonegod.gui.core.utils.UIDUtil;
import tonegod.gui.effects.Effect;

/**
 *
 * @author t0neg0d
 */
public class ScrollPanelBarH extends Element {
	private ButtonAdapter btnLeft, btnRight, track, thumb;
	private MouseButtonEvent trackEvent = null;
	private ScrollPanel scrollPanel = null;
	private boolean scalingEnabled = true;
	
	public ScrollPanelBarH(ScrollPanel scrollPanel) {
		super(
			scrollPanel.getScreen(),
			UIDUtil.getUID(),
			new Vector2f(0,scrollPanel.getDimensions().y-25),
			new Vector2f(scrollPanel.getDimensions().x-25, 25),
			Vector4f.ZERO,
			null
		);
		
		this.scrollPanel = scrollPanel;
		this.setScaleNS(false);
		this.setScaleEW(true);
		this.setDocking(Element.Docking.SE);
		this.setAsContainerOnly();
		
		initControl();
	}
	
	private void initControl() {
		track = new ButtonAdapter(screen, getUID() + ":hTrack",
			new Vector2f(getHeight(),0),
			new Vector2f(getWidth()-(getHeight()*2), getHeight()),
			screen.getStyle("ScrollArea#VScrollBar").getVector4f("trackResizeBorders"),
			screen.getStyle("ScrollArea#VScrollBar").getString("trackImg")
		) {
			@Override
			public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
				trackEvent = evt;
				if (trackEvent.getY()-getAbsoluteY() < thumb.getY()) {
					if (thumb.getX()-scrollPanel.getTrackInc() > 0) {
						thumb.setX(thumb.getX()-scrollPanel.getTrackInc());
					} else {
						thumb.setX(0);
					}
					scrollScrollableArea();
				} else if (trackEvent.getX()-getAbsoluteX() > thumb.getX()+thumb.getWidth()) {
					if (thumb.getX()+scrollPanel.getTrackInc() < track.getWidth()-thumb.getWidth()) {
						thumb.setX(thumb.getX()+scrollPanel.getTrackInc());
					} else {
						thumb.setX(track.getWidth()-thumb.getWidth());
					}
					scrollScrollableArea();
				}
			}
			@Override
			public void onButtonStillPressedInterval() {
				if (trackEvent.getX()-getAbsoluteX() < thumb.getX()) {
					if (thumb.getX()-scrollPanel.getTrackInc() > 0) {
						thumb.setX(thumb.getX()-scrollPanel.getTrackInc());
					} else {
						thumb.setX(0);
					}
					scrollScrollableArea();
				} else if (trackEvent.getX()-getAbsoluteX() > thumb.getX()+thumb.getWidth()) {
					if (thumb.getX()+scrollPanel.getTrackInc() < thumb.getWidth()-thumb.getWidth()) {
						thumb.setX(thumb.getX()+scrollPanel.getTrackInc());
					} else {
						thumb.setX(track.getWidth()-thumb.getWidth());
					}
					scrollScrollableArea();
				}
			}
		};
		track.setScaleEW(true);
		track.setScaleNS(false);
		track.setDocking(Element.Docking.SW);
		track.setInterval(100);
		this.addChild(track);
		
		track.removeEffect(Effect.EffectEvent.Hover);
		track.removeEffect(Effect.EffectEvent.Press);
		track.removeEffect(Effect.EffectEvent.Release);
		
		thumb = new ButtonAdapter(screen, getUID() + ":hThumb",
			new Vector2f(0, 0),
			new Vector2f(getHeight(), getHeight()),
			screen.getStyle("ScrollArea#VScrollBar").getVector4f("thumbResizeBorders"),
			screen.getStyle("ScrollArea#VScrollBar").getString("thumbImg")
		) {
			@Override
			public void controlMoveHook() {
				scrollScrollableArea();
			}
		};
		thumb.setButtonHoverInfo(screen.getStyle("ScrollArea#VScrollBar").getString("thumbHoverImg"), ColorRGBA.White);
		thumb.setButtonPressedInfo(screen.getStyle("ScrollArea#VScrollBar").getString("thumbPressedImg"), ColorRGBA.Gray);
		thumb.setIsMovable(true);
		thumb.setLockToParentBounds(true);
		thumb.setScaleEW(true);
		thumb.setScaleNS(false);
		thumb.setDocking(Element.Docking.SW);
		track.addChild(thumb);
		
		btnLeft = new ButtonAdapter(screen, getUID() + ":hBtnLeft",
			new Vector2f(0, 0),
			new Vector2f(getHeight(), getHeight()),
			screen.getStyle("ScrollArea#VScrollBar").getVector4f("btnUpResizeBorders"),
			screen.getStyle("ScrollArea#VScrollBar").getString("btnUpImg")
		) {
			@Override
			public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
				if (thumb.getX() > 0) {
					thumb.setX(thumb.getX()-scrollPanel.getButtonInc());
				}
				scrollScrollableArea();
			}
			@Override
			public void onButtonStillPressedInterval() {
				if (thumb.getX() > 0) {
					thumb.setX(thumb.getX()-scrollPanel.getButtonInc());
				}
				scrollScrollableArea();
			}
		};
		btnLeft.setButtonHoverInfo(screen.getStyle("ScrollArea#VScrollBar").getString("btnUpHoverImg"), ColorRGBA.White);
		btnLeft.setButtonPressedInfo(screen.getStyle("ScrollArea#VScrollBar").getString("btnUpPressedImg"), ColorRGBA.Gray);
		btnLeft.setScaleEW(false);
		btnLeft.setScaleNS(false);
		btnLeft.setDocking(Element.Docking.SW);
		btnLeft.setInterval(100);
		if (screen.getStyle("ScrollArea#VScrollBar").getBoolean("useBtnUpArrowIcon")) {
			btnLeft.setButtonIcon(18, 18, screen.getStyle("Common").getString("arrowLeft"));
		}
		this.addChild(btnLeft);
		
		btnRight = new ButtonAdapter(screen, getUID() + ":hBtnRight",
			new Vector2f(getWidth()-getHeight(),0),
			new Vector2f(getHeight(), getHeight()),
			screen.getStyle("ScrollArea#VScrollBar").getVector4f("btnDownResizeBorders"),
			screen.getStyle("ScrollArea#VScrollBar").getString("btnDownImg")
		) {
			@Override
			public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
				if (thumb.getX() < (track.getWidth()-thumb.getWidth())) {
					thumb.setX(thumb.getX()+scrollPanel.getButtonInc());
				}
				scrollScrollableArea();
			}
			@Override
			public void onButtonStillPressedInterval() {
				if (thumb.getX() < (track.getWidth()-thumb.getWidth())) {
					thumb.setX(thumb.getX()+scrollPanel.getButtonInc());
				}
				scrollScrollableArea();
			}
		};
		btnRight.setButtonHoverInfo(screen.getStyle("ScrollArea#VScrollBar").getString("btnDownHoverImg"), ColorRGBA.White);
		btnRight.setButtonPressedInfo(screen.getStyle("ScrollArea#VScrollBar").getString("btnDownPressedImg"), ColorRGBA.Gray);
		btnRight.setScaleEW(false);
		btnRight.setScaleNS(false);
		btnRight.setDocking(Element.Docking.SE);
		btnRight.setInterval(100);
		if (screen.getStyle("ScrollArea#VScrollBar").getBoolean("useBtnDownArrowIcon")) {
			btnRight.setButtonIcon(18, 18, screen.getStyle("Common").getString("arrowRight"));
		}
		this.addChild(btnRight);
	}
	
	private void scrollScrollableArea() {
		scrollPanel.setScrollAreaPositionToHThumb();
	}
	
	public ButtonAdapter getButtonScrollLeft() { return this.btnLeft; }
	
	public ButtonAdapter getButtonScrollRight() { return this.btnRight; }
	
	public ButtonAdapter getScrollTrack() { return this.track; }
	
	public ButtonAdapter getScrollThumb() { return this.thumb; }
	
	@Override
	public void controlResizeHook() {
		if (!this.scalingEnabled) {
			scrollPanel.scrollToLeft();
		}
	}
	
	public void setScalingEnabled(boolean scalingEnabled) {
	//	this.scalingEnabled = scalingEnabled;
	//	this.setScaleEW(scalingEnabled);
	//	this.track.setScaleEW(scalingEnabled);
		
		this.scalingEnabled = scalingEnabled;
		this.setScaleEW(scalingEnabled);
		this.track.setScaleEW(scalingEnabled);
		this.setDocking(Docking.SW);
		this.btnRight.setDocking(Docking.SW);
		this.thumb.setDocking(Docking.SW);
		this.setDocking(Docking.SW);
		this.track.setScaleNS(scalingEnabled);
	}
}
