/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.lists;

import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import java.util.ArrayList;
import java.util.List;
import tonegod.gui.controls.buttons.Button;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.menuing.MenuItem;
import tonegod.gui.controls.text.TextField;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 *
 * @author t0neg0d
 */
public abstract class Spinner extends TextField {
	public static enum Orientation {
		VERTICAL,
		HORIZONTAL
	}
	
	protected List<String> stepValues = new ArrayList();
	private boolean cycle = false;
	private int selectedIndex = 0;
	private Orientation orientation;
	
	ButtonAdapter btnInc, btnDec;
	
	public Spinner(Screen screen, String UID, Vector2f position, Spinner.Orientation orientation, boolean cycle) {
		this(screen, UID, position,
			screen.getStyle("TextField").getVector2f("defaultSize"),
			screen.getStyle("TextField").getVector4f("resizeBorders"),
			screen.getStyle("TextField").getString("defaultImg"),
			orientation,
			cycle
		);
	}
	
	public Spinner(Screen screen, String UID, Vector2f position, Vector2f dimensions, Spinner.Orientation orientation, boolean cycle) {
		this(screen, UID, position, dimensions,
			screen.getStyle("TextField").getVector4f("resizeBorders"),
			screen.getStyle("TextField").getString("defaultImg"),
			orientation,
			cycle
		);
	}
	
	public Spinner(Screen screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg, Spinner.Orientation orientation, boolean cycle) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg
		);
		
		this.orientation = orientation;
		this.cycle = cycle;
		setScaleEW(false);
		setScaleNS(false);
		setDockN(true);
		setDockW(true);
		
		btnInc = new ButtonAdapter(
			screen,
			UID + ":btnInc",
			new Vector2f(getWidth(), 0),
			new Vector2f(getHeight(), getHeight())
		) {
			@Override
			public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
				screen.setTabFocusElement((Spinner)getElementParent());
				((Spinner)getElementParent()).incStep();
			}
			@Override
			public void onButtonStillPressedInterval() {
				((Spinner)getElementParent()).incStep();
			}
		};
		btnInc.setButtonIcon(18, 18, screen.getStyle("Common").getString("arrowRight"));
		btnInc.setDockS(true);
		btnInc.setDockW(true);
		
		addChild(btnInc);
		
		btnDec = new ButtonAdapter(
			screen,
			UID + ":btnDec",
			new Vector2f(-getHeight(), 0),
			new Vector2f(getHeight(), getHeight())
		) {
			@Override
			public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
				screen.setTabFocusElement((Spinner)getElementParent());
				((Spinner)getElementParent()).decStep();
			}
			@Override
			public void onButtonStillPressedInterval() {
				((Spinner)getElementParent()).decStep();
			}
		};
		btnDec.setButtonIcon(18, 18, screen.getStyle("Common").getString("arrowLeft"));
		btnDec.setDockS(true);
		btnDec.setDockW(true);
		
		addChild(btnDec);
		
		setIsEnabled(false);
	}
	
	public int getSelectedIndex() {
		return this.selectedIndex;
	}
	
	public void setInterval(float callsPerSecond) {
		btnInc.setInterval(callsPerSecond);
		btnDec.setInterval(callsPerSecond);
	}
	
	public void addStepValue(String value) {
		stepValues.add(value);
		displaySelectedStep();
	}
	
	public void removeStepValue(String value) {
		stepValues.remove(value);
	}
	
	public void setStepIntegerRange(int min, int max, int inc) {
		stepValues.clear();
		for (int i = min; i <= max; i += inc) {
			stepValues.add(String.valueOf(i));
		}
	}
	
	public void setStepFloatRange(float min, float max, float inc) {
		stepValues.clear();
		for (float i = min; i <= max; i += inc) {
			stepValues.add(String.valueOf(i));
		}
	}
	
	private void incStep() {
		selectedIndex++;
		if (selectedIndex == stepValues.size()) {
			if (cycle) {
				selectedIndex = 0;
			} else {
				selectedIndex--;
			}
		}
		displaySelectedStep();
		onChange(selectedIndex, stepValues.get(selectedIndex));
	}
	
	private void decStep() {
		selectedIndex--;
		if (selectedIndex == -1) {
			if (cycle) {
				selectedIndex = stepValues.size()-1;
			} else {
				selectedIndex = 0;
			}
		}
		displaySelectedStep();
		onChange(selectedIndex, stepValues.get(selectedIndex));
	}
	
	private void displaySelectedStep() {
		this.setText(stepValues.get(selectedIndex));
	}
	
	@Override
	public void controlKeyPressHook(KeyInputEvent evt, String text) {
		if (evt.getKeyCode() == KeyInput.KEY_LEFT) {
			decStep();
		} else if (evt.getKeyCode() == KeyInput.KEY_RIGHT) {
			incStep();
		} else {
			displaySelectedStep();
		}
	}
	
	public abstract void onChange(int selectedIndex, String value);
}
