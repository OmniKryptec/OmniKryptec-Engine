#version 330

in vec2 textureCoords;

out vec4 color;

uniform sampler2D tex;
uniform sampler2D depth;

uniform vec4 fog;

uniform float density;
uniform float gradient;

uniform vec2 pixelSize;

uniform mat4 invprojv;

uniform vec3 campos;

void main(void){
	vec3 pos = vec3(( gl_FragCoord.x * pixelSize.x),
                   (gl_FragCoord.y * pixelSize.y), 0.0);
	pos.z = texture(depth, textureCoords).r;
	
	vec4 clip = invprojv * vec4(pos * 2.0 - 1.0,  1.0);
	pos = clip.xyz / clip.w;
	float len = length(pos-campos);
	
	color = texture(tex, textureCoords);
	float ff = exp(-pow((len*density), gradient));
	color = mix(fog,color,ff);
}