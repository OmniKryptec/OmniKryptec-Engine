#version 150

out vec4 out_colour;

in vec2 blurTextureCoords[11];

uniform sampler2D tex;

void main(void){
	
	out_colour = vec4(0.0);
	out_colour += texture(tex, blurTextureCoords[0]) * 0.0093;
    out_colour += texture(tex, blurTextureCoords[1]) * 0.028002;
    out_colour += texture(tex, blurTextureCoords[2]) * 0.065984;
    out_colour += texture(tex, blurTextureCoords[3]) * 0.121703;
    out_colour += texture(tex, blurTextureCoords[4]) * 0.175713;
    out_colour += texture(tex, blurTextureCoords[5]) * 0.198596;
    out_colour += texture(tex, blurTextureCoords[6]) * 0.175713;
    out_colour += texture(tex, blurTextureCoords[7]) * 0.121703;
    out_colour += texture(tex, blurTextureCoords[8]) * 0.065984;
    out_colour += texture(tex, blurTextureCoords[9]) * 0.028002;
    out_colour += texture(tex, blurTextureCoords[10]) * 0.0093;

}