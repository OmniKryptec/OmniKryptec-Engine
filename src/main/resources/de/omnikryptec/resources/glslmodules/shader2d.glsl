$define shader engineRenderBatch2DShader VERTEX$
#version 330 core

layout(location = 1) in vec2 i_pos;
layout(location = 2) in vec2 i_texcoords;
layout(location = 0) in vec4 i_color;


out vec4 v_color;
out vec2 v_texcoords;

uniform mat4 u_transform;
uniform mat4 u_projview;

void main(void){
	v_color = i_color;
	v_texcoords = i_texcoords;
	gl_Position = u_projview * u_transform * vec4(i_pos,0,1);
}

$define shader engineRenderBatch2DShader FRAGMENT$
#version 330 core

in vec4 v_color;
in vec2 v_texcoords;

out vec4 color;

uniform sampler2D sampler;

void main(void){
	
	if(v_texcoords.x == -1){
		color = v_color;
	}else{
		color =  v_color * texture(sampler, v_texcoords);
	}
	
}


$define shader engineRenderBatch2DShaderRef VERTEX$
#version 330 core

layout(location = 2) in vec2 i_pos;
layout(location = 3) in vec2 i_texcoords;
layout(location = 0) in vec4 i_color;
layout(location = 1) in vec4 i_reflective;

out vec4 v_color;
out vec2 v_texcoords;
out vec4 v_reflectiveness;
out vec2 v_screenPos;

uniform mat4 u_transform;
uniform mat4 u_projview;

void main(void){
	v_color = i_color;
	v_texcoords = i_texcoords;
	v_reflectiveness = i_reflective;
	vec4 pos = u_projview * u_transform * vec4(i_pos,0,1);
	v_screenPos = (pos.xy+1)*0.5;
	gl_Position = pos;
}

$define shader engineRenderBatch2DShaderRef FRAGMENT$
#version 330 core

in vec4 v_color;
in vec2 v_texcoords;
in vec4 v_reflectiveness;
in vec2 v_screenPos;

out vec4 color;

uniform sampler2D sampler;
uniform sampler2D reflected;

uniform vec2 signedDistanceData;
uniform vec2 borderData;

uniform vec2 borderOffset;

uniform vec4 borderColor;

void main(void){
	float dCol = v_color.a;
	
	if(v_texcoords.x == -1){
		color = v_color;
	}else{
		color =  v_color * texture(sampler, v_texcoords);
		dCol = v_color.a * texture(sampler, v_texcoords + borderOffset).a;
    }
	vec3 refl = texture(reflected, v_screenPos).rgb;
	
	float dist = 1.0 - color.a;
	float alpha = 1.0 - smoothstep(signedDistanceData.x, signedDistanceData.y, dist);
	
	float dist2 = 1.0 - dCol;
	float outlineAlpha = 1.0 - smoothstep(borderData.x, borderData.y, dist2);
	
	float overallAlpha = alpha + (1.0 - alpha) * outlineAlpha;

	color.a = alpha;	
    color = mix(borderColor, color, alpha/overallAlpha);
    
	//TODO
	color.rgb = color.rgb + v_reflectiveness.rgb * refl.rgb;
}
