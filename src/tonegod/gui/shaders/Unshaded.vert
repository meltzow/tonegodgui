uniform mat4 g_WorldViewProjectionMatrix;
uniform mat4 g_WorldViewMatrix;
attribute vec3 inPosition;
varying vec4 pos;

attribute vec2 inTexCoord;
attribute vec4 inColor;
varying vec2 texCoord1;
varying vec4 vertColor;

void main(){
    pos = g_WorldViewMatrix * vec4(inPosition, 1.0);
	
	texCoord1 = inTexCoord;

    #ifdef HAS_VERTEXCOLOR
        vertColor = inColor;
    #endif

    gl_Position = g_WorldViewProjectionMatrix * vec4(inPosition, 1.0);
}