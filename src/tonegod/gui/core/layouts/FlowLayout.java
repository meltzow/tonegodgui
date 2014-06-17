/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.core.layouts;

import com.jme3.math.Vector2f;
import tonegod.gui.core.Element;
import tonegod.gui.core.ElementManager;
import tonegod.gui.core.layouts.LayoutHint.SizeUnit;
import static tonegod.gui.core.layouts.LayoutHint.SizeUnit.fill;
import static tonegod.gui.core.layouts.LayoutHint.SizeUnit.percent;

/**
 *
 * @author t0neg0d
 */
public class FlowLayout extends AbstractLayout {
	
	public FlowLayout(ElementManager screen, String... constraints) {
		super(screen, constraints);
	}
	
	@Override
	public void resize() {
		
	}
	
	@Override
	public void setOwner(Element el) {
		this.owner = el;
	}

	@Override
	public void layoutChildren() {
		Element lastEl = null;
		LayoutHelper.reset();
		LayoutHelper.setPadding(padding.x,padding.y,padding.x,padding.w);
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
					LayoutHelper.advanceY(lastEl,true);
					LayoutHelper.advanceX(padX);
					LayoutHelper.advanceY(padY);
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
		
		LayoutHelper.reset();
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
