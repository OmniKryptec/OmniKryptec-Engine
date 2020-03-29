$define shader raytracer COMPUTE$

#version 430

layout(binding = 0, rgba32f) uniform image2D framebuffer;

uniform vec3 eye;
uniform vec3 ray00;
uniform vec3 ray01;
uniform vec3 ray10;
uniform vec3 ray11;
uniform float time;

layout (local_size_x = 8, local_size_y = 8) in;

layout (std430, binding = 1) buffer shader_data_t
{ 
	float data[];
} shader_data;

#define SIZE 3
#define BOX_SIZE 1

#define MAX_SCENE_BOUNDS 500.0
#define MAX_STEPS 100


struct box {
  vec3 min;
  vec3 max;
};

const box BIG_BOX = {vec3(0,0,0), vec3(SIZE*BOX_SIZE,SIZE*BOX_SIZE,SIZE*BOX_SIZE)};

const ivec3[] DIRECTIONS = {ivec3(1,0,0),ivec3(-1,0,0),ivec3(0,1,0),ivec3(0,-1,0),ivec3(0,0,1),ivec3(0,0,-1)};

struct hitinfo {
  vec2 lambda;
  vec3 col;
};

ivec3 positionToFloored(vec3 pos){
    return ivec3(floor(pos.xyz/BOX_SIZE));
}

int positionToArrayIndex(ivec3 bpos){
    return bpos.x + bpos.y * SIZE + bpos.z * SIZE * SIZE;
}

bool exists(ivec3 bpos){
    return bpos.x >= 0 && bpos.x < SIZE && bpos.y >= 0 && bpos.y < SIZE && bpos.z >= 0 && bpos.z < SIZE;
}

vec2 intersectBox(vec3 origin, vec3 dir, const box b) {
  vec3 tMin = (b.min - origin) / dir;
  vec3 tMax = (b.max - origin) / dir;
  vec3 t1 = min(tMin, tMax);
  vec3 t2 = max(tMin, tMax);
  float tNear = max(max(t1.x, t1.y), t1.z);
  float tFar = min(min(t2.x, t2.y), t2.z);
  return vec2(tNear, tFar);
}

bool intersectBoxes(vec3 origin, vec3 dir, out hitinfo info) {    
  float smallest = MAX_SCENE_BOUNDS;
  bool found = false;
  vec2 lambda = intersectBox(origin, dir, BIG_BOX);
  if (lambda.x > 0.0 && lambda.x < lambda.y ) {
    for(int i = 0; i < MAX_STEPS; i++) {
        vec3 pos = origin + lambda.x * dir;
        ivec3 bpos = positionToFloored(pos);
        int index = positionToArrayIndex(bpos);
        if(shader_data.data[index] > 0.5f){
            info.lambda = lambda;
            info.col = vec3(1,1,0);
            found = true;
            smallest = lambda.x;
            break;
        }else{
            ivec3 nextbpos;
            bool fuck = false;
            for(int k = 0; k<6; k++){
                nextbpos = bpos + DIRECTIONS[k];
                if(exists(nextbpos)){
                    box b = {vec3(nextbpos.xyz*BOX_SIZE), vec3(nextbpos.xyz*(1+BOX_SIZE))};
                    lambda = intersectBox(origin, dir, b);
                    if(lambda.x > 0.0 && lambda.x < lambda.y){
                        fuck = true;
                        break;
                    }
                }
            }
            if(!fuck){
                break;
            }
        }
    }
  }
  
  return found;
}

vec4 trace(vec3 origin, vec3 dir) {
  hitinfo i;
  if (intersectBoxes(origin, dir, i)) {
    return vec4(i.col, 1.0);
  }
  return vec4(0,0,0.1, 1.0);
}

void main(void) {
  ivec2 pix = ivec2(gl_GlobalInvocationID.xy);
  ivec2 size = imageSize(framebuffer);
  if (pix.x >= size.x || pix.y >= size.y) {
    return;
  }
  vec2 pos = vec2(pix) / vec2(size.x - 1, size.y - 1);
  vec3 dir = mix(mix(ray00, ray01, pos.y), mix(ray10, ray11, pos.y), pos.x);
  vec4 color = trace(eye, dir);
  imageStore(framebuffer, pix, color);
}

$define shader ca COMPUTE$

#version 430

uniform float time;

layout (local_size_x = 8, local_size_y = 8) in;

layout (std430, binding = 1) buffer shader_data_t
{ 
	vec4 color;
} shader_data;


void main(void) {
  ivec2 pix = ivec2(gl_GlobalInvocationID.xy);
  
}
