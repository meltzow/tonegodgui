/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.form;

import java.util.ArrayList;
import java.util.List;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 *
 * @author t0neg0d
 */
public class Form {
	private Screen screen;
	private List<Element> elements = new ArrayList();
	private int currentTabIndex = 0;
	
	public Form(Screen screen) {
		
	}
	public void addFormElement(Element element) {
		elements.add(element);
	}
	
	public void removeFormElement(Element element) {
		elements.remove(element);
	}
	
	public void setSelectedTabIndex(Element element) {
		currentTabIndex = element.getTabIndex();
	}
	
	public void tabNext() {
		currentTabIndex++;
		if (currentTabIndex == elements.size())
			currentTabIndex = 0;
		boolean elementFound = false;
		for (Element el : elements) {
			if (el.getTabIndex() == currentTabIndex) {
				screen.setTabFocusElement(el);
				elementFound = true;
			}
		}
		if (!elementFound) {
			screen.resetTabFocusElement();
			tabNext();
		}
	}
	
	public void tabPrev() {
		currentTabIndex--;
		if (currentTabIndex == -1)
			currentTabIndex = elements.size()-1;
		boolean elementFound = false;
		for (Element el : elements) {
			if (el.getTabIndex() == currentTabIndex) {
				screen.setTabFocusElement(el);
				elementFound = true;
			}
		}
		if (!elementFound) {
			screen.resetTabFocusElement();
			tabPrev();
		}
	}
	
	public void submitForm() {
		
	}
}
