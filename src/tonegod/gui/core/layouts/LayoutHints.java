/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.core.layouts;

/**
 *
 * @author t0neg0d
 */
public class LayoutHints {
	private boolean useLayoutPadX = true;
	private boolean useLayoutPadY = true;
	private boolean advanceY = false;
	private boolean lineFeed = false;
	private int numLineFeeds = 1;
	private float elementPadX = 0;
	private float elementPadY = 0;
	
	public void setLayoutAdvanceY(boolean advanceY) { this.advanceY = advanceY; }
	public boolean getLayoutAdvanceY() { return this.advanceY; }
	public void setLayoutLineFeed(boolean lineFeed) { this.lineFeed = lineFeed; }
	public boolean getLayoutLineFeed() { return this.lineFeed; }
	public void setLayoutNumLineFeeds(int numLineFeeds) { this.numLineFeeds = numLineFeeds; }
	public int getLayoutNumLineFeeds() { return this.numLineFeeds; }
	public void setUseLayoutPadX(boolean useLayoutPadX) { this.useLayoutPadX = useLayoutPadX; }
	public boolean getUseLayoutPadX() { return this.useLayoutPadX; }
	public void setUseLayoutPadY(boolean useLayoutPadY) { this.useLayoutPadY = useLayoutPadY; }
	public boolean getUseLayoutPadY() { return this.useLayoutPadY; }
	public void setElementPadX(float elementPadX) { this.elementPadX = elementPadX; }
	public float getElementPadX() { return this.elementPadX; }
	public void setElementPadY(float elementPadY) { this.elementPadY = elementPadY; }
	public float getElementPadY() { return this.elementPadY; }
}