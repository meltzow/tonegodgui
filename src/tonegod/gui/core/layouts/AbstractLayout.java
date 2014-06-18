/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.core.layouts;

import com.jme3.math.Vector4f;
import java.util.HashMap;
import java.util.Map;
import tonegod.gui.core.Element;
import tonegod.gui.core.ElementManager;

/**
 *
 * @author t0neg0d
 */
public abstract class AbstractLayout implements Layout {
	public Map<String,LayoutParam> params = new HashMap();
	protected ElementManager screen;
	protected Element owner;
	protected Vector4f margins = new Vector4f(10,10,10,10);
	protected Vector4f padding = new Vector4f(5,5,5,5);
	protected Vector4f tempV4 = new Vector4f();
	protected boolean handlesResize = true;
	protected boolean props = false;
	protected boolean clip = false;
	
	public AbstractLayout(ElementManager screen, String... constraints) {
		this.screen = screen;
		for (String param : constraints) {
			LayoutParam lp = new LayoutParam(param);
			this.params.put(lp.type.name(),lp);
		}
		LayoutParam m = params.get("margin");
		if (m != null) {
			margins.set(
				(Float)m.getValues().get("left").getValue(),
				(Float)m.getValues().get("top").getValue(),
				(Float)m.getValues().get("right").getValue(),
				(Float)m.getValues().get("bottom").getValue()
			);
		}
		LayoutParam p = params.get("pad");
		if (p != null) {
			padding.set(
				(Float)p.getValues().get("left").getValue(),
				(Float)p.getValues().get("top").getValue(),
				(Float)p.getValues().get("right").getValue(),
				(Float)p.getValues().get("bottom").getValue()
			);
		}
		LayoutParam c = params.get("clip");
		if (c != null) {
			clip = (Boolean)c.getValues().get("clip").getValue();
		}
	}
	
	@Override
	public Layout define(String... params) {
		for (String param : params) {
			LayoutParam lp = new LayoutParam(param);
			this.params.put(lp.type.name(),lp);
		}
		return this;
	}
	
	@Override
	public Layout set(String param) {
		LayoutParam lp = new LayoutParam(param);
		this.params.put(lp.type.name(),lp);
		return this;
	}
	
	@Override
	public LayoutParam get(String key) {
		return params.get(key);
	}
	
	@Override
	public ElementManager getScreen() {
		return screen;
	}
	
	@Override
	public void setHandlesResize(boolean handlesResize) {
		this.handlesResize = handlesResize;
	}
	
	@Override
	public boolean getHandlesResize() {
		return handlesResize;
	}
	
	
	
	/*
	private ElementManager screen;
	private Element owner;
	private LayoutMode mode = LayoutMode.Flow;
	private Vector2f margins = new Vector2f(10,10);
	private float padding = 5;
	private float lineFeedHeight = 25;
	
	public DefaultLayout(ElementManager screen) {
		this.screen = screen;
	}
	
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
		
		fill();
		
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
		
		fill();
		
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
	
	private void fill() {
		Element lastEl = null;
		for (Element el : owner.getElements()) {
			if (owner.getDimensions() != Vector2f.ZERO) {
				switch (el.getLayoutHints().getFillTypeX()) {
					case Fill:
						float lWidth = 0;
						if (lastEl != null)
							lWidth = lastEl.getX()+lastEl.getWidth();
						el.setWidth(owner.getWidth()-(margins.x*2)-lWidth);
						break;
					case Percent:
						el.setWidth((owner.getWidth()-(margins.x*2))*el.getLayoutHints().getFillX());
						break;
				}
				switch (el.getLayoutHints().getFillTypeY()) {
					case Fill:
						float lHeight = 0;
						if (lastEl != null)
							lHeight = lastEl.getY()+lastEl.getHeight();
						System.out.println(lHeight);
						el.setHeight(owner.getHeight()-(margins.y*2)-lHeight);
						break;
					case Percent:
						el.setHeight((owner.getHeight()-(margins.y*2))*el.getLayoutHints().getFillY());
						break;
				}
				lastEl = el;
			}
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
	
	@Override
	public DefaultLayout clone() {
		DefaultLayout clone = new DefaultLayout(screen);
		clone.setMode(mode);
		clone.setMargins(margins.x, margins.y);
		clone.setPadding(padding);
		clone.setLineFeedHeight(lineFeedHeight);
		return clone;
	}
	*/
}
