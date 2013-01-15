/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.text;

import com.jme3.app.Application;
import com.jme3.font.BitmapText;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import tonegod.gui.core.Screen;

/**
 *
 * @author t0neg0d
 */
public class Password extends TextField {
	char mask = '*';
	
	public Password(Screen screen, String UID, Vector2f position) {
		this(screen, UID, position,
			screen.getStyle("TextField").getVector2f("defaultSize"),
			screen.getStyle("TextField").getVector4f("resizeBorders"),
			screen.getStyle("TextField").getString("defaultImg")
		);
	}
	
	public Password(Screen screen, String UID, Vector2f position, Vector2f dimensions) {
		this(screen, UID, position, dimensions,
			screen.getStyle("TextField").getVector4f("resizeBorders"),
			screen.getStyle("TextField").getString("defaultImg")
		);
	}
	
	public Password (Screen screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg);
		
	}
	
	public void setMask(char mask) {
		this.mask= mask;
	}
	
	public String getMask() {
		return String.valueOf(this.mask);
	}
}
