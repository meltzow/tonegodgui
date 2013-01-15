/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.menuing;

/**
 *
 * @author t0neg0d
 */
public class MenuItem {
	Menu menu, subMenu;
	String caption, value;
	
	public MenuItem(Menu menu, String caption, String value, Menu subMenu) {
		this.menu = menu;
		this.subMenu = subMenu;
		this.caption = caption;
		this.value = value;
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
	
	public String getValue() {
		return this.value;
	}
}
