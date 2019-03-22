package de.omnikryptec.render.storage;

public interface IRenderedObjectListener {

    void onAdd(IRenderedObjectManager mgr);

    void onRemove(IRenderedObjectManager mgr);

}
