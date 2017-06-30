package omnikryptec.resource.texture;

import omnikryptec.display.DisplayManager;

public class SimpleAnimation extends Texture {

    private Texture[] textures;
    private int index = 0;
    private float secondsperframe = 1;
    private float time = 0;
    private long lastupdated = 0;

    public SimpleAnimation(float fps, Texture... textures) {
        this("", fps, textures);
    }
    
    public SimpleAnimation(String name, float fps, Texture... textures) {
        super(name, true);
        this.textures = textures;
        this.secondsperframe = 1.0f / fps;
    }

    public SimpleAnimation setFPS(float fps) {
        this.secondsperframe = 1.0f / fps;
        return this;
    }

    public SimpleAnimation setSecondsPerFrame(float f) {
        this.secondsperframe = f;
        return this;
    }

    public int getCurrentIndex() {
        return index;
    }

    public Texture[] getTextures() {
        return textures;
    }

    @Override
    public void bindToUnita(int unit, int... info) {
        textures[index].bindToUnita(unit, info);
        if (lastupdated < DisplayManager.instance().getFramecount()) {
            lastupdated = DisplayManager.instance().getFramecount();
            if (time >= secondsperframe) {
                index++;
                index %= textures.length;
                time = 0;
            } else {
                time += DisplayManager.instance().getDeltaTimef();
            }
        }
    }

}
