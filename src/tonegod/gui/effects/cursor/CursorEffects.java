/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.effects.cursor;

import com.jme3.app.Application;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import tonegod.gui.controls.extras.emitter.AlphaInfluencer;
import tonegod.gui.controls.extras.emitter.ColorInfluencer;
import tonegod.gui.controls.extras.emitter.DirectionInfluencer;
import tonegod.gui.controls.extras.emitter.ElementEmitter;
import tonegod.gui.controls.extras.emitter.GravityInfluencer;
import tonegod.gui.controls.extras.emitter.ImpulseInfluencer;
import tonegod.gui.controls.extras.emitter.RotationInfluencer;
import tonegod.gui.controls.extras.emitter.SizeInfluencer;
import tonegod.gui.controls.extras.emitter.SpriteInfluencer;
import tonegod.gui.core.Screen;

/**
 *
 * @author t0neg0d
 */
public class CursorEffects {
	public static enum EmitterConfig {
		DEFAULT,
		LEFT_CLICK,
		WHEEL_CLICK,
		RIGHT_CLICK
	}
	public static enum EmitterTheme {
		SPARKS,
		FLAMES,
		POISON,
		CUSTOM
	}
	Screen screen;
	Application app;
	ElementEmitter cursorEmitter;
	EmitterTheme theme = EmitterTheme.SPARKS;
	CursorEffectSettings defaultSettings = new CursorEffectSettings();
	CursorEffectSettings leftClickSettings = new CursorEffectSettings();
	CursorEffectSettings wheelClickSettings = new CursorEffectSettings();
	CursorEffectSettings rightClickSettings = new CursorEffectSettings();
	
	public CursorEffects(Screen screen) {
		this.screen = screen;
		this.app = screen.getApplication();
		
		cursorEmitter = new ElementEmitter(screen,new Vector2f(screen.getWidth()/2-200,screen.getHeight()/2),2,2);
		cursorEmitter.setSprite("Textures/bullet_02.png", 3, 3, 8);
		cursorEmitter.setMaxParticles(160);
		this.setTheme(EmitterTheme.SPARKS);
		
	}
	
	public void updatePosition(Vector2f position) {
		cursorEmitter.setPosition(position);
	}
	
	public void handleClick(int which) {
		switch(which) {
			case 0:
				loadSettings(EmitterConfig.LEFT_CLICK);
				cursorEmitter.emitAllParticles();
				loadSettings(EmitterConfig.DEFAULT);
			case 1:
				loadSettings(EmitterConfig.RIGHT_CLICK);
				cursorEmitter.emitAllParticles();
				loadSettings(EmitterConfig.DEFAULT);
			case 2:
				loadSettings(EmitterConfig.WHEEL_CLICK);
				cursorEmitter.emitAllParticles();
				loadSettings(EmitterConfig.DEFAULT);
		}
	}
	
	public void start() {
		cursorEmitter.startEmitter((Node)app.getGuiViewPort().getScenes().get(0));
		float z = screen.getGUINode().getLocalTranslation().z;
		cursorEmitter.getParticles().setLocalTranslation(0, 0, 1f);
	}
	
	public void stop() {
		cursorEmitter.stopEmitter();
	}
	
	public ElementEmitter getEmitter() {
		return this.cursorEmitter;
	}
	
	private void loadSettings(EmitterConfig which) {
		CursorEffectSettings currentConfig = defaultSettings;
		switch (which) {
			case DEFAULT:
				break;
			case LEFT_CLICK:
				currentConfig = leftClickSettings;
				break;
			case WHEEL_CLICK:
				currentConfig = wheelClickSettings;
				break;
			case RIGHT_CLICK:
				currentConfig = rightClickSettings;
				break;
		}
		cursorEmitter.setMinForce(currentConfig.minForce);
		cursorEmitter.setMaxForce(currentConfig.maxForce);
		cursorEmitter.setHighLife(currentConfig.highLife);
		cursorEmitter.setLowLife(currentConfig.lowLife);
		cursorEmitter.setUseFixedDirection(currentConfig.useFixedDirection,currentConfig.fixedDirection);
		cursorEmitter.setParticlesPerSecond(currentConfig.particlesPerSecond);
	//	cursorEmitter.removeInfluencer(AlphaInfluencer.class);
	//	cursorEmitter.addInfluencer(currentConfig.ai);
	//	cursorEmitter.removeInfluencer(ColorInfluencer.class);
	//	cursorEmitter.addInfluencer(currentConfig.ci);
	//	cursorEmitter.removeInfluencer(DirectionInfluencer.class);
	//	cursorEmitter.addInfluencer(currentConfig.di);
	//	cursorEmitter.removeInfluencer(GravityInfluencer.class);
	//	cursorEmitter.addInfluencer(currentConfig.gi);
	//	cursorEmitter.removeInfluencer(ImpulseInfluencer.class);
	//	cursorEmitter.addInfluencer(currentConfig.ii);
	//	cursorEmitter.removeInfluencer(RotationInfluencer.class);
	//	cursorEmitter.addInfluencer(currentConfig.ri);
	//	cursorEmitter.removeInfluencer(SizeInfluencer.class);
	//	cursorEmitter.addInfluencer(currentConfig.si);
	//	cursorEmitter.removeInfluencer(SpriteInfluencer.class);
	//	cursorEmitter.addInfluencer(currentConfig.spi);
	//	if (currentConfig != defaultSettings)
	//		cursorEmitter.removeAllParticles();
	}
	
	public void storeSettings(EmitterConfig which) {
		CursorEffectSettings currentConfig = defaultSettings;
		switch (which) {
			case DEFAULT:
				break;
			case LEFT_CLICK:
				currentConfig = leftClickSettings;
				break;
			case WHEEL_CLICK:
				currentConfig = wheelClickSettings;
				break;
			case RIGHT_CLICK:
				currentConfig = rightClickSettings;
				break;
		}
		currentConfig.minForce = cursorEmitter.getMinForce();
		currentConfig.maxForce = cursorEmitter.getMaxForce();
		currentConfig.highLife = cursorEmitter.getHighLife();
		currentConfig.lowLife = cursorEmitter.getLowLife();
		currentConfig.fixedDirection.set(cursorEmitter.getFixedDirection());
		currentConfig.particlesPerSecond = cursorEmitter.getParticlesPerSecond();
		currentConfig.useFixedDirection = cursorEmitter.getUseFixedDirection();
		currentConfig.ai = cursorEmitter.getInfluencer(AlphaInfluencer.class).clone();
		currentConfig.ci = cursorEmitter.getInfluencer(ColorInfluencer.class).clone();
		currentConfig.di = cursorEmitter.getInfluencer(DirectionInfluencer.class).clone();
		currentConfig.gi = cursorEmitter.getInfluencer(GravityInfluencer.class).clone();
		currentConfig.ii = cursorEmitter.getInfluencer(ImpulseInfluencer.class).clone();
		currentConfig.ri = cursorEmitter.getInfluencer(RotationInfluencer.class).clone();
		currentConfig.si = cursorEmitter.getInfluencer(SizeInfluencer.class).clone();
		currentConfig.spi = cursorEmitter.getInfluencer(SpriteInfluencer.class).clone();
	}
	
	public final void setTheme(EmitterTheme theme) {
		this.theme = theme;
		configTheme();
	}
	
	private void configTheme() {
		switch (theme) {
			case SPARKS:
				cursorEmitter.setParticlesPerSecond(30);
				cursorEmitter.setMinForce(1.75f);
				cursorEmitter.setMaxForce(1.75f);
				cursorEmitter.setHighLife(2.5f);
				cursorEmitter.setLowLife(1.2f);
				cursorEmitter.getInfluencer(GravityInfluencer.class).setGravity(new Vector2f(0f,1f));
				cursorEmitter.getInfluencer(RotationInfluencer.class).setMaxRotationSpeed(.25f);
				cursorEmitter.getInfluencer(ColorInfluencer.class).setStartColor(new ColorRGBA(1.0f,1.0f,1.0f,1.0f));
				cursorEmitter.getInfluencer(ColorInfluencer.class).setEndColor(ColorRGBA.White);
				cursorEmitter.getInfluencer(SizeInfluencer.class).setStartSize(1f);
				cursorEmitter.getInfluencer(SizeInfluencer.class).setStartSize(.5f);
				cursorEmitter.setUseFixedDirection(false, Vector2f.ZERO);
				cursorEmitter.setFixedDirectionStrength(0.5f);
				storeSettings(EmitterConfig.DEFAULT);
				cursorEmitter.setParticlesPerSecond(0);
				cursorEmitter.setMinForce(2.75f);
				cursorEmitter.setMaxForce(4.75f);
				cursorEmitter.setUseFixedDirection(true, new Vector2f(-1,1).normalize());
				storeSettings(EmitterConfig.LEFT_CLICK);
				cursorEmitter.setMinForce(2.75f);
				cursorEmitter.setMaxForce(4.75f);
				cursorEmitter.setUseFixedDirection(true, new Vector2f(0,1).normalize());
				storeSettings(EmitterConfig.WHEEL_CLICK);
				cursorEmitter.setMinForce(2.75f);
				cursorEmitter.setMaxForce(4.75f);
				cursorEmitter.setUseFixedDirection(true, new Vector2f(1,1).normalize());
				storeSettings(EmitterConfig.RIGHT_CLICK);
				cursorEmitter.setMinForce(1.75f);
				cursorEmitter.setMaxForce(1.75f);
				cursorEmitter.setParticlesPerSecond(30);
				cursorEmitter.setUseFixedDirection(false, Vector2f.ZERO);
				break;
			case FLAMES:
				cursorEmitter.setParticlesPerSecond(30);
				cursorEmitter.setForce(.25f);
				cursorEmitter.setHighLife(4f);
				cursorEmitter.setLowLife(1.2f);
				cursorEmitter.getInfluencer(GravityInfluencer.class).setGravity(new Vector2f(0f,0f));
				cursorEmitter.getInfluencer(RotationInfluencer.class).setMaxRotationSpeed(.25f);
				cursorEmitter.getInfluencer(ColorInfluencer.class).setStartColor(ColorRGBA.Green);
				cursorEmitter.getInfluencer(ColorInfluencer.class).setEndColor(ColorRGBA.White);
				cursorEmitter.getInfluencer(SizeInfluencer.class).setStartSize(1f);
				cursorEmitter.getInfluencer(SizeInfluencer.class).setStartSize(.5f);
				cursorEmitter.getInfluencer(ImpulseInfluencer.class).setVariationStrength(1.5f);
				cursorEmitter.setUseFixedDirection(false, Vector2f.ZERO);
				storeSettings(EmitterConfig.DEFAULT);
				cursorEmitter.setParticlesPerSecond(0);
				cursorEmitter.setUseFixedDirection(true, new Vector2f(1,1).normalize());
				storeSettings(EmitterConfig.LEFT_CLICK);
				cursorEmitter.setUseFixedDirection(true, new Vector2f(0,1).normalize());
				storeSettings(EmitterConfig.WHEEL_CLICK);
				cursorEmitter.setUseFixedDirection(true, new Vector2f(-1,1).normalize());
				storeSettings(EmitterConfig.RIGHT_CLICK);
				cursorEmitter.setParticlesPerSecond(30);
				cursorEmitter.setUseFixedDirection(false, Vector2f.ZERO);
				break;
			case POISON:
				cursorEmitter.setParticlesPerSecond(30);
				cursorEmitter.setForce(.25f);
				cursorEmitter.setHighLife(4f);
				cursorEmitter.setLowLife(1.2f);
				cursorEmitter.getInfluencer(GravityInfluencer.class).setGravity(new Vector2f(0f,0f));
				cursorEmitter.getInfluencer(RotationInfluencer.class).setMaxRotationSpeed(.25f);
				cursorEmitter.getInfluencer(ColorInfluencer.class).setStartColor(ColorRGBA.Green);
				cursorEmitter.getInfluencer(ColorInfluencer.class).setEndColor(ColorRGBA.White);
				cursorEmitter.getInfluencer(SizeInfluencer.class).setStartSize(1f);
				cursorEmitter.getInfluencer(SizeInfluencer.class).setStartSize(.5f);
				cursorEmitter.getInfluencer(ImpulseInfluencer.class).setVariationStrength(1.5f);
				cursorEmitter.setUseFixedDirection(false, Vector2f.ZERO);
				storeSettings(EmitterConfig.DEFAULT);
				cursorEmitter.setParticlesPerSecond(0);
				cursorEmitter.setUseFixedDirection(true, new Vector2f(1,1).normalize());
				storeSettings(EmitterConfig.LEFT_CLICK);
				cursorEmitter.setUseFixedDirection(true, new Vector2f(0,1).normalize());
				storeSettings(EmitterConfig.WHEEL_CLICK);
				cursorEmitter.setUseFixedDirection(true, new Vector2f(-1,1).normalize());
				storeSettings(EmitterConfig.RIGHT_CLICK);
				cursorEmitter.setParticlesPerSecond(30);
				cursorEmitter.setUseFixedDirection(false, Vector2f.ZERO);
				break;
			case CUSTOM:
				break;
		}
	}
	
	private class CursorEffectSettings {
		public GravityInfluencer gi;
		public AlphaInfluencer ai;
		public ColorInfluencer ci;
		public SizeInfluencer si;
		public DirectionInfluencer di;
		public ImpulseInfluencer ii;
		public SpriteInfluencer spi;
		public RotationInfluencer ri;
		public float highLife;
		public float lowLife;
		public float minForce;
		public float maxForce;
		public Vector2f fixedDirection = new Vector2f();
		public boolean useFixedDirection;
		public int particlesPerSecond;
	}
}
