import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.lwjgl.util.glu.GLU;
import shaders.ShaderUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Model {
	public float[] verts = {
		-0.5f, 0.5f, 0f,
		-0.5f, -0.5f, 0f,
		0.5f, -0.5f, 0f,
		0.5f, 0.5f, 0f
	};

	public int[] indices = {
		0, 1, 2,
		2, 3, 0
	};

	public float[] colors = {
			1f, 0f, 0f, 1f,
			0f, 1f, 0f, 1f,
			0f, 1f, 0f, 1f,
			0f, 0f, 1f, 1f,
			1f, 1f, 1f, 1f
	};

	public int vao_vertices_ID = 0;

	public int vbo_vertices_Id = 0;

	public int vbo_indices_ID = 0;

	public int vbo_colors_ID = 0;

	public int pId = 0;

	public IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.length);

	public FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(verts.length);

	public FloatBuffer colorsBuffer = BufferUtils.createFloatBuffer(colors.length);

	public int vsId = ShaderUtils.loadShader("res/shaders/sprites.vert", GL20.GL_VERTEX_SHADER);

	public int fsId = ShaderUtils.loadShader("res/shaders/sprites.frag", GL20.GL_FRAGMENT_SHADER);

	public void create(){
		System.out.println("Buffers");
		verticesBuffer.put(verts);
		verticesBuffer.flip();
		indicesBuffer.put(indices);
		indicesBuffer.flip();
		colorsBuffer.put(colors);
		colorsBuffer.flip();

		// Create a new Vertex Array Object in memory and select it (bind)
		vao_vertices_ID = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao_vertices_ID);

		System.out.println("Verts");
		// Create a new Vertex Buffer Object in memory and select it (bind) - VERTICES
		vbo_vertices_Id = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo_vertices_Id);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		System.out.println("Colors");
		// Create a new VBO for the indices and select it (bind) - COLORS
		vbo_colors_ID = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo_colors_ID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, colorsBuffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(1, 4, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		// Deselect (bind to 0) the VAO
		GL30.glBindVertexArray(0);

		System.out.println("Inds");
		// Create a new VBO for the indices and select it (bind) - INDICES
		vbo_indices_ID = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo_indices_ID);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		setupShaders();
	}

	public void draw() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

		GL20.glUseProgram(pId);

		// Bind to the VAO that has all the information about the vertices
		GL30.glBindVertexArray(vao_vertices_ID);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);

		// Bind to the index VBO that has all the information about the order of the vertices
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo_indices_ID);

		// Draw the vertices
		GL11.glDrawElements(GL11.GL_TRIANGLES, indices.length, GL11.GL_UNSIGNED_INT, 0);

		// Put everything back to default (deselect)
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
		GL20.glUseProgram(0);
	}

	public void delete() {
		// Delete the shaders
		GL20.glUseProgram(0);
		GL20.glDetachShader(pId, vsId);
		GL20.glDetachShader(pId, fsId);

		GL20.glDeleteShader(vsId);
		GL20.glDeleteShader(fsId);
		GL20.glDeleteProgram(pId);
		// Delete the color VBO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(vbo_colors_ID);
	}

	private void setupShaders() {
		System.out.println("Setting up Shaders");
		int errorCheckValue = GL11.glGetError();

//		// Load the vertex shader
//		vsId = ShaderUtils.loadShader("/res/shaders/sprites.vert", GL20.GL_VERTEX_SHADER);
//		// Load the fragment shader
//		fsId = ShaderUtils.loadShader("/res/shaders/ssprites.flac", GL20.GL_FRAGMENT_SHADER);

		// Create a new shader program that links both shaders
		pId = GL20.glCreateProgram();
		GL20.glAttachShader(pId, vsId);
		GL20.glAttachShader(pId, fsId);

		// Position information will be attribute 0
		GL20.glBindAttribLocation(pId, 0, "in_Position");
		// Color information will be attribute 1
		GL20.glBindAttribLocation(pId, 1, "in_Color");

		GL20.glLinkProgram(pId);
		GL20.glValidateProgram(pId);

		errorCheckValue = GL11.glGetError();
		if (errorCheckValue != GL11.GL_NO_ERROR) {
			System.out.println("ERROR - Could not create the shaders:" + GLU.gluErrorString(errorCheckValue));
			System.exit(-1);
		}
	}
}
