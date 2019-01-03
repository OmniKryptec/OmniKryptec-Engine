package de.omnikryptec.resource.loadervpc;

import java.nio.ByteBuffer;
import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.Assimp;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.resource.MeshData;
import de.omnikryptec.resource.MeshFile;
import de.omnikryptec.resource.MeshData.VertexAttribute;

public class MeshLoader implements ResourceLoader<MeshFile> {
    
    private final int flags = Assimp.aiProcess_CalcTangentSpace | Assimp.aiProcess_JoinIdenticalVertices
            | Assimp.aiProcess_RemoveRedundantMaterials | Assimp.aiProcess_OptimizeMeshes
            | Assimp.aiProcess_SplitLargeMeshes | Assimp.aiProcess_Triangulate | Assimp.aiProcess_SortByPType;
    
    public MeshLoader() {
        //TODO init native assimp lib in a different way
        String s = Assimp.aiGetErrorString();
    }
    
    @Override
    public MeshFile load(AdvancedFile file) throws Exception {
        byte[] bytes = IOUtils.toByteArray(file.createInputStream());
        ByteBuffer buffer = BufferUtils.createByteBuffer(bytes.length);
        buffer.put(bytes);
        buffer.flip();
        //Move?
        AIScene scene = Assimp.aiImportFileFromMemory(buffer, flags, "dae");
        MeshData[] meshFile = new MeshData[scene.mNumMeshes()];
        AIMaterial[] mats = new AIMaterial[scene.mNumMaterials()];
        for (int i = 0; i < scene.mNumMaterials(); i++) {
            mats[i] = AIMaterial.create(scene.mMaterials().get(i));
        }
        for (int i = 0; i < scene.mNumMeshes(); i++) {
            AIMesh mesh = AIMesh.create(scene.mMeshes().get(i));
            
            int vertexCount = mesh.mNumVertices();
            float[] positions = new float[vertexCount * 3];
            for (int j = 0; j < vertexCount; j += 3) {
                AIVector3D pos = mesh.mVertices().get(j);
                positions[j] = pos.x();
                positions[j + 1] = pos.y();
                positions[j + 2] = pos.z();
            }
            int[] indices = new int[mesh.mNumFaces() * mesh.mFaces().get(0).mNumIndices()];
            for (int j = 0; j < mesh.mNumFaces(); j++) {
                AIFace face = mesh.mFaces().get(j);
                for (int k = 0; k < face.mNumIndices(); k++) {
                    indices[j + k] = face.mIndices().get(k);
                }
            }
            meshFile[i] = new MeshData(VertexAttribute.Position, positions, VertexAttribute.Index, indices);
        }
        return new MeshFile(meshFile);
    }
    
    @Override
    public String getFileNameRegex() {
        return ".*\\.dae";
    }
    
    @Override
    public boolean requiresMainThread() {
        return false;
    }
    
}
