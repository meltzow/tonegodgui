/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.extras;

import com.jme3.font.BitmapFont;
import com.jme3.font.LineWrapMode;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.util.BufferUtils;
import java.nio.FloatBuffer;
import java.util.Set;
import tonegod.gui.controls.buttons.ButtonAdapter;
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
public abstract class ColorWheel extends Window {
	Dial primarySelector;
	Element colorSwatch;
	Slider secondarySelector, sR, sG, sB, sH, sL, sS, sA;
	TextField tfR, tfG, tfB, tfA;
	TextField tfHex;
	ButtonAdapter bFinish;
	
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
			screen.getStyle("ColorWheel").getVector2f("defaultSize"),
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
			screen.getStyle("ColorWheel").getVector4f("resizeBorders"),
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
		setIsResizable(false);
		
		float sX = 194;
		float lX = 343;
		float tfX = 370;
		float yInc = 20;
		float tfY = 38;
		float lY = 32;
		float sY = 38;
		float hexX = 242;
		float hexY = getHeight()-34;
		
		tfR = new TextField(screen, UID + ":tfR", new Vector2f(tfX, tfY), new Vector2f(60, 15)) {
			
		};
		tfR.setType(TextField.Type.NUMERIC);
		tfR.setMaxLength(5);
		tfR.setFontSize(16);
		tfR.setIsEnabled(false);
		tfR.setScaleEW(false);
		tfR.setScaleNS(false);
		addChild(tfR);
		
		tfY += yInc;
		
		tfG = new TextField(screen, UID + ":tfG", new Vector2f(tfX, tfY), new Vector2f(60, 15)) {
			
		};
		tfG.setType(TextField.Type.NUMERIC);
		tfG.setMaxLength(5);
		tfG.setFontSize(16);
		tfG.setIsEnabled(false);
		tfG.setScaleEW(false);
		tfG.setScaleNS(false);
		addChild(tfG);
		
		tfY += yInc;
		
		tfB = new TextField(screen, UID + ":tfB", new Vector2f(tfX, tfY), new Vector2f(60, 15)) {
			
		};
		tfB.setType(TextField.Type.NUMERIC);
		tfB.setMaxLength(5);
		tfB.setFontSize(16);
		tfB.setIsEnabled(false);
		tfB.setScaleEW(false);
		tfB.setScaleNS(false);
		addChild(tfB);
		
		tfY += yInc*4;
		
		tfA = new TextField(screen, UID + ":tfA", new Vector2f(tfX, tfY), new Vector2f(60, 15)) {
			
		};
		tfA.setType(TextField.Type.NUMERIC);
		tfA.setMaxLength(5);
		tfA.setFontSize(16);
		tfA.setIsEnabled(false);
		tfA.setScaleEW(false);
		tfA.setScaleNS(false);
		addChild(tfA);
		
		tfHex = new TextField(screen, UID + ":tfHex", new Vector2f(hexX, hexY), new Vector2f(60, 15)) {
			
		};
		tfHex.setType(TextField.Type.ALPHANUMERIC_NOSPACE);
		tfHex.setMaxLength(6);
		tfHex.setFontSize(16);
		tfHex.setIsEnabled(false);
		tfHex.setScaleEW(false);
		tfHex.setScaleNS(false);
		addChild(tfHex);
		
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
		
		float csX = 10+(primarySelector.getWidth()/2)-38;
		float csY = 35+(primarySelector.getHeight()/2)-38;
		float csW = 76;
		float csH = 76;
		colorSwatch = new Element(screen, UID + ":colorSwatch", new Vector2f(csX, csY), new Vector2f(csW, csH), Vector4f.ZERO, null);
		colorSwatch.getElementMaterial().setColor("Color", new ColorRGBA(1.0f, 0.0f, 0.0f, 1.0f));
		colorSwatch.setScaleEW(false);
		colorSwatch.setScaleNS(false);
		colorSwatch.setIsMovable(false);
		colorSwatch.setIsResizable(false);
		addChild(colorSwatch);
		
		sR = new Slider(screen, UID + ":sR", new Vector2f(sX, sY), new Vector2f(150, 15), Vector4f.ZERO, null, Slider.Orientation.HORIZONTAL, true) {
			@Override
			public void onChange(int selectedIndex, Object value) {
				R = (int)(selectedIndex*2.55f);
				factorAndDisplay();
			}
		};
		sR.getElementMaterial().setColor("Color", new ColorRGBA(1.0f, 0.0f, 0.0f, 1.0f));
		addChild(sR);
		
		Label lR = new Label(screen, UID + ":lR", new Vector2f(lX, lY), new Vector2f(30, 15));
		lR.setFontSize(18);
		lR.setTextVAlign(BitmapFont.VAlign.Center);
		lR.setText("  R  ");
		addChild(lR);
		
		lY += yInc;
		sY += yInc;
		
		sG = new Slider(screen, UID + ":sG", new Vector2f(sX, sY), new Vector2f(150, 15), Vector4f.ZERO, null, Slider.Orientation.HORIZONTAL, true) {
			@Override
			public void onChange(int selectedIndex, Object value) {
				G = (int)(selectedIndex*2.55f);
				factorAndDisplay();
			}
		};
		sG.getElementMaterial().setColor("Color", new ColorRGBA(0.0f, 1.0f, 0.0f, 1.0f));
		addChild(sG);
		
		Label lG = new Label(screen, UID + ":lG", new Vector2f(lX, lY), new Vector2f(30, 15));
		lG.setFontSize(18);
		lG.setTextVAlign(BitmapFont.VAlign.Center);
		lG.setText("  G  ");
		addChild(lG);
		
		lY += yInc;
		sY += yInc;
		
		sB = new Slider(screen, UID + ":sB", new Vector2f(sX, sY), new Vector2f(150, 15), Vector4f.ZERO, null, Slider.Orientation.HORIZONTAL, true) {
			@Override
			public void onChange(int selectedIndex, Object value) {
				B = (int)(selectedIndex*2.55f);
				factorAndDisplay();
			}
		};
		sB.getElementMaterial().setColor("Color", new ColorRGBA(0.0f, 0.0f, 1.0f, 1.0f));
		addChild(sB);
		
		Label lB = new Label(screen, UID + ":lB", new Vector2f(lX, lY), new Vector2f(30, 15));
		lB.setFontSize(18);
		lB.setTextVAlign(BitmapFont.VAlign.Center);
		lB.setText("  B  ");
		addChild(lB);
		
		lY += yInc;
		sY += yInc;
		
		sH = new Slider(screen, UID + ":sH", new Vector2f(sX, sY), new Vector2f(150,15), Vector4f.ZERO, null, Slider.Orientation.HORIZONTAL, true) {
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
		
		Label lH = new Label(screen, UID + ":lH", new Vector2f(lX, lY), new Vector2f(30, 15));
		lH.setFontSize(18);
		lH.setTextVAlign(BitmapFont.VAlign.Center);
		lH.setText("  H  ");
		addChild(lH);
		
		lY += yInc;
		sY += yInc;
		
		sS = new Slider(screen, UID + ":sS", new Vector2f(sX, sY), new Vector2f(150,15), Vector4f.ZERO, screen.getStyle("ColorWheel").getString("colorSImg"), Slider.Orientation.HORIZONTAL, true) {
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
		
		Label lS = new Label(screen, UID + ":lS", new Vector2f(lX, lY), new Vector2f(30, 15));
		lS.setFontSize(18);
		lS.setTextVAlign(BitmapFont.VAlign.Center);
		lS.setText("  S  ");
		addChild(lS);
		
		lY += yInc;
		sY += yInc;
		
		sL = new Slider(screen, UID + ":sL", new Vector2f(sX, sY), new Vector2f(150,15), Vector4f.ZERO, screen.getStyle("ColorWheel").getString("colorLImg"), Slider.Orientation.HORIZONTAL, true) {
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
		
		Label lL = new Label(screen, UID + ":lL", new Vector2f(lX, lY), new Vector2f(30, 15));
		lL.setFontSize(18);
		lL.setTextVAlign(BitmapFont.VAlign.Center);
		lL.setText("  L  ");
		addChild(lL);
		
		lY += yInc;
		sY += yInc;
		
		sA = new Slider(screen, UID + ":sA", new Vector2f(sX, sY), new Vector2f(150,15), Vector4f.ZERO, screen.getStyle("ColorWheel").getString("colorLImg"), Slider.Orientation.HORIZONTAL, true) {
			@Override
			public void onChange(int selectedIndex, Object value) {
				A = selectedIndex;
				alpha = A*0.01f;
				finalAlpha = alpha;
				factorAndDisplay();
			}
		};
		sA.getElementMaterial().setColor("Color", new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
		sA.getElementMaterial().setBoolean("VertexColor", true);
		sA.getModel().setGradientFillVertical(new ColorRGBA(finalRed, finalGreen, finalBlue, 0.0f), new ColorRGBA(finalRed, finalGreen, finalBlue, 1.0f));
		addChild(sA);
		
		Label lA = new Label(screen, UID + ":lA", new Vector2f(lX, lY), new Vector2f(30, 15));
		lA.setFontSize(18);
		lA.setTextVAlign(BitmapFont.VAlign.Center);
		lA.setText("  A  ");
		addChild(lA);
		
		Label lHex = new Label(screen, UID + ":lHex", new Vector2f(sX, hexY-5), new Vector2f(60, 15));
		lHex.setFontSize(18);
		lHex.setTextVAlign(BitmapFont.VAlign.Center);
		lHex.setText("HEX: #");
		addChild(lHex);
		
		bFinish = new ButtonAdapter(screen, UID + ":bFiniah", new Vector2f(getWidth()-110, getHeight()-40)) {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean isToggled) {
				onComplete(new ColorRGBA(finalRed, finalGreen, finalBlue, finalAlpha));
				hideWindow();
			}
		};
		bFinish.setText("Done");
		addChild(bFinish);
		
		sR.setSelectedIndexWithCallback(100);
		sH.setSelectedIndexWithCallback(100);
		sS.setSelectedIndexWithCallback(100);
		sL.setSelectedIndexWithCallback(100);
		sA.setSelectedIndexWithCallback(100);
		
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
	
	private float average() {
		red = (float)R/255f;
		green = (float)G/255f;
		blue = (float)B/255f;
		float sum = red+green+blue;
		return sum/3;
	}
	private float applySaturation(float c) {
		float mid = average();
		float diff = FastMath.abs(c-mid);
		float ret = c;
		if (c < mid) {
			ret = mid-(diff*finalSaturation);
		} else {
			ret = mid+(diff*finalSaturation);
		}
		return ret;
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
		String strA = String.valueOf(finalAlpha);
		if (strA.length() > 5) strA = strA.substring(0,5);
		tfA.setText(strA);
		String hex = String.format("%02x%02x%02x", (int)(finalRed*255), (int)(finalGreen*255), (int)(finalBlue*255));
		tfHex.setText(hex);
		sR.setSelectedIndex((int)((int)(red*100)));
		sG.setSelectedIndex((int)((int)(green*100)));
		sB.setSelectedIndex((int)((int)(blue*100)));
		colorSwatch.getElementMaterial().setColor("Color", new ColorRGBA(finalRed, finalGreen, finalBlue, finalAlpha));
		sH.getModel().setGradientFillVertical(ColorRGBA.White, new ColorRGBA(red, green, blue, 1.0f));
		float av = average();
		sS.getModel().setGradientFillVertical(new ColorRGBA(av, av, av, 1.0f), new ColorRGBA(red, green, blue, 1.0f));
		sL.getModel().setGradientFillVertical(ColorRGBA.Black, new ColorRGBA(red, green, blue, 1.0f));
		sA.getModel().setGradientFillVertical(new ColorRGBA(finalRed, finalGreen, finalBlue, 0.0f), new ColorRGBA(finalRed, finalGreen, finalBlue, 1.0f));
	}
	
	public void setColor(ColorRGBA color) {
		R = (int)(color.r*255);
		if (R < 0) R = 0;
		else if (R > 255) R = 255;
		G = (int)(color.g*255);
		if (G < 0) G = 0;
		else if (G > 255) G = 255;
		B = (int)(color.b*255);
		if (B < 0) B = 0;
		else if (B > 255) B = 255;
		A = (int)(color.a*100);
		if (A < 0) A = 0;
		else if (A > 100) A = 100;
		sA.setSelectedIndex(A);
		factorAndDisplay();
	}
	
	public void setColor(float red, float green, float blue) {
		if (red < 0) red = 0;
		else if (red > 1) red = 1;
		R = (int)(red*255);
		if (green < 0) green = 0;
		else if (green > 1) green = 1;
		G = (int)(green*255);
		if (blue < 0) blue = 0;
		else if (blue > 1) blue = 1;
		B = (int)(blue*255);
		factorAndDisplay();
	}
	
	public void setColor(float red, float green, float blue, float alpha) {
		if (red < 0) red = 0;
		else if (red > 1) red = 1;
		R = (int)(red*255);
		if (green < 0) green = 0;
		else if (green > 1) green = 1;
		G = (int)(green*255);
		if (blue < 0) blue = 0;
		else if (blue > 1) blue = 1;
		B = (int)(blue*255);
		if (alpha < 0) alpha = 0;
		else if (alpha > 1) alpha = 1;
		A = (int)(alpha*100);
		sA.setSelectedIndex(A);
		factorAndDisplay();
	}
	
	public void setColor(int red, int green, int blue) {
		if (red < 0) red = 0;
		else if (red > 255) red = 255;
		R = red;
		if (green < 0) green = 0;
		else if (green > 255) green = 255;
		G = green;
		if (blue < 0) blue = 0;
		else if (blue > 255) blue = 255;
		B = blue;
		factorAndDisplay();
	}
	
	public void setRed(int red) {
		if (red < 0) red = 0;
		else if (red > 255) red = 255;
		R = red;
		factorAndDisplay();
	}
	
	public void setRed(float red) {
		if (red < 0) red = 0;
		else if (red > 1) red = 1;
		R = (int)(red*255);
		factorAndDisplay();
	}
	
	public void setGreen(int green) {
		if (green < 0) green = 0;
		else if (green > 255) green = 255;
		G = green;
		factorAndDisplay();
	}
	
	public void setGreen(float green) {
		if (green < 0) green = 0;
		else if (green > 1) green = 1;
		G = (int)(green*255);
		factorAndDisplay();
	}
	
	public void setBlue(int blue) {
		if (blue < 0) blue = 0;
		else if (blue > 255) blue = 255;
		B = blue;
		factorAndDisplay();
	}
	
	public void setBlue(float blue) {
		if (blue < 0) blue = 0;
		else if (blue > 1) blue = 1;
		B = (int)(blue*255);
		factorAndDisplay();
	}
	
	public void setAlpha(float alpha) {
		if (alpha < 0) alpha = 0;
		else if (alpha > 1) alpha = 1;
		A = (int)(alpha*100);
		sA.setSelectedIndex(A);
		factorAndDisplay();
	}
	
	public void setHue(float hue) {
		if (hue < 0) hue = 0;
		else if (hue > 1) hue = 1;
		H = (int)(hue*100);
		sH.setSelectedIndex(H);
		factorAndDisplay();
	}
	
	public void setSaturation(float saturation) {
		if (saturation < 0) saturation = 0;
		else if (saturation > 1) saturation = 1;
		S = (int)(saturation*100);
		sS.setSelectedIndex(S);
		factorAndDisplay();
	}
	
	public void setLight(float light) {
		if (light < 0) light = 0;
		else if (light > 1) light = 1;
		L = (int)(light*100);
		sL.setSelectedIndex(L);
		factorAndDisplay();
	}
	
	public ColorRGBA getColor() {
		return new ColorRGBA(finalRed, finalGreen, finalBlue, finalAlpha);
	}
	
	public float getRed() {
		return finalRed;
	}
	
	public float getGreen() {
		return finalGreen;
	}
	
	public float getBlue() {
		return finalGreen;
	}
	
	public float getAlpha() {
		return finalAlpha;
	}
	
	public float getHue() {
		return finalHue;
	}
	
	public float getSaturation() {
		return finalSaturation;
	}
	
	public float getLight() {
		return finalLight;
	}
	
	public abstract void onComplete(ColorRGBA color);
}
