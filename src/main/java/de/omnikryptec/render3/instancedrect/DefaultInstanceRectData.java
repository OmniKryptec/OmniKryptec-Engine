package de.omnikryptec.render3.instancedrect;

import org.joml.Matrix3x2f;
import org.joml.Vector2f;

import de.omnikryptec.render3.BatchedRenderer;
import de.omnikryptec.render3.FloatCollector;
import de.omnikryptec.util.data.Color;

public class DefaultInstanceRectData extends InstancedRectData {
    
    public Matrix3x2f transform = new Matrix3x2f();
    private float u0 = 0, v0 = 0, u1 = 1, v1 = 1;
    private Color color = new Color(1, 0, 0, 1).randomizeRGB();
    private Vector2f sdData = new Vector2f();
    private Vector2f bsdData = new Vector2f();
    private Color borderColor = new Color();
    private Vector2f offset = new Vector2f();
    
    public void fill(FloatCollector b) {
        b.put(transform);
        b.put(u0).put(v0).put(u1).put(v1);
        color.get(b);
        borderColor.get(b);
        b.put(sdData).put(bsdData);
        b.put(offset);
    }
    
    @Override
    public Class<? extends BatchedRenderer> getDefaultRenderer() {
        return DefaultInstancedRectRenderer.class;
    }
}
