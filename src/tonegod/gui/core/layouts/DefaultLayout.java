/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.core.layouts;

import com.jme3.math.Vector2f;
import tonegod.gui.controls.lists.ComboBox;
import tonegod.gui.controls.lists.SelectBox;
import tonegod.gui.controls.lists.Spinner;
import tonegod.gui.core.Element;
import tonegod.gui.core.ElementManager;

/**
 *
 * @author t0neg0d
 */
public class DefaultLayout implements Layout {
	private ElementManager screen;
	private Element owner;
	private LayoutMode mode = LayoutMode.Flow;
	private Vector2f margins = new Vector2f(10,10);
	private float padding = 5;
	private float lineFeedHeight = 25;
	
	public DefaultLayout(ElementManager screen) {
		this.screen = screen;
	}
	
	@Override
	public void setMode(LayoutMode mode) {
		this.mode = mode;
	}
	
	@Override
	public void setOwner(Element el) {
		this.owner = el;
	}
	
	@Override
	public ElementManager getScreen() {
		return this.screen;
	}
	
	@Override
	public void layoutChildren() {
		// Reset LayoutHelper
		LayoutHelper.reset();
		LayoutHelper.setPadding(padding);
		LayoutHelper.setLineFeedHeight(lineFeedHeight);
		
		// Advance for Margins
		LayoutHelper.advanceX(margins.x);
		LayoutHelper.advanceY(margins.y);
		
		switch (mode) {
			case Vertical:
				verticalLayout();
				break;
			case Horizontal:
				horizontalLayout();
				break;
			case Absolute:
				absoluteLayout();
				break;
			case Flow:
				flowLayout();
				break;
		}
	}
	
	private void verticalLayout() {
		Element lastEl = null;
		
		for (Element el : owner.getElements()) {
			if (lastEl != null) {
				LayoutHelper.resetX();
				LayoutHelper.advanceX(margins.x);
				LayoutHelper.advanceY(lastEl, lastEl.getLayoutHints().getUseLayoutPadY());
				LayoutHelper.advanceY(lastEl.getLayoutHints().getElementPadY());
				if (lastEl.getLayoutHints().getLayoutLineFeed()) {
					for (int lf = 0; lf < lastEl.getLayoutHints().getLayoutNumLineFeeds(); lf++)
						LayoutHelper.advanceY(LayoutHelper.feed());
				}
			}
			
			el.setPosition(LayoutHelper.position());
			el.setY(owner.getHeight()-el.getY()-el.getHeight());
			lastEl = el;
		}
	}
	
	private void horizontalLayout() {
		Element lastEl = null;
		
		for (Element el : owner.getElements()) {
			if (lastEl != null) {
				if (LayoutHelper.position().x + el.getWidth() > owner.getWidth()-margins.x) {
					LayoutHelper.resetX();
					LayoutHelper.advanceX(margins.x);
					LayoutHelper.advanceY(lastEl, lastEl.getLayoutHints().getUseLayoutPadY());
					LayoutHelper.advanceY(lastEl.getLayoutHints().getElementPadY());
				} else {
					LayoutHelper.advanceX(lastEl, lastEl.getLayoutHints().getUseLayoutPadX());
					LayoutHelper.advanceX(lastEl.getLayoutHints().getElementPadX());
				}
			}
			
			el.setPosition(LayoutHelper.position());
			el.setY(owner.getHeight()-el.getY()-el.getHeight());
			lastEl = el;
		}
	}
	
	private void absoluteLayout() {  }
	
	private void flowLayout() {
		Element lastEl = null;
		
		for (Element el : owner.getElements()) {
			if (lastEl != null) {
				boolean advanceY = lastEl.getLayoutHints().getLayoutAdvanceY();
				if (advanceY) {
					LayoutHelper.resetX();
					LayoutHelper.advanceX(margins.x);
					LayoutHelper.advanceY(lastEl, lastEl.getLayoutHints().getUseLayoutPadY());
					LayoutHelper.advanceY(lastEl.getLayoutHints().getElementPadY());
					if (lastEl.getLayoutHints().getLayoutLineFeed()) {
						for (int lf = 0; lf < lastEl.getLayoutHints().getLayoutNumLineFeeds(); lf++)
							LayoutHelper.advanceY(LayoutHelper.feed());
					}
				} else {
					if (LayoutHelper.position().x + el.getWidth() > owner.getWidth()-margins.x) {
						LayoutHelper.resetX();
						LayoutHelper.advanceX(margins.x);
						LayoutHelper.advanceY(lastEl, lastEl.getLayoutHints().getUseLayoutPadY());
						LayoutHelper.advanceY(lastEl.getLayoutHints().getElementPadY());
					} else {
						LayoutHelper.advanceX(lastEl, lastEl.getLayoutHints().getUseLayoutPadX());
						LayoutHelper.advanceX(lastEl.getLayoutHints().getElementPadX());
					}
				}
			}
			
			el.setPosition(LayoutHelper.position());
			el.setY(owner.getHeight()-el.getY()-el.getHeight());
			lastEl = el;
		}
	}
	
	@Override
	public void setMargins(float vMargin, float hMargin) {
		this.margins.set(vMargin, hMargin);
	}

	@Override
	public float getVMargin() {
		return this.margins.x;
	}

	@Override
	public float getHMargin() {
		return this.margins.y;
	}

	@Override
	public Vector2f getMargins() {
		return this.margins;
	}
	
	@Override
	public void setPadding(float padding) {
		this.padding = padding;
	}

	@Override
	public float getPadding() {
		return this.padding;
	}

	@Override
	public void setLineFeedHeight(float lineFeedHeight) {
		this.lineFeedHeight = lineFeedHeight;
	}

	@Override
	public float getLineFeedHeight() {
		return this.lineFeedHeight;
	}
}
