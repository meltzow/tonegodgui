/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.text;

import com.jme3.font.BitmapFont;
import com.jme3.font.LineWrapMode;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 *
 * @author t0neg0d
 */
public class Label extends Element {
	
	public Label(Screen screen, String UID, Vector2f position, Vector2f dimensions) {
		this(screen, UID, position, dimensions,
			screen.getStyle("Label").getVector4f("resizeBorders"),
			screen.getStyle("Label").getString("defaultImg")
		);
	}
	
	public Label(Screen screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg);
		
		this.setIsResizable(true);
		this.setScaleNS(false);
		this.setScaleEW(false);
		this.setDockN(true);
		this.setDockW(true);
		
		// Load default font info
		this.setFontColor(screen.getStyle("Label").getColorRGBA("fontColor"));
		this.setFontSize(screen.getStyle("Label").getFloat("fontSize"));
		this.setTextAlign(BitmapFont.Align.valueOf(screen.getStyle("Label").getString("textAlign")));
		this.setTextVAlign(BitmapFont.VAlign.valueOf(screen.getStyle("Label").getString("textVAlign")));
		this.setTextWrap(LineWrapMode.valueOf(screen.getStyle("Label").getString("textWrap")));
		this.setTextPadding(screen.getStyle("Label").getFloat("textPadding"));
		this.setTextClipPadding(screen.getStyle("Label").getFloat("textPadding"));
		
		
	}
	
}
