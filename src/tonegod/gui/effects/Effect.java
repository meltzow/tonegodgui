/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.effects;

import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.texture.Texture;
import tonegod.gui.core.Element;

/**
 *
 * @author t0neg0d
 */
public class Effect implements Cloneable {
	public enum EffectType {
		FadeIn,
		FadeOut,
		ZoomIn,
		ZoomOut,
		SlideIn,
		SlideOut,
		SpinIn,
		SpinOut,
		Pulse,
		ColorSwap,
		PulseColor,
		ImageSwap
	}
	
	public enum EffectEvent {
		GetFocus,
		LoseFocus,
		Show,
		Hide,
		Hover,
		Press,
		Release,
		TabFocus,
		LoseTabFocus
	}
	
	public enum EffectDirection {
		Top,
		Bottom,
		Left,
		Right
	}
	
	private Element element;
	private EffectType type;
	private EffectEvent event;
	private float pass = 0.0f;
	private boolean direction = true;
	private float duration;
	private boolean isActive = true;
	private boolean localActive = true;
	private Texture blendImage;
	private ColorRGBA blendColor;
	private boolean init = false;
	private boolean destroyOnHide = false;
	private EffectDirection effectDir = EffectDirection.Top;
	private Vector2f def = new Vector2f();
	private Vector2f diff = new Vector2f();
	private Vector2f fract = new Vector2f();
	private String audioFile = null;
	private float audioVolume = 1;
	
	public Effect(EffectType type, EffectEvent event, float duration) {
		this(type, event, duration, null, 1);
	//	this.type = type;
	//	this.event = event;
	//	this.duration = duration;
	}
	
	public Effect (EffectType type, EffectEvent event, float duration, String audioFile, float audioVolume) {
		this.type = type;
		this.event = event;
		this.duration = duration;
		this.audioFile = audioFile;
		this.audioVolume = audioVolume;
	}
	
	public void setElement(Element element) {
		this.element = element;
	}
	
	public void setBlendImage(Texture blendImage) {
		this.blendImage = blendImage;
	}
	
	public void setColor(ColorRGBA blendColor) {
		this.blendColor = blendColor;
	}
	
	public Element getElement() {
		return this.element;
	}
	
	public float getDuration() {
		return this.duration;
	}
	
	public boolean getIsActive() {
		return this.isActive;
	}
	
	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	public EffectEvent getEffectEvent() {
		return this.event;
	}
	
	public EffectType getEffectType() {
		return this.type;
	}
	
	public void setEffectDirection(EffectDirection effectDir) {
		this.effectDir = effectDir;
	}
	
	public EffectDirection getEffectDirection() {
		return this.effectDir;
	}
	
	public void setDestroyOnHide(boolean destroyOnHide) {
		this.destroyOnHide = destroyOnHide;
	}
	
	public void setAudioFile(String audioFile) {
		this.audioFile = audioFile;
	}
	
	public String getAudioFile() {
		return this.audioFile;
	}
	
	public void setAudioVolume(float audioVolume) {
		this.audioVolume = audioVolume;
	}
	
	public float getAudioVolume() {
		return this.audioVolume;
	}
	
	public void update(float tpf) {
		if (type == EffectType.ZoomIn) {
			if (!init) {
				def.set(element.getPosition().clone());
				diff.set(element.getWidth()/2,element.getHeight()/2);
				Vector2f inc = new Vector2f(diff.x*pass,diff.y*pass);
				element.setPosition(def.add(diff.subtract(inc)));
				element.setLocalScale(pass);
				element.show();
				init = true;
			} else if (localActive) {
				Vector2f inc = new Vector2f(diff.x*pass,diff.y*pass);
				element.setPosition(def.add(diff.subtract(inc)));
				element.setLocalScale(pass);
			}
			if (pass >= 1.0) {
				element.setPosition(def);
				element.setLocalScale(pass);
				isActive = false;
			}
		} else if (type == EffectType.ZoomOut) {
			if (!init) {
				def.set(element.getPosition().clone());
				diff.set(element.getWidth()/2,element.getHeight()/2);
				Vector2f inc = new Vector2f(diff.x*pass,diff.y*pass);
				element.setPosition(def.add(inc));
				element.setLocalScale(1-pass);
				init = true;
			} else if (localActive) {
				Vector2f inc = new Vector2f(diff.x*pass,diff.y*pass);
				element.setPosition(def.add(inc));
				element.setLocalScale(1-pass);
			}
			if (pass >= 1.0) {
				if (!destroyOnHide) {
					element.hide();
					element.setPosition(def);
					element.setLocalScale(0);
					isActive = false;
				} else {
					destoryElement();
					isActive = false;
				}
			}
		} else if (type == EffectType.SlideIn) {
			if (!init) {
				initSlides();
				updateSlideIn();
				element.show();
				init = true;
			} else if (localActive) {
				updateSlideIn();
			}
			if (pass >= 1.0) {
				element.setPosition(def);
				element.setLocalScale(pass);
				isActive = false;
			}
		} else if (type == EffectType.SlideOut) {
			if (!init) {
				initSlides();
				updateSlideOut();
				init = true;
			} else if (localActive) {
				updateSlideOut();
			}
			if (pass >= 1.0) {
				if (!destroyOnHide) {
					element.hide();
					element.setPosition(def);
					isActive = false;
				} else {
					destoryElement();
					isActive = false;
				}
			}
		} else if (type == EffectType.SpinIn) {
			if (!init) {
				def.set(element.getPosition().clone());
				diff.set(element.getWidth()/2,element.getHeight()/2);
				Vector2f inc = new Vector2f(diff.x*pass,diff.y*pass);
				element.setPosition(def.add(diff.subtract(inc)));
				element.setLocalScale(pass);
				element.show();
				init = true;
			} else if (localActive) {
				Vector2f inc = new Vector2f(diff.x*pass,diff.y*pass);
				element.setPosition(def.add(diff.subtract(inc)));
				element.setLocalScale(pass);
			}
			if (pass >= 1.0) {
				element.setPosition(def);
				element.setLocalScale(pass);
				isActive = false;
			}
			element.setLocalRotation(element.getLocalRotation().fromAngles(0, 0, 360*FastMath.DEG_TO_RAD*pass));
		} else if (type == EffectType.SpinOut) {
			if (!init) {
				def.set(element.getPosition().clone());
				diff.set(element.getWidth()/2,element.getHeight()/2);
				Vector2f inc = new Vector2f(diff.x*pass,diff.y*pass);
				element.setPosition(def.subtract(inc));
				element.setLocalScale(1-pass);
				init = true;
			} else if (localActive) {
				Vector2f inc = new Vector2f(diff.x*pass,diff.y*pass);
				element.setPosition(def.subtract(inc));
				element.setLocalScale(1-pass);
			}
			if (pass >= 1.0) {
				if (!destroyOnHide) {
					element.hide();
					element.setPosition(def);
					element.setLocalScale(1);
					isActive = false;
				} else {
					destoryElement();
					isActive = false;
				}
			}
			element.setLocalRotation(element.getLocalRotation().fromAngles(0, 0, 360*FastMath.DEG_TO_RAD*(1.0f-pass)));
		} else if (type == EffectType.FadeIn) {
			if (!init) {
				element.getElementMaterial().setBoolean("UseEffect", true);
				element.getElementMaterial().setBoolean("EffectFade", true);
				element.getElementMaterial().setBoolean("EffectPulse", false);
			//	element.getElementMaterial().setTexture("EffectMap", blendImage);
				element.show();
				init = true;
			}
			if (localActive)
				element.getElementMaterial().setFloat("EffectStep", pass);
			if (pass >= 1.0f)
				isActive = false;
		} else if (type == EffectType.FadeOut) {
			if (!init) {
				element.getElementMaterial().setBoolean("UseEffect", true);
				element.getElementMaterial().setBoolean("EffectFade", true);
				element.getElementMaterial().setBoolean("EffectPulse", false);
			//	element.getElementMaterial().setTexture("EffectMap", blendImage);
				init = true;
			}
			if (pass >= 1.0) {
				if (!destroyOnHide) {
					element.hide();
					element.getElementMaterial().setBoolean("UseEffect", false);
					element.getElementMaterial().setBoolean("EffectFade", false);
					element.getElementMaterial().setBoolean("EffectPulse", false);
					isActive = false;
				} else {
					destoryElement();
					isActive = false;
				}
			} else
				element.getElementMaterial().setFloat("EffectStep", 1.0f-pass);
		} else if (type == EffectType.ImageSwap) {
			if (!init) {
				element.getElementMaterial().setBoolean("UseEffect", false);
				element.getElementMaterial().setBoolean("EffectFade", false);
				element.getElementMaterial().setBoolean("EffectPulse", false);
				element.getElementMaterial().setTexture("ColorMap", blendImage);
				element.getElementMaterial().setFloat("EffectStep", 1.0f);
				init = true;
				isActive = false;
			}
		} else if (type == EffectType.ColorSwap) {
			if (!init) {
				element.getElementMaterial().setBoolean("UseEffect", false);
				element.getElementMaterial().setBoolean("EffectFade", false);
				element.getElementMaterial().setBoolean("EffectPulse", false);
				element.getElementMaterial().setBoolean("EffectPulseColor", false);
				element.getElementMaterial().setColor("Color", blendColor);
				element.getElementMaterial().setFloat("EffectStep", 1.0f);
				init = true;
				isActive = false;
			}
		}

		if (isActive) {
			if (type != EffectType.Pulse && type != EffectType.PulseColor) {
				pass += tpf/duration;
				if (pass >= 1.0) {
					pass = 1.0f;
					localActive = false;
				}
			} else {
				if (type == EffectType.Pulse) {
					if (pass >= 1.0f) {
						direction = false;
					} else if (pass <= -1.0f) {
						direction = true;
					}
					if (direction) pass += tpf/duration;
					else pass -= tpf/duration;
					if (!init) {
						element.getElementMaterial().setBoolean("UseEffect", true);
						element.getElementMaterial().setBoolean("EffectFade", false);
						element.getElementMaterial().setBoolean("EffectPulse", true);
						element.getElementMaterial().setBoolean("EffectPulseColor", false);
						element.getElementMaterial().setTexture("EffectMap", blendImage);
						init = true;
					}
					element.getElementMaterial().setFloat("EffectStep", pass);
				} else if (type == EffectType.PulseColor) {
					if (pass >= 1.0f) {
						direction = false;
					} else if (pass <= -1.0f) {
						direction = true;
					}
					if (direction) pass += tpf/duration;
					else pass -= tpf/duration;
					if (!init) {
						element.getElementMaterial().setBoolean("UseEffect", true);
						element.getElementMaterial().setBoolean("EffectFade", false);
						element.getElementMaterial().setBoolean("EffectPulse", false);
						element.getElementMaterial().setBoolean("EffectPulseColor", true);
						element.getElementMaterial().setColor("EffectColor", blendColor);
						init = true;
					}
					element.getElementMaterial().setFloat("EffectStep", pass);
				}
			}
		}
	}
	
	private void destoryElement() {
		if (element.getElementParent() == null) {
			element.getScreen().removeElement(element);
		} else {
			element.getElementParent().removeChild(element);
		}
	}
	
	public void resetShader() {
		element.getElementMaterial().setBoolean("UseEffect", false);
		element.getElementMaterial().setBoolean("EffectFade", false);
		element.getElementMaterial().setBoolean("EffectPulse", false);
		element.getElementMaterial().setTexture("EffectMap", null);
		element.getElementMaterial().setFloat("EffectStep", 0.0f);
	}
	
	@Override
	public Effect clone() {
		Effect effect = new Effect(
			this.type,
			this.event,
			this.duration
		);
		effect.setElement(this.element);
		effect.setEffectDirection(this.effectDir);
		effect.setDestroyOnHide(this.destroyOnHide);
		return effect;
	}
	
	// Effect methods
	private void initSlides() {
		def.set(element.getPosition().clone());
		if (effectDir == EffectDirection.Bottom) {
			diff.set(0,element.getAbsoluteHeight());
		} else if (effectDir == EffectDirection.Top) {
			diff.set(0,element.getScreen().getHeight()-element.getAbsoluteY());
		} else if (effectDir == EffectDirection.Left) {
			diff.set(element.getAbsoluteWidth(),0);
		} else if (effectDir == EffectDirection.Right) {
			diff.set(element.getScreen().getWidth()-element.getAbsoluteX(),0);
		}
	}
	
	private void updateSlideIn() {
		Vector2f inc = new Vector2f(diff.x*pass,diff.y*pass);
		if (effectDir == EffectDirection.Bottom || effectDir == EffectDirection.Left) {
			element.setPosition(def.subtract(diff.subtract(inc)));
		} else if (effectDir == EffectDirection.Top || effectDir == EffectDirection.Right) {
			element.setPosition(def.add(diff.subtract(inc)));
		}
	}
	
	private void updateSlideOut() {
		Vector2f inc = new Vector2f(diff.x*(1-pass),diff.y*(1-pass));
		if (effectDir == EffectDirection.Bottom || effectDir == EffectDirection.Left) {
			element.setPosition(def.subtract(diff.subtract(inc)));
		} else if (effectDir == EffectDirection.Top || effectDir == EffectDirection.Right) {
			element.setPosition(def.add(diff.subtract(inc)));
		}
	}
}
