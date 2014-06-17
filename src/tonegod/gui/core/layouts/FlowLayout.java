/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.core.layouts;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import java.util.HashMap;
import java.util.Map;
import tonegod.gui.core.Element;
import tonegod.gui.core.ElementManager;
import tonegod.gui.core.layouts.LayoutHint.SizeUnit;
import static tonegod.gui.core.layouts.LayoutHint.SizeUnit.fill;
import static tonegod.gui.core.layouts.LayoutHint.SizeUnit.percent;

/**
 *
 * @author t0neg0d
 */
public class FlowLayout implements Layout {
	public Map<String,LayoutParam> params = new HashMap();
	private ElementManager screen;
	private Element owner;
	private boolean handlesResize = true;
	private Vector4f margins = new Vector4f(10,10,10,10);
	private Vector4f padding = new Vector4f(5,5,5,5);
	private boolean clip = false;
	
	public FlowLayout(ElementManager screen, String... constraints) {
		this.screen = screen;
		for (String param : constraints) {
			LayoutParam lp = new LayoutParam(param);
			this.params.put(lp.type.name(),lp);
		}
		LayoutParam m = params.get("margins");
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
	public void setHandlesResize(boolean handleResize) {
		this.handlesResize = handleResize;
	}
	
	@Override
	public boolean getHandlesResize() {
		return handlesResize;
	}
	
	@Override
	public void resize() {
		
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
		Element lastEl = null;
		LayoutHelper.reset();
		LayoutHelper.setPadding(padding.x);
		LayoutHelper.advanceX(margins.x);
		LayoutHelper.advanceY(margins.y);
		
		for (Element el : owner.getElements()) {
			if (lastEl != null) {
				boolean advanceY = (lastEl.getLayoutHints().get("wrap") == null) ? false : true;
				float padX = 0, padY = 0;
				if (el.getLayoutHints().get("pad") != null) {
					padX = (Float)el.getLayoutHints().get("pad").getValues().get("left").getValue();
					padY = (Float)el.getLayoutHints().get("pad").getValues().get("top").getValue();
				}
				
				if (advanceY) {
					LayoutHelper.resetX();
					LayoutHelper.advanceX(margins.x);
					LayoutHelper.advanceY(lastEl,true);//lastEl.getLayoutHints().getUseLayoutPadY());
					LayoutHelper.advanceX(padX);
					LayoutHelper.advanceY(padY);
				//	if (lastEl.getLayoutHints().getLayoutLineFeed()) {
				//		for (int lf = 0; lf < lastEl.getLayoutHints().getLayoutNumLineFeeds(); lf++)
				//			LayoutHelper.advanceY(LayoutHelper.feed());
				//	}
				} else {
					if (LayoutHelper.position().x + el.getWidth() > owner.getWidth()-margins.x) {
						LayoutHelper.resetX();
						LayoutHelper.advanceX(margins.x);
						LayoutHelper.advanceY(lastEl,true);
						LayoutHelper.advanceX(padX);
						LayoutHelper.advanceY(padY);
					} else {
						LayoutHelper.advanceX(lastEl,true);
						LayoutHelper.advanceX(padX);
						LayoutHelper.advanceX(padY);
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
				SizeUnit xUnit = (SizeUnit)el.getLayoutHints().get("fillx").getValues().get("fillx").getSizeUnit();
				SizeUnit yUnit = (SizeUnit)el.getLayoutHints().get("filly").getValues().get("filly").getSizeUnit();
				float fillX = (Float)el.getLayoutHints().get("fillx").getValues().get("fillx").getValue();
				float fillY = (Float)el.getLayoutHints().get("filly").getValues().get("filly").getValue();
				
				switch (xUnit) {
					case fill:
						float lWidth = 0;
						if (lastEl != null)
							lWidth = lastEl.getX()+lastEl.getWidth();
						el.setWidth(owner.getWidth()-(margins.x*2)-lWidth);
						break;
					case percent:
						el.setWidth((owner.getWidth()-(margins.x*2))*fillX);
						break;
				}
				switch (yUnit) {
					case fill:
						float lHeight = 0;
						if (lastEl != null)
							lHeight = lastEl.getY()+lastEl.getHeight();
						System.out.println(lHeight);
						el.setHeight(owner.getHeight()-(margins.y*2)-lHeight);
						break;
					case percent:
						el.setHeight((owner.getHeight()-(margins.y*2))*fillY);
						break;
				}
				lastEl = el;
			}
		}
	}
}
