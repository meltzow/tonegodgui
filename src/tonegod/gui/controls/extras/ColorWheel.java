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
import java.awt.Color;
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
	Element blackToColor, colorToWhite;
	
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
				setHFromWheel();
				sH.setSelectedIndex(100-H);
				getRGBFromHue();
				factor();
				sR.setSelectedIndex((int)(red*100));
				sG.setSelectedIndex((int)(green*100));
				sB.setSelectedIndex((int)(blue*100));
				RGBToHSL();
				sS.setSelectedIndex(S);
				sL.setSelectedIndex(L);
				displayFactoredColor();
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
				RGBToHSL();
				sH.setSelectedIndex(100-H);
				int hIndex = H+51;
				if (hIndex > 100) hIndex -= 101;
				primarySelector.setSelectedIndex(100-hIndex);
				sS.setSelectedIndex(S);
				sL.setSelectedIndex(L);
				factor();
				displayFactoredColor();
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
				RGBToHSL();
				sH.setSelectedIndex(100-H);
				int hIndex = H+51;
				if (hIndex > 100) hIndex -= 101;
				primarySelector.setSelectedIndex(100-hIndex);
				sS.setSelectedIndex(S);
				sL.setSelectedIndex(L);
				factor();
				displayFactoredColor();
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
				RGBToHSL();
				sH.setSelectedIndex(100-H);
				int hIndex = H+51;
				if (hIndex > 100) hIndex -= 101;
				primarySelector.setSelectedIndex(100-hIndex);
				sS.setSelectedIndex(S);
				sL.setSelectedIndex(L);
				factor();
				displayFactoredColor();
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
		
		addHueSliderBG(sX, sY, 150, 15);
		sH = new Slider(screen, UID + ":sH", new Vector2f(sX, sY), new Vector2f(150,15), Vector4f.ZERO, null, Slider.Orientation.HORIZONTAL, true) {
			@Override
			public void onChange(int selectedIndex, Object value) {
				setHFromSlider();
				int hIndex = H+51;
				if (hIndex > 100) hIndex -= 101;
				primarySelector.setSelectedIndex(100-hIndex);
				getRGBFromHue();
				factor();
				sR.setSelectedIndex((int)(red*100));
				sG.setSelectedIndex((int)(green*100));
				sB.setSelectedIndex((int)(blue*100));
				sS.setSelectedIndex(S);
				sL.setSelectedIndex(L);
				displayFactoredColor();
			}
		};
	//	sH.getElementMaterial().setColor("Color", new ColorRGBA(1.0f, 1.0f, 1.0f, 0.0f));
	//	sH.getElementMaterial().setBoolean("VertexColor", true);
	//	sH.getModel().setGradientFillVertical(ColorRGBA.White, new ColorRGBA(red, green, blue, 1.0f));
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
				HSLToRGB();
				factor();
				sR.setSelectedIndex((int)(red*100));
				sG.setSelectedIndex((int)(green*100));
				sB.setSelectedIndex((int)(blue*100));
				displayFactoredColor();
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
		
	//	addLightSliderBG(sX, sY, 150, 15);
		sL = new Slider(screen, UID + ":sL", new Vector2f(sX, sY), new Vector2f(150,15), Vector4f.ZERO, screen.getStyle("ColorWheel").getString("colorLImg"), Slider.Orientation.HORIZONTAL, true) {
			@Override
			public void onChange(int selectedIndex, Object value) {
				L = selectedIndex;
				HSLToRGB();
				factor();
				sR.setSelectedIndex((int)(red*100));
				sG.setSelectedIndex((int)(green*100));
				sB.setSelectedIndex((int)(blue*100));
				displayFactoredColor();
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
				factorAlpha();
				displayFactoredColor();
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
		
		sR.setSelectedIndex(100);
		sH.setSelectedIndex(100);
		sS.setSelectedIndex(100);
		sL.setSelectedIndex(100);
		sA.setSelectedIndex(100);
		
	}
	
	private void addHueSliderBG(float x, float y ,float w, float h) {
		Element bg1 = new Element(screen, getUID()+":HSBG1", new Vector2f(x,y), new Vector2f(w/6,h), Vector4f.ZERO,null);
		bg1.getModel().setGradientFillVertical(new ColorRGBA(1,0,0,1), new ColorRGBA(1,0,1,1));
		bg1.getElementMaterial().setColor("Color", ColorRGBA.White);
		bg1.getElementMaterial().setBoolean("VertexColor", true);
		addChild(bg1);
		
		Element bg2 = new Element(screen, ":HSBG2", new Vector2f(x+(w/6),y), new Vector2f(w/6,h), Vector4f.ZERO,null);
		bg2.getModel().setGradientFillVertical(new ColorRGBA(1,0,1,1), new ColorRGBA(0,0,1,1));
		bg2.getElementMaterial().setColor("Color", ColorRGBA.White);
		bg2.getElementMaterial().setBoolean("VertexColor", true);
		addChild(bg2);
		
		Element bg3 = new Element(screen, ":HSBG3", new Vector2f(x+(w/6*2),y), new Vector2f(w/6,h), Vector4f.ZERO,null);
		bg3.getModel().setGradientFillVertical(new ColorRGBA(0,0,1,1), new ColorRGBA(0,1,1,1));
		bg3.getElementMaterial().setColor("Color", ColorRGBA.White);
		bg3.getElementMaterial().setBoolean("VertexColor", true);
		addChild(bg3);
		
		Element bg4 = new Element(screen, ":HSBG4", new Vector2f(x+(w/6*3),y), new Vector2f(w/6,h), Vector4f.ZERO,null);
		bg4.getModel().setGradientFillVertical(new ColorRGBA(0,1,1,1), new ColorRGBA(0,1,0,1));
		bg4.getElementMaterial().setColor("Color", ColorRGBA.White);
		bg4.getElementMaterial().setBoolean("VertexColor", true);
		addChild(bg4);
		
		Element bg5 = new Element(screen, ":HSBG5", new Vector2f(x+(w/6*4),y), new Vector2f(w/6,h), Vector4f.ZERO,null);
		bg5.getModel().setGradientFillVertical(new ColorRGBA(0,1,0,1), new ColorRGBA(1,1,0,1));
		bg5.getElementMaterial().setColor("Color", ColorRGBA.White);
		bg5.getElementMaterial().setBoolean("VertexColor", true);
		addChild(bg5);
		
		Element bg6 = new Element(screen, ":HSBG6", new Vector2f(x+(w/6*5),y), new Vector2f(w/6,h), Vector4f.ZERO,null);
		bg6.getModel().setGradientFillVertical(new ColorRGBA(1,1,0,1), new ColorRGBA(1,0,0,1));
		bg6.getElementMaterial().setColor("Color", ColorRGBA.White);
		bg6.getElementMaterial().setBoolean("VertexColor", true);
		addChild(bg6);
	}
	
	private void addLightSliderBG(float x, float y ,float w, float h) {
		blackToColor = new Element(screen, getUID()+":blackToColor", new Vector2f(x,y), new Vector2f(w/2,h), Vector4f.ZERO, null);
		blackToColor.getModel().setGradientFillVertical(new ColorRGBA(0,0,0,1), new ColorRGBA(1,0,0,1));
		blackToColor.getElementMaterial().setColor("Color", ColorRGBA.White);
		blackToColor.getElementMaterial().setBoolean("VertexColor", true);
		addChild(blackToColor);
		
		colorToWhite = new Element(screen, ":colorToWhite", new Vector2f(x+(w/2),y), new Vector2f(w/2,h), Vector4f.ZERO, null);
		colorToWhite.getModel().setGradientFillVertical(new ColorRGBA(1,0,0,1), new ColorRGBA(1,1,1,1));
		colorToWhite.getElementMaterial().setColor("Color", ColorRGBA.White);
		colorToWhite.getElementMaterial().setBoolean("VertexColor", true);
		addChild(colorToWhite);
	}
	
	private void setHFromWheel() {
		int hIndex = primarySelector.getSelectedIndex();
		hIndex -= 51;
		if (hIndex < 0) hIndex += 101;
		H = 100-hIndex;
	}
	
	private void setHFromSlider() {
		H = 100-sH.getSelectedIndex();
	}
	
	private void factorRed() {
		red = (float)R/255f;
		finalRed = red;
	}
	
	private void factorGreen() {
		green = (float)G/255f;
		finalGreen = green;
	}
	
	private void factorBlue() {
		blue = (float)B/255f;
		finalBlue = blue;
	}
	
	private void RGBToHSL() {
		float[] hsv = new float[3];
		hsv = Color.RGBtoHSB(R,G,B,hsv);
		H = (int)(hsv[0]*100);
	//	System.out.println(hsv[0]);
		S = (int)(hsv[1]*100);
		L = (int)(hsv[2]*100);
	}
	
	private void HSLToRGB() {
		int rgb = Color.HSBtoRGB(H*0.01f, S*0.01f, L*0.01f);
		R = (rgb >> 16) & 0xff;
		G = (rgb >> 8) & 0xff;
		B = (rgb >> 0) & 0xff;
	}
	
	private void factorHue() {
		hue = H*0.01f;
		finalHue = hue;
	}
	
	private void getRGBFromHue() {
		HSLToRGB();
	//	int rgb = Color.HSBtoRGB(H*0.01f, 1f, 1f);
	//	R = (rgb >> 16) & 0xff;
	//	G = (rgb >> 8) & 0xff;
	//	B = (rgb >> 0) & 0xff;
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
	
	private void factorLight() {
		light = L*0.01f;
		finalLight = light;
	}
	
	private void factorAlpha() {
		alpha = A*0.01f;
		finalAlpha = alpha;
	}
	
	private void factor() {
		factorRed();
		factorGreen();
		factorBlue();
		factorHue();
		factorSaturation();
		factorLight();
		factorAlpha();
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
		colorSwatch.getElementMaterial().setColor("Color", new ColorRGBA(finalRed, finalGreen, finalBlue, finalAlpha));
		float av = average();
		sS.getModel().setGradientFillVertical(new ColorRGBA(av, av, av, 1.0f), new ColorRGBA(red, green, blue, 1.0f));
		sL.getModel().setGradientFillVertical(ColorRGBA.Black, new ColorRGBA(red, green, blue, 1.0f));
	//	colorToWhite.getModel().setGradientFillVertical(new ColorRGBA(red, green, blue, 1.0f), ColorRGBA.White);
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
	//	factorAndDisplay();
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
	//	factorAndDisplay();
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
	//	factorAndDisplay();
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
	//	factorAndDisplay();
	}
	
	public void setRed(int red) {
		if (red < 0) red = 0;
		else if (red > 255) red = 255;
		R = red;
	//	factorAndDisplay();
	}
	
	public void setRed(float red) {
		if (red < 0) red = 0;
		else if (red > 1) red = 1;
		R = (int)(red*255);
	//	factorAndDisplay();
	}
	
	public void setGreen(int green) {
		if (green < 0) green = 0;
		else if (green > 255) green = 255;
		G = green;
	//	factorAndDisplay();
	}
	
	public void setGreen(float green) {
		if (green < 0) green = 0;
		else if (green > 1) green = 1;
		G = (int)(green*255);
	//	factorAndDisplay();
	}
	
	public void setBlue(int blue) {
		if (blue < 0) blue = 0;
		else if (blue > 255) blue = 255;
		B = blue;
	//	factorAndDisplay();
	}
	
	public void setBlue(float blue) {
		if (blue < 0) blue = 0;
		else if (blue > 1) blue = 1;
		B = (int)(blue*255);
	//	factorAndDisplay();
	}
	
	public void setAlpha(float alpha) {
		if (alpha < 0) alpha = 0;
		else if (alpha > 1) alpha = 1;
		A = (int)(alpha*100);
		sA.setSelectedIndex(A);
	//	factorAndDisplay();
	}
	
	public void setHue(float hue) {
		if (hue < 0) hue = 0;
		else if (hue > 1) hue = 1;
		H = (int)(hue*100);
		sH.setSelectedIndex(H);
	//	factorAndDisplay();
	}
	
	public void setSaturation(float saturation) {
		if (saturation < 0) saturation = 0;
		else if (saturation > 1) saturation = 1;
		S = (int)(saturation*100);
		sS.setSelectedIndex(S);
	//	factorAndDisplay();
	}
	
	public void setLight(float light) {
		if (light < 0) light = 0;
		else if (light > 1) light = 1;
		L = (int)(light*100);
		sL.setSelectedIndex(L);
	//	factorAndDisplay();
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
