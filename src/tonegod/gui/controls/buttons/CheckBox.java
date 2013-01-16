/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.buttons;

import com.jme3.font.BitmapText;
import com.jme3.font.LineWrapMode;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import tonegod.gui.controls.text.Label;
import tonegod.gui.core.Screen;
import tonegod.gui.effects.Effect;

/**
 *
 * @author t0neg0d
 */
public class CheckBox extends Button {
	
	Label label;
	
	public CheckBox(Screen screen, String UID, Vector2f position) {
		this(screen, UID, position,
			screen.getStyle("CheckBox").getVector2f("defaultSize"),
			screen.getStyle("CheckBox").getVector4f("resizeBorders"),
			screen.getStyle("CheckBox").getString("defaultImg")
		);
	}
	
	public CheckBox(Screen screen, String UID, Vector2f position, Vector2f dimensions) {
		this(screen, UID, position, dimensions,
			screen.getStyle("CheckBox").getVector4f("resizeBorders"),
			screen.getStyle("CheckBox").getString("defaultImg")
		);
	}
	
	public CheckBox(Screen screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg);
		
		this.clearAltImages();
		this.removeEffect(Effect.EffectEvent.Hover);
		this.removeEffect(Effect.EffectEvent.Press);
		this.removeEffect(Effect.EffectEvent.LoseFocus);
		
		label = new Label(
			screen,
			UID + ":Label",
			new Vector2f(getWidth(), 0),
			new Vector2f(100,getHeight())
		);
		label.setIgnoreMouse(true);
		label.setTextWrap(LineWrapMode.NoWrap);
		
		this.setIsToggleButton(true);
		
		if (screen.getStyle("CheckBox").getString("hoverImg") != null) {
			setButtonHoverInfo(
				screen.getStyle("CheckBox").getString("hoverImg"),
				screen.getStyle("CheckBox").getColorRGBA("hoverColor")
			);
		}
		if (screen.getStyle("CheckBox").getString("pressedImg") != null) {
			setButtonPressedInfo(
				screen.getStyle("CheckBox").getString("pressedImg"),
				screen.getStyle("CheckBox").getColorRGBA("pressedColor")
			);
		}
		
		populateEffects("CheckBox");
	}
	
	public void setCheckboxText(String text) {
		if (label.getParent() != null) {
			elementChildren.remove(label.getUID());
			label.removeFromParent();
		}
		
		BitmapText temp = new BitmapText(font);
		temp.setSize(fontSize);
		temp.setLineWrapMode(LineWrapMode.NoWrap);
		
		temp.setBox(null);
		temp.setText(text);
		float width = temp.getLineWidth();
		
		label.setWidth(width+getWidth());
		label.setFontSize(fontSize);
		label.setText(text);
		
		addChild(label);
	}
	
	public boolean getIsChecked() {
		return this.getIsToggled();
	}
	
	@Override
	public void onMouseLeftDown(MouseButtonEvent evt, boolean toggled) {  }

	@Override
	public void onMouseRightDown(MouseButtonEvent evt, boolean toggled) {  }

	@Override
	public void onMouseLeftUp(MouseButtonEvent evt, boolean toggled) {  }

	@Override
	public void onMouseRightUp(MouseButtonEvent evt, boolean toggled) {  }

	@Override
	public void onButtonFocus(MouseMotionEvent evt) {  }

	@Override
	public void onButtonLostFocus(MouseMotionEvent evt) {  }

	@Override
	public void onStillPressedInterval() {  }
	
}
