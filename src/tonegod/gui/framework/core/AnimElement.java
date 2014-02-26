/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.framework.core;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import tonegod.gui.framework.animation.TemporalAction;

/**
 *
 * @author t0neg0d
 */
public abstract class AnimElement extends Node implements Transformable {
	public List<TemporalAction> actions = new ArrayList();
	protected Map<String, QuadData> quads = new LinkedHashMap();
	Texture tex;
	protected Map<String, TextureRegion> uvs = new HashMap();
	protected AnimElementMesh mesh;
	Vector2f position = new Vector2f(0,0);
	float z = 1;
	Vector2f scale = new Vector2f(1,1);
	Vector2f origin = new Vector2f(0,0);
	Vector2f dimensions = new Vector2f(0,0);
	ColorRGBA color = new ColorRGBA();
	float rotation;
	Spatial spatial;
	Material mat = null;
	AssetManager am;
	
	public AnimElement(AssetManager am) {
		this.am = am;
		mesh = new AnimElementMesh(this);
	}
	
	public void initialize() {
		flagForUpdate();
		mesh.initialize();
		
	//	mat = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
		mat = new Material(am, "tonegod/gui/shaders/Unshaded.j3md");
		mat.setTexture("ColorMap", tex);
		mat.setBoolean("VertexColor", true);
		mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
		
		Geometry geom = new Geometry();
		geom.setMesh(mesh);
		
		attachChild(geom);
		setMaterial(mat);
	}
	
	public boolean getIsInitialized() {
		return this.mesh.init;
	}
	
	public void setTexture(Texture tex) {
		this.tex = tex;
		if (mat != null)
			mat.setTexture("ColorMap", tex);
	}
	
	public Texture getTexture() {
		return tex;
	}
	
	public TextureRegion addTextureRegion(String regionKey, int x, int y, int w, int h) {
		TextureRegion tr = new TextureRegion(tex, x, y, w, h);
		tr.flip(false, true);
		uvs.put(regionKey, tr);
		return tr;
	}
	
	public TextureRegion getTextureRegion(String regionKey) {
		return uvs.get(regionKey);
	}
	
	public Material getMaterial() {
		return this.mat;
	}
	
	public void setElementMaterial(Material mat) {
		this.mat = mat;
	}
	
	public Map<String,TextureRegion> getTextureRegions() {
		return this.uvs;
	}
	
	public QuadData addQuad(String quadKey, String regionKey, Vector2f position, Vector2f origin) {
		QuadData qd = new QuadData(this, quadKey, uvs.get(regionKey), position.x, position.y, uvs.get(regionKey).getRegionWidth(), uvs.get(regionKey).getRegionHeight(), origin);
		quads.put(quadKey, qd);
		flagForUpdate();
		return qd;
	}
	
	public QuadData addQuad(String quadKey, String regionKey, Vector2f position, Vector2f origin, String parentKey) {
		QuadData qd = new QuadData(this, quadKey, uvs.get(regionKey), position.x, position.y, uvs.get(regionKey).getRegionWidth(), uvs.get(regionKey).getRegionHeight(), origin);
		qd.parent = quads.get(parentKey);
		qd.setPositionX(qd.getPositionX()-qd.parent.getPositionX());
		qd.setPositionY(qd.getPositionY()-qd.parent.getPositionY());
		quads.put(quadKey, qd);
		flagForUpdate();
		return qd;
	}
	
	public void flagForUpdate() {
		mesh.buildPosition = true;
		mesh.buildTexCoords = true;
		mesh.buildColor = true;
		mesh.buildIndices = true;
	}
	
	public void setQuadParent(String key, String parentKey) {
		QuadData qd = getQuads().get(key);
		qd.parent = quads.get(parentKey);
		qd.setPositionX(qd.getPositionX()-qd.parent.getPositionX());
		qd.setPositionY(qd.getPositionY()-qd.parent.getPositionY());
	}
	
	public void rotateQuad(String quadKey, int rotation) {
		quads.get(quadKey).setRotation(rotation);
	}
	
	public void moveQuad(String quadKey, float x, float y) {
		QuadData q = quads.get(quadKey);
		q.setPositionX(x);
		q.setPositionY(y);
	}
	
	public void moveQuad(String quadKey, float z) {
		QuadData q = quads.get(quadKey);
		q.setPositionZ(z);
	}
	
	public void scaleQuad(String quadKey, float scaleX, float scaleY) {
		QuadData q = quads.get(quadKey);
		q.setScaleX(scaleX);
		q.setScaleY(scaleY);
	}
	
	public void addQuadAction(String quadKey, TemporalAction action) {
		quads.get(quadKey).addAction(action);
	}
	
	public Map<String, QuadData> getQuads() {
		return this.quads;
	}
	
	public QuadData getQuad(String key) {
		return this.quads.get(key);
	}
	
	public void centerQuads() {
		float totalWidth = 0, totalHeight = 0;
		for (QuadData q : quads.values()) {
			if (q.getPositionX()+q.getTextureRegion().regionWidth > totalWidth)
				totalWidth = q.getPositionX()+q.getTextureRegion().regionWidth;
			if (q.getPositionY()+q.getTextureRegion().regionHeight > totalHeight)
				totalHeight = q.getPositionY()+q.getTextureRegion().regionHeight;
		}
		for (QuadData q : quads.values()) {
			if (q.parent == null) {
				q.setPositionX(q.getPositionX()-(totalWidth/2));
				q.setPositionY(q.getPositionY()-(totalHeight/2));
			}
		}
	}
	
	public Map<String, TextureRegion> getUVs() {
		return this.uvs;
	}
	
	public void deallocateBuffers() {
		mesh.deallocateBuffers();
	}
	
	public void update(float tpf) {
		mesh.update(tpf);
		for (TemporalAction a : actions) {
			a.act(tpf);
		}
		for (TemporalAction a : actions) {
			if (a.getTime() >= a.getDuration()) {
				actions.remove(a);
				break;
			}
		}
		animElementUpdate(tpf);
	}
	
	public abstract void animElementUpdate(float tpf);
	
	//<editor-fold desc="TRANSFORMABLE">
	@Override
	public void setPositionX(float x) {
		this.position.x = x;
		mesh.buildPosition = true;
	}
	@Override
	public void setPositionY(float y) {
		this.position.y = y;
		mesh.buildPosition = true;
	}
	@Override
	public void setPosition(float x, float y) {
		this.position.set(x,y);
		mesh.buildPosition = true;
	}
	@Override
	public void setPosition(Vector2f pos) {
		this.position.set(pos);
		mesh.buildPosition = true;
	}
	@Override
	public void setRotation(float rotation) {
		this.rotation = rotation;
		mesh.buildPosition = true;
	}
	@Override
	public void setScaleX(float scaleX) {
		this.scale.x = scaleX;
		mesh.buildPosition = true;
	}
	@Override
	public void setScaleY(float scaleY) {
		this.scale.y = scaleY;
		mesh.buildPosition = true;
	}
	@Override
	public void setScale(float x, float y) {
		this.scale.set(x,y);
		mesh.buildPosition = true;
		
	}
	@Override
	public void setScale(Vector2f scale) {
		this.scale.set(scale);
		mesh.buildPosition = true;
	}
	@Override
	public void setOrigin(float x, float y) {
		this.origin.set(x,y);
		mesh.buildPosition = true;
	}
	@Override
	public void setOrigin(Vector2f origin) {
		this.origin.set(origin);
		mesh.buildPosition = true;
	}
	@Override
	public void setOriginX(float originX) {
		this.origin.setX(originX);
		mesh.buildPosition = true;
	}
	@Override
	public void setOriginY(float originY) {
		this.origin.setY(originY);
		mesh.buildPosition = true;
	}
	@Override
	public void setColor(ColorRGBA color) {
		this.color.set(color);
	}
	@Override
	public void setColorR(float r) {
		this.color.r = r;
	}
	@Override
	public void setColorG(float g) {
		this.color.g = g;
	}
	@Override
	public void setColorB(float b) {
		this.color.b = b;
	}
	@Override
	public void setColorA(float a) {
		this.color.a = a;
	}
	@Override
	public void setTCOffsetX(float x) {
		
	}
	@Override
	public void setTCOffsetY(float y) {
		
	}
	@Override
	public void setDimensions(Vector2f dim) {
		this.dimensions.set(dim);
	}
	@Override
	public void setDimensions(float w, float h) {
		this.dimensions.set(w,h);
	}
	@Override
	public void setWidth(float w) {
		this.dimensions.setX(w);
	}
	@Override
	public void setHeight(float h) {
		this.dimensions.setY(h);
	}
	
	@Override
	public float getPositionX() {
		return position.x;
	}
	@Override
	public float getPositionY() {
		return position.y;
	}
	@Override
	public float getRotation() {
		return rotation;
	}
	@Override
	public float getScaleX() {
		return scale.x;
	}
	@Override
	public float getScaleY() {
		return scale.y;
	}
	@Override
	public Vector2f getOrigin() {
		return this.origin;
	}
	@Override
	public float getOriginX() {
		return this.origin.x;
	}
	@Override
	public float getOriginY() {
		return this.origin.y;
	}
	@Override
	public ColorRGBA getColor() {
		return color;
	}
	@Override
	public float getColorR() {
		return color.r;
	}
	@Override
	public float getColorG() {
		return color.g;
	}
	@Override
	public float getColorB() {
		return color.b;
	}
	@Override
	public float getColorA() {
		return color.g;
	}
	@Override
	public float getWidth() {
		return dimensions.x;
	}
	@Override
	public float getHeight() {
		return dimensions.y;
	}
	@Override
	public float getTCOffsetX() { return 0; }
	@Override
	public float getTCOffsetY() { return 0; }
	@Override
	public void setPositionZ(float z) {
		this.z = z;
		mesh.buildPosition = true;
	}
	@Override
	public float getPositionZ() { return z; }
	@Override
	public Vector2f getPosition() { return this.position; }
	@Override
	public Vector2f getScale() { return scale; }
	@Override
	public Vector2f getDimensions() {
		return this.dimensions;
	}
	@Override
	public Vector2f getTCOffset() {
		return null;
	}
	@Override
	public void addAction(TemporalAction action) {
		action.setTransformable(this);
		actions.add(action);
	}
	@Override
	public boolean getContainsAction(TemporalAction action) {
		return actions.contains(action);
	}
	//</editor-fold>
}
