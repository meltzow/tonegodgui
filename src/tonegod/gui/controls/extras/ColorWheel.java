/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.extras;

import com.jme3.font.BitmapFont;
import com.jme3.font.LineWrapMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.util.BufferUtils;
import java.nio.FloatBuffer;
import java.util.Set;
import tonegod.gui.controls.lists.Dial;
import tonegod.gui.controls.lists.Slider;
import tonegod.gui.controls.text.Label;
import tonegod.gui.controls.text.TextField;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 *
 * @author t0neg0d
 */
public class ColorWheel extends Window {
	Dial primarySelector;
	Element colorSwatch;
	Slider secondarySelector, sR, sG, sB, sH, sL, sS;
	TextField tfR, tfG, tfB;
	TextField tfC, tfY, tfK, tfM;
	TextField tfHex;
	
	int R = 255, G = 0, B = 0, A = 100, H = 100, S = 100, L = 100;
	float red = 1.0f, green = 0.0f, blue = 0.0f, alpha = 1.0f, hue = 1.0f, saturation = 1.0f, light = 1.0f;
	float finalRed = 1.0f, finalGreen = 0.0f, finalBlue = 0.0f, finalAlpha = 1.0f, finalHue = 1.0f, finalSaturation = 1.0f, finalLight = 1.0f;
	
	/**
	 * Creates a new instance of the ColorWheel control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 */
	public ColorWheel(Screen screen, String UID, Vector2f position) {
		this(screen, UID, position,
			screen.getStyle("Window").getVector2f("defaultSize"),
			screen.getStyle("Window").getVector4f("resizeBorders"),
			screen.getStyle("Window").getString("defaultImg")
		);
	}
	
	/**
	 * Creates a new instance of the ColorWheel control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 */
	public ColorWheel(Screen screen, String UID, Vector2f position, Vector2f dimensions) {
		this(screen, UID, position, dimensions,
			screen.getStyle("Window").getVector4f("resizeBorders"),
			screen.getStyle("Window").getString("defaultImg")
		);
	}
	
	/**
	 * Creates a new instance of the ColorWheel control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 * @param resizeBorders A Vector4f containg the border information used when resizing the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg The default image to use for the Slider's track
	 */
	public ColorWheel(Screen screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg);
		
		setScaleEW(false);
		setScaleNS(false);
		
		tfR = new TextField(screen, UID + ":tfR", new Vector2f(350, 35), new Vector2f(60, 15)) {
			
		};
		tfR.setType(TextField.Type.NUMERIC);
		tfR.setMaxLength(5);
		tfR.setFontSize(16);
		tfR.setIsEnabled(false);
		tfR.setScaleEW(false);
		tfR.setScaleNS(false);
		addChild(tfR);
		
		tfG = new TextField(screen, UID + ":tfG", new Vector2f(350, 55), new Vector2f(60, 15)) {
			
		};
		tfG.setType(TextField.Type.NUMERIC);
		tfG.setMaxLength(5);
		tfG.setFontSize(16);
		tfG.setIsEnabled(false);
		tfG.setScaleEW(false);
		tfG.setScaleNS(false);
		addChild(tfG);
		
		tfB = new TextField(screen, UID + ":tfB", new Vector2f(350, 75), new Vector2f(60, 15)) {
			
		};
		tfB.setType(TextField.Type.NUMERIC);
		tfB.setMaxLength(5);
		tfB.setFontSize(16);
		tfB.setIsEnabled(false);
		tfB.setScaleEW(false);
		tfB.setScaleNS(false);
		addChild(tfB);
		
		primarySelector = new Dial(screen, UID + ":primarySelector", new Vector2f(10,35), new Vector2f(getHeight()-45,getHeight()-45), Vector4f.ZERO, screen.getStyle("ColorWheel").getString("colorWheelImg")) {
			@Override
			public void onChange(int selectedIndex, Object value) {
				float altR = 180-(Integer)value*3.60f;
				if (altR < 0) altR += 360;
				
				if (altR > 0 && altR < 60)			{ R = 255; G = (int)(altR*4.25f); B = 0; }
				else if (altR > 60 && altR < 120)	{ R = 255-(int)((altR-60)*4.25f); G = 255; B = 0; }
				else if (altR > 120 && altR < 180)	{ R = 0; G = 255; B = (int)((altR-120)*4.25f); }
				else if (altR > 180 && altR < 240)	{ R = 0; G = 255-(int)((altR-180)*4.25f); B = 255; }
				else if (altR > 240 && altR < 300)	{ R = (int)((altR-240)*4.25f); G = 0; B = 255; }
				else if (altR > 300 && altR < 359)	{ R = 255; G = 0; B = 255-(int)((altR-300)*4.25f); }
				
				factorAndDisplay();
			}
		};
		primarySelector.setDialImageIndicator(screen.getStyle("ColorWheel").getString("colorWheelSelectorImg"));
		primarySelector.setIsMovable(false);
		primarySelector.setIsResizable(false);
	//	primarySelector.setScaleEW(true);
	//	primarySelector.setScaleNS(true);
		primarySelector.setDockN(true);
		primarySelector.setDockW(true);
		addChild(primarySelector);
		
		float csX = 10+(primarySelector.getWidth()/2)-25;
		float csY = 35+(primarySelector.getHeight()/2)-25;
		float csW = 50;
		float csH = 50;
		colorSwatch = new Element(screen, UID + ":colorSwatch", new Vector2f(csX, csY), new Vector2f(csW, csH), Vector4f.ZERO, null);
		colorSwatch.getElementMaterial().setColor("Color", new ColorRGBA(1.0f, 0.0f, 0.0f, 1.0f));
		colorSwatch.setScaleEW(false);
		colorSwatch.setScaleNS(false);
		colorSwatch.setIsMovable(false);
		colorSwatch.setIsResizable(false);
		addChild(colorSwatch);
		
		sR = new Slider(screen, UID + ":sR", new Vector2f(174, 35), new Vector2f(150, 15), Vector4f.ZERO, null, Slider.Orientation.HORIZONTAL, true) {
			@Override
			public void onChange(int selectedIndex, Object value) {
				R = (int)(selectedIndex*2.55f);
				factorAndDisplay();
			}
		};
		sR.getElementMaterial().setColor("Color", new ColorRGBA(1.0f, 0.0f, 0.0f, 1.0f));
		addChild(sR);
		
		Label lR = new Label(screen, UID + ":lR", new Vector2f(326, 32), new Vector2f(30, 15));
		lR.setFontSize(18);
		lR.setTextVAlign(BitmapFont.VAlign.Center);
		lR.setText("  R  ");
		addChild(lR);
		
		sG = new Slider(screen, UID + ":sG", new Vector2f(174, 55), new Vector2f(150, 15), Vector4f.ZERO, null, Slider.Orientation.HORIZONTAL, true) {
			@Override
			public void onChange(int selectedIndex, Object value) {
				G = (int)(selectedIndex*2.55f);
				factorAndDisplay();
			}
		};
		sG.getElementMaterial().setColor("Color", new ColorRGBA(0.0f, 1.0f, 0.0f, 1.0f));
		addChild(sG);
		
		Label lG = new Label(screen, UID + ":lG", new Vector2f(326, 52), new Vector2f(30, 15));
		lG.setFontSize(18);
		lG.setTextVAlign(BitmapFont.VAlign.Center);
		lG.setText("  G  ");
		addChild(lG);
		
		sB = new Slider(screen, UID + ":sB", new Vector2f(174, 75), new Vector2f(150, 15), Vector4f.ZERO, null, Slider.Orientation.HORIZONTAL, true) {
			@Override
			public void onChange(int selectedIndex, Object value) {
				B = (int)(selectedIndex*2.55f);
				factorAndDisplay();
			}
		};
		sB.getElementMaterial().setColor("Color", new ColorRGBA(0.0f, 0.0f, 1.0f, 1.0f));
		addChild(sB);
		
		Label lB = new Label(screen, UID + ":lB", new Vector2f(326, 72), new Vector2f(30, 15));
		lB.setFontSize(18);
		lB.setTextVAlign(BitmapFont.VAlign.Center);
		lB.setText("  B  ");
		addChild(lB);
		
		sH = new Slider(screen, UID + ":sH", new Vector2f(174, 95), new Vector2f(150,15), Vector4f.ZERO, null, Slider.Orientation.HORIZONTAL, true) {
			@Override
			public void onChange(int selectedIndex, Object value) {
				H = selectedIndex;
				factorAndDisplay();
			}
		};
		sH.getElementMaterial().setColor("Color", new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
		sH.getElementMaterial().setBoolean("VertexColor", true);
		sH.getModel().setGradientFillVertical(ColorRGBA.White, new ColorRGBA(red, green, blue, 1.0f));
		addChild(sH);
		
		Label lH = new Label(screen, UID + ":lH", new Vector2f(326, 92), new Vector2f(30, 15));
		lH.setFontSize(18);
		lH.setTextVAlign(BitmapFont.VAlign.Center);
		lH.setText("  H  ");
		addChild(lH);
		
		sS = new Slider(screen, UID + ":sS", new Vector2f(174, 115), new Vector2f(150,15), Vector4f.ZERO, screen.getStyle("ColorWheel").getString("colorSImg"), Slider.Orientation.HORIZONTAL, true) {
			@Override
			public void onChange(int selectedIndex, Object value) {
				S = selectedIndex;
				saturation = L*0.01f;
				finalSaturation = saturation;
				factorAndDisplay();
			}
		};
		sS.getElementMaterial().setColor("Color", new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
		sS.getElementMaterial().setBoolean("VertexColor", true);
		sS.getModel().setGradientFillVertical(ColorRGBA.Gray, new ColorRGBA(red, green, blue, 1.0f));
		addChild(sS);
		
		Label lS = new Label(screen, UID + ":lS", new Vector2f(326, 112), new Vector2f(30, 15));
		lS.setFontSize(18);
		lS.setTextVAlign(BitmapFont.VAlign.Center);
		lS.setText("  S  ");
		addChild(lS);
		
		sL = new Slider(screen, UID + ":sL", new Vector2f(174, 135), new Vector2f(150,15), Vector4f.ZERO, screen.getStyle("ColorWheel").getString("colorLImg"), Slider.Orientation.HORIZONTAL, true) {
			@Override
			public void onChange(int selectedIndex, Object value) {
				L = selectedIndex;
				light = L*0.01f;
				finalLight = light;
				factorAndDisplay();
			}
		};
		sL.getElementMaterial().setColor("Color", new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
		sL.getElementMaterial().setBoolean("VertexColor", true);
		sL.getModel().setGradientFillVertical(ColorRGBA.Black, new ColorRGBA(red, green, blue, 1.0f));
		addChild(sL);
		
		Label lL = new Label(screen, UID + ":lL", new Vector2f(326, 132), new Vector2f(30, 15));
		lL.setFontSize(18);
		lL.setTextVAlign(BitmapFont.VAlign.Center);
		lL.setText("  L  ");
		addChild(lL);
		
		sR.setSelectedIndexWithCallback(100);
		sH.setSelectedIndexWithCallback(100);
		sS.setSelectedIndexWithCallback(100);
		sL.setSelectedIndexWithCallback(100);
		
	}
	
	private void factorAndDisplay() {
		factorHue();
		factorSaturation();
		factorLight();
		factorRed();
		factorGreen();
		factorBlue();
		factorAlpha();
		displayFactoredColor();
	}
	private void factorRed() {
		red = (float)R/255f;
		finalRed = applySaturation(applyHue(red))*finalLight;
	}
	
	private void factorGreen() {
		green = (float)G/255f;
		finalGreen = applySaturation(applyHue(green))*finalLight;
	}
	
	private void factorBlue() {
		blue = (float)B/255f;
		finalBlue = applySaturation(applyHue(blue))*finalLight;
	}
	
	private void factorHue() {
		hue = H*0.01f;
		finalHue = hue;
	}
	
	private float applyHue(float c) {
		float a = H*0.01f;
		float ret = 1.0f * (1.0f - a) + c * a;
		return ret;
	}
	
	private void factorSaturation() {
		saturation = S*0.01f;
		finalSaturation = saturation;
	}
	
	private float applySaturation(float c) {
		red = (float)R/255f;
		green = (float)G/255f;
		blue = (float)B/255f;
		float max = Math.max(red, green);
		max = Math.max(max, blue);
		float min = Math.min(red, green);
		min = Math.min(min, blue);
		float mid = min+(max-min/2);
		float ret = c;
		if (c < mid) {
			ret = mid-(FastMath.abs(mid-c))*(finalSaturation);
		} else {
			ret = mid+(c-mid*finalSaturation/2);
		}
		return c;
	}
	
	private void factorLight() {
		light = L*0.01f;
		finalLight = light;
	}
	
	private void factorAlpha() {
		alpha = A*0.01f;
		finalAlpha = alpha;
	}
	
	private void displayFactoredColor() {
		String strR = String.valueOf(finalRed);
		if (strR.length() > 5) strR = strR.substring(0,5);
		tfR.setText(strR);
		String strG = String.valueOf(finalGreen);
		if (strG.length() > 5) strG = strG.substring(0,5);
		tfG.setText(strG);
		String strB = String.valueOf(finalBlue);
		if (strB.length() > 5) strB = strB.substring(0,5);
		tfB.setText(strB);
		sR.setSelectedIndex((int)((int)(red*100)));
		sG.setSelectedIndex((int)((int)(green*100)));
		sB.setSelectedIndex((int)((int)(blue*100)));
		colorSwatch.getElementMaterial().setColor("Color", new ColorRGBA(finalRed, finalGreen, finalBlue, finalAlpha));
		sH.getModel().setGradientFillVertical(ColorRGBA.White, new ColorRGBA(red, green, blue, 1.0f));
		sS.getModel().setGradientFillVertical(ColorRGBA.Gray, new ColorRGBA(red, green, blue, 1.0f));
		sL.getModel().setGradientFillVertical(ColorRGBA.Black, new ColorRGBA(red, green, blue, 1.0f));
	}
}
