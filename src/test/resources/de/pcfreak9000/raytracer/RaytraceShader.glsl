$define shader raytracer COMPUTE$

#version 430 core

layout(binding = 0, rgba32f) uniform image2D framebuffer;

layout (local_size_x = 8, local_size_y = 8) in;

void main(void){
	ivec2 pix = ivec2(gl_GlobalInvocationID.xy);
	ivec2 size = imageSize(framebuffer);
	if (pix.x >= size.x || pix.y >= size.y) {
		return;
	}
	vec2 lul = vec2(pix.xy);
	imageStore(framebuffer, pix, vec4(lul.x/size.x,0,lul.y/size.y,1));
}