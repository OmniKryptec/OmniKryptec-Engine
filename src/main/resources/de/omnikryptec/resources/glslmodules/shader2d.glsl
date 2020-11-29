$define shader gurke VERTEX$
#version 330 core

layout(location = 0) in vec2 i_pos;//[0,1]x[0,1]
layout(location = 1) in mat2 i_rotate;
layout(location = 3) in vec2 i_translate;
layout(location = 4) in vec4 i_texcoords;
layout(location = 5) in vec4 i_color;
layout(location = 6) in float i_texIndex;

out vec2 v_tex;
flat out vec4 v_color;
flat out int v_texIndex;

uniform mat4 u_projview;

void main(void){
    float x = mix(i_texcoords.x, i_texcoords.z, i_pos.x);
    float y = mix(i_texcoords.y, i_texcoords.w, i_pos.y);
    v_tex = vec2(x, y);
    vec2 pos = i_translate + i_rotate * i_pos;
    gl_Position = u_projview * vec4(pos, 0, 1);
    v_color = i_color;
    v_texIndex = int(i_texIndex);
}

$define shader gurke FRAGMENT$
#version 330 core

in vec2 v_tex;
flat in vec4 v_color;
flat in int v_texIndex;

out vec4 color;

uniform sampler2D samplers[8]; 

void main(void){
    color = texture(samplers[v_texIndex], v_tex) * v_color;
}


$define shader engineRenderBatch2DShader VERTEX$
#version 330 core

layout(location = 2) in vec2 i_pos;
layout(location = 3) in vec2 i_texcoords;
layout(location = 0) in vec4 i_color;
layout(location = 1) in float i_tiling;

out vec4 v_color;
out vec2 v_texcoords;
out float v_tiling;

uniform mat4 u_transform;
uniform mat4 u_projview;

void main(void){
    v_tiling = i_tiling;
	v_color = i_color;
	v_texcoords = i_texcoords;
	gl_Position = u_projview * u_transform * vec4(i_pos,0,1);
}

$define shader engineRenderBatch2DShader FRAGMENT$
#version 330 core

in vec4 v_color;
in vec2 v_texcoords;
in float v_tiling;

out vec4 color;

uniform sampler2D sampler;


void main(void){
	color = v_color * texture(sampler, v_texcoords * v_tiling);
}


$define shader engineRenderBatch2DShaderRef VERTEX$
#version 330 core

layout(location = 6) in vec2 i_pos;
layout(location = 7) in vec2 i_texcoords;
layout(location = 0) in vec4 i_color;
layout(location = 1) in vec4 i_reflective;
layout(location = 2) in vec4 i_borderColor;
layout(location = 3) in vec4 i_sdData;
layout(location = 4) in vec2 i_sdOffset;
layout(location = 5) in float i_tiling;

out vec4 v_color;
out vec2 v_texcoords;
out vec4 v_reflectiveness;
out vec2 v_screenPos;
out vec4 v_borderColor;
out vec4 v_sdData;
out vec2 v_sdOffset;
out float v_tiling;

uniform mat4 u_transform;
uniform mat4 u_projview;

void main(void){
	v_color = i_color;
	v_texcoords = i_texcoords;
	v_reflectiveness = i_reflective;
	v_sdData = i_sdData;
	v_sdOffset = i_sdOffset;
	v_borderColor = i_borderColor;
	v_tiling = i_tiling;
	vec4 pos = u_projview * u_transform * vec4(i_pos,0,1);
	v_screenPos = (pos.xy + 1) * 0.5;
	gl_Position = pos;
}

$define shader engineRenderBatch2DShaderRef FRAGMENT$
#version 330 core

in vec4 v_color;
in vec2 v_texcoords;
in vec4 v_reflectiveness;
in vec2 v_screenPos;
in vec4 v_borderColor;
in vec4 v_sdData;
in vec2 v_sdOffset;
in float v_tiling;

out vec4 color;

uniform sampler2D sampler;
uniform sampler2D reflected;


void main(void){
	float dCol = v_color.a;
	color = v_color * texture(sampler, v_texcoords * v_tiling);
	dCol = v_color.a * texture(sampler, v_texcoords * v_tiling + v_sdOffset).a;
    
	vec3 refl = texture(reflected, v_screenPos).rgb;
	
	float dist = 1.0 - color.a;
	float alpha = 1.0 - smoothstep(v_sdData.x, v_sdData.y, dist);
	
	float dist2 = 1.0 - dCol;
	float outlineAlpha = 1.0 - smoothstep(v_sdData.z, v_sdData.w, dist2);
	
	float overallAlpha = alpha + (1.0 - alpha) * outlineAlpha;

	color.a = alpha;	
    color = mix(v_borderColor, color, alpha/overallAlpha);
    
	//TODO
	color.rgb = color.rgb + v_reflectiveness.rgb * refl.rgb;
}
