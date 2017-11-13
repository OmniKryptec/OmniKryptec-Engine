package omnikryptec.gameobject.particles;

import org.joml.Vector2f;
import org.joml.Vector3f;

import omnikryptec.gameobject.Camera;
import omnikryptec.resource.texture.ParticleAtlas;
import omnikryptec.util.Color;
import omnikryptec.util.EnumCollection.RenderType;

public class Particle {

    protected ParticleAtlas particletexture;
    private RenderType type;
    protected Vector3f position = new Vector3f(0);
    protected float scale = 1, rot = 0;
    protected Vector2f texOffset1 = new Vector2f(), texOffset2 = new Vector2f();
    protected float textureblend;
    private float distance;
    protected Color color = new Color(1, 1, 1, 1);
    protected boolean wantsupdatelast=false;
    protected boolean wantsmultithreaded=false;
    
    public Particle(ParticleAtlas texture) {
        this(texture, RenderType.ALWAYS);
    }

    public Particle(ParticleAtlas texture, RenderType type) {
        this(null, texture, type);
    }

    public Particle(Vector3f pos, ParticleAtlas texture, RenderType type) {
        this.particletexture = texture;
        this.type = type;
        if (pos != null) {
            this.position = pos;
        }
    }

    public final ParticleAtlas getParticleTexture() {
        return particletexture;
    }

    public final Particle setPos(float x, float y, float z) {
        position.set(x, y, z);
        return this;
    }

    public final float getScale() {
        return scale;
    }

    public final Particle setScale(float f) {
        this.scale = f;
        return this;
    }

    public final float getRotation() {
        return rot;
    }

    public final Particle setRotation(float f) {
        this.rot = f;
        return this;
    }

    public final Vector3f getPosition() {
        return position;
    }

    public final RenderType getType() {
        return type;
    }

    private final Vector3f tmp = new Vector3f();

    final boolean update(Camera cam) {
        distance = (cam.getTransform().getPosition(true).sub(position, tmp)).lengthSquared();
        updateTexCoordInfo();
        return update();
    }

    protected boolean update() {
        return true;
    }

    protected boolean updateLast() {
        return false;
    }

    /**
     *
     * @return -1 to show the first texturetile or return elapsedtime/lifetime
     */
    protected float getLifeFactor() {
        return -1;
    }

    private float lifeFactor, atlasProg;
    private int stageCount, index1, index2;

    protected void updateTexCoordInfo() {
        lifeFactor = getLifeFactor();
        if (lifeFactor == -1) {
            textureblend = 0;
            texOffset1.set(0, 0);
            texOffset2.set(0, 0);
        } else {
            stageCount = particletexture.getNumberOfRows() * particletexture.getNumberOfRows();
            atlasProg = lifeFactor * stageCount;
            index1 = (int) atlasProg;
            index2 = index1 < stageCount - 1 ? index1 + 1 : index1;
            this.textureblend = atlasProg % 1;
            if (index1 == 0) {
                texOffset1.set(0);
            } else {
                texOffset1 = setTexOffset(texOffset1, index1);
            }
            if (index2 == 0) {
                texOffset2.set(0);
            } else {
                texOffset2 = setTexOffset(texOffset2, index2);
            }
        }
    }

    private int column, row;

    protected Vector2f setTexOffset(Vector2f offset, int index) {
        column = index % particletexture.getNumberOfRows();
        row = index / particletexture.getNumberOfRows();
        offset.x = (float) column / particletexture.getNumberOfRows();
        offset.y = (float) row / particletexture.getNumberOfRows();
        return offset;
    }

    public Vector2f getTexOffset1() {
        return texOffset1;
    }

    public Vector2f getTexOffset2() {
        return texOffset2;
    }

    public float getBlend() {
        return textureblend;
    }

    public float getDistance() {
        return distance;
    }

    public float getColorR() {
        return color.getR();
    }

    public float getColorG() {
        return color.getG();
    }

    public float getColorB() {
        return color.getB();
    }

    public float getColorA() {
        return color.getA();
    }
    
	public final boolean wantsUpdateLast() {
		return wantsupdatelast;
	}

	public boolean wantsMultithreaded() {
		return wantsmultithreaded;
	}
}
