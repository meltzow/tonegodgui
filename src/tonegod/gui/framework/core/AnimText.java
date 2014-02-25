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
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import java.util.LinkedList;

/**
 *
 * @author t0neg0d
 */
public class AnimText extends AnimElement {
	public static enum TagType {
		Paragraph,
		NewLine,
		Underline,
		StrikeThrough,
		Italic,
		Bold
	}
	private BitmapFont font;
	private String text;
	private int imgHeight;
	private boolean fadeIn = false;
	private boolean fadeOut = false;
	private QuadData[] letters;
	private QuadData qd;
	private TextureRegion tr;
	private Vector2f bounds = new Vector2f();
	private Character c;
	
	char[] characters;
	
	private LineWrapMode textWrap = LineWrapMode.NoWrap;
	private Align textAlign = Align.Left;
	private VAlign textVAlign = VAlign.Top;
	private Align lastAlign = textAlign;
	private Align currentAlign = textAlign;

	private int lineCount = 0;
	private float	fadeDuration = 0,
					fadeCounter = 0,
					alpha = 1,
					lineWidth = 0,
					lineHeight = 0;
	
	BitmapCharacter bc, bcSpc;
	int wordSIndex = 0, wordEIndex = 0;
	int lineSIndex = 0, lineEIndex = 0;
	float x = 0, y = 0, lnWidth = 0, wordWidth = 0;
	int lIndex = 0;
	boolean placeWord = false;
	
	private boolean useDropShadow = false;
	private Vector2f dsOffset = new Vector2f(0,-3);
	private ColorRGBA fontColor = new ColorRGBA(1f,1f,1f,1f);
	private ColorRGBA dsColor = new ColorRGBA(0.3f,0.3f,0.3f,0.5f);
	
	// Temp vars
	private Vector2f align = new Vector2f();
	private Vector2f pos = new Vector2f();
	
	char[] nl;
	
	private LinkedList<Tag> tags = new LinkedList();
	
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
	
	public final void setText(String text) {
		this.uvs.clear();
		this.quads.clear();
		bcSpc = font.getCharSet().getCharacter('i');
		lIndex = 0;
		lineWidth = 0;
		
		text = stripTags(text);
		
		for (int i = 0; i < text.length(); i++) {
			c = text.charAt(i);
			bc = font.getCharSet().getCharacter(c.charValue());
			
			if (bc != null) {
				if (c != ' ') {
					tr = addTextureRegion(String.valueOf(c.hashCode()), bc.getX(), imgHeight-bc.getY()-bc.getHeight(), bc.getWidth(), bc.getHeight());
					tr.flip(false, true);
					align.set(bc.getWidth()/2, bc.getHeight()/2);
					pos.set(lineWidth,font.getCharSet().getBase()-bc.getHeight()-bc.getYOffset());
					qd = addQuad(String.valueOf(lIndex), String.valueOf(c.hashCode()), pos, align);
					qd.setColor(fontColor);
					qd.userIndex = i;
					lineWidth += bc.getXAdvance();
					lIndex++;
				} else {
					lineWidth += bcSpc.getXAdvance();
				}
			}
		}
		
		setOrigin(getWidth()/2,getHeight()/2);
		mesh.initialize();
		mesh.update(0);
		
		mesh.updateBound();
		
		characters = text.toCharArray();
		
		letters = quads.values().toArray(new QuadData[0]);
		
		this.text = text;
	}
	
	private String stripTags(String text) {
		tags.clear();
		int sIndex = 0, eIndex = 0;
		
		sIndex = text.indexOf("<");
		int lineIndex = 0;
		while (sIndex > -1) {
			eIndex = text.indexOf(">");
			if (eIndex > -1) {
				String tagName = text.substring(sIndex, eIndex+1);
				TagType type = getTagType(tagName);
				Tag tag = new Tag(sIndex,type);
				if (tagName.indexOf("</") != -1)
					tag.close = true;
				switch(type) {
					case Paragraph:
						if (tagName.indexOf("align") != -1) {
							tag.align = Align.valueOf(tagName.substring(tagName.indexOf("=")+1,tagName.indexOf(">")));
						}
						break;
					case Underline:
						if (!tag.close) {
							
						}
						break;
				}
				
				tags.add(tag);
				text = text.replaceFirst(tagName, "");
			} else {
				break;
			}
			sIndex = text.indexOf("<");
		}
		
		return text;
	}
	
	private TagType getTagType(String tagName) {
		if (tagName.equals("<u>") || tagName.equals("</u>"))
			return TagType.Underline;
		else if (tagName.indexOf("<i>") != -1 || tagName.indexOf("</i>") != -1)
			return TagType.Italic;
		else if (tagName.indexOf("<strike") != -1 || tagName.indexOf("</strike") != -1)
			return TagType.StrikeThrough;
		else if (tagName.indexOf("<b>") != -1 || tagName.indexOf("</b>") != -1)
			return TagType.Bold;
		else if (tagName.indexOf("</br>") != -1 || tagName.indexOf("<br>") != -1)
			return TagType.NewLine;
		else if (tagName.indexOf("<p") != -1 || tagName.indexOf("</p>") != -1)
			return TagType.Paragraph;
		else return null;
	}
	
	private Tag hasTagAt(int index) {
		Tag ret = null;
		
		for (Tag tag : tags) {
			if (tag.index == index) {
				ret = tag;
				break;
			}
		}
		
		return ret;
	}
	
	public void setMaxAlpha(float alpha) {
		this.alpha = alpha;
	}
	
	public void setAlpha(float alpha) {
		for (QuadData quad : quads.values())
			quad.setColorA(alpha);
	}
	
	public void wrapTextNoWrap() {
	//	int i = 0;
		float x = 0, y = -(font.getCharSet().getBase()/2);
		float lnWidth = 0;
		int lIndex = 0;
		BitmapCharacter bc, bcSpc = font.getCharSet().getCharacter('i');
		for (char c : text.toCharArray()) {
			bc = font.getCharSet().getCharacter(c);
			
			if (bc != null) {
				if (c != ' ') {
					QuadData quad = getQuads().get(String.valueOf(lIndex));
					quad.setPositionX(x);
					quad.setPositionY(font.getCharSet().getBase()-bc.getHeight()-bc.getYOffset()+y);

					x += bc.getXAdvance();
					lnWidth += bc.getXAdvance();
					lIndex++;
				} else {
					x += bcSpc.getXAdvance();
					lnWidth += bcSpc.getXAdvance();
				}
			}
		}
		
		lineWidth = lnWidth;
		lineHeight = font.getCharSet().getLineHeight();
		
		updateLineForAlignment(0,lIndex,lnWidth);
		
		lineCount = 1;
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
		BitmapCharacter bc, bcSpc = font.getCharSet().getCharacter('i');
		float lnWidth = 0;
		boolean newLine = false;
		lineCount = 1;
		int lIndex = 0;
		int newLineCount = 0;
		
		for (char c : characters) {
			c = text.charAt(i);
			bc = font.getCharSet().getCharacter(c);
			
			if (bc != null) {
				if (c == ' ' && ! newLine) {
					lnWidth += bc.getXAdvance();
					x += bcSpc.getXAdvance();
				}
				else {
					if (x+bc.getXAdvance() > width || newLine) {
						if (i > 0) {
							int tIndex = i;
						//	tIndex;
							while (characters[tIndex] == ' ') {
								x -= bcSpc.getXAdvance();
								lnWidth -= bcSpc.getXAdvance();
								tIndex--;
							}
						}
						updateLineForAlignment(lineIndex, lIndex, lnWidth);
						lnWidth = 0;
						x = 0;
						if (newLine)	y -= font.getCharSet().getBase()*newLineCount;
						else			y -= font.getCharSet().getBase();
						lineIndex = lIndex;
						newLine = false;
						newLineCount = 0;
					}
					qd = letters[lIndex];
					qd.setPositionX(x);
					qd.setPositionY(font.getCharSet().getBase()-bc.getHeight()-bc.getYOffset()+y);
					lnWidth += bc.getXAdvance();
					x += bc.getXAdvance();
					lIndex++;
				}
			} else {
				newLine = true;
				newLineCount++;
			}
			i++;
		}
		
		updateLineForAlignment(lineIndex, lIndex, lnWidth);
		
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
		
		bcSpc = font.getCharSet().getCharacter('i');
		wordSIndex = 0; wordEIndex = 0;
		lineSIndex = 0; lineEIndex = 0;
		x = 0; y = -(font.getCharSet().getBase()/2);
		lnWidth = 0; wordWidth = 0;
		lIndex = 0;
		placeWord = false;
		int i = 0;
		boolean startUL = false;
		boolean endUL = false;
		
		for (char c : characters) {
		//	c = text.charAt(i);
			bc = font.getCharSet().getCharacter(c);
			
			if (bc != null) {
				wordEIndex = lIndex;
				for (Tag t : tags) {
					if (t.index == i) {
						switch (t.type) {
							case NewLine:
								formatNewLine(width);
								break;
							case Paragraph:
								formatParagraph(t,width);
								break;
							case Underline:
								
								break;
						}
					}
				}
				if (c == ' ') {
					lnWidth += bcSpc.getXAdvance();
					wordSIndex = lIndex;
				} else {
					QuadData quad = letters[lIndex];//getQuadDataAt(lIndex);
					quad.setPosition(x,font.getCharSet().getBase()-bc.getHeight()-bc.getYOffset()+y);
					x += bc.getXAdvance();
					wordWidth += bc.getXAdvance();
					
					if (i+1 < text.length()) {
						char ch = text.charAt(i+1);
						if (ch == ' ')
							placeWord = true;
					} else if (i+1 >= text.length()) {
						placeWord = true;
					}
					if (placeWord) {
						placeWord(width);
					}
					lIndex++;
				}
			}
			i++;
		}
		wordEIndex = letters.length-1;
		for (Tag t : tags) {
			if (t.index == i) {
				switch (t.type) {
					case NewLine:
						formatNewLine(width);
						break;
					case Paragraph:
						formatParagraph(t,width);
						break;
				}
			}
		}
		setOrigin(getWidth()/2,getHeight()/2);
		alignToBoundsV();
		mesh.update(0);
		mesh.updateBound();
	}
	
	private void placeWord(float width) {
		if (lnWidth+wordWidth < width) {
			for (int w = wordSIndex; w <= wordEIndex; w++) {
				QuadData quad = letters[w];
				quad.setPositionX(quad.getPositionX()+lnWidth);
			}
			lnWidth += wordWidth;
		} else {
			updateLineForAlignment(currentAlign, lineSIndex, wordSIndex, width, lnWidth);
			
			y -= font.getCharSet().getBase();

			for (int w = wordSIndex; w <= wordEIndex; w++) {
				QuadData quad = letters[w];
				quad.setPositionY(
					quad.getPositionY()-font.getCharSet().getBase()
				);
			}
			lineSIndex = wordSIndex;
			
			lnWidth = wordWidth;
		}
		x = 0;
		wordWidth = 0;
		wordSIndex = lIndex;
		placeWord = false;
	}
	
	private void formatNewLine(float width) {
		placeWord(width);
		updateLineForAlignment(currentAlign, lineSIndex, wordEIndex, width, lnWidth);
		lineSIndex = wordEIndex;
		lnWidth = wordWidth;
		x = 0;
		wordWidth = 0;
		y -= font.getCharSet().getBase();
	}
	
	public void formatParagraph(Tag t, float width) {
		placeWord(width);
		updateLineForAlignment(currentAlign, lineSIndex, wordEIndex, width, lnWidth);
		lineSIndex = wordEIndex;
		x = 0;
		lnWidth = wordWidth;
		wordWidth = 0;
		y -= font.getCharSet().getBase()*2;
		if (!t.close) {
			currentAlign = t.align;
		} else {
			currentAlign = textAlign;
		}
	}
	
	private void updateLineForAlignment(int head, int tail, float width) {
		switch (textAlign) {
			case Right:
				for (int xi = head; xi < tail; xi++) {
					letters[xi].setPositionX(letters[xi].getPositionX()-width);
				}
				break;
			case Center:
				for (int xi = head; xi < tail; xi++) {
					letters[xi].setPositionX(letters[xi].getPositionX()-(width/2));
				}
				break;
		}
	}
	
	private void updateLineForAlignment(Align textAlign, int head, int tail, float width, float lnWidth) {
		if (tail == letters.length-1) tail = letters.length;
		switch (textAlign) {
			case Right:
				for (int xi = head; xi < tail; xi++) {
					letters[xi].setPositionX(letters[xi].getPositionX()+(width-lnWidth));
				}
				break;
			case Center:
				for (int xi = head; xi < tail; xi++) {
					letters[xi].setPositionX(letters[xi].getPositionX()+((width/2)-(lnWidth/2)));
				}
				break;
		}
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
		float lnHeight = font.getCharSet().getCharacter('X').getYOffset()*getScale().y;
		innerHeight -= lnHeight;
		
		switch (textVAlign) {
			case Top:
				setPositionY(height-lnHeight);
				break;
			case Center:
				switch (textWrap) {
					case NoWrap:
					case Clip:
						setPositionY((height/2)+(lnHeight/2));
						break;
					default:
						setPositionY((height/2)+(innerHeight/2));
						break;
				}
				break;
			case Bottom:
				switch (textWrap) {
					case NoWrap:
					case Clip:
						setPositionY((lnHeight*2));
						break;
					default:
						setPositionY(innerHeight+lnHeight);
						break;
				}
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
		currentAlign = textAlign;
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
	
	public void setFont(BitmapFont font) {
		this.font = font;
		
		Texture bfTexture = (Texture)font.getPage(0).getParam("ColorMap").getValue();
		setTexture(bfTexture);
		imgHeight = (int)bfTexture.getImage().getHeight();
		
		setText(".");
		initialize();
		
	//	setBounds(getWidth(),getHeight());
	}
	
	public void setSubStringColor(String subString, ColorRGBA color, boolean allInstances, int... whichInstances) {
		String temp = text;
		int lastIndex = 0;
		int sIndex = 1;
		int count = 1;
		while (sIndex != -1) {
			sIndex = temp.indexOf(subString);
			if (sIndex != -1) {
				int spcCount = 0;
				for (int i = 0; i < subString.length(); i++) {
					if (subString.charAt(i) == ' ')
						spcCount++;
				}
				int eIndex = sIndex+subString.length()-spcCount;
				boolean valid = false;
				if (!allInstances) {
					for (int c : whichInstances) {
						if (c == count) {
							valid = true;
							break;
						}
					}
					if (valid) {
						spcCount = 0;
						for (int i = 0; i < sIndex+lastIndex; i++) {
							if (text.charAt(i) == ' ')
								spcCount++;
						}
						for (int i = sIndex+lastIndex-spcCount; i < eIndex+lastIndex-spcCount; i++) {
							letters[i].setColor(color);
						}
					}
					count++;
				} else {
					spcCount = 0;
					for (int i = 0; i < sIndex+lastIndex; i++) {
						if (text.charAt(i) == ' ')
							spcCount++;
					}
					for (int i = sIndex+lastIndex-spcCount; i < eIndex+lastIndex-spcCount; i++) {
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
	
	public class Tag {
		public TagType type;
		public int index = 0;
		public Align align = null;
		public boolean close = false;
		
		public Tag(int index, TagType type) {
			this.index = index;
			this.type = type;
		}
	}
}
