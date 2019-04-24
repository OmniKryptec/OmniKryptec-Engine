$define shader engineRenderBatch2DShader VERTEX$
#version 330 core

layout(location = 0) in vec2 i_pos;
layout(location = 1) in vec2 i_texcoords;
layout(location = 2) in vec4 i_color;

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

layout(location = 0) in vec2 i_pos;
layout(location = 1) in vec2 i_texcoords;
layout(location = 2) in vec4 i_color;
layout(location = 3) in vec4 i_reflective;

out vec4 v_color;
out vec2 v_texcoords;
out vec4 v_reflectiveness;
out vec2 v_screenpos;

uniform mat4 u_transform;
uniform mat4 u_projview;

void main(void){
	v_color = i_color;
	v_texcoords = i_texcoords;
	v_reflectiveness = i_reflective;
	vec4 pos = u_projview * u_transform * vec4(i_pos,0,1);
	
	v_screenpos = vec2(pos.x * 0.5f + 0.5f, pos.y * 0.5f + 0.5f);
	gl_Position = pos;
}

$define shader engineRenderBatch2DShaderRef FRAGMENT$
#version 330 core

in vec4 v_color;
in vec2 v_texcoords;
in vec4 v_reflectiveness;
in vec2 v_screenpos;

out vec4 color;

uniform sampler2D sampler;
uniform sampler2D reflected;

void main(void){
	
	if(v_texcoords.x == -1){
		color = v_color;
	}else{
		color =  v_color * texture(sampler, v_texcoords);
	}
	vec4 refl = texture(reflected, v_screenpos);
	color = (1 - v_reflectiveness) * color + v_reflectiveness * refl;
}