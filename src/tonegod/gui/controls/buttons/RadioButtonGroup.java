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
	private Screen screen;
	private String UID;
	private List<Button> radioButtons = new ArrayList();
	private int selectedIndex = -1;
	private Button selected = null;
	
	public RadioButtonGroup(Screen screen, String UID) {
		this.screen = screen;
		this.UID = UID;
	}
	
	/**
	 * Returns the String unique ID of the RadioButtonGroup
	 * @return String
	 */
	public String getUID() {
		return this.UID;
	}
	
	/**
	 * Adds any Button or extended class and enables the Button's Radio state
	 * @param button 
	 */
	public void addButton(Button button) {
		button.setRadioButtonGroup(this);
		radioButtons.add(button);
		
		if (selectedIndex == 0)
			setSelected(0);
	}
	
	/**
	 * Sets the current selected Radio Button to the Button associated with the provided index
	 * @param index 
	 */
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
	
	/**
	 * Sets the current selected Radio Button to the Button instance provided
	 * @param button 
	 */
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
	
	/**
	 * Abstract event method for change in selected Radio Button
	 * @param index The index of the selected button
	 * @param value The selected button instance
	 */
	public abstract void onSelect(int index, Button value);
	
	/**
	 * An alternate way to add all Radio Buttons as children to the provided Element
	 * @param element The element to add Radio Button's to.  null = Screen
	 */
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
