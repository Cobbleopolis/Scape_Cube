import model.Vertex;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.lwjgl.util.glu.GLU;
import shaders.ShaderUtils;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Model {

	final int floatByteSize = 4;

	final int vertexFloatCount = 3;

	final int colorFloatCount = 4;

	final int floatsPerVertex = vertexFloatCount + colorFloatCount;

	final int vertexFloatSizeInBytes = floatsPerVertex * floatByteSize;


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
			0f, 0f, 1f, 1f,
			1f, 1f, 1f, 1f
	};

	public int vaoId = 0;

	public int vbo_inter_Id = 0;

	public int vbo_indices_Id = 0;

	public int pId = 0;

	public IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.length);

	public int vsId = ShaderUtils.loadShader("res/shaders/sprites.vert", GL20.GL_VERTEX_SHADER);

	public int fsId = ShaderUtils.loadShader("res/shaders/sprites.frag", GL20.GL_FRAGMENT_SHADER);

	public void create(){
		System.out.println("Buffers");
		// We'll define our quad using 4 vertices of the custom 'Vertex' class
		Vertex v0 = new Vertex(); v0.setXYZ(-0.5f, 0.5f, 0f); v0.setRGB(1, 0, 0);
		Vertex v1 = new Vertex(); v1.setXYZ(-0.5f, -0.5f, 0f); v1.setRGB(0, 1, 0);
		Vertex v2 = new Vertex(); v2.setXYZ(0.5f, -0.5f, 0f); v2.setRGB(0, 0, 1);
		Vertex v3 = new Vertex(); v3.setXYZ(0.5f, 0.5f, 0f); v3.setRGB(1, 1, 1);

		Vertex[] vertices = new Vertex[] {v0, v1, v2, v3};
		// Put each 'Vertex' in one FloatBuffer
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length *
				Vertex.elementCount);
		for (int i = 0; i < vertices.length; i++) {
			verticesBuffer.put(vertices[i].getXYZW());
			verticesBuffer.put(vertices[i].getRGBA());
		}

		verticesBuffer.flip();

		indicesBuffer.put(indices);
		indicesBuffer.flip();

		// Create a new Vertex Array Object in memory and select it (bind)
		vaoId = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoId);

		// Create a new Vertex Buffer Object in memory and select it (bind)
		vbo_inter_Id = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo_inter_Id);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);
		// Put the positions in attribute list 0
		GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, Vertex.sizeInBytes, 0);
		// Put the colors in attribute list 1
		GL20.glVertexAttribPointer(1, 4, GL11.GL_FLOAT, false, Vertex.sizeInBytes,
				Vertex.elementBytes * 4);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		// Deselect (bind to 0) the VAO
		GL30.glBindVertexArray(0);

		// Create a new VBO for the indices and select it (bind) - INDICES
		vbo_indices_Id = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo_indices_Id);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		setupShaders();
	}

	public void draw() {

		GL20.glUseProgram(pId);

		// Bind to the VAO that has all the information about the vertices
		GL30.glBindVertexArray(vaoId);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);

		// Bind to the index VBO that has all the information about the order of the vertices
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo_indices_Id);

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

		// Select the VAO
		GL30.glBindVertexArray(vaoId);

		// Disable the VBO index from the VAO attributes list
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);

		// Delete the vertex VBO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(vbo_inter_Id);

		// Delete the index VBO
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(vbo_indices_Id);

		// Delete the VAO
		GL30.glBindVertexArray(0);
		GL30.glDeleteVertexArrays(vaoId);
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
