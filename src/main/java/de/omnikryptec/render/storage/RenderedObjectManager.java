package de.omnikryptec.render.storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.omnikryptec.util.data.DynamicArray;

public class RenderedObjectManager implements IRenderedObjectManager {
    
    private DynamicArray<List<RenderedObject>> objects;
    
    public RenderedObjectManager() {
        objects = new DynamicArray<>();
    }
    
    @Override
    public <T extends RenderedObject> List<T> getFor(RenderedObjectType type) {
        return (List<T>) Collections.unmodifiableList(objects.get(type.id));
    }
    
    @Override
    public void clear(RenderedObjectType type) {
        List<? extends RenderedObject> list = objects.get(type.id);
        if (list != null) {
            list.clear();
        }
    }
    
    @Override
    public void clearAll() {
        for (List<? extends RenderedObject> list : objects) {
            list.clear();
        }
    }
    
    @Override
    public void add(RenderedObjectType type, RenderedObject renderedObject) {
        getList(type).add(renderedObject);
    }
    
    @Override
    public void addAll(RenderedObjectType type, Collection<RenderedObject> collection) {
        getList(type).addAll(collection);
    }
    
    private List<RenderedObject> getList(RenderedObjectType type) {
        List<RenderedObject> list = objects.get(type.id);
        if (list == null) {
            list = new ArrayList<>();
            objects.set(type.id, list);
        }
        return list;
    }
    
    @Override
    public void remove(RenderedObjectType type, RenderedObject renderedObject) {
        List<RenderedObject> list = objects.get(type.id);
        if (list != null) {
            list.remove(renderedObject);
        }
    }
    
}
