/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.framework.core;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.font.BitmapCharacter;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapFont.Align;
import static com.jme3.font.BitmapFont.Align.Center;
import static com.jme3.font.BitmapFont.Align.Left;
import static com.jme3.font.BitmapFont.Align.Right;
import com.jme3.font.BitmapFont.VAlign;
import com.jme3.font.LineWrapMode;
import static com.jme3.font.LineWrapMode.Character;
import static com.jme3.font.LineWrapMode.Clip;
import static com.jme3.font.LineWrapMode.NoWrap;
import static com.jme3.font.LineWrapMode.Word;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.texture.Texture;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author t0neg0d
 */
public class AnimText extends AnimElement {
	private BitmapFont font;
	private String text;
	private int imgHeight;
	private boolean fadeIn = false;
	private boolean fadeOut = false;
	private QuadData[] letters;
	private QuadData qd;
	private TextureRegion tr;
	private StringTokenizer st, st2;
	private List<String> wordBreak = new ArrayList();
	private String[] words;
	private Vector2f bounds = new Vector2f();
	private Character c;
	
	private LineWrapMode textWrap = LineWrapMode.Clip;
	private Align textAlign = Align.Left;
	private VAlign textVAlign = VAlign.Top;
	
	private float	fadeDuration = 0,
					fadeCounter = 0,
					alpha = 1,
					lineWidth = 0,
					lineHeight = 0;
	
	private boolean useDropShadow = false;
	private Vector2f dsOffset = new Vector2f(0,-3);
	private ColorRGBA fontColor = new ColorRGBA(1f,1f,1f,1f);
	private ColorRGBA dsColor = new ColorRGBA(0.3f,0.3f,0.3f,0.5f);
	
	// Temp vars
	private Vector2f align = new Vector2f();
	private Vector2f pos = new Vector2f();
	
	char[] nl;
	
	public AnimText(AssetManager assetManager, BitmapFont font) {
		this(assetManager, font, (Texture)font.getPage(0).getParam("ColorMap").getValue());
	}
	
	public AnimText(AssetManager assetManager, BitmapFont font, Texture bfTexture) {
		super(assetManager);
		this.font = font;
		this.text = "";
		this.setScale(1,1);
		this.setPosition(0,0);
		this.setOrigin(0,0);
		nl = System.getProperty("line.separator").toCharArray();
		
		setTexture(bfTexture);
		imgHeight = (int)bfTexture.getImage().getHeight();
		
		setText(text);
		initialize();
		
		setBounds(getWidth(),getHeight());
	}
	
	public void setUseDropShadow(boolean useDropShadow) {
		this.useDropShadow = useDropShadow;
	}
	
	public void setDropShadowOffset(Vector2f dsOffset) {
		this.dsOffset.set(dsOffset);
	}
	
	public void setDropShadowColor(ColorRGBA color) {
		boolean set = true;
		for (QuadData quad : quads.values()) {
			if (useDropShadow) {
				if (set) quad.setColor(color);
				set = !set;
			}
		}
		this.dsColor.set(color);
	}
	
	public final void setText(String text) {
		this.uvs.clear();
		this.quads.clear();
		
		lineWidth = 0;
		
		for (int i = 0; i < text.length(); i++) {
			c = text.charAt(i);
			BitmapCharacter bc = font.getCharSet().getCharacter(c.charValue());
				if (bc != null) {
					if (bc.getHeight() > lineHeight)
						lineHeight = bc.getYOffset();
				
					tr = addTextureRegion(String.valueOf(i), bc.getX(), imgHeight-bc.getY()-bc.getHeight(), bc.getWidth(), bc.getHeight());
					tr.flip(false, true);
					align.set(bc.getWidth()/2, bc.getHeight()/2);
				} else {
					tr = addTextureRegion(String.valueOf(i), 0, 0, 0, 0);
				}
				if (useDropShadow) {
					if (bc != null) pos.set(lineWidth+dsOffset.x,font.getCharSet().getBase()-bc.getHeight()-bc.getYOffset()+dsOffset.y);
					else {
						pos.set(0,0);
						align.set(0,0);
					}
					qd = addQuad(String.valueOf(i)+"ds", String.valueOf(i), pos, align);
					qd.setColor(dsColor);
					qd.userIndex = i;
				}
				if (bc != null) pos.set(lineWidth,font.getCharSet().getBase()-bc.getHeight()-bc.getYOffset());
				else {
					pos.set(0,0);
					align.set(0,0);
				}
				qd = addQuad(String.valueOf(i), String.valueOf(i), pos, align);
				qd.setColor(fontColor);
				qd.userIndex = i;
				if (bc != null) {
					lineWidth += bc.getWidth();
					lineWidth += bc.getXOffset();
				}
				if (text.charAt(i) == ' ')
					lineWidth += font.getCharSet().getCharacter('i').getWidth();
		}
		
		setOrigin(getWidth()/2,getHeight()/2);
		mesh.initialize();
		mesh.update(0);
		
		mesh.updateBound();
		
		letters = quads.values().toArray(new QuadData[0]);
		
		st = new StringTokenizer(text, " ");
		st.countTokens();
		words = text.split(" ");
		/*
		for (String s : words) {
			String[] br = s.split("\n");
			if (br.length == 2) {
				wordBreak.add(br[0]);
				wordBreak.add("\n");
				wordBreak.add(br[1]);
			} else {
				wordBreak.add(br[0]);
			}
		}
		words = wordBreak.toArray(new String[0]);
		* */
		this.text = text;
	}
	
	public void setMaxAlpha(float alpha) {
		this.alpha = alpha;
	}
	
	public void setAlpha(float alpha) {
		for (QuadData quad : quads.values())
			quad.setColorA(alpha);
	}
	
	public void wrapTextNoWrap() {
		int i = 0;
		float x = 0, y = -(font.getCharSet().getBase()/2);
		float lnWidth = 0;
		BitmapCharacter bc;
		for (QuadData quad : letters) { //getQuads().values()) {
			c = text.charAt(i);
			if (c == ' ')
				bc = font.getCharSet().getCharacter('i');
			else
				bc = font.getCharSet().getCharacter(c);
			
			if (bc != null) {
				quad.setPositionX(x);
				quad.setPositionY(font.getCharSet().getBase()-bc.getHeight()-bc.getYOffset()+y);

				x += bc.getXAdvance();
				lnWidth += bc.getXAdvance();
			} else {
				quad.setPositionX(x);
				quad.setPositionY(font.getCharSet().getBase()+y);
			}
			i++;
		}
		switch (textAlign) {
			case Right:
				for (QuadData quad : letters) {
					quad.setPositionX(quad.getPositionX()-lnWidth);
				}
				break;
			case Center:
				for (QuadData quad : letters) {
					quad.setPositionX(quad.getPositionX()-(lnWidth/2));
				}
				break;
		}
		updateForAlign();
		setOrigin(getWidth()/2,getHeight()/2);
		mesh.update(0);
		mesh.updateBound();
		alignToBoundsV();
	}
	public void wrapTextToCharacter(float width) {
		float scaled = width*getScale().x;
		float diff = scaled-width;
		width -= diff;
		int i = 0, lineIndex = 0;
		float x = 0, y = -(font.getCharSet().getBase()/2);
		BitmapCharacter bc;
		float lnWidth = 0;
		boolean newLine = false;
		
		for (QuadData quad : letters) { //getQuads().values()) {
			c = text.charAt(i);
			if (c == ' ')	bc = font.getCharSet().getCharacter('i');
			else			bc = font.getCharSet().getCharacter(c);
			
			quad.setPositionX(x);
			if (bc != null) {
				quad.setPositionY(font.getCharSet().getBase()-bc.getHeight()-bc.getYOffset()+y);
				x += bc.getXAdvance();
			} else {
				quad.setPositionY(font.getCharSet().getBase()+y);
				if (checkNewLine(c))
					newLine = true;
			}
			
			if (x > width || newLine) {
				y -= font.getCharSet().getBase();
				
				newLine = false;
				
				switch (textAlign) {
					case Right:
						for (int xi = lineIndex; xi < i; xi++) {
							letters[xi].setPositionX(letters[xi].getPositionX()-lnWidth);
						}
						break;
					case Center:
						for (int xi = lineIndex; xi < i; xi++) {
							letters[xi].setPositionX(letters[xi].getPositionX()-(lnWidth/2));
						}
						break;
				}
				x = 0;
				lnWidth = 0;
				lineIndex = i;
				quad.setPositionX(x);
				
				if (bc != null)	quad.setPositionY(font.getCharSet().getBase()-bc.getHeight()-bc.getYOffset()+y);
				else			quad.setPositionY(font.getCharSet().getBase()+y);
				
				if (text.charAt(i) == ' ')	x = 0;
				else {	if (bc != null)		x += bc.getXAdvance(); }
			}
			if (bc != null)	lnWidth += bc.getXAdvance();
			i++;
		}
		switch (textAlign) {
			case Right:
				for (int xi = lineIndex; xi < i; xi++) {
					letters[xi].setPositionX(letters[xi].getPositionX()-lnWidth);
				}
				break;
			case Center:
				for (int xi = lineIndex; xi < i; xi++) {
					letters[xi].setPositionX(letters[xi].getPositionX()-(lnWidth/2));
				}
				break;
		}
		updateForAlign();
		setOrigin(getWidth()/2,getHeight()/2);
		mesh.update(0);
		mesh.updateBound();
		alignToBoundsV();
	}
	public void wrapTextToWord(float width) {
		float scaled = width*getScale().x;
		float diff = scaled-width;
		width -= diff;
		float x, y = -(font.getCharSet().getBase()/2);
		float wordWidth, lnWidth = 0;
		int sIndex, lIndex = 0, lineIndex = 0;
		BitmapCharacter bc;
		boolean newLine = false;
		int newLineCount = 0;
		
		for (String word : words) {
			x = 0;
			wordWidth = 0;
			sIndex = lIndex;
			for (int l = 0; l < word.length(); l++) {
				c = word.charAt(l);
				bc = font.getCharSet().getCharacter(c);
				qd = letters[lIndex];
				qd.setPositionX(x);
				if (bc != null) {
					qd.setPositionY(font.getCharSet().getBase()-bc.getHeight()-bc.getYOffset()+y);
					x += bc.getXAdvance();
					wordWidth += bc.getXAdvance();
				} else {
					qd.setPositionY(font.getCharSet().getBase()+y);
					if (checkNewLine(c)) {
						newLine = true;
						newLineCount++;
					}
				}
				lIndex++;
			}
			
			if (lnWidth+wordWidth > width || newLine) {
				if (newLine)	y -= font.getCharSet().getBase()*newLineCount;
				else			y -= font.getCharSet().getBase();
				
				newLine = false;
				newLineCount = 0;
				
				switch (textAlign) {
					case Right:
						for (int xi = lineIndex; xi < sIndex; xi++) {
							letters[xi].setPositionX(letters[xi].getPositionX()-lnWidth);
						}
						break;
					case Center:
						for (int xi = lineIndex; xi < sIndex; xi++) {
							letters[xi].setPositionX(letters[xi].getPositionX()-(lnWidth/2));
						}
						break;
				}
				lnWidth = 0;
				lineIndex = sIndex;
				int cIndex = sIndex;
				for (int l = 0; l < word.length(); l++) {
					c = word.charAt(l);
					bc = font.getCharSet().getCharacter(c);
					qd = letters[cIndex];
					if (bc != null)	qd.setPositionY(font.getCharSet().getBase()-bc.getHeight()-bc.getYOffset()+y);
					else			qd.setPositionY(font.getCharSet().getBase()+y);
					cIndex++;
				}
				lnWidth += wordWidth;
			} else {
				int cIndex = sIndex;
				for (int l = 0; l < word.length(); l++) {
					c = word.charAt(l);
					bc = font.getCharSet().getCharacter(c);
					qd = letters[cIndex];
					qd.setPositionX(qd.getPositionX()+lnWidth);
					if (bc != null)	qd.setPositionY(font.getCharSet().getBase()-bc.getHeight()-bc.getYOffset()+y);
					else			qd.setPositionY(font.getCharSet().getBase()+y);
					cIndex++;
				}
				lnWidth += wordWidth;
			}
			
			if (lIndex < text.length()) {
				bc = font.getCharSet().getCharacter('i');
				c = text.charAt(lIndex);
				while (c == ' ' && lIndex < text.length()) {
					lnWidth += bc.getXAdvance();
					lIndex++;
					c = text.charAt(lIndex);
				}
			} else {
				switch (textAlign) {
					case Right:
						for (int xi = lineIndex; xi < text.length(); xi++) {
							letters[xi].setPositionX(letters[xi].getPositionX()-lnWidth);
						}
						break;
					case Center:
						for (int xi = lineIndex; xi < text.length(); xi++) {
							letters[xi].setPositionX(letters[xi].getPositionX()-(lnWidth/2));
						}
						break;
				}
			}
		}
		updateForAlign();
		setOrigin(getWidth()/2,getHeight()/2);
		mesh.update(0);
		mesh.updateBound();
		alignToBoundsV();
	}
	
	@Override
	public void animElementUpdate(float tpf) {
		if (fadeIn) {
			fadeCounter += tpf;
			float percent = fadeCounter/fadeDuration;
			if (percent >= 1) {
				percent = 1;
				for (QuadData quad : quads.values())
					quad.setColorA(percent*alpha);
				fadeCounter = 0;
				fadeIn = false;
			} else {
				for (QuadData quad : quads.values())
					quad.setColorA(percent*alpha);
			}
		} else if (fadeOut) {
			fadeCounter += tpf;
			float percent = 1-(fadeCounter/fadeDuration);
			if (percent <= 0) {
				percent = 0;
				for (QuadData quad : quads.values())
					quad.setColorA(percent*alpha);
				fadeCounter = 0;
				fadeOut = false;
			} else {
				for (QuadData quad : quads.values())
					quad.setColorA(percent*alpha);
			}
		}
	}
	
	public void setFontSize(float size) {
		float tempScale = size/font.getPreferredSize();
		setScale(
			tempScale,
			tempScale
		);
	}
	
	public float getLineWidth() {
		return lineWidth*getScaleX();
	}
	
	public float getLineHeight() {
		return lineHeight*getScaleY();
	}
	
	public void fadeTextIn(float duration) {
		fadeIn = true;
		fadeDuration = duration;
	}
	
	public void fadeTextOut(float duration) {
		fadeOut = true;
		fadeDuration = duration;
	}
	
	public boolean isAnimating() {
		return fadeIn || fadeOut;
	}
	
	public void setFontColor(ColorRGBA color) {
		boolean set = false;
		for (QuadData quad : quads.values()) {
			if (useDropShadow) {
				if (set) quad.setColor(color);
				set = !set;
			} else {
				quad.setColor(color);
			}
		}
		fontColor.set(color);
		alpha = color.a;
	}
	
	public QuadData getQuadDataAt(int index) {
		if (useDropShadow)
			return (QuadData)quads.values().toArray()[index*2+1];
		else
			return (QuadData)quads.values().toArray()[index];
	}
	
	public QuadData getDropShadowAt(int index) {
		if (useDropShadow)
			return (QuadData)quads.values().toArray()[index*2];
		else
			return null;
	}
	
	public int length() {
		return text.length();
	}
	
	public String getText() {
		return this.text;
	}
	
	public void setBounds(Vector2f dimensions) {
		this.bounds.set(dimensions);
	}
	
	public final void setBounds(float x, float y) {
		this.bounds.set(x,y);
	}
	
	public Vector2f getBounds() {
		return this.bounds;
	}
	
	public float getBoundsX() {
		return this.bounds.x;
	}
	
	public float getBoundsY() {
		return this.bounds.y;
	}
	
	private void alignToBoundsV() {
		float height = bounds.y;
		float innerHeight = ((BoundingBox)this.mesh.getBound()).getYExtent()*2;
		switch (textVAlign) {
			case Top:
				setPositionY(height-(lineHeight/2));
				break;
			case Center:
				switch (textWrap) {
					case NoWrap:
					case Clip:
						setPositionY(
							(height/2)+(lineHeight/2)
						);
						break;
					default:
						setPositionY(
							(height/2)+(innerHeight/2)
						);
						break;
				}
				
				break;
			case Bottom:
				setPositionY(innerHeight);
				break;
		}
		mesh.update(0);
		mesh.updateBound();
	}
	
	public void setTextWrap(LineWrapMode textWrap) {
		this.textWrap = textWrap;
	}
	
	private void updateForAlign() {
		switch (textAlign) {
			case Left:
				setPositionX(0);
				break;
			case Center:
				setPositionX(bounds.x/2);
				break;
			case Right:
				setPositionX(bounds.x);
				break;
		}
	}
	
	public void setTextAlign(Align textAlign) {
		this.textAlign = textAlign;
		switch (textWrap) {
			case Character:
				wrapTextToCharacter(bounds.x);
				break;
			case Word:
				wrapTextToWord(bounds.x);
				break;
			case NoWrap:
				wrapTextNoWrap();
				break;
			case Clip:
				wrapTextNoWrap();
				break;
		}
	}
	
	public void setTextVAlign(VAlign textVAlign) {
		this.textVAlign = textVAlign;
		alignToBoundsV();
	}
	
	public void setSubStringColor(String subString, ColorRGBA color, boolean allInstances, int... whichInstances) {
		String temp = text;
		int lastIndex = 0;
		int sIndex = 1;
		int count = 1;
		while (sIndex != -1) {
			sIndex = temp.indexOf(subString);
			if (sIndex != -1) {
				int eIndex = sIndex+subString.length();
				boolean valid = false;
				if (!allInstances) {
					for (int c : whichInstances) {
						if (c == count) {
							valid = true;
							break;
						}
					}
					if (valid) {
						for (int i = sIndex+lastIndex; i < eIndex+lastIndex; i++) {
							letters[i].setColor(color);
						}
					}
					count++;
				} else {
					for (int i = sIndex+lastIndex; i < eIndex+lastIndex; i++) {
						letters[i].setColor(color);
					}
				}
				lastIndex += eIndex;
				temp = temp.substring(eIndex, temp.length());
			}
		}
	}
	
	private boolean checkNewLine(char ch) {
		boolean ret = false;
		for (int i = 0; i < nl.length; i++) {
			if (ret == false)
				ret = (ch == System.getProperty("line.separator").charAt(i));
		}
		return ret; //(c == System.getProperty("line.separator").charAt(0) ||
				//c == System.getProperty("line.separator").charAt(1));
	}
}
