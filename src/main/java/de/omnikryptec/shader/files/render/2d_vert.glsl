#version 330

in vec2 pos;
in vec4 rgba;
in vec2 uv;

out vec4 col;
out vec2 tex;

uniform mat4 projview;

void main(void){
	
	gl_Position =  projview * vec4(pos, $2D_Z_OFFSET$, 1.0);
	col = rgba;
	tex = uv;
}