#ifdef HAS_ALPHAMAP
	uniform sampler2D m_AlphaMap;
#endif

uniform float g_Time;

varying vec4 pos;
uniform bool m_UseClipping;
uniform vec4 m_Clipping;

uniform sampler2D m_EffectMap;
uniform vec4 m_EffectColor;
uniform bool m_UseEffect;
uniform bool m_EffectFade;
uniform bool m_EffectPulse;
uniform bool m_EffectPulseColor;
uniform float m_EffectStep;
uniform float m_GlobalAlpha;

#ifdef IS_TEXTFIELD
uniform bool m_IsTextField;
uniform bool m_HasTabFocus;
uniform float m_CaretX;
uniform float m_CaretSpeed;
uniform float m_LastUpdate;
uniform bool m_ShowTextRange;
uniform float m_TextRangeStart;
uniform float m_TextRangeEnd;
#endif

uniform vec4 m_Color;
uniform sampler2D m_ColorMap;

varying vec2 texCoord1;

varying vec4 vertColor;

vec3 altMix(in vec3 color1, in vec3 color2, in float alpha) {
	return (color1.rgb * vec3(1.0-alpha) + color2.rgb * vec3(alpha));
}

void main(){
	if (m_UseClipping) {
		if (pos.x < m_Clipping.x || pos.x > m_Clipping.z || 
			pos.y < m_Clipping.y || pos.y > m_Clipping.w) {
			discard;
		}
	}
	
	vec4 color = vec4(1.0);
	
	#ifdef HAS_COLORMAP
        color *= texture2D(m_ColorMap, texCoord1);
		
		if (m_UseEffect) {
			if (m_EffectPulse) {
				vec4 mixColor;
				mixColor = texture2D(m_EffectMap, texCoord1);
				color.rgb = altMix(color.rgb, mixColor.rgb, m_EffectStep);
			//	color.rgb = color.rgb * vec3(1.0-m_EffectStep) + mixColor.rgb * vec3(m_EffectStep);
			//	color = mix(color, texture2D(m_EffectMap, texCoord1), m_EffectStep);
			} else if (m_EffectFade) {
				color.a *= m_EffectStep;
			} else if (m_EffectPulseColor) {
				color.rgb =  altMix(color.rgb, m_EffectColor.rgb, m_EffectStep*0.5);
			} else {
				vec4 mixColor;
				mixColor = texture2D(m_EffectMap, texCoord1);
				color.rgb = altMix(color.rgb, mixColor.rgb, 1.0);
			//	color = mix(color, texture2D(m_EffectMap, texCoord1), 1.0);
			}
		}
	#endif
	
	#ifdef HAS_COLOR
		color *= m_Color;
	#endif
	
	#ifdef HAS_VERTEXCOLOR
		color *= vertColor;
	#endif
	
	#ifdef IS_TEXTFIELD
	if (m_ShowTextRange) {
		float trStart;
		float trEnd;
		if (m_TextRangeStart < m_TextRangeEnd) {
			trStart = m_TextRangeStart;
			trEnd = m_TextRangeEnd;
		} else {
			trStart = m_TextRangeEnd;
			trEnd = m_TextRangeStart;
		}
		if (pos.x >= trStart && pos.x <= trEnd) {
			color = vec4(0.0,0.0,1.0,0.5);
		}
	}
	if (m_HasTabFocus) {
		if (g_Time-m_LastUpdate > 0.25) {
			if (pos.x > m_CaretX-1.0 && pos.x < m_CaretX+1.0) {
				color = m_Color;
				color.a = sin((g_Time-m_LastUpdate)*m_CaretSpeed);
			} else {
				if (color == m_Color)
					color = vec4(0.0);
			}
		} else {
			if (pos.x > m_CaretX-1.0 && pos.x < m_CaretX+1.0) {
				color = m_Color;
			} else {
				if (color == m_Color)
					color = vec4(0.0);
			}
		}
	} else {
		color.a = 0.0;
	}
	#endif
	
	#if defined(HAS_ALPHAMAP)
		color.a *= texture2D(m_AlphaMap, texCoord1).r;
	#endif
	
	color.a *= m_GlobalAlpha;
	
    gl_FragColor = color;
}