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
	String caption;
	Object value;
	
	public MenuItem(Menu menu, String caption, Object value, Menu subMenu) {
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
	
	public Object getValue() {
		return this.value;
	}
}
