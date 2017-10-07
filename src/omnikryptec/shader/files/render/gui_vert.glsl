#version 330

in vec2 pos;
in vec4 rgba;
in vec2 uv;

out vec4 col;
out vec2 tex;

void main(void){
	
	gl_Position = vec4(pos, 0.0, 1.0);
	col = rgba;
	tex = uv;
}