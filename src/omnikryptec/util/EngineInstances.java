package omnikryptec.util;

import omnikryptec.display.DisplayManager;
import omnikryptec.entity.Camera;
import omnikryptec.event.EventSystem;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.main.Scene;
import omnikryptec.postprocessing.PostProcessing;
import omnikryptec.settings.GameSettings;
import omnikryptec.settings.KeySettings;

public class EngineInstances {
	
	public static final OmniKryptecEngine getEngine(){
		return OmniKryptecEngine.instance();
	}
	
	public static final PostProcessing getPostProcessing(){
		return PostProcessing.instance();
	}
	
	public static final DisplayManager getDisplayManager(){
		return DisplayManager.instance();
	}
	
	public static final Scene getCurrentScene(){
		return getEngine().getCurrentScene();
	}
	
	public static final Camera getCurrentCamera(){
		return getCurrentScene()==null?null:getCurrentScene().getCamera();
	}
	
	public static final EventSystem getEventSystem(){
		return EventSystem.instance();
	}
	
	public static final GameSettings getGameSettings(){
		return getDisplayManager().getSettings();
	}
	
	public static final KeySettings getKeySettings(){
		return getGameSettings().getKeySettings();
	}
	
}
