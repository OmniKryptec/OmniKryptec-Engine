/*
 *    Copyright 2017 - 2020 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.resource.loadervpc;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.resource.MeshData;
import de.omnikryptec.resource.MeshData.VertexAttribute;
import de.omnikryptec.resource.MeshFile;
import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.assimp.*;

import java.nio.ByteBuffer;

public class MeshLoader implements ResourceLoader<MeshFile> {
    
    private final int flags = Assimp.aiProcess_CalcTangentSpace | Assimp.aiProcess_JoinIdenticalVertices
            | Assimp.aiProcess_RemoveRedundantMaterials | Assimp.aiProcess_OptimizeMeshes
            | Assimp.aiProcess_SplitLargeMeshes | Assimp.aiProcess_Triangulate | Assimp.aiProcess_SortByPType;
    
    @Override
    public MeshFile load(final AdvancedFile file) throws Exception {
        final byte[] bytes = IOUtils.toByteArray(file.createInputStream());
        final ByteBuffer buffer = BufferUtils.createByteBuffer(bytes.length);
        buffer.put(bytes);
        buffer.flip();
        //Move?
        final AIScene scene = Assimp.aiImportFileFromMemory(buffer, this.flags, "dae");
        final MeshData[] meshFile = new MeshData[scene.mNumMeshes()];
        final AIMaterial[] mats = new AIMaterial[scene.mNumMaterials()];
        for (int i = 0; i < scene.mNumMaterials(); i++) {
            mats[i] = AIMaterial.create(scene.mMaterials().get(i));
        }
        for (int i = 0; i < scene.mNumMeshes(); i++) {
            final AIMesh mesh = AIMesh.create(scene.mMeshes().get(i));
            
            final int vertexCount = mesh.mNumVertices();
            final float[] positions = new float[vertexCount * 3];
            for (int j = 0; j < vertexCount; j += 3) {
                final AIVector3D pos = mesh.mVertices().get(j);
                positions[j] = pos.x();
                positions[j + 1] = pos.y();
                positions[j + 2] = pos.z();
            }
            final int[] indices = new int[mesh.mNumFaces() * mesh.mFaces().get(0).mNumIndices()];
            for (int j = 0; j < mesh.mNumFaces(); j++) {
                final AIFace face = mesh.mFaces().get(j);
                for (int k = 0; k < face.mNumIndices(); k++) {
                    indices[j + k] = face.mIndices().get(k);
                }
            }
            meshFile[i] = new MeshData(VertexAttribute.Position, 3, positions, VertexAttribute.Index, indices);
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
