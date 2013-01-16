#if defined(HAS_COLORMAP) || defined(HAS_ALPHAMAP) || (defined(HAS_LIGHTMAP) && !defined(SEPARATE_TEXCOORD))
    #define NEED_TEXCOORD1
#endif

uniform float g_Time;
const float pi = 3.14159;

uniform mat4 g_WorldViewProjectionMatrix;
uniform mat4 g_WorldViewMatrix;
attribute vec3 inPosition;
varying vec4 pos;

attribute vec2 inTexCoord;
attribute vec2 inTexCoord2;
attribute vec4 inColor;

varying vec2 texCoord1;
varying vec2 texCoord2;

varying vec4 vertColor;

void main(){
    pos = g_WorldViewMatrix * vec4(inPosition, 1.0);
	
	#ifdef NEED_TEXCOORD1
        texCoord1 = inTexCoord;
    #endif

    #ifdef SEPARATE_TEXCOORD
        texCoord2 = inTexCoord2;
    #endif

    #ifdef HAS_VERTEXCOLOR
        vertColor = inColor;
    #endif

    gl_Position = g_WorldViewProjectionMatrix * vec4(inPosition, 1.0);
}