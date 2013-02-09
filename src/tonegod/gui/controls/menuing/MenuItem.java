/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.menuing;

import tonegod.gui.controls.buttons.CheckBox;

/**
 *
 * @author t0neg0d
 */
public class MenuItem {
	Menu menu, subMenu;
	String caption;
	Object value;
	boolean isToggleItem = false;
	CheckBox toggle = null;
	boolean isToggled = false;
	
	public MenuItem(Menu menu, String caption, Object value, Menu subMenu, boolean isToggleItem) {
		this.menu = menu;
		this.subMenu = subMenu;
		this.caption = caption;
		this.value = value;
		this.isToggleItem = isToggleItem;
	}
	
	public Menu getMenu() {
		return this.menu;
	}
	
	public Menu getSubMenu() {
		return this.subMenu;
	}
	
	public String getCaption() {
		return this.caption;
	}
	
	public Object getValue() {
		return this.value;
	}
	
	public void setIsToggleItem(boolean isToggleItem) {
		this.isToggleItem = isToggleItem;
	}
	
	public boolean getIsToggleItem() {
		return this.isToggleItem;
	}
	
	public void setIsToggled(boolean isToggled) {
		this.isToggled = isToggled;
		if (toggle != null) {
			toggle.setIsChecked(isToggled);
		}
	}
	
	public boolean getIsToggled() {
		return this.isToggled;
	}
	
	public void setCheckBox(CheckBox toggle) {
		this.toggle = toggle;
	}
}
