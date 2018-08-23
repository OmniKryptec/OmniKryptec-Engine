package de.omnikryptec.resource.model;

import de.omnikryptec.graphics.OpenGL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class VertexArrayObject {

	private static final int BYTES_PER_FLOAT = 4;
	private static final int BYTES_PER_INT = 4;

	private static int lastBoundId = -1;

	public final int ID;
	private VertexBufferObject dataVboTmp;
	private VertexBufferObject indexVbo;
	private int indexCount;

	private static final List<VertexArrayObject> vaos = new ArrayList<>();

	/**
	 * Creates a new empty VertexArrayObject
	 * 
	 * @return VertexArrayObject VertexArrayObject
	 */
	public static final VertexArrayObject create() {
		int id = OpenGL.gl30genVertexArrays();
		return new VertexArrayObject(id);
	}

	private VertexArrayObject(int id) {
		this.ID = id;
		vaos.add(this);
	}

	/**
	 * Returns the number of indices
	 * 
	 * @return Integer Index Count
	 */
	public final int getIndexCount() {
		return indexCount;
	}

	/**
	 * Binds the VertexArrayObject to OpenGL
	 * 
	 * @return A reference to this VertexArrayObject
	 */
	public final VertexArrayObject bind() {
		if (ID != lastBoundId) {
			OpenGL.gl30bindVertexArray(ID);
			lastBoundId = ID;
		}
		return this;
	}

	/**
	 * Binds the given Attributes to OpenGL
	 * 
	 * @param attributes
	 *            Integer Array Attributes
	 * @return A reference to this VertexArrayObject
	 */
	public final VertexArrayObject bind(int... attributes) {
		bind();
		for (int i : attributes) {
			OpenGL.gl20enableVertexAttribArray(i);
		}
		return this;
	}

	/**
	 * Unbinds this VertexArrayObject
	 * 
	 * @return A reference to this VertexArrayObject
	 * @deprecated Not even deprecated
	 */
	@Deprecated
	public final VertexArrayObject unbind() {
		lastBoundId = 0;
		OpenGL.gl30bindVertexArray(0);
		return this;
	}

	/**
	 * Unbinds the given Attributes
	 * 
	 * @param attributes
	 *            Integer Array Attributes
	 * @return A reference to this VertexArrayObject
	 * @deprecated Not even deprecated
	 */
	@Deprecated
	public final VertexArrayObject unbind(int... attributes) {
		for (int i : attributes) {
			OpenGL.gl20disableVertexAttribArray(i);
		}
		unbind();
		return this;
	}

	/**
	 * Stores data into the VertexArrayObject
	 * 
	 * @param indices
	 *            Integer Array Indices
	 * @param vertexCount
	 *            Integer Number of verticies
	 * @param data
	 *            Float Array Array Data
	 * @return A reference to this VertexArrayObject
	 */
	public final VertexArrayObject storeDataf(int[] indices, int vertexCount, float[]... data) {
		bind();
		storeDataf(vertexCount, data);
		createIndexBuffer(indices);
		return this;
	}

	public final VertexArrayObject storeData(int[] indices, int vertexCount, DataObject... data) {
		bind();
		storeData(vertexCount, data);
		createIndexBuffer(indices);
		return this;
	}

	/**
	 * Deletes this VertexArrayObject
	 * 
	 * @return A reference to this VertexArrayObject
	 */
	public final VertexArrayObject delete() {
		OpenGL.gl30deleteVertexArrays(ID);
		return this;
	}

	// *******************************************************************************************************************

	/**
	 * Creates an indices buffer
	 * 
	 * @param indices
	 *            Integer Array indices
	 * @return A reference to this VertexArrayObject
	 */
	public final VertexArrayObject createIndexBuffer(int[] indices) {
		this.indexVbo = VertexBufferObject.create(GL15.GL_ELEMENT_ARRAY_BUFFER);
		indexVbo.bind();
		indexVbo.storeData(indices);
		this.indexCount = indices.length;
		return this;
	}

	/**
	 * Creates a new Float Array Attribute
	 * 
	 * @param attribute
	 *            Integer Number of the Attribute
	 * @param data
	 *            Float Array Data
	 * @param attributeSize
	 *            Integer Number of following data
	 * @return A reference to this VertexArrayObject
	 */
	public final VertexArrayObject createAttribute(int attribute, float[] data, int attributeSize) {
		dataVboTmp = VertexBufferObject.create(GL15.GL_ARRAY_BUFFER);
		dataVboTmp.bind();
		dataVboTmp.storeData(data);
		OpenGL.gl20vertexAttribPointer(attribute, attributeSize, GL11.GL_FLOAT, false, attributeSize * BYTES_PER_FLOAT, 0);
		return this;
	}

	/**
	 * Creates a new Integer Array Attribute
	 * 
	 * @param attribute
	 *            Integer Number of the Attribute
	 * @param data
	 *            Integer Array Data
	 * @param attributeSize
	 *            Integer Number of following data
	 * @return A reference to this VertexArrayObject
	 */
	public final VertexArrayObject createIntAttribute(int attribute, int[] data, int attributeSize) {
		dataVboTmp = VertexBufferObject.create(GL15.GL_ARRAY_BUFFER);
		dataVboTmp.bind();
		dataVboTmp.storeData(data);
		OpenGL.gl30vertexAttribIPointer(attribute, attributeSize, GL11.GL_INT, attributeSize * BYTES_PER_INT, 0);
		return this;
	}
	// *******************************************************************************************************************

	public final VertexArrayObject storeData(int vertexCount, DataObject... data) {
		int[] lengths = getAttributeLengths(data, vertexCount);
		store(lengths, data);
		return this;
	}


	
	private final VertexArrayObject store(int[] lengths, DataObject[] data) {
		for (int i = 0; i < data.length; i++) {
			dataVboTmp = VertexBufferObject.create(GL15.GL_ARRAY_BUFFER);
			dataVboTmp.bind();
			data[i].store(dataVboTmp);
			if (data[i].holdsInt()) {
				OpenGL.gl30vertexAttribIPointer(i, lengths[i], data[i].getType(), lengths[i] * BYTES_PER_INT, 0);
			} else if (data[i].holdsFloat()) {
				OpenGL.gl20vertexAttribPointer(i, lengths[i], data[i].getType(), false, lengths[i] * BYTES_PER_FLOAT, 0);
			}
		}
		return this;
	}

	private final int[] getAttributeLengths(DataObject[] data, int vertexCount) {
		int[] lengths = new int[data.length];
		for (int i = 0; i < data.length; i++) {
			lengths[i] = data[i].getLength() / vertexCount;
		}
		return lengths;
	}

	private final VertexArrayObject storeDataf(int vertexCount, float[]... data) {
		float[] interleavedData = interleaveFloatData(vertexCount, data);
		int[] lengths = getAttributeLengthsf(data, vertexCount);
		storeInterleavedData(interleavedData, lengths);
		return this;
	}

	private final int[] getAttributeLengthsf(float[][] data, int vertexCount) {
		int[] lengths = new int[data.length];
		for (int i = 0; i < data.length; i++) {
			lengths[i] = data[i].length / vertexCount;
		}
		return lengths;
	}

	//TODO ?
	public final VertexArrayObject storeBufferf(int vertexcount, int[] lengths, FloatBuffer buffer) {
		if(dataVboTmp==null) {
			dataVboTmp = VertexBufferObject.create(GL15.GL_ARRAY_BUFFER);
		}
		dataVboTmp.bind();
		dataVboTmp.storeData(buffer);
		linkVboDataToAttributes(lengths, calculateBytesPerVertex(lengths));
		return this;
	}
	
	private final VertexArrayObject storeInterleavedData(float[] data, int... lengths) {
		dataVboTmp = VertexBufferObject.create(GL15.GL_ARRAY_BUFFER);
		dataVboTmp.bind();
		dataVboTmp.storeData(data);
		int bytesPerVertex = calculateBytesPerVertex(lengths);
		linkVboDataToAttributes(lengths, bytesPerVertex);
		return this;
	}

	private final VertexArrayObject linkVboDataToAttributes(int[] lengths, int bytesPerVertex) {
		int total = 0;
		for (int i = 0; i < lengths.length; i++) {
			OpenGL.gl20vertexAttribPointer(i, lengths[i], GL11.GL_FLOAT, false, bytesPerVertex, BYTES_PER_FLOAT * total);
			total += lengths[i];
		}
		return this;
	}

	private final int calculateBytesPerVertex(int[] lengths) {
		int total = 0;
		for (int i = 0; i < lengths.length; i++) {
			total += lengths[i];
		}
		return BYTES_PER_FLOAT * total;
	}

	private final float[] interleaveFloatData(int count, float[]... data) {
		int totalSize = 0;
		int[] lengths = new int[data.length];
		for (int i = 0; i < data.length; i++) {
			int elementLength = data[i].length / count;
			lengths[i] = elementLength;
			totalSize += data[i].length;
		}
		float[] interleavedBuffer = new float[totalSize];
		int pointer = 0;
		for (int i = 0; i < count; i++) {
			for (int j = 0; j < data.length; j++) {
				int elementLength = lengths[j];
				for (int k = 0; k < elementLength; k++) {
					interleavedBuffer[pointer++] = data[j][i * elementLength + k];
				}
			}
		}
		return interleavedBuffer;
	}

	public static void cleanup() {
		for (int i = 0; i < vaos.size(); i++) {
			vaos.get(i).delete();
		}
		vaos.clear();
	}

}
