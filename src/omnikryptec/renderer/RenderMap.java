package omnikryptec.renderer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import omnikryptec.entity.Entity;
import omnikryptec.model.TexturedModel;

public class RenderMap{
	
	private Map<TexturedModel, List<Entity>> map = new HashMap<>();
	private TexturedModel[] keys;
	
	private boolean keysDirty=true;
	
	public void put(TexturedModel k, List<Entity> v){
		map.put(k, v);
		keysDirty = true;
	}
	
	public List<Entity> get(TexturedModel key){
		return map.get(key);
	}
	
	public TexturedModel[] keysArray(){
		if(keysDirty){
			keys = map.keySet().toArray(new TexturedModel[map.size()]);
			keysDirty = false;
		}
		return keys;
	}
	
	public void remove(TexturedModel tm){
		map.remove(tm);
		keysDirty = true;
	}
	
	public boolean isEmpty(){
		return map.isEmpty();
	}
	
}	
