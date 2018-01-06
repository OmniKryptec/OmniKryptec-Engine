package omnikryptec.resource.texture;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import de.codemakers.io.file.AdvancedFile;
import omnikryptec.graphics.OpenGL;
import omnikryptec.util.logger.Logger;

public class SimpleTexture extends Texture {

    private final TextureData data;
    private final int type;
    private final int id;

    private static final List<SimpleTexture> alltex = new ArrayList<>();

    protected SimpleTexture(String name, int textureId, TextureData data) {
        this(name, textureId, GL11.GL_TEXTURE_2D, data);
    }

    protected SimpleTexture(String name, int textureId, int type, TextureData data) {
        super(name);
        alltex.add(this);
        this.data = data;
        this.type = type;
        this.id = textureId;
    }

    @Override
    public void bindToUnit(int unit, int... info) {
        OpenGL.gl13activeTextureZB(unit);
        super.bindTexture(type, id);
    }

    public SimpleTexture delete() {
        GL11.glDeleteTextures(id);
        return this;
    }

    public static TextureBuilder newTextureb(AdvancedFile file) {
        return newTextureb("", file);
    }

    public static TextureBuilder newTextureb(String name, AdvancedFile file) {
        try {
            return newTextureb(name, file.createInputStream());
        } catch (Exception ex) {
            Logger.logErr("Error while creating FileInputStream: " + ex, ex);
            return null;
        }
    }

    public static TextureBuilder newTextureb(String path) {
        return newTextureb("", path);
    }

    public static TextureBuilder newTextureb(String name, String path) {
        try {
            return newTextureb(name, TextureBuilder.class.getResourceAsStream(path));
        } catch (Exception ex) {
            Logger.logErr("Error while creating Stream from path: " + ex, ex);
            return null;
        }
    }

    public static TextureBuilder newTextureb(InputStream textureFile) {
        return newTextureb("", textureFile);
    }

    public static TextureBuilder newTextureb(String name, InputStream textureFile) {
        return new TextureBuilder(name, textureFile);
    }

    public static SimpleTexture newTexture(AdvancedFile file, Properties tp) {
        return newTexture("", file, tp);
    }

    public static SimpleTexture newTexture(String name, AdvancedFile file, Properties tp) {
        try {
            return newTexture(name, file.createInputStream(), tp);
        } catch (Exception ex) {
            Logger.logErr("Error while creating FileInputStream: " + ex, ex);
            return null;
        }
    }

    public static SimpleTexture newTexture(String path) {
        return newTexture("", path);
    }

    @Deprecated
    public static SimpleTexture newTexture(String name, String path) {
        try {
            return newTexture(name, TextureBuilder.class.getResourceAsStream(path), null);
        } catch (Exception ex) {
            Logger.logErr("Error while creating Stream from path: " + ex, ex);
            return null;
        }
    }

    public static SimpleTexture newTexture(InputStream stream, Properties tp) {
        return newTexture("", stream, tp);
    }

    public static SimpleTexture newTexture(String name, InputStream stream, Properties tp) {
        return new TextureBuilder(name, stream).create(tp);
    }

    public static SimpleTexture newCubeMap(InputStream[] textureFiles) {
        return newCubeMap("", textureFiles);
    }

    public static SimpleTexture newCubeMap(String name, InputStream[] textureFiles) {
        int cubeMapId = TextureUtils.loadCubeMap(textureFiles);
        return new SimpleTexture(name, cubeMapId, GL13.GL_TEXTURE_CUBE_MAP, null);
    }

    public static SimpleTexture newEmptyCubeMap(int size) {
        return newEmptyCubeMap("", size);
    }

    public static SimpleTexture newEmptyCubeMap(String name, int size) {
        int cubeMapId = TextureUtils.createEmptyCubeMap(size);
        return new SimpleTexture(name, cubeMapId, GL13.GL_TEXTURE_CUBE_MAP, null);
    }

    /**
     * for cubemaps null
     *
     * @return
     */
    public TextureData getData() {
        return data;
    }

    public int getID() {
        return id;
    }

    public int getType() {
        return type;
    }

    public static void cleanup() {
        for (int i = 0; i < alltex.size(); i++) {
            alltex.get(i).delete();
        }
    }

    @Override
    public float getWidth() {
        return data == null ? 0 : data.getWidth();
    }

    @Override
    public float getHeight() {
        return data == null ? 0 : data.getHeight();
    }

}
