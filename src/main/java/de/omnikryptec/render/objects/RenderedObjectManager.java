package de.omnikryptec.render.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import de.omnikryptec.util.data.DynamicArray;

public class RenderedObjectManager implements IRenderedObjectManager {
    private final ListMultimap<RenderedObjectType, IRenderedObjectListener> listeners;
    private final DynamicArray<List<RenderedObject>> objects;

    public RenderedObjectManager() {
        this.objects = new DynamicArray<>();
        this.listeners = ArrayListMultimap.create();
    }

    @Override
    public <T extends RenderedObject> List<T> getFor(final RenderedObjectType type) {
        return (List<T>) Collections.unmodifiableList(getList(type));
    }

    @Override
    public void add(final RenderedObjectType type, final RenderedObject renderedObject) {
        getList(type).add(renderedObject);
        notifyAdd(type, renderedObject);
    }

    @Override
    public void remove(final RenderedObjectType type, final RenderedObject renderedObject) {
        final List<RenderedObject> list = this.objects.get(type.id);
        if (list != null) {
            list.remove(renderedObject);
            notifyRemove(type, renderedObject);
        }
    }

    @Override
    public void addListener(final RenderedObjectType type, final IRenderedObjectListener listener) {
        this.listeners.put(type, listener);
    }

    @Override
    public void removeListener(final RenderedObjectType type, final IRenderedObjectListener listener) {
        this.listeners.remove(type, listener);
    }

    private List<RenderedObject> getList(final RenderedObjectType type) {
        List<RenderedObject> list = this.objects.get(type.id);
        if (list == null) {
            list = new ArrayList<>();
            this.objects.set(type.id, list);
        }
        return list;
    }

    private void notifyAdd(final RenderedObjectType type, final RenderedObject obj) {
        final List<IRenderedObjectListener> list = this.listeners.get(type);
        for (final IRenderedObjectListener l : list) {
            l.onAdd(obj);
        }
    }

    private void notifyRemove(final RenderedObjectType type, final RenderedObject obj) {
        final List<IRenderedObjectListener> list = this.listeners.get(type);
        for (final IRenderedObjectListener l : list) {
            l.onRemove(obj);
        }
    }
}
