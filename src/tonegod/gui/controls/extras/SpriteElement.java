/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.extras;

import com.jme3.font.BitmapFont;
import com.jme3.font.LineWrapMode;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import tonegod.gui.controls.buttons.Button;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;
import tonegod.gui.core.utils.BitmapTextUtil;
import tonegod.gui.core.utils.UIDUtil;

/**
 *
 * @author t0neg0d
 */
public class SpriteElement extends Element implements Control {

	private Spatial spatial;
	private boolean isEnabled = true;
	private boolean useInterval = true;
	private float framesPerSecond = 4;
	protected float trackInterval = (1/framesPerSecond), currentTrack = 0;
	private Texture sprite;
	private int spriteCols, spriteRows;
	private float imgWidth, imgHeight, spriteWidth, spriteHeight, currentX = 0, currentY = 0;
	
	/**
	 * Creates a new instance of the Menu control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param isScrollable Boolean defining if the menu is a scrollable list
	 */
	public SpriteElement(Screen screen, Vector2f position) {
		this(screen, UIDUtil.getUID(), position,
			screen.getStyle("Menu").getVector2f("defaultSize"),
			screen.getStyle("Menu").getVector4f("resizeBorders"),
			screen.getStyle("Menu").getString("defaultImg")
		);
	}
	
	/**
	 * Creates a new instance of the Menu control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 * @param isScrollable Boolean defining if the menu is a scrollable list
	 */
	public SpriteElement(Screen screen, Vector2f position, Vector2f dimensions) {
		this(screen, UIDUtil.getUID(), position, dimensions,
			screen.getStyle("Menu").getVector4f("resizeBorders"),
			screen.getStyle("Menu").getString("defaultImg")
		);
	}
	
	/**
	 * Creates a new instance of the Menu control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 * @param resizeBorders A Vector4f containg the border information used when resizing the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg The default image to use for the Slider's track
	 * @param isScrollable Boolean defining if the menu is a scrollable list
	 */
	public SpriteElement(Screen screen, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		this(screen, UIDUtil.getUID(), position, dimensions, resizeBorders, defaultImg);
	}
	
	/**
	 * Creates a new instance of the Menu control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param isScrollable Boolean defining if the menu is a scrollable list
	 */
	public SpriteElement(Screen screen, String UID, Vector2f position) {
		this(screen, UID, position,
			screen.getStyle("Menu").getVector2f("defaultSize"),
			screen.getStyle("Menu").getVector4f("resizeBorders"),
			screen.getStyle("Menu").getString("defaultImg")
		);
	}
	
	/**
	 * Creates a new instance of the Menu control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 * @param isScrollable Boolean defining if the menu is a scrollable list
	 */
	public SpriteElement(Screen screen, String UID, Vector2f position, Vector2f dimensions) {
		this(screen, UID, position, dimensions,
			screen.getStyle("Menu").getVector4f("resizeBorders"),
			screen.getStyle("Menu").getString("defaultImg")
		);
	}
	
	/**
	 * Creates a new instance of the Menu control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 * @param resizeBorders A Vector4f containg the border information used when resizing the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg The default image to use for the Slider's track
	 * @param isScrollable Boolean defining if the menu is a scrollable list
	 */
	public SpriteElement(Screen screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg);
		
	
	}
	
	public void setSprite(String imgPath, int rows, int cols, float framesPerSecond) {
		this.spriteRows = rows;
		this.spriteCols = cols;
		sprite = screen.getApplication().getAssetManager().loadTexture(imgPath);
		sprite.setMagFilter(Texture.MagFilter.Bilinear);
		sprite.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
		sprite.setWrap(Texture.WrapMode.Repeat);
		
		Image img = sprite.getImage();
		imgWidth = img.getWidth();
		imgHeight = img.getHeight();
		
		spriteWidth = imgWidth/cols;
		spriteHeight = imgHeight/rows;
		
		currentX = 0;
		currentY = 0;
		
		this.setTextureAtlasImage(sprite, "x=" + currentX + "|y=" + currentY + "|w=" + spriteWidth + "|h=" + spriteHeight);
		
		this.useInterval = true;
		this.framesPerSecond = framesPerSecond;
		this.trackInterval = (float)(1/framesPerSecond);
		setIsEnabled(true);
	}
	
	public void setIsEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
		if (isEnabled)	this.addControl(this);
		else			this.removeControl(this);
	}
	
	public boolean getIsEnabled() {
		return this.isEnabled;
	}
	
	@Override
	public Control cloneForSpatial(Spatial spatial) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	@Override
	public void setSpatial(Spatial spatial) {
		this.spatial = spatial;
	}

	@Override
	public void update(float tpf) {
		if (isEnabled) {
			if (useInterval) {
				currentTrack += tpf;
				if (currentTrack >= trackInterval) {
					updateSprite();
					currentTrack -= trackInterval;
				}
			}
		}
	}
	
	private void updateSprite() {
		currentX += spriteWidth;
		if (currentX >= imgWidth) {
			currentX = 0;
			currentY += spriteHeight;
			if (currentY >= imgHeight) {
				currentY = 0;
			}
		}
		setTextureAtlasImage(sprite,"x=" + currentX + "|y=" + currentY + "|w=" + spriteWidth + "|h=" + spriteHeight);
	}
	
	public void setCurrentFrame(int row, int col) {
		currentX = row*spriteWidth;
		currentY = col*spriteHeight;
		this.setTextureAtlasImage(sprite, "x=" + currentX + "|y=" + currentY + "|w=" + spriteWidth + "|h=" + spriteHeight);
	}
	
	@Override
	public void render(RenderManager rm, ViewPort vp) {
		
	}
	
}
