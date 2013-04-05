/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.extras.emitter;

import com.jme3.app.Application;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import tonegod.gui.controls.extras.SpriteElement;
import tonegod.gui.core.Screen;

/**
 *
 * @author t0neg0d
 */
public class ElementEmitter implements Control {
	public static enum InfluencerType {
		Gravity,
		Color,
		Size,
		Rotation
	}
	
	Screen screen;
	Application app;
	private float targetInterval = 1f;
	private float currentInterval = 0f;
	private boolean isEnabled = false;
	Map<String, Influencer> influencers = new HashMap();
	ElementParticle[] particles;
	float emitterWidth, emitterHeight;
	Vector2f emitterPosition = new Vector2f();
	
	// Sprite Info
	private String spriteImagePath;
	private int spriteRows, spriteCols, spriteFPS;
	private float spriteSize = 30;
	
	// Globals
	private float force = .25f;
	private float highLife = .5f;
	private float lowLife = .1f;
	
	public ElementEmitter(Screen screen, Vector2f emitterPosition, float emitterWidth, float emitterHeight) {
		this.screen = screen;
		this.app = screen.getApplication();
		this.emitterWidth = emitterWidth;
		this.emitterHeight = emitterHeight;
		this.emitterPosition.set(emitterPosition);
		
		GravityInfluencer g = new GravityInfluencer();
		influencers.put("Gravity",g);
		ColorInfluencer c = new ColorInfluencer();
		influencers.put("Color",c);
		SizeInfluencer s = new SizeInfluencer();
		influencers.put("Size",s);
		RotationInfluencer r = new RotationInfluencer();
		influencers.put("Rotation",r);
		
	}
	
	public Influencer getInfluencer(String key) {
		return influencers.get(key);
	}
	
	public Influencer getInfluencer(InfluencerType type) {
		return influencers.get(type.toString());
	}
	
	public void setSprite(String spriteImagePath, int spriteRows, int spriteCols, int spriteFPS) {
		this.spriteImagePath = spriteImagePath;
		this.spriteRows = spriteRows;
		this.spriteCols = spriteCols;
		this.spriteFPS = spriteFPS;
	}
	
	@Override
	public void update(float tpf) {
		if (isEnabled) {
			currentInterval += tpf;
			if (currentInterval >= targetInterval) {
				emitNextParticle();
				currentInterval -= targetInterval;
			}
		}
		for (ElementParticle p : particles) {
			if (p.active) p.update(tpf);
		}
	}
	
	public void setMaxParticles(int maxParticles) {
		particles = new ElementParticle[maxParticles];
		for (int i = 0; i < maxParticles; i++) {
			ElementParticle p = new ElementParticle();
			p.particle = new SpriteElement(screen,
				Vector2f.ZERO,
				new Vector2f(spriteSize, spriteSize),
				Vector4f.ZERO,
				null
			);
			p.particle.setSprite(spriteImagePath, spriteRows, spriteCols, spriteFPS);
			p.particle.getGeometry().center();
			screen.addElement(p.particle);
			p.initialize(true);
			particles[i] = p;
		}
	}
	
	public void setParticlesPerSecond(int particlesPerSecond) {
		this.targetInterval = 1f/(float)particlesPerSecond;
	}
	
	public void startEmitter(Node root) {
		this.isEnabled = true;
		root.addControl(this);
	}
	
	public void stopEmitter() {
		this.isEnabled = false;
	}
	
	public void destroyEmitter() {
		this.isEnabled = false;
	}
	
	private void emitNextParticle() {
		boolean particleEmitted = false;
		for (ElementParticle p : particles) {
			if (!p.active && !particleEmitted) {
				p.initialize(false);
				particleEmitted = true;
			}
		}
	}

	public float getForce() {
		return force;
	}

	public void setForce(float force) {
		this.force = force;
	}

	public float getHighLife() {
		return highLife;
	}

	public void setHighLife(float highLife) {
		this.highLife = highLife;
	}

	public float getLowLife() {
		return lowLife;
	}

	public void setLowLife(float lowLife) {
		this.lowLife = lowLife;
	}

	@Override
	public Control cloneForSpatial(Spatial spatial) {
		return this;
	}

	@Override
	public void setSpatial(Spatial spatial) {
		
	}

	@Override
	public void render(RenderManager rm, ViewPort vp) {
		
	}

	@Override
	public void write(JmeExporter ex) throws IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void read(JmeImporter im) throws IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	public class ElementParticle {
		public SpriteElement particle;
		public Vector2f position = new Vector2f();
		public Vector2f velocity = new Vector2f();
		public ColorRGBA color = new ColorRGBA();
		public float size;
		public float life;
		public float startlife;
		public float angle;
		public float rotateSpeed;
		public boolean rotateDir;
		public boolean active = false;
		public float blend;
		
		public void update(float tpf) {
			final ElementParticle p = this;
			
			p.life -= tpf;
			blend = (p.startlife - p.life) / p.startlife;
			if (blend > 1) blend = 1;
			if (blend < 0) blend = 0;
			
			if (life < 0) {
				p.killParticle();
				return;
			}
			
			for (Influencer inf : influencers.values()) {
				inf.update(p, tpf);
			}
			
		//	app.enqueue(new Callable() {
		//		@Override
		//		public Object call() throws Exception {
					p.particle.setPosition(p.position);
					p.particle.setLocalScale(p.size);
					p.particle.getElementMaterial().setColor("Color", p.color);
					p.particle.setLocalRotation(p.particle.getLocalRotation().fromAngleAxis(p.angle, Vector3f.UNIT_Z));
		//			return null;
		//		}
		//	});
		};
		
		public void initialize(boolean hide) {
			float diffX = FastMath.rand.nextFloat()*emitterWidth;
			if (FastMath.rand.nextBoolean()) diffX = -diffX;
			float diffY = FastMath.rand.nextFloat()*emitterHeight;
			if (FastMath.rand.nextBoolean()) diffY = -diffY;
			position.set(emitterPosition.add(new Vector2f(diffX,diffY)));
			float velX = FastMath.rand.nextFloat()*force;
			if (FastMath.rand.nextBoolean()) velX = -velX;
			float velY = FastMath.rand.nextFloat()*force;
			if (FastMath.rand.nextBoolean()) velY = -velY;
			velocity.set(new Vector2f(velX,velY));
			life = highLife;
			startlife = lowLife + FastMath.nextRandomFloat() * (highLife - lowLife);
			life = startlife;
			rotateDir = FastMath.rand.nextBoolean();
			rotateSpeed = FastMath.rand.nextFloat();
			
			for (Influencer inf : influencers.values()) {
				inf.initialize(this);
			}
			active = !hide;
		//	if (screen.getElementById(particle.getUID()) == null)
			if (hide)	particle.hide();
			else		particle.show();
			update(0);
		}
		
		public void killParticle() {
			active = false;
			particle.hide();
		}
	}
}
