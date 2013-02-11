/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.buttons;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import java.util.ArrayList;
import java.util.List;
import tonegod.gui.controls.form.Form;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 *
 * @author t0neg0d
 */
public abstract class RadioButtonGroup {
	Screen screen;
	Form form = null;
	String UID;
	private List<RadioButton> radioButtons = new ArrayList();
	int selectedIndex = -1;
	RadioButton selected = null;
	
	public RadioButtonGroup(Screen screen, String UID, Form form) {
		this.screen = screen;
		this.UID = UID;
		this.form = form;
	}
	
	public String getUID() {
		return this.UID;
	}
	
	public void addRadioButton(Vector2f position, String caption, Object value) {
		RadioButton radio = new RadioButton(position, caption,  value, this);
		radioButtons.add(radio);
	}
	
	public void setForm(Form form) {
		this.form = form;
		
		for (RadioButton radio : radioButtons) {
			if (form.getFormElement(radio.getRadioButton()) == null)
				form.addFormElement(form.getFormElement(radio.getRadioButton()));
		}
	}
	
	public void setSelected(int index) {
		if (index >= 0 && index < radioButtons.size()) {
			RadioButton rb = radioButtons.get(index);
			this.selected = rb;
			this.selectedIndex = index;
			for (RadioButton rb2 : radioButtons) {
				if (rb2 != this.selected)
					rb2.getRadioButton().setIsChecked(false);
				else {
					
				}
			}
			onSelect(selectedIndex, rb.getValue());
		}
	}
	
	protected void setSelected(RadioButton radio) {
		this.selected = radio;
		this.selectedIndex = radioButtons.indexOf(radio);
		for (RadioButton rb : radioButtons) {
			if (rb != this.selected) {
				if (rb.getRadioButton().getIsChecked())
					rb.getRadioButton().setIsChecked(false);
			}
		}
		onSelect(selectedIndex, radio.getValue());
	}
	
	public abstract void onSelect(int index, Object value);
	
	public void setDisplayElement(Element element) {
		for (RadioButton rb : radioButtons) {
			CheckBox radio = rb.getRadioButton();
			if (screen.getElementById(radio.getUID()) == null) {
				if (element != null) {
					element.addChild(radio);
				} else {
					screen.addElement(radio);
				}
			}
		}
	}
	
	public class RadioButton {
		CheckBox radioButton;
		RadioButtonGroup group;
		String caption;
		Object value;
		
		public RadioButton(Vector2f position, String caption, Object value, RadioButtonGroup group) {
			this.group = group;
			radioButton = new CheckBox(screen, group.getUID() + "RadioButton:" + group.radioButtons.size(), position,
				screen.getStyle("RadioButton").getVector2f("defaultSize"),
				screen.getStyle("RadioButton").getVector4f("resizeBorders"),
				screen.getStyle("RadioButton").getString("defaultImg")
			) {
				@Override
				public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
					setSelected(toggled);
				}
			};
			radioButton.setButtonHoverInfo(
				screen.getStyle("RadioButton").getString("hoverImg"),
				screen.getStyle("RadioButton").getColorRGBA("hoverColor")
			);
			radioButton.setButtonPressedInfo(
				screen.getStyle("RadioButton").getString("pressedImg"),
				screen.getStyle("RadioButton").getColorRGBA("pressedColor")
			);
			radioButton.setCheckboxText(caption);
			this.value = value;
		}
		
		public CheckBox getRadioButton() {
			return this.radioButton;
		}
		
		public String getCaption() {
			return this.caption;
		}
		
		public Object getValue() {
			return this.value;
		}
		
		private void setSelected(boolean toggled) {
			if (toggled)
				group.setSelected(this);
		}
	}
}
