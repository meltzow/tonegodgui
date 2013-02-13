/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.buttons;

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
	String UID;
	private List<Button> radioButtons = new ArrayList();
	int selectedIndex = -1;
	Button selected = null;
	
	public RadioButtonGroup(Screen screen, String UID) {
		this.screen = screen;
		this.UID = UID;
	}
	
	public String getUID() {
		return this.UID;
	}
	
	public void addButton(Button button) {
		button.setRadioButtonGroup(this);
		radioButtons.add(button);
		
		if (selectedIndex == 0)
			setSelected(0);
	}
	
	public void setSelected(int index) {
		if (index >= 0 && index < radioButtons.size()) {
			Button rb = radioButtons.get(index);
			this.selected = rb;
			this.selectedIndex = index;
			for (Button rb2 : radioButtons) {
				if (rb2 != this.selected)
					rb2.setIsToggled(false);
			}
			onSelect(selectedIndex, rb);
		}
	}
	
	protected void setSelected(Button button) {
		this.selected = button;
		this.selectedIndex = radioButtons.indexOf(button);
		for (Button rb : radioButtons) {
			if (rb != this.selected) {
				if (rb.getIsToggled())
					rb.setIsToggled(false);
			}
		}
		onSelect(selectedIndex, button);
	}
	
	public abstract void onSelect(int index, Button value);
	
	public void setDisplayElement(Element element) {
		for (Button rb : radioButtons) {
			if (screen.getElementById(rb.getUID()) == null) {
				if (element != null) {
					element.addChild(rb);
				} else {
					screen.addElement(rb);
				}
			}
		}
	}
	
	
}
