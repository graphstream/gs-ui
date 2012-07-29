#version 120

attribute vec3 P;

uniform mat4 MVP;

void main(void) {
	gl_Position = MVP * vec4(P, 1.0);
}