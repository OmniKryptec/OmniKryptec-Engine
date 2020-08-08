package de.omnikryptec.render.postprocessing;

import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.Mesh;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.resource.MeshData;
import de.omnikryptec.resource.MeshData.VertexAttribute;

public class PPMesh {
    private static final float[] QUAD_VERTICES = { -1, 1, -1, -1, 1, 1, 1, -1 };
    private static final int[] QUAD_INDICES = { 0, 1, 2, 1, 3, 2 };
    private static final MeshData QUAD = new MeshData(VertexAttribute.Position, 2, QUAD_VERTICES, VertexAttribute.Index,
            QUAD_INDICES);
    
    private static Mesh mesh;
    
    public static Mesh quadMesh() {
        if (mesh == null) {
            mesh = new Mesh(QUAD);
        }
        return mesh;
    }
    
    public static void renderPPMesh() {
        LibAPIManager.instance().getGLFW().getRenderAPI().renderMesh(quadMesh());
    }
}
