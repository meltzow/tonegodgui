/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.scrolling;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;
import tonegod.gui.effects.Effect;

/**
 *
 * @author t0neg0d
 */
public class VScrollBar extends Element {
	ButtonAdapter btnScrollTrack, btnScrollUp, btnScrollDown, btnScrollThumb;
	int btnInc = 1, trackInc = 10;
	MouseButtonEvent trackEvent = null;
	ScrollArea scrollableArea = null;
	
	public VScrollBar(Screen screen, String UID, Vector2f position, Vector2f dimensions) {
		super(screen, UID, position, dimensions, new Vector4f(0,0,0,0), null);
		
		this.setScaleNS(true);
		this.setScaleEW(false);
		this.setDockN(true);
		this.setDockE(true);
		
		btnScrollTrack = new ButtonAdapter(screen, UID + "btnScrollTrack",
			new Vector2f(0, getWidth()),
			new Vector2f(getWidth(), getHeight()-(getWidth()*2)),
			screen.getStyle("ScrollArea#VScrollBar").getVector4f("trackResizeBorders"),
			screen.getStyle("ScrollArea#VScrollBar").getString("trackImg")
		) {
			@Override
			public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
				trackEvent = evt;
				if (trackEvent.getY()-getAbsoluteY() < btnScrollThumb.getY()) {
					if (btnScrollThumb.getY()-trackInc > 0) {
						btnScrollThumb.setY(btnScrollThumb.getY()-trackInc);
					} else {
						btnScrollThumb.setY(0);
					}
					scrollScrollableArea();
				} else if (trackEvent.getY()-getAbsoluteY() > btnScrollThumb.getY()+btnScrollThumb.getHeight()) {
					if (btnScrollThumb.getY()+trackInc < btnScrollTrack.getHeight()-btnScrollThumb.getHeight()) {
						btnScrollThumb.setY(btnScrollThumb.getY()+trackInc);
					} else {
						btnScrollThumb.setY(btnScrollTrack.getHeight()-btnScrollThumb.getHeight());
					}
					scrollScrollableArea();
				}
			}
			@Override
			public void onButtonMouseRightDown(MouseButtonEvent evt, boolean toggled) {  }
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {  }
			@Override
			public void onButtonMouseRightUp(MouseButtonEvent evt, boolean toggled) {  }
			@Override
			public void onButtonStillPressedInterval() {
				if (trackEvent.getY()-getAbsoluteY() < btnScrollThumb.getY()) {
					if (btnScrollThumb.getY()-trackInc > 0) {
						btnScrollThumb.setY(btnScrollThumb.getY()-trackInc);
					} else {
						btnScrollThumb.setY(0);
					}
					scrollScrollableArea();
				} else if (trackEvent.getY()-getAbsoluteY() > btnScrollThumb.getY()+btnScrollThumb.getHeight()) {
					if (btnScrollThumb.getY()+trackInc < btnScrollTrack.getHeight()-btnScrollThumb.getHeight()) {
						btnScrollThumb.setY(btnScrollThumb.getY()+trackInc);
					} else {
						btnScrollThumb.setY(btnScrollTrack.getHeight()-btnScrollThumb.getHeight());
					}
					scrollScrollableArea();
				}
			}
			@Override
			public void onButtonFocus(MouseMotionEvent evt) {  }
			@Override
			public void onButtonLostFocus(MouseMotionEvent evt) {  }
		};
		btnScrollTrack.setScaleEW(false);
		btnScrollTrack.setScaleNS(true);
		btnScrollTrack.setDockS(true);
		btnScrollTrack.setInterval(100);
		this.addChild(btnScrollTrack);
		
		btnScrollTrack.removeEffect(Effect.EffectEvent.Hover);
		btnScrollTrack.removeEffect(Effect.EffectEvent.Press);
		btnScrollTrack.removeEffect(Effect.EffectEvent.Release);
		
		btnScrollThumb = new ButtonAdapter(screen, UID + "btnScrollThumb",
			new Vector2f(0, 0),//btnScrollTrack.getHeight()-getWidth()),
			new Vector2f(getWidth(), getWidth()),
			screen.getStyle("ScrollArea#VScrollBar").getVector4f("thumbResizeBorders"),
			screen.getStyle("ScrollArea#VScrollBar").getString("thumbImg")
		) {
			@Override
			public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {  }
			@Override
			public void onButtonMouseRightDown(MouseButtonEvent evt, boolean toggled) {  }
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {  }
			@Override
			public void onButtonMouseRightUp(MouseButtonEvent evt, boolean toggled) {  }
			@Override
			public void onButtonStillPressedInterval() {
				
			}
			@Override
			public void controlMoveHook() {
				if (scrollableArea != null) {
				//	setThumbScale();
					setByThumbPosition();
				}
			}
			@Override
			public void onButtonFocus(MouseMotionEvent evt) {  }
			@Override
			public void onButtonLostFocus(MouseMotionEvent evt) {  }
		};
		btnScrollThumb.setButtonHoverInfo(screen.getStyle("ScrollArea#VScrollBar").getString("thumbHoverImg"), ColorRGBA.White);
		btnScrollThumb.setButtonPressedInfo(screen.getStyle("ScrollArea#VScrollBar").getString("thumbPressedImg"), ColorRGBA.Gray);
		btnScrollThumb.setIsMovable(true);
		btnScrollThumb.setlockToParentBounds(true);
		btnScrollThumb.setScaleEW(false);
		btnScrollThumb.setScaleNS(true);
		btnScrollThumb.setDockN(true);
		btnScrollTrack.addChild(btnScrollThumb);
		
		btnScrollUp = new ButtonAdapter(screen, UID + "btnScrollUp",
			new Vector2f(0, 0),
			new Vector2f(getWidth(), getWidth()),
			screen.getStyle("ScrollArea#VScrollBar").getVector4f("btnUpResizeBorders"),
			screen.getStyle("ScrollArea#VScrollBar").getString("btnUpImg")
		) {
			@Override
			public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
				if (btnScrollThumb.getY() < (btnScrollTrack.getHeight()-btnScrollThumb.getHeight())) {
					btnScrollThumb.setY(btnScrollThumb.getY()+btnInc);
				}
				scrollScrollableArea();
			}
			@Override
			public void onButtonMouseRightDown(MouseButtonEvent evt, boolean toggled) {  }
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {  }
			@Override
			public void onButtonMouseRightUp(MouseButtonEvent evt, boolean toggled) {  }
			@Override
			public void onButtonStillPressedInterval() {
				if (btnScrollThumb.getY() < (btnScrollTrack.getHeight()-btnScrollThumb.getHeight())) {
					btnScrollThumb.setY(btnScrollThumb.getY()+btnInc);
				}
				scrollScrollableArea();
			}
			@Override
			public void onButtonFocus(MouseMotionEvent evt) {  }
			@Override
			public void onButtonLostFocus(MouseMotionEvent evt) {  }
		};
		btnScrollUp.setButtonHoverInfo(screen.getStyle("ScrollArea#VScrollBar").getString("btnUpHoverImg"), ColorRGBA.White);
		btnScrollUp.setButtonPressedInfo(screen.getStyle("ScrollArea#VScrollBar").getString("btnUpPressedImg"), ColorRGBA.Gray);
		btnScrollUp.setScaleEW(false);
		btnScrollUp.setScaleNS(false);
		btnScrollUp.setDockN(true);
		btnScrollUp.setDockW(true);
		btnScrollUp.setInterval(100);
		if (screen.getStyle("ScrollArea#VScrollBar").getBoolean("useBtnUpArrowIcon")) {
			btnScrollUp.setButtonIcon(18, 18, screen.getStyle("Common").getString("arrowUp"));
		}
		this.addChild(btnScrollUp);
		
		btnScrollDown = new ButtonAdapter(screen, UID + "btnScrollDown",
			new Vector2f(0, getHeight()-getWidth()),
			new Vector2f(getWidth(), getWidth()),
			screen.getStyle("ScrollArea#VScrollBar").getVector4f("btnDownResizeBorders"),
			screen.getStyle("ScrollArea#VScrollBar").getString("btnDownImg")
		) {
			@Override
			public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
				if (btnScrollThumb.getY() > 0) {
					btnScrollThumb.setY(btnScrollThumb.getY()-btnInc);
				}
				scrollScrollableArea();
			}
			@Override
			public void onButtonMouseRightDown(MouseButtonEvent evt, boolean toggled) {  }
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {  }
			@Override
			public void onButtonMouseRightUp(MouseButtonEvent evt, boolean toggled) {  }
			@Override
			public void onButtonStillPressedInterval() {
				if (btnScrollThumb.getY() > 0) {
					btnScrollThumb.setY(btnScrollThumb.getY()-btnInc);
				}
				scrollScrollableArea();
			}
			@Override
			public void onButtonFocus(MouseMotionEvent evt) {  }
			@Override
			public void onButtonLostFocus(MouseMotionEvent evt) {  }
		};
		btnScrollDown.setButtonHoverInfo(screen.getStyle("ScrollArea#VScrollBar").getString("btnDownHoverImg"), ColorRGBA.White);
		btnScrollDown.setButtonPressedInfo(screen.getStyle("ScrollArea#VScrollBar").getString("btnDownPressedImg"), ColorRGBA.Gray);
		btnScrollDown.setScaleEW(false);
		btnScrollDown.setScaleNS(false);
		btnScrollDown.setDockS(true);
		btnScrollDown.setDockW(true);
		btnScrollDown.setInterval(100);
		if (screen.getStyle("ScrollArea#VScrollBar").getBoolean("useBtnDownArrowIcon")) {
			btnScrollDown.setButtonIcon(18, 18, screen.getStyle("Common").getString("arrowDown"));
		}
		this.addChild(btnScrollDown);
	}
	
	public void setButtonInc(int btnInc) {
		this.btnInc = btnInc;
	}
	
	public int getButtonInc() {
		return this.btnInc;
	}
	
	public void setTrackInc(int trackInc) {
		this.trackInc = trackInc;
	}
	
	public int getTrackInc() {
		return this.trackInc;
	}
	
	public void setScrollableArea(ScrollArea scrollableArea) {
		this.scrollableArea = scrollableArea;
	}
	
	protected void scrollToTop() {
		
	}
	
	protected void scrollToBottom() {
		setThumbScale();
		btnScrollThumb.setY(0);
		setByThumbPosition();
	}
	
	private void scrollScrollableArea() {
		if (this.scrollableArea != null) {
			setByThumbPosition();
		}
	}
	
	public void scrollYTo(float y) {
		float scrollLayerHeight = scrollableArea.getScrollableHeight();
		float diff = scrollLayerHeight-scrollableArea.getHeight();
		float scale = 1/diff*y;
		float trackArea = btnScrollTrack.getHeight()-btnScrollThumb.getHeight();
		if (-(trackArea*scale) < 0)
			btnScrollThumb.setY(0);
		else
			btnScrollThumb.setY(-(trackArea*scale));
		setByThumbPosition();
	}
	
	public void scrollByYInc(float yInc) {
		if (this.scrollableArea != null) {
			// TODO: Add bounds constaints and adjust Inc accordingly
			if (yInc < 0) {
				if (btnScrollThumb.getY() == 0) {
					yInc = 0;
				} else if (btnScrollThumb.getY()+yInc < 0) {
					yInc = FastMath.abs(btnScrollThumb.getY()+yInc);
				}
			} else if (yInc > 0) {
				if (btnScrollThumb.getY()+btnScrollThumb.getHeight() == btnScrollTrack.getHeight()) {
					yInc = 0;
				} else if (btnScrollThumb.getY()+btnScrollThumb.getHeight()+yInc > btnScrollTrack.getHeight()) {
					yInc = yInc-FastMath.abs(btnScrollTrack.getHeight()-(btnScrollThumb.getY()+btnScrollThumb.getHeight()+yInc));
				}
			}
			btnScrollThumb.setY(btnScrollThumb.getY()+yInc);
			setByThumbPosition();
		}
	}
	
	protected void setThumbScale() {
		float scrollLayerHeight = scrollableArea.getScrollableHeight();
	//	System.out.println(scrollLayerHeight);
		float diff = scrollLayerHeight-scrollableArea.getHeight();
		if (diff > 0) {
			float scale = 1/scrollLayerHeight*diff;
			btnScrollThumb.setHeight(btnScrollTrack.getHeight()-(btnScrollTrack.getHeight()*scale));
		//	if (btnScrollThumb.getY()+btnScrollThumb.getHeight() > btnScrollTrack.getHeight())
				btnScrollThumb.setY(btnScrollTrack.getHeight()-btnScrollThumb.getHeight());
		} else {
			btnScrollThumb.setY(0);
			btnScrollThumb.setHeight(btnScrollTrack.getHeight());
		}
		
		if (diff > 10) {
			if (getParent() == null) {
				getElementParent().attachChild(this);
			}
		} else {
			removeFromParent();
		}
	}
	
	private void setByThumbPosition() {
		float scrollLayerHeight = scrollableArea.getScrollableHeight();
		float diff = btnScrollTrack.getHeight()-btnScrollThumb.getHeight();
		float sadiff = scrollLayerHeight-scrollableArea.getHeight()+(scrollableArea.getPadding());
		float yLoc = sadiff*(btnScrollThumb.getY()/diff);
		
		if (scrollableArea.getIsTextOnly()) {
			if (yLoc < sadiff) {
				scrollableArea.scrollYTo(scrollLayerHeight-yLoc);
			} else {
				scrollableArea.scrollYTo(scrollLayerHeight-sadiff);
			}
		} else {
			if (yLoc < sadiff) {
				scrollableArea.scrollYTo(-yLoc);
			} else {
				scrollableArea.scrollYTo(-sadiff);
			}
		}
	}
}
