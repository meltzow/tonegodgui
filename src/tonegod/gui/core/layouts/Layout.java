/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.core.layouts;

import com.jme3.math.Vector2f;
import tonegod.gui.core.Element;
import tonegod.gui.core.ElementManager;

/**
 *
 * @author t0neg0d
 */
public interface Layout {
	public static enum LayoutMode {
		Vertical,
		Horizontal,
		VerticalLineFeed,
		Flow,
		Absolute
	}
	public void setMode(LayoutMode mode);
	public void setOwner(Element el);
	public ElementManager getScreen();
	public void layoutChildren();
	public void setMargins(float vMargin, float hMargin);
	public float getVMargin();
	public float getHMargin();
	public Vector2f getMargins();
	public void setPadding(float padding);
	public float getPadding();
	public void setLineFeedHeight(float lineFeedHeight);
	public float getLineFeedHeight();
}