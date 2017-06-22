package omnikryptec.model;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class VertexArrayObject {

    private static final int BYTES_PER_FLOAT = 4;
    private static final int BYTES_PER_INT = 4;

    private static int lastBoundId = -1;

    public final int ID;
    private VertexBufferObject dataVbo;
    private VertexBufferObject indexVbo;
    private int indexCount;

    private static final List<VertexArrayObject> vaos = new ArrayList<>();

    /**
     * Creates a new empty VertexArrayObject
     * @return VertexArrayObject VertexArrayObject
     */
    public static final VertexArrayObject create() {
        int id = GL30.glGenVertexArrays();
        return new VertexArrayObject(id);
    }

    private VertexArrayObject(int id) {
        this.ID = id;
        vaos.add(this);
    }

    /**
     * Returns the number of indices
     * @return Integer Index Count
     */
    public final int getIndexCount() {
        return indexCount;
    }

    /**
     * Binds the VertexArrayObject to OpenGL
     * @return A reference to this VertexArrayObject
     */
    public final VertexArrayObject bind() {
        if(ID != lastBoundId) {
            GL30.glBindVertexArray(ID);
            lastBoundId = ID;
        }
        return this;
    }

    /**
     * Binds the given Attributes to OpenGL
     * @param attributes Integer Array Attributes
     * @return A reference to this VertexArrayObject
     */
    public final VertexArrayObject bind(int... attributes) {
        bind();
        for(int i : attributes) {
            GL20.glEnableVertexAttribArray(i);
        }
        return this;
    }

    /**
     * Unbinds this VertexArrayObject
     * @return A reference to this VertexArrayObject
     * @deprecated Not even deprecated
     */
    @Deprecated
    public final VertexArrayObject unbind() {
        lastBoundId = 0;
        GL30.glBindVertexArray(0);
        return this;
    }

    /**
     * Unbinds the given Attributes
     * @param attributes Integer Array Attributes
     * @return A reference to this VertexArrayObject
     * @deprecated Not even deprecated
     */
    @Deprecated
    public final VertexArrayObject unbind(int... attributes) {
        for(int i : attributes) {
                GL20.glDisableVertexAttribArray(i);
        }
        unbind();
        return this;
    }

    /**
     * Stores data into the VertexArrayObject
     * @param indices Integer Array Indices
     * @param vertexCount Integer Number of verticies
     * @param data Float Array Array Data
     * @return A reference to this VertexArrayObject
     */
    public final VertexArrayObject storeData(int[] indices, int vertexCount, float[]... data) {
        bind();
        storeData(vertexCount, data);
        createIndexBuffer(indices);
        unbind();
        return this;
    }

    /**
     * Deletes this VertexArrayObject
     * @return A reference to this VertexArrayObject
     */
    public final VertexArrayObject delete() {
        GL30.glDeleteVertexArrays(ID);
        dataVbo.delete();
        indexVbo.delete();
        return this;
    }

    /**
     * Creates an indices buffer
     * @param indices Integer Array indices
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
     * @param attribute Integer Number of the Attribute
     * @param data Float Array Data
     * @param attributeSize Integer Number of following data
     * @return A reference to this VertexArrayObject
     */
    public final VertexArrayObject createAttribute(int attribute, float[] data, int attributeSize) {
        dataVbo = VertexBufferObject.create(GL15.GL_ARRAY_BUFFER);
        dataVbo.bind();
        dataVbo.storeData(data);
        GL20.glVertexAttribPointer(attribute, attributeSize, GL11.GL_FLOAT, false, attributeSize * BYTES_PER_FLOAT, 0);
        dataVbo.unbind();
        return this;
    }

    /**
     * Creates a new Integer Array Attribute
     * @param attribute Integer Number of the Attribute
     * @param data Integer Array Data
     * @param attributeSize Integer Number of following data
     * @return A reference to this VertexArrayObject
     */
    public final VertexArrayObject createIntAttribute(int attribute, int[] data, int attributeSize){
        dataVbo = VertexBufferObject.create(GL15.GL_ARRAY_BUFFER);
        dataVbo.bind();
        dataVbo.storeData(data);
        GL30.glVertexAttribIPointer(attribute, attributeSize, GL11.GL_INT, attributeSize * BYTES_PER_INT, 0);
        dataVbo.unbind();
        return this;
    }

    private final VertexArrayObject storeData(int vertexCount, float[]... data) {
        float[] interleavedData = interleaveFloatData(vertexCount, data);
        int[] lengths = getAttributeLengths(data, vertexCount);
        storeInterleavedData(interleavedData, lengths);
        return this;
    }

    private final int[] getAttributeLengths(float[][] data, int vertexCount) {
        int[] lengths = new int[data.length];
        for(int i = 0; i < data.length; i++) {
            lengths[i] = data[i].length / vertexCount;
        }
        return lengths;
    }

    private final VertexArrayObject storeInterleavedData(float[] data, int... lengths) {
        dataVbo = VertexBufferObject.create(GL15.GL_ARRAY_BUFFER);
        dataVbo.bind();
        dataVbo.storeData(data);
        int bytesPerVertex = calculateBytesPerVertex(lengths);
        linkVboDataToAttributes(lengths, bytesPerVertex);
        dataVbo.unbind();
        return this;
    }

    private final VertexArrayObject linkVboDataToAttributes(int[] lengths, int bytesPerVertex) {
        int total = 0;
        for(int i = 0; i < lengths.length; i++) {
            GL20.glVertexAttribPointer(i, lengths[i], GL11.GL_FLOAT, false, bytesPerVertex, BYTES_PER_FLOAT * total);
            total += lengths[i];
        }
        return this;
    }

    private final int calculateBytesPerVertex(int[] lengths) {
        int total = 0;
        for(int i = 0; i < lengths.length; i++) {
            total += lengths[i];
        }
        return BYTES_PER_FLOAT * total;
    }

    private final float[] interleaveFloatData(int count, float[]... data) {
        int totalSize = 0;
        int[] lengths = new int[data.length];
        for(int i = 0; i < data.length; i++) {
            int elementLength = data[i].length / count;
            lengths[i] = elementLength;
            totalSize += data[i].length;
        }
        float[] interleavedBuffer = new float[totalSize];
        int pointer = 0;
        for(int i = 0; i < count; i++) {
            for(int j = 0; j < data.length; j++) {
                int elementLength = lengths[j];
                for(int k = 0; k < elementLength; k++) {
                    interleavedBuffer[pointer++] = data[j][i * elementLength + k];
                }
            }
        }
        return interleavedBuffer;
    }

    public static void cleanup() {
        for(int i = 0; i < vaos.size(); i++) {
            vaos.get(i).delete();
        }
        vaos.clear();
    }

}
