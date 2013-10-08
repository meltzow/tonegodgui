/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.core;

import com.jme3.app.Application;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import com.jme3.texture.Texture;
import tonegod.gui.controls.menuing.Menu;
import tonegod.gui.core.Screen.CursorType;
import tonegod.gui.effects.EffectManager;
import tonegod.gui.framework.core.AnimManager;

/**
 *
 * @author t0neg0d
 */
public interface ElementManager {
	
	public Application getApplication();
	
	public float getWidth();
	public float getHeight();
	public Vector2f getMouseXY();
	public Node getGUINode();
	
	public void addElement(Element element);
	public void removeElement(Element element);
	public Element getElementById(String UID);
	public void setKeyboardElement(Element element);
	public void setTabFocusElement(Element element);
	public void resetTabFocusElement();
	public Element getDropElement();
	
	public float getZOrderStepMinor();
	public float getZOrderStepMajor();
	public void updateZOrder(Element element);
	
	public Style getStyle(String key);
	public void setClipboardText(String text);
	public String getClipboardText();
	
	public boolean getUseTextureAtlas();
	public float[] parseAtlasCoords(String coords);
	public Texture getAtlasTexture();
	public Texture createNewTexture(String texturePath);
	public void setGlobalAlpha(float alpha);
	public float getGlobalAlpha();
	
	public EffectManager getEffectManager();
	public AnimManager getAnimManager();
	
	public boolean getUseUIAudio();
	public void setUseUIAudio(boolean use);
	public void setUIAudioVolume(float volume);
	
	public boolean getUseToolTips();
	public void setUseToolTips(boolean use);
	public void updateToolTipLocation();
	
	public void setUseCustomCursors(boolean use);
	public boolean getUseCustomCursors();
	public void setCursor(CursorType cursorType);
	public void setUseCursorEffects(boolean use);
//	public boolean getUseCursorEffects();
	
	public void onKeyEvent(KeyInputEvent evt);
	
	public void showVirtualKeyboard();
	public void hideVirtualKeyboard();
	public void handleAndroidMenuState(Element element);
}
