package de.omnikryptec.render.storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;

import de.omnikryptec.util.data.DynamicArray;

public class RenderedObjectManager implements IRenderedObjectManager {
    private ListMultimap<RenderedObjectType, IRenderedObjectListener> listeners;
    private DynamicArray<List<RenderedObject>> objects;
    
    public RenderedObjectManager() {
        objects = new DynamicArray<>();
        listeners = ArrayListMultimap.create();
    }
    
    @Override
    public <T extends RenderedObject> List<T> getFor(RenderedObjectType type) {
        return (List<T>) Collections.unmodifiableList(getList(type));
    }
    
    @Override
    public void add(RenderedObjectType type, RenderedObject renderedObject) {
        getList(type).add(renderedObject);
        notifyAdd(type, renderedObject);
    }
    
    @Override
    public void remove(RenderedObjectType type, RenderedObject renderedObject) {
        List<RenderedObject> list = objects.get(type.id);
        if (list != null) {
            list.remove(renderedObject);
            notifyRemove(type, renderedObject);
        }
    }
    
    @Override
    public void addListener(RenderedObjectType type, IRenderedObjectListener listener) {
        listeners.put(type, listener);
    }
    
    @Override
    public void removeListener(RenderedObjectType type, IRenderedObjectListener listener) {
        listeners.remove(type, listener);
    }
    
    private List<RenderedObject> getList(RenderedObjectType type) {
        List<RenderedObject> list = objects.get(type.id);
        if (list == null) {
            list = new ArrayList<>();
            objects.set(type.id, list);
        }
        return list;
    }
    
    private void notifyAdd(RenderedObjectType type, RenderedObject obj) {
        List<IRenderedObjectListener> list = listeners.get(type);
        for (IRenderedObjectListener l : list) {
            l.onAdd(obj);
        }
    }
    
    private void notifyRemove(RenderedObjectType type, RenderedObject obj) {
        List<IRenderedObjectListener> list = listeners.get(type);
        for (IRenderedObjectListener l : list) {
            l.onRemove(obj);
        }
    }
}
