package omnikryptec.main;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.opengl.GL30;

import omnikryptec.audio.AudioManager;
import omnikryptec.display.Display;
import omnikryptec.display.DisplayManager;
import omnikryptec.event.event.Event;
import omnikryptec.event.event.EventSystem;
import omnikryptec.event.event.EventType;
import omnikryptec.event.input.InputManager;
import omnikryptec.postprocessing.main.FrameBufferObject;
import omnikryptec.postprocessing.main.FrameBufferObject.DepthbufferType;
import omnikryptec.postprocessing.main.PostProcessing;
import omnikryptec.postprocessing.main.RenderTarget;
import omnikryptec.renderer.Query;
import omnikryptec.renderer.RenderChunk;
import omnikryptec.renderer.RenderChunk.AllowedRenderer;
import omnikryptec.renderer.RendererRegistration;
import omnikryptec.resource.model.VertexArrayObject;
import omnikryptec.resource.model.VertexBufferObject;
import omnikryptec.resource.texture.SimpleTexture;
import omnikryptec.shader.base.Shader;
import omnikryptec.util.RenderUtil;
import omnikryptec.util.error.ErrorObject;
import omnikryptec.util.error.OmnikryptecError;
import omnikryptec.util.logger.Commands;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;
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

    public static OmniKryptecEngine rawInstance() {
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

    private GameState state = GameState.Stopped;

    public GameState getState() {
        return state;
    }

    private DisplayManager manager;
    private EventSystem eventsystem;
    private PostProcessing postpro;
    private final ArrayList<AbstractScene> scenes = new ArrayList<>();
    private AbstractScene sceneCurrent;
    private long vertsCountCurrent = 0;

    private ShutdownOption shutdownOption = ShutdownOption.JAVA;
    private boolean requestclose = false;    
    private double frametime = 0;

    public OmniKryptecEngine(DisplayManager manager) {
        if (manager == null) {
            throw new NullPointerException("DisplayManager is null");
        }
        if (instance != null) {
            throw new IllegalStateException("OmniKryptec-Engine was already created!");
        }
        try {
            Profiler.addProfilable(this, 0);
            this.manager = manager;
            state = GameState.Starting;
            instance = this;
            eventsystem = EventSystem.instance();
            postpro = PostProcessing.instance();
            RenderUtil.cullBackFaces(true);
            RenderUtil.enableDepthTesting(true);
            RendererRegistration.init();
            createFbos();
            Display.show();
            eventsystem.fireEvent(new Event(), EventType.BOOTING_COMPLETED);
            Logger.log("Successfully booted the Engine!", LogLevel.FINEST);
        } catch (Exception e) {
            errorOccured(e, "Error occured while booting.");
        }
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
        state = GameState.Running;
        while (!Display.isCloseRequested() && !requestclose && state != GameState.Error) {
            frame(obj.clear, obj.onlyRender, obj.sleepWhenInactive);
        }
        close(this.shutdownOption);
    }

    public final LoopObject getLoopObject() {
        return obj;
    }

    public final OmniKryptecEngine requestClose() {
        return requestClose(shutdownOption);
    }

    public final OmniKryptecEngine frame(boolean clear, boolean onlyrender, boolean sleepwheninactive) {
        final double currentTime = manager.getCurrentTime();
        if (state != GameState.Running) {
            Logger.log("Incorrect enginestate.", LogLevel.WARNING);
            return this;
        }
        try {
            if (!Display.isActive() && sleepwheninactive) {
                manager.updateDisplay();
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    errorOccured(e, "Error occured while sleeping.");
                }
                return this;
            }
            AudioManager.update(currentTime);
            if (Display.wasResized()) {
            	resizeFbos();
                PostProcessing.instance().resize();
                eventsystem.fireEvent(new Event(manager), EventType.RESIZED);
            }
            scenefbo.bindFrameBuffer();
            if (sceneCurrent != null) {
                if (clear) {
                    RenderUtil.clear(sceneCurrent.getClearColor());
                }
                if(!onlyrender){
                	sceneCurrent.publicLogic(true);
                }
                vertsCountCurrent = sceneCurrent.publicRender(true, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, null, AllowedRenderer.All);
            }
            eventsystem.fireEvent(new Event(), EventType.RENDER_FRAME_EVENT);
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
            errorOccured(e, "Error occured in frame.");
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

    public final long getFaceCount() {
        return vertsCountCurrent/3;
    }
    
    public final double getRenderTimeMS() {
        return sceneCurrent==null?0:sceneCurrent.getRenderTimeMS();
    }

    public final double getLogicTimeMS() {
        return sceneCurrent==null?0:sceneCurrent.getLogicTimeMS();
    }
    
    public final double getFrameTimeMS() {
        return frametime;
    }

    public final OmniKryptecEngine close(ShutdownOption shutdownOption) {
        if (shutdownOption.getLevel() >= ShutdownOption.ENGINE.getLevel()) {
            state = GameState.Stopping;
            cleanup();
            manager.close();
            state = GameState.Stopped;
            if (shutdownOption.getLevel() >= ShutdownOption.JAVA.getLevel()) {
                Commands.COMMANDEXIT.run("-java");
            }
            return null;
        } else {
            return this;
        }
    }

    public void errorOccured(Exception e, String text) {
        state = GameState.Error;
        new OmnikryptecError(e, new ErrorObject<String>(text)).print();
        eventsystem.fireEvent(new Event(e), EventType.ERROR);
    }

    private void cleanup() {
        RenderChunk.cleanup();
        PostProcessing.cleanup();
        VertexArrayObject.cleanup();
        VertexBufferObject.cleanup();
        FrameBufferObject.cleanup();
        SimpleTexture.cleanup();
        Shader.cleanAllShader();
        Query.cleanup();
        EventSystem.instance().fireEvent(new Event(), EventType.CLEANUP);
        instance = null;
    }

    public final OmniKryptecEngine addAndSetScene(AbstractScene scene) {
        if(scene!=null){
    		addScene(scene);
        	setScene(scene.getName());
        }
        return this;
    }

    public final OmniKryptecEngine addScene(AbstractScene scene) {
        if (scene != null) {
            scenes.add(scene);
        }
        return this;
    }

    public final OmniKryptecEngine setScene(String name) {
        List<AbstractScene> scenesEquals = scenes.stream().filter((scene) -> scene.getName().equals(name))
                .collect(Collectors.toList());
        sceneCurrent = (scenesEquals.isEmpty() ? null : scenesEquals.get(0));
        return this;
    }

    public final AbstractScene getCurrentScene() {
        return sceneCurrent;
    }

    public final ShutdownOption getShutdownOption() {
        return shutdownOption;
    }

    public final OmniKryptecEngine setShutdownOption(ShutdownOption shutdownOption) {
        this.shutdownOption = shutdownOption;
        return this;
    }

    public final ArrayList<AbstractScene> getScenes() {
        return scenes;
    }

    public String getCurrentSceneName() {
        return sceneCurrent == null ? null : sceneCurrent.getName();
    }

    @Override
    public ProfileContainer[] getProfiles() {
        return new ProfileContainer[]{new ProfileContainer(Profiler.OVERALL_FRAME_TIME, getFrameTimeMS()), new ProfileContainer(Profiler.SCENE_RENDER_TIME, getRenderTimeMS()), new ProfileContainer(Profiler.SCENE_LOGIC_TIME, getLogicTimeMS())};
    }

    public static class LoopObject {

        public boolean onlyRender = false;
        public boolean clear = true;
        public boolean sleepWhenInactive = true;
    }

	public static boolean isCreated() {
		return instance!=null;
	}
	
}
