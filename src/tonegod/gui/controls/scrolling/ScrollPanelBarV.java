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
public class ScrollPanelBarV extends Element {
	ButtonAdapter btnUp, btnDown, track, thumb;
	int btnInc = 1, trackInc = 10;
	MouseButtonEvent trackEvent = null;
	ScrollPanel scrollPanel = null;
	
	public ScrollPanelBarV(ScrollPanel scrollPanel) {
		super(
			scrollPanel.getScreen(),
			UIDUtil.getUID(),
			new Vector2f(
				scrollPanel.getDimensions().x-25,
				0
			),
			new Vector2f(25, scrollPanel.getDimensions().y-25),
			Vector4f.ZERO,
			null
		);
		
		this.scrollPanel = scrollPanel;
		this.setScaleNS(true);
		this.setScaleEW(false);
		this.setDocking(Docking.NE);
		
		initControl();
	}
	
	private void initControl() {
		track = new ButtonAdapter(screen, getUID() + ":track",
			new Vector2f(0, getWidth()),
			new Vector2f(getWidth(), getHeight()-(getWidth()*2)),
			screen.getStyle("ScrollArea#VScrollBar").getVector4f("trackResizeBorders"),
			screen.getStyle("ScrollArea#VScrollBar").getString("trackImg")
		) {
			@Override
			public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
				trackEvent = evt;
				if (trackEvent.getY()-getAbsoluteY() < thumb.getY()) {
					if (thumb.getY()-trackInc > 0) {
						thumb.setY(thumb.getY()-trackInc);
					} else {
						thumb.setY(0);
					}
					scrollScrollableArea();
				} else if (trackEvent.getY()-getAbsoluteY() > thumb.getY()+thumb.getHeight()) {
					if (thumb.getY()+trackInc < track.getHeight()-thumb.getHeight()) {
						thumb.setY(thumb.getY()+trackInc);
					} else {
						thumb.setY(track.getHeight()-thumb.getHeight());
					}
					scrollScrollableArea();
				}
			}
			@Override
			public void onButtonStillPressedInterval() {
				if (trackEvent.getY()-getAbsoluteY() < thumb.getY()) {
					if (thumb.getY()-trackInc > 0) {
						thumb.setY(thumb.getY()-trackInc);
					} else {
						thumb.setY(0);
					}
					scrollScrollableArea();
				} else if (trackEvent.getY()-getAbsoluteY() > thumb.getY()+thumb.getHeight()) {
					if (thumb.getY()+trackInc < thumb.getHeight()-thumb.getHeight()) {
						thumb.setY(thumb.getY()+trackInc);
					} else {
						thumb.setY(track.getHeight()-thumb.getHeight());
					}
					scrollScrollableArea();
				}
			}
		};
		track.setScaleEW(false);
		track.setScaleNS(true);
		track.setDocking(Docking.SW);
		track.setInterval(100);
		this.addChild(track);
		
		track.removeEffect(Effect.EffectEvent.Hover);
		track.removeEffect(Effect.EffectEvent.Press);
		track.removeEffect(Effect.EffectEvent.Release);
		
		thumb = new ButtonAdapter(screen, getUID() + ":thumb",
			new Vector2f(0, 0),
			new Vector2f(getWidth(), getWidth()),
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
		thumb.setlockToParentBounds(true);
		thumb.setScaleEW(false);
		thumb.setScaleNS(true);
		thumb.setDocking(Docking.NW);
		track.addChild(thumb);
		
		btnUp = new ButtonAdapter(screen, getUID() + ":btnUp",
			new Vector2f(0, 0),
			new Vector2f(getWidth(), getWidth()),
			screen.getStyle("ScrollArea#VScrollBar").getVector4f("btnUpResizeBorders"),
			screen.getStyle("ScrollArea#VScrollBar").getString("btnUpImg")
		) {
			@Override
			public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
				if (thumb.getY() < (track.getHeight()-thumb.getHeight())) {
					thumb.setY(thumb.getY()+btnInc);
				}
				scrollScrollableArea();
			}
			@Override
			public void onButtonStillPressedInterval() {
				if (thumb.getY() < (track.getHeight()-thumb.getHeight())) {
					thumb.setY(thumb.getY()+btnInc);
				}
				scrollScrollableArea();
			}
		};
		btnUp.setButtonHoverInfo(screen.getStyle("ScrollArea#VScrollBar").getString("btnUpHoverImg"), ColorRGBA.White);
		btnUp.setButtonPressedInfo(screen.getStyle("ScrollArea#VScrollBar").getString("btnUpPressedImg"), ColorRGBA.Gray);
		btnUp.setScaleEW(false);
		btnUp.setScaleNS(false);
		btnUp.setDocking(Docking.NW);
		btnUp.setInterval(100);
		if (screen.getStyle("ScrollArea#VScrollBar").getBoolean("useBtnUpArrowIcon")) {
			btnUp.setButtonIcon(18, 18, screen.getStyle("Common").getString("arrowUp"));
		}
		this.addChild(btnUp);
		
		btnDown = new ButtonAdapter(screen, getUID() + ":btnDown",
			new Vector2f(0, getHeight()-getWidth()),
			new Vector2f(getWidth(), getWidth()),
			screen.getStyle("ScrollArea#VScrollBar").getVector4f("btnDownResizeBorders"),
			screen.getStyle("ScrollArea#VScrollBar").getString("btnDownImg")
		) {
			@Override
			public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
				if (thumb.getY() > 0) {
					thumb.setY(thumb.getY()-btnInc);
				}
				scrollScrollableArea();
			}
			@Override
			public void onButtonStillPressedInterval() {
				if (thumb.getY() > 0) {
					thumb.setY(thumb.getY()-btnInc);
				}
				scrollScrollableArea();
			}
		};
		btnDown.setButtonHoverInfo(screen.getStyle("ScrollArea#VScrollBar").getString("btnDownHoverImg"), ColorRGBA.White);
		btnDown.setButtonPressedInfo(screen.getStyle("ScrollArea#VScrollBar").getString("btnDownPressedImg"), ColorRGBA.Gray);
		btnDown.setScaleEW(false);
		btnDown.setScaleNS(false);
		btnDown.setDocking(Docking.SW);
		btnDown.setInterval(100);
		if (screen.getStyle("ScrollArea#VScrollBar").getBoolean("useBtnDownArrowIcon")) {
			btnDown.setButtonIcon(18, 18, screen.getStyle("Common").getString("arrowDown"));
		}
		this.addChild(btnDown);
	}
	
	private void scrollScrollableArea() {
		scrollPanel.setScrollAreaPositionToVThumb();
	}
}
