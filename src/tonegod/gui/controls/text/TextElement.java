/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.text;

import com.jme3.font.BitmapFont.Align;
import com.jme3.font.BitmapFont.VAlign;
import com.jme3.font.LineWrapMode;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.texture.Texture;
import tonegod.gui.core.Element;
import tonegod.gui.core.ElementManager;
import tonegod.gui.core.utils.UIDUtil;
import tonegod.gui.framework.animation.MoveByAction;
import tonegod.gui.framework.animation.RotateByAction;
import tonegod.gui.framework.animation.ScaleByAction;
import tonegod.gui.framework.core.AnimText;

/**
 *
 * @author t0neg0d
 */
public class TextElement extends Element implements Control {
	AnimText animText;
	
//	LineWrapMode wrapMode = LineWrapMode.NoWrap;
//	VAlign vAlign = VAlign.Top;
//	Align hAlign = Align.Left;
	
	
	int qdIndex = 0;
	ScaleByAction sRoll;
	RotateByAction rRoll;
	MoveByAction mRoll;
	float rollTime = .025f;
	float rotTime = .125f;
	float rollCounter = 0;
	
	/**
	 * Creates a new instance of the TextElement control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param position A Vector2f containing the x/y position of the Element
	 */
	public TextElement(ElementManager screen, Vector2f position) {
		this(screen, UIDUtil.getUID(), position,
			screen.getStyle("Label").getVector2f("defaultSize"),
			Vector4f.ZERO,
			null
		);
	}
	
	/**
	 * Creates a new instance of the TextElement control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 */
	public TextElement(ElementManager screen, Vector2f position, Vector2f dimensions) {
		this(screen, UIDUtil.getUID(), position, dimensions,
			Vector4f.ZERO,
			null
		);
	}
	
	/**
	 * Creates a new instance of the TextElement control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 * @param resizeBorders A Vector4f containg the border information used when resizing the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg The default image to use for the Slider's track
	 */
	public TextElement(ElementManager screen, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		this(screen, UIDUtil.getUID(), position, dimensions,resizeBorders,defaultImg);
	}
	
	/**
	 * Creates a new instance of the TextElement control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 */
	public TextElement(ElementManager screen, String UID, Vector2f position) {
		this(screen, UID, position,
			screen.getStyle("Label").getVector2f("defaultSize"),
			Vector4f.ZERO,
			null
		);
	}
	
	/**
	 * Creates a new instance of the TextElement control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 */
	public TextElement(ElementManager screen, String UID, Vector2f position, Vector2f dimensions) {
		this(screen, UID, position, dimensions,
			Vector4f.ZERO,
			null
		);
	}
	
	/**
	 * Creates a new instance of the TextElement control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 * @param resizeBorders A Vector4f containg the border information used when resizing the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg The default image to use for the Slider's track
	 */
	public TextElement(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg);
		this.setIsResizable(false);
		this.setIsMovable(false);
		this.setIgnoreMouse(true);
		this.setClippingLayer(this);
		
		Material mat = new Material(app.getAssetManager(), "tonegod/gui/shaders/Unshaded.j3md");
		mat.setTexture("ColorMap", (Texture)font.getPage(0).getParam("ColorMap").getValue());
		mat.setColor("Color", new ColorRGBA(1,1,1,1));
		mat.setVector2("OffsetAlphaTexCoord", new Vector2f(0,0));
		mat.setFloat("GlobalAlpha", screen.getGlobalAlpha());
		mat.setBoolean("VertexColor", true);
		
		mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
		mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Back);
		
		animText = new AnimText(
			screen.getApplication().getAssetManager(),
			getFont()
		);
		animText.setBounds(dimensions);
		animText.setScale(1,1);
		animText.setOrigin(0,0);
		attachChild(animText);
	}
	
	@Override
	public void setText(String text) {
	//	this.text = text;
		animText.setText(text);
		animText.setPositionY(getHeight()-animText.getLineHeight());
		setTextWrap(textWrap);
	}
	
	@Override
	public void controlMoveHook() {
		animText.getMaterial().setVector4("Clipping", getClippingBounds());
	//	animText.getMaterial().setBoolean("UseClipping", true);
	}
	
	@Override
	public void controlResizeHook() {
		animText.setPositionY(getHeight()-animText.getLineHeight());
		animText.setBounds(getDimensions());
		switch (textWrap) {
			case Character:
				animText.wrapTextToCharacter(getWidth());
				break;
			case Word:
				animText.wrapTextToWord(getWidth());
				break;
		}
		setTextAlign(textAlign);
		setTextVAlign(textVAlign);
		animText.getMaterial().setVector4("Clipping", getClippingBounds());
	//	animText.getMaterial().setBoolean("UseClipping", true);
		animText.update(0);
	}
	
	public void setLineWrapMode(LineWrapMode textWrap) {
		setTextWrap(textWrap);
	}
	
	@Override
	public void setTextWrap(LineWrapMode textWrap) {
		this.textWrap = textWrap;
		animText.setTextWrap(textWrap);
		switch (textWrap) {
			case Character:
				animText.wrapTextToCharacter(getWidth());
				break;
			case Word:
				animText.wrapTextToWord(getWidth());
				break;
			case NoWrap:
				animText.wrapTextNoWrap();
				break;
			case Clip:
				animText.wrapTextNoWrap();
				setUseTextClipping(true);
				break;
		}
		animText.update(0);
	}
	
	public void setUseTextClipping(boolean clip) {
		animText.getMaterial().setVector4("Clipping", getClippingBounds());
		animText.getMaterial().setBoolean("UseClipping", clip);
	}
	
	public void setSize(float size) {
		setFontSize(size);
	}
	
	@Override
	public void setFontSize(float fontSize) {
		animText.setFontSize(fontSize);
		setTextWrap(textWrap);
	}
	
	public void setColor(ColorRGBA color) {
		setFontColor(color);
	}
	
	@Override
	public void setFontColor(ColorRGBA fontColor) {
		animText.setFontColor(fontColor);
		setTextWrap(textWrap);
	}
	
	public void setSubStringColor(String subString, ColorRGBA color) {
		this.setSubStringColor(subString, color, false, 1);
	}
	
	public void setSubStringColor(String subString, ColorRGBA color, boolean allInstances) {
		this.setSubStringColor(subString, color, allInstances, 1);
	}
	
	public void setSubStringColor(String subString, ColorRGBA color, boolean allInstances, int... whichInstances) {
		animText.setSubStringColor(subString, color, allInstances, whichInstances);
	}
	
	public void setAlpha(float alpha) {
		animText.setAlpha(alpha);
		animText.update(0);
	}
	
	public void setAlignment(Align textAlign) {
		setTextAlign(textAlign);
	}
	
	@Override
	public void setTextAlign(Align textAlign) {
		this.textAlign = textAlign;
		animText.setTextAlign(textAlign);
	//	animText.alignToBoundsH(textAlign);
	}
	
	public void setVerticalAlignment(VAlign textVAlign) {
		setTextVAlign(textVAlign);
	}
	
	@Override
	public void setTextVAlign(VAlign textVAlign) {
		this.textVAlign = textVAlign;
		animText.setTextVAlign(textVAlign);
	}
	
	public void effect(boolean active) {
		if (active) {
			this.addControl(this);
		} else {
			this.removeControl(TextElement.class);
		}
	}
	
	@Override
	public void update(float tpf) {
		rollText(tpf);
		animText.update(tpf);
	}
	
	public void resetRoll() {
		rollCounter = 0;
		qdIndex = 0;
	}
	
	private void rollText(float tpf) {
		rollCounter += tpf;
		if (rollCounter >= rollTime) {
			if (animText.getQuadDataAt(qdIndex).actions.isEmpty()) {
				float mX = animText.getWidth()/2;
				mX *= 3;
				mX = -mX;
				float mY = 0;
				sRoll = new ScaleByAction();
				sRoll.setAmount(1.5f, 1.65f);
				sRoll.setDuration(.5f);
				sRoll.setAutoReverse(true);
				animText.getQuadDataAt(qdIndex).addAction(sRoll);
				mRoll = new MoveByAction();
				mRoll.setAmount(mX,mY);
				mRoll.setDuration(.5f);
				mRoll.setAutoReverse(true);
				animText.getQuadDataAt(qdIndex).addAction(mRoll);
				qdIndex++;
				if (qdIndex == animText.length())
					qdIndex = 0;
				rollCounter = 0;
			}
		}
	}
	
	public void updateAnimText(float tpf) {
		animText.update(0);
	}
	
	@Override
	public Control cloneForSpatial(Spatial spatial) {
		return this;
	}

	@Override
	public void setSpatial(Spatial spatial) {  }

	@Override
	public void render(RenderManager rm, ViewPort vp) {  }
}
