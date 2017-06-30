package omnikryptec.main;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import omnikryptec.audio.AudioManager;
import omnikryptec.display.Display;
import omnikryptec.display.DisplayManager;
import omnikryptec.event.Event;
import omnikryptec.event.EventSystem;
import omnikryptec.event.EventType;
import omnikryptec.logger.Commands;
import omnikryptec.logger.Logger;
import omnikryptec.model.VertexArrayObject;
import omnikryptec.model.VertexBufferObject;
import omnikryptec.particles.ParticleMaster;
import omnikryptec.postprocessing.main.FrameBufferObject;
import omnikryptec.postprocessing.main.PostProcessing;
import omnikryptec.postprocessing.main.RenderTarget;
import omnikryptec.postprocessing.main.FrameBufferObject.DepthbufferType;
import omnikryptec.renderer.RenderChunk;
import omnikryptec.renderer.RenderChunk.AllowedRenderer;
import omnikryptec.renderer.RendererRegistration;
import omnikryptec.texture.SimpleTexture;
import omnikryptec.input.InputManager;
import omnikryptec.util.RenderUtil;
import omnikryptec.util.error.ErrorObject;
import omnikryptec.util.error.OmnikryptecError;
import omnikryptec.util.profiler.Profilable;
import omnikryptec.util.profiler.ProfileContainer;
import omnikryptec.util.profiler.Profiler;

/**
 *
 * @author Panzer1119 &amp; pcfreak9000
 */
public class OmniKryptecEngine implements Profilable {

    private static OmniKryptecEngine instance;

    public static OmniKryptecEngine instance() {
        if (instance == null) {
            if (DisplayManager.instance() == null) {
                throw new IllegalStateException(
                        "Cant create the Engine because the DisplayManager is not created yet!");
            }
            new OmniKryptecEngine(DisplayManager.instance());
        }
        return instance;
    }

    public static OmniKryptecEngine getInstance() {
        return instance;
    }

    public static void addShutdownHook(Runnable run) {
        if (run == null) {
            return;
        }
        Runtime.getRuntime().addShutdownHook(new Thread(run));
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO say that this is a library?
    }

    public static enum State {
        Starting, Running, Error, Stopping, Stopped;
    }

    public static enum ShutdownOption {
        JAVA(2), ENGINE(1), NOTHING(0);

        private final int level;

        private ShutdownOption(int level) {
            this.level = level;
        }

        public int getLevel() {
            return level;
        }
    }

    private State state = State.Stopped;

    public State getState() {
        return state;
    }

    private DisplayManager manager;
    private EventSystem eventsystem;
    private PostProcessing postpro;
    private final ArrayList<Scene> scenes = new ArrayList<>();
    private Scene sceneCurrent;
    private long vertsCountCurrent = 0;

    private ShutdownOption shutdownOption = ShutdownOption.JAVA;
    private boolean requestclose = false;

    private double rendertime = 0;
    private double tmptime = 0;
    private double frametime = 0;

    public OmniKryptecEngine(DisplayManager manager) {
        if (manager == null) {
            throw new NullPointerException("DisplayManager is null");
        }
        if (instance != null) {
            throw new IllegalStateException("OmniKryptec-Engine was already created!");
        }
        Profiler.addProfilable(this, 0);
        this.manager = manager;
        state = State.Starting;
        instance = this;
        eventsystem = EventSystem.instance();
        postpro = PostProcessing.instance();
        RenderUtil.cullBackFaces(true);
        RenderUtil.enableDepthTesting(true);
        RendererRegistration.init();
        createFbos();
        Display.show();
        eventsystem.fireEvent(new Event(), EventType.BOOTING_COMPLETED);
    }

    private FrameBufferObject scenefbo;
    private FrameBufferObject unsampledfbo, normalfbo, specularfbo, extrainfofbo;
    private FrameBufferObject[] add;

    private void createFbos() {
        scenefbo = new FrameBufferObject(Display.getWidth(), Display.getHeight(),
                manager.getSettings().getMultiSamples(), manager.getSettings().getAddAttachments(),
                new RenderTarget(GL30.GL_COLOR_ATTACHMENT0), new RenderTarget(GL30.GL_COLOR_ATTACHMENT1),
                new RenderTarget(GL30.GL_COLOR_ATTACHMENT2, true), new RenderTarget(GL30.GL_COLOR_ATTACHMENT3));
        unsampledfbo = new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.DEPTH_TEXTURE,
                new RenderTarget(GL30.GL_COLOR_ATTACHMENT0));
        normalfbo = new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.NONE,
                new RenderTarget(GL30.GL_COLOR_ATTACHMENT0));
        specularfbo = new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.NONE,
                new RenderTarget(GL30.GL_COLOR_ATTACHMENT0, true));
        extrainfofbo = new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.NONE,
                new RenderTarget(GL30.GL_COLOR_ATTACHMENT0));
        add = manager.getSettings().getAddFBOs();
    }

    private void resizeFbos() {
        if (scenefbo != null) {
            scenefbo.delete();
        }
        if (unsampledfbo != null) {
            unsampledfbo.delete();
        }
        if (normalfbo != null) {
            normalfbo.delete();
        }
        if (specularfbo != null) {
            specularfbo.delete();
        }
        if (extrainfofbo != null) {
            extrainfofbo.delete();
        }
        createFbos();
    }

    public final DisplayManager getDisplayManager() {
        return manager;
    }

    public final EventSystem getEventsystem() {
        return eventsystem;
    }

    public final PostProcessing getPostprocessor() {
        return postpro;
    }

    private final LoopObject obj = new LoopObject();
    public final void startLoop(ShutdownOption shutdownOption) {
        setShutdownOption(shutdownOption);
        state = State.Running;
        while (!Display.isCloseRequested() && !requestclose && state != State.Error) {
            frame(obj.clear, obj.onlyRender, obj.sleepWhenInactive);
        }
        close(this.shutdownOption);
    }

    public final LoopObject getLoopObject(){
    	return obj;
    }
    
    public final OmniKryptecEngine requestClose() {
        return requestClose(shutdownOption);
    }

    public final OmniKryptecEngine frame(boolean clear, boolean onlyrender, boolean sleepwheninactive) {
        final double currentTime = manager.getCurrentTime();
        try {
            if (!Display.isActive()&&sleepwheninactive) {
                Display.update();
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    errorOccured(e, "Error occured while sleeping: ");
                }
                return this;
            }
            AudioManager.update(currentTime);
            if (Display.wasResized()) {
                GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
                resizeFbos();
                PostProcessing.instance().resize();
                eventsystem.fireEvent(new Event(manager), EventType.RESIZED);
            }
            scenefbo.bindFrameBuffer();
            if (sceneCurrent != null) {
                if (clear) {
                    RenderUtil.clear(sceneCurrent.getClearColor());
                }
                tmptime = manager.getCurrentTime();
                vertsCountCurrent = sceneCurrent.frame(Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, onlyrender, AllowedRenderer.All);
                rendertime = manager.getCurrentTime() - tmptime;
                ParticleMaster.instance().update(getCurrentScene().getCamera());
            }
            eventsystem.fireEvent(new Event(), EventType.RENDER_EVENT);
            scenefbo.unbindFrameBuffer();
            scenefbo.resolveToFbo(unsampledfbo, GL30.GL_COLOR_ATTACHMENT0);
            scenefbo.resolveToFbo(normalfbo, GL30.GL_COLOR_ATTACHMENT1);
            scenefbo.resolveToFbo(specularfbo, GL30.GL_COLOR_ATTACHMENT2);
            scenefbo.resolveToFbo(extrainfofbo, GL30.GL_COLOR_ATTACHMENT3);
            if (scenefbo.getTargets().length > 4) {
                for (int i = 4; i < scenefbo.getTargets().length; i++) {
                    scenefbo.resolveToFbo(add[i], manager.getSettings().getAddAttachments()[i - 4].target);
                }
            }
            if (sceneCurrent != null) {
                PostProcessing.instance().doPostProcessing(add, unsampledfbo, normalfbo, specularfbo, extrainfofbo);
            }
            eventsystem.fireEvent(new Event(), EventType.FRAME_EVENT);
            InputManager.prePollEvents();
            manager.updateDisplay();
            InputManager.nextFrame();
            frametime = manager.getCurrentTime() - currentTime;
        } catch (Exception e) {
            errorOccured(e, "Error occured in frame: ");
        }
        eventsystem.fireEvent(new Event(), EventType.AFTER_FRAME);
        return this;
    }

    public final OmniKryptecEngine requestClose(ShutdownOption shutdownOption) {
        setShutdownOption(shutdownOption);
        requestclose = true;
        return this;
    }

    public final long getModelVertsCount() {
        return vertsCountCurrent;
    }

    public final double getRenderTimeMS() {
        return rendertime;
    }

    public final double getFrameTimeMS() {
        return frametime;
    }

    public final OmniKryptecEngine close(ShutdownOption shutdownOption) {
        if (shutdownOption.getLevel() >= ShutdownOption.ENGINE.getLevel()) {
            state = State.Stopping;
            cleanup();
            manager.close();
            state = State.Stopped;
            if (shutdownOption.getLevel() >= ShutdownOption.JAVA.getLevel()) {
                Commands.COMMANDEXIT.run("-java");
            }
            return null;
        } else {
            return this;
        }
    }

    public void errorOccured(Exception e, String text) {
    	state = State.Error;
    	new OmnikryptecError(e).print();
        eventsystem.fireEvent(new Event(e), EventType.ERROR);
    }

    private void cleanup() {
        RenderChunk.cleanup();
        PostProcessing.cleanup();
        VertexArrayObject.cleanup();
        VertexBufferObject.cleanup();
        FrameBufferObject.cleanup();
        RendererRegistration.cleanup();
        ParticleMaster.cleanup();
        SimpleTexture.cleanup();
    }

    public final OmniKryptecEngine addAndSetScene(Scene scene) {
        addScene(scene);
        setScene(scene.getName());
        return this;
    }

    public final OmniKryptecEngine addScene(Scene scene) {
        if (scene != null) {
            scenes.add(scene);
        }
        return this;
    }

    public final OmniKryptecEngine setScene(String name) {
        List<Scene> scenesEquals = scenes.stream().filter((scene) -> scene.getName().equals(name))
                .collect(Collectors.toList());
        sceneCurrent = (scenesEquals.isEmpty() ? null : scenesEquals.get(0));
        return this;
    }

    public final Scene getCurrentScene() {
        return sceneCurrent;
    }

    public final ShutdownOption getShutdownOption() {
        return shutdownOption;
    }

    public final OmniKryptecEngine setShutdownOption(ShutdownOption shutdownOption) {
        this.shutdownOption = shutdownOption;
        return this;
    }

    public final ArrayList<Scene> getScenes() {
        return scenes;
    }

    public String getCurrentSceneName() {
        return sceneCurrent == null ? null : sceneCurrent.getName();
    }

    @Override
    public ProfileContainer[] getProfiles() {
        return new ProfileContainer[]{new ProfileContainer(Profiler.OVERALL_FRAME_TIME, getFrameTimeMS()), new ProfileContainer(Profiler.SCENE_TIME, getRenderTimeMS())};
    }
    
    public static class LoopObject{
    	public boolean onlyRender=false;
    	public boolean clear=true;
    	public boolean sleepWhenInactive=true;
    }

}
